package com.airbnb.epoxy

import com.airbnb.epoxy.Utils.belongToTheSamePackage
import com.airbnb.epoxy.Utils.isFieldPackagePrivate
import com.airbnb.epoxy.Utils.isSubtype
import com.airbnb.epoxy.Utils.isSubtypeOfType
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedHashMap
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier.PRIVATE
import javax.lang.model.element.Modifier.STATIC
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeKind
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

// TODO: (eli_hart 5/30/17) allow param counts > 0 in setters
internal class ModelViewProcessor(
    private val elements: Elements,
    private val types: Types,
    private val configManager: ConfigManager,

    private val errorLogger: ErrorLogger,
    private val modelWriter: GeneratedModelWriter,
    private val resourceProcessor: ResourceProcessor
) {

    private val modelClassMap = LinkedHashMap<Element, ModelViewInfo>()
    private val styleableModelsToWrite = mutableListOf<ModelViewInfo>()

    fun process(
        roundEnv: RoundEnvironment,
        otherGeneratedModels: List<GeneratedModelInfo>
    ): Collection<GeneratedModelInfo> {

        modelClassMap.clear()

        processViewAnnotations(roundEnv)

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
        updatesViewsForInheritedBaseModelAttributes(otherGeneratedModels)
        addStyleAttributes()

        writeJava()

        return modelClassMap.values
    }

    private fun processViewAnnotations(roundEnv: RoundEnvironment) {
        for (viewElement in roundEnv.getElementsAnnotatedWith(ModelView::class.java)) {
            try {
                if (!validateViewElement(viewElement)) {
                    continue
                }

                modelClassMap.put(
                    viewElement,
                    ModelViewInfo(
                        viewElement as TypeElement, types, elements,
                        errorLogger,
                        configManager, resourceProcessor
                    )
                )
            } catch (e: Exception) {
                errorLogger.logError(e, "Error creating model view info classes.")
            }
        }
    }

    private fun validateViewElement(viewElement: Element): Boolean {
        if (viewElement.kind != ElementKind.CLASS || viewElement !is TypeElement) {
            errorLogger.logError(
                "${ModelView::class.java} annotations can only be on a class " +
                    "(element: ${viewElement.simpleName})"
            )
            return false
        }

        val modifiers = viewElement.getModifiers()
        if (modifiers.contains(PRIVATE)) {
            errorLogger.logError(
                "${ModelView::class.java} annotations must not be on private classes. " +
                    "(class: ${viewElement.getSimpleName()})"
            )
            return false
        }

        // Nested classes must be static
        if (viewElement.nestingKind.isNested) {
            errorLogger.logError(
                "Classes with ${ModelView::class.java} annotations cannot be nested. " +
                    "(class: ${viewElement.getSimpleName()})"
            )
            return false
        }

        if (!isSubtypeOfType(viewElement.asType(), Utils.ANDROID_VIEW_TYPE)) {
            errorLogger.logError(
                "Classes with ${ModelView::class.java} annotations must extend " +
                    "android.view.View. (class: ${viewElement.getSimpleName()})"
            )
            return false
        }

        return true
    }

    private fun processSetterAnnotations(roundEnv: RoundEnvironment) {

        for (propAnnotation in modelPropAnnotations) {
            for (prop in roundEnv.getElementsAnnotatedWith(propAnnotation)) {
                val info = getModelInfoForPropElement(prop)
                if (info == null) {
                    errorLogger.logError(
                        "${propAnnotation.simpleName} annotation can only be used in classes " +
                            "annotated with ${ModelView::class.java.simpleName} " +
                            "(${prop.enclosingElement.simpleName}#${prop.simpleName})"
                    )
                    continue
                }

                // JvmOverloads is used on properties with default arguments, which we support.
                // However, the generated no arg version of the function will also have the
                // @ModelProp annotation so we need to ignore it when it is processed.
                // However, the JvmOverloads annotation is removed in the java class so we need
                // to manually look for a valid overload function.
                if (prop is ExecutableElement &&
                    prop.parameters.isEmpty() &&
                    info.viewElement.findOverload(
                        prop,
                        1
                    )?.hasAnyAnnotation(modelPropAnnotations) == true
                ) {
                    continue
                }

                if (!validatePropElement(prop, propAnnotation)) {
                    continue
                }

                info.addProp(prop)
            }
        }
    }

    private fun groupOverloads() {
        for (viewInfo in modelClassMap.values) {
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
                        errorLogger
                            .logError(
                                "Only one setter was included in the custom group " +
                                    "'$customGroup' at ${viewInfo.viewElement.simpleName}#" +
                                    "${attribute.viewAttributeName}. Groups should have at " +
                                    "least 2 setters."
                            )
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

    private fun validatePropElement(
        prop: Element,
        propAnnotation: Class<out Annotation>
    ): Boolean {
        return when (prop) {
            is ExecutableElement -> validateExecutableElement(prop, propAnnotation, 1)
            is VariableElement -> validateVariableElement(prop, propAnnotation)
            else -> {
                errorLogger.logError(
                    "%s annotations can only be on a method or a field(element: %s)",
                    propAnnotation,
                    prop.simpleName
                )
                return false
            }
        }
    }

    private fun validateVariableElement(field: Element, annotationClass: Class<*>): Boolean {
        var isValidField = field.kind == ElementKind.FIELD &&
            !field.modifiers.contains(PRIVATE) &&
            !field.modifiers.contains(STATIC)

        if (!isValidField) {
            errorLogger.logError(
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
            errorLogger.logError(
                "%s annotations can only be on a method (element: %s)",
                annotationClass::class.java.simpleName,
                element.simpleName
            )
            return false
        }

        if (element.parameters.size != paramCount) {
            errorLogger.logError(
                "Methods annotated with %s must have exactly %s parameter (method: %s)",
                annotationClass::class.java.simpleName, paramCount, element.getSimpleName()
            )
            return false
        }

        checkTypeParameters?.let { expectedTypeParameters ->
            // Check also the parameter types
            var hasErrors = false
            element.parameters.forEachIndexed { i, parameter ->
                hasErrors = hasErrors || parameter.asType().kind != expectedTypeParameters[i]
            }
            if (hasErrors) {
                errorLogger.logError(
                    "Methods annotated with %s must have parameter types %s, " +
                        "found: %s (method: %s)",
                    annotationClass::class.java.simpleName,
                    expectedTypeParameters,
                    element.parameters.map { it.asType().kind },
                    element.simpleName
                )
            }
        }

        val modifiers = element.getModifiers()
        if (modifiers.contains(STATIC) || modifiers.contains(PRIVATE)) {
            errorLogger.logError(
                "Methods annotated with %s cannot be private or static (method: %s)",
                annotationClass::class.java.simpleName, element.getSimpleName()
            )
            return false
        }

        return true
    }

    private fun processResetAnnotations(roundEnv: RoundEnvironment) {
        for (recycleMethod in roundEnv.getElementsAnnotatedWith(OnViewRecycled::class.java)) {
            if (!validateResetElement(recycleMethod)) {
                continue
            }

            val info = getModelInfoForPropElement(recycleMethod)
            if (info == null) {
                errorLogger.logError(
                    "%s annotation can only be used in classes annotated with %s",
                    OnViewRecycled::class.java, ModelView::class.java
                )
                continue
            }

            info.addOnRecycleMethodIfNotExists(recycleMethod as ExecutableElement)
        }
    }

    private fun processVisibilityStateChangedAnnotations(roundEnv: RoundEnvironment) {
        for (
        visibilityMethod in
        roundEnv.getElementsAnnotatedWith(OnVisibilityStateChanged::class.java)
        ) {
            if (!validateVisibilityStateChangedElement(visibilityMethod)) {
                continue
            }

            val info = getModelInfoForPropElement(visibilityMethod)
            if (info == null) {
                errorLogger.logError(
                    "%s annotation can only be used in classes annotated with %s",
                    OnVisibilityStateChanged::class.java, ModelView::class.java
                )
                continue
            }

            info.addOnVisibilityStateChangedMethodIfNotExists(visibilityMethod as ExecutableElement)
        }
    }

    private fun processVisibilityChangedAnnotations(roundEnv: RoundEnvironment) {
        for (
        visibilityMethod in
        roundEnv.getElementsAnnotatedWith(OnVisibilityChanged::class.java)
        ) {
            if (!validateVisibilityChangedElement(visibilityMethod)) {
                continue
            }

            val info = getModelInfoForPropElement(visibilityMethod)
            if (info == null) {
                errorLogger.logError(
                    "%s annotation can only be used in classes annotated with %s",
                    OnVisibilityChanged::class.java, ModelView::class.java
                )
                continue
            }

            info.addOnVisibilityChangedMethodIfNotExists(visibilityMethod as ExecutableElement)
        }
    }

    private fun processAfterBindAnnotations(roundEnv: RoundEnvironment) {
        for (afterPropsMethod in roundEnv.getElementsAnnotatedWith(AfterPropsSet::class.java)) {
            if (!validateAfterPropsMethod(afterPropsMethod)) {
                continue
            }

            val info = getModelInfoForPropElement(afterPropsMethod)
            if (info == null) {
                errorLogger.logError(
                    "%s annotation can only be used in classes annotated with %s",
                    AfterPropsSet::class.java, ModelView::class.java
                )
                continue
            }

            info.addAfterPropsSetMethodIfNotExists(afterPropsMethod as ExecutableElement)
        }
    }

    private fun validateAfterPropsMethod(resetMethod: Element): Boolean =
        validateExecutableElement(resetMethod, AfterPropsSet::class.java, 0)

    /** Include props and reset methods from super class views.  */
    private fun updateViewsForInheritedViewAnnotations() {
        for (view in modelClassMap.values) {

            // We walk up the super class tree and look for any elements with epoxy annotations.
            // This approach lets us capture views that we've already processed as well as views
            // in other libraries that we wouldn't have otherwise processed.

            view.viewElement.iterateSuperClasses(types) { superViewElement ->
                val samePackage = belongToTheSamePackage(
                    view.viewElement, superViewElement,
                    elements
                )

                fun forEachElementWithAnnotation(
                    annotations: List<Class<out Annotation>>,
                    function: (Element) -> Unit
                ) {
                    superViewElement.enclosedElements
                        .filter {
                            // Make sure we can access the method
                            samePackage || !isFieldPackagePrivate(it)
                        }
                        .filter {
                            hasAnnotation(it, annotations)
                        }
                        .forEach {
                            function(it)
                        }
                }

                // We don't want the attribute from the super class replacing an attribute in the
                // subclass if the subclass overrides it, since the subclass definition could include
                // different annotation parameter settings, or we could end up with duplicates

                forEachElementWithAnnotation(modelPropAnnotations) {
                    // todo Include view interfaces for the super class in this model
                    // 1. we should only do that if all methods in the super class are accessible to this (ie not package private and in a different package)
                    // 2. We also need to handle the case the that super view is abstract - right now interfaces are not generated for abstract views
                    // 3. If an abstract view only implements part of the interface it would mess up the way we check which methods count in the interface
                    view.addPropIfNotExists(it)
                }

                forEachElementWithAnnotation(listOf(OnViewRecycled::class.java)) {
                    view.addOnRecycleMethodIfNotExists(it)
                }

                forEachElementWithAnnotation(listOf(OnVisibilityStateChanged::class.java)) {
                    view.addOnVisibilityStateChangedMethodIfNotExists(it)
                }

                forEachElementWithAnnotation(listOf(OnVisibilityChanged::class.java)) {
                    view.addOnVisibilityChangedMethodIfNotExists(it)
                }

                forEachElementWithAnnotation(listOf(AfterPropsSet::class.java)) {
                    view.addAfterPropsSetMethodIfNotExists(it)
                }
            }
        }
    }

    private fun hasAnnotation(
        element: Element,
        annotations: List<Class<out Annotation>>
    ): Boolean = annotations.any { element.getAnnotation(it) != null }

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

    private fun addStyleAttributes() {
        modelClassMap
            .values
            .filter { it.viewElement.hasStyleableAnnotation(elements) }
            .also { styleableModelsToWrite.addAll(it) }
    }

    private fun validateResetElement(resetMethod: Element): Boolean =
        validateExecutableElement(resetMethod, OnViewRecycled::class.java, 0)

    private fun validateVisibilityStateChangedElement(visibilityMethod: Element): Boolean =
        validateExecutableElement(
            visibilityMethod, OnVisibilityStateChanged::class.java, 1,
            checkTypeParameters = listOf(TypeKind.INT)
        )

    private fun validateVisibilityChangedElement(visibilityMethod: Element): Boolean =
        validateExecutableElement(
            visibilityMethod, OnVisibilityChanged::class.java, 4,
            checkTypeParameters = listOf(TypeKind.FLOAT, TypeKind.FLOAT, TypeKind.INT, TypeKind.INT)
        )

    private fun writeJava() {
        val modelsToWrite = mutableListOf<ModelViewInfo>()
        modelsToWrite.addAll(modelClassMap.values)
        modelsToWrite.removeAll(styleableModelsToWrite)

        styleableModelsToWrite.forEach {
            if (tryAddStyleBuilderAttribute(it, elements, types)) {
                modelsToWrite.add(it)
            }
        }

        styleableModelsToWrite.removeAll(modelsToWrite)

        ModelViewWriter(modelWriter, errorLogger, types, elements, configManager)
            .writeModels(modelsToWrite)

        if (styleableModelsToWrite.isEmpty()) {
            // Make sure all models have been processed and written before we generate interface information
            modelWriter.writeFilesForViewInterfaces()
        }
    }

    fun hasModelsToWrite() = styleableModelsToWrite.isNotEmpty()

    private fun getModelInfoForPropElement(element: Element): ModelViewInfo? =
        element.enclosingElement?.let { modelClassMap[it] }

    companion object {
        val modelPropAnnotations = listOf(
            ModelProp::class, TextProp::class,
            CallbackProp::class
        ).map { it.java }
    }
}
