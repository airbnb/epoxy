package com.airbnb.epoxy.processor

import com.airbnb.epoxy.PackageModelViewConfig
import com.squareup.javapoet.ClassName
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror

class PackageModelViewSettings(
    rClassName: ClassName,
    annotation: PackageModelViewConfig
) {

    // The R class may be R or R2. We create the class name again to make sure we don't use R2.
    val rClass: ClassName = ClassName.get(rClassName.packageName(), "R", "layout")
    val layoutName: String = annotation.defaultLayoutPattern
    val includeAlternateLayouts: Boolean = annotation.useLayoutOverloads
    val generatedModelSuffix: String = annotation.generatedModelSuffix
    val disableGenerateBuilderOverloads: Boolean? =
        annotation.disableGenerateBuilderOverloads.toBoolean()
    val disableGenerateGetters: Boolean? = annotation.disableGenerateGetters.toBoolean()
    val disableGenerateReset: Boolean? = annotation.disableGenerateReset.toBoolean()

    val defaultBaseModel: TypeMirror? by lazy {

        val defaultBaseModel: TypeMirror? = try {
            // this should throw
            annotation.defaultBaseModelClass
            null
        } catch (mte: MirroredTypeException) {
            mte.typeMirror
        }

        if (defaultBaseModel?.isVoidClass() == true) {
            // The default value of the annotation parameter is Void.class to signal that the user
            // does not want to provide a custom base class
            null
        } else {
            defaultBaseModel
        }
    }

    fun getNameForView(viewElement: TypeElement): ResourceValue {
        val viewName = Utils.toSnakeCase(viewElement.simpleName.toString())
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
