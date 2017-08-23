package com.airbnb.epoxy

import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.ProcessingEnvironment


internal class KotlinExtensionGenerator(
        private val processingEnv: ProcessingEnvironment,
        private val errorLogger: ErrorLogger
) {

    fun generateExtensionsForModels(generatedModels: List<GeneratedModelInfo>) {
        val kaptGeneratedDirPath = processingEnv.options[EpoxyProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME]
                ?.replace("kaptKotlin", "kapt") ?: run {
            // https://youtrack.jetbrains.com/issue/KT-19097
            // not being processed by kapt, so we don't need to generate kotlin extensions
            return
        }

        val modelsGroupedByPackage = generatedModels.groupBy { it.generatedClassName.packageName() }


        for ((packageName, models) in modelsGroupedByPackage) {
            KotlinFile.builder(packageName, "EpoxyModelKotlinExtensions").apply {
                models.map { buildFun(it) }
                        .forEach { addFun(it) }

                build().writeTo(File(kaptGeneratedDirPath))
            }
        }
    }

    private fun buildFun(model: GeneratedModelInfo): FunSpec {
        // todo visibility based on model visiblitity

        val initializerLambda = LambdaTypeName.get(
                // todo parameterized classname
                receiver = model.generatedClassName.toKPoet(),
                returnType = ClassName.bestGuess("kotlin.Unit")
        )

        val conditionParam = ParameterSpec.builder(
                name = "condition",
                type = Boolean::class
        ).defaultValue("true")
                .build()


        FunSpec.builder(getMethodName(model)).run {
            receiver(ClassNames.EPOXY_CONTROLLER.toKPoet())
            addParameter(conditionParam)
            addParameter("modelInitializer", initializerLambda)
            addModifiers(listOf(KModifier.INLINE))
            beginControlFlow("if (condition)")
            addStatement("val model = %T()", model.generatedClassName.toKPoet())
            addStatement("model.modelInitializer()")
            addStatement("model.addTo(this)")
            endControlFlow()
            return build()
        }
    }

    private fun getMethodName(model: GeneratedModelInfo): String {
        return model
                .generatedClassName
                .simpleName()
                .lowerCaseFirstLetter()
                .removeSuffix(GeneratedModelInfo.GENERATED_MODEL_SUFFIX)
                .removeSuffix(DataBindingModelInfo.BINDING_SUFFIX)
                .removeSuffix(GeneratedModelInfo.GENERATED_CLASS_NAME_SUFFIX)
                .removeSuffix("Epoxy")
    }

    private fun String.lowerCaseFirstLetter(): String {
        if (isEmpty()) {
            return this
        }

        return Character.toLowerCase(get(0)) + substring(1)
    }

    fun com.squareup.javapoet.ClassName.toKPoet(): com.squareup.kotlinpoet.ClassName {
        return com.squareup.kotlinpoet.ClassName(packageName(), simpleName())
    }

//    fun com.squareup.javapoet.ParameterizedTypeName.toKPoet(): com.squareup.kotlinpoet.ParameterizedTypeName {
//        return com.squareup.kotlinpoet.ParameterizedTypeName.get(rawType.toKPoet(), typeArguments)
//    }
//
//    fun com.squareup.javapoet.TypeName.toKPoet(): com.squareup.kotlinpoet.TypeName {
//        return com.squareup.kotlinpoet.TypeName
//    }

}
