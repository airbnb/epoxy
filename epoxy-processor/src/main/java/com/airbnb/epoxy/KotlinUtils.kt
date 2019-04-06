package com.airbnb.epoxy

import com.airbnb.epoxy.Utils.getElementByName
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import javax.lang.model.AnnotatedConstruct
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import kotlin.reflect.KClass

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
            append(
                when (prevChar) {
                    null -> Character.toLowerCase(char)
                    '_' -> Character.toUpperCase(char)
                    else -> char
                }
            )
        }
    }
}

fun String.toUpperCamelCase(): String {
    val separators = listOf('_', ' ')

    return transformEachChar { prevChar, char, _ ->
        if (char !in separators) {
            append(
                when (prevChar) {
                    null, in separators -> Character.toUpperCase(char)
                    else -> char
                }
            )
        }
    }
}

fun TypeElement.executableElements() = enclosedElements.filterIsInstance<ExecutableElement>()

/** @return Whether at least one of the given annotations is present on the receiver. */
fun AnnotatedConstruct.hasAnyAnnotation(annotationClasses: List<Class<out Annotation>>) =
    annotationClasses.any {
        try {
            getAnnotation(it) != null
        } catch (e: MirroredTypeException) {
            // This will be thrown if the annotation contains a param of type Class. This is fine,
            // it still means that the annotation is present
            true
        }
    }

fun AnnotatedConstruct.hasAnnotation(annotationClass: KClass<out Annotation>) =
    hasAnyAnnotation(listOf(annotationClass.java))

fun AnnotatedConstruct.hasAnnotation(annotationClass: Class<out Annotation>) =
    hasAnyAnnotation(listOf(annotationClass))

inline fun <reified T : Annotation> AnnotatedConstruct.annotation(): T? =
    getAnnotation(T::class.java)

/** Creates a new version of the classname where the simple name has the given suffix added to it.
 *
 * If there are multiple simple names they are combined into 1.
 * */
fun ClassName.appendToName(suffix: String) = ClassName.get(
    packageName(),
    simpleNames().joinToString(separator = "_", postfix = suffix)
).annotated(annotations)!!

/** Iterates through each character, allowing you to build a string by transforming the characters as needed. */
private fun String.transformEachChar(
    operation: StringBuilder.(Char?, Char, Char?) -> Unit
): String {
    val stringBuilder = StringBuilder(length)

    for (i in 0 until length) {
        stringBuilder.operation(getOrNull(i - 1), get(i), getOrNull(i))
    }

    return stringBuilder.toString()
}

fun Elements.isTypeLoaded(className: ClassName): Boolean {
    return getTypeMirror(className.reflectionName(), this) != null
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

inline fun <reified T : Annotation> Element.annotation(): T? = getAnnotation(T::class.java)

/**
 * Returns a list of annotations specs representing annotations on the given type element.
 *
 * @param annotationFilter Return false to exclude annotations with the given class name.
 */
fun TypeElement.buildAnnotationSpecs(
    annotationFilter: (ClassName) -> Boolean = { true }
): List<AnnotationSpec> {
    val internalAnnotationFilter = { className: ClassName ->
        if (className.reflectionName() == "kotlin.Metadata") {
            // Don't include the generated kotlin metadata since it only applies to the original
            // kotlin class and is wrong to put on our generated java classes.
            false
        } else {
            annotationFilter(className)
        }
    }
    return annotationMirrors
        .map { AnnotationSpec.get(it) }
        .filter { internalAnnotationFilter(it.type as ClassName) }
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

/**
 * True if the two elements represent overloads of the same function in a class.
 */
fun areOverloads(e1: ExecutableElement, e2: ExecutableElement): Boolean {
    return e1.parameters.size != e2.parameters.size &&
        e1.simpleName == e2.simpleName &&
        e1.enclosingElement == e2.enclosingElement &&
        e1.returnType == e2.returnType &&
        e1.modifiers == e2.modifiers
}

fun TypeElement.findOverload(element: ExecutableElement, paramCount: Int): ExecutableElement? {
    require(element.parameters.size != paramCount) { "Element $element already has param count $paramCount" }

    return enclosedElements
        .filterIsInstance<ExecutableElement>()
        .firstOrNull { it.parameters.size == paramCount && areOverloads(it, element) }
}

fun TypeElement.hasOverload(element: ExecutableElement, paramCount: Int): Boolean {
    return findOverload(element, paramCount) != null
}
