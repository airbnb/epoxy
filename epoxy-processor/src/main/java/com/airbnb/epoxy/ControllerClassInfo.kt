package com.airbnb.epoxy

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import java.util.HashSet

import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

private val GENERATED_HELPER_CLASS_SUFFIX = "_EpoxyHelper"

class ControllerClassInfo(
        private val elementUtils: Elements,
        val controllerClassElement: TypeElement) {

    val models: MutableSet<ControllerModelField> = HashSet()
    val generatedClassName: ClassName = getGeneratedClassName(controllerClassElement)
    val controllerClassType: TypeName = TypeName.get(controllerClassElement.asType())

    fun addModel(controllerModelField: ControllerModelField) {
        models.add(controllerModelField)
    }

    fun addModels(controllerModelFields: Collection<ControllerModelField>) {
        models.addAll(controllerModelFields)
    }

    private fun getGeneratedClassName(controllerClass: TypeElement): ClassName {
        val packageName = elementUtils.getPackageOf(controllerClass).qualifiedName.toString()

        val packageLen = packageName.length + 1
        val className = controllerClass.qualifiedName.toString().substring(packageLen).replace('.', '$')

        return ClassName.get(packageName, "$className$GENERATED_HELPER_CLASS_SUFFIX")
    }

    override fun toString() = "ControllerClassInfo(models=$models, generatedClassName=$generatedClassName, controllerClassType=$controllerClassType)"
}