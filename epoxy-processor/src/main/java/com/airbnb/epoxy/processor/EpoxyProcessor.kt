package com.airbnb.epoxy.processor

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import java.util.LinkedHashMap
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import kotlin.reflect.KClass

/**
 * Looks for [EpoxyAttribute] annotations and generates a subclass for all classes that have
 * those attributes. The generated subclass includes setters, getters, equals, and hashcode for the
 * given field. Any constructors on the original class are duplicated. Abstract classes are ignored
 * since generated classes would have to be abstract in order to guarantee they compile, and that
 * reduces their usefulness and doesn't make as much sense to support.
 */
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
class EpoxyProcessor : BaseProcessorWithPackageConfigs() {

    override val usesPackageEpoxyConfig: Boolean = true
    override val usesModelViewConfig: Boolean = false
    private val styleableModelsToWrite = mutableListOf<BasicGeneratedModelInfo>()

    override fun additionalSupportedAnnotations(): List<KClass<*>> = listOf(
        EpoxyModelClass::class,
        EpoxyAttribute::class
    )

    override suspend fun processRound(roundEnv: RoundEnvironment, roundNumber: Int) {
        super.processRound(roundEnv, roundNumber)
        val modelClassMap = ConcurrentHashMap<TypeElement, GeneratedModelInfo>()

        roundEnv.getElementsAnnotatedWith(EpoxyAttribute::class)
            .map("Find EpoxyAttribute class") { annotatedElement ->
                annotatedElement to getOrCreateTargetClass(
                    modelClassMap,
                    annotatedElement.enclosingElement as TypeElement
                )
            }
            .map("Build EpoxyAttribute") { (attribute, targetClass) ->
                buildAttributeInfo(
                    attribute,
                    logger,
                    typeUtils,
                    elementUtils,
                    memoizer
                ) to targetClass
            }.forEach { (attributeInfo, targetClass) ->
                // Do this after, synchronously, to preserve order of the generated attributes.
                // This keeps the generated code consistent, which is necessary for cache keys,
                // and some users may rely on the order that attributes are set (even though they shouldn't)
                targetClass.addAttribute(attributeInfo)
            }

        roundEnv.getElementsAnnotatedWith(EpoxyModelClass::class)
            .map("Process EpoxyModelClass") { clazz ->
                getOrCreateTargetClass(modelClassMap, clazz as TypeElement)
            }

        addAttributesFromOtherModules(modelClassMap)

        updateClassesForInheritance(modelClassMap)

        val modelInfos = modelClassMap.values

        val styleableModels = modelInfos.map("Check for styleable") { modelInfo ->
            if (modelInfo is BasicGeneratedModelInfo &&
                modelInfo.superClassElement.annotation<EpoxyModelClass>()?.layout == 0 &&
                modelInfo.boundObjectTypeElement?.hasStyleableAnnotation(elementUtils) == true
            ) {
                modelInfo
            } else {
                null
            }
        }

        styleableModelsToWrite.addAll(styleableModels)

        modelInfos.minus(styleableModels).map("Write model") {
            writeModel(it)
        }

        styleableModelsToWrite.map("Write styleable model") { modelInfo ->
            if (tryAddStyleBuilderAttribute(modelInfo, elementUtils, typeUtils)) {
                writeModel(modelInfo)
                modelInfo
            } else {
                null
            }
        }
            .let { styleableModelsToWrite.removeAll(it) }

        generatedModels.addAll(modelClassMap.values)
    }

    private fun writeModel(modelInfo: GeneratedModelInfo) {
        modelWriter.generateClassForModel(
            modelInfo,
            originatingElements = modelInfo.originatingElements()
        )
    }

    private fun getOrCreateTargetClass(
        modelClassMap: MutableMap<TypeElement, GeneratedModelInfo>,
        classElement: TypeElement
    ): GeneratedModelInfo = synchronizedByElement(classElement) {
        modelClassMap[classElement]?.let { return it }

        val isFinal = classElement.modifiers.contains(Modifier.FINAL)
        if (isFinal) {
            logger.logError(
                "Class with %s annotations cannot be final: %s",
                EpoxyAttribute::class.java.simpleName, classElement.simpleName
            )
        }

        // Nested classes must be static
        if (classElement.nestingKind.isNested) {
            if (!classElement.modifiers.contains(Modifier.STATIC)) {
                logger.logError(
                    "Nested model classes must be static. (class: %s)",
                    classElement.simpleName
                )
            }
        }

        if (!Utils.isEpoxyModel(classElement.asType())) {
            logger.logError(
                "Class with %s annotations must extend %s (%s)",
                EpoxyAttribute::class.java.simpleName, Utils.EPOXY_MODEL_TYPE,
                classElement.simpleName
            )
        }

        if (configManager.requiresAbstractModels(classElement) && !classElement.modifiers.contains(
            Modifier.ABSTRACT
        )
        ) {
            logger
                .logError(
                    "Epoxy model class must be abstract (%s)",
                    classElement.simpleName
                )
        }

        val generatedModelInfo = BasicGeneratedModelInfo(
            elementUtils,
            typeUtils,
            classElement,
            logger,
            memoizer
        )
        modelClassMap[classElement] = generatedModelInfo

        return generatedModelInfo
    }

    /**
     * Looks for attributes on super classes that weren't included in this processor's coverage. Super
     * classes are already found if they are in the same module since the processor will pick them up
     * with the rest of the annotations.
     */
    private suspend fun addAttributesFromOtherModules(modelClassMap: Map<TypeElement, GeneratedModelInfo>) {
        modelClassMap.entries.forEach("addAttributesFromOtherModules") { (currentEpoxyModel, generatedModelInfo) ->
            // We add just the attribute info to the class in our module. We do NOT want to
            // generate a class for the super class EpoxyModel in the other module since one
            // will be created when that module is processed. If we make one as well there will
            // be a duplicate (causes proguard errors and is just wrong).
            memoizer.getInheritedEpoxyAttributes(
                currentEpoxyModel.superclass.ensureLoaded(),
                generatedModelInfo.generatedName.packageName(),
                logger,
                includeSuperClass = { superClassElement ->
                    !modelClassMap.keys.contains(superClassElement)
                }
            ).let { attributeInfos ->
                generatedModelInfo.addAttributes(attributeInfos)
            }
        }
    }

    /**
     * Check each model for super classes that also have attributes. For each super class with
     * attributes we add those attributes to the attributes of the generated class, so that a
     * generated class contains all the attributes of its super classes combined.
     *
     * One caveat is that if a sub class is in a different package than its super class we can't
     * include attributes that are package private, otherwise the generated class won't compile.
     */
    private suspend fun updateClassesForInheritance(
        helperClassMap: Map<TypeElement, GeneratedModelInfo>
    ) {
        helperClassMap.forEach("updateClassesForInheritance") { thisModelClass, generatedModelInfo ->
            thisModelClass.ensureLoaded()

            val otherClasses = LinkedHashMap(helperClassMap)
            otherClasses.remove(thisModelClass)

            otherClasses
                .filter { (otherClass, _) ->
                    Utils.isSubtype(thisModelClass, otherClass, typeUtils)
                }
                .forEach { (otherClass, modelInfo) ->
                    val otherAttributes = modelInfo.attributeInfo

                    if (Utils.belongToTheSamePackage(thisModelClass, otherClass, elementUtils)) {
                        generatedModelInfo.addAttributes(otherAttributes)
                    } else {
                        otherAttributes
                            .filterNot { it.isPackagePrivate }
                            .forEach { generatedModelInfo.addAttribute(it) }
                    }
                }
        }
    }

    companion object {
        fun buildAttributeInfo(
            attribute: Element,
            logger: Logger,
            typeUtils: Types,
            elementUtils: Elements,
            memoizer: Memoizer
        ): AttributeInfo {
            Utils.validateFieldAccessibleViaGeneratedCode(
                attribute,
                EpoxyAttribute::class.java,
                logger,
                skipPrivateFieldCheck = true
            )
            return BaseModelAttributeInfo(attribute, typeUtils, elementUtils, logger, memoizer)
        }
    }
}
