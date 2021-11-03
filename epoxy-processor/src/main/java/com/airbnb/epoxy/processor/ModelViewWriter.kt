package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XElement
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

/**
 * Used for writing the java code for models generated with @ModelView.
 */
internal class ModelViewWriter(
    private val modelWriter: GeneratedModelWriter,
    asyncable: Asyncable
) : Asyncable by asyncable {

    fun writeModels(
        models: List<ModelViewInfo>,
        originatingConfigElements: List<XElement>
    ) {
        models.forEach("Write model view classes") { modelInfo ->
            modelWriter.generateClassForModel(
                modelInfo,
                originatingElements = originatingConfigElements + modelInfo.originatingElements(),
                builderHooks = generateBuilderHook(modelInfo)
            )
        }
    }

    private fun generateBuilderHook(modelInfo: ModelViewInfo) =
        object : GeneratedModelWriter.BuilderHooks() {
            override fun addToBindMethod(
                methodBuilder: MethodSpec.Builder,
                boundObjectParam: ParameterSpec
            ) {

                for (attributeGroup in modelInfo.attributeGroups) {
                    val attrCount = attributeGroup.attributes.size
                    fun attr(index: Int) = attributeGroup.attributes[index] as ViewAttributeInfo

                    // If there are multiple attributes, or a default kotlin value, then we need to generate code to
                    // check which properties have been set.
                    val noConditionals = !hasConditionals(attributeGroup)

                    for (i in 0 until attrCount) {
                        val viewAttribute = attr(i)

                        if (noConditionals) {
                            // When there is only one attribute we can simply set the
                            // value. Otherwise we need to handle attibute group checking.
                            methodBuilder
                                .addCode(
                                    buildCodeBlockToSetAttribute(
                                        boundObjectParam.name,
                                        viewAttribute
                                    )
                                )
                            continue
                        }

                        if (i == 0) {
                            methodBuilder.beginControlFlow(
                                "if (\$L)",
                                GeneratedModelWriter.isAttributeSetCode(
                                    modelInfo,
                                    viewAttribute
                                )
                            )
                        } else if (i == attrCount - 1 && attributeGroup.isRequired) {
                            methodBuilder.beginControlFlow(
                                "else"
                            )
                        } else {
                            methodBuilder.beginControlFlow(
                                "else if (\$L)",
                                GeneratedModelWriter.isAttributeSetCode(
                                    modelInfo,
                                    viewAttribute
                                )
                            )
                        }

                        methodBuilder
                            .addCode(
                                buildCodeBlockToSetAttribute(
                                    boundObjectParam.name,
                                    viewAttribute
                                )
                            )
                            .endControlFlow()
                    }

                    if (!attributeGroup.isRequired && !noConditionals) {
                        val defaultAttribute =
                            attributeGroup.defaultAttribute as ViewAttributeInfo

                        methodBuilder.beginControlFlow(
                            "else"
                        )
                            .addCode(
                                buildCodeBlockToSetAttribute(
                                    objectName = boundObjectParam.name,
                                    attr = defaultAttribute,
                                    useKotlinDefaultIfAvailable = true
                                )
                            )
                            .endControlFlow()
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
                    val noConditionals = !hasConditionals(attributeGroup)

                    methodBuilder.addCode("\n")

                    for ((index, attribute) in attributes.withIndex()) {
                        if (noConditionals) {
                            methodBuilder.apply {
                                GeneratedModelWriter.startNotEqualsControlFlow(
                                    this,
                                    attribute
                                )

                                addCode(
                                    buildCodeBlockToSetAttribute(
                                        boundObjectParam.name,
                                        attribute as ViewAttributeInfo
                                    )
                                )

                                endControlFlow()
                            }

                            continue
                        }

                        val isAttributeSetCode = GeneratedModelWriter.isAttributeSetCode(
                            modelInfo,
                            attribute
                        )

                        methodBuilder.apply {
                            beginControlFlow(
                                "${if (index != 0) "else " else ""}if (\$L)",
                                isAttributeSetCode
                            )

                            // For primitives we do a simple != check to check if the prop changed from the previous model.
                            // For objects we first check if the prop was not set on the previous model to be able to skip the equals check in some cases
                            if (attribute.isPrimitive) {
                                GeneratedModelWriter.startNotEqualsControlFlow(
                                    this,
                                    attribute
                                )
                            } else {
                                beginControlFlow(
                                    "if (!that.\$L || \$L)", isAttributeSetCode,
                                    GeneratedModelWriter.notEqualsCodeBlock(attribute)
                                )
                            }

                            addCode(
                                buildCodeBlockToSetAttribute(
                                    boundObjectParam.name,
                                    attribute as ViewAttributeInfo
                                )
                            )

                            endControlFlow()
                            endControlFlow()
                        }
                    }

                    if (!attributeGroup.isRequired && !noConditionals) {
                        val defaultAttribute =
                            attributeGroup.defaultAttribute as ViewAttributeInfo

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
                                "A value was not set so we should use the default value, " +
                                    "but we only need to set it if the previous model " +
                                    "had a custom value set."
                            )
                            .beginControlFlow(
                                "else if ($ifConditionArgs)",
                                *ifConditionValues.toTypedArray()
                            )
                            .addCode(
                                buildCodeBlockToSetAttribute(
                                    objectName = boundObjectParam.name,
                                    attr = defaultAttribute,
                                    useKotlinDefaultIfAvailable = true
                                )
                            )
                            .endControlFlow()
                    }
                }
            }

            override fun addToHandlePostBindMethod(
                postBindBuilder: MethodSpec.Builder,
                boundObjectParam: ParameterSpec
            ) {

                addAfterPropsAddedMethodsToBuilder(
                    postBindBuilder, modelInfo,
                    boundObjectParam
                )
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

                addResetMethodsToBuilder(
                    unbindBuilder,
                    modelInfo,
                    unbindParamName
                )
            }

            override fun addToVisibilityStateChangedMethod(
                visibilityBuilder: MethodSpec.Builder,
                visibilityParamName: String
            ) {
                addVisibilityStateChangedMethodsToBuilder(
                    visibilityBuilder,
                    modelInfo,
                    visibilityParamName
                )
            }

            override fun addToVisibilityChangedMethod(
                visibilityBuilder: MethodSpec.Builder,
                visibilityParamName: String
            ) {
                addVisibilityChangedMethodsToBuilder(
                    visibilityBuilder,
                    modelInfo,
                    visibilityParamName
                )
            }

            override fun beforeFinalBuild(builder: TypeSpec.Builder) {
                if (modelInfo.saveViewState) {
                    builder.addMethod(
                        buildSaveStateMethod()
                    )
                }

                if (modelInfo.fullSpanSize) {
                    builder.addMethod(buildFullSpanSizeMethod())
                }
            }
        }

    private fun buildCodeBlockToSetAttribute(
        objectName: String,
        attr: ViewAttributeInfo,
        setToNull: Boolean = false,
        useKotlinDefaultIfAvailable: Boolean = false
    ): CodeBlock {

        val usingDefaultArg = useKotlinDefaultIfAvailable && attr.hasDefaultKotlinValue

        val expression = "\$L.\$L" + when {
            attr.viewAttributeTypeName == ViewAttributeType.Field -> if (setToNull) " = (\$T) null" else " = \$L"
            setToNull -> "((\$T) null)"
            usingDefaultArg -> "()\$L" // The kotlin default doesn't need a variable, but this let's us share the code with the other case
            else -> "(\$L)"
        }

        return CodeBlock.builder().addStatement(
            expression,
            objectName,
            attr.viewAttributeName,
            when {
                usingDefaultArg -> ""
                setToNull -> attr.typeName
                else -> getValueToSetOnView(attr, objectName)
            }
        ).build()
    }

    private fun getValueToSetOnView(
        viewAttribute: ViewAttributeInfo,
        objectName: String
    ): String {
        val fieldName = viewAttribute.fieldName

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

    private fun addVisibilityStateChangedMethodsToBuilder(
        builder: MethodSpec.Builder,
        modelViewInfo: ModelViewInfo,
        visibilityParamName: String
    ) {
        for (methodName in modelViewInfo.visibilityStateChangedMethodNames) {
            builder.addStatement("$visibilityParamName.$methodName(visibilityState)")
        }
    }

    private fun addVisibilityChangedMethodsToBuilder(
        builder: MethodSpec.Builder,
        modelViewInfo: ModelViewInfo,
        visibilityParamName: String
    ) {
        for (methodName in modelViewInfo.visibilityChangedMethodNames) {
            builder.addStatement(
                "$visibilityParamName.$methodName" +
                    "(percentVisibleHeight, percentVisibleWidth, visibleHeight, visibleWidth)"
            )
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

    companion object {
        fun hasConditionals(attributeGroup: GeneratedModelInfo.AttributeGroup?): Boolean {
            if (attributeGroup == null) return false

            return attributeGroup.attributes.size > 1 || (attributeGroup.defaultAttribute as ViewAttributeInfo?)?.hasDefaultKotlinValue == true
        }
    }
}
