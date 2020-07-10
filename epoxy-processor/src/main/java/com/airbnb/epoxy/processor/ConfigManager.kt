package com.airbnb.epoxy.processor

import com.airbnb.epoxy.PackageEpoxyConfig
import com.airbnb.epoxy.PackageModelViewConfig
import com.airbnb.epoxy.processor.PackageConfigSettings.Companion.create
import com.airbnb.epoxy.processor.PackageConfigSettings.Companion.forDefaults
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/** Manages configuration settings for different packages.  */
class ConfigManager internal constructor(
    options: Map<String, String>,
    private val elementUtils: Elements,
    private val typeUtils: Types
) {
    val packageEpoxyConfigElements: MutableList<Element> = mutableListOf()
    val packageModelViewConfigElements: MutableList<Element> = mutableListOf()
    private val configurationMap: MutableMap<String, PackageConfigSettings> = mutableMapOf()
    private val modelViewNamingMap: MutableMap<String, PackageModelViewSettings?> = mutableMapOf()
    private val validateModelUsage: Boolean
    private val globalRequireHashCode: Boolean
    private val globalRequireAbstractModels: Boolean
    private val globalImplicitlyAddAutoModels: Boolean
    private val disableKotlinExtensionGeneration: Boolean
    private val disableGenerateReset: Boolean
    private val disableGenerateGetters: Boolean
    private val disableGenerateBuilderOverloads: Boolean
    val logTimings: Boolean
    val enableCoroutines: Boolean

    init {
        validateModelUsage = getBooleanOption(
            options,
            PROCESSOR_OPTION_VALIDATE_MODEL_USAGE,
            defaultValue = true
        )

        globalRequireHashCode = getBooleanOption(
            options, PROCESSOR_OPTION_REQUIRE_HASHCODE,
            PackageEpoxyConfig.REQUIRE_HASHCODE_DEFAULT
        )

        globalRequireAbstractModels = getBooleanOption(
            options,
            PROCESSOR_OPTION_REQUIRE_ABSTRACT_MODELS,
            PackageEpoxyConfig.REQUIRE_ABSTRACT_MODELS_DEFAULT
        )

        globalImplicitlyAddAutoModels = getBooleanOption(
            options,
            PROCESSOR_OPTION_IMPLICITLY_ADD_AUTO_MODELS,
            PackageEpoxyConfig.IMPLICITLY_ADD_AUTO_MODELS_DEFAULT
        )

        disableKotlinExtensionGeneration = getBooleanOption(
            options,
            PROCESSOR_OPTION_DISABLE_KOTLIN_EXTENSION_GENERATION,
            defaultValue = false
        )

        logTimings = getBooleanOption(
            options,
            PROCESSOR_OPTION_LOG_TIMINGS,
            defaultValue = false
        )

        enableCoroutines = getBooleanOption(
            options,
            PROCESSOR_OPTION_ENABLE_PARALLEL,
            defaultValue = false
        )

        disableGenerateReset = getBooleanOption(
            options,
            PROCESSOR_OPTION_DISABLE_GENERATE_RESET,
            defaultValue = false
        )

        disableGenerateGetters = getBooleanOption(
            options,
            PROCESSOR_OPTION_DISABLE_GENERATE_GETTERS,
            defaultValue = false
        )

        disableGenerateBuilderOverloads = getBooleanOption(
            options,
            PROCESSOR_OPTION_DISABLE_GENERATE_BUILDER_OVERLOADS,
            defaultValue = false
        )
    }

    fun processPackageEpoxyConfig(roundEnv: RoundEnvironment): List<Exception> {
        val errors = mutableListOf<Exception>()

        for (element in roundEnv.getElementsAnnotatedWith(PackageEpoxyConfig::class.java)) {
            packageEpoxyConfigElements.add(element)
            val packageName = elementUtils.getPackageOf(element).qualifiedName.toString()
            if (configurationMap.containsKey(packageName)) {
                errors.add(
                    Utils.buildEpoxyException(
                        "Only one Epoxy configuration annotation is allowed per package (%s)",
                        packageName
                    )
                )
                continue
            }
            val annotation = element.getAnnotation(PackageEpoxyConfig::class.java)
            configurationMap[packageName] = create(annotation)
        }

        return errors
    }

    fun processPackageModelViewConfig(roundEnv: RoundEnvironment): List<Exception> {
        val errors = mutableListOf<Exception>()

        for (element in roundEnv.getElementsAnnotatedWith(PackageModelViewConfig::class.java)) {
            packageModelViewConfigElements.add(element)
            val packageName = elementUtils.getPackageOf(element).qualifiedName.toString()
            if (modelViewNamingMap.containsKey(packageName)) {
                errors.add(
                    Utils.buildEpoxyException(
                        "Only one %s annotation is allowed per package (%s)",
                        PackageModelViewConfig::class.java.simpleName,
                        packageName
                    )
                )
                continue
            }
            val rClassName = Utils.getClassParamFromAnnotation(
                element,
                PackageModelViewConfig::class.java,
                "rClass",
                typeUtils
            )
            if (rClassName == null) {
                errors.add(
                    Utils.buildEpoxyException(
                        "Unable to get R class details from annotation %s (package: %s)",
                        PackageModelViewConfig::class.java.simpleName,
                        packageName
                    )
                )
                continue
            }
            val rLayoutClassString = rClassName.reflectionName()
            if (!rLayoutClassString.endsWith(".R") &&
                !rLayoutClassString.endsWith(".R2")
            ) {
                errors.add(
                    Utils.buildEpoxyException(
                        "Invalid R class in %s. Was '%s' (package: %s)",
                        PackageModelViewConfig::class.java.simpleName,
                        rLayoutClassString,
                        packageName
                    )
                )
                continue
            }
            val annotation = element.getAnnotation(PackageModelViewConfig::class.java)
            modelViewNamingMap[packageName] = PackageModelViewSettings(rClassName, annotation)
        }

        return errors
    }

    fun requiresHashCode(attributeInfo: AttributeInfo): Boolean {
        return if (attributeInfo is ViewAttributeInfo) {
            // View props are forced to implement hash and equals since it is a safer pattern
            true
        } else {
            globalRequireHashCode || attributeInfo.packageName?.let { packageName ->
                getConfigurationForPackage(packageName).requireHashCode
            } == true
        }

        // Legacy models can choose whether they want to require it
    }

    fun requiresAbstractModels(classElement: TypeElement): Boolean {
        return (
            globalRequireAbstractModels ||
                getConfigurationForElement(classElement).requireAbstractModels
            )
    }

    fun implicitlyAddAutoModels(controller: ControllerClassInfo): Boolean {
        return (
            globalImplicitlyAddAutoModels ||
                getConfigurationForElement(controller.controllerClassElement).implicitlyAddAutoModels
            )
    }

    fun disableKotlinExtensionGeneration(): Boolean = disableKotlinExtensionGeneration

    /**
     * If true, Epoxy models added to an EpoxyController will be
     * validated at run time to make sure they are properly used.
     *
     * By default this is true, and it is highly recommended to enable it to prevent accidental misuse
     * of your models. However, you may want to disable this for production builds to avoid the
     * overhead of the runtime validation code.
     *
     * Using a debug build flag is a great way to do this.
     */
    fun shouldValidateModelUsage(): Boolean = validateModelUsage

    fun getModelViewConfig(modelViewInfo: ModelViewInfo?): PackageModelViewSettings? {
        if (modelViewInfo == null) return null
        return getModelViewConfig(modelViewInfo.viewElement)
    }

    fun getModelViewConfig(viewElement: Element): PackageModelViewSettings? {
        val packageName = elementUtils.getPackageOf(viewElement).qualifiedName.toString()
        return getObjectFromPackageMap(
            modelViewNamingMap,
            packageName,
            ifNotFound = null
        )
    }

    fun getDefaultBaseModel(viewElement: TypeElement): TypeMirror? {
        return getModelViewConfig(viewElement)?.defaultBaseModel
    }

    fun includeAlternateLayoutsForViews(viewElement: TypeElement): Boolean {
        return getModelViewConfig(viewElement)?.includeAlternateLayouts ?: false
    }

    fun generatedModelSuffix(viewElement: TypeElement): String {
        return getModelViewConfig(viewElement)?.generatedModelSuffix
            ?: GeneratedModelInfo.GENERATED_MODEL_SUFFIX
    }

    fun disableGenerateBuilderOverloads(modelInfo: GeneratedModelInfo): Boolean {
        return getModelViewConfig(modelInfo as? ModelViewInfo)?.disableGenerateBuilderOverloads
            ?: disableGenerateBuilderOverloads
    }

    fun disableGenerateReset(modelInfo: GeneratedModelInfo): Boolean {
        return getModelViewConfig(modelInfo as? ModelViewInfo)?.disableGenerateReset
            ?: disableGenerateReset
    }

    fun disableGenerateGetters(modelInfo: GeneratedModelInfo): Boolean {
        return getModelViewConfig(modelInfo as? ModelViewInfo)?.disableGenerateGetters
            ?: disableGenerateGetters
    }

    private fun getConfigurationForElement(element: Element): PackageConfigSettings {
        return getConfigurationForPackage(elementUtils.getPackageOf(element))
    }

    private fun getConfigurationForPackage(packageElement: PackageElement): PackageConfigSettings {
        val packageName = packageElement.qualifiedName.toString()
        return getConfigurationForPackage(packageName)
    }

    private fun getConfigurationForPackage(packageName: String): PackageConfigSettings {
        return getObjectFromPackageMap(
            configurationMap,
            packageName,
            DEFAULT_PACKAGE_CONFIG_SETTINGS
        )!!
    }

    companion object {
        const val PROCESSOR_OPTION_DISABLE_GENERATE_RESET = "epoxyDisableGenerateReset"
        const val PROCESSOR_OPTION_DISABLE_GENERATE_GETTERS = "epoxyDisableGenerateGetters"
        const val PROCESSOR_OPTION_DISABLE_GENERATE_BUILDER_OVERLOADS =
            "epoxyDisableGenerateOverloads"
        const val PROCESSOR_OPTION_LOG_TIMINGS = "logEpoxyTimings"
        const val PROCESSOR_OPTION_ENABLE_PARALLEL = "enableParallelEpoxyProcessing"
        const val PROCESSOR_OPTION_VALIDATE_MODEL_USAGE = "validateEpoxyModelUsage"
        const val PROCESSOR_OPTION_REQUIRE_HASHCODE = "requireHashCodeInEpoxyModels"
        const val PROCESSOR_OPTION_REQUIRE_ABSTRACT_MODELS = "requireAbstractEpoxyModels"
        const val PROCESSOR_OPTION_IMPLICITLY_ADD_AUTO_MODELS = "implicitlyAddAutoModels"
        const val PROCESSOR_OPTION_DISABLE_KOTLIN_EXTENSION_GENERATION =
            "disableEpoxyKotlinExtensionGeneration"
        private val DEFAULT_PACKAGE_CONFIG_SETTINGS = forDefaults()

        private fun getBooleanOption(
            options: Map<String, String>,
            option: String,
            defaultValue: Boolean
        ): Boolean {
            val value = options[option] ?: return defaultValue
            return value.toBoolean()
        }

        private fun <T> getObjectFromPackageMap(
            map: Map<String, T>,
            packageName: String,
            ifNotFound: T
        ): T? {
            if (map.containsKey(packageName)) {
                return map[packageName]
            }

            // If there isn't a configuration for that exact package then we look for configurations for
            // parent packages which include the target package. If multiple parent packages declare
            // configurations we take the configuration from the more nested parent.
            var matchValue: T? = null
            var matchLength = 0
            map.forEach { (entryPackage, value) ->
                if (!packageName.startsWith("$entryPackage.")) {
                    return@forEach
                }
                if (matchLength < entryPackage.length) {
                    matchLength = entryPackage.length
                    matchValue = value
                }
            }

            return matchValue ?: ifNotFound
        }
    }
}
