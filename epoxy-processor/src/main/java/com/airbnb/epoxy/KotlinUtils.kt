package com.airbnb.epoxy

import com.airbnb.epoxy.Utils.*
import com.squareup.javapoet.*
import javax.lang.model.*
import javax.lang.model.element.*
import javax.lang.model.type.*
import javax.lang.model.util.*
import kotlin.reflect.*

fun getTypeMirror(
        className: ClassName,
        elements: Elements,
        types: Types
): TypeMirror {
    val classElement = getElementByName(className, elements, types)
            ?: throw IllegalArgumentException("Unknown class: " + className)

    return classElement.asType()
}

fun getTypeMirror(
        clazz: Class<*>,
        elements: Elements
): TypeMirror? = getTypeMirror(clazz.canonicalName, elements)

fun getTypeMirror(
        canonicalName: String,
        elements: Elements
): TypeMirror? {
    return try {
        elements.getTypeElement(canonicalName)?.asType()
    } catch (mte: MirroredTypeException) {
        mte.typeMirror
    }

}

fun Class<*>.asTypeElement(
        elements: Elements,
        types: Types
): TypeElement? {
    val typeMirror = getTypeMirror(this, elements) ?: return null
    return types.asElement(typeMirror) as? TypeElement
}

fun KClass<*>.asTypeElement(
        elements: Elements,
        types: Types
) = java.asTypeElement(elements, types)

fun String.toLowerCamelCase(): String {

    return transformEachChar { prevChar, char, _ ->
        if (char != '_') {
            append(when (prevChar) {
                       null -> Character.toLowerCase(char)
                       '_' -> Character.toUpperCase(char)
                       else -> char
                   })
        }
    }
}

fun String.toUpperCamelCase(): String {
    val separators = listOf('_', ' ')

    return transformEachChar { prevChar, char, _ ->
        if (char !in separators) {
            append(when (prevChar) {
                       null, in separators -> Character.toUpperCase(char)
                       else -> char
                   })
        }
    }
}

fun TypeElement.executableElements() = enclosedElements.filterIsInstance<ExecutableElement>()

/** @return Whether at least one of the given annotations is present on the receiver. */
fun AnnotatedConstruct.hasAnyAnnotation(annotationClasses: List<Class<out Annotation>>)
        = annotationClasses.any {
    try {
        getAnnotation(it) != null
    } catch (e: MirroredTypeException) {
        // This will be thrown if the annotation contains a param of type Class. This is fine,
        // it still means that the annotation is present
        true
    }
}


fun AnnotatedConstruct.hasAnnotation(annotationClass: KClass<out Annotation>)
        = hasAnyAnnotation(listOf(annotationClass.java))

fun AnnotatedConstruct.hasAnnotation(annotationClass: Class<out Annotation>)
        = hasAnyAnnotation(listOf(annotationClass))

inline fun <reified T : Annotation> AnnotatedConstruct.annotation(): T?
        = getAnnotation(T::class.java)

/** Creates a new version of the classname where the simple name has the given suffix added to it.
 *
 * If there are multiple simple names they are combined into 1.
 * */
fun ClassName.appendToName(suffix: String)
        = ClassName.get(
        packageName(),
        simpleNames().joinToString(separator = "_", postfix = suffix)
).annotated(annotations)!!

/** Iterates through each character, allowing you to build a string by transforming the characters as needed. */
private fun String.transformEachChar(operation: StringBuilder.(Char?, Char, Char?) -> Unit): String {
    val stringBuilder = StringBuilder(length)

    for (i in 0 until length) {
        stringBuilder.operation(getOrNull(i - 1), get(i), getOrNull(i))
    }

    return stringBuilder.toString()
}

/** Return each of the classes in the class hierarchy, starting with the initial receiver and working upwards until Any. */
tailrec fun Element.iterateClassHierarchy(
        types: Types,
        classCallback: (classElement: TypeElement) -> Unit
) {
    if (this !is TypeElement) {
        return
    }

    classCallback(this)

    val superClazz = getParentClassElement(types) ?: return
    superClazz.iterateClassHierarchy(types, classCallback)
}

/** Iterate each super class of the receiver, starting with the initial super class and going until Any. */
fun Element.iterateSuperClasses(
        types: Types,
        classCallback: (classElement: TypeElement) -> Unit
) {
    iterateClassHierarchy(types) {
        // Skip the original class so that only super classes are passed to the callback
        if (it != this) {
            classCallback(it)
        }
    }
}

fun TypeElement.getParentClassElement(
        types: Types
): TypeElement? = types.asElement(superclass) as? TypeElement

/** Similar to the java 8 Map#merge method. */
fun <K, V> MutableMap<K, V>.putOrMerge(
        key: K,
        value: V,
        reduceFunction: (V, V) -> V
) {
    val oldValue = get(key)
    val newValue = if (oldValue == null) {
        value
    } else {
        reduceFunction(oldValue, value)
    }

    put(key, newValue)
}