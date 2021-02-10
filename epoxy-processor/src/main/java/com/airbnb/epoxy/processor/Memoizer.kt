package com.airbnb.epoxy.processor

import com.airbnb.epoxy.AfterPropsSet
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.OnViewRecycled
import com.airbnb.epoxy.OnVisibilityChanged
import com.airbnb.epoxy.OnVisibilityStateChanged
import com.airbnb.epoxy.TextProp
import com.airbnb.epoxy.processor.GeneratedModelInfo.Companion.RESET_METHOD
import com.airbnb.epoxy.processor.GeneratedModelInfo.Companion.buildParamSpecs
import com.airbnb.epoxy.processor.Utils.isSubtype
import com.squareup.javapoet.ClassName
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.Name
import javax.lang.model.element.Parameterizable
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.ExecutableType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class Memoizer(
    val types: Types,
    val elements: Elements,
    val logger: Logger
) {

    val epoxyModelClassAnnotation by lazy { EpoxyModelClass::class.className() }

    val epoxyDataBindingModelBaseClass: TypeElement by lazy {
        Utils.getElementByName(
            ClassNames.EPOXY_DATA_BINDING_MODEL,
            elements,
            types
        )
    }

    val parisStyleType by lazy {
        getTypeMirror(ClassNames.PARIS_STYLE, elements, types)
    }

    val epoxyModelClassElementUntyped by lazy {
        Utils.getElementByName(
            ClassNames.EPOXY_MODEL_UNTYPED,
            elements,
            types
        )
    }

    val viewType: TypeMirror by lazy {
        getTypeMirror(ClassNames.ANDROID_VIEW, elements, types)
    }

    private val methodsReturningClassType = mutableMapOf<Name, Set<MethodInfo>>()

    fun getMethodsReturningClassType(classType: TypeMirror): Set<MethodInfo> =
        synchronized(methodsReturningClassType) {
            val classElement = types.asElement(classType) as TypeElement
            methodsReturningClassType.getOrPut(classElement.qualifiedName) {

                classType.ensureLoaded()

                val superClassType = classElement.superclass
                superClassType.ensureLoaded()
                // Check for base Object class
                if (superClassType.kind == TypeKind.NONE) return@getOrPut emptySet()

                val methodInfos: List<MethodInfo> =
                    classElement.enclosedElementsThreadSafe.mapNotNull { subElement ->
                        val modifiers: Set<Modifier> = subElement.modifiers
                        if (subElement.kind !== ElementKind.METHOD ||
                            modifiers.contains(Modifier.PRIVATE) ||
                            modifiers.contains(Modifier.FINAL) ||
                            modifiers.contains(Modifier.STATIC)
                        ) {
                            return@mapNotNull null
                        }

                        val methodReturnType = (subElement.asType() as ExecutableType).returnType
                        if (methodReturnType != classType && !isSubtype(
                                classType,
                                methodReturnType,
                                types
                            )
                        ) {
                            return@mapNotNull null
                        }

                        val castedSubElement = subElement as ExecutableElement
                        val params: List<VariableElement> = castedSubElement.parametersThreadSafe
                        val methodName = subElement.getSimpleName().toString()
                        if (methodName == RESET_METHOD && params.isEmpty()) {
                            return@mapNotNull null
                        }
                        val isEpoxyAttribute =
                            castedSubElement.getAnnotation<EpoxyAttribute>() != null

                        MethodInfo(
                            methodName,
                            modifiers,
                            buildParamSpecs(params),
                            castedSubElement.isVarArgsThreadSafe,
                            isEpoxyAttribute,
                            castedSubElement
                        )
                    }

                // Note: Adding super type methods second preserves any overloads in the base
                // type that may have changes (ie, a new return type or annotation), since
                // Set.plus only adds items that don't already exist.
                methodInfos.toSet() + getMethodsReturningClassType(superClassType)
            }
        }

    private val classConstructors =
        mutableMapOf<Name, List<GeneratedModelInfo.ConstructorInfo>>()

    /**
     * Get information about constructors of the original class so we can duplicate them in the
     * generated class and call through to super with the proper parameters
     */
    fun getClassConstructors(classElement: TypeElement): List<GeneratedModelInfo.ConstructorInfo> =
        synchronized(classConstructors) {
            classConstructors.getOrPut(classElement.qualifiedName) {

                classElement
                    .enclosedElementsThreadSafe
                    .filter { subElement ->
                        subElement.kind == ElementKind.CONSTRUCTOR &&
                            !subElement.modifiersThreadSafe.contains(Modifier.PRIVATE)
                    }
                    .map { subElement ->
                        val constructor = subElement as ExecutableElement
                        val params: List<VariableElement> = constructor.parametersThreadSafe

                        GeneratedModelInfo.ConstructorInfo(
                            subElement.modifiersThreadSafe,
                            buildParamSpecs(params),
                            constructor.isVarArgsThreadSafe
                        )
                    }
            }
        }

    private val validatedViewModelBaseElements = mutableMapOf<Name, TypeElement?>()
    fun validateViewModelBaseClass(
        baseModelType: TypeMirror,
        logger: Logger,
        viewName: Name
    ): TypeElement? =
        synchronized(validatedViewModelBaseElements) {
            val baseModelElement = types.asElement(baseModelType) as TypeElement
            validatedViewModelBaseElements.getOrPut(baseModelElement.qualifiedName) {

                baseModelType.ensureLoaded()
                if (!Utils.isEpoxyModel(baseModelType)) {
                    logger.logError(
                        "The base model provided to an %s must extend EpoxyModel, but was %s (%s).",
                        ModelView::class.java.simpleName, baseModelType, viewName
                    )
                    null
                } else if (!validateSuperClassIsTypedCorrectly(baseModelElement)) {
                    logger.logError(
                        "The base model provided to an %s must have View as its type (%s).",
                        ModelView::class.java.simpleName, viewName
                    )
                    null
                } else {
                    baseModelElement
                }
            }
        }

    /** The super class that our generated model extends from must have View as its only type.  */
    private fun validateSuperClassIsTypedCorrectly(classType: TypeElement): Boolean {
        val classElement = classType as? Parameterizable ?: return false

        val typeParameters = classElement.typeParametersThreadSafe
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
        return Utils.isAssignable(viewType, typeMirror, types) || types.isSubtype(
            typeMirror,
            viewType
        )
    }

    /**
     * Looks up all of the declared EpoxyAttribute fields on superclasses and returns
     * attribute info for them.
     */
    fun getInheritedEpoxyAttributes(
        originatingSuperClassType: TypeMirror,
        modelPackage: String,
        logger: Logger,
        includeSuperClass: (TypeElement) -> Boolean = { true }
    ): List<AttributeInfo> {
        val result = mutableListOf<AttributeInfo>()

        var currentSuperClassElement: TypeElement? =
            (types.asElement(originatingSuperClassType) as TypeElement).ensureLoaded()

        while (currentSuperClassElement != null) {
            val superClassAttributes = getEpoxyAttributesOnElement(
                currentSuperClassElement,
                logger
            )

            val attributes = superClassAttributes?.superClassAttributes

            if (attributes?.isNotEmpty() == true) {
                attributes.takeIf {
                    includeSuperClass(currentSuperClassElement!!)
                }?.filterTo(result) {
                    // We can't inherit a package private attribute if we're not in the same package
                    !it.isPackagePrivate || modelPackage == superClassAttributes.superClassPackage
                }
            }

            currentSuperClassElement = currentSuperClassElement.superClassElement(types)
        }

        return result
    }

    data class SuperClassAttributes(
        val superClassPackage: String,
        val superClassAttributes: List<AttributeInfo>
    )

    private val inheritedEpoxyAttributes = mutableMapOf<Name, SuperClassAttributes?>()

    private fun getEpoxyAttributesOnElement(
        classElement: TypeElement,
        logger: Logger
    ): SuperClassAttributes? {
        return synchronized(inheritedEpoxyAttributes) {
            inheritedEpoxyAttributes.getOrPut(classElement.qualifiedName) {
                if (!Utils.isEpoxyModel(classElement.asType())) {
                    null
                } else {
                    val attributes = classElement
                        .enclosedElementsThreadSafe
                        .filter { it.getAnnotationThreadSafe(EpoxyAttribute::class.java) != null }
                        .map {
                            EpoxyProcessor.buildAttributeInfo(
                                it,
                                logger,
                                types,
                                elements,
                                memoizer = this
                            )
                        }

                    SuperClassAttributes(
                        superClassPackage = elements.getPackageOf(
                            classElement
                        ).qualifiedName.toString(),
                        superClassAttributes = attributes
                    )
                }
            }
        }
    }

    class SuperViewAnnotations(
        val viewPackageName: Name,
        val annotatedElements: Map<Class<out Annotation>, List<ViewElement>>
    )

    class ViewElement(
        val element: Element,
        val isPackagePrivate: Boolean,
        val attributeInfo: Lazy<ViewAttributeInfo>
    ) {
        val simpleName: String by lazy {
            element.simpleName.toString()
        }
    }

    private val annotationsOnSuperView = mutableMapOf<Name, SuperViewAnnotations>()

    fun getAnnotationsOnViewSuperClass(
        superViewElement: TypeElement,
        logger: Logger,
        resourceProcessor: ResourceProcessor
    ): SuperViewAnnotations {
        return synchronized(annotationsOnSuperView) {
            annotationsOnSuperView.getOrPut(superViewElement.qualifiedName) {

                val viewPackageName = elements.getPackageOf(superViewElement).qualifiedName
                val annotatedElements =
                    mutableMapOf<Class<out Annotation>, MutableList<ViewElement>>()

                superViewElement.enclosedElementsThreadSafe.forEach { element ->
                    val isPackagePrivate by lazy { Utils.isFieldPackagePrivate(element) }

                    viewModelAnnotations.forEach { annotation ->
                        if (element.getAnnotationThreadSafe(annotation) != null) {
                            annotatedElements
                                .getOrPut(annotation) { mutableListOf() }
                                .add(
                                    ViewElement(
                                        element = element,
                                        isPackagePrivate = isPackagePrivate,
                                        attributeInfo = lazy {
                                            ViewAttributeInfo(
                                                viewElement = superViewElement,
                                                viewPackage = viewPackageName.toString(),
                                                hasDefaultKotlinValue = false,
                                                viewAttributeElement = element,
                                                types = types,
                                                elements = elements,
                                                logger = logger,
                                                resourceProcessor = resourceProcessor,
                                                memoizer = this
                                            )
                                        }
                                    )
                                )
                        }
                    }
                }

                SuperViewAnnotations(
                    viewPackageName,
                    annotatedElements
                )
            }
        }
    }

    private val typeMap = mutableMapOf<String, Type>()
    fun getType(typeMirror: TypeMirror): Type {
        return synchronized(typeMap) {
            val typeMirrorAsString = typeMirror.ensureLoaded().toString()
            typeMap.getOrPut(typeMirrorAsString) {
                Type(typeMirror, typeMirrorAsString)
            }
        }
    }

    private val implementsModelCollectorMap = mutableMapOf<Name, Boolean>()
    fun implementsModelCollector(classElement: TypeElement): Boolean {
        return synchronized(typeMap) {
            implementsModelCollectorMap.getOrPut(classElement.qualifiedName) {
                classElement.interfaces.any {
                    it.toString() == ClassNames.MODEL_COLLECTOR.toString()
                } || classElement.superClassElement(types)?.let { superClassElement ->
                    // Also check the class hierarchy
                    implementsModelCollector(superClassElement)
                } ?: false
            }
        }
    }

    private val hasViewParentConstructorMap = mutableMapOf<Name, Boolean>()
    fun hasViewParentConstructor(classElement: TypeElement): Boolean {
        return synchronized(typeMap) {
            hasViewParentConstructorMap.getOrPut(classElement.qualifiedName) {
                getClassConstructors(classElement).filter {
                    it.params.size == 1 && it.params[0].type == ClassName.get("android.view", "ViewParent")
                }.isNotEmpty()
            }
        }
    }
}

private val viewModelAnnotations = listOf(
    ModelProp::class.java,
    TextProp::class.java,
    CallbackProp::class.java,
    AfterPropsSet::class.java,
    OnVisibilityChanged::class.java,
    OnVisibilityStateChanged::class.java,
    OnViewRecycled::class.java
)
