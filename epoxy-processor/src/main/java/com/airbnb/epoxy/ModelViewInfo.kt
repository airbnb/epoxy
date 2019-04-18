package com.airbnb.epoxy

import com.airbnb.epoxy.Utils.isEpoxyModel
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.declaresDefaultValue
import me.eugeniomarletti.kotlin.metadata.extractFullName
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
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
            kotlinMetadata?.findMatchingSetter(prop)?.valueParameterList?.single()?.declaresDefaultValue == true

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

    private fun KotlinClassMetadata.findMatchingSetter(functionElement: ExecutableElement): ProtoBuf.FunctionOrBuilder? {
        // Given an element representing a function we want to find the corresponding function information
        // in the kotlin metadata of this class. We do this by searching for a function matching the same
        // name, param count, param name, and param type.
        // This assumes the param count we're looking for is 1 since this is an epoxy setter.
        require(functionElement.parameters.size == 1) { "Expected function $functionElement to have exactly 1 parameter" }
        val paramName = functionElement.parameters.single().simpleName.toString()
        val functionName = functionElement.simpleName.toString()

        // Checking function name, param count, and param name are easy
        val matchingFunctions = data.classProto.functionOrBuilderList
            .asSequence()
            .filter { it.hasName() && data.nameResolver.getString(it.name) == functionName }
            .filter { it.valueParameterCount == 1 }
            .filter {
                val param = it.valueParameterList.single()
                param.hasName() && data.nameResolver.getString(param.name) == paramName
            }
            .toList()

        // However, checking param type is hard. If there are function overloads that have the same
        // naming, but with different param types, then we need to be able to distinguish between
        // them. This is difficult because the type information from the Kotlin metadata doesn't
        // have a clear mapping to the Element type information.
        if (matchingFunctions.size <= 1) {
            return matchingFunctions.firstOrNull()
        }
        val propTypeClassName =
            functionElement.parameters.single().asType().toString().let { ClassNameData.from(it) }

        return matchingFunctions.map { function ->
            function.valueParameterList.single().type
                .extractFullName(data, outputTypeAlias = false)
                .replace("`", "") // Back ticks separate all the words
                .let { function to ClassNameData.from(it) }
        }.sortedWith(Comparator<Pair<ProtoBuf.FunctionOrBuilder, ClassNameData>> { p0, p1 ->
            // Given two kotlin metadata functions and their class names, this comparator
            // compares them to see which is more likely to be the same as the function Element
            // we are trying to match with.
            compareNames(propTypeClassName, p0.second, p1.second)
        })
            .last().first
    }

    private fun compareNames(
        targetType: ClassNameData,
        leftType: ClassNameData,
        rightType: ClassNameData
    ): Int {
        // The package names of kotlin metadata types use kotlin versions of things (ie kotlin.String)
        // whereas the java Element types use the Java package names. This makes it hard to directly
        // compare types. Additionally, generic information is presented in slightly different formats
        // To avoid errors where we don't compare types correctly, we instead look for the closest
        // matching types, and sort to see which type we think matches the best.

        // Sanity check that generic type counts are correct
        if (leftType.hasGenericTypes == rightType.hasGenericTypes) {
            if (leftType.hasGenericTypes != targetType.hasGenericTypes) {
                // Neither of the types has the right generic information, so they are equally unfit
                return 0
            }
        } else {
            return if (leftType.hasGenericTypes == targetType.hasGenericTypes) 1 else -1
        }

        targetType.packageNames.forEachIndexed { index, name ->
            val leftName = leftType.packageNames.getOrNull(index)
            val rightName = rightType.packageNames.getOrNull(index)

            // If names are equal then don't judge based on them (difference could be in generic types)
            // If neither side has a name that matches then don't judge based on naming alone.
            // But, if this is the simple name, then it must at least match (package name won't necessarily match)
            when {
                leftName == name && rightName != name -> return 1
                rightName == name -> return -1
                index == 0 -> return 0
            }
        }

        targetType.typeNames.forEachIndexed { index, typeData ->
            val leftTypeData = leftType.typeNames.getOrNull(index)
                ?: throw IllegalStateException("Expected type at index $index for $leftType")

            val rightTypeData = rightType.typeNames.getOrNull(index) ?: throw IllegalStateException(
                "Expected type at index $index for $rightType"
            )

            val comparisonResult = compareNames(typeData, leftTypeData, rightTypeData)
            if (comparisonResult != 0) {
                return comparisonResult
            }
        }

        return 0
    }

    data class ClassNameData(
        val reflectionName: String,
        val fullNameWithoutTypes: String,
        val typeNames: List<ClassNameData>
    ) {
        val packageNames = fullNameWithoutTypes.split(".").reversed()
        val hasGenericTypes = typeNames.isNotEmpty()

        companion object {
            fun from(reflectionName: String): ClassNameData {
                val fullNameWithoutTypes = reflectionName.substringBefore("<")

                val typeNames = reflectionName
                    .takeIf { it.contains("<") }
                    ?.substringAfter("<")
                    ?.substringBeforeLast(">")
                    ?.split(",")
                    ?.map { it.trim() }
                    ?.filter { it.isNotBlank() }
                    ?.takeIf { it.isNotEmpty() }
                    ?.map { ClassNameData.from(it) }
                    ?: emptyList()

                return ClassNameData(reflectionName, fullNameWithoutTypes, typeNames)
            }
        }
    }
}
