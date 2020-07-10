package com.airbnb.epoxy.processor

import com.airbnb.epoxy.EpoxyDataBindingLayouts
import com.airbnb.epoxy.EpoxyDataBindingPattern
import com.airbnb.epoxy.processor.Utils.getClassParamFromAnnotation
import com.squareup.javapoet.ClassName
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import java.util.Collections
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.VariableElement
import kotlin.reflect.KClass

@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
class DataBindingProcessor : BaseProcessor() {
    private val modelsToWrite = Collections.synchronizedList(
        mutableListOf<DataBindingModelInfo>()
    )

    override fun supportedAnnotations(): List<KClass<*>> = listOf(
        EpoxyDataBindingLayouts::class,
        EpoxyDataBindingPattern::class
    )

    override suspend fun processRound(roundEnv: RoundEnvironment, roundNumber: Int) {

        roundEnv.getElementsAnnotatedWith(EpoxyDataBindingLayouts::class)
            .map("parse EpoxyDataBindingLayouts") { layoutsAnnotatedElement ->

                val layoutResources = resourceProcessor.getLayoutsInAnnotation(
                    layoutsAnnotatedElement,
                    EpoxyDataBindingLayouts::class.java
                )

                // Get the module name after parsing resources so we can use the resource classes to
                // figure out the module name
                val moduleName = dataBindingModuleLookup.getModuleName(layoutsAnnotatedElement)

                val enableDoNotHash =
                    layoutsAnnotatedElement.annotation<EpoxyDataBindingLayouts>()?.enableDoNotHash == true

                layoutResources.map { resourceValue ->
                    DataBindingModelInfo(
                        typeUtils = typeUtils,
                        elementUtils = elementUtils,
                        layoutResource = resourceValue,
                        moduleName = moduleName,
                        enableDoNotHash = enableDoNotHash,
                        annotatedElement = layoutsAnnotatedElement,
                        memoizer = memoizer
                    )
                }
            }.let { dataBindingModelInfos ->
                modelsToWrite.addAll(dataBindingModelInfos.flatten())
            }

        roundEnv.getElementsAnnotatedWith(EpoxyDataBindingPattern::class)
            .map("parse EpoxyDataBindingPattern") { annotatedElement ->

                val patternAnnotation =
                    annotatedElement.getAnnotation(EpoxyDataBindingPattern::class.java)

                val layoutPrefix = patternAnnotation.layoutPrefix
                val rClassName = getClassParamFromAnnotation(
                    annotatedElement,
                    EpoxyDataBindingPattern::class.java,
                    "rClass",
                    typeUtils
                ) ?: return@map emptyList<DataBindingModelInfo>()

                val moduleName = rClassName.packageName()
                val layoutClassName = ClassName.get(moduleName, "R", "layout")
                val enableDoNotHash =
                    annotatedElement.annotation<EpoxyDataBindingPattern>()?.enableDoNotHash == true

                val rClassElement = Utils.getElementByName(layoutClassName, elementUtils, typeUtils)
                rClassElement.ensureLoaded()

                rClassElement
                    .enclosedElementsThreadSafe
                    .asSequence()
                    .filterIsInstance<VariableElement>()
                    .map { it.simpleName.toString() }
                    .filter { it.startsWith(layoutPrefix) }
                    .map { ResourceValue(layoutClassName, it, 0 /* value doesn't matter */) }
                    .toList()
                    .map("Create DataBindingModelInfo") { layoutResource ->
                        DataBindingModelInfo(
                            typeUtils = typeUtils,
                            elementUtils = elementUtils,
                            layoutResource = layoutResource,
                            moduleName = moduleName,
                            layoutPrefix = layoutPrefix,
                            enableDoNotHash = enableDoNotHash,
                            annotatedElement = annotatedElement,
                            memoizer = memoizer
                        )
                    }
            }.let { dataBindingModelInfos ->
                modelsToWrite.addAll(dataBindingModelInfos.flatten())
            }

        val modelsWritten = resolveDataBindingClassesAndWriteJava()
        if (modelsWritten.isNotEmpty()) {
            // All databinding classes are generated at the same time, so once one is ready they
            // all should be. Since we infer databinding layouts based on a naming pattern we may
            // have some false positives which we can clear from the list if we can't find a
            // databinding class for them.
            modelsToWrite.clear()
        }

        generatedModels.addAll(modelsWritten)
    }

    private suspend fun resolveDataBindingClassesAndWriteJava(): List<DataBindingModelInfo> {
        return modelsToWrite.filter("resolveDataBindingClassesAndWriteJava") { bindingModelInfo ->
            bindingModelInfo.parseDataBindingClass() ?: return@filter false
            modelWriter.generateClassForModel(
                bindingModelInfo,
                originatingElements = bindingModelInfo.originatingElements()
            )
            true
        }.also { writtenModels ->
            modelsToWrite.removeAll(writtenModels)
        }
    }
}
