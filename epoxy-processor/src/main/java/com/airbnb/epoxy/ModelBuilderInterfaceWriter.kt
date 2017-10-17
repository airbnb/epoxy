package com.airbnb.epoxy

import com.squareup.javapoet.*
import javax.annotation.processing.*
import javax.lang.model.element.*
import javax.lang.model.util.*

const val MODEL_BUILDER_INTERFACE_SUFFIX = "Builder"

/**
 * Creates an interface for a generated model that contains all of the valid setters for building a
 * model in an EpoxyController. This allows us to expose the interface when building a model and
 * hide all of the non applicable junk that shouldn't be used when building a model (eg equals/bind/toString etc)
 *
 * We can also hide the setters that are legacy from usage with EpoxyAdapter.
 */
internal class ModelBuilderInterfaceWriter(
        private val filer: Filer,
        val types: Types
) {

    private val viewInterfacesToGenerate = mutableMapOf<ClassName, Set<MethodDetails>>()

    /** These setters can't be used with models in an EpoxyController, they were made for EpoxyAdapter. */
    private val blackListedLegacySetterNames = setOf("hide", "show", "reset")

    fun writeInterface(
            modelInfo: GeneratedModelInfo,
            methods: MutableList<MethodSpec>
    ): TypeName {

        val interfaceName = getBuilderInterfaceClassName(modelInfo)
        val modelInterface = buildInterface(interfaceName) {
            val interfaceMethods = getInterfaceMethods(modelInfo, methods, interfaceName)

            if (modelInfo is ModelViewInfo) {
                modelInfo.generatedViewInterfaceNames.forEach {
                    addSuperinterface(it)

                    // Store the subset of methods common to all interface implementations so we
                    // can generate the interface with the proper methods later
                    viewInterfacesToGenerate
                            .putOrMerge(
                                    it,
                                    interfaceMethods.map { MethodDetails(it) }.toSet()
                            ) { set1, set2 -> set1 intersect set2 }
                }
            }

            addModifiers(Modifier.PUBLIC)
            addTypeVariables(modelInfo.typeVariables)
            addMethods(interfaceMethods)
        }

        JavaFile.builder(modelInfo.generatedClassName.packageName(), modelInterface)
                .build()
                .writeTo(filer)

        return getBuilderInterfaceTypeName(modelInfo)
    }

    private fun getInterfaceMethods(
            modelInfo: GeneratedModelInfo,
            methods: MutableList<MethodSpec>,
            interfaceName: ClassName
    ): List<MethodSpec> {
        return methods
                .filter {
                    it.returnType == modelInfo.parameterizedGeneratedName
                }
                .filter {
                    !blackListedLegacySetterNames.contains(it.name)
                }
                .filter {
                    // Layout throws an exception for programmatic views, so we might a well leave it out too
                    !(modelInfo.isProgrammaticView && it.name == "layout")
                }
                .map {
                    it.copy(
                            // We have the methods return the interface type instead of the model, so
                            // that subclasses of the model can also implement this interface
                            returns = interfaceName,
                            additionalModifiers = listOf(Modifier.ABSTRACT)
                    )
                }
    }

    /**
     * We need to gather information about all of the view interfaces first, and then write the interface classes.
     */
    fun writeFilesForViewInterfaces() {
        // For each interface we figure out which methods to add to it by getting the largest subset of props supported by all models with the interface.
        // This approach has a few advantages:
        // 1. Easily inherit TextProp and other overloads
        // 2. Inherit base model props like id
        // 3. Don't have to figure out if a prop matches an interface method exactly (eg the model method is "clickable" but the interface method is "setClickable")
        // This means that the generated interface won't necesarily map exactly to the original view interface
        // It just represents the set of props shared by all models with that view interface, which should be all we need in practice.

        for ((interfaceName, methodsToWrite) in viewInterfacesToGenerate) {

            val interfaceSpec = buildInterface(interfaceName) {
                addModifiers(Modifier.PUBLIC)

                addMethods(methodsToWrite.map {
                    it.methodSpec.copy {
                        returns(interfaceName)
                    }
                })
            }

            JavaFile.builder(interfaceName.packageName(), interfaceSpec)
                    .build()
                    .writeTo(filer)
        }

        viewInterfacesToGenerate.clear()
    }

    /** A wrapper around MethodSpec that allows us to compare methods with equality that only
     * checks name and param types. This prevents things like annotations and param name from
     * affecting equality, and gets us closer to checking the actual method signature.
     * */
    class MethodDetails(val methodSpec: MethodSpec) {
        val name = methodSpec.name!!
        val params = methodSpec.parameters.map { ParamDetails(it) }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is MethodDetails) return false

            if (name != other.name) return false
            if (params != other.params) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + params.hashCode()
            return result
        }

    }

    /** A wrapper around ParameterSpec that allows us to compare params with equality that only
     * checks param type. This prevents things like annotations and param name from
     * affecting equality.
     * */
    class ParamDetails(val parameterSpec: ParameterSpec) {
        val type = parameterSpec.type!!

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ParamDetails) return false

            if (type != other.type) return false

            return true
        }

        override fun hashCode() = type.hashCode()

    }

}

internal fun getBuilderInterfaceTypeName(modelInfo: GeneratedModelInfo): TypeName {
    val interfaceClassName = getBuilderInterfaceClassName(modelInfo)

    val types: Array<TypeName> = modelInfo.typeVariableNames.toTypedArray()
    return if (types.isEmpty()) {
        interfaceClassName
    } else {
        ParameterizedTypeName.get(interfaceClassName, *types)
    }
}

internal fun getBuilderInterfaceClassName(modelInfo: GeneratedModelInfo): ClassName {
    val generatedModelName = modelInfo.generatedClassName

    return ClassName.get(
            generatedModelName.packageName(),
            generatedModelName.simpleName().removeSuffix("_").replace("$",
                                                                      "_") + MODEL_BUILDER_INTERFACE_SUFFIX)
}