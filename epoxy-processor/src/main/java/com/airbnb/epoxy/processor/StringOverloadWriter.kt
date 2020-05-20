package com.airbnb.epoxy.processor

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import com.airbnb.epoxy.processor.GeneratedModelWriter.Companion.addOnMutationCall
import com.airbnb.epoxy.processor.GeneratedModelWriter.Companion.addParameterNullCheckIfNeeded
import com.airbnb.epoxy.processor.GeneratedModelWriter.Companion.setBitSetIfNeeded
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.MethodSpec.Builder
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import java.util.ArrayList
import javax.lang.model.element.Modifier.PUBLIC

internal class StringOverloadWriter(
    private val modelInfo: GeneratedModelInfo,
    private val attr: AttributeInfo,
    private val configManager: ConfigManager
) {
    private val fieldName: String = attr.fieldName
    private val nullable: Boolean = attr.isNullable == true

    fun buildMethods(): List<MethodSpec> {
        val methods = ArrayList<MethodSpec>()
        methods.add(buildGetter())

        methods.add(finishSetter(buildCharSequenceSetter(baseSetter())))
        methods.add(finishSetter(buildStringResSetter(baseSetter())))
        methods.add(finishSetter(buildStringResWithArgsSetter(baseSetter())))
        methods.add(finishSetter(buildQuantityStringResSetter(baseSetter("QuantityRes"))))

        return methods
    }

    private fun buildCharSequenceSetter(builder: Builder): Builder {
        val paramName = attr.generatedSetterName()
        val paramBuilder = ParameterSpec.builder(CharSequence::class.java, paramName)

        if (nullable) {
            paramBuilder.addAnnotation(Nullable::class.java)
        } else {
            paramBuilder.addAnnotation(NonNull::class.java)
        }

        addJavaDoc(builder, false)

        builder.addParameter(paramBuilder.build())

        addParameterNullCheckIfNeeded(configManager, attr, paramName, builder)
        builder.addStatement("\$L.setValue(\$L)", fieldName, paramName)

        return builder
    }

    private fun buildStringResSetter(builder: Builder): Builder {
        builder.addParameter(
            ParameterSpec.builder(TypeName.INT, STRING_RES_PARAM)
                .addAnnotation(StringRes::class.java)
                .build()
        )

        addJavaDoc(builder, true)
        builder.addStatement("\$L.setValue(\$L)", fieldName, STRING_RES_PARAM)

        return builder
    }

    private fun buildStringResWithArgsSetter(builder: Builder): Builder {
        builder.addParameter(
            ParameterSpec.builder(TypeName.INT, STRING_RES_PARAM)
                .addAnnotation(StringRes::class.java)
                .build()
        )

        addJavaDoc(builder, true)

        builder
            .addParameter(
                ParameterSpec.builder(
                    ArrayTypeName.of(TypeName.OBJECT),
                    ARGS_PARAM
                ).build()
            )

        builder.addStatement("\$L.setValue(\$L, \$L)", fieldName, STRING_RES_PARAM, ARGS_PARAM)
            .varargs()

        return builder
    }

    private fun buildQuantityStringResSetter(builder: Builder): Builder {

        builder.addParameter(
            ParameterSpec.builder(TypeName.INT, PLURAL_RES_PARAM)
                .addAnnotation(PluralsRes::class.java)
                .build()
        )

        addJavaDoc(builder, true)

        builder.addParameter(ParameterSpec.builder(TypeName.INT, QUANTITY_PARAM).build())

        builder
            .addParameter(
                ParameterSpec.builder(
                    ArrayTypeName.of(TypeName.OBJECT),
                    ARGS_PARAM
                ).build()
            )

        builder.addStatement(
            "\$L.setValue(\$L, \$L, \$L)", fieldName, PLURAL_RES_PARAM,
            QUANTITY_PARAM,
            ARGS_PARAM
        )
            .varargs()

        return builder
    }

    private fun baseSetter(suffix: String = ""): Builder {
        val name = attr.generatedSetterName() + suffix

        val builder = MethodSpec.methodBuilder(name)
            .addModifiers(PUBLIC)
            .returns(modelInfo.parameterizedGeneratedName)

        addOnMutationCall(builder)
        setBitSetIfNeeded(modelInfo, attr, builder)
        return builder
    }

    private fun addJavaDoc(
        builder: Builder,
        forStringRes: Boolean
    ) {
        if (attr.javaDoc == null) {
            return
        }

        val javaDoc = CodeBlock.builder()

        if (forStringRes) {
            if (attr.isRequired) {
                javaDoc.add("Throws if a value <= 0 is set.\n<p>\n")
            } else {
                javaDoc.add(
                    "If a value of 0 is set then this attribute will revert to its " +
                        "default value.\n<p>\n"
                )
            }
        }

        builder.addJavadoc(javaDoc.add(attr.javaDoc).build())
    }

    private fun buildGetter(): MethodSpec {
        val getterName = attr.generatedGetterName(
            isOverload = modelInfo.isOverload(attr)
        )
        val builder = MethodSpec.methodBuilder(getterName)
            .addModifiers(PUBLIC)
            .returns(CharSequence::class.java)

        if (nullable) {
            builder.addAnnotation(Nullable::class.java)
        }

        return builder
            .addAnnotations(attr.getterAnnotations)
            .addParameter(ClassNames.ANDROID_CONTEXT, "context")
            .addStatement("return \$L", fieldName + ".toString(context)")
            .build()
    }

    companion object {
        private val PLURAL_RES_PARAM = "pluralRes"
        private val STRING_RES_PARAM = "stringRes"
        private val ARGS_PARAM = "formatArgs"
        private val QUANTITY_PARAM = "quantity"

        private fun finishSetter(builder: MethodSpec.Builder): MethodSpec {
            return builder
                .addStatement("return this")
                .build()
        }
    }
}
