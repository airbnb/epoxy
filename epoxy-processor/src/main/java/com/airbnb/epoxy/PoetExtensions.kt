package com.airbnb.epoxy

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
    is JavaClassName -> toKPoet()
    is JavaParametrizedTypeName -> toKPoet()
    is JavaArrayTypeName -> toKPoet()
    is JavaTypeVariableName -> toKPoet()
    is JavaWildcardTypeName -> toKPoet()
    else -> throw IllegalArgumentException("Unsupported type: ${this::class.simpleName}")
}

fun <T : JavaTypeName> Iterable<T>.toKPoet() = map { it.toKPoet() }