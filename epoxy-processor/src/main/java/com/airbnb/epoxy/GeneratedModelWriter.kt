package com.airbnb.epoxy

import android.support.annotation.LayoutRes
import com.airbnb.epoxy.ClassNames.ANDROID_ASYNC_TASK
import com.airbnb.epoxy.Utils.EPOXY_CONTROLLER_TYPE
import com.airbnb.epoxy.Utils.EPOXY_VIEW_HOLDER_TYPE
import com.airbnb.epoxy.Utils.GENERATED_MODEL_INTERFACE
import com.airbnb.epoxy.Utils.MODEL_CLICK_LISTENER_TYPE
import com.airbnb.epoxy.Utils.MODEL_LONG_CLICK_LISTENER_TYPE
import com.airbnb.epoxy.Utils.ON_BIND_MODEL_LISTENER_TYPE
import com.airbnb.epoxy.Utils.ON_UNBIND_MODEL_LISTENER_TYPE
import com.airbnb.epoxy.Utils.UNTYPED_EPOXY_MODEL_TYPE
import com.airbnb.epoxy.Utils.WRAPPED_LISTENER_TYPE
import com.airbnb.epoxy.Utils.getClassName
import com.airbnb.epoxy.Utils.implementsMethod
import com.airbnb.epoxy.Utils.isDataBindingModel
import com.airbnb.epoxy.Utils.isEpoxyModel
import com.airbnb.epoxy.Utils.isEpoxyModelWithHolder
import com.airbnb.epoxy.Utils.isViewLongClickListenerType
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.MethodSpec.Builder
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeName.BOOLEAN
import com.squareup.javapoet.TypeName.BYTE
import com.squareup.javapoet.TypeName.CHAR
import com.squareup.javapoet.TypeName.DOUBLE
import com.squareup.javapoet.TypeName.FLOAT
import com.squareup.javapoet.TypeName.INT
import com.squareup.javapoet.TypeName.LONG
import com.squareup.javapoet.TypeName.SHORT
import com.squareup.javapoet.TypeSpec
import java.io.IOException
import java.lang.annotation.AnnotationTypeMismatchException
import java.lang.ref.WeakReference
import java.util.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import javax.lang.model.element.Modifier.FINAL
import javax.lang.model.element.Modifier.PRIVATE
import javax.lang.model.element.Modifier.PROTECTED
import javax.lang.model.element.Modifier.PUBLIC
import javax.lang.model.element.Modifier.STATIC
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

internal class GeneratedModelWriter(
        private val filer: Filer,
        private val types: Types,
        private val errorLogger: ErrorLogger,
        private val resourceProcessor: ResourceProcessor,
        private val configManager: ConfigManager,
        private val dataBindingModuleLookup: DataBindingModuleLookup,
        private val elements: Elements
) {

    val modelInterfaceWriter = ModelBuilderInterfaceWriter(filer, types)

    private var builderHooks: BuilderHooks? = null

    internal open class BuilderHooks {
        open fun beforeFinalBuild(builder: TypeSpec.Builder) {}

        /** Opportunity to add additional code to the unbind method.  */
        open fun addToUnbindMethod(
                unbindBuilder: Builder,
                unbindParamName: String
        ) {

        }

        /**
         * True true to have the bind method build, false to not add the method to the generated class.
         */
        open fun addToBindMethod(
                methodBuilder: Builder,
                boundObjectParam: ParameterSpec
        ) {
        }

        /**
         * True true to have the bind method build, false to not add the method to the generated class.
         */
        open fun addToBindWithDiffMethod(
                methodBuilder: Builder,
                boundObjectParam: ParameterSpec,
                previousModelParam: ParameterSpec
        ) {
        }

        open fun addToHandlePostBindMethod(
                postBindBuilder: Builder,
                boundObjectParam: ParameterSpec
        ) {

        }
    }

    fun writeFilesForViewInterfaces() {
        modelInterfaceWriter.writeFilesForViewInterfaces()
    }

    @Throws(IOException::class)
    @JvmOverloads
    fun generateClassForModel(
            info: GeneratedModelInfo,
            builderHooks: BuilderHooks? = null
    ) {
        this.builderHooks = builderHooks
        if (!info.shouldGenerateModel) {
            return
        }

        val generatedModelName = info.generatedName

        val modelClass = buildClass(generatedModelName) {
            addJavadoc("Generated file. Do not modify!")
            addModifiers(PUBLIC)
            superclass(info.getSuperClassName())
            addSuperinterface(getGeneratedModelInterface(info))
            addTypeVariables(info.typeVariables)
            addAnnotations(info.getAnnotations())
            addFields(buildStyleConstant(info))
            addFields(generateFields(info))
            addMethods(generateConstructors(info))

            generateDebugAddToMethodIfNeeded(this, info)

            addMethods(generateProgrammaticViewMethods(info))
            addMethods(generateBindMethods(info))
            addMethods(generateStyleableViewMethods(info))
            addMethods(generateSettersAndGetters(info))
            addMethods(generateMethodsReturningClassType(info))
            addMethods(generateDefaultMethodImplementations(info))
            addMethods(generateOtherLayoutOptions(info))
            addMethods(generateDataBindingMethodsIfNeeded(info))
            addMethod(generateReset(info))
            addMethod(generateEquals(info))
            addMethod(generateHashCode(info))
            addMethod(generateToString(info))

            builderHooks?.beforeFinalBuild(this)


            addSuperinterface(modelInterfaceWriter.writeInterface(info, this.build().methodSpecs))
        }

        JavaFile.builder(generatedModelName.packageName(), modelClass)
                .build()
                .writeTo(filer)
    }

    private fun generateOtherLayoutOptions(info: GeneratedModelInfo): Iterable<MethodSpec> {
        if (!info.includeOtherLayoutOptions || info.isProgrammaticView) { // Layout resources can't be mixed with programmatic views
            return emptyList()
        }

        val result = ArrayList<MethodSpec>()
        val layout = getDefaultLayoutResource(info)
        if (layout?.qualified != true) {
            return emptyList()
        }

        val defaultLayoutNameLength = layout.resourceName!!.length

        for (otherLayout in resourceProcessor.getAlternateLayouts(layout)) {
            if (!otherLayout.qualified) {
                continue
            }

            var layoutDescription = ""
            for (namePart in otherLayout.resourceName!!.substring(defaultLayoutNameLength).split(
                    "_".toRegex()).dropLastWhile { it.isEmpty() }) {
                layoutDescription += Utils.capitalizeFirstLetter(namePart)
            }

            result.add(buildMethod("with" + layoutDescription + "Layout") {
                returns(info.parameterizedGeneratedName)
                addModifiers(PUBLIC)
                addStatement("layout(\$L)", otherLayout.code)
                addStatement("return this")
            })

        }

        return result
    }

    private fun getGeneratedModelInterface(info: GeneratedModelInfo): ParameterizedTypeName {
        return ParameterizedTypeName.get(
                getClassName(GENERATED_MODEL_INTERFACE),
                info.modelType
        )
    }

    private fun buildStyleConstant(info: GeneratedModelInfo): Iterable<FieldSpec> {

        val styleBuilderInfo = info.styleBuilderInfo ?: return emptyList()

        val constantFields = ArrayList<FieldSpec>()

        // If this is a styleable view we add a constant to store the default style builder.
        // This is an optimization to avoid recreating the default style many times, since it is likely
        // often needed at runtime.
        constantFields.add(
                buildField(ClassNames.PARIS_STYLE, PARIS_DEFAULT_STYLE_CONSTANT_NAME) {
                    addModifiers(FINAL, PRIVATE, STATIC)
                    initializer("new \$T().addDefault().build()",
                                styleBuilderInfo.styleBuilderClass)
                })

        // We store styles in a weak reference since if a controller uses it
        // once it is likely to be used in other models and when models are rebuilt
        for ((name) in styleBuilderInfo.styles) {
            constantFields.add(
                    FieldSpec.builder(
                            ParameterizedTypeName.get(ClassName.get(WeakReference::class.java),
                                                      ClassNames.PARIS_STYLE),
                            weakReferenceFieldForStyle(name),
                            PRIVATE, STATIC
                    )
                            .build())
        }

        return constantFields
    }

    private fun generateFields(classInfo: GeneratedModelInfo): Iterable<FieldSpec> {
        val fields = ArrayList<FieldSpec>()

        // bit set for tracking what attributes were set
        if (shouldUseBitSet(classInfo)) {
            fields.add(
                    buildField(BitSet::class.className(), ATTRIBUTES_BITSET_FIELD_NAME) {
                        addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        initializer("new \$T(\$L)", BitSet::class.java,
                                    classInfo.attributeInfo.size)
                    })
        }

        // Add fields for the bind/unbind listeners
        val onBindListenerType = ParameterizedTypeName.get(
                getClassName(ON_BIND_MODEL_LISTENER_TYPE),
                classInfo.parameterizedGeneratedName,
                classInfo.modelType
        )
        fields.add(FieldSpec.builder(onBindListenerType, modelBindListenerFieldName(),
                                     PRIVATE).build())

        val onUnbindListenerType = ParameterizedTypeName.get(
                getClassName(ON_UNBIND_MODEL_LISTENER_TYPE),
                classInfo.parameterizedGeneratedName,
                classInfo.modelType
        )
        fields.add(FieldSpec.builder(onUnbindListenerType, modelUnbindListenerFieldName(),
                                     PRIVATE).build())

        classInfo.getAttributeInfo()
                .filter { it.isGenerated }
                .mapTo(fields) {
                    buildField(it.typeName, it.fieldName) {
                        addModifiers(PRIVATE)
                        addAnnotations(it.getSetterAnnotations())

                        if (shouldUseBitSet(classInfo)) {
                            addJavadoc("Bitset index: \$L", attributeIndex(classInfo, it))
                        }

                        if (it.codeToSetDefault.isPresent) {
                            initializer(it.codeToSetDefault.value())
                        }
                    }
                }

        return fields
    }

    private fun modelUnbindListenerFieldName(): String =
            "onModelUnboundListener" + GENERATED_FIELD_SUFFIX

    private fun modelBindListenerFieldName(): String =
            "onModelBoundListener" + GENERATED_FIELD_SUFFIX

    private fun getModelClickListenerType(classInfo: GeneratedModelInfo): ParameterizedTypeName {
        return ParameterizedTypeName.get(
                getClassName(MODEL_CLICK_LISTENER_TYPE),
                classInfo.parameterizedGeneratedName,
                classInfo.modelType)
    }

    private fun getModelLongClickListenerType(classInfo: GeneratedModelInfo): ParameterizedTypeName {
        return ParameterizedTypeName.get(
                getClassName(MODEL_LONG_CLICK_LISTENER_TYPE),
                classInfo.parameterizedGeneratedName,
                classInfo.modelType)
    }

    /** Include any constructors that are in the super class.  */
    private fun generateConstructors(info: GeneratedModelInfo): Iterable<MethodSpec> {
        return info.constructors.map {
            buildConstructor {
                addModifiers(it.modifiers)
                addParameters(it.params)
                varargs(it.varargs)

                val statementBuilder = StringBuilder("super(")
                generateParams(statementBuilder, it.params)
                addStatement(statementBuilder.toString())
            }
        }
    }

    private fun generateDebugAddToMethodIfNeeded(
            classBuilder: TypeSpec.Builder,
            info: GeneratedModelInfo
    ) {
        if (!configManager.shouldValidateModelUsage()) {
            return
        }

        classBuilder.addMethod("addTo") {
            addParameter(getClassName(EPOXY_CONTROLLER_TYPE), "controller")
            addAnnotation(Override::class.java)
            addModifiers(PUBLIC)
            addStatement("super.addTo(controller)")
            addStatement("addWithDebugValidation(controller)")

            // If no group default exists, and no attribute in group is set, throw an exception
            info.attributeGroups
                    .filter { it.isRequired }
                    .forEach { attributeGroup ->

                        addCode("if (")
                        attributeGroup.attributes.forEachIndexed { index, attribute ->
                            if (index != 0) {
                                addCode(" && ")
                            }

                            addCode("!\$L", isAttributeSetCode(info, attribute))
                        }

                        addCode(") {\n")
                        addStatement("\tthrow new \$T(\"A value is required for \$L\")",
                                     IllegalStateException::class.java,
                                     attributeGroup.name)
                        addCode("}\n")
                    }

        }
    }

    private fun generateProgrammaticViewMethods(modelInfo: GeneratedModelInfo): Iterable<MethodSpec> {

        if (!modelInfo.isProgrammaticView) {
            return emptyList()
        }

        val methods = ArrayList<MethodSpec>()

        // getViewType method so that view type is generated at runtime
        methods.add(buildMethod("getViewType") {
            addAnnotation(Override::class.java)
            addModifiers(PROTECTED)
            returns(TypeName.INT)
            addStatement("return 0", modelInfo.boundObjectTypeName)
        })

        // buildView method to return new view instance
        methods.add(buildMethod("buildView") {
            addAnnotation(Override::class.java)
            addParameter(ClassNames.ANDROID_VIEW_GROUP, "parent")
            addModifiers(PROTECTED)
            returns(modelInfo.boundObjectTypeName)
            addStatement("\$T v = new \$T(parent.getContext())", modelInfo.boundObjectTypeName,
                         modelInfo.boundObjectTypeName)

            val (layoutWidth, layoutHeight) = getLayoutDimensions(modelInfo)

            addStatement("v.setLayoutParams(new \$T(\$L, \$L))",
                         ClassNames.ANDROID_MARGIN_LAYOUT_PARAMS, layoutWidth, layoutHeight)

            addStatement("return v")
        })

        return methods
    }

    private fun getLayoutDimensions(modelInfo: GeneratedModelInfo): Pair<CodeBlock, CodeBlock> {
        val matchParent = CodeBlock.of("\$T.MATCH_PARENT", ClassNames.ANDROID_MARGIN_LAYOUT_PARAMS)
        val wrapContent = CodeBlock.of("\$T.WRAP_CONTENT", ClassNames.ANDROID_MARGIN_LAYOUT_PARAMS)

        // Returns a pair of width to height
        return when (modelInfo.layoutParams) {
            ModelView.Size.WRAP_WIDTH_MATCH_HEIGHT -> wrapContent to matchParent
            ModelView.Size.MATCH_WIDTH_MATCH_HEIGHT -> matchParent to matchParent
        // This will be used for Styleable views as the default
            ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT -> matchParent to wrapContent
            ModelView.Size.WRAP_WIDTH_WRAP_HEIGHT -> wrapContent to wrapContent
            else -> wrapContent to wrapContent
        }
    }

    private fun generateBindMethods(modelInfo: GeneratedModelInfo): Iterable<MethodSpec> {
        val methods = ArrayList<MethodSpec>()

        // Add bind/unbind methods so the class can set the epoxyModelBoundObject and
        // boundEpoxyViewHolder fields for the model click listener to access

        val viewHolderType = getClassName(EPOXY_VIEW_HOLDER_TYPE)
        val viewHolderParam = ParameterSpec.builder(viewHolderType, "holder", FINAL).build()

        val boundObjectParam = ParameterSpec.builder(modelInfo.modelType, "object", FINAL).build()

        methods.add(buildPreBindMethod(modelInfo, viewHolderParam, boundObjectParam))

        // If the view is styleable then we need to override bind to apply the style
        // If builderhooks is nonnull we assume that it is adding code to the bind methods
        if (builderHooks != null || modelInfo.isStyleable) {
            methods.add(buildBindMethod(boundObjectParam, modelInfo))
            methods.add(buildBindWithDiffMethod(modelInfo, boundObjectParam))
        }

        val postBindBuilder = MethodSpec.methodBuilder("handlePostBind")
                .addModifiers(PUBLIC)
                .addAnnotation(Override::class.java)
                .addParameter(boundObjectParam)
                .addParameter(TypeName.INT, "position")
                .beginControlFlow("if (\$L != null)", modelBindListenerFieldName())
                .addStatement("\$L.onModelBound(this, object, position)",
                              modelBindListenerFieldName())
                .endControlFlow()

        addHashCodeValidationIfNecessary(postBindBuilder,
                                         "The model was changed during the bind call.")

        builderHooks?.addToHandlePostBindMethod(postBindBuilder, boundObjectParam)

        methods.add(postBindBuilder
                            .build())

        val onBindListenerType = ParameterizedTypeName.get(
                getClassName(ON_BIND_MODEL_LISTENER_TYPE),
                modelInfo.parameterizedGeneratedName,
                modelInfo.modelType
        )
        val bindListenerParam = ParameterSpec.builder(onBindListenerType, "listener").build()

        val onBind = MethodSpec.methodBuilder("onBind")
                .addJavadoc(
                        "Register a listener that will be called when this model is bound to a view.\n"
                                + "<p>\n"
                                + "The listener will contribute to this model's hashCode state per the {@link\n"
                                + "com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.\n"
                                + "<p>\n"
                                + "You may clear the listener by setting a null value, or by calling "
                                + "{@link #reset()}")
                .addModifiers(PUBLIC)
                .returns(modelInfo.parameterizedGeneratedName)
                .addParameter(bindListenerParam)

        addOnMutationCall(onBind)
                .addStatement("this.\$L = listener", modelBindListenerFieldName())
                .addStatement("return this")
                .build()

        methods.add(onBind.build())

        val unbindParamName = "object"
        val unbindObjectParam = ParameterSpec.builder(modelInfo.modelType, unbindParamName).build()

        val unbindBuilder = MethodSpec.methodBuilder("unbind")
                .addAnnotation(Override::class.java)
                .addModifiers(PUBLIC)
                .addParameter(unbindObjectParam)

        unbindBuilder
                .addStatement("super.unbind(object)")
                .beginControlFlow("if (\$L != null)", modelUnbindListenerFieldName())
                .addStatement("\$L.onModelUnbound(this, object)", modelUnbindListenerFieldName())
                .endControlFlow()

        builderHooks?.addToUnbindMethod(unbindBuilder, unbindParamName)

        methods.add(unbindBuilder
                            .build())

        val onUnbindListenerType = ParameterizedTypeName.get(
                getClassName(ON_UNBIND_MODEL_LISTENER_TYPE),
                modelInfo.parameterizedGeneratedName,
                modelInfo.modelType
        )
        val unbindListenerParam = ParameterSpec.builder(onUnbindListenerType, "listener").build()

        val onUnbind = MethodSpec.methodBuilder("onUnbind")
                .addJavadoc(
                        "Register a listener that will be called when this model is unbound from a "
                                + "view.\n"
                                + "<p>\n"
                                + "The listener will contribute to this model's hashCode state per the {@link\n"
                                + "com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.\n"
                                + "<p>\n"
                                + "You may clear the listener by setting a null value, or by calling "
                                + "{@link #reset()}")
                .addModifiers(PUBLIC)
                .returns(modelInfo.parameterizedGeneratedName)

        addOnMutationCall(onUnbind)
                .addParameter(unbindListenerParam)
                .addStatement("this.\$L = listener", modelUnbindListenerFieldName())
                .addStatement("return this")

        methods.add(onUnbind.build())

        return methods
    }

    private fun buildBindMethod(
            boundObjectParam: ParameterSpec,
            modelInfo: GeneratedModelInfo
    ) = buildMethod("bind") {
        addAnnotation(Override::class.java)
        addModifiers(PUBLIC)
        addParameter(boundObjectParam)

        // The style is applied before calling super or binding properties so that the model can
        // override style settings
        addBindStyleCodeIfNeeded(modelInfo, this, boundObjectParam, false)

        addStatement("super.bind(\$L)", boundObjectParam.name)

        builderHooks?.addToBindMethod(this, boundObjectParam)
    }

    private fun buildBindWithDiffMethod(
            classInfo: GeneratedModelInfo,
            boundObjectParam: ParameterSpec
    ) = buildMethod("bind") {

        val previousModelParam = ParameterSpec.builder(getClassName(UNTYPED_EPOXY_MODEL_TYPE),
                                                       "previousModel").build()

        addAnnotation(Override::class.java)
        addModifiers(PUBLIC)
        addParameter(boundObjectParam)
        addParameter(previousModelParam)

        val generatedModelClass = classInfo.generatedClassName
        beginControlFlow(
                "if (!(\$L instanceof \$T))",
                previousModelParam.name,
                generatedModelClass)
        addStatement("bind(\$L)", boundObjectParam.name)
        addStatement("return")
        endControlFlow()
        addStatement(
                "\$T that = (\$T) previousModel",
                generatedModelClass,
                generatedModelClass)

        addBindStyleCodeIfNeeded(classInfo, this, boundObjectParam, true)

        // We want to make sure the base model has its bind method called as well. Since the
        // user can provide a custom base class we aren't sure if it implements diff binding.
        // If so we should call it, but if not, calling it would invoke the default
        // EpoxyModel implementation which calls normal "bind". Doing that would force a full
        // bind!!! So we mustn't do that. So, we only call the super diff binding if we think
        // it's a custom implementation.
        if (modelImplementsBindWithDiff(classInfo.superClassElement, build(), types, elements)) {
            addStatement(
                    "super.bind(\$L, \$L)",
                    boundObjectParam.name,
                    previousModelParam.name)
        } else {
            addStatement("super.bind(\$L)", boundObjectParam.name)
        }

        builderHooks?.addToBindWithDiffMethod(this, boundObjectParam, previousModelParam)
    }

    private fun buildPreBindMethod(
            modelInfo: GeneratedModelInfo,
            viewHolderParam: ParameterSpec,
            boundObjectParam: ParameterSpec
    ): MethodSpec {

        val positionParamName = "position"
        val preBindBuilder = MethodSpec.methodBuilder("handlePreBind")
                .addModifiers(PUBLIC)
                .addAnnotation(Override::class.java)
                .addParameter(viewHolderParam)
                .addParameter(boundObjectParam)
                .addParameter(TypeName.INT, positionParamName, Modifier.FINAL)

        addHashCodeValidationIfNecessary(preBindBuilder,
                                         "The model was changed between being added to the controller and being bound.")

        if (modelInfo.isStyleable && configManager.shouldValidateModelUsage()) {

            // We validate that the style attributes are the same as in the default, otherwise
            // recycling will not work correctly. It is done in the background since it is fairly slow
            // and can noticeably add jank to scrolling in dev
            preBindBuilder
                    .beginControlFlow(
                            "if (!\$T.equals(\$L, \$L.getTag(\$T.id.epoxy_saved_view_style)))",
                            Objects::class.java, PARIS_STYLE_ATTR_NAME, boundObjectParam.name,
                            ClassNames.EPOXY_R)
                    .beginControlFlow("\$T.THREAD_POOL_EXECUTOR.execute(new \$T()",
                                      ANDROID_ASYNC_TASK,
                                      Runnable::class.java)
                    .beginControlFlow("public void run()")
                    .beginControlFlow("try")
                    .addStatement("\$T.assertSameAttributes(new \$T(\$L), \$L, \$L)",
                                  ClassNames.PARIS_STYLE_UTILS,
                                  modelInfo.styleBuilderInfo!!.styleApplierClass,
                                  boundObjectParam.name, PARIS_STYLE_ATTR_NAME,
                                  PARIS_DEFAULT_STYLE_CONSTANT_NAME)
                    .endControlFlow()
                    .beginControlFlow("catch(\$T e)", AssertionError::class.java)
                    .addStatement(
                            "throw new \$T(\"\$L model at position \" + \$L + \" has an invalid style:\\n\\n\" + e" + ".getMessage())",
                            IllegalStateException::class.java,
                            modelInfo.generatedClassName.simpleName(),
                            positionParamName)
                    .endControlFlow()
                    .endControlFlow()
                    .endControlFlow(")")
                    .endControlFlow()
        }

        return preBindBuilder.build()
    }

    private fun generateStyleableViewMethods(modelInfo: GeneratedModelInfo): Iterable<MethodSpec> {
        val styleBuilderInfo = modelInfo.styleBuilderInfo ?: return emptyList()

        val methods = ArrayList<MethodSpec>()
        val styleType = styleBuilderInfo.typeName
        val styleBuilderClass = styleBuilderInfo.styleBuilderClass

        // setter for style object
        val builder = MethodSpec.methodBuilder(PARIS_STYLE_ATTR_NAME)
                .addModifiers(PUBLIC)
                .returns(modelInfo.parameterizedGeneratedName)
                .addParameter(styleType, PARIS_STYLE_ATTR_NAME)

        setBitSetIfNeeded(modelInfo, styleBuilderInfo, builder)
        addOnMutationCall(builder)
                .addStatement(styleBuilderInfo.setterCode(), PARIS_STYLE_ATTR_NAME)

        methods.add(builder
                            .addStatement("return this")
                            .build())

        // Lambda for building the style
        val parameterizedBuilderCallbackType = ParameterizedTypeName.get(
                ClassNames.EPOXY_STYLE_BUILDER_CALLBACK, styleBuilderClass)

        methods.add(MethodSpec.methodBuilder("styleBuilder")
                            .addModifiers(PUBLIC)
                            .returns(modelInfo.parameterizedGeneratedName)
                            .addParameter(parameterizedBuilderCallbackType, "builderCallback")
                            .addStatement("\$T builder = new \$T()", styleBuilderClass,
                                          styleBuilderClass)
                            .addStatement("builderCallback.buildStyle(builder.addDefault())")
                            .addStatement("return \$L(builder.build())", PARIS_STYLE_ATTR_NAME)
                            .build())

        // Methods for setting each defined style directly
        for ((name, javadoc) in styleBuilderInfo.styles) {
            val capitalizedStyle = Utils.capitalizeFirstLetter(name)
            val methodName = "with" + capitalizedStyle + "Style"
            val fieldName = weakReferenceFieldForStyle(name)

            // The style is stored in a static weak reference since it is likely to be reused in other
            // models are when models are rebuilt.
            val styleMethodBuilder = MethodSpec.methodBuilder(methodName)

            if (javadoc != null) {
                styleMethodBuilder.addJavadoc(javadoc)
            }

            methods.add(styleMethodBuilder
                                .addModifiers(PUBLIC)
                                .returns(modelInfo.parameterizedGeneratedName)
                                .addStatement("\$T style = \$L != null ? \$L.get() : null",
                                              ClassNames.PARIS_STYLE,
                                              fieldName, fieldName)
                                .beginControlFlow("if (style == null)")
                                .addStatement("style =  new \$T().add\$L().build()",
                                              styleBuilderClass, capitalizedStyle)
                                .addStatement("\$L = new \$T<>(style)", fieldName,
                                              WeakReference::class.java)
                                .endControlFlow()
                                .addStatement("return \$L(style)", PARIS_STYLE_ATTR_NAME)
                                .build())
        }

        return methods
    }

    private fun generateMethodsReturningClassType(info: GeneratedModelInfo): Iterable<MethodSpec> {
        val methods = ArrayList<MethodSpec>(info.getMethodsReturningClassType().size)

        for (methodInfo in info.getMethodsReturningClassType()) {
            val builder = MethodSpec.methodBuilder(methodInfo.name)
                    .addModifiers(methodInfo.modifiers)
                    .addParameters(methodInfo.params)
                    .addAnnotation(Override::class.java)
                    .varargs(methodInfo.varargs)
                    .returns(info.parameterizedGeneratedName)

            if (info.isProgrammaticView
                    && "layout" == methodInfo.name
                    && methodInfo.params.size == 1
                    && methodInfo.params[0].type === TypeName.INT) {

                builder
                        .addStatement(
                                "throw new \$T(\"Layout resources are unsupported with programmatic views.\")",
                                UnsupportedOperationException::class.java)
            } else {

                val statementBuilder = StringBuilder(String.format("super.%s(",
                                                                   methodInfo.name))
                generateParams(statementBuilder, methodInfo.params)

                builder
                        .addStatement(statementBuilder.toString())
                        .addStatement("return this")
            }

            methods.add(builder.build())
        }

        return methods
    }

    /**
     * Generates default implementations of certain model methods if the model is abstract and doesn't
     * implement them.
     */
    private fun generateDefaultMethodImplementations(info: GeneratedModelInfo): Iterable<MethodSpec> {
        val methods = ArrayList<MethodSpec>()

        if (info.isProgrammaticView) {
            methods.add(buildDefaultLayoutMethodBase()
                                .toBuilder()
                                .addStatement(
                                        "throw new \$T(\"Layout resources are unsupported for views created programmatically" + ".\")",
                                        UnsupportedOperationException::class.java)
                                .build())
        } else {
            addCreateHolderMethodIfNeeded(info, methods)
            addDefaultLayoutMethodIfNeeded(info, methods)
        }

        return methods
    }

    /**
     * If the model is a holder and doesn't implement the "createNewHolder" method we can generate a
     * default implementation by getting the class type and creating a new instance of it.
     */
    private fun addCreateHolderMethodIfNeeded(
            modelClassInfo: GeneratedModelInfo,
            methods: MutableList<MethodSpec>
    ) {

        val originalClassElement = modelClassInfo.superClassElement
        if (!isEpoxyModelWithHolder(originalClassElement)) {
            return
        }

        var createHolderMethod = MethodSpec.methodBuilder(
                CREATE_NEW_HOLDER_METHOD_NAME)
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PROTECTED)
                .build()

        if (implementsMethod(originalClassElement, createHolderMethod, types, elements)) {
            return
        }

        createHolderMethod = createHolderMethod.toBuilder()
                .returns(modelClassInfo.modelType)
                .addStatement("return new \$T()", modelClassInfo.modelType)
                .build()

        methods.add(createHolderMethod)
    }

    /**
     * If there is no existing implementation of getDefaultLayout we can generate an implementation.
     * This relies on a layout res being set in the @EpoxyModelClass annotation.
     */
    private fun addDefaultLayoutMethodIfNeeded(
            modelInfo: GeneratedModelInfo,
            methods: MutableList<MethodSpec>
    ) {

        val layout = getDefaultLayoutResource(modelInfo) ?: return

        methods.add(buildDefaultLayoutMethodBase()
                            .toBuilder()
                            .addStatement("return \$L", layout.code)
                            .build())
    }

    private fun buildDefaultLayoutMethodBase(): MethodSpec {
        return MethodSpec.methodBuilder(GET_DEFAULT_LAYOUT_METHOD_NAME)
                .addAnnotation(Override::class.java)
                .addAnnotation(LayoutRes::class.java)
                .addModifiers(Modifier.PROTECTED)
                .returns(TypeName.INT)
                .build()
    }

    private fun getDefaultLayoutResource(modelInfo: GeneratedModelInfo): ResourceValue? {
        // TODO: This is pretty ugly and could be abstracted/decomposed better. We could probably
        // make a small class to contain this logic, or build it into the model info classes

        if (modelInfo is DataBindingModelInfo) {
            return modelInfo.layoutResource
        }

        if (modelInfo is ModelViewInfo) {
            return modelInfo.getLayoutResource(resourceProcessor)
        }

        val superClassElement = modelInfo.superClassElement
        if (implementsMethod(superClassElement, buildDefaultLayoutMethodBase(), types, elements)) {
            return null
        }

        val modelClassWithAnnotation = findSuperClassWithClassAnnotation(superClassElement)
        if (modelClassWithAnnotation == null) {
            errorLogger
                    .logError(
                            "Model must use %s annotation if it does not implement %s. (class: %s)",
                            EpoxyModelClass::class.java,
                            GET_DEFAULT_LAYOUT_METHOD_NAME,
                            modelInfo.getSuperClassName())
            return null
        }

        return resourceProcessor
                .getLayoutInAnnotation(modelClassWithAnnotation, EpoxyModelClass::class.java)
    }

    /**
     * Add `setDataBindingVariables` for DataBinding models if they haven't implemented it. This adds
     * the basic method and a method that checks for payload changes and only sets the variables that
     * changed.
     */
    private fun generateDataBindingMethodsIfNeeded(info: GeneratedModelInfo): Iterable<MethodSpec> {
        if (!isDataBindingModel(info.superClassElement)) {
            return emptyList()
        }

        val bindVariablesMethod = MethodSpec.methodBuilder("setDataBindingVariables")
                .addAnnotation(Override::class.java)
                .addParameter(ClassName.get("android.databinding", "ViewDataBinding"), "binding")
                .addModifiers(Modifier.PROTECTED)
                .returns(TypeName.VOID)
                .build()

        // If the base method is already implemented don't bother checking for the payload method
        if (implementsMethod(info.superClassElement, bindVariablesMethod, types,
                             elements)) {
            return emptyList()
        }

        val generatedModelClass = info.generatedName

        val moduleName = (info as? DataBindingModelInfo)?.moduleName
                ?: dataBindingModuleLookup.getModuleName(info.superClassElement)

        val baseMethodBuilder = bindVariablesMethod.toBuilder()

        val payloadMethodBuilder = bindVariablesMethod
                .toBuilder()
                .addParameter(getClassName(UNTYPED_EPOXY_MODEL_TYPE), "previousModel")
                .beginControlFlow("if (!(previousModel instanceof \$T))", generatedModelClass)
                .addStatement("setDataBindingVariables(binding)")
                .addStatement("return")
                .endControlFlow()
                .addStatement("\$T that = (\$T) previousModel", generatedModelClass,
                              generatedModelClass)

        val brClass = ClassName.get(moduleName, "BR")
        val validateAttributes = configManager.shouldValidateModelUsage()
        for (attribute in info.getAttributeInfo()) {
            val attrName = attribute.getFieldName()
            val setVariableBlock = CodeBlock.of("binding.setVariable(\$T.\$L, \$L)", brClass,
                                                attrName, attribute.getterCode())

            if (validateAttributes) {
                // The setVariable method returns false if the variable id was not found in the layout.
                // We can warn the user about this if they have model validations turned on, otherwise
                // it fails silently.
                baseMethodBuilder
                        .beginControlFlow("if (!\$L)", setVariableBlock)
                        .addStatement(
                                "throw new \$T(\"The attribute \$L was defined in your data binding model (\$L) but " + "a data variable of that name was not found in the layout.\")",
                                IllegalStateException::class.java, attrName,
                                info.getSuperClassName())
                        .endControlFlow()
            } else {
                baseMethodBuilder.addStatement("\$L", setVariableBlock)
            }

            // Handle binding variables only if they changed
            startNotEqualsControlFlow(payloadMethodBuilder, attribute)
                    .addStatement("\$L", setVariableBlock)
                    .endControlFlow()
        }

        val methods = ArrayList<MethodSpec>()
        methods.add(baseMethodBuilder.build())
        methods.add(payloadMethodBuilder.build())
        return methods
    }

    /**
     * Looks for [EpoxyModelClass] annotation in the original class and his parents.
     */
    private fun findSuperClassWithClassAnnotation(classElement: TypeElement): TypeElement? {
        if (!isEpoxyModel(classElement)) {
            return null
        }

        val annotation = classElement.getAnnotation(
                EpoxyModelClass::class.java)
                ?: // This is an error. The model must have an EpoxyModelClass annotation
                // since getDefaultLayout is not implemented
                return null

        val layoutRes: Int
        try {
            layoutRes = annotation.layout
        } catch (e: AnnotationTypeMismatchException) {
            errorLogger.logError("Invalid layout value in %s annotation. (class: %s). %s: %s",
                                 EpoxyModelClass::class.java,
                                 classElement.simpleName,
                                 e.javaClass.simpleName,
                                 e.message ?: "")
            return null
        }

        if (layoutRes != 0) {
            return classElement
        }

        // This model did not specify a layout in its EpoxyModelClass annotation,
        // but its superclass might
        val superClass = types.asElement(classElement.superclass) as TypeElement
        val superClassWithAnnotation = findSuperClassWithClassAnnotation(superClass)

        if (superClassWithAnnotation != null) {
            return superClassWithAnnotation
        }

        errorLogger
                .logError(
                        "Model must specify a valid layout resource in the %s annotation. (class: %s)",
                        EpoxyModelClass::class.java.simpleName,
                        classElement.simpleName)

        return null
    }

    private fun generateParams(
            statementBuilder: StringBuilder,
            params: List<ParameterSpec>
    ) {
        var first = true
        for (param in params) {
            if (!first) {
                statementBuilder.append(", ")
            }
            first = false
            statementBuilder.append(param.name)
        }
        statementBuilder.append(")")
    }

    private fun generateSettersAndGetters(modelInfo: GeneratedModelInfo): List<MethodSpec> {
        val methods = ArrayList<MethodSpec>()

        for (attr in modelInfo.getAttributeInfo()) {
            if (attr is ViewAttributeInfo && attr.generateStringOverloads) {
                methods.addAll(StringOverloadWriter(modelInfo, attr, configManager).buildMethods())
            } else {
                if (attr.isViewClickListener) {
                    methods.add(generateSetClickModelListener(modelInfo, attr))
                }

                if (attr.generateSetter() && !attr.hasFinalModifier()) {
                    methods.add(generateSetter(modelInfo, attr))
                }

                if (attr.generateGetter()) {
                    methods.add(generateGetter(attr))
                }
            }
        }

        return methods
    }

    private fun generateSetClickModelListener(
            classInfo: GeneratedModelInfo,
            attribute: AttributeInfo
    ): MethodSpec {
        val attributeName = attribute.generatedSetterName()

        val clickListenerType = if (isViewLongClickListenerType(attribute.getTypeMirror()))
            getModelLongClickListenerType(classInfo)
        else
            getModelClickListenerType(classInfo)

        val param = ParameterSpec.builder(clickListenerType, attributeName, FINAL).build()

        val builder = MethodSpec.methodBuilder(attributeName)
                .addJavadoc(
                        "Set a click listener that will provide the parent view, model, and adapter "
                                + "position of the clicked view. This will clear the normal View.OnClickListener "
                                + "if one has been set")
                .addModifiers(PUBLIC)
                .returns(classInfo.parameterizedGeneratedName)
                .addParameter(param)
                .addAnnotations(attribute.getSetterAnnotations())

        setBitSetIfNeeded(classInfo, attribute, builder)

        val wrapperClickListenerConstructor = CodeBlock.of("new \$T(\$L)",
                                                           getClassName(WRAPPED_LISTENER_TYPE),
                                                           param.name)

        addOnMutationCall(builder)
                .beginControlFlow("if (\$L == null)", attributeName)
                .addStatement(attribute.setterCode(), "null")
                .endControlFlow()
                .beginControlFlow("else")
                .addStatement(attribute.setterCode(), wrapperClickListenerConstructor)
                .endControlFlow()
                .addStatement("return this")

        return builder.build()
    }

    private fun generateEquals(helperClass: GeneratedModelInfo) = buildMethod("equals") {
        addAnnotation(Override::class.java)
        addModifiers(PUBLIC)
        returns(Boolean::class.javaPrimitiveType!!)
        addParameter(Any::class.java, "o")
        beginControlFlow("if (o == this)")
        addStatement("return true")
        endControlFlow()
        beginControlFlow("if (!(o instanceof \$T))", helperClass.generatedName)
        addStatement("return false")
        endControlFlow()
        beginControlFlow("if (!super.equals(o))")
        addStatement("return false")
        endControlFlow()
        addStatement("\$T that = (\$T) o", helperClass.generatedName, helperClass.generatedName)

        startNotEqualsControlFlow(
                this,
                false,
                getClassName(ON_BIND_MODEL_LISTENER_TYPE),
                modelBindListenerFieldName())
        addStatement("return false")
        endControlFlow()

        startNotEqualsControlFlow(
                this,
                false,
                getClassName(ON_UNBIND_MODEL_LISTENER_TYPE),
                modelUnbindListenerFieldName())
        addStatement("return false")
        endControlFlow()

        for (attributeInfo in helperClass.getAttributeInfo()) {
            val type = attributeInfo.typeName

            if (!attributeInfo.useInHash() && type.isPrimitive) {
                continue
            }

            startNotEqualsControlFlow(this, attributeInfo)
            addStatement("return false")
            endControlFlow()
        }

        addStatement("return true")
    }

    private fun generateHashCode(helperClass: GeneratedModelInfo) = buildMethod("hashCode") {
        addAnnotation(Override::class.java)
        addModifiers(PUBLIC)
        returns(TypeName.INT)
        addStatement("int result = super.hashCode()")

        addHashCodeLineForType(
                this,
                false,
                getClassName(ON_BIND_MODEL_LISTENER_TYPE),
                modelBindListenerFieldName()
        )

        addHashCodeLineForType(
                this,
                false,
                getClassName(ON_UNBIND_MODEL_LISTENER_TYPE),
                modelUnbindListenerFieldName()
        )

        for (attributeInfo in helperClass.getAttributeInfo()) {
            if (!attributeInfo.useInHash()) {
                continue
            }
            if (attributeInfo.typeName === DOUBLE) {
                addStatement("long temp")
                break
            }
        }

        for (attributeInfo in helperClass.getAttributeInfo()) {
            val type = attributeInfo.typeName

            if (!attributeInfo.useInHash() && type.isPrimitive) {
                continue
            }

            addHashCodeLineForType(this, attributeInfo.useInHash(), type,
                                   attributeInfo.getterCode())
        }

        addStatement("return result")
    }

    private fun generateToString(helperClass: GeneratedModelInfo) = buildMethod("toString") {
        addAnnotation(Override::class.java)
        addModifiers(PUBLIC)
        returns(String::class.java)

        val sb = StringBuilder()
        sb.append(String.format("\"%s{\" +\n", helperClass.generatedName.simpleName()))

        var first = true
        for (attributeInfo in helperClass.getAttributeInfo()) {
            if (attributeInfo.doNotUseInToString()) {
                continue
            }

            val attributeName = attributeInfo.getFieldName()
            if (first) {
                sb.append(String.format("\"%s=\" + %s +\n", attributeName,
                                        attributeInfo.getterCode()))
                first = false
            } else {
                sb.append(String.format("\", %s=\" + %s +\n", attributeName,
                                        attributeInfo.getterCode()))
            }
        }

        sb.append("\"}\" + super.toString()")

        addStatement("return \$L", sb.toString())
    }

    private fun generateGetter(data: AttributeInfo) = buildMethod(data.generatedGetterName()) {
        addModifiers(PUBLIC)
        returns(data.typeName)
        addAnnotations(data.getGetterAnnotations())
        addStatement("return \$L", data.superGetterCode())
    }

    private fun generateSetter(
            modelInfo: GeneratedModelInfo,
            attribute: AttributeInfo
    ): MethodSpec {
        val attributeName = attribute.getFieldName()
        val paramName = attribute.generatedSetterName()
        val builder = MethodSpec.methodBuilder(attribute.generatedSetterName())
                .addModifiers(PUBLIC)
                .returns(modelInfo.parameterizedGeneratedName)

        val hasMultipleParams = attribute is MultiParamAttribute
        if (hasMultipleParams) {
            builder.addParameters((attribute as MultiParamAttribute).params)
            builder.varargs((attribute as MultiParamAttribute).varargs())
        } else {
            builder.addParameter(
                    ParameterSpec.builder(attribute.typeName, paramName)
                            .addAnnotations(attribute.getSetterAnnotations()).build())
        }

        if (attribute.javaDoc != null) {
            builder.addJavadoc(attribute.javaDoc)
        }

        if (!hasMultipleParams) {
            addParameterNullCheckIfNeeded(configManager, attribute, paramName, builder)
        }

        setBitSetIfNeeded(modelInfo, attribute, builder)

        if (attribute.isOverload) {
            for (overload in attribute.attributeGroup.attributes) {
                if (overload === attribute) {
                    // Need to clear the other attributes in the group
                    continue
                }

                if (shouldUseBitSet(modelInfo)) {
                    builder.addStatement("\$L.clear(\$L)", ATTRIBUTES_BITSET_FIELD_NAME,
                                         attributeIndex(modelInfo, overload))
                }

                builder.addStatement(overload.setterCode(),
                                     if (overload.codeToSetDefault.isPresent)
                                         overload.codeToSetDefault.value()
                                     else
                                         Utils.getDefaultValue(overload.typeName))
            }
        }

        addOnMutationCall(builder)
                .addStatement(attribute.setterCode(),
                              if (hasMultipleParams)
                                  (attribute as MultiParamAttribute).valueToSetOnAttribute
                              else
                                  paramName)

        // Call the super setter if it exists.
        // No need to do this if the attribute is private since we already called the super setter to
        // set it
        if (!attribute.isPrivate && attribute.hasSuperSetterMethod()) {
            if (hasMultipleParams) {
                errorLogger
                        .logError("Multi params not supported for methods that call super (%s)",
                                  attribute)
            }

            builder.addStatement("super.\$L(\$L)", attributeName, paramName)
        }

        return builder
                .addStatement("return this")
                .build()
    }

    private fun generateReset(helperClass: GeneratedModelInfo) = buildMethod("reset") {
        addAnnotation(Override::class.java)
        addModifiers(PUBLIC)
        returns(helperClass.parameterizedGeneratedName)
        addStatement("\$L = null", modelBindListenerFieldName())
        addStatement("\$L = null", modelUnbindListenerFieldName())

        if (shouldUseBitSet(helperClass)) {
            addStatement("\$L.clear()", ATTRIBUTES_BITSET_FIELD_NAME)
        }

        helperClass.getAttributeInfo()
                .filterNot { it.hasFinalModifier() }
                .forEach {
                    addStatement(it.setterCode(),
                                 if (it.codeToSetDefault.isPresent)
                                     it.codeToSetDefault.value()
                                 else
                                     Utils.getDefaultValue(it.typeName))
                }

        addStatement("super.reset()")
        addStatement("return this")
    }

    private fun addHashCodeValidationIfNecessary(
            method: MethodSpec.Builder,
            message: String
    ): MethodSpec.Builder {
        if (configManager.shouldValidateModelUsage()) {
            method.addStatement("validateStateHasNotChangedSinceAdded(\$S, position)", message)
        }

        return method
    }

    companion object {
        /**
         * Use this suffix on helper fields added to the generated class so that we don't clash with
         * fields on the original model.
         */
        private val GENERATED_FIELD_SUFFIX = "_epoxyGeneratedModel"
        private val CREATE_NEW_HOLDER_METHOD_NAME = "createNewHolder"
        private val GET_DEFAULT_LAYOUT_METHOD_NAME = "getDefaultLayout"
        val ATTRIBUTES_BITSET_FIELD_NAME = "assignedAttributes" + GENERATED_FIELD_SUFFIX

        fun shouldUseBitSet(info: GeneratedModelInfo): Boolean = info is ModelViewInfo

        fun isAttributeSetCode(
                info: GeneratedModelInfo,
                attribute: AttributeInfo
        ) = CodeBlock.of("\$L.get(\$L)", ATTRIBUTES_BITSET_FIELD_NAME,
                         attributeIndex(info, attribute))!!

        private fun attributeIndex(
                modelInfo: GeneratedModelInfo,
                attributeInfo: AttributeInfo
        ): Int {
            val index = modelInfo.attributeInfo.indexOf(attributeInfo)
            if (index < 0) {
                throw IllegalStateException("The attribute does not exist in the model.")
            }
            return index
        }

        fun setBitSetIfNeeded(
                modelInfo: GeneratedModelInfo,
                attr: AttributeInfo,
                stringSetter: Builder
        ) {
            if (shouldUseBitSet(modelInfo)) {
                stringSetter.addStatement("\$L.set(\$L)", ATTRIBUTES_BITSET_FIELD_NAME,
                                          attributeIndex(modelInfo, attr))
            }
        }

        fun addParameterNullCheckIfNeeded(
                configManager: ConfigManager,
                attr: AttributeInfo,
                paramName: String,
                builder: Builder
        ) {

            if (configManager.shouldValidateModelUsage()
                    && attr.hasSetNullability()
                    && !attr.isNullable) {

                builder.beginControlFlow("if (\$L == null)", paramName)
                        .addStatement("throw new \$T(\"\$L cannot be null\")",
                                      IllegalArgumentException::class.java, paramName)
                        .endControlFlow()
            }
        }

        fun startNotEqualsControlFlow(
                methodBuilder: MethodSpec.Builder,
                attribute: AttributeInfo
        ): MethodSpec.Builder {
            val attributeType = attribute.typeName
            val useHash = attributeType.isPrimitive || attribute.useInHash()
            return startNotEqualsControlFlow(methodBuilder, useHash, attributeType,
                                             attribute.getterCode())
        }

        fun startNotEqualsControlFlow(
                builder: Builder,
                useObjectHashCode: Boolean,
                type: TypeName,
                accessorCode: String
        ) = builder.beginControlFlow("if (\$L)",
                                     notEqualsCodeBlock(useObjectHashCode, type, accessorCode))

        fun notEqualsCodeBlock(attribute: AttributeInfo): CodeBlock {
            val attributeType = attribute.typeName
            val useHash = attributeType.isPrimitive || attribute.useInHash()
            return notEqualsCodeBlock(useHash, attributeType, attribute.getterCode())
        }

        fun notEqualsCodeBlock(
                useObjectHashCode: Boolean,
                type: TypeName,
                accessorCode: String
        ): CodeBlock = if (useObjectHashCode) {
            when {
                type === FLOAT -> CodeBlock.of("(Float.compare(that.\$L, \$L) != 0)",
                                               accessorCode, accessorCode)
                type === DOUBLE -> CodeBlock.of("(Double.compare(that.\$L, \$L) != 0)",
                                                accessorCode, accessorCode)
                type.isPrimitive -> CodeBlock.of("(\$L != that.\$L)", accessorCode, accessorCode)
                type is ArrayTypeName -> CodeBlock.of("!\$T.equals(\$L, that.\$L)",
                                                      TypeName.get(Arrays::class.java),
                                                      accessorCode, accessorCode)
                else -> CodeBlock.of(
                        "(\$L != null ? !\$L.equals(that.\$L) : that.\$L != null)",
                        accessorCode, accessorCode, accessorCode, accessorCode)
            }
        } else {
            CodeBlock.of("((\$L == null) != (that.\$L == null))", accessorCode, accessorCode)
        }

        private fun addHashCodeLineForType(
                builder: Builder,
                useObjectHashCode: Boolean,
                type: TypeName,
                accessorCode: String
        ) {
            builder.apply {
                if (useObjectHashCode) {
                    when (type) {
                        BYTE, CHAR, SHORT, INT -> addStatement("result = 31 * result + \$L",
                                                               accessorCode)
                        LONG -> addStatement("result = 31 * result + (int) (\$L ^ (\$L >>> 32))",
                                             accessorCode,
                                             accessorCode)
                        FLOAT -> addStatement(
                                "result = 31 * result + (\$L != +0.0f " + "? Float.floatToIntBits(\$L) : 0)",
                                accessorCode, accessorCode)
                        DOUBLE -> {
                            addStatement("temp = Double.doubleToLongBits(\$L)", accessorCode)
                            addStatement("result = 31 * result + (int) (temp ^ (temp >>> 32))")
                        }
                        BOOLEAN -> addStatement("result = 31 * result + (\$L ? 1 : 0)",
                                                accessorCode)
                        is ArrayTypeName -> addStatement(
                                "result = 31 * result + Arrays.hashCode(\$L)", accessorCode)
                        else -> addStatement(
                                "result = 31 * result + (\$L != null ? \$L.hashCode() : 0)",
                                accessorCode,
                                accessorCode)
                    }
                } else {
                    addStatement("result = 31 * result + (\$L != null ? 1 : 0)", accessorCode)
                }
            }
        }

        fun addOnMutationCall(method: MethodSpec.Builder) = method.addStatement("onMutation()")!!

        fun modelImplementsBindWithDiff(
                clazz: TypeElement,
                bindWithDiffMethod: MethodSpec,
                types: Types,
                elements: Elements
        ): Boolean {
            val methodOnClass = Utils.getMethodOnClass(clazz, bindWithDiffMethod, types,
                                                       elements) ?: return false

            if (Modifier.ABSTRACT in methodOnClass.modifiers) {
                return false
            }

            val enclosingElement = methodOnClass.enclosingElement as TypeElement

            // As long as the implementation is not on the base EpoxyModel we consider it a custom
            // implementation
            return enclosingElement.qualifiedName.toString() != Utils.UNTYPED_EPOXY_MODEL_TYPE
        }
    }
}
