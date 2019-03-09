@file:Suppress("asfd")

package com.airbnb.epoxy

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeVariableName
import java.io.File
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier

private const val KOTLIN_EXTENSION_FILE_NAME = "EpoxyModelKotlinExtensions"

internal class KotlinModelBuilderExtensionWriter(
    private val processingEnv: ProcessingEnvironment
) {

    fun generateExtensionsForModels(generatedModels: List<GeneratedModelInfo>) {
        val kaptGeneratedDirPath =
            processingEnv.options[EpoxyProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME]
                ?.replace("kaptKotlin", "kapt")
                ?: run {
                    // Need to change the path because of https://youtrack.jetbrains.com/issue/KT-19097

                    // If the option does not exist this is not being processed by kapt,
                    // so we don't need to generate kotlin extensions
                    return
                }

        generatedModels
            .filter { it.shouldGenerateModel }
            .groupBy { it.generatedClassName.packageName() }
            .map { (packageName, models) ->
                buildExtensionFile(
                    packageName,
                    models
                )
            }
            .forEach {
                it.writeTo(File(kaptGeneratedDirPath))
            }
    }

    private fun buildExtensionFile(
        packageName: String,
        models: List<GeneratedModelInfo>
    ): FileSpec {
        val fileBuilder = FileSpec.builder(
            packageName,
            KOTLIN_EXTENSION_FILE_NAME
        )

        models
            .flatMap {
                if (it.constructors.isEmpty()) {
                    listOf(buildExtensionsForModel(it, null))
                } else {
                    it.constructors.map { constructor ->
                        buildExtensionsForModel(it, constructor)
                    }
                }
            }
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

        // Kotlin cannot directly reference a class with a $ in the name. It must be wrapped in ticks (``)
        val useTicksAroundModelName = model.generatedName.simpleName().contains("$")
        val tick = if (useTicksAroundModelName) "`" else ""

        val initializerLambda = LambdaTypeName.get(
            receiver = getBuilderInterfaceTypeName(model).toKPoet(),
            returnType = ClassName.bestGuess("kotlin.Unit")
        )

        FunSpec.builder(getMethodName(model)).run {
            receiver(ClassNames.EPOXY_CONTROLLER.toKPoet())
            val params = constructor?.params ?: listOf()
            addParameters(params.toKParams())

            addParameter(
                "modelInitializer",
                initializerLambda
            )

            val modelClass = model.parameterizedGeneratedName.toKPoet()
            if (modelClass is ParameterizedTypeName) {
                // We expect the type arguments to be of type TypeVariableName
                // Otherwise we can't get bounds information off of it and can't do much
                modelClass
                    .typeArguments
                    .filterIsInstance<TypeVariableName>()
                    .let { if (it.isNotEmpty()) addTypeVariables(it) }
            }

            addModifiers(KModifier.INLINE)
            if (constructorIsNotPublic) addModifiers(KModifier.INTERNAL)

            beginControlFlow(
                "$tick%T$tick(${params.joinToString(", ") { it.name }}).apply ",
                modelClass
            )
            addStatement("modelInitializer()")
            endControlFlow()
            addStatement(".addTo(this)")
            return build()
        }
    }

    private fun getMethodName(model: GeneratedModelInfo) = model.generatedClassName
        .simpleName()
        .lowerCaseFirstLetter()
        .removeSuffix(GeneratedModelInfo.GENERATED_MODEL_SUFFIX)
        .removeSuffix(DataBindingModelInfo.BINDING_SUFFIX)
        .removeSuffix(GeneratedModelInfo.GENERATED_CLASS_NAME_SUFFIX)
        .replace("$", "")
        .removeSuffix("Epoxy")
}
