package com.airbnb.epoxy.processor

import androidx.annotation.LayoutRes
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XFiler
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.addOriginatingElement
import androidx.room.compiler.processing.writeTo
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.processor.ClassNames.ANDROID_ASYNC_TASK
import com.airbnb.epoxy.processor.ClassNames.EPOXY_MODEL_PROPERTIES
import com.airbnb.epoxy.processor.ClassNames.PARIS_STYLE
import com.airbnb.epoxy.processor.Utils.implementsMethod
import com.airbnb.epoxy.processor.resourcescanning.ResourceScanner
import com.airbnb.epoxy.processor.resourcescanning.ResourceValue
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
import java.lang.annotation.AnnotationTypeMismatchException
import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.Arrays
import java.util.BitSet
import java.util.Objects
import javax.lang.model.element.Modifier
import javax.lang.model.element.Modifier.FINAL
import javax.lang.model.element.Modifier.PRIVATE
import javax.lang.model.element.Modifier.PROTECTED
import javax.lang.model.element.Modifier.PUBLIC
import javax.lang.model.element.Modifier.STATIC

class GeneratedModelWriter(
    private val filer: XFiler,
    private val environment: XProcessingEnv,
    private val logger: Logger,
    private val resourceProcessor: ResourceScanner,
    private val configManager: ConfigManager,
    private val dataBindingModuleLookup: DataBindingModuleLookup,
    asyncable: Asyncable,
    private val memoizer: Memoizer
) {

    val modelInterfaceWriter =
        ModelBuilderInterfaceWriter(filer, environment, asyncable, configManager)

    open class BuilderHooks {
        open fun beforeFinalBuild(builder: TypeSpec.Builder) {}

        /** Opportunity to add additional code to the unbind method.  */
        open fun addToUnbindMethod(
            unbindBuilder: Builder,
            unbindParamName: String
        ) {
        }

        /** Opportunity to add additional code to the visibilityStateChanged method.  */
        open fun addToVisibilityStateChangedMethod(
            visibilityBuilder: Builder,
            visibilityParamName: String
        ) {
        }

        /** Opportunity to add additional code to the visibilityChanged method.  */
        open fun addToVisibilityChangedMethod(
            visibilityBuilder: MethodSpec.Builder,
            visibilityParamName: String
        ) {
        }

        /**
         * True true to have the bind method build, false to not add the method to the generated
         * class.
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

    fun generateClassForModel(
        info: GeneratedModelInfo,
        originatingElements: List<XElement>,
        builderHooks: BuilderHooks? = null
    ) {
        if (!info.shouldGenerateModel) {
            return
        }

        val generatedModelName = info.generatedName

        val modelClass = buildClass(generatedModelName) {
            addJavadoc("Generated file. Do not modify!")
            addModifiers(PUBLIC)
            superclass(info.superClassName)
            addSuperinterface(getGeneratedModelInterface(info))
            addTypeVariables(info.typeVariables)
            addAnnotations(info.annotations)
            addFields(buildStyleConstant(info))
            addFields(generateFields(info))
            addMethods(generateConstructors(info))

            generateDebugAddToMethodIfNeeded(this, info)

            addMethods(generateProgrammaticViewMethods(info))
            addMethods(generateBindMethods(builderHooks, info))
            addMethods(generateVisibilityMethods(builderHooks, info))
            addMethods(generateStyleableViewMethods(info))
            addMethods(generateSettersAndGetters(info))
            addMethods(generateMethodsReturningClassType(info))
            addMethods(generateDefaultMethodImplementations(info))
            addMethods(generateOtherLayoutOptions(info))
            addMethods(generateDataBindingMethodsIfNeeded(info))
            if (!configManager.disableGenerateReset(info)) {
                addMethod(generateReset(info))
            }
            addMethod(generateEquals(info))
            addMethod(generateHashCode(info))
            addMethod(generateToString(info))

            addFromPropertiesMethodIfNeeded(this, info)

            builderHooks?.beforeFinalBuild(this)

            addSuperinterface(modelInterfaceWriter.writeInterface(info, this.build().methodSpecs))

            originatingElements.forEach {
                addOriginatingElement(it)
            }
        }

        JavaFile.builder(generatedModelName.packageName(), modelClass)
            .build()
            .writeTo(filer, mode = XFiler.Mode.Aggregating)
    }

    private fun generateOtherLayoutOptions(info: GeneratedModelInfo): Iterable<MethodSpec> {
        if (!info.includeOtherLayoutOptions || info.isProgrammaticView) {
            // Layout resources can't be mixed with programmatic views
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
            for (
                namePart in otherLayout.resourceName!!.substring(defaultLayoutNameLength).split(
                    "_".toRegex()
                ).dropLastWhile { it.isEmpty() }
            ) {
                layoutDescription += Utils.capitalizeFirstLetter(namePart)
            }

            result.add(
                buildMethod("with" + layoutDescription + "Layout") {
                    returns(info.parameterizedGeneratedName)
                    addModifiers(PUBLIC)
                    addStatement("layout(\$L)", otherLayout.code)
                    addStatement("return this")
                }
            )
        }

        return result
    }

    private fun getGeneratedModelInterface(info: GeneratedModelInfo): ParameterizedTypeName {
        return ParameterizedTypeName.get(
            ClassNames.EPOXY_GENERATED_MODEL_INTERFACE,
            info.modelType
        )
    }

    private fun buildStyleConstant(info: GeneratedModelInfo): Iterable<FieldSpec> {

        val styleBuilderInfo = info.styleBuilderInfo ?: return emptyList()

        val constantFields = ArrayList<FieldSpec>()

        // If this is a styleable view we add a constant to store the default style builder.
        // This is an optimization to avoid recreating the default style many times, since it is
        // likely often needed at runtime.
        constantFields.add(
            buildField(ClassNames.PARIS_STYLE, PARIS_DEFAULT_STYLE_CONSTANT_NAME) {
                addModifiers(FINAL, PRIVATE, STATIC)
                initializer(
                    "new \$T().addDefault().build()",
                    styleBuilderInfo.styleBuilderClass
                )
            }
        )

        // We store styles in a weak reference since if a controller uses it
        // once it is likely to be used in other models and when models are rebuilt
        for ((name) in styleBuilderInfo.styles) {
            constantFields.add(
                FieldSpec.builder(
                    ParameterizedTypeName.get(
                        ClassName.get(WeakReference::class.java),
                        ClassNames.PARIS_STYLE
                    ),
                    weakReferenceFieldForStyle(name),
                    PRIVATE, STATIC
                )
                    .build()
            )
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
                    initializer(
                        "new \$T(\$L)", BitSet::class.java,
                        classInfo.attributeInfo.size
                    )
                }
            )
        }

        // Add fields for the bind/unbind listeners
        val onBindListenerType = ParameterizedTypeName.get(
            ClassNames.EPOXY_ON_BIND_MODEL_LISTENER,
            classInfo.parameterizedGeneratedName,
            classInfo.modelType
        )
        fields.add(
            FieldSpec.builder(
                onBindListenerType, modelBindListenerFieldName(),
                PRIVATE
            ).build()
        )

        val onUnbindListenerType = ParameterizedTypeName.get(
            ClassNames.EPOXY_ON_UNBIND_MODEL_LISTENER,
            classInfo.parameterizedGeneratedName,
            classInfo.modelType
        )
        fields.add(
            FieldSpec.builder(
                onUnbindListenerType, modelUnbindListenerFieldName(),
                PRIVATE
            ).build()
        )

        val onVisibilityStateChangedListenerType = ParameterizedTypeName.get(
            ClassNames.EPOXY_ON_VISIBILITY_STATE_MODEL_LISTENER,
            classInfo.parameterizedGeneratedName,
            classInfo.modelType
        )
        fields.add(
            FieldSpec.builder(
                onVisibilityStateChangedListenerType,
                modelVisibilityStateChangedListenerFieldName(),
                PRIVATE
            ).build()
        )

        val onVisibilityChangedListenerType = ParameterizedTypeName.get(
            ClassNames.EPOXY_ON_VISIBILITY_MODEL_LISTENER,
            classInfo.parameterizedGeneratedName,
            classInfo.modelType
        )
        fields.add(
            FieldSpec.builder(
                onVisibilityChangedListenerType, modelVisibilityChangedListenerFieldName(),
                PRIVATE
            ).build()
        )

        classInfo.attributeInfo
            .filter { it.isGenerated }
            .mapTo(fields) { attributeInfo ->
                buildField(attributeInfo.typeName, attributeInfo.fieldName) {
                    addModifiers(PRIVATE)
                    addAnnotations(attributeInfo.setterAnnotations)

                    if (shouldUseBitSet(classInfo, attr = attributeInfo)) {
                        addJavadoc("Bitset index: \$L", attributeIndex(classInfo, attributeInfo))
                    }

                    if (attributeInfo.codeToSetDefault.isPresent) {
                        initializer(attributeInfo.codeToSetDefault.value())
                    }
                }
            }

        return fields
    }

    private fun modelUnbindListenerFieldName(): String =
        "onModelUnboundListener$GENERATED_FIELD_SUFFIX"

    private fun modelBindListenerFieldName(): String =
        "onModelBoundListener$GENERATED_FIELD_SUFFIX"

    private fun modelVisibilityStateChangedListenerFieldName(): String =
        "onModelVisibilityStateChangedListener$GENERATED_FIELD_SUFFIX"

    private fun modelVisibilityChangedListenerFieldName(): String =
        "onModelVisibilityChangedListener$GENERATED_FIELD_SUFFIX"

    private fun getModelClickListenerType(classInfo: GeneratedModelInfo): ParameterizedTypeName {
        return ParameterizedTypeName.get(
            ClassNames.EPOXY_MODEL_CLICK_LISTENER,
            classInfo.parameterizedGeneratedName,
            classInfo.modelType
        )
    }

    private fun getModelLongClickListenerType(
        classInfo: GeneratedModelInfo
    ): ParameterizedTypeName {
        return ParameterizedTypeName.get(
            ClassNames.EPOXY_MODEL_LONG_CLICK_LISTENER,
            classInfo.parameterizedGeneratedName,
            classInfo.modelType
        )
    }

    private fun getModelCheckedChangeListenerType(
        classInfo: GeneratedModelInfo
    ): ParameterizedTypeName {
        return ParameterizedTypeName.get(
            ClassNames.EPOXY_MODEL_CHECKED_CHANGE_LISTENER,
            classInfo.parameterizedGeneratedName,
            classInfo.modelType
        )
    }

    /** Include any constructors that are in the super class.  */
    private fun generateConstructors(info: GeneratedModelInfo): Iterable<MethodSpec> {
        return info.constructors.map {
            buildConstructor {
                // Final is not allowed on java constructors, but ksp can add it, so we remove it
                addModifiers(it.modifiers.minus(FINAL))
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
            addParameter(ClassNames.EPOXY_CONTROLLER, "controller")
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
                    addStatement(
                        "\tthrow new \$T(\"A value is required for \$L\")",
                        IllegalStateException::class.java,
                        attributeGroup.name
                    )
                    addCode("}\n")
                }
        }
    }

    private fun generateProgrammaticViewMethods(
        modelInfo: GeneratedModelInfo
    ): Iterable<MethodSpec> {

        if (!modelInfo.isProgrammaticView) {
            return emptyList()
        }

        val methods = ArrayList<MethodSpec>()

        // getViewType method so that view type is generated at runtime
        methods.add(
            buildMethod("getViewType") {
                addAnnotation(Override::class.java)
                addModifiers(PROTECTED)
                returns(TypeName.INT)
                addStatement("return 0", modelInfo.modelType)
            }
        )

        // buildView method to return new view instance
        methods.add(
            buildMethod("buildView") {
                addAnnotation(Override::class.java)
                addParameter(ClassNames.ANDROID_VIEW_GROUP, "parent")
                addModifiers(PUBLIC)
                returns(modelInfo.modelType)
                addStatement(
                    "\$T v = new \$T(parent.getContext())", modelInfo.modelType,
                    modelInfo.modelType
                )

                val (layoutWidth, layoutHeight) = getLayoutDimensions(modelInfo)

                addStatement(
                    "v.setLayoutParams(new \$T(\$L, \$L))",
                    ClassNames.ANDROID_MARGIN_LAYOUT_PARAMS, layoutWidth, layoutHeight
                )

                addStatement("return v")
            }
        )

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

    private fun generateVisibilityMethods(
        builderHooks: BuilderHooks?,
        modelInfo: GeneratedModelInfo
    ): Iterable<MethodSpec> {
        val methods = ArrayList<MethodSpec>()

        val visibilityObjectParam =
            ParameterSpec.builder(modelInfo.modelType, "object", FINAL).build()

        methods.add(buildVisibilityStateChangedMethod(builderHooks, visibilityObjectParam))

        val onVisibilityStateChangedListenerType = ParameterizedTypeName.get(
            ClassNames.EPOXY_ON_VISIBILITY_STATE_MODEL_LISTENER,
            modelInfo.parameterizedGeneratedName,
            modelInfo.modelType
        )
        val visibilityStateChangedListenerParam =
            ParameterSpec.builder(onVisibilityStateChangedListenerType, "listener").build()

        val onVisibilityStateChanged = MethodSpec.methodBuilder("onVisibilityStateChanged")
            .addJavadoc(
                "Register a listener that will be called when this model visibility state " +
                    "has changed.\n" +
                    "<p>\n" +
                    "The listener will contribute to this model's hashCode state per the {@link\n" +
                    "com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.\n"
            )
            .addModifiers(PUBLIC)
            .returns(modelInfo.parameterizedGeneratedName)

        addOnMutationCall(onVisibilityStateChanged)
            .addParameter(visibilityStateChangedListenerParam)
            .addStatement("this.\$L = listener", modelVisibilityStateChangedListenerFieldName())
            .addStatement("return this")

        methods.add(onVisibilityStateChanged.build())

        methods.add(buildVisibilityChangedMethod(builderHooks, visibilityObjectParam))

        val onVisibilityChangedListenerType = ParameterizedTypeName.get(
            ClassNames.EPOXY_ON_VISIBILITY_MODEL_LISTENER,
            modelInfo.parameterizedGeneratedName,
            modelInfo.modelType
        )
        val visibilityChangedListenerParam =
            ParameterSpec.builder(onVisibilityChangedListenerType, "listener").build()

        val onVisibilityChanged = MethodSpec.methodBuilder("onVisibilityChanged")
            .addJavadoc(
                "Register a listener that will be called when this model visibility has " +
                    "changed.\n" +
                    "<p>\n" +
                    "The listener will contribute to this model's hashCode state per the {@link\n" +
                    "com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.\n"
            )
            .addModifiers(PUBLIC)
            .returns(modelInfo.parameterizedGeneratedName)

        addOnMutationCall(onVisibilityChanged)
            .addParameter(visibilityChangedListenerParam)
            .addStatement("this.\$L = listener", modelVisibilityChangedListenerFieldName())
            .addStatement("return this")

        methods.add(onVisibilityChanged.build())

        return methods
    }

    private fun generateBindMethods(
        builderHooks: BuilderHooks?,
        modelInfo: GeneratedModelInfo
    ): Iterable<MethodSpec> {
        val methods = ArrayList<MethodSpec>()

        // Add bind/unbind methods so the class can set the epoxyModelBoundObject and
        // boundEpoxyViewHolder fields for the model click listener to access

        val viewHolderType = ClassNames.EPOXY_VIEW_HOLDER
        val viewHolderParam = ParameterSpec.builder(viewHolderType, "holder", FINAL).build()

        val boundObjectParam = ParameterSpec.builder(modelInfo.modelType, "object", FINAL)
            .build()

        methods.add(buildPreBindMethod(modelInfo, viewHolderParam, boundObjectParam))

        // If the view is styleable then we need to override bind to apply the style
        // If builderhooks is nonnull we assume that it is adding code to the bind methods
        if (builderHooks != null || modelInfo.isStyleable) {
            methods.add(buildBindMethod(builderHooks, boundObjectParam, modelInfo))
            methods.add(buildBindWithDiffMethod(builderHooks, modelInfo, boundObjectParam))
        }

        val postBind = buildMethod("handlePostBind") {
            addModifiers(PUBLIC)
            addAnnotation(Override::class.java)
            addParameter(boundObjectParam)
            addParameter(TypeName.INT, "position")

            if (modelInfo.isSuperClassAlsoGenerated) {
                // If a super class is also generated we need to make sure to call through to these
                // methods on it as well. This is particularly important for EpoxyModelGroup.
                addStatement("super.handlePostBind(\$L, position)", boundObjectParam.name)
            }

            beginControlFlow("if (\$L != null)", modelBindListenerFieldName())
            addStatement(
                "\$L.onModelBound(this, object, position)",
                modelBindListenerFieldName()
            )
            endControlFlow()

            addHashCodeValidationIfNecessary(
                this,
                "The model was changed during the bind call."
            )

            builderHooks?.addToHandlePostBindMethod(this, boundObjectParam)
        }

        methods.add(postBind)

        val onBindListenerType = ParameterizedTypeName.get(
            ClassNames.EPOXY_ON_BIND_MODEL_LISTENER,
            modelInfo.parameterizedGeneratedName,
            modelInfo.modelType
        )
        val bindListenerParam = ParameterSpec.builder(onBindListenerType, "listener").build()

        val onBind = MethodSpec.methodBuilder("onBind")
            .addJavadoc(
                "Register a listener that will be called when this model is bound to a view.\n" +
                    "<p>\n" +
                    "The listener will contribute to this model's hashCode state per the {@link\n" +
                    "com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.\n" +
                    "<p>\n" +
                    "You may clear the listener by setting a null value, or by calling " +
                    "{@link #reset()}"
            )
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

        methods.add(
            unbindBuilder
                .build()
        )

        val onUnbindListenerType = ParameterizedTypeName.get(
            ClassNames.EPOXY_ON_UNBIND_MODEL_LISTENER,
            modelInfo.parameterizedGeneratedName,
            modelInfo.modelType
        )
        val unbindListenerParam = ParameterSpec.builder(onUnbindListenerType, "listener")
            .build()

        val onUnbind = MethodSpec.methodBuilder("onUnbind")
            .addJavadoc(
                "Register a listener that will be called when this model is unbound from a " +
                    "view.\n" +
                    "<p>\n" +
                    "The listener will contribute to this model's hashCode state per the {@link\n" +
                    "com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.\n" +
                    "<p>\n" +
                    "You may clear the listener by setting a null value, or by calling " +
                    "{@link #reset()}"
            )
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
        builderHooks: BuilderHooks?,
        boundObjectParam: ParameterSpec,
        modelInfo: GeneratedModelInfo
    ) = buildMethod("bind") {
        addAnnotation(Override::class.java)
        addModifiers(PUBLIC)
        addParameter(boundObjectParam)

        // The style is applied before calling super or binding properties so that the model can
        // override style settings
        addBindStyleCodeIfNeeded(
            modelInfo,
            this,
            boundObjectParam,
            false
        )

        addStatement("super.bind(\$L)", boundObjectParam.name)

        builderHooks?.addToBindMethod(this, boundObjectParam)
    }

    private fun buildVisibilityStateChangedMethod(
        builderHooks: BuilderHooks?,
        visibilityObjectParam: ParameterSpec
    ) = buildMethod("onVisibilityStateChanged") {
        addAnnotation(Override::class.java)
        addModifiers(PUBLIC)
        addParameter(TypeName.INT, "visibilityState")
        addParameter(visibilityObjectParam)

        beginControlFlow("if (\$L != null)", modelVisibilityStateChangedListenerFieldName())
        addStatement(
            "\$L.onVisibilityStateChanged(this, object, visibilityState)",
            modelVisibilityStateChangedListenerFieldName()
        )
        endControlFlow()

        builderHooks?.addToVisibilityStateChangedMethod(
            this,
            visibilityObjectParam.name
        )

        addStatement(
            "super.onVisibilityStateChanged(\$L, \$L)",
            "visibilityState",
            visibilityObjectParam.name
        )
    }

    private fun buildVisibilityChangedMethod(
        builderHooks: BuilderHooks?,
        visibilityObjectParam: ParameterSpec
    ) = buildMethod("onVisibilityChanged") {
        addAnnotation(Override::class.java)
        addModifiers(PUBLIC)
        addParameter(TypeName.FLOAT, "percentVisibleHeight")
        addParameter(TypeName.FLOAT, "percentVisibleWidth")
        addParameter(TypeName.INT, "visibleHeight")
        addParameter(TypeName.INT, "visibleWidth")
        addParameter(visibilityObjectParam)

        beginControlFlow("if (\$L != null)", modelVisibilityChangedListenerFieldName())
        addStatement(
            "\$L.onVisibilityChanged(this, object, percentVisibleHeight, percentVisibleWidth, " +
                "visibleHeight, visibleWidth)",
            modelVisibilityChangedListenerFieldName()
        )
        endControlFlow()

        builderHooks?.addToVisibilityChangedMethod(this, visibilityObjectParam.name)

        addStatement(
            "super.onVisibilityChanged(\$L, \$L, \$L, \$L, \$L)", "percentVisibleHeight",
            "percentVisibleWidth", "visibleHeight", "visibleWidth", visibilityObjectParam.name
        )
    }

    private fun buildBindWithDiffMethod(
        builderHooks: BuilderHooks?,
        classInfo: GeneratedModelInfo,
        boundObjectParam: ParameterSpec
    ) = buildMethod("bind") {

        val previousModelParam = ParameterSpec.builder(
            ClassNames.EPOXY_MODEL_UNTYPED,
            "previousModel"
        ).build()

        addAnnotation(Override::class.java)
        addModifiers(PUBLIC)
        addParameter(boundObjectParam)
        addParameter(previousModelParam)

        val generatedModelClass = classInfo.generatedName
        beginControlFlow(
            "if (!(\$L instanceof \$T))",
            previousModelParam.name,
            generatedModelClass
        )
        addStatement("bind(\$L)", boundObjectParam.name)
        addStatement("return")
        endControlFlow()
        addStatement(
            "\$T that = (\$T) previousModel",
            generatedModelClass,
            generatedModelClass
        )

        addBindStyleCodeIfNeeded(
            classInfo,
            this,
            boundObjectParam,
            true
        )

        // We want to make sure the base model has its bind method called as well. Since the
        // user can provide a custom base class we aren't sure if it implements diff binding.
        // If so we should call it, but if not, calling it would invoke the default
        // EpoxyModel implementation which calls normal "bind". Doing that would force a full
        // bind!!! So we mustn't do that. So, we only call the super diff binding if we think
        // it's a custom implementation.
        if (modelImplementsBindWithDiff(classInfo.superClassElement, build(), environment)) {
            addStatement(
                "super.bind(\$L, \$L)",
                boundObjectParam.name,
                previousModelParam.name
            )
        } else {
            addStatement("super.bind(\$L)", boundObjectParam.name)
        }

        builderHooks?.addToBindWithDiffMethod(
            this,
            boundObjectParam,
            previousModelParam
        )
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

        if (modelInfo.isSuperClassAlsoGenerated) {
            // If a super class is also generated we need to make sure to call through to these
            // methods on it as well. This is particularly important for EpoxyModelGroup.
            preBindBuilder.addStatement(
                "super.handlePreBind(\$L, \$L, \$L)",
                viewHolderParam.name,
                boundObjectParam.name,
                positionParamName
            )
        }

        addHashCodeValidationIfNecessary(
            preBindBuilder,
            "The model was changed between being added to the controller and being bound."
        )

        if (modelInfo.isStyleable && configManager.shouldValidateModelUsage()) {

            // We validate that the style attributes are the same as in the default, otherwise
            // recycling will not work correctly. It is done in the background since it is fairly
            // slow and can noticeably add jank to scrolling in dev
            preBindBuilder
                .beginControlFlow(
                    "if (!\$T.equals(\$L, \$L.getTag(\$T.id.epoxy_saved_view_style)))",
                    Objects::class.java,
                    PARIS_STYLE_ATTR_NAME,
                    boundObjectParam.name,
                    ClassNames.EPOXY_R
                )
                .beginControlFlow(
                    "\$T.THREAD_POOL_EXECUTOR.execute(new \$T()",
                    ANDROID_ASYNC_TASK,
                    Runnable::class.java
                )
                .beginControlFlow("public void run()")
                .beginControlFlow("try")
                .addStatement(
                    "\$T.assertSameAttributes(new \$T(\$L), \$L, \$L)",
                    ClassNames.PARIS_STYLE_UTILS,
                    modelInfo.styleBuilderInfo!!.styleApplierClass,
                    boundObjectParam.name,
                    PARIS_STYLE_ATTR_NAME,
                    PARIS_DEFAULT_STYLE_CONSTANT_NAME
                )
                .endControlFlow()
                .beginControlFlow("catch(\$T e)", AssertionError::class.java)
                .addStatement(
                    "throw new \$T(\"\$L model at position \" + \$L + \" has an invalid " +
                        "style:\\n\\n\" + e" + ".getMessage())",
                    IllegalStateException::class.java,
                    modelInfo.generatedName.simpleName(),
                    positionParamName
                )
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

        methods.add(
            builder
                .addStatement("return this")
                .build()
        )

        // Lambda for building the style
        val parameterizedBuilderCallbackType = ParameterizedTypeName.get(
            ClassNames.EPOXY_STYLE_BUILDER_CALLBACK, styleBuilderClass
        )

        methods.add(
            MethodSpec.methodBuilder("styleBuilder")
                .addModifiers(PUBLIC)
                .returns(modelInfo.parameterizedGeneratedName)
                .addParameter(parameterizedBuilderCallbackType, "builderCallback")
                .addStatement(
                    "\$T builder = new \$T()",
                    styleBuilderClass,
                    styleBuilderClass
                )
                .addStatement("builderCallback.buildStyle(builder.addDefault())")
                .addStatement("return \$L(builder.build())", PARIS_STYLE_ATTR_NAME)
                .build()
        )

        // Methods for setting each defined style directly
        for ((name, javadoc) in styleBuilderInfo.styles) {
            val capitalizedStyle = Utils.capitalizeFirstLetter(name)
            val methodName = "with" + capitalizedStyle + "Style"
            val fieldName = weakReferenceFieldForStyle(name)

            // The style is stored in a static weak reference since it is likely to be reused in
            // other models are when models are rebuilt.
            val styleMethodBuilder = MethodSpec.methodBuilder(methodName)

            if (javadoc != null) {
                styleMethodBuilder.addJavadoc(javadoc)
            }

            methods.add(
                styleMethodBuilder
                    .addModifiers(PUBLIC)
                    .returns(modelInfo.parameterizedGeneratedName)
                    .addStatement(
                        "\$T style = \$L != null ? \$L.get() : null",
                        ClassNames.PARIS_STYLE,
                        fieldName, fieldName
                    )
                    .beginControlFlow("if (style == null)")
                    .addStatement(
                        "style =  new \$T().add\$L().build()",
                        styleBuilderClass, capitalizedStyle
                    )
                    .addStatement(
                        "\$L = new \$T<>(style)", fieldName,
                        WeakReference::class.java
                    )
                    .endControlFlow()
                    .addStatement("return \$L(style)", PARIS_STYLE_ATTR_NAME)
                    .build()
            )
        }

        return methods
    }

    private fun generateMethodsReturningClassType(info: GeneratedModelInfo): Iterable<MethodSpec> {
        return info.methodsReturningClassType.mapNotNull { methodInfo ->

            val builder = MethodSpec.methodBuilder(methodInfo.name)
                .addModifiers(methodInfo.modifiers)
                .addParameters(methodInfo.params)
                .addAnnotation(Override::class.java)
                .varargs(methodInfo.varargs)
                .returns(info.parameterizedGeneratedName)

            val isLayoutUnsupportedOverload = info.isProgrammaticView &&
                "layout" == methodInfo.name &&
                methodInfo.params.size == 1 &&
                methodInfo.params[0].type === TypeName.INT

            if (isLayoutUnsupportedOverload) {
                builder.addStatement(
                    "throw new \$T(\"Layout resources are unsupported with programmatic " +
                        "views.\")",
                    UnsupportedOperationException::class.java
                )
            } else {

                val statementBuilder = StringBuilder(
                    String.format(
                        "super.%s(",
                        methodInfo.name
                    )
                )
                generateParams(statementBuilder, methodInfo.params)

                builder
                    .addStatement(statementBuilder.toString())
                    .addStatement("return this")
            }

            if (configManager.disableGenerateBuilderOverloads(info) && !isLayoutUnsupportedOverload) {
                // We want to keep the layout overload when it is throwing an UnsupportedOperationException
                // because that actually adds new behavior. All other overloads simply call super
                // and return "this", which can be disabled when builder chaining is not needed
                // (ie with kotlin).
                null
            } else {
                builder.build()
            }
        }
    }

    /**
     * Generates default implementations of certain model methods if the model is abstract and
     * doesn't implement them.
     */
    private fun generateDefaultMethodImplementations(
        info: GeneratedModelInfo
    ): Iterable<MethodSpec> {
        val methods = ArrayList<MethodSpec>()

        if (info.isProgrammaticView) {
            methods.add(
                buildDefaultLayoutMethodBase()
                    .toBuilder()
                    .addStatement(
                        "throw new \$T(\"Layout resources are unsupported for views created " +
                            "programmatically" + ".\")",
                        UnsupportedOperationException::class.java
                    )
                    .build()
            )
        } else {
            addCreateHolderMethodIfNeeded(info, methods)
            addDefaultLayoutMethodIfNeeded(info, methods)
        }

        return methods
    }

    /**
     * If the model is a holder and doesn't implement the "createNewHolder" method we can generate a
     * default implementation by getting the class type and creating a new instance of it.
     *
     * If the holder class have a constructor that accept the parent it will invoke it.
     */
    private fun addCreateHolderMethodIfNeeded(
        modelClassInfo: GeneratedModelInfo,
        methods: MutableList<MethodSpec>
    ) {

        val originalClassElement = modelClassInfo.superClassElement
        if (!originalClassElement.type.isEpoxyModelWithHolder(memoizer)) {
            return
        }

        var createHolderMethod = MethodSpec.methodBuilder(
            CREATE_NEW_HOLDER_METHOD_NAME
        )
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PROTECTED)
            .addParameter(
                ClassName.get("android.view", "ViewParent"),
                "parent"
            )
            .build()

        if (implementsMethod(originalClassElement, createHolderMethod, environment)) {
            return
        }

        createHolderMethod = with(createHolderMethod.toBuilder()) {
            returns(modelClassInfo.modelType)
            val modelTypeElement = modelClassInfo.modelType?.let { environment.findTypeElement(it) }
            if (modelTypeElement != null &&
                modelClassInfo.memoizer.hasViewParentConstructor(modelTypeElement)
            ) {
                addStatement("return new \$T(parent)", modelClassInfo.modelType)
            } else {
                addStatement("return new \$T()", modelClassInfo.modelType)
            }
            build()
        }

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

        methods.add(
            buildDefaultLayoutMethodBase()
                .toBuilder()
                .addStatement("return \$L", layout.code)
                .build()
        )
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
        if (implementsMethod(superClassElement, buildDefaultLayoutMethodBase(), environment)) {
            return null
        }

        val modelClassWithAnnotation = findSuperClassWithClassAnnotation(superClassElement)
        if (modelClassWithAnnotation == null) {
            logger
                .logError(
                    "Model must use %s annotation if it does not implement %s. (class: %s)",
                    EpoxyModelClass::class.java,
                    GET_DEFAULT_LAYOUT_METHOD_NAME,
                    modelInfo.superClassName
                )
            return null
        }

        return resourceProcessor
            .getResourceValue(EpoxyModelClass::class, modelClassWithAnnotation, "layout")
    }

    /**
     * Add `setDataBindingVariables` for DataBinding models if they haven't implemented it. This
     * adds the basic method and a method that checks for payload changes and only sets the
     * variables that changed.
     */
    private fun generateDataBindingMethodsIfNeeded(info: GeneratedModelInfo): Iterable<MethodSpec> {
        if (!info.superClassElement.type.isDataBindingEpoxyModel(memoizer)) {
            return emptyList()
        }

        val bindVariablesMethod = MethodSpec.methodBuilder("setDataBindingVariables")
            .addAnnotation(Override::class.java)
            .addParameter(
                ClassName.get("androidx.databinding", "ViewDataBinding"),
                "binding"
            )
            .addModifiers(Modifier.PROTECTED)
            .returns(TypeName.VOID)
            .build()

        // If the base method is already implemented don't bother checking for the payload method
        if (implementsMethod(
                info.superClassElement,
                bindVariablesMethod,
                environment
            )
        ) {
            return emptyList()
        }

        val generatedModelClass = info.generatedName

        val moduleName = (info as? DataBindingModelInfo)?.moduleName
            ?: dataBindingModuleLookup.getModuleName(info.superClassElement)

        val baseMethodBuilder = bindVariablesMethod.toBuilder()

        val payloadMethodBuilder = bindVariablesMethod
            .toBuilder()
            .addParameter(ClassNames.EPOXY_MODEL_UNTYPED, "previousModel")
            .beginControlFlow(
                "if (!(previousModel instanceof \$T))",
                generatedModelClass
            )
            .addStatement("setDataBindingVariables(binding)")
            .addStatement("return")
            .endControlFlow()
            .addStatement(
                "\$T that = (\$T) previousModel", generatedModelClass,
                generatedModelClass
            )

        val brClass = ClassName.get(moduleName, "BR")
        val validateAttributes = configManager.shouldValidateModelUsage()
        for (attribute in info.attributeInfo) {
            val attrName = attribute.fieldName
            val setVariableBlock = CodeBlock.of(
                "binding.setVariable(\$T.\$L, \$L)", brClass,
                attrName, attribute.getterCode()
            )

            if (validateAttributes) {
                // The setVariable method returns false if the variable id was not found in the
                // layout. We can warn the user about this if they have model validations turned on,
                // otherwise it fails silently.
                baseMethodBuilder
                    .beginControlFlow("if (!\$L)", setVariableBlock)
                    .addStatement(
                        "throw new \$T(\"The attribute \$L was defined in your data binding " +
                            "model (\$L) but " + "a data variable of that name was not found in " +
                            "the layout.\")",
                        IllegalStateException::class.java, attrName,
                        info.superClassName
                    )
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
    private fun findSuperClassWithClassAnnotation(classElement: XTypeElement): XTypeElement? {
        if (!classElement.isEpoxyModel(memoizer)) {
            return null
        }

        val annotation = classElement.getAnnotation(EpoxyModelClass::class)
            // This is an error. The model must have an EpoxyModelClass annotation
            // since getDefaultLayout is not implemented
            ?: return null

        val layoutRes: Int
        try {
            layoutRes = annotation.value.layout
        } catch (e: AnnotationTypeMismatchException) {
            logger.logError(
                "Invalid layout value in %s annotation. (class: %s). %s: %s",
                EpoxyModelClass::class.java,
                classElement.name,
                e.javaClass.simpleName,
                e.message ?: ""
            )
            return null
        }

        if (layoutRes != 0) {
            return classElement
        }

        // This model did not specify a layout in its EpoxyModelClass annotation,
        // but its superclass might
        classElement.superType?.typeElement
            ?.let { superClass ->
                findSuperClassWithClassAnnotation(superClass)
            }
            ?.let {
                return it
            }

        logger.logError(
            classElement,
            "Model must specify a valid layout resource in the %s annotation. " +
                "(class: %s)",
            EpoxyModelClass::class.java.simpleName,
            classElement.name
        )

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

        for (attr in modelInfo.attributeInfo) {
            if (attr is ViewAttributeInfo && attr.generateStringOverloads) {
                methods.addAll(StringOverloadWriter(modelInfo, attr, configManager).buildMethods())
            } else {
                if (attr.isViewClickListener || attr.isViewLongClickListener) {
                    methods.add(generateSetClickModelListener(modelInfo, attr))
                }

                if (attr.isViewCheckedChangeListener) {
                    methods.add(generateSetCheckedChangeModelListener(modelInfo, attr))
                }

                if (attr.generateSetter && !attr.hasFinalModifier) {
                    methods.add(generateSetter(modelInfo, attr))
                }

                if (attr.generateGetter && !configManager.disableGenerateGetters(modelInfo)) {
                    methods.add(generateGetter(modelInfo, attr))
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

        val clickListenerType = if (attribute.isViewLongClickListener)
            getModelLongClickListenerType(classInfo)
        else
            getModelClickListenerType(classInfo)

        val param = ParameterSpec.builder(clickListenerType, attributeName, FINAL)
            .addAnnotations(attribute.setterAnnotations).build()

        val builder = MethodSpec.methodBuilder(attributeName)
            .addJavadoc(
                "Set a click listener that will provide the parent view, model, and adapter " +
                    "position of the clicked view. This will clear the normal " +
                    "View.OnClickListener if one has been set"
            )
            .addModifiers(PUBLIC)
            .returns(classInfo.parameterizedGeneratedName)
            .addParameter(param)

        setBitSetIfNeeded(classInfo, attribute, builder)

        val wrapperClickListenerConstructor = CodeBlock.of(
            "new \$T(\$L)",
            ClassNames.EPOXY_WRAPPED_LISTENER,
            param.name
        )

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

    private fun generateSetCheckedChangeModelListener(
        classInfo: GeneratedModelInfo,
        attribute: AttributeInfo
    ): MethodSpec {
        val attributeName = attribute.generatedSetterName()
        val checkedListenerType = getModelCheckedChangeListenerType(classInfo)

        val param = ParameterSpec.builder(checkedListenerType, attributeName, FINAL)
            .addAnnotations(attribute.setterAnnotations).build()

        val builder = MethodSpec.methodBuilder(attributeName)
            .addJavadoc(
                "Set a checked change listener that will provide the parent view, model, value, and adapter " +
                    "position of the checked view. This will clear the normal " +
                    "CompoundButton.OnCheckedChangeListener if one has been set"
            )
            .addModifiers(PUBLIC)
            .returns(classInfo.parameterizedGeneratedName)
            .addParameter(param)

        setBitSetIfNeeded(classInfo, attribute, builder)

        val wrapperCheckedListenerConstructor = CodeBlock.of(
            "new \$T(\$L)",
            ClassNames.EPOXY_WRAPPED_CHECKED_LISTENER,
            param.name
        )

        addOnMutationCall(builder)
            .beginControlFlow("if (\$L == null)", attributeName)
            .addStatement(attribute.setterCode(), "null")
            .endControlFlow()
            .beginControlFlow("else")
            .addStatement(attribute.setterCode(), wrapperCheckedListenerConstructor)
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
            ClassNames.EPOXY_ON_BIND_MODEL_LISTENER,
            modelBindListenerFieldName()
        )
        addStatement("return false")
        endControlFlow()

        startNotEqualsControlFlow(
            this,
            false,
            ClassNames.EPOXY_ON_UNBIND_MODEL_LISTENER,
            modelUnbindListenerFieldName()
        )
        addStatement("return false")
        endControlFlow()

        startNotEqualsControlFlow(
            this,
            false,
            ClassNames.EPOXY_ON_VISIBILITY_STATE_MODEL_LISTENER,
            modelVisibilityStateChangedListenerFieldName()
        )
        addStatement("return false")
        endControlFlow()

        startNotEqualsControlFlow(
            this,
            false,
            ClassNames.EPOXY_ON_VISIBILITY_MODEL_LISTENER,
            modelVisibilityChangedListenerFieldName()
        )
        addStatement("return false")
        endControlFlow()

        for (attributeInfo in helperClass.attributeInfo) {
            val type = attributeInfo.typeName

            if (!attributeInfo.useInHash && type.isPrimitive) {
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
        addStatement("int $HASH_CODE_RESULT_PROPERTY = super.hashCode()")

        addHashCodeLineForType(
            this,
            false,
            ClassNames.EPOXY_ON_BIND_MODEL_LISTENER,
            modelBindListenerFieldName()
        )

        addHashCodeLineForType(
            this,
            false,
            ClassNames.EPOXY_ON_UNBIND_MODEL_LISTENER,
            modelUnbindListenerFieldName()
        )

        addHashCodeLineForType(
            this,
            false,
            ClassNames.EPOXY_ON_VISIBILITY_STATE_MODEL_LISTENER,
            modelVisibilityStateChangedListenerFieldName()
        )

        addHashCodeLineForType(
            this,
            false,
            ClassNames.EPOXY_ON_VISIBILITY_MODEL_LISTENER,
            modelVisibilityChangedListenerFieldName()
        )

        for (attributeInfo in helperClass.attributeInfo) {
            if (!attributeInfo.useInHash) {
                continue
            }
            if (attributeInfo.typeName === DOUBLE) {
                addStatement("long temp")
                break
            }
        }

        for (attributeInfo in helperClass.attributeInfo) {
            val type = attributeInfo.typeName

            if (!attributeInfo.useInHash && type.isPrimitive) {
                continue
            }

            addHashCodeLineForType(
                this, attributeInfo.useInHash, type,
                attributeInfo.getterCode()
            )
        }

        addStatement("return $HASH_CODE_RESULT_PROPERTY")
    }

    private fun generateToString(helperClass: GeneratedModelInfo) = buildMethod("toString") {
        addAnnotation(Override::class.java)
        addModifiers(PUBLIC)
        returns(String::class.java)

        val sb = StringBuilder()
        sb.append(String.format("\"%s{\" +\n", helperClass.generatedName.simpleName()))

        var first = true
        for (attributeInfo in helperClass.attributeInfo) {
            if (attributeInfo.doNotUseInToString) {
                continue
            }

            val attributeName = attributeInfo.fieldName
            if (first) {
                sb.append(
                    String.format(
                        "\"%s=\" + %s +\n", attributeName,
                        attributeInfo.getterCode()
                    )
                )
                first = false
            } else {
                sb.append(
                    String.format(
                        "\", %s=\" + %s +\n", attributeName,
                        attributeInfo.getterCode()
                    )
                )
            }
        }

        sb.append("\"}\" + super.toString()")

        addStatement("return \$L", sb.toString())
    }

    private fun generateGetter(modelInfo: GeneratedModelInfo, data: AttributeInfo): MethodSpec {
        val getterName = data.generatedGetterName(
            isOverload = modelInfo.isOverload(data)
        )
        return buildMethod(getterName) {
            addModifiers(PUBLIC)
            returns(data.typeName)
            addAnnotations(data.getterAnnotations)
            addStatement("return \$L", data.superGetterCode())
        }
    }

    private fun generateSetter(
        modelInfo: GeneratedModelInfo,
        attribute: AttributeInfo
    ): MethodSpec {
        val attributeName = attribute.fieldName
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
                    .addAnnotations(attribute.setterAnnotations).build()
            )
        }

        if (attribute.javaDoc != null) {
            builder.addJavadoc(attribute.javaDoc)
        }

        if (!hasMultipleParams) {
            addParameterNullCheckIfNeeded(configManager, attribute, paramName, builder)
        }

        setBitSetIfNeeded(modelInfo, attribute, builder)

        modelInfo.otherAttributesInGroup(attribute)

        for (overload in modelInfo.otherAttributesInGroup(attribute)) {
            if (shouldUseBitSet(modelInfo)) {
                builder.addStatement(
                    "\$L.clear(\$L)", ATTRIBUTES_BITSET_FIELD_NAME,
                    attributeIndex(modelInfo, overload)
                )
            }

            builder.addStatement(
                overload.setterCode(),
                if (overload.codeToSetDefault.isPresent)
                    overload.codeToSetDefault.value()
                else
                    Utils.getDefaultValue(overload.typeName)
            )
        }

        addOnMutationCall(builder)
            .addStatement(
                attribute.setterCode(),
                if (hasMultipleParams)
                    (attribute as MultiParamAttribute).valueToSetOnAttribute
                else
                    paramName
            )

        // Call the super setter if it exists.
        // No need to do this if the attribute is private since we already called the super setter
        // to set it
        if (!attribute.isPrivate && attribute.hasSuperSetter) {
            if (hasMultipleParams) {
                logger
                    .logError(
                        "Multi params not supported for methods that call super (%s)",
                        attribute
                    )
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
        addStatement("\$L = null", modelVisibilityStateChangedListenerFieldName())
        addStatement("\$L = null", modelVisibilityChangedListenerFieldName())

        if (shouldUseBitSet(helperClass)) {
            addStatement("\$L.clear()", ATTRIBUTES_BITSET_FIELD_NAME)
        }

        helperClass.attributeInfo
            .filterNot { it.hasFinalModifier }
            .forEach {
                addStatement(
                    it.setterCode(),
                    if (it.codeToSetDefault.isPresent)
                        it.codeToSetDefault.value()
                    else
                        Utils.getDefaultValue(it.typeName)
                )
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

    /**
     * If the modelfactory module is present, and if this model supports it, this generates a static
     * method whose purpose is to create a new model based on a typed mapping of property names to
     * values provided via the ModelProperties interface. Notably, this generated method makes it
     * easy to create models from JSON and various other data formats.
     */
    private fun addFromPropertiesMethodIfNeeded(
        classBuilder: TypeSpec.Builder,
        modelInfo: GeneratedModelInfo
    ) {
        // The epoxy-modelfactory module must be present to enable this functionality
        if (!environment.isTypeLoaded(EPOXY_MODEL_PROPERTIES)) {
            return
        }

        // Models that don't have an empty constructor are not supported because there would be no
        // clear way to create new instances
        if (!modelInfo.hasEmptyConstructor()) {
            return
        }

        val attributeInfoConditions = listOf(
            AttributeInfo::isBoolean,
            AttributeInfo::isCharSequenceOrString,
            AttributeInfo::isDouble,
            AttributeInfo::isDrawableRes,
            AttributeInfo::isEpoxyModelList,
            AttributeInfo::isInt,
            AttributeInfo::isLong,
            AttributeInfo::isRawRes,
            AttributeInfo::isStringList,
            AttributeInfo::isStringAttributeData,
            AttributeInfo::isViewClickListener
        )
        val supportedAttributeInfo = if (modelInfo.attributeGroups.isNotEmpty()) {
            modelInfo.attributeInfo
                .groupBy { it.generatedSetterName() }
                .mapNotNull { (_, attributes) ->
                    // Amongst attributes with a supported type, we only include those that have a
                    // unique name. This means that multiple attributes with the same name and
                    // supported types are excluded, because we wouldn't know which one to use.
                    attributes.singleOrNull { attributeInfo ->
                        attributeInfoConditions.any { it.invoke(attributeInfo) }
                    }
                }
        } else {
            // attributeGroups is always empty for models not using @ModelView
            modelInfo.attributeInfo.filter { attributeInfo ->
                attributeInfoConditions.any { it.invoke(attributeInfo) }
            }
        }
            .filter { it.generateSetter && !it.hasFinalModifier }

        // If none of the properties are of a supported type the method isn't generated
        if (supportedAttributeInfo.isEmpty()) {
            return
        }

        val method = MethodSpec.methodBuilder("from").apply {
            addModifiers(PUBLIC, STATIC)
            addParameter(EPOXY_MODEL_PROPERTIES, "properties")
            returns(modelInfo.generatedName)

            addStatement(
                "\$T model = new \$T()",
                modelInfo.generatedName,
                modelInfo.generatedName
            )

            addStatement("model.id(properties.getId())")

            // Groups attributes that are part of the same attribute group, others will be by
            // themselves (at this point no two attributes should have the same generated setter
            // name).
            val supportedAttributeInfoGroups = supportedAttributeInfo.groupBy {
                if (it.groupKey.isNullOrEmpty()) {
                    it.generatedSetterName()
                } else {
                    it.groupKey
                }
            }

            for ((_, attributeInfoGroup) in supportedAttributeInfoGroups) {
                for ((index, attributeInfo) in attributeInfoGroup.withIndex()) {
                    val setterName = attributeInfo.generatedSetterName()
                    val isStartOfGroup = index == 0
                    val isEndOfGroup = index == attributeInfoGroup.size - 1

                    if (isStartOfGroup) {
                        beginControlFlow("if (properties.has(\$S))", setterName)
                    } else {
                        nextControlFlow("else if (properties.has(\$S))", setterName)
                    }

                    val jsonGetterName = when {
                        attributeInfo.isBoolean -> "getBoolean"
                        attributeInfo.isCharSequenceOrString ||
                            attributeInfo.isStringAttributeData -> "getString"
                        attributeInfo.isDouble -> "getDouble"
                        attributeInfo.isDrawableRes -> "getDrawableRes"
                        attributeInfo.isEpoxyModelList -> "getEpoxyModelList"
                        attributeInfo.isInt && !attributeInfo.isDrawableRes &&
                            !attributeInfo.isRawRes -> "getInt"
                        attributeInfo.isLong -> "getLong"
                        attributeInfo.isRawRes -> "getRawRes"
                        attributeInfo.isStringList -> "getStringList"
                        attributeInfo.isViewClickListener -> "getOnClickListener"
                        else -> {
                            logger.logError(
                                "Missing ModelProperties method for a supported attribute type."
                            )
                            null
                        }
                    }
                    jsonGetterName?.let {
                        addStatement(
                            "model.\$N(properties.\$N(\$S))",
                            setterName,
                            jsonGetterName,
                            setterName
                        )
                    }

                    if (isEndOfGroup) {
                        endControlFlow()
                    }
                }
            }

            if (modelInfo.isStyleable) {
                addStatement("\$T style = properties.getStyle()", PARIS_STYLE)
                beginControlFlow("if (style != null)")
                addStatement("model.style(style)")
                endControlFlow()
            }

            addStatement("return model")
        }.build()

        classBuilder.addMethod(method)
    }

    companion object {
        /**
         * Use this suffix on helper fields added to the generated class so that we don't clash with
         * fields on the original model.
         */
        private val GENERATED_FIELD_SUFFIX = "_epoxyGeneratedModel"
        private val CREATE_NEW_HOLDER_METHOD_NAME = "createNewHolder"
        private val GET_DEFAULT_LAYOUT_METHOD_NAME = "getDefaultLayout"
        val ATTRIBUTES_BITSET_FIELD_NAME = "assignedAttributes$GENERATED_FIELD_SUFFIX"

        fun shouldUseBitSet(info: GeneratedModelInfo): Boolean {
            return info.attributeInfo.any { shouldUseBitSet(info, it) }
        }

        // Avoid generating bitset code for attributes that don't need it.
        fun shouldUseBitSet(info: GeneratedModelInfo, attr: AttributeInfo): Boolean {
            if (info !is ModelViewInfo) return false

            // We use the bitset to validate if a required attribute had a value set on it
            if (attr.isRequired) return true

            // If the attribute is not generated then we assume that its parent model
            // handles its binding.
            if (!attr.isGenerated) return false

            // With default values we use the bitset when our bind code needs to conditionally
            // check which attribute value to set (either because its in a group or it has a default value)
            return ModelViewWriter.hasConditionals(info.attributeGroup(attr))
        }

        fun isAttributeSetCode(
            info: GeneratedModelInfo,
            attribute: AttributeInfo
        ) = CodeBlock.of(
            "\$L.get(\$L)", ATTRIBUTES_BITSET_FIELD_NAME,
            attributeIndex(info, attribute)
        )!!

        private fun attributeIndex(
            modelInfo: GeneratedModelInfo,
            attributeInfo: AttributeInfo
        ): Int {
            val index = modelInfo.attributeInfo.indexOf(attributeInfo)
            if (index < 0) {
                error("The attribute $attributeInfo does not exist in the model ${modelInfo.generatedName}")
            }
            return index
        }

        fun setBitSetIfNeeded(
            modelInfo: GeneratedModelInfo,
            attr: AttributeInfo,
            stringSetter: Builder
        ) {
            if (shouldUseBitSet(modelInfo, attr)) {
                stringSetter.addStatement(
                    "\$L.set(\$L)", ATTRIBUTES_BITSET_FIELD_NAME,
                    attributeIndex(modelInfo, attr)
                )
            }
        }

        fun addParameterNullCheckIfNeeded(
            configManager: ConfigManager,
            attr: AttributeInfo,
            paramName: String,
            builder: Builder
        ) {

            if (configManager.shouldValidateModelUsage() &&
                attr.hasSetNullability() &&
                !attr.isNullable()
            ) {

                builder.beginControlFlow("if (\$L == null)", paramName)
                    .addStatement(
                        "throw new \$T(\"\$L cannot be null\")",
                        IllegalArgumentException::class.java, paramName
                    )
                    .endControlFlow()
            }
        }

        fun startNotEqualsControlFlow(
            methodBuilder: MethodSpec.Builder,
            attribute: AttributeInfo
        ): MethodSpec.Builder {
            val attributeType = attribute.typeName
            val useHash = attributeType.isPrimitive || attribute.useInHash
            return startNotEqualsControlFlow(
                methodBuilder, useHash, attributeType,
                attribute.getterCode()
            )
        }

        fun startNotEqualsControlFlow(
            builder: Builder,
            useObjectHashCode: Boolean,
            type: TypeName,
            accessorCode: String
        ) = builder.beginControlFlow(
            "if (\$L)",
            notEqualsCodeBlock(useObjectHashCode, type, accessorCode)
        )

        fun notEqualsCodeBlock(attribute: AttributeInfo): CodeBlock {
            val attributeType = attribute.typeName
            val useHash = attributeType.isPrimitive || attribute.useInHash
            return notEqualsCodeBlock(useHash, attributeType, attribute.getterCode())
        }

        fun notEqualsCodeBlock(
            useObjectHashCode: Boolean,
            type: TypeName,
            accessorCode: String
        ): CodeBlock = if (useObjectHashCode) {
            when {
                type === FLOAT -> CodeBlock.of(
                    "(Float.compare(that.\$L, \$L) != 0)",
                    accessorCode, accessorCode
                )
                type === DOUBLE -> CodeBlock.of(
                    "(Double.compare(that.\$L, \$L) != 0)",
                    accessorCode, accessorCode
                )
                type.isPrimitive -> CodeBlock.of("(\$L != that.\$L)", accessorCode, accessorCode)
                type is ArrayTypeName -> CodeBlock.of(
                    "!\$T.equals(\$L, that.\$L)",
                    TypeName.get(Arrays::class.java),
                    accessorCode, accessorCode
                )
                else -> CodeBlock.of(
                    "(\$L != null ? !\$L.equals(that.\$L) : that.\$L != null)",
                    accessorCode, accessorCode, accessorCode, accessorCode
                )
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
                        BYTE, CHAR, SHORT, INT -> addStatement(
                            "$HASH_CODE_RESULT_PROPERTY = 31 * $HASH_CODE_RESULT_PROPERTY + \$L",
                            accessorCode
                        )
                        LONG -> addStatement(
                            "$HASH_CODE_RESULT_PROPERTY = 31 * $HASH_CODE_RESULT_PROPERTY + (int) (\$L ^ (\$L >>> 32))",
                            accessorCode,
                            accessorCode
                        )
                        FLOAT -> addStatement(
                            "$HASH_CODE_RESULT_PROPERTY = 31 * $HASH_CODE_RESULT_PROPERTY + (\$L != +0.0f " + "? " +
                                "Float.floatToIntBits(\$L) : 0)",
                            accessorCode, accessorCode
                        )
                        DOUBLE -> {
                            addStatement("temp = Double.doubleToLongBits(\$L)", accessorCode)
                            addStatement("$HASH_CODE_RESULT_PROPERTY = 31 * $HASH_CODE_RESULT_PROPERTY + (int) (temp ^ (temp >>> 32))")
                        }
                        BOOLEAN -> addStatement(
                            "$HASH_CODE_RESULT_PROPERTY = 31 * $HASH_CODE_RESULT_PROPERTY + (\$L ? 1 : 0)",
                            accessorCode
                        )
                        is ArrayTypeName -> addStatement(
                            "$HASH_CODE_RESULT_PROPERTY = 31 * $HASH_CODE_RESULT_PROPERTY + Arrays.hashCode(\$L)",
                            accessorCode
                        )
                        else -> addStatement(
                            "$HASH_CODE_RESULT_PROPERTY = 31 * $HASH_CODE_RESULT_PROPERTY + (\$L != null ? \$L.hashCode() : 0)",
                            accessorCode,
                            accessorCode
                        )
                    }
                } else {
                    addStatement(
                        "$HASH_CODE_RESULT_PROPERTY = 31 * $HASH_CODE_RESULT_PROPERTY + (\$L != null ? 1 : 0)",
                        accessorCode
                    )
                }
            }
        }

        fun addOnMutationCall(method: MethodSpec.Builder) = method.addStatement("onMutation()")!!

        fun modelImplementsBindWithDiff(
            clazz: XTypeElement,
            bindWithDiffMethod: MethodSpec,
            environment: XProcessingEnv
        ): Boolean {
            val methodOnClass = Utils.getMethodOnClass(
                clazz,
                bindWithDiffMethod,
                environment
            ) ?: return false

            if (methodOnClass.isAbstract()) {
                return false
            }

            val enclosingElement = methodOnClass.enclosingElement as XTypeElement

            // As long as the implementation is not on the base EpoxyModel we consider it a custom
            // implementation
            return enclosingElement.qualifiedName != Utils.UNTYPED_EPOXY_MODEL_TYPE
        }
    }
}

/**
 * Property name of the int property used to build the hashcode result.
 * An underscore is used to not clash with any attribute names a user might choose.
 */
private const val HASH_CODE_RESULT_PROPERTY = "_result"
