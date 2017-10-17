package com.airbnb.epoxy

import com.airbnb.epoxy.Utils.*
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.element.*
import javax.lang.model.element.Modifier.*
import javax.lang.model.util.*

internal class ModelProcessor(
        private val elements: Elements,
        private val types: Types,
        private val configManager: ConfigManager,
        private val errorLogger: ErrorLogger,
        private val modelWriter: GeneratedModelWriter
) {

    fun hasModelsToWrite() = styleableModelsToWrite.isNotEmpty()
    private val styleableModelsToWrite = mutableListOf<BasicGeneratedModelInfo>()

    fun processModels(roundEnv: RoundEnvironment): Collection<GeneratedModelInfo> {
        val modelClassMap = LinkedHashMap<TypeElement, GeneratedModelInfo>()

        for (attribute in roundEnv.getElementsAnnotatedWith(EpoxyAttribute::class.java)) {
            try {
                addAttributeToGeneratedClass(attribute, modelClassMap)
            } catch (e: Exception) {
                errorLogger.logError(e)
            }
        }

        for (clazz in roundEnv.getElementsAnnotatedWith(EpoxyModelClass::class.java)) {
            try {
                getOrCreateTargetClass(modelClassMap, clazz as TypeElement)
            } catch (e: Exception) {
                errorLogger.logError(e)
            }
        }

        try {
            addAttributesFromOtherModules(modelClassMap)
        } catch (e: Exception) {
            errorLogger.logError(e)
        }

        try {
            updateClassesForInheritance(modelClassMap)
        } catch (e: Exception) {
            errorLogger.logError(e)
        }

        for ((_, modelInfo) in modelClassMap) {

            if (modelInfo is BasicGeneratedModelInfo
                    && modelInfo.superClassElement.annotation<EpoxyModelClass>()?.layout == 0
                    && modelInfo.boundObjectTypeElement?.hasStyleableAnnotation(elements) == true) {
                styleableModelsToWrite.add(modelInfo)
            } else {
                writeModel(modelInfo)
            }
        }

        styleableModelsToWrite
                .filter { tryAddStyleBuilderAttribute(it, elements, types) }
                .forEach {
                    writeModel(it)
                    styleableModelsToWrite.remove(it)
                }

        return modelClassMap.values
    }

    private fun writeModel(modelInfo: GeneratedModelInfo) {
        try {
            modelWriter.generateClassForModel(modelInfo)
        } catch (e: Exception) {
            errorLogger.logError(e, "Error generating model classes")
        }
    }

    private fun addAttributeToGeneratedClass(
            attribute: Element,
            modelClassMap: MutableMap<TypeElement, GeneratedModelInfo>
    ) {
        val classElement = attribute.enclosingElement as TypeElement
        val helperClass = getOrCreateTargetClass(modelClassMap, classElement)
        helperClass.addAttribute(buildAttributeInfo(attribute))
    }

    private fun buildAttributeInfo(attribute: Element): AttributeInfo {
        validateFieldAccessibleViaGeneratedCode(attribute, EpoxyAttribute::class.java, errorLogger,
                                                true)
        return BaseModelAttributeInfo(attribute, types, elements, errorLogger)
    }

    private fun getOrCreateTargetClass(
            modelClassMap: MutableMap<TypeElement, GeneratedModelInfo>,
            classElement: TypeElement
    ): GeneratedModelInfo {

        var generatedModelInfo: GeneratedModelInfo? = modelClassMap[classElement]

        val isFinal = classElement.modifiers.contains(Modifier.FINAL)
        if (isFinal) {
            errorLogger.logError("Class with %s annotations cannot be final: %s",
                                 EpoxyAttribute::class.java.simpleName, classElement.simpleName)
        }

        // Nested classes must be static
        if (classElement.nestingKind.isNested) {
            if (!classElement.modifiers.contains(STATIC)) {
                errorLogger.logError(
                        "Nested model classes must be static. (class: %s)",
                        classElement.simpleName)
            }
        }

        if (!isEpoxyModel(classElement.asType())) {
            errorLogger.logError("Class with %s annotations must extend %s (%s)",
                                 EpoxyAttribute::class.java.simpleName, EPOXY_MODEL_TYPE,
                                 classElement.simpleName)
        }

        if (configManager.requiresAbstractModels(classElement) && !classElement.modifiers.contains(
                ABSTRACT)) {
            errorLogger
                    .logError("Epoxy model class must be abstract (%s)", classElement.simpleName)
        }

        if (generatedModelInfo == null) {
            generatedModelInfo = BasicGeneratedModelInfo(elements, types, classElement,
                                                         errorLogger)
            modelClassMap.put(classElement, generatedModelInfo)
        }

        return generatedModelInfo
    }

    /**
     * Looks for attributes on super classes that weren't included in this processor's coverage. Super
     * classes are already found if they are in the same module since the processor will pick them up
     * with the rest of the annotations.
     */
    private fun addAttributesFromOtherModules(
            modelClassMap: Map<TypeElement, GeneratedModelInfo>
    ) {
        // Copy the entries in the original map so we can add new entries to the map while we iterate
        // through the old entries
        val originalEntries = HashSet(modelClassMap.entries)

        for ((currentEpoxyModel, generatedModelInfo) in originalEntries) {
            var superclassType = currentEpoxyModel.superclass

            while (isEpoxyModel(superclassType)) {
                val superclassEpoxyModel = types.asElement(superclassType) as TypeElement

                if (!modelClassMap.keys.contains(superclassEpoxyModel)) {
                    // We can't inherit a package private attribute if we're not in the same package

                    // We add just the attribute info to the class in our module. We do NOT want to
                    // generate a class for the super class EpoxyModel in the other module since one
                    // will be created when that module is processed. If we make one as well there will
                    // be a duplicate (causes proguard errors and is just wrong).
                    superclassEpoxyModel.enclosedElements
                            .filter { it.getAnnotation(EpoxyAttribute::class.java) != null }
                            .map { buildAttributeInfo(it) }
                            .filter {
                                !it.isPackagePrivate
                                        || belongToTheSamePackage(
                                        currentEpoxyModel,
                                        superclassEpoxyModel,
                                        elements)
                            }
                            .forEach { generatedModelInfo.addAttribute(it) }
                }

                superclassType = superclassEpoxyModel.superclass
            }
        }
    }

    /**
     * Check each model for super classes that also have attributes. For each super class with
     * attributes we add those attributes to the attributes of the generated class, so that a
     * generated class contains all the attributes of its super classes combined.
     *
     *
     * One caveat is that if a sub class is in a different package than its super class we can't
     * include attributes that are package private, otherwise the generated class won't compile.
     */
    private fun updateClassesForInheritance(
            helperClassMap: Map<TypeElement, GeneratedModelInfo>
    ) {
        for ((thisClass, value) in helperClassMap) {

            val otherClasses = LinkedHashMap(helperClassMap)
            otherClasses.remove(thisClass)

            for ((otherClass, value1) in otherClasses) {

                if (!isSubtype(thisClass, otherClass, types)) {
                    continue
                }

                val otherAttributes = value1.getAttributeInfo()

                if (belongToTheSamePackage(thisClass, otherClass, elements)) {
                    value.addAttributes(otherAttributes)
                } else {
                    otherAttributes
                            .filterNot { it.isPackagePrivate }
                            .forEach { value.addAttribute(it) }
                }
            }
        }
    }
}
