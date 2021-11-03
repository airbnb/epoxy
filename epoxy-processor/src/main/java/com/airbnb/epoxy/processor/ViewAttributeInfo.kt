package com.airbnb.epoxy.processor

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XFieldElement
import androidx.room.compiler.processing.XMethodElement
import androidx.room.compiler.processing.XNullability
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.XVariableElement
import androidx.room.compiler.processing.isField
import androidx.room.compiler.processing.isMethod
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelProp.Option
import com.airbnb.epoxy.TextProp
import com.airbnb.epoxy.processor.Utils.capitalizeFirstLetter
import com.airbnb.epoxy.processor.Utils.getDefaultValue
import com.airbnb.epoxy.processor.Utils.isFieldPackagePrivate
import com.airbnb.epoxy.processor.Utils.removeSetPrefix
import com.airbnb.epoxy.processor.resourcescanning.ResourceScanner
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeVariableName
import java.util.HashSet

internal val NON_NULL_ANNOTATION_SPEC = AnnotationSpec.builder(NonNull::class.java).build()
internal val NULLABLE_ANNOTATION_SPEC = AnnotationSpec.builder(Nullable::class.java).build()

sealed class ViewAttributeType {
    object Method : ViewAttributeType()
    object Field : ViewAttributeType()
}

class ViewAttributeInfo(
    private val viewElement: XTypeElement,
    viewPackage: String,
    val hasDefaultKotlinValue: Boolean,
    val viewAttributeElement: XElement,
    logger: Logger,
    resourceProcessor: ResourceScanner,
    memoizer: Memoizer
) : AttributeInfo(memoizer) {
    val propName: String
    val viewAttributeName: String
    val resetWithNull: Boolean
    val generateStringOverloads: Boolean
    val viewAttributeTypeName: ViewAttributeType?
    var constantFieldNameForDefaultValue: String? = null

    init {
        val propAnnotation = viewAttributeElement.getAnnotation(ModelProp::class)
        val textAnnotation = viewAttributeElement.getAnnotation(TextProp::class)
        val callbackAnnotation = viewAttributeElement.getAnnotation(CallbackProp::class)

        val options = HashSet<Option>()
        val param: XVariableElement = when (viewAttributeElement) {
            is XMethodElement -> viewAttributeElement.parameters.first()
            is XVariableElement -> viewAttributeElement
            else -> error("Unsupported element type $viewAttributeElement")
        }

        viewAttributeTypeName = getViewAttributeType(viewAttributeElement, logger)

        groupKey = ""
        var defaultConstant = ""
        if (propAnnotation != null) {
            defaultConstant = propAnnotation.value.defaultValue
            groupKey = propAnnotation.value.group
            options.addAll(propAnnotation.value.options)
            options.addAll(propAnnotation.value.value)
        } else if (textAnnotation != null) {
            val stringResValue = textAnnotation.value.defaultRes
            if (stringResValue != 0) {
                val stringResource = resourceProcessor.getResourceValue(
                    TextProp::class,
                    viewAttributeElement,
                    "defaultRes",
                    stringResValue
                )
                if (!stringResource.isStringResource()) {
                    logger.logError(
                        viewAttributeElement,
                        "@TextProp value for defaultRes must be a String resource."
                    )
                }
                codeToSetDefault.explicit = stringResource.code
            }
            options.add(Option.GenerateStringOverloads)
        } else if (callbackAnnotation != null) {
            options.add(Option.DoNotHash)
            if (param.isNullable()) {
                options.add(Option.NullOnRecycle)
            } else {
                logger.logError(
                    "Setters with %s must be marked Nullable",
                    CallbackProp::class.java.simpleName
                )
            }
        }

        generateSetter = true
        generateGetter = true
        hasFinalModifier = false
        isPackagePrivate = isFieldPackagePrivate(viewAttributeElement)
        isGenerated = true

        useInHash = Option.DoNotHash !in options
        ignoreRequireHashCode = Option.IgnoreRequireHashCode in options
        resetWithNull = Option.NullOnRecycle in options
        generateStringOverloads = Option.GenerateStringOverloads in options

        this.rootClass = viewElement.name
        this.packageName = viewPackage

        this.viewAttributeName = viewAttributeElement.expectName
        propName = removeSetPrefix(viewAttributeName)
        setXType(param.type, memoizer)
        assignDefaultValue(defaultConstant, logger)
        assignNullability(param)

        // TODO: (eli_hart 9/26/17) Get the javadoc on the super method if this setter overrides
        // something and doesn't have its own javadoc
        createJavaDoc(
            viewAttributeElement.docComment,
            codeToSetDefault,
            constantFieldNameForDefaultValue,
            viewElement,
            typeName,
            viewAttributeName
        )

        validatePropOptions(logger, options, memoizer)

        if (generateStringOverloads) {
            setXType(
                memoizer.stringAttributeType,
                memoizer
            )

            if (codeToSetDefault.isPresent) {
                if (codeToSetDefault.explicit != null) {
                    codeToSetDefault.explicit = CodeBlock.of(
                        " new \$T(\$L)", typeName,
                        codeToSetDefault.explicit
                    )
                }

                if (codeToSetDefault.implicit != null) {
                    codeToSetDefault.implicit = CodeBlock.of(
                        " new \$T(\$L)", typeName,
                        codeToSetDefault.implicit
                    )
                }
            } else {
                codeToSetDefault.implicit = CodeBlock.of(" new \$T()", typeName)
            }
        }

        // Suffix the field name with the type to prevent collisions from overloaded setter methods
        this.fieldName = propName + "_" + getSimpleName(typeName)

        parseAnnotations(param, param.isNullable(), typeName)
        if (generateStringOverloads) {
            // Since we generate other setters like @StringRes it doesn't make sense to carryover
            // annotations that might not apply to other param types
            setterAnnotations.clear()
            getterAnnotations.clear()
        }
    }

    override val isRequired
        get() = when {
            hasDefaultKotlinValue -> false
            generateStringOverloads -> !isNullable() && constantFieldNameForDefaultValue == null
            else -> super.isRequired
        }

    private fun getViewAttributeType(
        element: XElement,
        logger: Logger
    ): ViewAttributeType? = when {
        element.isMethod() -> ViewAttributeType.Method
        element.isField() -> ViewAttributeType.Field
        else -> {
            logger.logError(
                element,
                "Element must be either method or field (element: %s)",
                element
            )
            null
        }
    }

    private fun assignNullability(
        paramElement: XVariableElement,
    ) {
        if (isPrimitive) {
            return
        }

        // Default to not nullable
        isNullable = false

        if (paramElement.isNullable()) {
            isNullable = true
            // Need to cast the null because if there are other overloads with the same method
            // name this can be ambiguous and fail to compile
            codeToSetDefault.implicit = CodeBlock.of("(\$T) null", typeName)
        }
    }

    private fun XVariableElement.isNullable(): Boolean {
        // There are multiple packages/frameworks that define a Nullable annotation and we want
        // to support all of them. We just check for a class named Nullable and ignore the
        // package.
        fun hasNullableAnnotation() = getAllAnnotations().any { it.name == "Nullable" }

        // When processing with KSP nullability is reported differently - we can't look at nullability
        // annotations as those are only generated for kotlin types in kapt.
        // KSP can also report normal java types as nullable if there are no nullability annotations
        // on them, but we consider the lack of a nullability annotation as not null
        return if (memoizer.environment.backend == XProcessingEnv.Backend.KSP) {
            if (isJavaSourceInKsp()) {
                hasNullableAnnotation()
            } else {
                type.nullability == XNullability.NULLABLE
            }
        } else {
            hasNullableAnnotation()
        }
    }

    private fun assignDefaultValue(
        defaultConstant: String,
        logger: Logger,
    ) {

        if (hasDefaultKotlinValue) {
            if (defaultConstant.isNotEmpty()) {
                logger.logError(
                    "Default set via both kotlin parameter and annotation constant. Use only one. (%s#%s)",
                    viewElement.name,
                    viewAttributeName
                )
            }
            return
        }

        if (defaultConstant.isEmpty()) {
            if (isPrimitive) {
                codeToSetDefault.implicit = CodeBlock.of(getDefaultValue(typeName))
            }

            return
        }

        var viewClass: XTypeElement? = viewElement
        while (viewClass != null) {
            for (element in viewClass.getDeclaredFields()) {
                if (checkElementForConstant(element, defaultConstant, logger)) {
                    return
                }
            }

            viewClass = viewClass.superType?.typeElement
        }

        logger.logError(
            viewElement,
            "The default value for (%s#%s) could not be found. Expected a constant named " +
                "'%s' in the " + "view class.",
            viewElement.name, viewAttributeName, defaultConstant
        )
    }

    private fun checkElementForConstant(
        element: XFieldElement,
        constantName: String,
        logger: Logger
    ): Boolean {
        if (!element.isField() || element.name != constantName) {
            return false
        }

        if (!element.isFinal() ||
            !element.isStatic() ||
            // KSP/XProcessing sees companion property fields as private even when they're not.
            // It would be hard to look up the correct information with xprocessing, so we just
            // ignore that check with ksp. If it is actually private it will be a compiler error
            // when the generated code accesses it, which will still be fairly clear.
            (element.isPrivate() && memoizer.environment.backend != XProcessingEnv.Backend.KSP)
        ) {
            logger.logError(
                element,
                "Default values for view props must be static, final, and not private. " +
                    "(%s#%s)",
                viewElement.name, viewAttributeName
            )
            return true
        }

        // Make sure that the type of the default value is a valid type for the prop
        if (!element.type.isSubTypeOf(xType)) {
            logger.logError(
                element,
                "The default value for (%s#%s) must be a %s.",
                viewElement.name, viewAttributeName, typeName
            )
            return true
        }
        constantFieldNameForDefaultValue = constantName

        codeToSetDefault.explicit = CodeBlock.of(
            "\$T.\$L",
            viewElement.className,
            constantName
        )

        return true
    }

    private fun validatePropOptions(
        logger: Logger,
        options: Set<Option>,
        memoizer: Memoizer
    ) {
        if (options.contains(Option.IgnoreRequireHashCode) && options.contains(Option.DoNotHash)) {
            logger
                .logError(
                    "Illegal to use both %s and %s options in an %s annotation. (%s#%s)",
                    Option.DoNotHash, Option.IgnoreRequireHashCode,
                    ModelProp::class.java.simpleName, rootClass, viewAttributeName
                )
        }

        if (options.contains(Option.GenerateStringOverloads) &&
            !(xType.isSameType(memoizer.charSequenceType) || xType.isSameType(memoizer.charSequenceNullableType))
        ) {
            logger
                .logError(
                    viewAttributeElement,
                    "Setters with %s option must be a CharSequence. (%s#%s)",
                    Option.GenerateStringOverloads, rootClass, viewAttributeName
                )
        }

        if (options.contains(Option.NullOnRecycle) && (!hasSetNullability() || !isNullable())) {
            logger
                .logError(
                    "Setters with %s option must have a type that is annotated with @Nullable. " +
                        "(%s#%s)",
                    Option.NullOnRecycle, rootClass, viewAttributeName
                )
        }
    }

    /** Tries to return the simple name of the given type.  */
    private fun getSimpleName(name: TypeName): String? {
        if (name.isPrimitive) {
            return capitalizeFirstLetter(name.withoutAnnotations().toString())
        }

        return when (name) {
            is ClassName -> name.simpleName()
            is ArrayTypeName -> getSimpleName(name.componentType)!! + "Array"
            is ParameterizedTypeName -> getSimpleName(name.rawType)
            is TypeVariableName -> capitalizeFirstLetter(name.name)
            // Don't expect this to happen
            else -> name.toString().replace(".", "")
        }
    }

    private fun parseAnnotations(
        paramElement: XVariableElement,
        markedNullable: Boolean,
        typeName: TypeName
    ) {
        for (xAnnotation in paramElement.getAllAnnotations()) {

            if (xAnnotation.name in ModelViewProcessor.modelPropAnnotationSimpleNames) {
                continue
            }

            val annotationSpec = xAnnotation.toAnnotationSpec(memoizer)
            setterAnnotations.add(annotationSpec)
            getterAnnotations.add(annotationSpec)
        }

        // If a param is an object and not nullable we add a non null annotation if it doesn't
        // already have it. This is to make the generated interface more effective for IDE
        // nullability tooling since we know epoxy will enforce that the param value is
        // non null at run time.

        // When KAPT processes kotlin sources it sees java intermediary, which has automatically
        // generated nullability annotations which we automatically inherit. However, with
        // KSP we see the kotlin code directly so we don't get those annotations by default
        // and our poet classnames lose nullability info, so we add it manually.

        // primitives cannot be null
        if (!typeName.isPrimitive) {

            // Look at just simple name of annotation as there are many packages providing them (eg androidx, jetbrains)
            val annotations = setterAnnotations.map { annotation ->
                when (val type = annotation.type) {
                    is ClassName -> type.simpleName()
                    else -> annotation.toString().substringAfterLast(".")
                }
            }

            if (markedNullable) {
                if (annotations.none { it == "Nullable" }) {
                    setterAnnotations.add(NULLABLE_ANNOTATION_SPEC)
                    getterAnnotations.add(NULLABLE_ANNOTATION_SPEC)
                }
            } else {
                if (annotations.none { it == "NotNull" || it == "NonNull" }) {
                    setterAnnotations.add(NON_NULL_ANNOTATION_SPEC)
                    getterAnnotations.add(NON_NULL_ANNOTATION_SPEC)
                }
            }
        }
    }

    private fun createJavaDoc(
        docComment: String?,
        codeToSetDefault: DefaultValue,
        constantFieldNameForDefaultValue: String?,
        viewElement: XTypeElement,
        attributeTypeName: TypeName,
        viewAttributeName: String
    ) {
        setJavaDocString(docComment)

        if (javaDoc == null) {
            javaDoc = CodeBlock.of("")
        }

        val builder = javaDoc!!.toBuilder()

        if (!javaDoc!!.isEmpty) {
            builder.add("\n<p>\n")
        }

        if (isRequired) {
            builder.add("<i>Required.</i>")
        } else {
            builder.add("<i>Optional</i>: ")
            when {
                hasDefaultKotlinValue -> {
                    builder.add("View function has a Kotlin default argument")
                }
                constantFieldNameForDefaultValue == null -> {
                    builder.add("Default value is \$L", codeToSetDefault.value())
                }
                else -> {
                    builder.add(
                        "Default value is <b>{@value \$T#\$L}</b>", viewElement.className,
                        constantFieldNameForDefaultValue
                    )
                }
            }
        }

        if (viewAttributeTypeName == ViewAttributeType.Field) {
            builder.add("\n\n@see \$T#\$L", viewElement.type.typeNameWithWorkaround(memoizer), viewAttributeName)
        } else {
            builder.add(
                "\n\n@see \$T#\$L(\$T)", viewElement.type.typeNameWithWorkaround(memoizer), viewAttributeName,
                attributeTypeName
            )
        }

        javaDoc = builder
            .add("\n").build()
    }

    override fun generatedSetterName(): String = propName

    override fun generatedGetterName(isOverload: Boolean): String {
        if (isOverload) {
            // Avoid method name collisions for overloaded method by appending the return type
            return propName + getSimpleName(typeName)!!
        } else if (generateStringOverloads) {
            return "get" + capitalizeFirstLetter(propName)
        }

        return propName
    }

    override fun toString(): String {
        return (
            "View Prop {" +
                "view='" + viewElement.name + '\'' +
                ", name='" + viewAttributeName + '\'' +
                ", type=" + typeName +
                ", hasDefaultKotlinValue=" + hasDefaultKotlinValue +
                '}'
            )
    }
}
