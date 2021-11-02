package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XAnnotation
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XEnumEntry
import androidx.room.compiler.processing.XExecutableElement
import androidx.room.compiler.processing.XFieldElement
import androidx.room.compiler.processing.XHasModifiers
import androidx.room.compiler.processing.XMethodElement
import androidx.room.compiler.processing.XType
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.XVariableElement
import androidx.room.compiler.processing.compat.XConverters.toJavac
import androidx.room.compiler.processing.isVoid
import androidx.room.compiler.processing.isVoidObject
import com.airbnb.epoxy.processor.ClassNames.KOTLIN_ANY
import com.airbnb.epoxy.processor.resourcescanning.getFieldWithReflection
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.Origin
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterSpec
import java.lang.Character.isISOControl
import javax.lang.model.element.Modifier
import kotlin.reflect.KClass

/**
 * Look up enclosing type element if this is a field or function within a class.
 */
val XElement.enclosingTypeElement: XTypeElement?
    get() {
        return when (this) {
            is XExecutableElement -> enclosingElement as? XTypeElement
            is XFieldElement -> enclosingElement as? XTypeElement
            else -> null
        }
    }

fun XTypeElement.hasOverload(element: XMethodElement, paramCount: Int): Boolean {
    return findOverload(element, paramCount) != null
}

fun XTypeElement.findOverload(element: XMethodElement, paramCount: Int): XMethodElement? {
    require(element.parameters.size != paramCount) { "Element $element already has param count $paramCount" }

    return getDeclaredMethods()
        .firstOrNull { it.parameters.size == paramCount && areOverloads(it, element) }
}

/**
 * True if the two elements represent overloads of the same function in a class.
 */
fun areOverloads(e1: XMethodElement, e2: XMethodElement): Boolean {
    return e1.parameters.size != e2.parameters.size &&
        e1.name == e2.name &&
        e1.enclosingElement == e2.enclosingElement &&
        e1.returnType == e2.returnType &&
        e1.isStatic() == e2.isStatic() &&
        e1.isPrivate() == e2.isPrivate()
}

/** Return each of the classes in the class hierarchy, starting with the initial receiver and working upwards until Any. */
tailrec fun XElement.iterateClassHierarchy(
    classCallback: (classElement: XTypeElement) -> Unit
) {
    if (this !is XTypeElement) {
        return
    }

    classCallback(this)

    val superClazz = this.superType?.typeElement
    superClazz?.iterateClassHierarchy(classCallback)
}

/** Iterate each super class of the receiver, starting with the initial super class and going until Any. */
fun XElement.iterateSuperClasses(
    classCallback: (classElement: XTypeElement) -> Unit
) {
    iterateClassHierarchy {
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
fun XTypeElement.buildAnnotationSpecs(
    annotationFilter: (ClassName) -> Boolean,
    memoizer: Memoizer
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
    return getAllAnnotations()
        .map { it.toAnnotationSpec(memoizer) }
        .filter { internalAnnotationFilter(it.type as ClassName) }
}

fun XAnnotation.toAnnotationSpec(memoizer: Memoizer): AnnotationSpec {

    // Adapted from javapoet internals
    fun AnnotationSpec.Builder.addMemberForValue(
        memberName: String,
        value: Any?,
        memoizer: Memoizer
    ): AnnotationSpec.Builder {
        if (value is XType) {
            return if (value.isVoid() || value.isVoidObject()) {
                addMember(memberName, "Void.class")
            } else {
                addMember(memberName, "\$T.class", value.typeNameWithWorkaround(memoizer))
            }
        }
        if (value is XEnumEntry) {
            return addMember(memberName, "\$T.\$L", value.enumTypeElement.className, value.name)
        }
        if (value is XAnnotation) {
            return addMember(memberName, "\$L", value.toAnnotationSpec(memoizer))
        }
        if (value is List<*>) {
            if (value.isEmpty()) {
                addMember(memberName, "{}")
            } else {
                value.forEach { listValue ->
                    addMemberForValue(
                        memberName,
                        listValue ?: error("Unexpected null item in annotation value list"),
                        memoizer
                    )
                }
            }
            return this
        }
        if (value is String) {
            return addMember(memberName, "\$S", value)
        }
        if (value is Float) {
            return addMember(memberName, "\$Lf", value)
        }
        if (value is Char) {
            return addMember(
                memberName,
                "'\$L'",
                characterLiteralWithoutSingleQuotes(value)
            )
        }
        return addMember(memberName, "\$L", value)
    }
    return AnnotationSpec.builder(ClassName.get(packageName, name)).apply {
        annotationValues.forEach { annotationValue ->
            addMemberForValue(annotationValue.name, annotationValue.value, memoizer)
        }
    }.build()
}

fun XVariableElement.toParameterSpec(memoizer: Memoizer): ParameterSpec {
    val builder = ParameterSpec.builder(
        type.typeNameWithWorkaround(memoizer),
        name
    )
    for (annotation in getAllAnnotations()) {
        builder.addAnnotation(annotation.toAnnotationSpec(memoizer))
    }

    return builder.build()
}

fun characterLiteralWithoutSingleQuotes(c: Char): String {
    // see https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.6
    return when (c) {
        '\b' -> "\\b" /* \u0008: backspace (BS) */
        '\t' -> "\\t" /* \u0009: horizontal tab (HT) */
        '\n' -> "\\n" /* \u000a: linefeed (LF) */
        '\r' -> "\\r" /* \u000d: carriage return (CR) */
        '\"' -> "\"" /* \u0022: double quote (") */
        '\'' -> "\\'" /* \u0027: single quote (') */
        '\\' -> "\\\\" /* \u005c: backslash (\) */
        else -> if (isISOControl(c)) String.format("\\u%04x", c.code) else c.toString()
    }
}

val XAnnotation.packageName: String get() = qualifiedName.substringBeforeLast(".$name")

fun XTypeElement.isEpoxyModel(memoizer: Memoizer): Boolean {
    return isSubTypeOf(memoizer.epoxyModelClassElementUntyped)
}

fun XType.isEpoxyModel(memoizer: Memoizer): Boolean {
    return typeElement?.isEpoxyModel(memoizer) == true
}

fun XType.isDataBindingEpoxyModel(memoizer: Memoizer): Boolean {
    val databindingType = memoizer.epoxyDataBindingModelBaseClass?.type ?: return false
    return isSubTypeOf(databindingType)
}

fun XType.isEpoxyModelWithHolder(memoizer: Memoizer): Boolean {
    return isSubTypeOf(memoizer.epoxyModelWithHolderTypeUntyped)
}

fun XType.isEpoxyModelCollector(memoizer: Memoizer): Boolean {
    return isSubTypeOf(memoizer.epoxyModelCollectorType)
}

fun XTypeElement.isEpoxyController(memoizer: Memoizer): Boolean {
    return isSubTypeOf(memoizer.epoxyControllerType)
}

val XHasModifiers.javacModifiers: Set<Modifier>
    get() {
        return setOfNotNull(
            if (isPublic()) Modifier.PUBLIC else null,
            if (isProtected()) Modifier.PROTECTED else null,
            if (isAbstract()) Modifier.ABSTRACT else null,
            if (isPrivate()) Modifier.PRIVATE else null,
            if (isStatic()) Modifier.STATIC else null,
            if (isFinal()) Modifier.FINAL else null,
            if (isTransient()) Modifier.TRANSIENT else null,
        )
    }

val XElement.expectName: String
    get() = when (this) {
        is XVariableElement -> this.name
        is XMethodElement -> this.name
        is XTypeElement -> this.name
        else -> throw EpoxyProcessorException(
            "Expected this to be a variable or method $this",
            element = this
        )
    }

fun XType.isSubTypeOf(otherType: XType): Boolean {
    // Using the normal "isAssignableFrom" on XType doesn't always work correctly or predictably
    // with generics, so when we just want to check if something is a subclass without considering
    // that this is the simplest approach.
    // This is especially because we generally just use this to check class type hierarchies, not
    // parameter/field types.
    return otherType.rawType.isAssignableFrom(this)
}

fun XTypeElement.isSubTypeOf(otherType: XTypeElement): Boolean {
    return type.isSubTypeOf(otherType.type)
}

fun XTypeElement.isSubTypeOf(otherType: XType): Boolean {
    return type.isSubTypeOf(otherType)
}

fun XTypeElement.isInSamePackageAs(class2: XTypeElement): Boolean {
    return packageName == class2.packageName
}

fun XType.isObjectOrAny(): Boolean = typeName == KOTLIN_ANY || typeName == ClassName.OBJECT

val KSAnnotation.containingPackage: String?
    get() = parent?.containingPackage

val KSNode.containingPackage: String?
    get() {
        return when (this) {
            is KSFile -> packageName.asString()
            is KSDeclaration -> packageName.asString()
            else -> parent?.containingPackage
        }
    }

fun XElement.isJavaSourceInKsp(): Boolean {
    return try {
        val declaration = getFieldWithReflection<KSAnnotated>("declaration")
        // If getting the declaration succeeded then we are in KSP and we can check the source origin.
        declaration.origin == Origin.JAVA || declaration.origin == Origin.JAVA_LIB
    } catch (e: Throwable) {
        // Not KSP
        false
    }
}

fun XElement.isKotlinSourceInKsp(): Boolean {
    return try {
        val declaration = getFieldWithReflection<KSAnnotated>("declaration")
        // If getting the declaration succeeded then we are in KSP and we can check the source origin.
        declaration.origin == Origin.KOTLIN_LIB || declaration.origin == Origin.KOTLIN
    } catch (e: Throwable) {
        // Not KSP
        false
    }
}

val XFieldElement.declaration: KSPropertyDeclaration get() = getFieldWithReflection("declaration")

fun KSDeclaration.isKotlinOrigin(): Boolean {
    return when (origin) {
        Origin.KOTLIN -> true
        Origin.KOTLIN_LIB -> true
        Origin.JAVA -> false
        Origin.JAVA_LIB -> false
        Origin.SYNTHETIC -> false
    }
}

val XElement.isKsp: Boolean
    get() = try {
        toJavac()
        false
    } catch (e: Throwable) {
        true
    }

fun XTypeElement.getElementsAnnotatedWith(annotationClass: KClass<out Annotation>): List<XElement> {
    return listOf(this).getElementsAnnotatedWith(annotationClass)
}

fun List<XTypeElement>.getElementsAnnotatedWith(annotationClass: KClass<out Annotation>): List<XElement> {
    return flatMap { typeElement ->
        (typeElement.getDeclaredMethods() + typeElement.getDeclaredFields()).filter { xElement ->
            xElement.hasAnnotation(annotationClass)
        }
    }
}

data class MethodInfoLight(
    val name: String,
    val docComment: String?,
)

fun XTypeElement.getDeclaredMethodsLight(memoizer: Memoizer): List<MethodInfoLight> {
    return memoizer.getDeclaredMethodsLight(this)
}
