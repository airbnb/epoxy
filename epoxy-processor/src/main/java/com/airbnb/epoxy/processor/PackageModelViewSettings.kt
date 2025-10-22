package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XAnnotation
import androidx.room.compiler.processing.XType
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.isVoid
import androidx.room.compiler.processing.isVoidObject
import com.airbnb.epoxy.PackageModelViewConfig
import com.airbnb.epoxy.processor.resourcescanning.ResourceValue
import com.squareup.javapoet.ClassName

class PackageModelViewSettings(
    rClassName: XTypeElement,
    annotation: XAnnotation
) {

    // The R class may be R or R2. We create the class name again to make sure we don't use R2.
    val rClass: ClassName = ClassName.get(rClassName.packageName, "R", "layout")
    val layoutName: String = annotation.getAsString("defaultLayoutPattern")
    val includeAlternateLayouts: Boolean = annotation.getAsBoolean("useLayoutOverloads")
    val generatedModelSuffix: String = annotation.getAsString("generatedModelSuffix")
    val disableGenerateBuilderOverloads: Boolean? = PackageModelViewConfig.Option.valueOf(
        annotation.getAsEnum("disableGenerateBuilderOverloads").name
    ).toBoolean()
    val disableGenerateGetters: Boolean? = PackageModelViewConfig.Option.valueOf(
        annotation.getAsEnum("disableGenerateGetters").name
    ).toBoolean()
    val disableGenerateReset: Boolean? = PackageModelViewConfig.Option.valueOf(
        annotation.getAsEnum("disableGenerateReset").name
    ).toBoolean()

    val defaultBaseModel: XType? by lazy {
        annotation.getAsType("defaultBaseModelClass")
            .takeIf {
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
