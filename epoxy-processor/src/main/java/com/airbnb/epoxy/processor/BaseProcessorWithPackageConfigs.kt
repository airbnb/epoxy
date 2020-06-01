package com.airbnb.epoxy.processor

import com.airbnb.epoxy.PackageEpoxyConfig
import com.airbnb.epoxy.PackageModelViewConfig
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import kotlin.reflect.KClass

abstract class BaseProcessorWithPackageConfigs : BaseProcessor() {

    abstract val usesPackageEpoxyConfig: Boolean
    abstract val usesModelViewConfig: Boolean

    final override fun supportedAnnotations(): List<KClass<*>> = mutableListOf<KClass<*>>().apply {
        if (usesPackageEpoxyConfig) {
            add(PackageEpoxyConfig::class)
        }
        if (usesModelViewConfig) {
            add(PackageModelViewConfig::class)
        }
    }.plus(additionalSupportedAnnotations())

    abstract fun additionalSupportedAnnotations(): List<KClass<*>>

    /**
     * Returns all of the package config elements applicable to this processor.
     */
    fun originatingConfigElements(): List<Element> = mutableListOf<Element>().apply {
        // TODO: Be more discerning about which config elements are returned here, eg
        // only if they apply to a specific model or package. Perhaps support an isolated processor
        // if a user knows they don't have any package config elements (ie the setting
        // can be provided via an annotation processor option instead.)

        if (usesPackageEpoxyConfig) {
            addAll(configManager.packageEpoxyConfigElements)
        }

        if (usesModelViewConfig) {
            addAll(configManager.packageModelViewConfigElements)
        }
    }

    override suspend fun processRound(roundEnv: RoundEnvironment, roundNumber: Int) {
        // We don't expect package configs to be generated, so they should all be picked up in
        // the first round. This is because the configs greatly influence the settings of the generated
        // models, and if models in the first round are created with different configs that models
        // in later rounds (if more configs are picked up) then it would be confusing and potentially
        // buggy.
        // This also is a slight optimization to not do extra lookups.
        if (roundNumber > 1) return

        logger.measure("Process package configurations") {
            if (usesPackageEpoxyConfig) {
                val errors = configManager.processPackageEpoxyConfig(roundEnv)
                logger.logErrors(errors)
            }

            if (usesModelViewConfig) {
                val errors = configManager.processPackageModelViewConfig(roundEnv)
                logger.logErrors(errors)
            }
        }
    }
}
