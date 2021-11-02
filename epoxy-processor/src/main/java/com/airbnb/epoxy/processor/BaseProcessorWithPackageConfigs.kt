package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XRoundEnv
import com.airbnb.epoxy.PackageEpoxyConfig
import com.airbnb.epoxy.PackageModelViewConfig
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import kotlin.reflect.KClass

abstract class BaseProcessorWithPackageConfigs(kspEnvironment: SymbolProcessorEnvironment?) :
    BaseProcessor(kspEnvironment) {

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
    fun originatingConfigElements(): List<XElement> = mutableListOf<XElement>().apply {
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

    override fun processRound(
        environment: XProcessingEnv,
        round: XRoundEnv,
        memoizer: Memoizer,
        timer: Timer,
        roundNumber: Int
    ): List<XElement> {
        // We don't expect package configs to be generated, so they should all be picked up in
        // the first round. This is because the configs greatly influence the settings of the generated
        // models, and if models in the first round are created with different configs that models
        // in later rounds (if more configs are picked up) then it would be confusing and potentially
        // buggy.
        // This also is a slight optimization to not do extra lookups.
        if (roundNumber > 1) return emptyList()

        if (usesPackageEpoxyConfig) {
            val errors = configManager.processPackageEpoxyConfig(round)
            logger.logErrors(errors)
        }

        if (usesModelViewConfig) {
            val errors = configManager.processPackageModelViewConfig(round)
            logger.logErrors(errors)
        }

        timer.markStepCompleted("process package configs")

        return emptyList()
    }
}
