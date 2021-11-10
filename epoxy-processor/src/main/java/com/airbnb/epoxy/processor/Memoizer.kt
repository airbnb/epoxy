package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XArrayType
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XType
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.isVoid
import androidx.room.compiler.processing.isVoidObject
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
import com.airbnb.epoxy.processor.Utils.EPOXY_CONTROLLER_TYPE
import com.airbnb.epoxy.processor.Utils.EPOXY_HOLDER_TYPE
import com.airbnb.epoxy.processor.Utils.VIEW_CHECKED_CHANGE_LISTENER_TYPE
import com.airbnb.epoxy.processor.Utils.VIEW_CLICK_LISTENER_TYPE
import com.airbnb.epoxy.processor.Utils.VIEW_LONG_CLICK_LISTENER_TYPE
import com.airbnb.epoxy.processor.resourcescanning.ResourceScanner
import com.airbnb.epoxy.processor.resourcescanning.getFieldWithReflection
import com.airbnb.epoxy.processor.resourcescanning.getFieldWithReflectionOrNull
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.TypeName
import kotlin.reflect.KClass

class Memoizer(
    val environment: XProcessingEnv,
    val logger: Logger
) {

    val isKsp: Boolean get() = environment.backend == XProcessingEnv.Backend.KSP

    val androidViewType: XType by lazy {
        environment.requireType(Utils.ANDROID_VIEW_TYPE)
    }

    val epoxyModelClassAnnotation by lazy { EpoxyModelClass::class.className() }

    val generatedModelType: XType by lazy {
        environment.requireType("com.airbnb.epoxy.GeneratedModel")
    }

    val viewOnClickListenerType: XType by lazy {
        environment.requireType(VIEW_CLICK_LISTENER_TYPE)
    }
    val viewOnLongClickListenerType: XType by lazy {
        environment.requireType(VIEW_LONG_CLICK_LISTENER_TYPE)
    }
    val viewOnCheckChangedType: XType by lazy {
        environment.requireType(VIEW_CHECKED_CHANGE_LISTENER_TYPE)
    }

    val charSequenceType: XType by lazy {
        environment.requireType(CharSequence::class)
    }

    val charSequenceNullableType: XType by lazy {
        environment.requireType(CharSequence::class).makeNullable()
    }

    val iterableType: XType by lazy {
        environment.requireType(Iterable::class)
    }

    val stringAttributeType: XType by lazy {
        environment.requireType(ClassNames.EPOXY_STRING_ATTRIBUTE_DATA)
    }

    val epoxyDataBindingModelBaseClass: XTypeElement? by lazy {
        environment.findTypeElement(ClassNames.EPOXY_DATA_BINDING_MODEL)
    }

    val parisStyleType: XType by lazy {
        environment.requireType(ClassNames.PARIS_STYLE)
    }

    val epoxyModelClassElementUntyped: XTypeElement by lazy {
        environment.requireTypeElement(ClassNames.EPOXY_MODEL_UNTYPED)
    }

    val epoxyModelCollectorType: XType by lazy {
        environment.requireType(ClassNames.MODEL_COLLECTOR)
    }

    val epoxyControllerType: XType by lazy {
        environment.requireType(EPOXY_CONTROLLER_TYPE)
    }

    val epoxyModelWithHolderTypeUntyped: XType by lazy {
        environment.requireType(ClassNames.EPOXY_MODEL_WITH_HOLDER_UNTYPED)
    }

    val epoxyHolderType: XType by lazy {
        environment.requireType(EPOXY_HOLDER_TYPE)
    }

    val viewType: XType by lazy {
        environment.requireType(ClassNames.ANDROID_VIEW)
    }

    private val methodsReturningClassType = mutableMapOf<String, Set<MethodInfo>>()

    fun getMethodsReturningClassType(classType: XType, memoizer: Memoizer): Set<MethodInfo> {
        val classElement = classType.typeElement!!
        return methodsReturningClassType.getOrPut(classElement.qualifiedName) {

            val methodInfos: List<MethodInfo> =
                classElement.getDeclaredMethods().mapNotNull { subElement ->

                    if (subElement.isPrivate() || subElement.isFinal() || subElement.isStatic()) {
                        return@mapNotNull null
                    }

                    val methodReturnType = subElement.returnType
                    if (!methodReturnType.isSameType(classType) &&
                        !classType.isSubTypeOf(methodReturnType)
                    ) {
                        return@mapNotNull null
                    }

                    val methodName = subElement.name
                    if (methodName == RESET_METHOD && subElement.parameters.isEmpty()) {
                        return@mapNotNull null
                    }
                    val isEpoxyAttribute = subElement.hasAnnotation(EpoxyAttribute::class)

                    MethodInfo(
                        methodName,
                        // Javapoet needs the javax modifiers to create the MethodSpec, so we
                        // manually create them. These are the only options after returning
                        // from checking the other modifiers
                        subElement.javacModifiers,
                        buildParamSpecs(subElement.parameters, memoizer),
                        subElement.isVarArgs(),
                        isEpoxyAttribute,
                        subElement
                    )
                }

            // Note: Adding super type methods second preserves any overloads in the base
            // type that may have changes (ie, a new return type or annotation), since
            // Set.plus only adds items that don't already exist.
            val superClassType = classElement.superType ?: return@getOrPut emptySet()
            methodInfos.toSet() + getMethodsReturningClassType(superClassType, memoizer)
        }
    }

    private val classConstructors =
        mutableMapOf<String, List<GeneratedModelInfo.ConstructorInfo>>()

    /**
     * Get information about constructors of the original class so we can duplicate them in the
     * generated class and call through to super with the proper parameters
     */
    fun getClassConstructors(
        classElement: XTypeElement,
        memoizer: Memoizer
    ): List<GeneratedModelInfo.ConstructorInfo> {
        return classConstructors.getOrPut(classElement.qualifiedName) {

            classElement
                .getConstructors()
                .map { xConstructorElement ->

                    GeneratedModelInfo.ConstructorInfo(
                        xConstructorElement.javacModifiers,
                        buildParamSpecs(xConstructorElement.parameters, memoizer),
                        xConstructorElement.isVarArgs()
                    )
                }
        }
    }

    private val validatedViewModelBaseElements = mutableMapOf<String, XTypeElement?>()
    fun validateViewModelBaseClass(
        baseModelType: XType,
        logger: Logger,
        viewName: String
    ): XTypeElement? {
        val baseModelElement = baseModelType.typeElement!!
        return validatedViewModelBaseElements.getOrPut(baseModelElement.qualifiedName) {

            if (!baseModelType.isEpoxyModel(this)) {
                logger.logError(
                    baseModelElement,
                    "The base model provided to an %s must extend EpoxyModel, but was %s (%s).",
                    ModelView::class.java.simpleName, baseModelType, viewName
                )
                null
            } else if (!validateSuperClassIsTypedCorrectly(baseModelElement)) {
                logger.logError(
                    baseModelElement,
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
    private fun validateSuperClassIsTypedCorrectly(classType: XTypeElement): Boolean {
        val typeParameters = classType.type.typeArguments

        // TODO: (eli_hart 6/15/17) It should be valid to have multiple or no types as long as they
        // are correct, but that should be a rare case
        val typeParam = typeParameters.singleOrNull() ?: return false

        // Any type is allowed, so View wil work
        return typeParam.isObjectOrAny() ||
            // If there is no bound then a View will work
            typeParam.extendsBound() == null ||
            // if the bound is Any, then that is fine too.
            // For some reason this case is different in KSP and needs to be checked for.
            typeParam.extendsBound()?.typeElement?.type?.isObjectOrAny() == true ||
            typeParam.isSubTypeOf(viewType)
    }

    /**
     * Looks up all of the declared EpoxyAttribute fields on superclasses and returns
     * attribute info for them.
     */
    fun getInheritedEpoxyAttributes(
        originatingSuperClassType: XType,
        modelPackage: String,
        logger: Logger,
        includeSuperClass: (XTypeElement) -> Boolean = { true }
    ): List<AttributeInfo> {
        val result = mutableListOf<AttributeInfo>()

        var currentSuperClassElement: XTypeElement? = originatingSuperClassType.typeElement

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

            currentSuperClassElement = currentSuperClassElement.superType?.typeElement
        }

        return result
    }

    data class SuperClassAttributes(
        val superClassPackage: String,
        val superClassAttributes: List<AttributeInfo>
    )

    private val inheritedEpoxyAttributes = mutableMapOf<String, SuperClassAttributes?>()

    private fun getEpoxyAttributesOnElement(
        classElement: XTypeElement,
        logger: Logger
    ): SuperClassAttributes? {
        return inheritedEpoxyAttributes.getOrPut(classElement.qualifiedName) {
            if (!classElement.isEpoxyModel(this)) {
                null
            } else {
                val attributes = classElement
                    .getDeclaredFields()
                    .filter { it.hasAnnotation(EpoxyAttribute::class) }
                    .map {
                        EpoxyProcessor.buildAttributeInfo(
                            it,
                            logger,
                            memoizer = this
                        )
                    }

                SuperClassAttributes(
                    superClassPackage = classElement.packageName,
                    superClassAttributes = attributes
                )
            }
        }
    }

    class SuperViewAnnotations(
        val viewPackageName: String,
        val annotatedElements: Map<KClass<out Annotation>, List<ViewElement>>
    )

    class ViewElement(
        val element: XElement,
        val isPackagePrivate: Boolean,
        val attributeInfo: Lazy<ViewAttributeInfo>
    ) {
        val simpleName: String by lazy {
            element.expectName
        }
    }

    private val annotationsOnSuperView = mutableMapOf<String, SuperViewAnnotations>()

    fun getAnnotationsOnViewSuperClass(
        superViewElement: XTypeElement,
        logger: Logger,
        resourceProcessor: ResourceScanner
    ): SuperViewAnnotations {
        return annotationsOnSuperView.getOrPut(superViewElement.qualifiedName) {

            val viewPackageName = superViewElement.packageName
            val annotatedElements =
                mutableMapOf<KClass<out Annotation>, MutableList<ViewElement>>()

            viewModelAnnotations.forEach { annotation ->
                superViewElement.getElementsAnnotatedWith(annotation).forEach { element ->
                    annotatedElements
                        .getOrPut(annotation) { mutableListOf() }
                        .add(
                            ViewElement(
                                element = element,
                                isPackagePrivate = Utils.isFieldPackagePrivate(element),
                                attributeInfo = lazy {
                                    ViewAttributeInfo(
                                        viewElement = superViewElement,
                                        viewPackage = viewPackageName,
                                        hasDefaultKotlinValue = false,
                                        viewAttributeElement = element,
                                        logger = logger,
                                        resourceProcessor = resourceProcessor,
                                        memoizer = this
                                    )
                                }
                            )
                        )
                }
            }

            SuperViewAnnotations(
                viewPackageName,
                annotatedElements
            )
        }
    }

    private val typeMap = mutableMapOf<XType, Type>()
    fun getType(xType: XType): Type {
        return typeMap.getOrPut(xType) { Type(xType, this) }
    }

    private val implementsModelCollectorMap = mutableMapOf<String, Boolean>()
    fun implementsModelCollector(classElement: XTypeElement): Boolean {
        return implementsModelCollectorMap.getOrPut(classElement.qualifiedName) {
            classElement.getSuperInterfaceElements().any {
                it.type.isEpoxyModelCollector(this)
            } || classElement.superType?.typeElement?.let { superClassElement ->
                // Also check the class hierarchy
                implementsModelCollector(superClassElement)
            } ?: false
        }
    }

    private val hasViewParentConstructorMap = mutableMapOf<String, Boolean>()
    fun hasViewParentConstructor(classElement: XTypeElement): Boolean {
        return hasViewParentConstructorMap.getOrPut(classElement.qualifiedName) {
            getClassConstructors(classElement, this).any {
                it.params.size == 1 && it.params[0].type == ClassNames.VIEW_PARENT
            }
        }
    }

    private val typeNameMap = mutableMapOf<XType, TypeName>()
    fun typeNameWithWorkaround(xType: XType): TypeName {
        if (!isKsp) return xType.typeName

        return typeNameMap.getOrPut(xType) {
            // The different subtypes of KSType do different things.
            if (xType is XArrayType) {
                return@getOrPut ArrayTypeName.of(xType.componentType.typeNameWithWorkaround(this))
            }

            val original = xType.typeName
            if (original.isPrimitive || (xType.isVoidObject() || xType.isVoid())) return@getOrPut original

            when (xType.javaClass.simpleName) {
                // not sure if type arguments are correct to handle differently, so leaving the original
                // implementation
                "KspTypeArgumentType" -> return@getOrPut original
            }

            // Handle the "DefaultKspType", which is the main case we are trying to patch.
            val ksType =
                xType.getFieldWithReflectionOrNull<KSType>("ksType") ?: return@getOrPut original
            // always box these. For primitives, typeName might return the primitive type but if we
            // wanted it to be a primitive, we would've resolved it to [KspPrimitiveType].
            val env = xType.getFieldWithReflection<XProcessingEnv>("env")
            val resolver = env.getFieldWithReflection<Resolver>("_resolver")
            ksType.typeName(resolver).tryBox()
        }
    }

    private val lightMethodsMap = mutableMapOf<XTypeElement, List<MethodInfoLight>>()

    /**
     * A function more efficient way to get basic information about elements, without type resolution.
     */
    fun getDeclaredMethodsLight(element: XTypeElement): List<MethodInfoLight> {
        return lightMethodsMap.getOrPut(element) {
            if (isKsp) {
                element.getFieldWithReflection<KSClassDeclaration>("declaration")
                    .getDeclaredFunctions()
                    .map {
                        MethodInfoLight(
                            name = it.simpleName.asString(),
                            docComment = it.docString
                        )
                    }.toList()
            } else {
                element.getDeclaredMethods().map {
                    MethodInfoLight(
                        name = it.name,
                        docComment = it.docComment
                    )
                }
            }
        }
    }
}

private val viewModelAnnotations = listOf(
    ModelProp::class,
    TextProp::class,
    CallbackProp::class,
    AfterPropsSet::class,
    OnVisibilityChanged::class,
    OnVisibilityStateChanged::class,
    OnViewRecycled::class
)
