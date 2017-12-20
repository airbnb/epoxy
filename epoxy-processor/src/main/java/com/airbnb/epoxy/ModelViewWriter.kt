package com.airbnb.epoxy

import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Used for writing the java code for models generated with @ModelView.
 */
internal class ModelViewWriter(
        private val modelWriter: GeneratedModelWriter,
        val errorLogger: ErrorLogger,
        val types: Types,
        val elements: Elements,
        val configManager: ConfigManager
) {

    fun writeModels(models: List<ModelViewInfo>) {
        for (modelInfo in models) {
            try {
                modelWriter.generateClassForModel(modelInfo, generateBuilderHook(modelInfo))
            } catch (e: Exception) {
                errorLogger.logError(
                        EpoxyProcessorException(e, "Error generating model view classes"))
            }

        }
    }

    private fun generateBuilderHook(modelInfo: ModelViewInfo) = object : GeneratedModelWriter.BuilderHooks() {
        override fun addToBindMethod(
                methodBuilder: MethodSpec.Builder,
                boundObjectParam: ParameterSpec
        ) {

            for (attributeGroup in modelInfo.attributeGroups) {
                val attrCount = attributeGroup.attributes.size
                if (attrCount == 1) {
                    val viewAttribute = attributeGroup.attributes[0] as ViewAttributeInfo
                    methodBuilder
                            .addCode(
                                    buildCodeBlockToSetAttribute(
                                            boundObjectParam.name,
                                            viewAttribute))
                } else {
                    for (i in 0 until attrCount) {
                        val viewAttribute = attributeGroup.attributes[i] as ViewAttributeInfo

                        if (i == 0) {
                            methodBuilder.beginControlFlow(
                                    "if (\$L)",
                                    GeneratedModelWriter.isAttributeSetCode(
                                            modelInfo,
                                            viewAttribute))
                        } else if (i == attrCount - 1 && attributeGroup.isRequired) {
                            methodBuilder.beginControlFlow(
                                    "else")
                        } else {
                            methodBuilder.beginControlFlow(
                                    "else if (\$L)",
                                    GeneratedModelWriter.isAttributeSetCode(
                                            modelInfo,
                                            viewAttribute))
                        }

                        methodBuilder
                                .addCode(
                                        buildCodeBlockToSetAttribute(
                                                boundObjectParam.name,
                                                viewAttribute))
                                .endControlFlow()
                    }

                    if (!attributeGroup.isRequired) {
                        val defaultAttribute = attributeGroup.defaultAttribute as ViewAttributeInfo

                        methodBuilder.beginControlFlow(
                                "else")
                                .addCode(
                                        buildCodeBlockToSetAttribute(
                                                boundObjectParam.name,
                                                defaultAttribute))
                                .endControlFlow()
                    }
                }
            }

        }

        override fun addToBindWithDiffMethod(
                methodBuilder: MethodSpec.Builder,
                boundObjectParam: ParameterSpec,
                previousModelParam: ParameterSpec
        ) {

            for (attributeGroup in modelInfo.attributeGroups) {
                val attributes = attributeGroup.attributes

                methodBuilder.addCode("\n")

                if (attributes.size == 1) {
                    val attribute = attributes.first()

                    methodBuilder.apply {
                        GeneratedModelWriter.startNotEqualsControlFlow(this, attribute)

                        addCode(buildCodeBlockToSetAttribute(
                                boundObjectParam.name,
                                attribute as ViewAttributeInfo))

                        endControlFlow()
                    }
                } else {

                    for ((index, attribute) in attributes.withIndex()) {
                        val isAttributeSetCode = GeneratedModelWriter.isAttributeSetCode(
                                modelInfo,
                                attribute)

                        methodBuilder.apply {
                            beginControlFlow("${if (index != 0) "else " else ""}if (\$L)",
                                             isAttributeSetCode)

                            // For primitives we do a simple != check to check if the prop changed from the previous model.
                            // For objects we first check if the prop was not set on the previous model to be able to skip the equals check in some cases
                            if (attribute.isPrimitive) {
                                GeneratedModelWriter.startNotEqualsControlFlow(this, attribute)
                            } else {
                                beginControlFlow("if (!that.\$L || \$L)", isAttributeSetCode,
                                                 GeneratedModelWriter.notEqualsCodeBlock(attribute))
                            }

                            addCode(buildCodeBlockToSetAttribute(
                                    boundObjectParam.name,
                                    attribute as ViewAttributeInfo))

                            endControlFlow()
                            endControlFlow()
                        }
                    }

                    if (!attributeGroup.isRequired) {
                        val defaultAttribute = attributeGroup.defaultAttribute as ViewAttributeInfo

                        val ifConditionArgs = StringBuilder().apply {
                            attributes.indices.forEach {
                                if (it != 0) {
                                    append(" || ")
                                }
                                append("that.\$L")
                            }
                        }

                        val ifConditionValues = attributes.map {
                            GeneratedModelWriter.isAttributeSetCode(modelInfo, it)
                        }

                        methodBuilder
                                .addComment(
                                        "A value was not set so we should use the default value, but we only need to set it if the previous model had a custom value set.")
                                .beginControlFlow("else if ($ifConditionArgs)",
                                                  *ifConditionValues.toTypedArray())
                                .addCode(buildCodeBlockToSetAttribute(
                                        boundObjectParam.name, defaultAttribute))
                                .endControlFlow()
                    }
                }
            }
        }

        override fun addToHandlePostBindMethod(
                postBindBuilder: MethodSpec.Builder,
                boundObjectParam: ParameterSpec
        ) {

            addAfterPropsAddedMethodsToBuilder(
                    postBindBuilder, modelInfo,
                    boundObjectParam)
        }

        override fun addToUnbindMethod(
                unbindBuilder: MethodSpec.Builder,
                unbindParamName: String
        ) {
            modelInfo.viewAttributes
                    .filter { it.resetWithNull }
                    .forEach {
                        unbindBuilder.addCode(
                                buildCodeBlockToSetAttribute(
                                        objectName = unbindParamName,
                                        attr = it,
                                        setToNull = true
                                )
                        )
                    }

            addResetMethodsToBuilder(unbindBuilder,
                                     modelInfo,
                                     unbindParamName)
        }

        override fun beforeFinalBuild(builder: TypeSpec.Builder) {
            if (modelInfo.saveViewState) {
                builder.addMethod(
                        buildSaveStateMethod())
            }

            if (modelInfo.fullSpanSize) {
                builder.addMethod(buildFullSpanSizeMethod())
            }
        }
    }

    private fun buildCodeBlockToSetAttribute(
            objectName: String,
            attr: ViewAttributeInfo,
            setToNull: Boolean = false
    ): CodeBlock {

        val expression = "\$L.\$L" + if (attr.viewAttributeTypeName == ViewAttributeType.Field) {
            if (setToNull) " = (\$T) null" else " = \$L"
        } else {
            if (setToNull) "((\$T) null)" else "(\$L)"
        }

        return CodeBlock.builder().addStatement(
                expression,
                objectName,
                attr.viewAttributeName,
                if (setToNull) attr.typeMirror else getValueToSetOnView(attr, objectName)
        ).build()
    }

    private fun getValueToSetOnView(
            viewAttribute: ViewAttributeInfo,
            objectName: String
    ): String {
        val fieldName = viewAttribute.getFieldName()

        return if (viewAttribute.generateStringOverloads) {
            "$fieldName.toString($objectName.getContext())"
        } else {
            fieldName
        }
    }

    private fun buildSaveStateMethod(): MethodSpec {
        return MethodSpec.methodBuilder("shouldSaveViewState")
                .addAnnotation(Override::class.java)
                .returns(TypeName.BOOLEAN)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return true")
                .build()
    }

    private fun buildFullSpanSizeMethod(): MethodSpec {
        return MethodSpec.methodBuilder("getSpanSize")
                .addAnnotation(Override::class.java)
                .returns(TypeName.INT)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.INT, "totalSpanCount")
                .addParameter(TypeName.INT, "position")
                .addParameter(TypeName.INT, "itemCount")
                .addStatement("return totalSpanCount")
                .build()
    }

    private fun addResetMethodsToBuilder(
            builder: MethodSpec.Builder,
            modelViewInfo: ModelViewInfo,
            unbindParamName: String
    ) {
        for (methodName in modelViewInfo.resetMethodNames) {
            builder.addStatement("$unbindParamName.$methodName()")
        }
    }

    private fun addAfterPropsAddedMethodsToBuilder(
            methodBuilder: MethodSpec.Builder,
            modelInfo: ModelViewInfo,
            boundObjectParam: ParameterSpec
    ) {
        for (methodName in modelInfo.afterPropsSetMethodNames) {
            methodBuilder.addStatement(boundObjectParam.name + "." + methodName + "()")
        }
    }

}
