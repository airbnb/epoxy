package com.airbnb.epoxy

import com.squareup.kotlinpoet.*
import javax.lang.model.element.*

typealias JavaClassName = com.squareup.javapoet.ClassName
typealias JavaTypeName = com.squareup.javapoet.TypeName
typealias JavaWildcardTypeName = com.squareup.javapoet.WildcardTypeName
typealias JavaArrayTypeName = com.squareup.javapoet.ArrayTypeName
typealias JavaTypeVariableName = com.squareup.javapoet.TypeVariableName
typealias JavaParametrizedTypeName = com.squareup.javapoet.ParameterizedTypeName

typealias JavaParameterSpec = com.squareup.javapoet.ParameterSpec
typealias JavaFieldSpec = com.squareup.javapoet.FieldSpec
typealias JavaAnnotationSpec = com.squareup.javapoet.AnnotationSpec
typealias JavaTypeSpec = com.squareup.javapoet.TypeSpec

typealias KotlinClassName = com.squareup.kotlinpoet.ClassName
typealias KotlinParameterizedTypeName = com.squareup.kotlinpoet.ParameterizedTypeName
typealias KotlinTypeName = com.squareup.kotlinpoet.TypeName
typealias KotlinWildcardTypeName = com.squareup.kotlinpoet.WildcardTypeName
typealias KotlinTypeVariableName = com.squareup.kotlinpoet.TypeVariableName

typealias KotlinParameterSpec = com.squareup.kotlinpoet.ParameterSpec
typealias KotlinAnnotationSpec = com.squareup.kotlinpoet.AnnotationSpec
typealias KotlinTypeSpec = com.squareup.kotlinpoet.TypeSpec

fun JavaClassName.toKPoet() = KotlinClassName(
        packageName(),
        simpleName())

// Does not support transferring annotations
fun JavaWildcardTypeName.toKPoet() =
        if (!lowerBounds.isEmpty()) {
            KotlinWildcardTypeName.supertypeOf(lowerBounds.first().toKPoet())
        } else {
            KotlinWildcardTypeName.subtypeOf(upperBounds.first().toKPoet())
        }

// Does not support transferring annotations
fun JavaParametrizedTypeName.toKPoet()
        = KotlinParameterizedTypeName.get(
        this.rawType.toKPoet(),
        *typeArguments.toKPoet().toTypedArray())

// Does not support transferring annotations
fun JavaArrayTypeName.toKPoet()
        = KotlinParameterizedTypeName.get(
        KotlinClassName.bestGuess("kotlin.Array"),
        this.componentType.toKPoet())

// Does not support transferring annotations
fun JavaTypeVariableName.toKPoet()
        = KotlinTypeVariableName.invoke(
        name,
        *bounds.toKPoet().toTypedArray())

fun JavaTypeName.toKPoet(): KotlinTypeName = when (this) {
    JavaTypeName.BOOLEAN -> BOOLEAN
    JavaTypeName.BYTE -> BYTE
    JavaTypeName.SHORT -> SHORT
    JavaTypeName.CHAR -> CHAR
    JavaTypeName.INT -> INT
    JavaTypeName.LONG -> LONG
    JavaTypeName.FLOAT -> FLOAT
    JavaTypeName.DOUBLE -> DOUBLE
    JavaTypeName.OBJECT -> ANY
    JavaTypeName.VOID -> UNIT
    is JavaClassName -> toKPoet()
    is JavaParametrizedTypeName -> toKPoet()
    is JavaArrayTypeName -> toKPoet()
    is JavaTypeVariableName -> toKPoet()
    is JavaWildcardTypeName -> toKPoet()
    else -> throw IllegalArgumentException("Unsupported type: ${this::class.simpleName}")
}

fun <T : JavaTypeName> Iterable<T>.toKPoet() = map { it.toKPoet() }

fun JavaParameterSpec.toKPoet(): KotlinParameterSpec
        = KotlinParameterSpec.builder(
        name,
        type.toKPoet(),
        *modifiers.toKModifier().toTypedArray()
).build()

fun Iterable<JavaParameterSpec>.toKParams() = map { it.toKPoet() }

fun Iterable<Modifier>.toKModifier(): List<KModifier> =
        map { it.toKModifier() }.filter { it != null }.map { it!! }

fun Modifier.toKModifier() = when (this) {
    Modifier.PUBLIC -> KModifier.PUBLIC
    Modifier.PRIVATE -> KModifier.PRIVATE
    Modifier.PROTECTED -> KModifier.PROTECTED
    Modifier.FINAL -> KModifier.FINAL
    Modifier.ABSTRACT -> KModifier.ABSTRACT
    else -> null
}

