package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XAnnotationBox
import com.airbnb.epoxy.PackageEpoxyConfig

/**
 * Stores configuration settings for a package.
 */
class PackageConfigSettings private constructor(
    val requireHashCode: Boolean,
    val requireAbstractModels: Boolean,
    val implicitlyAddAutoModels: Boolean
) {

    companion object {

        fun forDefaults() = PackageConfigSettings(
            PackageEpoxyConfig.REQUIRE_HASHCODE_DEFAULT,
            PackageEpoxyConfig.REQUIRE_ABSTRACT_MODELS_DEFAULT,
            PackageEpoxyConfig.IMPLICITLY_ADD_AUTO_MODELS_DEFAULT
        )

        fun create(configAnnotation: XAnnotationBox<PackageEpoxyConfig>) = PackageConfigSettings(
            configAnnotation.value.requireHashCode,
            configAnnotation.value.requireAbstractModels,
            configAnnotation.value.implicitlyAddAutoModels
        )
    }
}
