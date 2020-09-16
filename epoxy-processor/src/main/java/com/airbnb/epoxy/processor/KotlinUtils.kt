package com.airbnb.epoxy.processor

import com.airbnb.epoxy.processor.Utils.getElementByNameNullable
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import kotlin.reflect.KClass

fun TypeMirror.isVoidClass(): Boolean = toString() == Void::class.java.canonicalName

fun typeMirror(block: () -> KClass<*>): TypeMirror? = synchronizedForTypeLookup {
    // Unfortunately we have to do this weird try/catch to get the class type
    return try {
        // this should throw
        block()
        null
    } catch (mte: MirroredTypeException) {
        mte.typeMirror
    }
}

fun getTypeMirror(
    className: ClassName,
    elements: Elements,
    types: Types
): TypeMirror {
    return getTypeMirrorNullable(className, elements, types)
        ?: error("Unable to find type for $className")
}

fun getTypeMirrorNullable(
    className: ClassName,
    elements: Elements,
    types: Types
): TypeMirror? {
    return getElementByNameNullable(className, elements, types)?.asType()
}

fun getTypeMirrorNullable(
    clazz: Class<*>,
    elements: Elements
): TypeMirror? = getTypeMirrorNullable(clazz.canonicalName, elements)

fun getTypeMirror(
    clazz: Class<*>,
    elements: Elements
): TypeMirror = getTypeMirrorNullable(clazz.canonicalName, elements)
    ?: error("Could not find type mirror for ${clazz.canonicalName}")

fun getTypeMirrorNullable(
    canonicalName: String,
    elements: Elements
): TypeMirror? = synchronizedForTypeLookup {
    try {
        elements.getTypeElement(canonicalName)?.asType()
    } catch (mte: MirroredTypeException) {
        mte.typeMirror
    }?.ensureLoaded()
}

fun TypeMirror.typeNameSynchronized(): TypeName = synchronizedForTypeLookup {
    // This uses a visitor pattern to explore the types in this type, and if it is a
    // parameterized or nested type it can load the classes of those other types.
    TypeName.get(this)
}

fun TypeElement.superClassElement(types: Types): TypeElement? =
    types.asElement(superclass)?.ensureLoaded() as TypeElement?

fun ClassName.asTypeElement(
    elements: Elements
): TypeElement? = synchronizedForTypeLookup {
    elements.getTypeElement(reflectionName())?.ensureLoaded()
}

fun Class<*>.asTypeElement(
    elements: Elements,
    types: Types
): TypeElement {
    val typeMirror = getTypeMirrorNullable(this, elements)
    return (types.asElement(typeMirror) as TypeElement).ensureLoaded()
}

fun KClass<*>.asTypeElement(
    elements: Elements,
    types: Types
): TypeElement = java.asTypeElement(elements, types)

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

fun TypeElement.executableElements() =
    enclosedElementsThreadSafe.filterIsInstance<ExecutableElement>()

/** @return Whether at least one of the given annotations is present on the receiver. */
fun Element.hasAnyAnnotation(annotationClasses: List<KClass<out Annotation>>) =
    synchronizedForTypeLookup {
        annotationClasses.any {
            try {
                getAnnotationThreadSafe(it.java) != null
            } catch (e: MirroredTypeException) {
                // This will be thrown if the annotation contains a param of type Class. This is fine,
                // it still means that the annotation is present
                true
            }
        }
    }

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
    return getTypeMirrorNullable(className.reflectionName(), this) != null
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

    val superClazz = this.superClassElement(types) ?: return
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

/**
 * Returns a list of annotations specs representing annotations on the given type element.
 *
 * @param annotationFilter Return false to exclude annotations with the given class name.
 */
fun TypeElement.buildAnnotationSpecs(
    annotationFilter: (ClassName) -> Boolean
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
    return annotationMirrorsThreadSafe
        .map { AnnotationSpec.get(it) }
        .filter { internalAnnotationFilter(it.type as ClassName) }
}

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
    return e1.parametersThreadSafe.size != e2.parametersThreadSafe.size &&
        e1.simpleName == e2.simpleName &&
        e1.enclosingElement == e2.enclosingElement &&
        e1.returnType == e2.returnType &&
        e1.modifiers == e2.modifiers
}

fun TypeElement.findOverload(element: ExecutableElement, paramCount: Int): ExecutableElement? {
    require(element.parametersThreadSafe.size != paramCount) { "Element $element already has param count $paramCount" }

    return enclosedElementsThreadSafe
        .filterIsInstance<ExecutableElement>()
        .firstOrNull { it.parametersThreadSafe.size == paramCount && areOverloads(it, element) }
}

fun TypeElement.hasOverload(element: ExecutableElement, paramCount: Int): Boolean {
    return findOverload(element, paramCount) != null
}
