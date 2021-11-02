package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.addOriginatingElement
import androidx.room.compiler.processing.writeTo
import com.airbnb.epoxy.EpoxyBuildScope
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import java.util.concurrent.ConcurrentHashMap
import javax.lang.model.element.Modifier

const val MODEL_BUILDER_INTERFACE_SUFFIX = "Builder"

/**
 * Creates an interface for a generated model that contains all of the valid setters for building a
 * model in an EpoxyController. This allows us to expose the interface when building a model and
 * hide all of the non applicable junk that shouldn't be used when building a model (eg equals/bind/toString etc)
 *
 * We can also hide the setters that are legacy from usage with EpoxyAdapter.
 */
class ModelBuilderInterfaceWriter(
    private val filer: XFiler,
    private val environment: XProcessingEnv,
    val asyncable: Asyncable,
    val configManager: ConfigManager,
) : Asyncable by asyncable {

    private val viewInterfacesToGenerate = ConcurrentHashMap<ClassName, InterfaceDetails>()

    data class InterfaceDetails(
        val implementingViews: Set<XTypeElement> = emptySet(),
        val methodsOnInterface: Set<MethodDetails> = emptySet()
    )

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
                addOriginatingElement(modelInfo.viewElement)

                modelInfo.viewInterfaces.forEach { it ->
                    addOriginatingElement(it)

                    val packageName =
                        configManager.getModelViewConfig(modelInfo.viewElement)?.rClass?.packageName()
                            ?: it.packageName
                    val viewInterface =
                        it.className.appendToName("Model_").setPackage(packageName)
                    addSuperinterface(viewInterface)

                    // Store the subset of methods common to all interface implementations so we
                    // can generate the interface with the proper methods later
                    synchronized(viewInterfacesToGenerate) {
                        viewInterfacesToGenerate.putOrMerge(
                            viewInterface,
                            InterfaceDetails(
                                implementingViews = setOf(modelInfo.viewElement),
                                methodsOnInterface = interfaceMethods.map { MethodDetails(it) }
                                    .toSet()
                            )
                        ) { details1, details2 ->
                            InterfaceDetails(
                                implementingViews = details1.implementingViews + details2.implementingViews,
                                methodsOnInterface = details1.methodsOnInterface intersect details2.methodsOnInterface
                            )
                        }
                    }
                }
            }

            addModifiers(Modifier.PUBLIC)
            addTypeVariables(modelInfo.typeVariables)
            addMethods(interfaceMethods)
            if (!configManager.disableDslMarker) {
                addAnnotation(EpoxyBuildScope::class.java)
            }

            if (modelInfo.memoizer.implementsModelCollector(modelInfo.superClassElement)) {
                // If the model implements "ModelCollector" we want the builder too
                addSuperinterface(ClassNames.MODEL_COLLECTOR)
            }

            addOriginatingElement(modelInfo.superClassElement)
        }

        JavaFile.builder(modelInfo.generatedName.packageName(), modelInterface)
            .build()
            .writeTo(filer, mode = XFiler.Mode.Aggregating)

        return getBuilderInterfaceTypeName(modelInfo)
    }

    private fun getInterfaceMethods(
        modelInfo: GeneratedModelInfo,
        methods: MutableList<MethodSpec>,
        interfaceName: ClassName
    ): List<MethodSpec> {
        return methods
            .asSequence()
            .filter {
                !it.hasModifier(Modifier.STATIC)
            }
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
            .toList()
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
        // This means that the generated interface won't necessarily map exactly to the original view interface
        // It just represents the set of props shared by all models with that view interface, which should be all we need in practice.

        viewInterfacesToGenerate.forEach("write view interface") { interfaceName, details ->
            val interfaceSpec = buildInterface(interfaceName) {
                addModifiers(Modifier.PUBLIC)

                addMethods(
                    details.methodsOnInterface.map {
                        it.methodSpec.copy {
                            returns(interfaceName)
                        }
                    }
                        // For cache consistency of generated files make sure these are sorted.
                        // Methods may have the same name due to overloads, so also sorting by
                        // hashcode.
                        .sortedWith(compareBy({ it.name }, { it.hashCode() }))
                )

                details.implementingViews.forEach {
                    // Note: If a brand new model view class is added that implements this interface
                    // it could affect which methods are included on the generated interface since
                    // we use an intersection. If that happens the incremental compiler may not know
                    // that this file was affected, or the incremental annotation processing may
                    // in some way not work correctly. We ignore this possible bug for now as it
                    // would be very rare and there isn't much we can do without a large breaking
                    // change to how the interface generation works.
                    addOriginatingElement(it)
                }
            }

            val implementingViewTypeElement = details.implementingViews.firstOrNull()
            val packageName = implementingViewTypeElement?.let {
                configManager.getModelViewConfig(it)?.rClass?.packageName()
            } ?: interfaceName.packageName()
            JavaFile.builder(packageName, interfaceSpec)
                .build()
                .writeTo(filer, mode = XFiler.Mode.Aggregating)
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
    val generatedModelName = modelInfo.generatedName

    return ClassName.get(
        generatedModelName.packageName(),
        generatedModelName.simpleName().removeSuffix("_").replace(
            "$",
            "_"
        ) + MODEL_BUILDER_INTERFACE_SUFFIX
    )
}
