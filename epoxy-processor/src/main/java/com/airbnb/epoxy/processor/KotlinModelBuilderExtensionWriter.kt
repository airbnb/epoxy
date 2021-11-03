package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.addOriginatingElement
import androidx.room.compiler.processing.writeTo
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.javapoet.toKTypeName
import javax.lang.model.element.Modifier

internal class KotlinModelBuilderExtensionWriter(
    val filer: XFiler,
    asyncable: Asyncable
) : Asyncable by asyncable {

    fun generateExtensionsForModels(
        generatedModels: List<GeneratedModelInfo>,
        processorName: String
    ) {
        generatedModels
            .filter { it.shouldGenerateModel }
            .groupBy { it.generatedName.packageName() }
            .mapNotNull("generateExtensionsForModels") { packageName, models ->
                buildExtensionFile(
                    packageName,
                    models,
                    processorName
                )
            }.forEach("writeExtensionsForModels", parallel = false) {
                // Cannot be done in parallel since filer is not thread safe
                it.writeTo(filer, mode = XFiler.Mode.Aggregating)
            }
    }

    private fun buildExtensionFile(
        packageName: String,
        models: List<GeneratedModelInfo>,
        processorName: String
    ): FileSpec {
        val fileBuilder = FileSpec.builder(
            packageName,
            "Epoxy${processorName.removePrefix("Epoxy")}KotlinExtensions"
        )

        models.map {
            if (it.constructors.isEmpty()) {
                listOf(buildExtensionsForModel(it, null))
            } else {
                it.constructors.map { constructor ->
                    buildExtensionsForModel(it, constructor)
                }
            }
        }
            .flatten()
            // Sort by function name to keep ordering consistent across builds. Otherwise if the
            // processor processes models in differing orders we can have indeterminate source file
            // generation which breaks cache keys.
            .sortedBy { it.name }
            .forEach { fileBuilder.addFunction(it) }

        // We suppress Deprecation warnings for this class in case any of the models used are deprecated.
        // This prevents the generated file from causing errors for using deprecated classes.
        fileBuilder.addAnnotation(
            AnnotationSpec.builder(Suppress::class)
                .addMember("%S", "DEPRECATION")
                .build()
        )

        return fileBuilder.build()
    }

    private fun buildExtensionsForModel(
        model: GeneratedModelInfo,
        constructor: GeneratedModelInfo.ConstructorInfo?
    ): FunSpec {
        val constructorIsNotPublic =
            constructor != null && Modifier.PUBLIC !in constructor.modifiers

        val initializerLambda = LambdaTypeName.get(
            receiver = getBuilderInterfaceTypeName(model).toKTypeName(),
            returnType = KClassNames.KOTLIN_UNIT
        )

        FunSpec.builder(getMethodName(model)).run {
            receiver(ClassNames.MODEL_COLLECTOR.toKTypeName())
            val params = constructor?.params ?: listOf()
            addParameters(params.map { it.toKPoet() })

            addParameter(
                "modelInitializer",
                initializerLambda
            )

            val modelClass = model.parameterizedGeneratedName.toKTypeName()
            if (modelClass is ParameterizedTypeName) {
                // We expect the type arguments to be of type TypeVariableName
                // Otherwise we can't get bounds information off of it and can't do much
                modelClass
                    .typeArguments
                    .filterIsInstance<TypeVariableName>()
                    .let { if (it.isNotEmpty()) addTypeVariables(it) }
            }

            addModifiers(KModifier.INLINE)
            addModifiers(if (constructorIsNotPublic) KModifier.INTERNAL else KModifier.PUBLIC)

            addStatement("add(")
            beginControlFlow(
                "%T(${params.joinToString(", ") { it.name }}).apply",
                modelClass
            )
            addStatement("modelInitializer()")
            endControlFlow()
            addStatement(")")

            model.originatingElements().forEach {
                addOriginatingElement(it)
            }
            return build()
        }
    }

    private fun getMethodName(model: GeneratedModelInfo) = model.generatedName
        .simpleName()
        .lowerCaseFirstLetter()
        .removeSuffix(GeneratedModelInfo.GENERATED_MODEL_SUFFIX)
        .removeSuffix(DataBindingModelInfo.BINDING_SUFFIX)
        .removeSuffix(GeneratedModelInfo.GENERATED_CLASS_NAME_SUFFIX)
        .replace("$", "")
        .removeSuffix("Epoxy")
}
