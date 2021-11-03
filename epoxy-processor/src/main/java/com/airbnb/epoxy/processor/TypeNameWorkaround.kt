/**
 * This implementation is taken from Room XProcessing and patched to partially fix https://issuetracker.google.com/issues/204415667
 *
 * Changes are commented in the code below.
 */
package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XType
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isOpen
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Variance
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeVariableName
import com.squareup.javapoet.WildcardTypeName
import kotlin.coroutines.Continuation

fun XType.typeNameWithWorkaround(memoizer: Memoizer): TypeName {
    return memoizer.typeNameWithWorkaround(this)
}

// Catch-all type name when we cannot resolve to anything. This is what KAPT uses as error type
// and we use the same type in KSP for consistency.
// https://kotlinlang.org/docs/reference/kapt.html#non-existent-type-correction
internal val ERROR_TYPE_NAME = ClassName.get("error", "NonExistentClass")

/**
 * To handle self referencing types and avoid infinite recursion, we keep a lookup map for
 * TypeVariables.
 */
private typealias TypeArgumentTypeLookup = LinkedHashMap<KSName, TypeName>

internal fun TypeName.tryBox(): TypeName {
    return try {
        box()
    } catch (err: AssertionError) {
        this
    }
}

/**
 * Turns a KSTypeReference into a TypeName in java's type system.
 */
internal fun KSTypeReference?.typeName(resolver: Resolver): TypeName =
    typeName(
        resolver = resolver,
        typeArgumentTypeLookup = TypeArgumentTypeLookup()
    )

private fun KSTypeReference?.typeName(
    resolver: Resolver,
    typeArgumentTypeLookup: TypeArgumentTypeLookup
): TypeName {
    return if (this == null) {
        ERROR_TYPE_NAME
    } else {
        resolve().typeName(resolver, typeArgumentTypeLookup)
    }
}

/**
 * Turns a KSDeclaration into a TypeName in java's type system.
 */
internal fun KSDeclaration.typeName(resolver: Resolver): TypeName =
    typeName(
        resolver = resolver,
        typeArgumentTypeLookup = TypeArgumentTypeLookup()
    )

@OptIn(KspExperimental::class)
private fun KSDeclaration.typeName(
    resolver: Resolver,
    typeArgumentTypeLookup: TypeArgumentTypeLookup
): TypeName {
    if (this is KSTypeAlias) {
        return this.type.typeName(resolver, typeArgumentTypeLookup)
    }
    if (this is KSTypeParameter) {
        return this.typeName(resolver, typeArgumentTypeLookup)
    }
    // if there is no qualified name, it is a resolution error so just return shared instance
    // KSP may improve that later and if not, we can improve it in Room
    // TODO: https://issuetracker.google.com/issues/168639183
    val qualified = qualifiedName?.asString() ?: return ERROR_TYPE_NAME
    val jvmSignature = resolver.mapToJvmSignature(this)
    if (jvmSignature != null && jvmSignature.isNotBlank()) {
        return jvmSignature.typeNameFromJvmSignature()
    }

    // fallback to custom generation, it is very likely that this is an unresolved type
    // get the package name first, it might throw for invalid types, hence we use
    // safeGetPackageName
    val pkg = getNormalizedPackageName()
    // using qualified name and pkg, figure out the short names.
    val shortNames = if (pkg == "") {
        qualified
    } else {
        qualified.substring(pkg.length + 1)
    }.split('.')
    return ClassName.get(pkg, shortNames.first(), *(shortNames.drop(1).toTypedArray()))
}

// see https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3.2-200
internal fun String.typeNameFromJvmSignature(): TypeName {
    check(isNotEmpty())
    return when (this[0]) {
        'B' -> TypeName.BYTE
        'C' -> TypeName.CHAR
        'D' -> TypeName.DOUBLE
        'F' -> TypeName.FLOAT
        'I' -> TypeName.INT
        'J' -> TypeName.LONG
        'S' -> TypeName.SHORT
        'Z' -> TypeName.BOOLEAN
        'L' -> {
            val end = lastIndexOf(";")
            check(end > 0) {
                "invalid input $this"
            }
            val simpleNamesSeparator = lastIndexOf('/')
            val simpleNamesStart = if (simpleNamesSeparator < 0) {
                1 // first char is 'L'
            } else {
                simpleNamesSeparator + 1
            }
            val packageName = if (simpleNamesSeparator < 0) {
                // no package name
                ""
            } else {
                substring(1, simpleNamesSeparator).replace('/', '.')
            }
            val firstSimpleNameSeparator = indexOf('$', startIndex = simpleNamesStart)
            return if (firstSimpleNameSeparator < 0) {
                // not nested
                ClassName.get(packageName, substring(simpleNamesStart, end))
            } else {
                // nested class
                val firstSimpleName = substring(simpleNamesStart, firstSimpleNameSeparator)
                val restOfSimpleNames = substring(firstSimpleNameSeparator + 1, end)
                    .split('$')
                    .toTypedArray()
                ClassName.get(packageName, firstSimpleName, *restOfSimpleNames)
            }
        }
        '[' -> ArrayTypeName.of(substring(1).typeNameFromJvmSignature())
        else -> error("unexpected jvm signature $this")
    }
}

/**
 * Turns a KSTypeArgument into a TypeName in java's type system.
 */
internal fun KSTypeArgument.typeName(
    param: KSTypeParameter,
    resolver: Resolver
): TypeName = typeName(
    param = param,
    resolver = resolver,
    typeArgumentTypeLookup = TypeArgumentTypeLookup()
)

private fun KSTypeParameter.typeName(
    resolver: Resolver,
    typeArgumentTypeLookup: TypeArgumentTypeLookup
): TypeName {
    // see https://github.com/square/javapoet/issues/842
    typeArgumentTypeLookup[name]?.let {
        return it
    }
    val mutableBounds = mutableListOf<TypeName>()
    val typeName = createModifiableTypeVariableName(name = name.asString(), bounds = mutableBounds)
    typeArgumentTypeLookup[name] = typeName
    val resolvedBounds = bounds.map {
        it.typeName(resolver, typeArgumentTypeLookup).tryBox()
    }.toList()
    if (resolvedBounds.isNotEmpty()) {
        mutableBounds.addAll(resolvedBounds)
        mutableBounds.remove(TypeName.OBJECT)
    }
    typeArgumentTypeLookup.remove(name)
    return typeName
}

/**
 * This is the only function we change to fix https://issuetracker.google.com/issues/204415667
 */
private fun KSTypeArgument.typeName(
    param: KSTypeParameter,
    resolver: Resolver,
    typeArgumentTypeLookup: TypeArgumentTypeLookup
): TypeName {

    val typeName by lazy { type.typeName(resolver, typeArgumentTypeLookup).tryBox() }

    if (variance == Variance.STAR) {
        return WildcardTypeName.subtypeOf(TypeName.OBJECT)

        // TODO: Always returning an explicit * is not correct. Given a named type parameter and
        // a * in a use site declaration (eg Foo<A, *>) we need to be able to differentiate the two
        // and return the param type name instead of *, but we don't seem to have a way to tell
        // the two apart. The * case is more common for our use cases though, so preferring that for now.

//        return if (type == null || typeName == TypeName.OBJECT) {
//            // explicit *
//            WildcardTypeName.subtypeOf(TypeName.OBJECT)
//        } else {
//            param.typeName(resolver, typeArgumentTypeLookup)
//        }
    }

    // If the use site variance overrides declaration site variance (only in java sources)) we need to use that,
    // otherwise declaration site variance is inherited. Invariance is the default, so we check for that.
    return when (if (variance != Variance.INVARIANT) variance else param.variance) {
        Variance.CONTRAVARIANT -> {
            // It's impossible to have a super type of Object
            if (typeName == ClassName.OBJECT) {
                typeName
            } else {
                WildcardTypeName.supertypeOf(typeName)
            }
        }
        Variance.COVARIANT -> {
            // Cannot have a final type as an upper bound
            if (this@typeName.type?.resolve()?.declaration?.isOpen() == true) {
                WildcardTypeName.subtypeOf(typeName)
            } else {
                typeName
            }
        }
        else -> typeName
    }
}

/**
 * Turns a KSType into a TypeName in java's type system.
 */
internal fun KSType.typeName(resolver: Resolver): TypeName =
    typeName(
        resolver = resolver,
        typeArgumentTypeLookup = TypeArgumentTypeLookup()
    )

private fun KSType.typeName(
    resolver: Resolver,
    typeArgumentTypeLookup: TypeArgumentTypeLookup
): TypeName {
    return if (this.arguments.isNotEmpty()) {
        val args: Array<TypeName> = this.arguments
            .mapIndexed { index, typeArg ->
                typeArg.typeName(
                    param = this.declaration.typeParameters[index],
                    resolver = resolver,
                    typeArgumentTypeLookup = typeArgumentTypeLookup
                )
            }
            .map { it.tryBox() }
            .let { args ->
                if (this.isSuspendFunctionType) args.convertToSuspendSignature()
                else args
            }
            .toTypedArray()

        when (
            val typeName = declaration
                .typeName(resolver, typeArgumentTypeLookup).tryBox()
        ) {
            is ArrayTypeName -> ArrayTypeName.of(args.single())
            is ClassName -> ParameterizedTypeName.get(
                typeName,
                *args
            )
            else -> error("Unexpected type name for KSType: $typeName")
        }
    } else {
        this.declaration.typeName(resolver, typeArgumentTypeLookup)
    }
}

/**
 * Transforms [this] list of arguments to a suspend signature. For a [suspend] functional type, we
 * need to transform it to be a FunctionX with a [Continuation] with the correct return type. A
 * transformed SuspendFunction looks like this:
 *
 * FunctionX<[? super $params], ? super Continuation<? super $ReturnType>, ?>
 */
private fun List<TypeName>.convertToSuspendSignature(): List<TypeName> {
    val args = this

    // The last arg is the return type, so take everything except the last arg
    val actualArgs = args.subList(0, args.size - 1)
    val continuationReturnType = WildcardTypeName.supertypeOf(args.last())
    val continuationType = ParameterizedTypeName.get(
        ClassName.get(Continuation::class.java),
        continuationReturnType
    )
    return actualArgs + listOf(
        WildcardTypeName.supertypeOf(continuationType),
        WildcardTypeName.subtypeOf(TypeName.OBJECT)
    )
}

/**
 * Root package comes as <root> instead of "" so we work around it here.
 */
internal fun KSDeclaration.getNormalizedPackageName(): String {
    return packageName.asString().let {
        if (it == "<root>") {
            ""
        } else {
            it
        }
    }
}

/**
 * The private constructor of [TypeVariableName] which receives a list.
 * We use this in [createModifiableTypeVariableName] to create a [TypeVariableName] whose bounds
 * can be modified afterwards.
 */
private val typeVarNameConstructor by lazy {
    try {
        TypeVariableName::class.java.getDeclaredConstructor(
            String::class.java,
            List::class.java
        ).also {
            it.isAccessible = true
        }
    } catch (ex: NoSuchMethodException) {
        throw IllegalStateException(
            """
            Room couldn't find the constructor it is looking for in JavaPoet.
            Please file a bug.
            """.trimIndent(),
            ex
        )
    }
}

/**
 * Creates a TypeVariableName where we can change the bounds after constructor.
 * This is used to workaround a case for self referencing type declarations.
 * see b/187572913 for more details
 */
private fun createModifiableTypeVariableName(
    name: String,
    bounds: List<TypeName>
): TypeVariableName = typeVarNameConstructor.newInstance(
    name,
    bounds
) as TypeVariableName
