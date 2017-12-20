package com.airbnb.epoxy

import com.squareup.javapoet.*
import java.lang.reflect.*
import javax.lang.model.element.Modifier
import kotlin.reflect.*

inline internal fun buildClass(
        name: String,
        initializer: TypeSpec.Builder.() -> Unit
): TypeSpec = TypeSpec.classBuilder(name).apply(initializer).build()

inline internal fun buildInterface(
        name: String,
        initializer: TypeSpec.Builder.() -> Unit
): TypeSpec = TypeSpec.interfaceBuilder(name).apply(initializer).build()

inline internal fun buildInterface(
        name: ClassName,
        initializer: TypeSpec.Builder.() -> Unit
): TypeSpec = TypeSpec.interfaceBuilder(name).apply(initializer).build()

inline internal fun buildClass(
        className: ClassName,
        initializer: TypeSpec.Builder.() -> Unit
): TypeSpec = TypeSpec.classBuilder(className).apply(initializer).build()

inline internal fun TypeSpec.Builder.addAnnotation(
        type: Class<*>,
        initializer: AnnotationSpec.Builder.() -> Unit
) {
    addAnnotation(AnnotationSpec.builder(type).apply(initializer).build())
}

inline internal fun TypeSpec.Builder.addAnnotation(
        type: ClassName,
        initializer: AnnotationSpec.Builder.() -> Unit
) {
    addAnnotation(AnnotationSpec.builder(type).apply(initializer).build())
}

inline internal fun buildConstructor(
        initializer: MethodSpec.Builder.() -> Unit
): MethodSpec = MethodSpec.constructorBuilder().apply(initializer).build()

inline internal fun buildMethod(
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
        callback: MethodSpec.Builder.() -> Unit = {} // Any other custom init code, like adding statements
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

inline internal fun MethodSpec.Builder.addAnnotation(
        type: Class<*>,
        initializer: AnnotationSpec.Builder.() -> Unit
) {
    addAnnotation(AnnotationSpec.builder(type).apply(initializer).build())
}

inline internal fun MethodSpec.Builder.addAnnotation(
        type: ClassName,
        initializer: AnnotationSpec.Builder.() -> Unit
) {
    addAnnotation(AnnotationSpec.builder(type).apply(initializer).build())
}

inline internal fun buildField(
        type: Type,
        name: String,
        initializer: FieldSpec.Builder.() -> Unit
): FieldSpec = FieldSpec.builder(type, name).apply(initializer).build()

inline internal fun buildField(
        type: TypeName,
        name: String,
        initializer: FieldSpec.Builder.() -> Unit
): FieldSpec = FieldSpec.builder(type, name).apply(initializer).build()

inline internal fun FieldSpec.Builder.addAnnotation(
        type: Class<*>,
        initializer: AnnotationSpec.Builder.() -> Unit
) {
    addAnnotation(AnnotationSpec.builder(type).apply(initializer).build())
}

inline internal fun FieldSpec.Builder.addAnnotation(
        type: ClassName,
        initializer: AnnotationSpec.Builder.() -> Unit
) {
    addAnnotation(AnnotationSpec.builder(type).apply(initializer).build())
}

inline internal fun TypeSpec.Builder.addMethod(
        name: String,
        initializer: MethodSpec.Builder.() -> Unit
) {
    addMethod(MethodSpec.methodBuilder(name).apply(initializer).build())
}

inline internal fun TypeSpec.Builder.addField(
        type: TypeName,
        name: String,
        vararg modifiers: Modifier,
        initializer: FieldSpec.Builder.() -> Unit
) {
    addField(FieldSpec.builder(type, name, *modifiers).apply(initializer).build())
}

inline internal fun TypeSpec.Builder.addConstructor(initializer: MethodSpec.Builder.() -> Unit) {
    addMethod(MethodSpec.constructorBuilder().apply(initializer).build())
}

inline internal fun MethodSpec.Builder.addParameter(
        type: TypeName,
        name: String,
        vararg modifiers: Modifier,
        initializer: ParameterSpec.Builder.() -> Unit
) {
    addParameter(ParameterSpec.builder(type, name, *modifiers).apply(initializer).build())
}

internal fun KClass<*>.className() = ClassName.get(this.java)
internal fun Class<*>.className() = ClassName.get(this)