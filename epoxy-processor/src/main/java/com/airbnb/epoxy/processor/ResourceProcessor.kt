package com.airbnb.epoxy.processor

import com.airbnb.epoxy.EpoxyDataBindingLayouts
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.ModelView
import com.squareup.javapoet.ClassName
import com.sun.source.util.Trees
import com.sun.tools.javac.code.Symbol
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.tree.JCTree.JCFieldAccess
import com.sun.tools.javac.tree.TreeScanner
import java.util.ArrayList
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Scans R files and and compiles resource values in those R classes. This allows us to look
 * up raw resource values (eg 23523452) and convert that to the resource name (eg
 * R.layout.my_view) so that we can properly reference that resource. This is important in library
 * projects where the R value at process time can be different from the final R value in the app.
 *
 *
 * This is adapted from Butterknife. https://github.com/JakeWharton/butterknife/pull/613
 */
class ResourceProcessor internal constructor(
    processingEnv: ProcessingEnvironment,
    private val logger: Logger,
    private val elementUtils: Elements,
    private val typeUtils: Types
) {
    private val rClassNameMap: MutableMap<String, ClassName> = ConcurrentHashMap()
    val trees: Trees?

    /** Maps the name of an R class to a list of all of the resources in that class.  */
    private val rClassResources: MutableMap<ClassName?, List<ResourceValue>> = ConcurrentHashMap()

    init {
        trees = try {
            Trees.instance(processingEnv)
        } catch (ignored: IllegalArgumentException) {
            try {
                // Get original ProcessingEnvironment from Gradle-wrapped one or KAPT-wrapped one.
                // In Kapt, its field is called "delegate". In Gradle's, it's called "processingEnv"
                processingEnv.javaClass.declaredFields.mapNotNull { field ->
                    if (field.name == "delegate" || field.name == "processingEnv") {
                        field.isAccessible = true
                        val javacEnv = field[processingEnv] as ProcessingEnvironment
                        Trees.instance(javacEnv)
                    } else {
                        null
                    }
                }.firstOrNull()
            } catch (ignored2: Throwable) {
                null
            }
        }
    }

    fun getLayoutInAnnotation(
        element: Element,
        annotationClass: Class<out Annotation>
    ): ResourceValue {
        val layouts = getLayoutsInAnnotation(element, annotationClass)
        if (layouts.size != 1) {
            logger.logError(
                "Expected exactly 1 layout resource in the %s annotation but received %s. Annotated " +
                    "element is %s",
                annotationClass.simpleName, layouts.size, element.simpleName
            )
            if (layouts.isEmpty()) {
                // Just pass back something so the code can compile before the error logger prints
                return ResourceValue(0)
            }
        }
        return layouts[0]
    }

    /**
     * Get detailed information about the layout resources that are parameters to the given
     * annotation.
     */
    fun getLayoutsInAnnotation(
        element: Element,
        annotationClass: Class<out Annotation>
    ): List<ResourceValue> {
        val layoutValues = getLayoutValues(element, annotationClass)
        return getResourcesInAnnotation(element, annotationClass, "layout", layoutValues)
    }

    fun getStringResourceInAnnotation(
        element: Element,
        annotationClass: Class<*>,
        resourceValue: Int
    ): ResourceValue {
        return getResourceInAnnotation(element, annotationClass, "string", resourceValue)
    }

    fun getResourceInAnnotation(
        element: Element,
        annotationClass: Class<*>,
        resourceType: String?,
        resourceValue: Int
    ): ResourceValue {
        val layouts =
            getResourcesInAnnotation(
                element,
                annotationClass,
                resourceType,
                listOf(resourceValue)
            )
        if (layouts.size != 1) {
            logger.logError(
                "Expected exactly 1 %s resource in the %s annotation but received %s. Annotated " +
                    "element is %s",
                resourceType!!, annotationClass.simpleName, layouts.size, element.simpleName
            )
            if (layouts.isEmpty()) {
                // Just pass back something so the code can compile before the error logger prints
                return ResourceValue(0)
            }
        }
        return layouts[0]
    }

    fun getResourcesInAnnotation(
        element: Element,
        annotationClass: Class<*>,
        resourceType: String?,
        resourceValues: List<Int>
    ): List<ResourceValue> {
        val resources: MutableList<ResourceValue> = ArrayList(resourceValues.size)

        // This should not happen, but sanity check here just in case
        checkNotNull(trees) {
            "Compiler Trees instance was not found in processing environment"
        }

        val tree = synchronized(trees) {
            trees.getTree(element, getAnnotationMirror(element, annotationClass)) as? JCTree
        }

        // tree can be null if the references are compiled types and not source
        if (tree != null) {
            val scanner = AnnotationResourceParamScanner()
            // Collects details about the layout resource used for the annotation parameter
            scanner.setCurrentAnnotationDetails(element, annotationClass, resourceType)
            tree.accept(scanner)
            resources.addAll(scanner.getResults())
        }

        // Resource values may not have been picked up by the scanner if they are hardcoded.
        // In that case we just use the hardcoded value without an R class
        if (resources.size != resourceValues.size) {
            for (layoutValue in resourceValues) {
                if (!isLayoutValueInResources(resources, layoutValue)) {
                    resources.add(ResourceValue(layoutValue))
                }
            }
        }
        return resources
    }

    private fun isLayoutValueInResources(
        resources: List<ResourceValue>,
        layoutValue: Int
    ): Boolean {
        for (resource in resources) {
            if (resource.value == layoutValue) {
                return true
            }
        }
        return false
    }

    private fun getAnnotationMirror(
        element: Element,
        annotationClass: Class<*>
    ): AnnotationMirror? {
        for (annotationMirror in element.annotationMirrorsThreadSafe) {
            if (annotationMirror.annotationType.toString()
                == annotationClass.canonicalName
            ) {
                return annotationMirror
            }
        }
        logger.logError(
            "Unable to get %s annotation on model %",
            annotationClass.simpleName, element.simpleName
        )
        return null
    }

    val rClassNames: List<ClassName>
        get() = rClassNameMap.values.toList()

    /**
     * Returns a list of layout resources whose name contains the given layout as a prefix.
     */
    fun getAlternateLayouts(layout: ResourceValue): List<ResourceValue> {
        val layoutClassName = layout.className ?: return emptyList()

        synchronizedByValue(layoutClassName) {
            if (rClassResources.isEmpty()) {
                // This will only have been filled if at least one view has a layout in it's annotation.
                // If all view's use their default layout then resources haven't been parsed yet and we can
                // do it now
                val rLayoutClassElement: Element = Utils.getElementByName(
                    layoutClassName,
                    elementUtils,
                    typeUtils
                )
                saveResourceValuesForRClass(layoutClassName, rLayoutClassElement)
            }
        }

        val layouts = rClassResources[layoutClassName]
        if (layouts == null) {
            logger.logError("No layout files found for R class: %s", layoutClassName)
            return emptyList()
        }

        val target = layout.resourceName + "_"
        return layouts.filter { otherLayout ->
            otherLayout.resourceName?.startsWith(target) == true
        }
    }

    /**
     * @param rClass Class name for a resource, like R.layout
     * @param resourceClass The class element representing that resource eg the R.layout class
     */

    private fun saveResourceValuesForRClass(
        rClass: ClassName?,
        resourceClass: Element
    ) {
        if (rClass == null) return

        synchronizedByValue(rClass) {
            if (rClassResources.containsKey(rClass)) return

            val resourceNames = resourceClass.enclosedElementsThreadSafe
                .filterIsInstance<VariableElement>()
                .map { it.simpleName.toString() }
                .map {
                    ResourceValue(
                        rClass,
                        it,
                        value = 0 // Don't care about this for our use case
                    )
                }

            rClassResources[rClass] = resourceNames
        }
    }

    /**
     * Scans annotations that have resources as parameters. It supports both one resource parameter,
     * and parameters in an array. The R class, resource name, and value, is extracted to create a
     * corresponding [ResourceValue] for each resource.
     */
    private inner class AnnotationResourceParamScanner : TreeScanner() {
        private val results: MutableList<ResourceValue> = ArrayList()
        private var element: Element? = null
        private var annotationClass: Class<*>? = null

        /** Eg "string", "layout", etc  */
        private var resourceType: String? = null

        fun getResults(): List<ResourceValue> = results

        override fun visitSelect(jcFieldAccess: JCFieldAccess) {
            // This "visit" method is called for each parameter in the annotation, but only if the
            // parameter is a field type (eg R.layout.resource_name is a field inside the R.layout
            // class). This means this method will not pick up things like booleans and strings.

            // This is the resource parameter inside the annotation
            val symbol = jcFieldAccess.sym
            if (symbol is Symbol.VarSymbol &&
                symbol.getEnclosingElement() != null && // The R.resourceType class
                symbol.getEnclosingElement().enclosingElement != null && // The R class
                symbol.getEnclosingElement().enclosingElement.enclClass() != null
            ) {
                val result = parseResourceSymbol(symbol as VariableElement)
                if (result != null) {
                    results.add(result)
                }
            }
        }

        private fun parseResourceSymbol(symbol: VariableElement): ResourceValue? {
            val resourceClass = symbol.enclosingElement as TypeElement

            // eg com.airbnb.epoxy.R
            val rClass = (resourceClass.enclosingElement as TypeElement).qualifiedName.toString()

            // eg com.airbnb.epoxy.R.layout
            val resourceClassName = resourceClass.qualifiedName.toString()

            // Make sure this is the right resource type
            if ("$rClass.$resourceType" != resourceClassName) {
                logger
                    .logError(
                        "%s annotation requires a %s resource but received %s. (Element: %s)",
                        annotationClass!!.simpleName, resourceType!!, resourceClass,
                        element!!.simpleName
                    )
                return null
            }

            // eg button_layout, as in R.layout.button_layout
            val resourceName = symbol.simpleName.toString()
            val resourceValue = symbol.constantValue
            if (resourceValue !is Int) {
                logger.logError(
                    "%s annotation requires an int value but received %s. (Element: %s)",
                    annotationClass!!.simpleName, symbol.simpleName, element!!.simpleName
                )
                return null
            }
            val rClassName = getClassName(resourceClassName, resourceType)
            saveResourceValuesForRClass(rClassName, resourceClass)
            return ResourceValue(rClassName!!, resourceName, resourceValue)
        }

        fun setCurrentAnnotationDetails(
            element: Element?,
            annotationClass: Class<*>?,
            resourceType: String?
        ) {
            this.element = element
            this.annotationClass = annotationClass
            this.resourceType = resourceType
        }
    }

    /**
     * Builds a JavaPoet ClassName from the string value of an R class. This is memoized since there
     * should be very few different R classes used.
     */
    private fun getClassName(
        rClass: String,
        resourceType: String?
    ): ClassName? = synchronized(rClassNameMap) {
        rClassNameMap.getOrPut(rClass) {
            val rClassElement = Utils.getElementByName(rClass, elementUtils, typeUtils)
            val rClassPackageName =
                elementUtils.getPackageOf(rClassElement).qualifiedName.toString()
            ClassName.get(rClassPackageName, "R", resourceType)
        }
    }

    companion object {
        private fun getLayoutValues(
            element: Element,
            annotationClass: Class<out Annotation>
        ): List<Int> {
            element.ensureLoaded()
            // We could do this in a more generic way if we ever need to support more annotation types
            return when (val annotation = element.getAnnotationThreadSafe(annotationClass)) {
                is EpoxyModelClass -> listOf(annotation.layout)
                is EpoxyDataBindingLayouts -> {
                    annotation.value.toList()
                }
                is ModelView -> listOf(annotation.defaultLayout)
                else -> emptyList()
            }
        }
    }
}
