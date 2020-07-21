package com.airbnb.epoxy.processor

import androidx.annotation.FloatRange
import androidx.annotation.NonNull
import com.airbnb.epoxy.EpoxyModelClass
import com.squareup.kotlinpoet.asTypeName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import javax.lang.model.element.Modifier

class PoetExtensionsTest {

    @Test
    fun testAnnotationSpecToKPoet() {
        val annotation = EpoxyModelClass::class.java
        val type = annotation.asTypeName()
        val javaAnnotation = JavaAnnotationSpec.builder(annotation).build()

        val kotlinAnnotation = javaAnnotation.toKPoet()
        assertNotNull(kotlinAnnotation)
        assertEquals(type, kotlinAnnotation?.className)
    }

    @Test
    fun testAnnotationSpecToKPoetWithParams() {
        val annotation = FloatRange::class.java
        val javaAnnotation = JavaAnnotationSpec.builder(annotation)
            .addMember("from", "0.0")
            .addMember("to", "1.0")
            .build()

        val kotlinAnnotation = javaAnnotation.toKPoet()
        assertNull(kotlinAnnotation)
    }

    @Test
    fun testIsLambdaWithString() {
        val stringType = JavaClassName.bestGuess("java.lang.String")
        assertFalse(isLambda(stringType))
    }

    @Test
    fun testIsLambdaWithLambda() {
        val lambdaType = JavaClassName.bestGuess("kotlin.Function2")
        assertTrue(isLambda(lambdaType))
    }

    @Test
    fun testJavaParameterSpecToKPoet() {
        val name = "android"
        val javaParameter = JavaParameterSpec.builder(
            JavaClassName.bestGuess("java.lang.String"), name, Modifier.PRIVATE
        )
            .addAnnotation(NonNull::class.java)
            .build()
        val kotlinString = KotlinClassName("kotlin", "String")

        val kotlinParameter = javaParameter.toKPoet()
        assertEquals(name, kotlinParameter.name)
        assertEquals(kotlinString, kotlinParameter.type)
        assertEquals(javaParameter.annotations.size, kotlinParameter.annotations.size)
        assertEquals(NonNull::class.java.asTypeName(), kotlinParameter.annotations[0].className)
    }

    @Test
    fun testJavaTypeNameToKPoet() {
        val javaType = JavaParametrizedTypeName.get(
            JavaClassName.bestGuess("java.util.List"),
            JavaClassName.bestGuess("java.lang.String")
        )

        val kotlinType = javaType.toKPoet()
        val kotlinList = KotlinClassName("kotlin.collections", "List")
        val kotlinString = KotlinClassName("kotlin", "String")
        assertEquals(kotlinList, kotlinType.rawType)
        assertEquals(kotlinString, kotlinType.typeArguments[0])
    }

    @Test
    fun testJavaArrayTypeNameToKPoet() {
        val javaIntArray = JavaArrayTypeName.of(JavaClassName.INT)
        val kotlinIntArray = javaIntArray.toKPoet()

        assertEquals("kotlin.IntArray", kotlinIntArray.toString())

        val javaFloatArray = JavaArrayTypeName.of(
            JavaClassName.bestGuess("java.lang.Float")
        )
        val kotlinFloatArray = javaFloatArray.toKPoet()

        assertEquals("kotlin.Array<kotlin.Float>", kotlinFloatArray.toString())
    }

    @Test
    fun testJavaClassNameToKPoet() {
        val javaClassName = JavaClassName.bestGuess("java.lang.Integer")
        val kotlinClassName = javaClassName.toKPoet()

        val javaByteName = JavaClassName.BYTE
        val kotlinByteName = javaByteName.toKPoet()

        assertEquals("kotlin.Int", kotlinClassName.toString())
        assertEquals("kotlin.Byte", kotlinByteName.toString())
    }
}
