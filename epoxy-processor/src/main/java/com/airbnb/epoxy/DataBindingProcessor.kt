package com.airbnb.epoxy

import com.airbnb.epoxy.Utils.*
import com.squareup.javapoet.*
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.element.*
import javax.lang.model.util.*

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

            layoutResources.mapTo(modelsToWrite) {
                DataBindingModelInfo(types, elements, it, moduleName)
            }
        }

        roundEnv.getElementsAnnotatedWith(EpoxyDataBindingPattern::class.java).forEach {
            val patternAnnotation = it.getAnnotation(EpoxyDataBindingPattern::class.java)

            val layoutPrefix = patternAnnotation.layoutPrefix
            val rClassName = getClassParamFromAnnotation<EpoxyDataBindingPattern>(
                    it,
                    EpoxyDataBindingPattern::class.java,
                    "rClass",
                    types)
                    ?: return@forEach

            val moduleName = rClassName.packageName()
            val layoutClassName = ClassName.get(moduleName, "R", "layout")

            Utils.getElementByName(layoutClassName, elements, types)
                    .enclosedElements
                    .filterIsInstance<VariableElement>()
                    .map { it.simpleName.toString() }
                    .filter { it.startsWith(layoutPrefix) }
                    .map { ResourceValue(layoutClassName, it, 0 /* value doesn't matter */) }
                    .mapTo(modelsToWrite) {
                        DataBindingModelInfo(types, elements, it, moduleName, layoutPrefix)
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
