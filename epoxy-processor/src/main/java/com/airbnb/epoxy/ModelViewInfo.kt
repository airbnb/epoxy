package com.airbnb.epoxy

import com.airbnb.epoxy.Utils.isEpoxyModel
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.declaresDefaultValue
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
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
    private val configManager: ConfigManager,
    private val resourceProcessor: ResourceProcessor
) : GeneratedModelInfo() {
    val resetMethodNames = mutableListOf<String>()
    val visibilityStateChangedMethodNames = mutableListOf<String>()
    val visibilityChangedMethodNames = mutableListOf<String>()
    val afterPropsSetMethodNames = mutableListOf<String>()
    val saveViewState: Boolean
    private val viewAnnotation: ModelView = viewElement.getAnnotation(ModelView::class.java)
    val fullSpanSize: Boolean
    private val generatedModelSuffix: String
    val kotlinMetadata: KotlinClassMetadata? = viewElement.kotlinMetadata as? KotlinClassMetadata

    /** All interfaces the view implements that have at least one prop set by the interface. */
    private val viewInterfaces: List<TypeElement>

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

        if (
            superClassElement.simpleName.toString() != ClassNames.EPOXY_MODEL_UNTYPED.simpleName()
        ) {
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
                    viewMethod.hasAnyAnnotation(ModelViewProcessor.modelPropAnnotations) &&
                        interfaceElement.executableElements().any { interfaceMethod ->
                            // To keep this simple we only compare name and ignore parameters, should be close enough
                            viewMethod.simpleName.toString() ==
                                interfaceMethod.simpleName.toString()
                        }
                }
            }

        // Pass deprecated annotations on to the generated model
        annotations.addAll(
            viewElement.buildAnnotationSpecs { DEPRECATED == it.simpleName() }
        )
    }

    /** We generate an interface on the model to represent each interface on the view.
     * This lets models with the same view interface be grouped together. */
    val generatedViewInterfaceNames: List<ClassName> by lazy {
        viewInterfaces.map {
            ClassName.get(it).appendToName("Model_")
        }
    }

    private fun lookUpSuperClassElement(): TypeElement {
        val defaultSuper = Utils.getElementByName(
            ClassNames.EPOXY_MODEL_UNTYPED,
            elements, typeUtils
        ) as TypeElement

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
                    ModelView::class.java.simpleName, classToExtend, viewElement.simpleName
                )
            return defaultSuper
        }

        if (!validateSuperClassIsTypedCorrectly(classToExtend)) {
            errorLogger.logError(
                "The base model provided to an %s must have View as its type (%s).",
                ModelView::class.java.simpleName, viewElement.simpleName
            )
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
        return typeUtils.isAssignable(viewType, typeMirror) || typeUtils.isSubtype(
            typeMirror,
            viewType
        )
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

        val hasDefaultKotlinValue = prop is ExecutableElement &&
            kotlinMetadata?.data?.let { classData ->

                val matchingFunctions = classData.classProto.functionOrBuilderList
                    .filter { it.hasName() }
                    .filter { classData.nameResolver.getString(it.name) == prop.simpleName.toString() }
                    .filter { it.valueParameterCount == 1 }
                    .map { it.valueParameterList.single() }
                    .filter { it.hasName() }
                    .filter { classData.nameResolver.getString(it.name) == prop.parameters.single().simpleName.toString() }

                when (matchingFunctions.size) {
                    0 -> false
                    1 -> matchingFunctions.single().declaresDefaultValue
                    else -> throw IllegalStateException("More than one function in $viewElement found matching $prop -> $matchingFunctions")
                }
            } ?: false

        // Since our generated code is java we need jvmoverloads so that a no arg
        // version of the function is generated. However, the JvmOverloads annotation
        // is stripped when generating the java code so we can't check it directly.
        // Instead, we verify that a no arg function of the same name exists
        val hasNoArgEquivalent = hasDefaultKotlinValue &&
            prop is ExecutableElement &&
            viewElement.hasOverload(prop, 0)

        if (hasDefaultKotlinValue && !hasNoArgEquivalent) {
            errorLogger.logError(
                "Model view function with default argument must be annotated with @JvmOverloads: %s#%s",
                viewElement.simpleName,
                prop.simpleName
            )
        }

        addAttribute(
            ViewAttributeInfo(
                modelInfo = this,
                hasDefaultKotlinValue = hasDefaultKotlinValue && hasNoArgEquivalent,
                viewAttributeElement = prop,
                types = typeUtils,
                elements = elements,
                errorLogger = errorLogger,
                resourceProcessor = resourceProcessor
            )
        )
    }

    fun addPropIfNotExists(prop: Element) {
        addAttributeIfNotExists(
            ViewAttributeInfo(
                modelInfo = this,
                hasDefaultKotlinValue = false,
                viewAttributeElement = prop,
                types = typeUtils,
                elements = elements,
                errorLogger = errorLogger,
                resourceProcessor = resourceProcessor
            )
        )
    }

    fun addOnRecycleMethodIfNotExists(resetMethod: Element) {
        val methodName = resetMethod.simpleName.toString()
        if (!resetMethodNames.contains(methodName)) {
            resetMethodNames.add(methodName)
        }
    }

    fun addOnVisibilityStateChangedMethodIfNotExists(visibilityMethod: Element) {
        val methodName = visibilityMethod.simpleName.toString()
        if (!visibilityStateChangedMethodNames.contains(methodName)) {
            visibilityStateChangedMethodNames.add(methodName)
        }
    }

    fun addOnVisibilityChangedMethodIfNotExists(visibilityMethod: Element) {
        val methodName = visibilityMethod.simpleName.toString()
        if (!visibilityChangedMethodNames.contains(methodName)) {
            visibilityChangedMethodNames.add(methodName)
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
