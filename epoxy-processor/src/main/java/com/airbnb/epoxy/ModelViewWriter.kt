package com.airbnb.epoxy

import com.squareup.javapoet.*
import javax.lang.model.element.*
import javax.lang.model.util.*

/**
 * Used for writing the java code for models generated with @ModelView.
 */
internal class ModelViewWriter(
        val modelWriter: GeneratedModelWriter,
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
        internal override fun addToBindMethod(
                methodBuilder: MethodSpec.Builder,
                boundObjectParam: ParameterSpec
        ): Boolean {

            for (attributeGroup in modelInfo.attributeGroups) {
                val attrCount = attributeGroup.attributes.size
                if (attrCount == 1) {
                    val viewAttribute = attributeGroup.attributes[0] as ViewAttributeInfo
                    methodBuilder
                            .addCode(
                                    buildCodeBlockToSetAttribute(
                                            boundObjectParam,
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
                                                boundObjectParam,
                                                viewAttribute))
                                .endControlFlow()
                    }

                    if (!attributeGroup.isRequired) {
                        val defaultAttribute = attributeGroup.defaultAttribute as ViewAttributeInfo

                        methodBuilder.beginControlFlow(
                                "else")
                                .addStatement(
                                        "\$L.\$L(\$L)",
                                        boundObjectParam.name,
                                        defaultAttribute.viewSetterMethodName,
                                        defaultAttribute.codeToSetDefault.value())
                                .endControlFlow()
                    }
                }
            }

            addBindStyleCodeIfNeeded(modelInfo,
                                     methodBuilder,
                                     boundObjectParam,
                                     hasPreviousModel = false)

            return true
        }

        internal override fun addToBindWithDiffMethod(
                methodBuilder: MethodSpec.Builder,
                boundObjectParam: ParameterSpec,
                previousModelParam: ParameterSpec
        ): Boolean {

            val generatedModelClass = modelInfo.generatedClassName
            methodBuilder
                    .beginControlFlow(
                            "if (!(\$L instanceof \$T))",
                            previousModelParam.name,
                            generatedModelClass)
                    .addStatement("bind(\$L)",
                                  boundObjectParam.name)
                    .addStatement("return")
                    .endControlFlow()
                    .addStatement(
                            "\$T that = (\$T) previousModel",
                            generatedModelClass,
                            generatedModelClass)

            // We want to make sure the base model has its bind method called as well. Since the
            // user can provide a custom base class we aren't sure if it implements diff binding.
            // If so we should call it, but if not, calling it would invoke the default
            // EpoxyModel implementation which calls normal "bind". Doing that would force a full
            // bind!!! So we mustn't do that. So, we only call the super diff binding if we think
            // it's a custom implementation.
            if (modelImplementsBindWithDiff(
                    modelInfo.superClassElement,
                    methodBuilder.build())) {
                methodBuilder.addStatement(
                        "super.bind(\$L, \$L)",
                        boundObjectParam.name,
                        previousModelParam.name)
            } else {
                methodBuilder.addStatement(
                        "super.bind(\$L)",
                        boundObjectParam.name)
            }

            for (attributeGroup in modelInfo.attributeGroups) {
                methodBuilder.addCode("\n")
                if (attributeGroup.attributes.size == 1) {
                    val attributeInfo = attributeGroup.attributes[0]

                    if (attributeInfo is ViewAttributeInfo && attributeInfo.generateStringOverloads) {
                        methodBuilder
                                .beginControlFlow(
                                        "if (!\$L.equals(that.\$L))",
                                        attributeInfo.getterCode(),
                                        attributeInfo.getterCode())
                    } else {
                        GeneratedModelWriter.startNotEqualsControlFlow(
                                methodBuilder,
                                attributeInfo)
                    }

                    methodBuilder.addCode(
                            buildCodeBlockToSetAttribute(
                                    boundObjectParam,
                                    attributeInfo as ViewAttributeInfo))
                            .endControlFlow()
                } else {
                    methodBuilder.beginControlFlow(
                            "if (\$L.equals(that.\$L))",
                            GeneratedModelWriter.ATTRIBUTES_BITSET_FIELD_NAME,
                            GeneratedModelWriter.ATTRIBUTES_BITSET_FIELD_NAME)

                    var firstAttribute = true
                    for (attribute in attributeGroup.attributes) {
                        if (!firstAttribute) {
                            methodBuilder.addCode(
                                    " else ")
                        }
                        firstAttribute = false

                        methodBuilder.beginControlFlow(
                                "if (\$L)",
                                GeneratedModelWriter.isAttributeSetCode(
                                        modelInfo,
                                        attribute))

                        GeneratedModelWriter.startNotEqualsControlFlow(
                                methodBuilder,
                                attribute)
                                .addCode(
                                        buildCodeBlockToSetAttribute(
                                                boundObjectParam,
                                                attribute as ViewAttributeInfo))
                                .endControlFlow()
                                .endControlFlow()
                    }

                    methodBuilder.endControlFlow()
                            .beginControlFlow("else")

                    firstAttribute = true
                    for (attribute in attributeGroup.attributes) {
                        if (!firstAttribute) {
                            methodBuilder.addCode(
                                    " else ")
                        }
                        firstAttribute = false

                        methodBuilder.beginControlFlow(
                                "if (\$L && !that.\$L)",
                                GeneratedModelWriter.isAttributeSetCode(
                                        modelInfo,
                                        attribute),
                                GeneratedModelWriter.isAttributeSetCode(
                                        modelInfo,
                                        attribute))
                                .addCode(
                                        buildCodeBlockToSetAttribute(
                                                boundObjectParam,
                                                attribute as ViewAttributeInfo))
                                .endControlFlow()
                    }

                    if (!attributeGroup.isRequired) {
                        val defaultAttribute = attributeGroup.defaultAttribute as ViewAttributeInfo

                        methodBuilder.beginControlFlow(
                                "else")
                                .addStatement(
                                        "\$L.\$L(\$L)",
                                        boundObjectParam.name,
                                        defaultAttribute.viewSetterMethodName,
                                        defaultAttribute.codeToSetDefault.value())
                                .endControlFlow()
                    }

                    methodBuilder.endControlFlow()
                }
            }

            addBindStyleCodeIfNeeded(modelInfo,
                                     methodBuilder,
                                     boundObjectParam,
                                     hasPreviousModel = true)

            return true
        }

        override fun addToHandlePostBindMethod(
                postBindBuilder: MethodSpec.Builder,
                boundObjectParam: ParameterSpec
        ) {

            addAfterPropsAddedMethodsToBuilder(
                    postBindBuilder, modelInfo,
                    boundObjectParam)
        }

        internal override fun addToUnbindMethod(
                unbindBuilder: MethodSpec.Builder,
                unbindParamName: String
        ) {
            modelInfo.viewAttributes
                    .filter { it.resetWithNull }
                    .forEach {
                        unbindBuilder.addStatement(
                                "\$L.\$L(null)",
                                unbindParamName,
                                it.viewSetterMethodName)
                    }

            addResetMethodsToBuilder(unbindBuilder,
                                     modelInfo,
                                     unbindParamName)
        }

        internal override fun beforeFinalBuild(builder: TypeSpec.Builder) {
            if (modelInfo.saveViewState) {
                builder.addMethod(
                        buildSaveStateMethod())
            }

            if (modelInfo.fullSpanSize) {
                builder.addMethod(
                        buildFullSpanSizeMethod())
            }
        }
    }

    private fun addBindStyleCodeIfNeeded(
            modelInfo: ModelViewInfo,
            methodBuilder: MethodSpec.Builder,
            boundObjectParam: ParameterSpec,
            hasPreviousModel: Boolean
    ) {
        val styleInfo = modelInfo.styleBuilderInfo ?: return

        methodBuilder.apply {
            // Compare against the style on the previous model if it exists,
            // otherwise we look up the saved style from the view tag
            if (hasPreviousModel) {
                beginControlFlow("\nif (!\$L.equals(that.\$L))",
                                 PARIS_STYLE_ATTR_NAME, PARIS_STYLE_ATTR_NAME)
            } else {
                beginControlFlow("\nif (!\$L.equals(\$L.getTag(\$T.id.epoxy_saved_view_style)))",
                                 PARIS_STYLE_ATTR_NAME, boundObjectParam.name, ClassNames.EPOXY_R)
            }

            addStyleApplierCode(this, styleInfo, boundObjectParam.name)

            endControlFlow()
        }

    }

    fun modelImplementsBindWithDiff(
            clazz: TypeElement,
            bindWithDiffMethod: MethodSpec
    ): Boolean {
        val methodOnClass = Utils.getMethodOnClass(clazz, bindWithDiffMethod, types,
                                                   elements) ?: return false

        val modifiers = methodOnClass.modifiers
        if (modifiers.contains(Modifier.ABSTRACT)) {
            return false
        }

        val enclosingElement = methodOnClass.enclosingElement as TypeElement

        // As long as the implementation is not on the base EpoxyModel we consider it a custom
        // implementation
        return enclosingElement.qualifiedName.toString() != Utils.UNTYPED_EPOXY_MODEL_TYPE
    }

    private fun buildCodeBlockToSetAttribute(
            boundObjectParam: ParameterSpec,
            viewAttribute: ViewAttributeInfo
    ): CodeBlock {
        return CodeBlock.of("\$L.\$L(\$L);\n", boundObjectParam.name,
                            viewAttribute.viewSetterMethodName,
                            getValueToSetOnView(viewAttribute, boundObjectParam))
    }

    private fun getValueToSetOnView(
            viewAttribute: ViewAttributeInfo,
            boundObjectParam: ParameterSpec
    ): String {
        val fieldName = viewAttribute.getFieldName()

        return if (viewAttribute.generateStringOverloads) {
            fieldName + ".toString(" + boundObjectParam.name + ".getContext())"
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
        for (methodName in modelViewInfo.getResetMethodNames()) {
            builder.addStatement("$unbindParamName.$methodName()")
        }
    }

    private fun addAfterPropsAddedMethodsToBuilder(
            methodBuilder: MethodSpec.Builder,
            modelInfo: ModelViewInfo,
            boundObjectParam: ParameterSpec
    ) {
        for (methodName in modelInfo.getAfterPropsSetMethodNames()) {
            methodBuilder.addStatement(boundObjectParam.name + "." + methodName + "()")
        }
    }

}

internal fun addStyleApplierCode(
        methodBuilder: MethodSpec.Builder,
        styleInfo: ParisStyleAttributeInfo,
        viewVariableName: String
) {

    methodBuilder.apply {

        addStatement("\$T styleApplier = new \$T(\$L)",
                     styleInfo.styleApplierClass, styleInfo.styleApplierClass, viewVariableName)

        addStatement("styleApplier.apply(\$L)", PARIS_STYLE_ATTR_NAME)

        // By saving the style as a tag we can prevent applying it
        // again when the view is recycled and the same style is used
        addStatement("\$L.setTag(\$T.id.epoxy_saved_view_style, \$L)",
                     viewVariableName, ClassNames.EPOXY_R, PARIS_STYLE_ATTR_NAME)
    }
}