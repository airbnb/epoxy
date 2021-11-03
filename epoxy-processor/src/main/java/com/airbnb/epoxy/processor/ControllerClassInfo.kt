package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XTypeElement
import com.airbnb.epoxy.processor.resourcescanning.ResourceScanner
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import java.util.HashSet

private const val GENERATED_HELPER_CLASS_SUFFIX = "_EpoxyHelper"

class ControllerClassInfo(
    // Not holding on to any symbols so that this can be stored across rounds
    controllerClassElement: XTypeElement,
    val resourceProcessor: ResourceScanner,
    memoizer: Memoizer
) {
    // Exception to the rule of not reusing symbols across rounds - should be safe for originating
    // elements since those just report the file information which should not change.
    val originatingElement: XElement = controllerClassElement
    val classPackage: String = controllerClassElement.packageName
    val models: MutableSet<ControllerModelField> = HashSet()
    val modelsImmutable: Set<ControllerModelField> get() = synchronized(models) { models.toSet() }

    val generatedClassName: ClassName = getGeneratedClassName(controllerClassElement)
    val controllerClassType: TypeName = controllerClassElement.type.typeNameWithWorkaround(memoizer)

    val imports: List<String> by lazy {
        resourceProcessor.getImports(controllerClassElement)
    }

    fun addModel(controllerModelField: ControllerModelField) = synchronized(models) {
        models.add(controllerModelField)
    }

    fun addModels(controllerModelFields: Collection<ControllerModelField>) = synchronized(models) {
        models.addAll(controllerModelFields)
    }

    private fun getGeneratedClassName(controllerClass: XTypeElement): ClassName {
        val packageName = controllerClass.packageName

        val packageLen = packageName.length + 1
        val className =
            controllerClass.qualifiedName.substring(packageLen).replace('.', '$')

        return ClassName.get(packageName, "$className$GENERATED_HELPER_CLASS_SUFFIX")
    }

    override fun toString() =
        "ControllerClassInfo(models=$models, generatedClassName=$generatedClassName, " +
            "controllerClassType=$controllerClassType)"
}
