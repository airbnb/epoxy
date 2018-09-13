package com.airbnb.epoxy

import com.airbnb.epoxy.Utils.getClassParamFromAnnotation
import com.squareup.javapoet.ClassName
import java.util.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.VariableElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

internal class DataBindingProcessor(
    private val elements: Elements,
    private val types: Types,
    private val errorLogger: ErrorLogger,
    private val configManager: ConfigManager,
    private val resourceProcessor: ResourceProcessor,
    private val dataBindingModuleLookup: DataBindingModuleLookup,
    private val modelWriter: GeneratedModelWriter
) {
    private val modelsToWrite = ArrayList<DataBindingModelInfo>()

    fun process(roundEnv: RoundEnvironment): List<DataBindingModelInfo> {

        roundEnv.getElementsAnnotatedWith(EpoxyDataBindingLayouts::class.java).forEach {

            val layoutResources = resourceProcessor
                .getLayoutsInAnnotation(it, EpoxyDataBindingLayouts::class.java)

            // Get the module name after parsing resources so we can use the resource classes to figure
            // out the module name
            val moduleName = dataBindingModuleLookup.getModuleName(it)

            val enableDoNotHash =
                it.annotation<EpoxyDataBindingLayouts>()?.enableDoNotHash == true
            layoutResources.mapTo(modelsToWrite) {
                DataBindingModelInfo(
                    typeUtils = types,
                    elementUtils = elements,
                    layoutResource = it,
                    moduleName = moduleName,
                    enableDoNotHash = enableDoNotHash
                )
            }
        }

        roundEnv.getElementsAnnotatedWith(EpoxyDataBindingPattern::class.java).forEach { element ->
            val patternAnnotation = element.getAnnotation(EpoxyDataBindingPattern::class.java)

            val layoutPrefix = patternAnnotation.layoutPrefix
            val rClassName = getClassParamFromAnnotation<EpoxyDataBindingPattern>(
                element,
                EpoxyDataBindingPattern::class.java,
                "rClass",
                types
            )
                ?: return@forEach

            val moduleName = rClassName.packageName()
            val layoutClassName = ClassName.get(moduleName, "R", "layout")
            val enableDoNotHash =
                element.annotation<EpoxyDataBindingLayouts>()?.enableDoNotHash == true

            Utils.getElementByName(layoutClassName, elements, types)
                .enclosedElements
                .filterIsInstance<VariableElement>()
                .map { it.simpleName.toString() }
                .filter { it.startsWith(layoutPrefix) }
                .map { ResourceValue(layoutClassName, it, 0 /* value doesn't matter */) }
                .mapTo(modelsToWrite) {
                    DataBindingModelInfo(
                        typeUtils = types,
                        elementUtils = elements,
                        layoutResource = it,
                        moduleName = moduleName,
                        layoutPrefix = layoutPrefix,
                        enableDoNotHash = enableDoNotHash
                    )
                }
        }

        val modelsWritten = resolveDataBindingClassesAndWriteJava()
        if (modelsWritten.isNotEmpty()) {
            // All databinding classes are generated at the same time, so once one is ready they
            // all should be. Since we infer databinding layouts based on a naming pattern we may
            // have some false positives which we can clear from the list if we can't find a
            // databinding class for them.
            modelsToWrite.clear()
        }

        return modelsWritten
    }

    /**
     * True if databinding models have been parsed and are waiting for the DataBinding classes to be
     * generated before they can be written.
     */
    fun hasModelsToWrite() = modelsToWrite.isNotEmpty()

    private fun resolveDataBindingClassesAndWriteJava(): List<DataBindingModelInfo> {
        return modelsToWrite
            .filter { it.parseDataBindingClass() }
            .onEach {
                try {
                    modelWriter.generateClassForModel(it)
                } catch (e: Exception) {
                    errorLogger.logError(e, "Error generating model classes")
                }

                modelsToWrite.remove(it)
            }
    }
}
