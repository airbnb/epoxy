package com.airbnb.epoxy

import com.airbnb.epoxy.Utils.isEpoxyModel
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.Parameterizable
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

internal class ModelViewInfo(
        val viewElement: TypeElement,
        val typeUtils: Types,
        val elements: Elements,
        val errorLogger: ErrorLogger,
        val configManager: ConfigManager,
        private val resourceProcessor: ResourceProcessor
) : GeneratedModelInfo() {
    val resetMethodNames = mutableListOf<String>()
    val afterPropsSetMethodNames = mutableListOf<String>()
    val saveViewState: Boolean
    private val viewAnnotation: ModelView = viewElement.getAnnotation(ModelView::class.java)
    val fullSpanSize: Boolean
    val generatedModelSuffix: String

    /** All interfaces the view implements that have at least one prop set by the interface. */
    val viewInterfaces: List<TypeElement>

    val viewAttributes: List<ViewAttributeInfo>
        get() = attributeInfo.filterIsInstance<ViewAttributeInfo>()

    init {
        superClassElement = lookUpSuperClassElement()
        this.superClassName = ParameterizedTypeName
                .get(ClassName.get(superClassElement), TypeName.get(viewElement.asType()))

        generatedModelSuffix = configManager.generatedModelSuffix(viewElement)
        generatedClassName = buildGeneratedModelName(viewElement, elements)
        // We don't have any type parameters on our generated model
        this.parametrizedClassName = generatedClassName
        shouldGenerateModel = Modifier.ABSTRACT !in viewElement.modifiers

        if (superClassElement.simpleName.toString() != ClassNames.EPOXY_MODEL_UNTYPED.simpleName()) {
            // If the view has a custom base model then we copy any custom constructors on it
            constructors.addAll(getClassConstructors(superClassElement))
        }

        collectMethodsReturningClassType(superClassElement, typeUtils)

        // The bound type is the type of this view
        boundObjectTypeName = ClassName.get(viewElement.asType())

        saveViewState = viewAnnotation.saveViewState
        layoutParams = viewAnnotation.autoLayout
        fullSpanSize = viewAnnotation.fullSpan
        includeOtherLayoutOptions = configManager.includeAlternateLayoutsForViews(viewElement)

        val methodsOnView = viewElement.executableElements()
        viewInterfaces = viewElement
                .interfaces
                .filterIsInstance<DeclaredType>()
                .map { it.asElement() }
                .filterIsInstance<TypeElement>()
                .filter { interfaceElement ->
                    // Only include the interface if the view has one of the interface methods annotated with a prop annotation
                    methodsOnView.any { viewMethod ->
                        viewMethod.hasAnyAnnotation(ModelViewProcessor.modelPropAnnotations)
                                && interfaceElement.executableElements().any { interfaceMethod ->
                            // To keep this simple we only compare name and ignore parameters, should be close enough
                            viewMethod.simpleName.toString() == interfaceMethod.simpleName.toString()
                        }
                    }
                }
    }

    /** We generate an interface on the model to represent each interface on the view.
     * This lets models with the same view interface be grouped together. */
    val generatedViewInterfaceNames: List<ClassName> by lazy {
        viewInterfaces.map {
            ClassName.get(it).appendToName("Model_")
        }
    }

    private fun lookUpSuperClassElement(): TypeElement {
        val defaultSuper = Utils.getElementByName(ClassNames.EPOXY_MODEL_UNTYPED,
                                                  elements, typeUtils) as TypeElement

        // Unfortunately we have to do this weird try/catch to get the class type
        var classToExtend: TypeMirror? = null
        try {
            viewAnnotation.baseModelClass // this should throw
        } catch (mte: MirroredTypeException) {
            classToExtend = mte.typeMirror
        }

        if (classToExtend == null || classToExtend.toString() == Void::class.java.canonicalName) {

            val defaultBaseModel = configManager.getDefaultBaseModel(viewElement)
            if (defaultBaseModel != null) {
                classToExtend = defaultBaseModel
            } else {
                return defaultSuper
            }
        }

        if (!isEpoxyModel(classToExtend)) {
            errorLogger
                    .logError(
                            "The base model provided to an %s must extend EpoxyModel, but was %s (%s).",
                            ModelView::class.java.simpleName, classToExtend, viewElement.simpleName)
            return defaultSuper
        }

        if (!validateSuperClassIsTypedCorrectly(classToExtend)) {
            errorLogger.logError(
                    "The base model provided to an %s must have View as its type (%s).",
                    ModelView::class.java.simpleName, viewElement.simpleName)
            return defaultSuper
        }

        return typeUtils.asElement(classToExtend) as TypeElement
    }

    /** The super class that our generated model extends from must have View as its only type.  */
    private fun validateSuperClassIsTypedCorrectly(classType: TypeMirror): Boolean {
        val classElement = typeUtils.asElement(classType) as? Parameterizable ?: return false

        val typeParameters = classElement.typeParameters
        if (typeParameters.size != 1) {
            // TODO: (eli_hart 6/15/17) It should be valid to have multiple or no types as long as they
            // are correct, but that should be a rare case
            return false
        }

        val typeParam = typeParameters[0]
        val bounds = typeParam.bounds
        if (bounds.isEmpty()) {
            // Any type is allowed, so View wil work
            return true
        }

        val typeMirror = bounds[0]
        val viewType = getTypeMirror(ClassNames.ANDROID_VIEW, elements, typeUtils)
        return typeUtils.isAssignable(viewType, typeMirror) || typeUtils.isSubtype(typeMirror,
                                                                                   viewType)
    }

    private fun buildGeneratedModelName(
            viewElement: TypeElement,
            elementUtils: Elements
    ): ClassName {
        val packageName = elementUtils.getPackageOf(viewElement).qualifiedName.toString()

        var className = viewElement.simpleName.toString()
        className += generatedModelSuffix

        return ClassName.get(packageName, className)
    }

    fun addProp(prop: Element) {
        addAttribute(ViewAttributeInfo(this, prop, typeUtils, elements, errorLogger,
                                       resourceProcessor))
    }

    fun addPropIfNotExists(prop: Element) {
        addAttributeIfNotExists(
                ViewAttributeInfo(this, prop, typeUtils, elements, errorLogger,
                                  resourceProcessor))
    }

    fun addOnRecycleMethodIfNotExists(resetMethod: Element) {
        val methodName = resetMethod.simpleName.toString()
        if (!resetMethodNames.contains(methodName)) {
            resetMethodNames.add(methodName)
        }
    }

    fun addAfterPropsSetMethodIfNotExists(afterPropsSetMethod: Element) {
        val methodName = afterPropsSetMethod.simpleName.toString()
        if (!afterPropsSetMethodNames.contains(methodName)) {
            afterPropsSetMethodNames.add(methodName)
        }
    }

    fun getLayoutResource(resourceProcessor: ResourceProcessor): ResourceValue {
        val annotation = viewElement.getAnnotation(ModelView::class.java)
        val layoutValue = annotation.defaultLayout
        if (layoutValue != 0) {
            return resourceProcessor.getLayoutInAnnotation(viewElement, ModelView::class.java)
        }

        val modelViewConfig = configManager.getModelViewConfig(viewElement)

        if (modelViewConfig != null) {
            return modelViewConfig.getNameForView(viewElement)
        }

        errorLogger.logError("Unable to get layout resource for view %s", viewElement.simpleName)
        return ResourceValue(0)
    }
}

