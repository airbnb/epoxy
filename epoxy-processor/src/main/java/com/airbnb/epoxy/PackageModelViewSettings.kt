package com.airbnb.epoxy

import com.squareup.javapoet.ClassName

import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror

class PackageModelViewSettings(
        rClassName: ClassName,
        annotation: PackageModelViewConfig) {

    // The R class may be R or R2. We create the class name again to make sure we don't use R2.
    val rClass: ClassName = ClassName.get(rClassName.packageName(), "R", "layout")
    val layoutName: String = annotation.defaultLayoutPattern
    val defaultBaseModel: TypeMirror? = getDefaultBaseModel(annotation)
    val includeAlternateLayouts: Boolean = annotation.useLayoutOverloads
    val generatedModelSuffix: String = annotation.generatedModelSuffix

    private fun getDefaultBaseModel(annotation: PackageModelViewConfig): TypeMirror? {
        var defaultBaseModel: TypeMirror? = null
        try {
            annotation.defaultBaseModelClass // this should throw
        } catch (mte: MirroredTypeException) {
            defaultBaseModel = mte.typeMirror
        }

        if (defaultBaseModel != null
                && defaultBaseModel.toString() == Void::class.java.canonicalName) {
            // The default value of the annotation parameter is Void.class to signal that the user
            // does not want to provide a custom base class
            defaultBaseModel = null
        }
        return defaultBaseModel
    }

    fun getNameForView(viewElement: TypeElement): ResourceValue {
        val viewName = Utils.toSnakeCase(viewElement.simpleName.toString())
        val resourceName = layoutName.replace("%s", viewName)
        return ResourceValue(rClass, resourceName, 0)
    }
}
