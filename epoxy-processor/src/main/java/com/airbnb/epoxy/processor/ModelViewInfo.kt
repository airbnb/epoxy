package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XAnnotationBox
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XMethodElement
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.isVoid
import androidx.room.compiler.processing.isVoidObject
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.processor.resourcescanning.ResourceScanner
import com.airbnb.epoxy.processor.resourcescanning.ResourceValue
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import java.util.Collections

class ModelViewInfo(
    val viewElement: XTypeElement,
    private val environment: XProcessingEnv,
    val logger: Logger,
    private val configManager: ConfigManager,
    private val resourceProcessor: ResourceScanner,
    memoizer: Memoizer
) : GeneratedModelInfo(memoizer) {

    val resetMethodNames = Collections.synchronizedSet(mutableSetOf<String>())
    val visibilityStateChangedMethodNames = Collections.synchronizedSet(mutableSetOf<String>())
    val visibilityChangedMethodNames = Collections.synchronizedSet(mutableSetOf<String>())
    val afterPropsSetMethodNames = Collections.synchronizedSet(mutableSetOf<String>())
    private val viewAnnotation: XAnnotationBox<ModelView> =
        viewElement.requireAnnotation(ModelView::class)

    val saveViewState: Boolean
    val fullSpanSize: Boolean
    private val generatedModelSuffix: String

    /** All interfaces the view implements that have at least one prop set by the interface. */
    val viewInterfaces: List<XTypeElement>

    val viewAttributes: List<ViewAttributeInfo>
        get() = attributeInfo.filterIsInstance<ViewAttributeInfo>()

    init {
        superClassElement = lookUpSuperClassElement()
        this.superClassName = ParameterizedTypeName
            .get(superClassElement.className, viewElement.type.typeNameWithWorkaround(memoizer))

        generatedModelSuffix = configManager.generatedModelSuffix(viewElement)
        generatedName = buildGeneratedModelName(viewElement)
        // We don't have any type parameters on our generated model
        this.parameterizedGeneratedName = generatedName
        shouldGenerateModel = !viewElement.isAbstract()

        if (
            superClassElement.name != ClassNames.EPOXY_MODEL_UNTYPED.simpleName()
        ) {
            // If the view has a custom base model then we copy any custom constructors on it
            constructors.addAll(getClassConstructors(superClassElement))
        }

        collectMethodsReturningClassType(superClassElement)

        // The bound type is the type of this view
        modelType = viewElement.type.typeName

        saveViewState = viewAnnotation.value.saveViewState
        layoutParams = viewAnnotation.value.autoLayout
        fullSpanSize = viewAnnotation.value.fullSpan
        includeOtherLayoutOptions = configManager.includeAlternateLayoutsForViews(viewElement)

        val methodsOnView = viewElement.getDeclaredMethods()
        viewInterfaces = viewElement
            .getSuperInterfaceElements()
            .filter { interfaceElement ->
                // Only include the interface if the view has one of the interface methods annotated with a prop annotation
                val interfaceMethods = interfaceElement.getDeclaredMethods()
                methodsOnView.any { viewMethod ->
                    viewMethod.hasAnyOf(*ModelViewProcessor.modelPropAnnotationsArray) &&
                        interfaceMethods.any { interfaceMethod ->
                            // To keep this simple we only compare name and ignore parameters, should be close enough
                            viewMethod.name == interfaceMethod.name
                        }
                }
            }

        // Pass deprecated annotations on to the generated model
        annotations.addAll(
            viewElement.buildAnnotationSpecs({ DEPRECATED == it.simpleName() }, memoizer)
        )
    }

    private fun lookUpSuperClassElement(): XTypeElement {
        val classToExtend = viewAnnotation.getAsType("baseModelClass")
            ?.takeIf { !it.isVoidObject() && !it.isVoid() }
            ?: configManager.getDefaultBaseModel(viewElement)
            ?: return memoizer.epoxyModelClassElementUntyped

        val superElement =
            memoizer.validateViewModelBaseClass(classToExtend, logger, viewElement.name)

        return superElement ?: memoizer.epoxyModelClassElementUntyped
    }

    private fun buildGeneratedModelName(
        viewElement: XTypeElement,
    ): ClassName {
        val packageName = viewElement.packageName

        var className = viewElement.name
        className += generatedModelSuffix

        return ClassName.get(packageName, className)
    }

    fun buildProp(prop: XElement): ViewAttributeInfo {

        val hasDefaultKotlinValue = checkIsSetterWithSingleDefaultParam(prop)

        // Since our generated code is java we need jvmoverloads so that a no arg
        // version of the function is generated. However, the JvmOverloads annotation
        // is stripped when generating the java code so we can't check it directly (but it is available in KSP).
        // Instead, we verify that a no arg function of the same name exists
        val hasNoArgEquivalent = hasDefaultKotlinValue &&
            prop is XMethodElement &&
            (prop.hasAnnotation(JvmOverloads::class) || viewElement.hasOverload(prop, 0))

        if (hasDefaultKotlinValue && !hasNoArgEquivalent) {
            logger.logError(
                prop,
                "Model view function with default argument must be annotated with @JvmOverloads: %s#%s",
                viewElement.name,
                prop
            )
        }

        return ViewAttributeInfo(
            viewElement = viewElement,
            viewPackage = generatedName.packageName(),
            hasDefaultKotlinValue = hasDefaultKotlinValue && hasNoArgEquivalent,
            viewAttributeElement = prop,
            logger = logger,
            resourceProcessor = resourceProcessor,
            memoizer = memoizer
        )
    }

    fun addOnRecycleMethod(methodName: String) {
        resetMethodNames.add(methodName)
    }

    fun addOnVisibilityStateChangedMethod(methodName: String) {
        visibilityStateChangedMethodNames.add(methodName)
    }

    fun addOnVisibilityChangedMethod(methodName: String) {
        visibilityChangedMethodNames.add(methodName)
    }

    fun addAfterPropsSetMethod(methodName: String) {
        afterPropsSetMethodNames.add(methodName)
    }

    fun getLayoutResource(resourceProcessor: ResourceScanner): ResourceValue {
        val annotation = viewElement.requireAnnotation(ModelView::class)
        val layoutValue = annotation.value.defaultLayout
        if (layoutValue != 0) {
            return resourceProcessor.getResourceValue(ModelView::class, viewElement, "defaultLayout")
                ?: error("ModelView default layout not found for $viewElement")
        }

        val modelViewConfig = configManager.getModelViewConfig(viewElement)

        if (modelViewConfig != null) {
            return modelViewConfig.getNameForView(viewElement)
        }

        logger.logError(viewElement, "UnabletypeNameWorkaround to get layout resource for view %s", viewElement.name)
        return ResourceValue(0)
    }

    private fun checkIsSetterWithSingleDefaultParam(element: XElement): Boolean {
        if (element !is XMethodElement) return false
        return element.parameters.singleOrNull()?.hasDefaultValue == true
    }

    override fun additionalOriginatingElements() = listOf(viewElement)
}
