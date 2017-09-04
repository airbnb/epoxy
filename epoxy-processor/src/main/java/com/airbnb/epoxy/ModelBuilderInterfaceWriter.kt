package com.airbnb.epoxy

import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

const val MODEL_BUILDER_INTERFACE_SUFFIX = "Builder"

/**
 * Creates an interface for a generated model that contains all of the valid setters for building a
 * model in an EpoxyController. This allows us to expose the interface when building a model and
 * hide all of the non applicable junk that shouldn't be used when building a model (eg equals/bind/toString etc)
 *
 * We can also hide the setters that are legacy from usage with EpoxyAdapter.
 */
internal class ModelBuilderInterfaceWriter(
        val filer: Filer,
        val modelInfo: GeneratedModelInfo,
        val methods: MutableList<MethodSpec>
) {

    /** These setters can't be used with models in an EpoxyController, they were made for EpoxyAdapter. */
    val blackListedLegacySetterNames = setOf("hide", "show", "reset")

    fun addInterface(modelBuilder: TypeSpec.Builder) {
        val generatedModelName = modelInfo.generatedClassName

        val interfaceClassName = ClassName.get(
                generatedModelName.packageName(),
                generatedModelName.simpleName().removeSuffix("_") + MODEL_BUILDER_INTERFACE_SUFFIX)

        modelBuilder.addSuperinterface(getInterfaceType(interfaceClassName))

        val modelInterface = TypeSpec.interfaceBuilder(interfaceClassName)
                .addTypeVariables(modelInfo.typeVariables)
                .addMethods(getInterfaceMethods())
                .build()

        JavaFile.builder(generatedModelName.packageName(), modelInterface)
                .build()
                .writeTo(filer)
    }

    private fun getInterfaceType(interfaceClassName: ClassName): TypeName {
        val types: Array<TypeName> = modelInfo.typeVariableNames.toTypedArray()

        return if (types.isEmpty()) {
            interfaceClassName
        } else {
            ParameterizedTypeName.get(interfaceClassName, *types)
        }
    }

    private fun getInterfaceMethods(): List<MethodSpec> {
        return methods.filter {
            it.returnType == modelInfo.parameterizedGeneratedName
        }.filter {
            !blackListedLegacySetterNames.contains(it.name)
        }.map {
            MethodSpec.methodBuilder(it.name).run {
                // Copy everything besides the code implementation and make it abstract
                addModifiers(it.modifiers)
                addModifiers(Modifier.ABSTRACT)
                returns(it.returnType)
                addParameters(it.parameters)
                varargs(it.varargs)
                addTypeVariables(it.typeVariables)
                addExceptions(it.exceptions)

                build()
            }
        }
    }
}