package com.airbnb.epoxy

import com.airbnb.epoxy.GeneratedModelWriter.BuilderHooks
import com.airbnb.epoxy.Utils.*
import com.squareup.javapoet.*
import com.squareup.javapoet.MethodSpec.Builder
import java.util.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.*
import javax.lang.model.element.Modifier.*
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import kotlin.reflect.KClass

// TODO: (eli_hart 6/6/17) consider binding base model after view so the model can override view
// behavior (like changing width dynamically or styles)
// TODO: (eli_hart 5/30/17) allow param counts > 0 in setters
// TODO: (eli_hart 5/23/17) Allow default values to be methods
internal class ModelViewProcessor(
        private val elements: Elements,
        private val types: Types,
        private val configManager: ConfigManager,

        private val errorLogger: ErrorLogger,
        private val modelWriter: GeneratedModelWriter, val layoutResourceProcessor: LayoutResourceProcessor
) {

    private val modelClassMap = LinkedHashMap<Element, ModelViewInfo>()

    fun process(
            roundEnv: RoundEnvironment,
            otherGeneratedModels: List<GeneratedModelInfo>
    ): Collection<GeneratedModelInfo> {

        modelClassMap.clear()

        processViewAnnotations(roundEnv)

        processSetterAnnotations(roundEnv)
        processResetAnnotations(roundEnv)
        processAfterBindAnnotations(roundEnv)

        updateViewsForInheritedViewAnnotations()

        // Group overloads after inheriting methods from super classes so those can be included in
        // the groups as well.
        groupOverloads()

        // Our code generation assumes that all attributes in a group are view attributes (and not
        // attributes inherited from a base model class), so this should be done after grouping
        // attributes, and these attributes should not be grouped.
        updatesViewsForInheritedBaseModelAttributes(otherGeneratedModels)

        writeJava()

        return modelClassMap.values
    }

    private fun processViewAnnotations(roundEnv: RoundEnvironment) {
        for (viewElement in roundEnv.getElementsAnnotatedWith(ModelView::class.java)) {
            try {
                if (!validateViewElement(viewElement)) {
                    continue
                }

                modelClassMap.put(viewElement,
                        ModelViewInfo(viewElement as TypeElement, types, elements, errorLogger,
                                configManager, layoutResourceProcessor))
            } catch (e: Exception) {
                errorLogger.logError(e, "Error creating model view info classes.")
            }

        }
    }

    private fun validateViewElement(viewElement: Element): Boolean {
        if (viewElement.kind != ElementKind.CLASS || viewElement !is TypeElement) {
            errorLogger.logError("${ModelView::class.java} annotations can only be on a class (element: ${viewElement.simpleName})")
            return false
        }

        val modifiers = viewElement.getModifiers()
        if (modifiers.contains(PRIVATE)) {
            errorLogger.logError(
                    "${ModelView::class.java} annotations must not be on private classes. (class: ${viewElement.getSimpleName()})")
            return false
        }

        // Nested classes must be static
        if (viewElement.nestingKind.isNested) {
            errorLogger.logError(
                    "Classes with ${ModelView::class.java} annotations cannot be nested. (class: ${viewElement.getSimpleName()})")
            return false
        }

        if (!isSubtypeOfType(viewElement.asType(), Utils.ANDROID_VIEW_TYPE)) {
            errorLogger.logError(
                    "Classes with ${ModelView::class.java} annotations must extend android.view.View. (class: ${viewElement.getSimpleName()})")
            return false
        }

        return true
    }

    private fun processSetterAnnotations(roundEnv: RoundEnvironment) {

        for (propAnnotation in listOf(ModelProp::class, TextProp::class, CallbackProp::class)) {
            for (propMethod in roundEnv.getElementsAnnotatedWith(propAnnotation.java)) {
                if (!validatePropElement(propMethod, propAnnotation)) {
                    continue
                }

                val info = getModelInfoForMethodElement(propMethod)
                if (info == null) {
                    errorLogger.logError("${propAnnotation.java.simpleName} annotation can only be used in classes annotated with ${ModelView::class.java.simpleName} (${propMethod.enclosingElement.simpleName}#${propMethod.simpleName})")
                    continue
                }

                info.addProp(propMethod as ExecutableElement)
            }
        }
    }

    private fun groupOverloads() {
        for (viewInfo in modelClassMap.values) {
            val attributeGroups = HashMap<String, MutableList<AttributeInfo>>()

            // Track which groups are created manually by the user via a group annotation param.
            // We use this to check that more than one setter is in the group, since otherwise it doesn't
            // make sense to have a group and there is likely a typo we can catch for them
            val customGroups = HashSet<String>()

            for (attributeInfo in viewInfo.attributeInfo) {
                val setterInfo = attributeInfo as ViewAttributeInfo

                var groupKey = notNull(setterInfo.groupKey)
                if (groupKey.isEmpty()) {
                    // Default to using the method name as the group name, so method overloads are grouped
                    // together by default
                    groupKey = setterInfo.viewSetterMethodName
                } else {
                    customGroups.add(groupKey)
                }

                var group: MutableList<AttributeInfo>? = attributeGroups[groupKey]
                if (group == null) {
                    group = ArrayList()
                    attributeGroups.put(groupKey, group)
                }

                group.add(attributeInfo)
            }

            for (customGroup in customGroups) {
                attributeGroups[customGroup]?.let {
                    if (it.size == 1) {
                        val attribute = it[0] as ViewAttributeInfo
                        errorLogger.logError(
                                "Only one setter was included in the custom group '$customGroup' at ${viewInfo.viewElement.simpleName}#${attribute.viewSetterMethodName}. Groups should have at least 2 setters.")
                    }
                }
            }

            for ((key, value) in attributeGroups) {
                try {
                    viewInfo.addAttributeGroup(key, value)
                } catch (e: EpoxyProcessorException) {
                    errorLogger.logError(e)
                }

            }
        }
    }

    private fun validatePropElement(methodElement: Element, propAnnotation: KClass<out Annotation>): Boolean =
            validateExecutableElement(methodElement, propAnnotation.java, 1)

    private fun validateExecutableElement(element: Element, annotationClass: Class<*>,
                                          paramCount: Int): Boolean {
        if (element !is ExecutableElement) {
            errorLogger.logError("%s annotations can only be on a method (element: %s)", annotationClass,
                    element.simpleName)
            return false
        }

        if (element.parameters.size != paramCount) {
            errorLogger.logError("Methods annotated with %s must have exactly %s parameter (method: %s)",
                    annotationClass, paramCount, element.getSimpleName())
            return false
        }

        val modifiers = element.getModifiers()
        if (modifiers.contains(STATIC) || modifiers.contains(PRIVATE)) {
            errorLogger.logError("Methods annotated with %s cannot be private or static (method: %s)",
                    annotationClass, element.getSimpleName())
            return false
        }

        return true
    }

    private fun processResetAnnotations(roundEnv: RoundEnvironment) {
        for (recycleMethod in roundEnv.getElementsAnnotatedWith(OnViewRecycled::class.java)) {
            if (!validateResetElement(recycleMethod)) {
                continue
            }

            val info = getModelInfoForMethodElement(recycleMethod)
            if (info == null) {
                errorLogger.logError("%s annotation can only be used in classes annotated with %s",
                        OnViewRecycled::class.java, ModelView::class.java)
                continue
            }

            info.addOnRecycleMethod(recycleMethod as ExecutableElement)
        }
    }

    private fun processAfterBindAnnotations(roundEnv: RoundEnvironment) {
        for (afterPropsMethod in roundEnv.getElementsAnnotatedWith(AfterPropsSet::class.java)) {
            if (!validateAfterPropsMethod(afterPropsMethod)) {
                continue
            }

            val info = getModelInfoForMethodElement(afterPropsMethod)
            if (info == null) {
                errorLogger.logError("%s annotation can only be used in classes annotated with %s",
                        AfterPropsSet::class.java, ModelView::class.java)
                continue
            }

            info.addAfterPropsSetMethod(afterPropsMethod as ExecutableElement)
        }
    }

    private fun validateAfterPropsMethod(resetMethod: Element): Boolean =
            validateExecutableElement(resetMethod, AfterPropsSet::class.java, 0)

    /** Include props and reset methods from super class views.  */
    private fun updateViewsForInheritedViewAnnotations() {
        for (view in modelClassMap.values) {
            val otherViews = HashSet(modelClassMap.values)
            otherViews.remove(view)

            for (otherView in otherViews) {
                if (!isSubtype(view.viewElement, otherView.viewElement, types)) {
                    continue
                }

                view.resetMethodNames.addAll(otherView.resetMethodNames)
                view.afterPropsSetMethodNames.addAll(otherView.afterPropsSetMethodNames)

                val samePackage = belongToTheSamePackage(view.viewElement, otherView.viewElement, elements)
                for (otherAttribute in otherView.attributeInfo) {
                    if (otherAttribute.packagePrivate && !samePackage) {
                        val otherSetterInfo = otherAttribute as ViewAttributeInfo
                        // It would be an unlikely case for someone to not want views to inherit superclass
                        // setters, so if they are package private and can't be inherited it is probably
                        // just accidental and we can point it out instead of silently excluding it
                        errorLogger.logError(
                                "View %s is in a different package then %s and cannot inherit its package " + "private setter %s",
                                view.viewElement.simpleName, otherView.viewElement.simpleName,
                                otherSetterInfo.viewSetterMethodName)
                    } else {
                        // We don't want the attribute from the super class replacing an attribute in the
                        // subclass if the subclass overrides it, since the subclass definition could include
                        // different annotation parameter settings.
                        view.addAttributeIfNotExists(otherAttribute)
                    }
                }
            }
        }
    }

    /**
     * If a view defines a base model that it's generated model should extend we need to check if that
     * base model has [com.airbnb.epoxy.EpoxyAttribute] fields and include those in our model if
     * so.
     */
    private fun updatesViewsForInheritedBaseModelAttributes(
            otherGeneratedModels: List<GeneratedModelInfo>
    ) {

        for (modelViewInfo in modelClassMap.values) {
            otherGeneratedModels
                    .filter {
                        isSubtype(modelViewInfo.superClassElement, it.superClassElement, types)
                    }
                    .forEach { modelViewInfo.addAttributes(it.attributeInfo) }
        }
    }

    private fun validateResetElement(resetMethod: Element): Boolean =
            validateExecutableElement(resetMethod, OnViewRecycled::class.java, 0)

    private fun writeJava() {
        for (modelInfo in modelClassMap.values) {
            try {
                modelWriter.generateClassForModel(modelInfo, object : BuilderHooks() {
                    internal override fun addToBindMethod(methodBuilder: Builder, boundObjectParam: ParameterSpec): Boolean {

                        for (attributeGroup in modelInfo.attributeGroups) {
                            val attrCount = attributeGroup.attributes.size
                            if (attrCount == 1) {
                                val viewAttribute = attributeGroup.attributes[0] as ViewAttributeInfo
                                methodBuilder
                                        .addCode(buildCodeBlockToSetAttribute(boundObjectParam, viewAttribute))
                            } else {
                                for (i in 0 until attrCount) {
                                    val viewAttribute = attributeGroup.attributes[i] as ViewAttributeInfo

                                    if (i == 0) {
                                        methodBuilder.beginControlFlow("if (\$L)",
                                                GeneratedModelWriter.isAttributeSetCode(modelInfo, viewAttribute))
                                    } else if (i == attrCount - 1 && attributeGroup.isRequired) {
                                        methodBuilder.beginControlFlow("else")
                                    } else {
                                        methodBuilder.beginControlFlow("else if (\$L)",
                                                GeneratedModelWriter.isAttributeSetCode(modelInfo, viewAttribute))
                                    }

                                    methodBuilder
                                            .addCode(buildCodeBlockToSetAttribute(boundObjectParam, viewAttribute))
                                            .endControlFlow()
                                }

                                if (!attributeGroup.isRequired) {
                                    val defaultAttribute = attributeGroup.defaultAttribute as ViewAttributeInfo

                                    methodBuilder.beginControlFlow("else")
                                            .addStatement("\$L.\$L(\$L)", boundObjectParam.name,
                                                    defaultAttribute.viewSetterMethodName,
                                                    defaultAttribute.codeToSetDefault.value())
                                            .endControlFlow()
                                }
                            }
                        }

                        return true
                    }

                    internal override fun addToBindWithDiffMethod(methodBuilder: Builder, boundObjectParam: ParameterSpec,
                                                                  previousModelParam: ParameterSpec): Boolean {

                        val generatedModelClass = modelInfo.generatedClassName
                        methodBuilder
                                .beginControlFlow("if (!(\$L instanceof \$T))", previousModelParam.name,
                                        generatedModelClass)
                                .addStatement("bind(\$L)", boundObjectParam.name)
                                .addStatement("return")
                                .endControlFlow()
                                .addStatement("\$T that = (\$T) previousModel", generatedModelClass,
                                        generatedModelClass)

                        // We want to make sure the base model has its bind method called as well. Since the
                        // user can provide a custom base class we aren't sure if it implements diff binding.
                        // If so we should call it, but if not, calling it would invoke the default
                        // EpoxyModel implementation which calls normal "bind". Doing that would force a full
                        // bind!!! So we mustn't do that. So, we only call the super diff binding if we think
                        // it's a custom implementation.
                        if (modelImplementsBindWithDiff(modelInfo.superClassElement, methodBuilder.build())) {
                            methodBuilder.addStatement("super.bind(\$L, \$L)", boundObjectParam.name,
                                    previousModelParam.name)
                        } else {
                            methodBuilder.addStatement("super.bind(\$L)", boundObjectParam.name)
                        }

                        for (attributeGroup in modelInfo.attributeGroups) {
                            methodBuilder.addCode("\n")

                            if (attributeGroup.attributes.size == 1) {
                                val attributeInfo = attributeGroup.attributes[0]

                                if (attributeInfo is ViewAttributeInfo && attributeInfo.generateStringOverloads) {
                                    methodBuilder
                                            .beginControlFlow("if (!\$L.equals(that.\$L))", attributeInfo.getterCode(),
                                                    attributeInfo.getterCode())
                                } else {
                                    GeneratedModelWriter.startNotEqualsControlFlow(methodBuilder, attributeInfo)
                                }

                                methodBuilder.addCode(buildCodeBlockToSetAttribute(boundObjectParam,
                                        attributeInfo as ViewAttributeInfo))
                                        .endControlFlow()
                            } else {
                                methodBuilder.beginControlFlow("if (\$L.equals(that.\$L))",
                                        GeneratedModelWriter.ATTRIBUTES_BITSET_FIELD_NAME,
                                        GeneratedModelWriter.ATTRIBUTES_BITSET_FIELD_NAME)

                                var firstAttribute = true
                                for (attribute in attributeGroup.attributes) {
                                    if (!firstAttribute) {
                                        methodBuilder.addCode(" else ")
                                    }
                                    firstAttribute = false

                                    methodBuilder.beginControlFlow("if (\$L)",
                                            GeneratedModelWriter.isAttributeSetCode(modelInfo, attribute))

                                    GeneratedModelWriter.startNotEqualsControlFlow(methodBuilder, attribute)
                                            .addCode(buildCodeBlockToSetAttribute(boundObjectParam,
                                                    attribute as ViewAttributeInfo))
                                            .endControlFlow()
                                            .endControlFlow()
                                }

                                methodBuilder.endControlFlow()
                                        .beginControlFlow("else")

                                firstAttribute = true
                                for (attribute in attributeGroup.attributes) {
                                    if (!firstAttribute) {
                                        methodBuilder.addCode(" else ")
                                    }
                                    firstAttribute = false

                                    methodBuilder.beginControlFlow("if (\$L && !that.\$L)",
                                            GeneratedModelWriter.isAttributeSetCode(modelInfo, attribute),
                                            GeneratedModelWriter.isAttributeSetCode(modelInfo, attribute))
                                            .addCode(buildCodeBlockToSetAttribute(boundObjectParam,
                                                    attribute as ViewAttributeInfo))
                                            .endControlFlow()
                                }

                                if (!attributeGroup.isRequired) {
                                    val defaultAttribute = attributeGroup.defaultAttribute as ViewAttributeInfo

                                    methodBuilder.beginControlFlow("else")
                                            .addStatement("\$L.\$L(\$L)", boundObjectParam.name,
                                                    defaultAttribute.viewSetterMethodName,
                                                    defaultAttribute.codeToSetDefault.value())
                                            .endControlFlow()
                                }

                                methodBuilder.endControlFlow()
                            }
                        }

                        return true
                    }

                    override fun addToHandlePostBindMethod(postBindBuilder: Builder,
                                                           boundObjectParam: ParameterSpec) {

                        addAfterPropsAddedMethodsToBuilder(postBindBuilder, modelInfo, boundObjectParam)
                    }

                    internal override fun addToUnbindMethod(unbindBuilder: MethodSpec.Builder, unbindParamName: String) {
                        modelInfo.viewAttributes
                                .filter { it.resetWithNull }
                                .forEach {
                                    unbindBuilder.addStatement("\$L.\$L(null)", unbindParamName,
                                            it.viewSetterMethodName)
                                }

                        addResetMethodsToBuilder(unbindBuilder, modelInfo, unbindParamName)
                    }

                    internal override fun beforeFinalBuild(builder: TypeSpec.Builder) {
                        if (modelInfo.saveViewState) {
                            builder.addMethod(buildSaveStateMethod())
                        }

                        if (modelInfo.fullSpanSize) {
                            builder.addMethod(buildFullSpanSizeMethod())
                        }
                    }
                })
            } catch (e: Exception) {
                errorLogger.logError(EpoxyProcessorException(e, "Error generating model view classes"))
            }

        }
    }

    fun modelImplementsBindWithDiff(clazz: TypeElement, bindWithDiffMethod: MethodSpec): Boolean {
        val methodOnClass = getMethodOnClass(clazz, bindWithDiffMethod, types, elements) ?: return false

        val modifiers = methodOnClass.modifiers
        if (modifiers.contains(Modifier.ABSTRACT)) {
            return false
        }

        val enclosingElement = methodOnClass.enclosingElement as TypeElement

        // As long as the implementation is not on the base EpoxyModel we consider it a custom
        // implementation
        return enclosingElement.qualifiedName.toString() != UNTYPED_EPOXY_MODEL_TYPE
    }

    private fun buildCodeBlockToSetAttribute(boundObjectParam: ParameterSpec,
                                             viewAttribute: ViewAttributeInfo): CodeBlock {
        return CodeBlock.of("\$L.\$L(\$L);\n", boundObjectParam.name,
                viewAttribute.viewSetterMethodName,
                getValueToSetOnView(viewAttribute, boundObjectParam))
    }

    private fun getValueToSetOnView(viewAttribute: ViewAttributeInfo,
                                    boundObjectParam: ParameterSpec): String {
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
                .addModifiers(PUBLIC)
                .addStatement("return true")
                .build()
    }

    private fun buildFullSpanSizeMethod(): MethodSpec {
        return MethodSpec.methodBuilder("getSpanSize")
                .addAnnotation(Override::class.java)
                .returns(TypeName.INT)
                .addModifiers(PUBLIC)
                .addParameter(TypeName.INT, "totalSpanCount")
                .addParameter(TypeName.INT, "position")
                .addParameter(TypeName.INT, "itemCount")
                .addStatement("return totalSpanCount")
                .build()
    }

    private fun addResetMethodsToBuilder(builder: Builder, modelViewInfo: ModelViewInfo,
                                         unbindParamName: String) {
        for (methodName in modelViewInfo.getResetMethodNames()) {
            builder.addStatement("$unbindParamName.$methodName()")
        }
    }

    private fun addAfterPropsAddedMethodsToBuilder(methodBuilder: Builder, modelInfo: ModelViewInfo,
                                                   boundObjectParam: ParameterSpec) {
        for (methodName in modelInfo.getAfterPropsSetMethodNames()) {
            methodBuilder.addStatement(boundObjectParam.name + "." + methodName + "()")
        }
    }

    private fun getModelInfoForMethodElement(element: Element): ModelViewInfo? =
            element.enclosingElement?.let { modelClassMap[it] }
}
