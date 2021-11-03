package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XRoundEnv
import androidx.room.compiler.processing.XTypeElement
import com.airbnb.epoxy.EpoxyDataBindingLayouts
import com.airbnb.epoxy.EpoxyDataBindingPattern
import com.airbnb.epoxy.processor.resourcescanning.ResourceValue
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.squareup.javapoet.ClassName
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import java.util.Collections
import kotlin.reflect.KClass

/**
 * Note, Databinding doens't actually work with KSP because it relies on KAPT, and KSP cannot depend
 * on KAPT sources.
 *
 * If that dependency can be resolved then the below processor implementation "should" work.
 */
class DataBindingProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return DataBindingProcessor(environment)
    }
}

@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
class DataBindingProcessor @JvmOverloads constructor(
    kspEnvironment: SymbolProcessorEnvironment? = null
) : BaseProcessor(kspEnvironment) {
    private val modelsToWrite = Collections.synchronizedList(
        mutableListOf<DataBindingModelInfo>()
    )

    override fun supportedAnnotations(): List<KClass<*>> = listOf(
        EpoxyDataBindingLayouts::class,
        EpoxyDataBindingPattern::class
    )

    override fun processRound(
        environment: XProcessingEnv,
        round: XRoundEnv,
        memoizer: Memoizer,
        timer: Timer,
        roundNumber: Int
    ): List<XElement> {
        round.getElementsAnnotatedWith(EpoxyDataBindingLayouts::class)
            .filterIsInstance<XTypeElement>()
            .also {
                timer.markStepCompleted("get databinding layouts")
            }
            .mapNotNull { layoutsAnnotatedElement ->

                val layoutResources = resourceProcessor.getResourceValueList(
                    EpoxyDataBindingLayouts::class,
                    layoutsAnnotatedElement,
                    "value"
                ) ?: run {
                    logger.logError(
                        layoutsAnnotatedElement,
                        "Unable to get EpoxyDataBindingLayouts value from $layoutsAnnotatedElement"
                    )
                    return@mapNotNull null
                }

                // Get the module name after parsing resources so we can use the resource classes to
                // figure out the module name
                val moduleName = dataBindingModuleLookup.getModuleName(layoutsAnnotatedElement)

                val enableDoNotHash =
                    layoutsAnnotatedElement.getAnnotation(EpoxyDataBindingLayouts::class)?.value?.enableDoNotHash == true

                layoutResources.map { resourceValue ->
                    DataBindingModelInfo(
                        layoutResource = resourceValue,
                        moduleName = moduleName,
                        enableDoNotHash = enableDoNotHash,
                        annotatedElement = layoutsAnnotatedElement,
                        memoizer = memoizer
                    )
                }
            }.let { dataBindingModelInfos ->
                timer.markStepCompleted("parse databinding layouts")
                modelsToWrite.addAll(dataBindingModelInfos.flatten())
            }

        round.getElementsAnnotatedWith(EpoxyDataBindingPattern::class)
            .filterIsInstance<XTypeElement>()
            .also {
                timer.markStepCompleted("get databinding patterns")
            }
            .map { annotatedElement ->

                val patternAnnotation =
                    annotatedElement.requireAnnotation(EpoxyDataBindingPattern::class)

                val layoutPrefix = patternAnnotation.value.layoutPrefix
                val rClassName = patternAnnotation.getAsType("rClass")?.typeElement
                    ?: return@map emptyList<DataBindingModelInfo>()

                val moduleName = rClassName.packageName
                val layoutClassName = ClassName.get(moduleName, "R", "layout")
                val enableDoNotHash =
                    annotatedElement.getAnnotation(EpoxyDataBindingPattern::class)?.value?.enableDoNotHash == true

                val rClassElement = environment.requireTypeElement(layoutClassName)

                rClassElement
                    .getDeclaredFields()
                    .asSequence()
                    .map { it.name }
                    .filter { it.startsWith(layoutPrefix) }
                    .map { ResourceValue(layoutClassName, it, 0 /* value doesn't matter */) }
                    .toList()
                    .mapNotNull { layoutResource ->
                        DataBindingModelInfo(
                            layoutResource = layoutResource,
                            moduleName = moduleName,
                            layoutPrefix = layoutPrefix,
                            enableDoNotHash = enableDoNotHash,
                            annotatedElement = annotatedElement,
                            memoizer = memoizer
                        )
                    }
            }.let { dataBindingModelInfos ->
                timer.markStepCompleted("parse databinding patterns")
                modelsToWrite.addAll(dataBindingModelInfos.flatten())
            }

        val modelsWritten = resolveDataBindingClassesAndWriteJava(memoizer)
        timer.markStepCompleted("resolve and write files")
        if (modelsWritten.isNotEmpty()) {
            // All databinding classes are generated at the same time, so once one is ready they
            // all should be. Since we infer databinding layouts based on a naming pattern we may
            // have some false positives which we can clear from the list if we can't find a
            // databinding class for them.
            modelsToWrite.clear()
        }

        generatedModels.addAll(modelsWritten)

        // We need to tell KSP that we are waiting for the databinding element so that we will
        // process another round. We don't have
        // that symbol to return directly, so we just return any symbol.
        return if (isKsp()) {
            modelsToWrite.map { it.annotatedElement }.also {
                // KSP doesn't normally resurface annotated elements in future rounds, but because
                // we return it as a deferred symbol it will allow it to be discovered again in the
                // next round, so to avoid duplicates we clear it.
                modelsToWrite.clear()
            }
        } else {
            emptyList()
        }
    }

    private fun resolveDataBindingClassesAndWriteJava(memoizer: Memoizer): List<DataBindingModelInfo> {
        return modelsToWrite.filter("resolveDataBindingClassesAndWriteJava") { bindingModelInfo ->
            bindingModelInfo.parseDataBindingClass(logger) ?: return@filter false
            createModelWriter(memoizer).generateClassForModel(
                bindingModelInfo,
                originatingElements = bindingModelInfo.originatingElements()
            )
            true
        }.also { writtenModels ->
            modelsToWrite.removeAll(writtenModels)
        }
    }
}
