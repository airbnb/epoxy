package com.airbnb.epoxy.processor

import com.squareup.javapoet.TypeName
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.BYTE
import com.squareup.kotlinpoet.CHAR
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.SHORT
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.WildcardTypeName
import javax.lang.model.element.Modifier

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

private val javaUtilPkg = "java.util"
private val javaLangPkg = "java.lang"
private val kotlinJvmFunction = "kotlin.jvm.functions"
private val kotlinCollectionsPkg = "kotlin.collections"
private val kotlinPkg = "kotlin"
fun JavaClassName.toKPoet(): KotlinClassName {

    val simpleNames = getSimpleNamesInKotlin()
    val packageName = getPackageNameInKotlin()

    return KotlinClassName(
        packageName,
        simpleNames.first(),
        *simpleNames.drop(1).toTypedArray()
    )
}

/** Some classes, like List or Byte have the same class name but a different package for their kotlin equivalent. */
private fun JavaClassName.getPackageNameInKotlin(): String {
    if (packageName() in listOf(
            javaUtilPkg,
            javaLangPkg,
            kotlinJvmFunction
        ) && simpleNames().size == 1
    ) {

        val transformedPkg = when {
            isBoxedPrimitive -> kotlinPkg
            isLambda(this) -> kotlinPkg
            else -> when (simpleName()) {
                "Collection",
                "List",
                "Map",
                "Set",
                "Iterable" -> kotlinCollectionsPkg
                "String" -> kotlinPkg
                "CharSequence" -> kotlinPkg
                else -> null
            }
        }

        if (transformedPkg != null) {
            return transformedPkg
        }
    }

    return packageName()
}

fun isLambda(type: JavaTypeName): Boolean {
    return type.toString().contains("Function") && type.toString().contains("kotlin")
}

/** Some classes, notably Integer and Character, have a different simple name in Kotlin. */
private fun JavaClassName.getSimpleNamesInKotlin(): List<String> {
    val originalNames = simpleNames()

    if (isBoxedPrimitive) {
        val transformedName = when (originalNames.first()) {
            "Integer" -> "Int"
            "Character" -> "Char"
            else -> null
        }

        if (transformedName != null) {
            return listOf(transformedName)
        }
    }

    return originalNames
}

// Does not support transferring complex annotations which
// have parameters and values associated with them.
fun JavaAnnotationSpec.toKPoet(): KotlinAnnotationSpec? {
    // If the annotation has any members (params), then we
    // return null since we don't yet support translating
    // params from Java annotation to Kotlin annotation.
    if (members.isNotEmpty()) {
        return null
    }
    val annotationClass = KotlinClassName.bestGuess(type.toString())
    return KotlinAnnotationSpec.builder(annotationClass).build()
}

fun JavaClassName.setPackage(packageName: String) =
    JavaClassName.get(packageName, simpleName(), *simpleNames().drop(1).toTypedArray())!!

// Does not support transferring annotations
fun JavaWildcardTypeName.toKPoet(): WildcardTypeName {
    return if (lowerBounds.isNotEmpty()) {
        KotlinWildcardTypeName.consumerOf(lowerBounds.first().toKPoet())
    } else when (val upperBound = upperBounds[0]) {
        TypeName.OBJECT -> STAR
        else -> KotlinWildcardTypeName.producerOf(upperBound.toKPoet())
    }
}

// Does not support transferring annotations
fun JavaParametrizedTypeName.toKPoet() =
    this.rawType.toKPoet().parameterizedBy(*typeArguments.toKPoet().toTypedArray())

// Does not support transferring annotations
fun JavaArrayTypeName.toKPoet(): KotlinTypeName {

    // Kotlin has special classes for primitive arrays
    if (componentType.isPrimitive) {
        val kotlinArrayType = when (componentType) {
            TypeName.BYTE -> "ByteArray"
            TypeName.SHORT -> "ShortArray"
            TypeName.CHAR -> "CharArray"
            TypeName.INT -> "IntArray"
            TypeName.FLOAT -> "FloatArray"
            TypeName.DOUBLE -> "DoubleArray"
            TypeName.LONG -> "LongArray"
            TypeName.BOOLEAN -> "BooleanArray"
            else -> null
        }

        if (kotlinArrayType != null) {
            return KotlinClassName(kotlinPkg, kotlinArrayType)
        }
    }

    return KotlinClassName(kotlinPkg, "Array").parameterizedBy(this.componentType.toKPoet())
}

// Does not support transferring annotations
fun JavaTypeVariableName.toKPoet() = KotlinTypeVariableName.invoke(
    if (name == "?") "*" else name,
    *bounds.toKPoet().toTypedArray()
)

fun JavaTypeName.toKPoet(nullable: Boolean = false): KotlinTypeName {
    val type = when (this) {
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

    if (nullable) {
        return type.copy(nullable = true)
    }

    return type
}

fun <T : JavaTypeName> Iterable<T>.toKPoet() = map { it.toKPoet() }

fun JavaParameterSpec.toKPoet(): KotlinParameterSpec {

    // A param name in java might be reserved in kotlin
    val paramName = if (name in KOTLIN_KEYWORDS) name + "Param" else name

    val nullable = annotations.any { (it.type as? JavaClassName)?.simpleName() == "Nullable" }

    val kotlinAnnotations: List<KotlinAnnotationSpec> = annotations.mapNotNull { it.toKPoet() }

    return KotlinParameterSpec.builder(
        paramName,
        // Not using built in javapoet interop because of bug https://github.com/square/kotlinpoet/issues/1181
        type.toKPoet(nullable),
        *modifiers.toKModifier().toTypedArray()
    ).apply {
        if (isLambda(type)) {
            addModifiers(KModifier.NOINLINE)
        }
        addAnnotations(kotlinAnnotations)
    }.build()
}

fun Iterable<Modifier>.toKModifier(): List<KModifier> = mapNotNull { it.toKModifier() }

fun Modifier.toKModifier() = when (this) {
    Modifier.PUBLIC -> KModifier.PUBLIC
    Modifier.PRIVATE -> KModifier.PRIVATE
    Modifier.PROTECTED -> KModifier.PROTECTED
    Modifier.FINAL -> KModifier.FINAL
    Modifier.ABSTRACT -> KModifier.ABSTRACT
    else -> null
}

// https://github.com/JetBrains/kotlin/blob/master/core/descriptors/src/org/jetbrains/kotlin/renderer/KeywordStringsGenerated.java
private val KOTLIN_KEYWORDS = setOf(
    "package",
    "as",
    "typealias",
    "class",
    "this",
    "super",
    "val",
    "var",
    "fun",
    "for",
    "null",
    "true",
    "false",
    "is",
    "in",
    "throw",
    "return",
    "break",
    "continue",
    "object",
    "if",
    "try",
    "else",
    "while",
    "do",
    "when",
    "interface",
    "typeof"
)
