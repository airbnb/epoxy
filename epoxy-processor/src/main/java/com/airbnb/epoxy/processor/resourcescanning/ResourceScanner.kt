package com.airbnb.epoxy.processor.resourcescanning

import androidx.room.compiler.processing.XAnnotation
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XTypeElement
import com.squareup.javapoet.ClassName
import kotlin.reflect.KClass

abstract class ResourceScanner(val environmentProvider: () -> XProcessingEnv) {
    val rClassNames: List<ClassName>
        get() = mutableRClasses.toList()

    private val mutableRClasses = mutableSetOf<ClassName>()

    /**
     * Returns the [ResourceValue] that is used as an annotation value of the given [XElement]
     */
    fun getResourceValue(
        annotation: KClass<out Annotation>,
        element: XElement,
        property: String,
    ): ResourceValue? {
        // The annotation box class doesn't allow us to query properties by name, so we get an
        // XAnnotation which has that information.
        val xAnnotation = element.getXAnnotation(annotation) ?: return null

        val value = (
            xAnnotation.annotationValues
                .firstOrNull { it.name == property }
                ?.value as? Int
            )
            ?: return null

        return getResourceValue(annotation, element, property, value)
    }

    private fun XElement.getXAnnotation(
        annotation: KClass<out Annotation>
    ): XAnnotation? {
        return getAllAnnotations().firstOrNull {
            // optimization to not resolve full annotation for fqn unless the simple name matches
            it.name == annotation.simpleName && it.qualifiedName == annotation.qualifiedName
        }
    }

    /**
     * Returns the [ResourceValue] that is used as an annotation value of the given [XElement]
     */
    fun getResourceValue(
        annotation: KClass<out Annotation>,
        element: XElement,
        property: String,
        value: Int
    ): ResourceValue {
        return getResourceValueInternal(annotation, element, property, value).also { resourceValue ->
            resourceValue?.className?.let { mutableRClasses.add(it) }
        } ?: ResourceValue(value)
    }

    /**
     * Returns the list of [ResourceValue] that is used as an annotation value of the given [XElement]
     */
    fun getResourceValueList(
        annotation: KClass<out Annotation>,
        element: XElement,
        property: String,
    ): List<ResourceValue>? {
        val xAnnotation = element.getXAnnotation(annotation) ?: return null

        val values = (
            xAnnotation.annotationValues
                .firstOrNull { it.name == property }
                ?.value as? List<Int>
            )
            ?: return null

        return getResourceValueListInternal(annotation, element, property, values).also { list ->
            list.forEach { resourceValue ->
                resourceValue.rClass?.let { mutableRClasses.add(it) }
            }
        }
    }

    protected abstract fun getResourceValueListInternal(
        annotation: KClass<out Annotation>,
        element: XElement,
        property: String,
        values: List<Int>,
    ): List<ResourceValue>

    protected abstract fun getResourceValueInternal(
        annotation: KClass<out Annotation>,
        element: XElement,
        property: String,
        value: Int
    ): ResourceValue?

    /**
     * Returns a list of layout resources whose name contains the given layout as a prefix.
     */
    fun getAlternateLayouts(layout: ResourceValue): List<ResourceValue> {
        val layoutClassName = layout.className ?: return emptyList()

        val rLayoutClassElement: XTypeElement =
            environmentProvider().requireTypeElement(layoutClassName)
        val target = layout.resourceName + "_"

        return rLayoutClassElement
            .getDeclaredFields()
            .map { it.name }
            .filter {
                it.startsWith(target)
            }
            .map {
                ResourceValue(
                    layout.className,
                    it,
                    value = 0 // Don't care about this for our use case
                )
            }
    }

    abstract fun getImports(classElement: XTypeElement): List<String>
}
