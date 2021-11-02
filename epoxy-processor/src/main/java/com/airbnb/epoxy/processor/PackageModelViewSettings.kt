package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XAnnotationBox
import androidx.room.compiler.processing.XType
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.isVoid
import androidx.room.compiler.processing.isVoidObject
import com.airbnb.epoxy.PackageModelViewConfig
import com.airbnb.epoxy.processor.resourcescanning.ResourceValue
import com.squareup.javapoet.ClassName

class PackageModelViewSettings(
    rClassName: XTypeElement,
    annotation: XAnnotationBox<PackageModelViewConfig>
) {

    // The R class may be R or R2. We create the class name again to make sure we don't use R2.
    val rClass: ClassName = ClassName.get(rClassName.packageName, "R", "layout")
    val layoutName: String = annotation.value.defaultLayoutPattern
    val includeAlternateLayouts: Boolean = annotation.value.useLayoutOverloads
    val generatedModelSuffix: String = annotation.value.generatedModelSuffix
    val disableGenerateBuilderOverloads: Boolean? =
        annotation.value.disableGenerateBuilderOverloads.toBoolean()
    val disableGenerateGetters: Boolean? = annotation.value.disableGenerateGetters.toBoolean()
    val disableGenerateReset: Boolean? = annotation.value.disableGenerateReset.toBoolean()

    val defaultBaseModel: XType? by lazy {
        annotation.getAsType("defaultBaseModelClass")
            ?.takeIf {
                // The default value of the annotation parameter is Void.class to signal that the user
                // does not want to provide a custom base class
                !it.isVoid() && !it.isVoidObject()
            }
    }

    fun getNameForView(viewElement: XTypeElement): ResourceValue {
        val viewName = Utils.toSnakeCase(viewElement.name)
        val resourceName = layoutName.replace("%s", viewName)
        return ResourceValue(rClass, resourceName, 0)
    }

    private fun PackageModelViewConfig.Option.toBoolean(): Boolean? {
        return when (this) {
            PackageModelViewConfig.Option.Default -> null
            PackageModelViewConfig.Option.Enabled -> true
            PackageModelViewConfig.Option.Disabled -> false
        }
    }
}
