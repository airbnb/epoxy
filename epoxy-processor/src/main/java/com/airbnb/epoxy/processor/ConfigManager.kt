package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XRoundEnv
import androidx.room.compiler.processing.XType
import androidx.room.compiler.processing.XTypeElement
import com.airbnb.epoxy.PackageEpoxyConfig
import com.airbnb.epoxy.PackageModelViewConfig
import com.airbnb.epoxy.processor.PackageConfigSettings.Companion.create
import com.airbnb.epoxy.processor.PackageConfigSettings.Companion.forDefaults

/** Manages configuration settings for different packages.  */
class ConfigManager internal constructor(
    options: Map<String, String>,
    private val environment: XProcessingEnv,
) {
    val packageEpoxyConfigElements: MutableList<XElement> = mutableListOf()
    val packageModelViewConfigElements: MutableList<XElement> = mutableListOf()
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
    val disableDslMarker: Boolean
    val logTimings: Boolean

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

        disableDslMarker = getBooleanOption(
            options,
            PROCESSOR_OPTION_DISABLE_DLS_MARKER,
            defaultValue = false
        )
    }

    fun processPackageEpoxyConfig(roundEnv: XRoundEnv): List<Exception> {
        val errors = mutableListOf<Exception>()

        roundEnv.getElementsAnnotatedWith(PackageEpoxyConfig::class)
            .filterIsInstance<XTypeElement>()
            .forEach { element ->
                packageEpoxyConfigElements.add(element)
                val packageName = element.packageName
                if (configurationMap.containsKey(packageName)) {
                    errors.add(
                        Utils.buildEpoxyException(
                            "Only one Epoxy configuration annotation is allowed per package (%s)",
                            packageName
                        )
                    )
                    return@forEach
                }
                val annotation = element.getAnnotation(PackageEpoxyConfig::class)!!
                configurationMap[packageName] = create(annotation)
            }

        return errors
    }

    fun processPackageModelViewConfig(roundEnv: XRoundEnv): List<Exception> {
        val errors = mutableListOf<Exception>()

        roundEnv.getElementsAnnotatedWith(PackageModelViewConfig::class)
            .filterIsInstance<XTypeElement>()
            .forEach { element ->
                packageModelViewConfigElements.add(element)
                val packageName = element.packageName
                if (modelViewNamingMap.containsKey(packageName)) {
                    errors.add(
                        Utils.buildEpoxyException(
                            "Only one %s annotation is allowed per package (%s)",
                            PackageModelViewConfig::class.java.simpleName,
                            packageName
                        )
                    )
                    return@forEach
                }
                val annotation = element.requireAnnotation(PackageModelViewConfig::class)

                val rClassName = annotation.getAsType("rClass")?.typeElement
                if (rClassName == null) {
                    errors.add(
                        Utils.buildEpoxyException(
                            element,
                            "Unable to get R class details from annotation %s (package: %s)",
                            PackageModelViewConfig::class.java.simpleName,
                            packageName
                        )
                    )
                    return@forEach
                }
                val rLayoutClassString = rClassName.className.reflectionName()
                if (!rLayoutClassString.endsWith(".R") &&
                    !rLayoutClassString.endsWith(".R2")
                ) {
                    errors.add(
                        Utils.buildEpoxyException(
                            element,
                            "Invalid R class in %s. Was '%s' (package: %s)",
                            PackageModelViewConfig::class.java.simpleName,
                            rLayoutClassString,
                            packageName
                        )
                    )
                    return@forEach
                }
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

    fun requiresAbstractModels(classElement: XTypeElement): Boolean {
        return (
            globalRequireAbstractModels ||
                getConfigurationForElement(classElement).requireAbstractModels
            )
    }

    fun implicitlyAddAutoModels(controller: ControllerClassInfo): Boolean {
        return (
            globalImplicitlyAddAutoModels ||
                getConfigurationForPackage(controller.classPackage).implicitlyAddAutoModels
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

    fun getModelViewConfig(viewElement: XTypeElement): PackageModelViewSettings? {
        val packageName = viewElement.packageName
        return getObjectFromPackageMap(
            modelViewNamingMap,
            packageName,
            ifNotFound = null
        )
    }

    fun getDefaultBaseModel(viewElement: XTypeElement): XType? {
        return getModelViewConfig(viewElement)?.defaultBaseModel
    }

    fun includeAlternateLayoutsForViews(viewElement: XTypeElement): Boolean {
        return getModelViewConfig(viewElement)?.includeAlternateLayouts ?: false
    }

    fun generatedModelSuffix(viewElement: XTypeElement): String {
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

    private fun getConfigurationForElement(element: XTypeElement): PackageConfigSettings {
        return getConfigurationForPackage(element.packageName)
    }

    fun getConfigurationForPackage(packageName: String): PackageConfigSettings {
        return getObjectFromPackageMap(
            configurationMap,
            packageName,
            DEFAULT_PACKAGE_CONFIG_SETTINGS
        )!!
    }

    companion object {
        const val PROCESSOR_OPTION_DISABLE_DLS_MARKER = "epoxyDisableDslMarker"
        const val PROCESSOR_OPTION_DISABLE_GENERATE_RESET = "epoxyDisableGenerateReset"
        const val PROCESSOR_OPTION_DISABLE_GENERATE_GETTERS = "epoxyDisableGenerateGetters"
        const val PROCESSOR_OPTION_DISABLE_GENERATE_BUILDER_OVERLOADS =
            "epoxyDisableGenerateOverloads"
        const val PROCESSOR_OPTION_LOG_TIMINGS = "logEpoxyTimings"
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
