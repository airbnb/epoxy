package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XProcessingEnv
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Types

fun TypeElement.superClassElement(types: Types): TypeElement? =
    types.asElement(superclass)?.ensureLoaded() as TypeElement?

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

fun XProcessingEnv.isTypeLoaded(typeName: TypeName): Boolean {
    return findTypeElement(typeName) != null
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
