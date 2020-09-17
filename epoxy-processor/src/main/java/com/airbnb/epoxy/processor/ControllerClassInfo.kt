package com.airbnb.epoxy.processor

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import java.util.HashSet
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

private val GENERATED_HELPER_CLASS_SUFFIX = "_EpoxyHelper"

class ControllerClassInfo(
    private val elementUtils: Elements,
    val controllerClassElement: TypeElement,
    val resourceProcessor: ResourceProcessor
) {

    val models: MutableSet<ControllerModelField> = HashSet()
    val modelsImmutable: Set<ControllerModelField> get() = synchronized(models) { models.toSet() }

    val generatedClassName: ClassName = getGeneratedClassName(controllerClassElement)
    val controllerClassType: TypeName = controllerClassElement.asType().typeNameSynchronized()

    val imports: List<String> by lazy {
        resourceProcessor
            .trees?.getPath(controllerClassElement)
            ?.compilationUnit
            ?.imports?.map { it.qualifiedIdentifier.toString() }
            ?: emptyList()
    }

    fun addModel(controllerModelField: ControllerModelField) = synchronized(models) {
        models.add(controllerModelField)
    }

    fun addModels(controllerModelFields: Collection<ControllerModelField>) = synchronized(models) {
        models.addAll(controllerModelFields)
    }

    private fun getGeneratedClassName(controllerClass: TypeElement): ClassName {
        val packageName = elementUtils.getPackageOf(controllerClass).qualifiedName.toString()

        val packageLen = packageName.length + 1
        val className =
            controllerClass.qualifiedName.toString().substring(packageLen).replace('.', '$')

        return ClassName.get(packageName, "$className$GENERATED_HELPER_CLASS_SUFFIX")
    }

    override fun toString() =
        "ControllerClassInfo(models=$models, generatedClassName=$generatedClassName, " +
            "controllerClassType=$controllerClassType)"
}
