package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XFieldElement
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XRoundEnv
import androidx.room.compiler.processing.XTypeElement
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import java.util.LinkedHashMap
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class EpoxyProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return EpoxyProcessor(environment)
    }
}

/**
 * Looks for [EpoxyAttribute] annotations and generates a subclass for all classes that have
 * those attributes. The generated subclass includes setters, getters, equals, and hashcode for the
 * given field. Any constructors on the original class are duplicated. Abstract classes are ignored
 * since generated classes would have to be abstract in order to guarantee they compile, and that
 * reduces their usefulness and doesn't make as much sense to support.
 */
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
class EpoxyProcessor @JvmOverloads constructor(
    kspEnvironment: SymbolProcessorEnvironment? = null
) : BaseProcessorWithPackageConfigs(kspEnvironment) {

    override val usesPackageEpoxyConfig: Boolean = true
    override val usesModelViewConfig: Boolean = false
    private val styleableModelsToWrite = mutableListOf<BasicGeneratedModelInfo>()

    override fun additionalSupportedAnnotations(): List<KClass<*>> = listOf(
        EpoxyModelClass::class,
        EpoxyAttribute::class
    )

    override fun processRound(
        environment: XProcessingEnv,
        round: XRoundEnv,
        memoizer: Memoizer,
        timer: Timer,
        roundNumber: Int
    ): List<XElement> {
        super.processRound(environment, round, memoizer, timer, roundNumber)
        val modelClassMap = ConcurrentHashMap<XTypeElement, GeneratedModelInfo>()

        round.getElementsAnnotatedWith(EpoxyAttribute::class)
            .filterIsInstance<XFieldElement>()
            .also {
                timer.markStepCompleted("get epoxy attributes")
            }
            .mapNotNull { annotatedElement ->
                getOrCreateTargetClass(
                    modelClassMap,
                    annotatedElement.enclosingElement as XTypeElement,
                    memoizer
                )?.let {
                    annotatedElement to it
                }
            }
            .also {
                timer.markStepCompleted("parse controller classes")
            }
            .map { (attribute, targetClass) ->
                buildAttributeInfo(
                    attribute,
                    logger,
                    memoizer
                ) to targetClass
            }.forEach { (attributeInfo, targetClass) ->
                // Do this after, synchronously, to preserve order of the generated attributes.
                // This keeps the generated code consistent, which is necessary for cache keys,
                // and some users may rely on the order that attributes are set (even though they shouldn't)
                targetClass.addAttribute(attributeInfo)
            }

        timer.markStepCompleted("build attribute info")

        round.getElementsAnnotatedWith(EpoxyModelClass::class)
            .filterIsInstance<XTypeElement>()
            .also {
                timer.markStepCompleted("get model classes")
            }
            .map { clazz ->
                getOrCreateTargetClass(modelClassMap, clazz, memoizer)
            }
        timer.markStepCompleted("build target class models")

        if (isKsp()) {
            modelClassMap.values
                .filterIsInstance<BasicGeneratedModelInfo>()
                .mapNotNull { it.boundObjectTypeElement }
                .filter { !it.validate() }
                .let { invalidModelTypes ->
                    timer.markStepCompleted("validate symbols")
                    if (invalidModelTypes.isNotEmpty()) {
                        return invalidModelTypes
                    }
                }
        }

        addAttributesFromOtherModules(modelClassMap, memoizer)
        timer.markStepCompleted("add attributes from other modules")

        updateClassesForInheritance(modelClassMap)
        timer.markStepCompleted("update classes for inheritance")

        val modelInfos = modelClassMap.values

        val styleableModels = modelInfos
            .filterIsInstance<BasicGeneratedModelInfo>()
            .filter { modelInfo ->
                modelInfo.superClassElement.getAnnotation(EpoxyModelClass::class)?.value?.layout == 0 &&
                    modelInfo.boundObjectTypeElement?.hasStyleableAnnotation() == true
            }
        timer.markStepCompleted("check for styleable models")

        styleableModelsToWrite.addAll(styleableModels)

        modelInfos.minus(styleableModels).mapNotNull {
            writeModel(it, memoizer)
        }

        styleableModelsToWrite.mapNotNull { modelInfo ->
            if (tryAddStyleBuilderAttribute(modelInfo, environment, memoizer)) {
                writeModel(modelInfo, memoizer)
                modelInfo
            } else {
                null
            }
        }
            .let { styleableModelsToWrite.removeAll(it) }

        generatedModels.addAll(modelClassMap.values)
        timer.markStepCompleted("write models")

        return emptyList()
    }

    private fun writeModel(modelInfo: GeneratedModelInfo, memoizer: Memoizer) {
        createModelWriter(memoizer).generateClassForModel(
            modelInfo,
            originatingElements = modelInfo.originatingElements()
        )
    }

    private fun getOrCreateTargetClass(
        modelClassMap: MutableMap<XTypeElement, GeneratedModelInfo>,
        classElement: XTypeElement,
        memoizer: Memoizer,
    ): GeneratedModelInfo? {
        modelClassMap[classElement]?.let { return it }

        val isFinal = classElement.isFinal()
        if (isFinal) {
            logger.logError(
                "Class with %s annotations cannot be final: %s",
                EpoxyAttribute::class.java.simpleName, classElement.name
            )
        }

        // Nested classes must be static
        if (classElement.enclosingTypeElement != null) {
            if (!classElement.isStatic()) {
                logger.logError(
                    "Nested model classes must be static. (class: %s)",
                    classElement.name
                )
                return null
            }
        }

        if (!classElement.isEpoxyModel(memoizer)) {
            logger.logError(
                classElement,
                "Class with %s annotations must extend %s (%s)",
                EpoxyAttribute::class.java.simpleName, Utils.EPOXY_MODEL_TYPE,
                classElement.name
            )
            return null
        }

        if (configManager.requiresAbstractModels(classElement) && !classElement.isAbstract()
        ) {
            logger
                .logError(
                    classElement,
                    "Epoxy model class must be abstract (%s)",
                    classElement.name
                )
        }

        val generatedModelInfo = BasicGeneratedModelInfo(
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
    private fun addAttributesFromOtherModules(
        modelClassMap: Map<XTypeElement, GeneratedModelInfo>,
        memoizer: Memoizer,
    ) {
        modelClassMap.entries.forEach("addAttributesFromOtherModules") { (currentEpoxyModel, generatedModelInfo) ->
            // We add just the attribute info to the class in our module. We do NOT want to
            // generate a class for the super class EpoxyModel in the other module since one
            // will be created when that module is processed. If we make one as well there will
            // be a duplicate (causes proguard errors and is just wrong).
            memoizer.getInheritedEpoxyAttributes(
                currentEpoxyModel.superType!!,
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
    private fun updateClassesForInheritance(
        helperClassMap: Map<XTypeElement, GeneratedModelInfo>
    ) {
        helperClassMap.forEach("updateClassesForInheritance") { thisModelClass, generatedModelInfo ->

            val otherClasses = LinkedHashMap(helperClassMap)
            otherClasses.remove(thisModelClass)

            otherClasses
                .filter { (otherClass, _) ->
                    thisModelClass.isSubTypeOf(otherClass)
                }
                .forEach { (otherClass, modelInfo) ->
                    val otherAttributes = modelInfo.attributeInfoImmutable

                    if (thisModelClass.isInSamePackageAs(otherClass)) {
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
            attribute: XFieldElement,
            logger: Logger,
            memoizer: Memoizer
        ): AttributeInfo {
            Utils.validateFieldAccessibleViaGeneratedCode(
                attribute,
                EpoxyAttribute::class.java,
                logger,
                skipPrivateFieldCheck = true
            )
            return BaseModelAttributeInfo(attribute, logger, memoizer)
        }
    }
}
