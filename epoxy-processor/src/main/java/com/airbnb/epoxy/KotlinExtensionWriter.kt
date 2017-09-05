package com.airbnb.epoxy

import com.squareup.kotlinpoet.*
import java.io.*
import javax.annotation.processing.*

private const val KOTLIN_EXTENSION_FILE_NAME = "EpoxyModelKotlinExtensions"

internal class KotlinExtensionWriter(
        private val processingEnv: ProcessingEnvironment,
        private val errorLogger: ErrorLogger
) {

    fun generateExtensionsForModels(generatedModels: List<GeneratedModelInfo>) {
        val kaptGeneratedDirPath = processingEnv.options[EpoxyProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME]
                ?.replace("kaptKotlin", "kapt")
                ?: run {
            // Need to change the path because of https://youtrack.jetbrains.com/issue/KT-19097

            // If the option does not exist this is not being processed by kapt,
            // so we don't need to generate kotlin extensions
            return
        }

        generatedModels.groupBy { it.generatedClassName.packageName() }
                .map { (packageName, models) ->
                    buildExtensionFile(
                            packageName,
                            models)
                }
                .forEach {
                    it.writeTo(File(kaptGeneratedDirPath))
                }
    }

    private fun buildExtensionFile(
            packageName: String,
            models: List<GeneratedModelInfo>
    ): KotlinFile {
        val fileBuilder = KotlinFile.builder(
                packageName,
                KOTLIN_EXTENSION_FILE_NAME)

        models.map { buildExtensionForModel(it) }
                .forEach { fileBuilder.addFun(it) }

        return fileBuilder.build()
    }

    private fun buildExtensionForModel(model: GeneratedModelInfo): FunSpec {

        val initializerLambda = LambdaTypeName.get(
                receiver = getBuilderInterfaceTypeName(model).toKPoet(),
                returnType = ClassName.bestGuess("kotlin.Unit"))

        FunSpec.builder(getMethodName(model)).run {
            receiver(ClassNames.EPOXY_CONTROLLER.toKPoet())
            addParameter(
                    "modelInitializer",
                    initializerLambda)
            addModifiers(KModifier.INLINE)
            beginControlFlow(
                    "%T().apply ",
                    model.generatedClassName.toKPoet())
            addStatement("modelInitializer()")
            endControlFlow()
            addStatement(".addTo(this)")
            return build()
        }
    }

    private fun getMethodName(model: GeneratedModelInfo)
            = model.generatedClassName
            .simpleName()
            .lowerCaseFirstLetter()
            .removeSuffix(GeneratedModelInfo.GENERATED_MODEL_SUFFIX)
            .removeSuffix(DataBindingModelInfo.BINDING_SUFFIX)
            .removeSuffix(GeneratedModelInfo.GENERATED_CLASS_NAME_SUFFIX)
            .replace("$", "")
            .removeSuffix("Epoxy")
}

fun String.lowerCaseFirstLetter(): String {
    if (isEmpty()) {
        return this
    }

    return Character.toLowerCase(get(0)) + substring(1)
}

typealias JavaClassName = com.squareup.javapoet.ClassName
typealias JavaTypeName = com.squareup.javapoet.TypeName
typealias JavaWildcardTypeName = com.squareup.javapoet.WildcardTypeName
typealias JavaArrayTypeName = com.squareup.javapoet.ArrayTypeName
typealias JavaTypeVariableName = com.squareup.javapoet.TypeVariableName
typealias JavaParametrizedTypeName = com.squareup.javapoet.ParameterizedTypeName

typealias KotlinClassName = com.squareup.kotlinpoet.ClassName
typealias KotlinParameterizedTypeName = com.squareup.kotlinpoet.ParameterizedTypeName
typealias KotlinTypeName = com.squareup.kotlinpoet.TypeName
typealias KotlinWildcardTypeName = com.squareup.kotlinpoet.WildcardTypeName
typealias KotlinTypeVariableName = com.squareup.kotlinpoet.TypeVariableName

fun JavaClassName.toKPoet() = KotlinClassName(
        packageName(),
        simpleName())

// Does not support transferring annotations
fun JavaWildcardTypeName.toKPoet() =
        if (!lowerBounds.isEmpty()) {
            KotlinWildcardTypeName.supertypeOf(lowerBounds.first().toKPoet())
        } else {
            KotlinWildcardTypeName.subtypeOf(upperBounds.first().toKPoet())
        }

// Does not support transferring annotations
fun JavaParametrizedTypeName.toKPoet()
        = KotlinParameterizedTypeName.get(
        this.rawType.toKPoet(),
        *typeArguments.toKPoet().toTypedArray())

// Does not support transferring annotations
fun JavaArrayTypeName.toKPoet()
        = KotlinParameterizedTypeName.get(
        KotlinClassName.bestGuess("kotlin.Array"),
        this.componentType.toKPoet())

// Does not support transferring annotations
fun JavaTypeVariableName.toKPoet()
        = KotlinTypeVariableName.invoke(
        name,
        *bounds.toKPoet().toTypedArray())

fun JavaTypeName.toKPoet(): KotlinTypeName = when (this) {
    is JavaClassName -> toKPoet()
    is JavaParametrizedTypeName -> toKPoet()
    is JavaArrayTypeName -> toKPoet()
    is JavaTypeVariableName -> toKPoet()
    is JavaWildcardTypeName -> toKPoet()
    else -> throw IllegalArgumentException("Unsupported type: ${this::class.simpleName}")
}

fun <T : JavaTypeName> Iterable<T>.toKPoet() = map { it.toKPoet() }


