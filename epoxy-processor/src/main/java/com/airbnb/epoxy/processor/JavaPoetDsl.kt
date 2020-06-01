package com.airbnb.epoxy.processor

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import com.squareup.javapoet.TypeVariableName
import java.lang.reflect.Type
import javax.lang.model.element.Modifier
import kotlin.reflect.KClass

internal inline fun buildClass(
    name: String,
    initializer: TypeSpec.Builder.() -> Unit
): TypeSpec = TypeSpec.classBuilder(name).apply(initializer).build()

internal inline fun buildInterface(
    name: String,
    initializer: TypeSpec.Builder.() -> Unit
): TypeSpec = TypeSpec.interfaceBuilder(name).apply(initializer).build()

internal inline fun buildInterface(
    name: ClassName,
    initializer: TypeSpec.Builder.() -> Unit
): TypeSpec = TypeSpec.interfaceBuilder(name).apply(initializer).build()

internal inline fun buildClass(
    className: ClassName,
    initializer: TypeSpec.Builder.() -> Unit
): TypeSpec = TypeSpec.classBuilder(className).apply(initializer).build()

internal inline fun TypeSpec.Builder.addAnnotation(
    type: Class<*>,
    initializer: AnnotationSpec.Builder.() -> Unit
) {
    addAnnotation(AnnotationSpec.builder(type).apply(initializer).build())
}

internal inline fun TypeSpec.Builder.addAnnotation(
    type: ClassName,
    initializer: AnnotationSpec.Builder.() -> Unit
) {
    addAnnotation(AnnotationSpec.builder(type).apply(initializer).build())
}

internal inline fun buildConstructor(
    initializer: MethodSpec.Builder.() -> Unit
): MethodSpec = MethodSpec.constructorBuilder().apply(initializer).build()

internal inline fun buildMethod(
    name: String,
    initializer: MethodSpec.Builder.() -> Unit
): MethodSpec = MethodSpec.methodBuilder(name).apply(initializer).build()

/** Copies all details of a methodspec besides the statement, allowing you to override any piece of the method. */
internal fun MethodSpec.copy(
    name: String? = null,
    modifiers: Iterable<Modifier>? = null, // replaces all existing modifiers if not null
    additionalModifiers: Iterable<Modifier>? = null, // Appends to existing modifiers if not null
    returns: TypeName? = null,
    params: Iterable<ParameterSpec>? = null,
    varargs: Boolean? = null,
    typeVariables: Iterable<TypeVariableName>? = null,
    exceptions: Iterable<TypeName>? = null,
    // Any other custom init code, like adding statements
    callback: MethodSpec.Builder.() -> Unit = {}
): MethodSpec {
    val builder = MethodSpec.methodBuilder(name ?: this.name)
        .addModifiers(modifiers ?: this.modifiers)
        .addModifiers(additionalModifiers ?: emptyList())
        .returns(returns ?: this.returnType)
        .addParameters(params ?: this.parameters)
        .varargs(varargs ?: this.varargs)
        .addTypeVariables(typeVariables ?: this.typeVariables)
        .addExceptions(exceptions ?: this.exceptions)

    return builder.apply(callback).build()
}

internal inline fun MethodSpec.Builder.addAnnotation(
    type: Class<*>,
    initializer: AnnotationSpec.Builder.() -> Unit
) {
    addAnnotation(AnnotationSpec.builder(type).apply(initializer).build())
}

internal inline fun MethodSpec.Builder.addAnnotation(
    type: ClassName,
    initializer: AnnotationSpec.Builder.() -> Unit
) {
    addAnnotation(AnnotationSpec.builder(type).apply(initializer).build())
}

internal inline fun buildField(
    type: Type,
    name: String,
    initializer: FieldSpec.Builder.() -> Unit
): FieldSpec = FieldSpec.builder(type, name).apply(initializer).build()

internal inline fun buildField(
    type: TypeName,
    name: String,
    initializer: FieldSpec.Builder.() -> Unit
): FieldSpec = FieldSpec.builder(type, name).apply(initializer).build()

internal inline fun FieldSpec.Builder.addAnnotation(
    type: Class<*>,
    initializer: AnnotationSpec.Builder.() -> Unit
) {
    addAnnotation(AnnotationSpec.builder(type).apply(initializer).build())
}

internal inline fun FieldSpec.Builder.addAnnotation(
    type: ClassName,
    initializer: AnnotationSpec.Builder.() -> Unit
) {
    addAnnotation(AnnotationSpec.builder(type).apply(initializer).build())
}

internal inline fun TypeSpec.Builder.addMethod(
    name: String,
    initializer: MethodSpec.Builder.() -> Unit
) {
    addMethod(MethodSpec.methodBuilder(name).apply(initializer).build())
}

internal inline fun TypeSpec.Builder.addField(
    type: TypeName,
    name: String,
    vararg modifiers: Modifier,
    initializer: FieldSpec.Builder.() -> Unit
) {
    addField(FieldSpec.builder(type, name, *modifiers).apply(initializer).build())
}

internal inline fun TypeSpec.Builder.addConstructor(initializer: MethodSpec.Builder.() -> Unit) {
    addMethod(MethodSpec.constructorBuilder().apply(initializer).build())
}

internal inline fun MethodSpec.Builder.addParameter(
    type: TypeName,
    name: String,
    vararg modifiers: Modifier,
    initializer: ParameterSpec.Builder.() -> Unit
) {
    addParameter(ParameterSpec.builder(type, name, *modifiers).apply(initializer).build())
}

internal fun KClass<*>.className() = ClassName.get(this.java)
internal fun Class<*>.className() = ClassName.get(this)
