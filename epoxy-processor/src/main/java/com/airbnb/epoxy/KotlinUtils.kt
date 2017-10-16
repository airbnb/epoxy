package com.airbnb.epoxy

import com.airbnb.epoxy.Utils.*
import com.squareup.javapoet.*
import javax.lang.model.element.*
import javax.lang.model.util.*
import kotlin.reflect.*

fun Class<*>.asTypeElement(
        elements: Elements,
        types: Types
) = getElementByName(ClassName.get(this), elements, types) as TypeElement

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

/** Iterates through each character, allowing you to build a string by transforming the characters as needed. */
private fun String.transformEachChar(operation: StringBuilder.(Char?, Char, Char?) -> Unit): String {
    val stringBuilder = StringBuilder(length)

    for (i in 0 until length) {
        stringBuilder.operation(getOrNull(i - 1), get(i), getOrNull(i))
    }

    return stringBuilder.toString()
}