package com.airbnb.epoxy.processor

import com.airbnb.epoxy.AfterPropsSet
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.OnViewRecycled
import com.airbnb.epoxy.OnVisibilityChanged
import com.airbnb.epoxy.OnVisibilityStateChanged
import com.airbnb.epoxy.TextProp
import com.airbnb.epoxy.processor.Utils.isSubtypeOfType
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import java.util.HashMap
import java.util.HashSet
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier.PRIVATE
import javax.lang.model.element.Modifier.STATIC
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeKind
import kotlin.reflect.KClass

@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
class ModelViewProcessor : BaseProcessorWithPackageConfigs() {

    override val usesPackageEpoxyConfig: Boolean = false
    override val usesModelViewConfig: Boolean = true

    private val modelClassMap = ConcurrentHashMap<Element, ModelViewInfo>()
    private val styleableModelsToWrite = mutableListOf<ModelViewInfo>()

    override fun additionalSupportedAnnotations(): List<KClass<*>> = listOf(
        ModelView::class,
        TextProp::class,
        CallbackProp::class
    )

    override suspend fun processRound(roundEnv: RoundEnvironment, roundNumber: Int) {
        super.processRound(roundEnv, roundNumber)
        processViewAnnotations(roundEnv)

        // Avoid doing the work to look up the rest of the annotations in model view classes
        // if no new  model view classes were found.
        if (modelClassMap.isNotEmpty()) {
            processSetterAnnotations(roundEnv)
            processResetAnnotations(roundEnv)
            processVisibilityStateChangedAnnotations(roundEnv)
            processVisibilityChangedAnnotations(roundEnv)
            processAfterBindAnnotations(roundEnv)

            updateViewsForInheritedViewAnnotations()

            // Group overloads after inheriting methods from super classes so those can be included in
            // the groups as well.
            groupOverloads()

            // Up until here our code generation has assumed that that all attributes in a group are
            // view attributes (and not attributes inherited from a base model class), so this should be
            // done after grouping attributes, and these attributes should not be grouped.
            // No code to bind these attributes is generated, as it is assumed that the original model
            // handles its own bind (also we can't know how to bind these).
            updatesViewsForInheritedBaseModelAttributes()
            addStyleAttributes()
        }

        // This may write previously generated models that were waiting for their style builder
        // to be generated.
        writeJava()

        generatedModels.addAll(modelClassMap.values)
        modelClassMap.clear()
    }

    private suspend fun processViewAnnotations(roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(ModelView::class)
            .forEach("processViewAnnotations") { viewElement ->
                if (!validateViewElement(viewElement)) {
                    return@forEach
                }

                modelClassMap[viewElement] = ModelViewInfo(
                    viewElement as TypeElement,
                    typeUtils,
                    elementUtils,
                    logger,
                    configManager,
                    resourceProcessor,
                    memoizer
                )
            }
    }

    private fun validateViewElement(viewElement: Element): Boolean {
        if (viewElement.kind != ElementKind.CLASS || viewElement !is TypeElement) {
            logger.logError(
                "${ModelView::class.java} annotations can only be on a class " +
                    "(element: ${viewElement.simpleName})"
            )
            return false
        }

        val modifiers = viewElement.getModifiers()
        if (modifiers.contains(PRIVATE)) {
            logger.logError(
                "${ModelView::class.java} annotations must not be on private classes. " +
                    "(class: ${viewElement.getSimpleName()})"
            )
            return false
        }

        // Nested classes must be static
        if (viewElement.nestingKind.isNested) {
            logger.logError(
                "Classes with ${ModelView::class.java} annotations cannot be nested. " +
                    "(class: ${viewElement.getSimpleName()})"
            )
            return false
        }

        if (!isSubtypeOfType(viewElement.asType(), Utils.ANDROID_VIEW_TYPE)) {
            logger.logError(
                "Classes with ${ModelView::class.java} annotations must extend " +
                    "android.view.View. (class: ${viewElement.getSimpleName()})"
            )
            return false
        }

        return true
    }

    private suspend fun processSetterAnnotations(roundEnv: RoundEnvironment) {

        for (propAnnotation in modelPropAnnotations) {
            roundEnv.getElementsAnnotatedWith(propAnnotation)
                .map("Process ${propAnnotation.simpleName}") { prop ->
                    // Interfaces can use model property annotations freely, they will be processed if
                    // and when implementors of that interface are processed. This is particularly
                    // useful for Kotlin delegation where the model view class may not be overriding
                    // the interface properties directly, and so doesn't have an opportunity to annotate
                    // them with Epoxy model property annotations.
                    if (prop.enclosingElement.kind == ElementKind.INTERFACE) {
                        return@map null
                    }

                    val info = getModelInfoForPropElement(prop)
                    if (info == null) {
                        logger.logError(
                            "${propAnnotation.simpleName} annotation can only be used in classes " +
                                "annotated with ${ModelView::class.java.simpleName} " +
                                "(${prop.enclosingElement.simpleName}#${prop.simpleName})"
                        )
                        return@map null
                    }

                    // JvmOverloads is used on properties with default arguments, which we support.
                    // However, the generated no arg version of the function will also have the
                    // @ModelProp annotation so we need to ignore it when it is processed.
                    // However, the JvmOverloads annotation is removed in the java class so we need
                    // to manually look for a valid overload function.
                    if (prop is ExecutableElement &&
                        prop.parametersThreadSafe.isEmpty() &&
                        info.viewElement.findOverload(
                            prop,
                            1
                        )?.hasAnyAnnotation(modelPropAnnotations) == true
                    ) {
                        return@map null
                    }

                    if (!validatePropElement(prop, propAnnotation.java)) {
                        return@map null
                    }

                    info.buildProp(prop) to info
                }.forEach { (viewProp, modelInfo) ->
                    // This is done synchronously after the parallel prop building so that we
                    // have all props in the order they are listed in the view.
                    // This keeps a consistent ordering despite the parallel execution, which is necessary
                    // for consistent generated code as well as consistent prop binding order (which
                    // people are not supposed to rely on, but inevitably do, and we want to avoid breaking
                    // that by changing the ordering).
                    modelInfo.addAttribute(viewProp)
                }
        }
    }

    private suspend fun groupOverloads() {
        modelClassMap.values.forEach("groupOverloads") { viewInfo ->
            val attributeGroups = HashMap<String, MutableList<AttributeInfo>>()

            // Track which groups are created manually by the user via a group annotation param.
            // We use this to check that more than one setter is in the group, since otherwise it
            // doesn't make sense to have a group and there is likely a typo we can catch for them
            val customGroups = HashSet<String>()

            for (attributeInfo in viewInfo.attributeInfo) {
                val setterInfo = attributeInfo as ViewAttributeInfo

                var groupKey = setterInfo.groupKey!!
                if (groupKey.isEmpty()) {
                    // Default to using the method name as the group name, so method overloads are
                    // grouped together by default
                    groupKey = setterInfo.viewAttributeName
                } else {
                    customGroups.add(groupKey)
                }

                attributeGroups
                    .getOrPut(groupKey) { mutableListOf() }
                    .add(attributeInfo)
            }

            for (customGroup in customGroups) {
                attributeGroups[customGroup]?.let {
                    if (it.size == 1) {
                        val attribute = it[0] as ViewAttributeInfo
                        logger.logError(
                            "Only one setter was included in the custom group " +
                                "'$customGroup' at ${viewInfo.viewElement.simpleName}#" +
                                "${attribute.viewAttributeName}. Groups should have at " +
                                "least 2 setters."
                        )
                    }
                }
            }

            for ((groupKey, groupAttributes) in attributeGroups) {
                viewInfo.addAttributeGroup(groupKey, groupAttributes)
            }
        }
    }

    private fun validatePropElement(
        prop: Element,
        propAnnotation: Class<out Annotation>
    ): Boolean {
        return when (prop) {
            is ExecutableElement -> validateExecutableElement(prop, propAnnotation, 1)
            is VariableElement -> validateVariableElement(prop, propAnnotation)
            else -> {
                logger.logError(
                    "%s annotations can only be on a method or a field(element: %s)",
                    propAnnotation,
                    prop.simpleName
                )
                return false
            }
        }
    }

    private fun validateVariableElement(field: Element, annotationClass: Class<*>): Boolean {
        val isValidField = field.kind == ElementKind.FIELD &&
            !field.modifiers.contains(PRIVATE) &&
            !field.modifiers.contains(STATIC)

        if (!isValidField) {
            logger.logError(
                "Field annotated with %s must not be static or private (field: %s)",
                annotationClass, field
            )
        }
        return isValidField
    }

    private fun validateExecutableElement(
        element: Element,
        annotationClass: Class<*>,
        paramCount: Int,
        checkTypeParameters: List<TypeKind>? = null
    ): Boolean {
        if (element !is ExecutableElement) {
            logger.logError(
                "%s annotations can only be on a method (element: %s)",
                annotationClass::class.java.simpleName,
                element.simpleName
            )
            return false
        }

        val parameters = element.parametersThreadSafe
        if (parameters.size != paramCount) {
            logger.logError(
                "Methods annotated with %s must have exactly %s parameter (method: %s)",
                annotationClass::class.java.simpleName, paramCount, element.getSimpleName()
            )
            return false
        }

        checkTypeParameters?.let { expectedTypeParameters ->
            // Check also the parameter types
            var hasErrors = false
            parameters.forEachIndexed { i, parameter ->
                hasErrors = hasErrors || parameter.asType().kind != expectedTypeParameters[i]
            }
            if (hasErrors) {
                logger.logError(
                    "Methods annotated with %s must have parameter types %s, " +
                        "found: %s (method: %s)",
                    annotationClass::class.java.simpleName,
                    expectedTypeParameters,
                    parameters.map { it.asType().kind },
                    element.simpleName
                )
            }
        }

        val modifiers = element.modifiers
        if (modifiers.contains(STATIC) || modifiers.contains(PRIVATE)) {
            logger.logError(
                "Methods annotated with %s cannot be private or static (method: %s)",
                annotationClass::class.java.simpleName, element.getSimpleName()
            )
            return false
        }

        return true
    }

    private suspend fun processResetAnnotations(roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(OnViewRecycled::class)
            .map("processResetAnnotations") { recycleMethod ->
                if (!validateResetElement(recycleMethod)) {
                    return@map null
                }

                val info = getModelInfoForPropElement(recycleMethod)
                if (info == null) {
                    logger.logError(
                        "%s annotation can only be used in classes annotated with %s",
                        OnViewRecycled::class.java, ModelView::class.java
                    )
                    return@map null
                }

                recycleMethod.simpleName.toString() to info
            }.forEach { (methodName, modelInfo) ->
                // Do this after, synchronously, to preserve function ordering in the view.
                // If there are multiple functions with this annotation this allows them
                // to be called in predictable order from top to bottom of the class, which
                // some users may depend on.
                modelInfo.addOnRecycleMethod(methodName)
            }
    }

    private suspend fun processVisibilityStateChangedAnnotations(roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(OnVisibilityStateChanged::class)
            .map("processVisibilityStateChangedAnnotations") { visibilityMethod ->
                if (!validateVisibilityStateChangedElement(visibilityMethod)) {
                    return@map null
                }

                val info = getModelInfoForPropElement(visibilityMethod)
                if (info == null) {
                    logger.logError(
                        "%s annotation can only be used in classes annotated with %s",
                        OnVisibilityStateChanged::class.java, ModelView::class.java
                    )
                    return@map null
                }

                visibilityMethod.simpleName.toString() to info
            }.forEach { (methodName, modelInfo) ->
                // Do this after, synchronously, to preserve function ordering in the view.
                // If there are multiple functions with this annotation this allows them
                // to be called in predictable order from top to bottom of the class, which
                // some users may depend on.
                modelInfo.addOnVisibilityStateChangedMethod(methodName)
            }
    }

    private suspend fun processVisibilityChangedAnnotations(roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(OnVisibilityChanged::class)
            .map("processVisibilityChangedAnnotations") { visibilityMethod ->
                if (!validateVisibilityChangedElement(visibilityMethod)) {
                    return@map null
                }

                val info = getModelInfoForPropElement(visibilityMethod)
                if (info == null) {
                    logger.logError(
                        "%s annotation can only be used in classes annotated with %s",
                        OnVisibilityChanged::class.java, ModelView::class.java
                    )
                    return@map null
                }

                visibilityMethod.simpleName.toString() to info
            }.forEach { (methodName, modelInfo) ->
                // Do this after, synchronously, to preserve function ordering in the view.
                // If there are multiple functions with this annotation this allows them
                // to be called in predictable order from top to bottom of the class, which
                // some users may depend on.
                modelInfo.addOnVisibilityChangedMethod(methodName)
            }
    }

    private suspend fun processAfterBindAnnotations(roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(AfterPropsSet::class)
            .map("processAfterBindAnnotations") { afterPropsMethod ->
                if (!validateAfterPropsMethod(afterPropsMethod)) {
                    return@map null
                }

                val info = getModelInfoForPropElement(afterPropsMethod)
                if (info == null) {
                    logger.logError(
                        "%s annotation can only be used in classes annotated with %s",
                        AfterPropsSet::class.java, ModelView::class.java
                    )
                    return@map null
                }

                afterPropsMethod.simpleName.toString() to info
            }.forEach { (methodName, modelInfo) ->
                // Do this after, synchronously, to preserve function ordering in the view.
                // If there are multiple functions with this annotation this allows them
                // to be called in predictable order from top to bottom of the class, which
                // some users may depend on.
                modelInfo.addAfterPropsSetMethod(methodName)
            }
    }

    private fun validateAfterPropsMethod(resetMethod: Element): Boolean =
        validateExecutableElement(resetMethod, AfterPropsSet::class.java, 0)

    /** Include props and reset methods from super class views.  */
    private suspend fun updateViewsForInheritedViewAnnotations() {

        modelClassMap.values.forEach("updateViewsForInheritedViewAnnotations") { view ->
            // We walk up the super class tree and look for any elements with epoxy annotations.
            // This approach lets us capture views that we've already processed as well as views
            // in other libraries that we wouldn't have otherwise processed.

            view.viewElement.iterateSuperClasses(typeUtils) { superViewElement ->
                val annotationsOnViewSuperClass = memoizer.getAnnotationsOnViewSuperClass(
                    superViewElement,
                    logger,
                    resourceProcessor
                )

                val isSamePackage by lazy {
                    annotationsOnViewSuperClass.viewPackageName == elementUtils.getPackageOf(view.viewElement).qualifiedName
                }

                fun forEachElementWithAnnotation(
                    annotations: List<KClass<out Annotation>>,
                    function: (Memoizer.ViewElement) -> Unit
                ) {
                    val javaAnnotations = annotations.map { it.java }

                    annotationsOnViewSuperClass.annotatedElements
                        .filterKeys { annotation ->
                            annotation in javaAnnotations
                        }
                        .values
                        .flatten()
                        .filter { viewElement ->
                            isSamePackage || !viewElement.isPackagePrivate
                        }
                        .forEach {
                            function(it)
                        }
                }

                forEachElementWithAnnotation(modelPropAnnotations) {
                    // todo Include view interfaces for the super class in this model
                    // 1. we should only do that if all methods in the super class are accessible to this (ie not package private and in a different package)
                    // 2. We also need to handle the case the that super view is abstract - right now interfaces are not generated for abstract views
                    // 3. If an abstract view only implements part of the interface it would mess up the way we check which methods count in the interface

                    // We don't want the attribute from the super class replacing an attribute in the
                    // subclass if the subclass overrides it, since the subclass definition could include
                    // different annotation parameter settings, or we could end up with duplicates
                    view.addAttributeIfNotExists(it.attributeInfo.value)
                }

                forEachElementWithAnnotation(listOf(OnViewRecycled::class)) {
                    view.addOnRecycleMethod(it.simpleName)
                }

                forEachElementWithAnnotation(listOf(OnVisibilityStateChanged::class)) {
                    view.addOnVisibilityStateChangedMethod(it.simpleName)
                }

                forEachElementWithAnnotation(listOf(OnVisibilityChanged::class)) {
                    view.addOnVisibilityChangedMethod(it.simpleName)
                }

                forEachElementWithAnnotation(listOf(AfterPropsSet::class)) {
                    view.addAfterPropsSetMethod(it.simpleName)
                }
            }
        }
    }

    /**
     * If a view defines a base model that its generated model should extend we need to check if that
     * base model has [com.airbnb.epoxy.EpoxyAttribute] fields and include those in our model if
     * so.
     */
    private suspend fun updatesViewsForInheritedBaseModelAttributes() {
        modelClassMap.values.forEach("updatesViewsForInheritedBaseModelAttributes") { modelViewInfo ->
            // Skip generated model super classes since it will already contain all of the functions
            // necessary for included attributes, and duplicating them is a waste.
            if (modelViewInfo.isSuperClassAlsoGenerated) return@forEach

            memoizer.getInheritedEpoxyAttributes(
                modelViewInfo.superClassElement.asType(),
                modelViewInfo.generatedName.packageName(),
                logger
            ).let { modelViewInfo.addAttributes(it) }
        }
    }

    private suspend fun addStyleAttributes() {
        modelClassMap
            .values
            .filter("addStyleAttributes") { it.viewElement.hasStyleableAnnotation(elementUtils) }
            .also { styleableModelsToWrite.addAll(it) }
    }

    private fun validateResetElement(resetMethod: Element): Boolean =
        validateExecutableElement(resetMethod, OnViewRecycled::class.java, 0)

    private fun validateVisibilityStateChangedElement(visibilityMethod: Element): Boolean =
        validateExecutableElement(
            visibilityMethod,
            OnVisibilityStateChanged::class.java,
            1,
            checkTypeParameters = listOf(TypeKind.INT)
        )

    private fun validateVisibilityChangedElement(visibilityMethod: Element): Boolean =
        validateExecutableElement(
            visibilityMethod,
            OnVisibilityChanged::class.java,
            4,
            checkTypeParameters = listOf(TypeKind.FLOAT, TypeKind.FLOAT, TypeKind.INT, TypeKind.INT)
        )

    private suspend fun writeJava() {
        val modelsToWrite = modelClassMap.values.toMutableList()
        modelsToWrite.removeAll(styleableModelsToWrite)

        styleableModelsToWrite.filter("addStyleBuilderAttributes") {
            tryAddStyleBuilderAttribute(it, elementUtils, typeUtils)
        }.let {
            modelsToWrite.addAll(it)
            styleableModelsToWrite.removeAll(it)
        }

        ModelViewWriter(modelWriter, typeUtils, elementUtils, this)
            .writeModels(modelsToWrite, originatingConfigElements())

        if (styleableModelsToWrite.isEmpty()) {
            // Make sure all models have been processed and written before we generate interface information
            modelWriter.writeFilesForViewInterfaces()
        }
    }

    private fun getModelInfoForPropElement(element: Element): ModelViewInfo? =
        element.enclosingElement?.let { modelClassMap[it] }

    companion object {
        val modelPropAnnotations = listOf(
            ModelProp::class,
            TextProp::class,
            CallbackProp::class
        )
    }
}
