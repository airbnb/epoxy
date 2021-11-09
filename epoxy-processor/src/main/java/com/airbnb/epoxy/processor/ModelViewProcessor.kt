package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XExecutableElement
import androidx.room.compiler.processing.XMethodElement
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XRoundEnv
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.XVariableElement
import com.airbnb.epoxy.AfterPropsSet
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.OnViewRecycled
import com.airbnb.epoxy.OnVisibilityChanged
import com.airbnb.epoxy.OnVisibilityStateChanged
import com.airbnb.epoxy.TextProp
import com.airbnb.epoxy.processor.Utils.validateFieldAccessibleViaGeneratedCode
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.squareup.javapoet.TypeName
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import java.util.HashMap
import java.util.HashSet
import java.util.concurrent.ConcurrentHashMap
import javax.tools.Diagnostic
import kotlin.contracts.contract
import kotlin.reflect.KClass

class ModelViewProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ModelViewProcessor(environment)
    }
}

@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
class ModelViewProcessor @JvmOverloads constructor(
    kspEnvironment: SymbolProcessorEnvironment? = null
) : BaseProcessorWithPackageConfigs(kspEnvironment) {

    override val usesPackageEpoxyConfig: Boolean = false
    override val usesModelViewConfig: Boolean = true

    private val modelClassMap = ConcurrentHashMap<XTypeElement, ModelViewInfo>()
    private val styleableModelsToWrite = mutableListOf<ModelViewInfo>()

    override fun additionalSupportedAnnotations(): List<KClass<*>> = listOf(
        ModelView::class,
        TextProp::class,
        CallbackProp::class
    )

    override fun processRound(
        environment: XProcessingEnv,
        round: XRoundEnv,
        memoizer: Memoizer,
        timer: Timer,
        roundNumber: Int
    ): List<XElement> {
        super.processRound(environment, round, memoizer, timer, roundNumber)
        timer.markStepCompleted("package config processing")

        // We have a very common case of needing to wait for Paris styleables to be generated before
        // we can fully process our models and generate code. To support this we need to tell KSP to
        // defer symbols. However, this also causes KSP to reprocess the same annotations and create
        // duplicates if we try to save the previous models, and with KSP it is also not valid to use
        // symbols across rounds and that can cause errors.
        // Java AP on the other hand will not reprocess annotated elements so we need to hold on to
        // them across rounds.
        // To support this case in an efficient way in KSP we check for any Paris dependencies and
        // bail ASAP in that case to avoid reprocessing as much as possible.
        // Paris only ever generates code in the first round, so it should be safe to rely on this.
        val shouldDeferElementsIfHasParisDependency = isKsp() && roundNumber == 1
        val elementsToDefer =
            processViewAnnotations(round, memoizer, shouldDeferElementsIfHasParisDependency)

        timer.markStepCompleted("process View Annotations")

        if (elementsToDefer.isNotEmpty()) {
            return elementsToDefer
        }

        // Avoid doing the work to look up the rest of the annotations in model view classes
        // if no new  model view classes were found.
        if (modelClassMap.isNotEmpty()) {
            val classTypes = modelClassMap.keys.toList()
            processSetterAnnotations(classTypes, memoizer)
            timer.markStepCompleted("process setter Annotations")

            processResetAnnotations(classTypes, memoizer)
            timer.markStepCompleted("process reset Annotations")

            processVisibilityStateChangedAnnotations(classTypes, memoizer)
            processVisibilityChangedAnnotations(classTypes, memoizer)
            timer.markStepCompleted("process visibility Annotations")

            processAfterBindAnnotations(classTypes, memoizer)
            timer.markStepCompleted("process after bind Annotations")

            updateViewsForInheritedViewAnnotations(memoizer)
            timer.markStepCompleted("update For Inherited Annotations")

            // Group overloads after inheriting methods from super classes so those can be included in
            // the groups as well.
            groupOverloads()
            timer.markStepCompleted("group overloads")

            // Up until here our code generation has assumed that that all attributes in a group are
            // view attributes (and not attributes inherited from a base model class), so this should be
            // done after grouping attributes, and these attributes should not be grouped.
            // No code to bind these attributes is generated, as it is assumed that the original model
            // handles its own bind (also we can't know how to bind these).
            updatesViewsForInheritedBaseModelAttributes(memoizer)
            timer.markStepCompleted("updates for inherited Attributes")

            addStyleAttributes()
            timer.markStepCompleted("add style attributes")
        }

        // This may write previously generated models that were waiting for their style builder
        // to be generated.
        writeJava(environment, memoizer, timer)

        generatedModels.addAll(modelClassMap.values)
        modelClassMap.clear()

        if (roundNumber > 2 && styleableModelsToWrite.isNotEmpty()) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Unable to find Paris generated code for styleable Epoxy models. Is Paris configured correctly?"
            )
        }

        return emptyList()
    }

    private fun processViewAnnotations(
        round: XRoundEnv,
        memoizer: Memoizer,
        shouldDeferElementsIfHasParisDependency: Boolean
    ): List<XElement> {
        val modelViewElements = round.getElementsAnnotatedWith(ModelView::class)

        if (shouldDeferElementsIfHasParisDependency && modelViewElements.any { it.hasStyleableAnnotation() }) {
            return modelViewElements.toList()
        }

        modelViewElements
            .forEach("processViewAnnotations") { viewElement ->
                if (!validateViewElement(viewElement, memoizer)) {
                    return@forEach
                }

                modelClassMap[viewElement] = ModelViewInfo(
                    viewElement,
                    environment,
                    logger,
                    configManager,
                    resourceProcessor,
                    memoizer
                )
            }

        return emptyList()
    }

    private fun validateViewElement(viewElement: XElement, memoizer: Memoizer): Boolean {
        contract {
            returns(true) implies (viewElement is XTypeElement)
        }
        if (viewElement !is XTypeElement) {
            logger.logError(
                "${ModelView::class.simpleName} annotations can only be on a class",
                viewElement
            )
            return false
        }

        if (viewElement.isPrivate()) {
            logger.logError(
                "${ModelView::class.simpleName} annotations must not be on private classes.",
                viewElement
            )
            return false
        }

        // Nested classes must be static
        if (viewElement.enclosingTypeElement != null) {
            logger.logError(
                "Classes with ${ModelView::class.java} annotations cannot be nested.",
                viewElement
            )
            return false
        }

        if (!viewElement.type.isSubTypeOf(memoizer.androidViewType)) {
            logger.logError(
                "Classes with ${ModelView::class.java} annotations must extend " +
                    "android.view.View.",
                viewElement
            )
            return false
        }

        return true
    }

    private fun processSetterAnnotations(classTypes: List<XTypeElement>, memoizer: Memoizer) {
        for (propAnnotation in modelPropAnnotations) {
            classTypes.getElementsAnnotatedWith(propAnnotation).mapNotNull { prop ->
                val enclosingElement = prop.enclosingTypeElement ?: return@mapNotNull null
                // Interfaces can use model property annotations freely, they will be processed if
                // and when implementors of that interface are processed. This is particularly
                // useful for Kotlin delegation where the model view class may not be overriding
                // the interface properties directly, and so doesn't have an opportunity to annotate
                // them with Epoxy model property annotations.
                if (enclosingElement.isInterface()) {
                    return@mapNotNull null
                }

                val info = getModelInfoForPropElement(prop)
                if (info == null) {
                    logger.logError(
                        "${propAnnotation.simpleName} annotation can only be used in classes " +
                            "annotated with ${ModelView::class.java.simpleName} " +
                            "(${enclosingElement.name}#$prop)",
                        prop
                    )
                    return@mapNotNull null
                }

                // JvmOverloads is used on properties with default arguments, which we support.
                // However, the generated no arg version of the function will also have the
                // @ModelProp annotation so we need to ignore it when it is processed.
                // However, the JvmOverloads annotation is removed in the java class so we need
                // to manually look for a valid overload function.
                if (prop is XMethodElement &&
                    prop.parameters.isEmpty() &&
                    info.viewElement.findOverload(
                            prop,
                            1
                        )?.hasAnyOf(*modelPropAnnotationsArray) == true
                ) {
                    return@mapNotNull null
                }

                if (!validatePropElement(prop, propAnnotation.java, memoizer)) {
                    return@mapNotNull null
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

    private fun groupOverloads() {
        modelClassMap.values.forEach { viewInfo ->
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
                                "'$customGroup' at ${viewInfo.viewElement.name}#" +
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
        prop: XElement,
        propAnnotation: Class<out Annotation>,
        memoizer: Memoizer
    ): Boolean {
        return when (prop) {
            is XExecutableElement -> validateExecutableElement(
                prop,
                propAnnotation,
                1,
                memoizer = memoizer
            )
            is XVariableElement -> validateVariableElement(prop, propAnnotation)
            else -> {
                logger.logError(
                    prop,
                    "%s annotations can only be on a method or a field(element: %s)",
                    propAnnotation,
                    prop
                )
                return false
            }
        }
    }

    private fun validateVariableElement(
        field: XVariableElement,
        annotationClass: Class<*>
    ): Boolean {
        return validateFieldAccessibleViaGeneratedCode(
            field,
            annotationClass,
            logger
        )
    }

    private fun validateExecutableElement(
        element: XElement,
        annotationClass: Class<*>,
        paramCount: Int,
        checkTypeParameters: List<TypeName>? = null,
        memoizer: Memoizer
    ): Boolean {
        contract {
            returns(true) implies (element is XMethodElement)
        }

        if (element !is XMethodElement) {
            logger.logError(
                element,
                "%s annotations can only be on a method (element: %s)",
                annotationClass::class.java.simpleName,
                element
            )
            return false
        }

        val parameters = element.parameters
        if (parameters.size != paramCount) {
            logger.logError(
                element,
                "Methods annotated with %s must have exactly %s parameter (method: %s)",
                annotationClass::class.java.simpleName, paramCount, element.name
            )
            return false
        }

        checkTypeParameters?.let { expectedTypeParameters ->
            // Check also the parameter types
            var hasErrors = false
            parameters.forEachIndexed { i, parameter ->
                val typeName = parameter.type.typeNameWithWorkaround(memoizer)
                val expectedType = expectedTypeParameters[i]
                hasErrors = hasErrors ||
                    (typeName != expectedType.box() && typeName != expectedType.unbox())
            }
            if (hasErrors) {
                logger.logError(
                    element,
                    "Methods annotated with %s must have parameter types %s, " +
                        "found: %s (method: %s)",
                    annotationClass::class.java.simpleName,
                    expectedTypeParameters,
                    parameters.map { it.type },
                    element.name
                )
            }
        }

        if (element.isStatic() || element.isPrivate()) {
            logger.logError(
                element,
                "Methods annotated with %s cannot be private or static (method: %s)",
                annotationClass::class.java.simpleName, element.name
            )
            return false
        }

        return true
    }

    private fun processResetAnnotations(classTypes: List<XTypeElement>, memoizer: Memoizer) {
        classTypes.getElementsAnnotatedWith(OnViewRecycled::class).mapNotNull { recycleMethod ->
            if (!validateResetElement(recycleMethod, memoizer)) {
                return@mapNotNull null
            }

            val info = getModelInfoForPropElement(recycleMethod)
            if (info == null) {
                logger.logError(
                    "%s annotation can only be used in classes annotated with %s",
                    OnViewRecycled::class.java, ModelView::class.java
                )
                return@mapNotNull null
            }

            recycleMethod.expectName to info
        }.forEach { (methodName, modelInfo) ->
            // Do this after, synchronously, to preserve function ordering in the view.
            // If there are multiple functions with this annotation this allows them
            // to be called in predictable order from top to bottom of the class, which
            // some users may depend on.
            modelInfo.addOnRecycleMethod(methodName)
        }
    }

    private fun processVisibilityStateChangedAnnotations(
        classTypes: List<XTypeElement>,
        memoizer: Memoizer
    ) {
        classTypes.getElementsAnnotatedWith(OnVisibilityStateChanged::class)
            .mapNotNull { visibilityMethod ->
                if (!validateVisibilityStateChangedElement(visibilityMethod, memoizer)) {
                    return@mapNotNull null
                }

                val info = getModelInfoForPropElement(visibilityMethod)
                if (info == null) {
                    logger.logError(
                        "%s annotation can only be used in classes annotated with %s",
                        OnVisibilityStateChanged::class.java, ModelView::class.java
                    )
                    return@mapNotNull null
                }

                visibilityMethod.expectName to info
            }.forEach { (methodName, modelInfo) ->
                // Do this after, synchronously, to preserve function ordering in the view.
                // If there are multiple functions with this annotation this allows them
                // to be called in predictable order from top to bottom of the class, which
                // some users may depend on.
                modelInfo.addOnVisibilityStateChangedMethod(methodName)
            }
    }

    private fun processVisibilityChangedAnnotations(
        classTypes: List<XTypeElement>,
        memoizer: Memoizer
    ) {
        classTypes.getElementsAnnotatedWith(OnVisibilityChanged::class).mapNotNull { visibilityMethod ->
            if (!validateVisibilityChangedElement(visibilityMethod, memoizer)) {
                return@mapNotNull null
            }

            val info = getModelInfoForPropElement(visibilityMethod)
            if (info == null) {
                logger.logError(
                    visibilityMethod,
                    "%s annotation can only be used in classes annotated with %s",
                    OnVisibilityChanged::class.java, ModelView::class.java
                )
                return@mapNotNull null
            }

            visibilityMethod.expectName to info
        }.forEach { (methodName, modelInfo) ->
            // Do this after, synchronously, to preserve function ordering in the view.
            // If there are multiple functions with this annotation this allows them
            // to be called in predictable order from top to bottom of the class, which
            // some users may depend on.
            modelInfo.addOnVisibilityChangedMethod(methodName)
        }
    }

    private fun processAfterBindAnnotations(classTypes: List<XTypeElement>, memoizer: Memoizer) {
        classTypes.getElementsAnnotatedWith(AfterPropsSet::class).mapNotNull { afterPropsMethod ->
            if (!validateAfterPropsMethod(afterPropsMethod, memoizer)) {
                return@mapNotNull null
            }

            val info = getModelInfoForPropElement(afterPropsMethod)
            if (info == null) {
                logger.logError(
                    afterPropsMethod,
                    "%s annotation can only be used in classes annotated with %s",
                    AfterPropsSet::class.java, ModelView::class.java
                )
                return@mapNotNull null
            }

            afterPropsMethod.expectName to info
        }.forEach { (methodName, modelInfo) ->
            // Do this after, synchronously, to preserve function ordering in the view.
            // If there are multiple functions with this annotation this allows them
            // to be called in predictable order from top to bottom of the class, which
            // some users may depend on.
            modelInfo.addAfterPropsSetMethod(methodName)
        }
    }

    private fun validateAfterPropsMethod(method: XElement, memoizer: Memoizer): Boolean {
        contract {
            returns(true) implies (method is XMethodElement)
        }
        return validateExecutableElement(method, AfterPropsSet::class.java, 0, memoizer = memoizer)
    }

    /** Include props and reset methods from super class views.  */
    private fun updateViewsForInheritedViewAnnotations(memoizer: Memoizer) {

        modelClassMap.values.forEach { view ->
            // We walk up the super class tree and look for any elements with epoxy annotations.
            // This approach lets us capture views that we've already processed as well as views
            // in other libraries that we wouldn't have otherwise processed.

            view.viewElement.iterateSuperClasses { superViewElement ->
                val annotationsOnViewSuperClass = memoizer.getAnnotationsOnViewSuperClass(
                    superViewElement,
                    logger,
                    resourceProcessor
                )

                val isSamePackage by lazy {
                    annotationsOnViewSuperClass.viewPackageName == view.viewElement.packageName
                }

                fun forEachElementWithAnnotation(
                    annotations: List<KClass<out Annotation>>,
                    function: (Memoizer.ViewElement) -> Unit
                ) {

                    annotationsOnViewSuperClass.annotatedElements
                        .filterKeys { annotation ->
                            annotation in annotations
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

                    // If an annotated prop method has a default value it will also have @JvmOverloads
                    // so java source in KAPT sees both a zero param and and 1 param method. We just
                    // ignore the empty param version.
                    if (it.element is XMethodElement && it.element.parameters.size != 1) {
                        return@forEachElementWithAnnotation
                    }

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
    private fun updatesViewsForInheritedBaseModelAttributes(memoizer: Memoizer) {
        modelClassMap.values.forEach { modelViewInfo ->
            // Skip generated model super classes since it will already contain all of the functions
            // necessary for included attributes, and duplicating them is a waste.
            if (modelViewInfo.isSuperClassAlsoGenerated) return@forEach

            memoizer.getInheritedEpoxyAttributes(
                modelViewInfo.superClassElement.type,
                modelViewInfo.generatedName.packageName(),
                logger
            ).let { modelViewInfo.addAttributes(it) }
        }
    }

    private fun addStyleAttributes() {
        modelClassMap
            .values
            .filter("addStyleAttributes") { it.viewElement.hasStyleableAnnotation() }
            .also { styleableModelsToWrite.addAll(it) }
    }

    private fun validateResetElement(resetMethod: XElement, memoizer: Memoizer): Boolean {
        contract {
            returns(true) implies (resetMethod is XMethodElement)
        }
        return validateExecutableElement(
            resetMethod,
            OnViewRecycled::class.java,
            0,
            memoizer = memoizer
        )
    }

    private fun validateVisibilityStateChangedElement(
        visibilityMethod: XElement,
        memoizer: Memoizer
    ): Boolean {
        contract {
            returns(true) implies (visibilityMethod is XMethodElement)
        }

        return validateExecutableElement(
            visibilityMethod,
            OnVisibilityStateChanged::class.java,
            1,
            checkTypeParameters = listOf(TypeName.INT),
            memoizer = memoizer
        )
    }

    private fun validateVisibilityChangedElement(visibilityMethod: XElement, memoizer: Memoizer): Boolean {
        contract {
            returns(true) implies (visibilityMethod is XMethodElement)
        }

        return validateExecutableElement(
            visibilityMethod,
            OnVisibilityChanged::class.java,
            4,
            checkTypeParameters = listOf(TypeName.FLOAT, TypeName.FLOAT, TypeName.INT, TypeName.INT),
            memoizer = memoizer
        )
    }

    private fun writeJava(processingEnv: XProcessingEnv, memoizer: Memoizer, timer: Timer) {
        val modelsToWrite = modelClassMap.values.toMutableList()
        modelsToWrite.removeAll(styleableModelsToWrite)

        val hasStyleableModels = styleableModelsToWrite.isNotEmpty()

        styleableModelsToWrite.filter {
            tryAddStyleBuilderAttribute(it, processingEnv, memoizer)
        }.let {
            modelsToWrite.addAll(it)
            styleableModelsToWrite.removeAll(it)
        }
        if (hasStyleableModels) {
            timer.markStepCompleted("update models with Paris Styleable builder")
        }

        val modelWriter = createModelWriter(memoizer)
        ModelViewWriter(modelWriter, this)
            .writeModels(modelsToWrite, originatingConfigElements())

        if (styleableModelsToWrite.isEmpty()) {
            // Make sure all models have been processed and written before we generate interface information
            modelWriter.writeFilesForViewInterfaces()
        }

        timer.markStepCompleted("write generated files")
    }

    private fun getModelInfoForPropElement(element: XElement): ModelViewInfo? =
        element.enclosingTypeElement?.let { modelClassMap[it] }

    companion object {
        val modelPropAnnotations: List<KClass<out Annotation>> = listOf(
            ModelProp::class,
            TextProp::class,
            CallbackProp::class
        )

        val modelPropAnnotationsArray: Array<KClass<out Annotation>> =
            modelPropAnnotations.toTypedArray()

        val modelPropAnnotationSimpleNames: Set<String> =
            modelPropAnnotations.mapNotNull { it.simpleName }.toSet()
    }
}
