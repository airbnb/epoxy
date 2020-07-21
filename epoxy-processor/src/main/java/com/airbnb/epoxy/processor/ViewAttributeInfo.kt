package com.airbnb.epoxy.processor

import androidx.annotation.NonNull
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelProp.Option
import com.airbnb.epoxy.TextProp
import com.airbnb.epoxy.processor.Utils.capitalizeFirstLetter
import com.airbnb.epoxy.processor.Utils.getDefaultValue
import com.airbnb.epoxy.processor.Utils.isAssignable
import com.airbnb.epoxy.processor.Utils.isFieldPackagePrivate
import com.airbnb.epoxy.processor.Utils.removeSetPrefix
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeVariableName
import org.jetbrains.annotations.NotNull
import java.util.HashSet
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

private val NOT_NULL_ANNOTATION_SPEC = AnnotationSpec.builder(NotNull::class.java).build()
private val NON_NULL_ANNOTATION_SPEC = AnnotationSpec.builder(NonNull::class.java).build()

sealed class ViewAttributeType {
    object Method : ViewAttributeType()
    object Field : ViewAttributeType()
}

class ViewAttributeInfo(
    private val viewElement: TypeElement,
    viewPackage: String,
    val hasDefaultKotlinValue: Boolean,
    viewAttributeElement: Element,
    types: Types,
    elements: Elements,
    logger: Logger,
    resourceProcessor: ResourceProcessor,
    memoizer: Memoizer
) : AttributeInfo() {
    val propName: String
    val viewAttributeName: String
    val resetWithNull: Boolean
    val generateStringOverloads: Boolean
    val viewAttributeTypeName: ViewAttributeType?
    var constantFieldNameForDefaultValue: String? = null

    init {
        val propAnnotation = viewAttributeElement.getAnnotation(ModelProp::class.java)
        val textAnnotation = viewAttributeElement.getAnnotation(TextProp::class.java)
        val callbackAnnotation = viewAttributeElement.getAnnotation(CallbackProp::class.java)

        val options = HashSet<Option>()
        val param: VariableElement = when (viewAttributeElement) {
            is ExecutableElement -> viewAttributeElement.parametersThreadSafe[0]
            is VariableElement -> viewAttributeElement
            else -> error("Unsuppported element type $viewAttributeElement")
        }.ensureLoaded()

        viewAttributeTypeName = getViewAttributeType(viewAttributeElement, logger)

        groupKey = ""
        var defaultConstant = ""
        if (propAnnotation != null) {
            defaultConstant = propAnnotation.defaultValue
            groupKey = propAnnotation.group
            options.addAll(propAnnotation.options)
            options.addAll(propAnnotation.value)
        } else if (textAnnotation != null) {
            val stringResValue = textAnnotation.defaultRes
            if (stringResValue != 0) {
                val stringResource = resourceProcessor
                    .getStringResourceInAnnotation(
                        viewAttributeElement, TextProp::class.java,
                        stringResValue
                    )
                codeToSetDefault.explicit = stringResource.code
            }
            options.add(Option.GenerateStringOverloads)
        } else if (callbackAnnotation != null) {
            options.add(Option.DoNotHash)
            if (isMarkedNullable(param)) {
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

        this.rootClass = viewElement.simpleName.toString()
        this.packageName = viewPackage

        this.viewAttributeName = viewAttributeElement.simpleName.toString()
        propName = removeSetPrefix(viewAttributeName)
        setTypeMirror(param.asType(), memoizer)
        assignDefaultValue(defaultConstant, logger, types)
        assignNullability(param, typeMirror)

        // TODO: (eli_hart 9/26/17) Get the javadoc on the super method if this setter overrides
        // something and doesn't have its own javadoc
        createJavaDoc(
            elements.getDocComment(viewAttributeElement),
            codeToSetDefault,
            constantFieldNameForDefaultValue,
            viewElement,
            typeMirror,
            viewAttributeName
        )

        validatePropOptions(logger, options, types, elements)

        if (generateStringOverloads) {
            setTypeMirror(
                getTypeMirror(
                    ClassNames.EPOXY_STRING_ATTRIBUTE_DATA, elements,
                    types
                ),
                memoizer
            )

            if (codeToSetDefault.isPresent) {
                if (codeToSetDefault.explicit != null) {
                    codeToSetDefault.explicit = CodeBlock.of(
                        " new \$T(\$L)", typeMirror,
                        codeToSetDefault.explicit
                    )
                }

                if (codeToSetDefault.implicit != null) {
                    codeToSetDefault.implicit = CodeBlock.of(
                        " new \$T(\$L)", typeMirror,
                        codeToSetDefault.implicit
                    )
                }
            } else {
                codeToSetDefault.implicit = CodeBlock.of(" new \$T()", typeMirror)
            }
        }

        // Suffix the field name with the type to prevent collisions from overloaded setter methods
        this.fieldName = propName + "_" + getSimpleName(typeName)

        parseAnnotations(param, types, isMarkedNullable(param), typeMirror.typeNameSynchronized())
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
        element: Element,
        logger: Logger
    ): ViewAttributeType? = when (element.kind) {
        ElementKind.METHOD -> ViewAttributeType.Method
        ElementKind.FIELD -> ViewAttributeType.Field
        else -> {
            logger.logError(
                "Element must be either method or field (element: %s)",
                element
            )
            null
        }
    }

    private fun assignNullability(
        paramElement: VariableElement,
        typeMirror: TypeMirror
    ) {
        if (isPrimitive) {
            return
        }

        // Default to not nullable
        isNullable = false

        if (isMarkedNullable(paramElement)) {
            isNullable = true
            // Need to cast the null because if there are other overloads with the same method
            // name this can be ambiguous and fail to compile
            codeToSetDefault.implicit = CodeBlock.of("(\$T) null", typeMirror)
        }
    }

    private fun isMarkedNullable(paramElement: VariableElement) =
        paramElement.annotationMirrors.any {
            // There are multiple packages/frameworks that define a Nullable annotation and we want
            // to support all of them. We just check for a class named Nullable and ignore the
            // package.
            it.annotationType.asElement().simpleName.toString() == "Nullable"
        }

    private fun assignDefaultValue(
        defaultConstant: String,
        logger: Logger,
        types: Types
    ) {

        if (hasDefaultKotlinValue) {
            if (defaultConstant.isNotEmpty()) {
                logger.logError(
                    "Default set via both kotlin parameter and annotation constant. Use only one. (%s#%s)",
                    viewElement.simpleName,
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

        var viewClass: TypeElement? = viewElement
        while (viewClass != null) {
            for (element in viewClass.enclosedElementsThreadSafe) {
                if (checkElementForConstant(element, defaultConstant, types, logger)) {
                    return
                }
            }

            viewClass = viewClass.superClassElement(types)
        }

        logger.logError(
            "The default value for (%s#%s) could not be found. Expected a constant named " +
                "'%s' in the " + "view class.",
            viewElement.simpleName, viewAttributeName, defaultConstant
        )
    }

    private fun checkElementForConstant(
        element: Element,
        constantName: String,
        types: Types,
        logger: Logger
    ): Boolean {
        if (element.kind == ElementKind.FIELD && element.simpleName.toString() == constantName) {

            val modifiers = element.modifiers
            if (!modifiers.contains(Modifier.FINAL) ||
                !modifiers.contains(Modifier.STATIC) ||
                modifiers.contains(Modifier.PRIVATE)
            ) {

                logger.logError(
                    "Default values for view props must be static, final, and not private. " +
                        "(%s#%s)",
                    viewElement.simpleName, viewAttributeName
                )
                return true
            }

            // Make sure that the type of the default value is a valid type for the prop
            if (!isAssignable(element.asType(), typeMirror, types)) {
                logger.logError(
                    "The default value for (%s#%s) must be a %s.",
                    viewElement.simpleName, viewAttributeName, typeMirror
                )
                return true
            }
            constantFieldNameForDefaultValue = constantName

            codeToSetDefault.explicit = CodeBlock.of(
                "\$T.\$L",
                ClassName.get(viewElement),
                constantName
            )

            return true
        }

        return false
    }

    private fun validatePropOptions(
        logger: Logger,
        options: Set<Option>,
        types: Types,
        elements: Elements
    ) {
        if (options.contains(Option.IgnoreRequireHashCode) && options.contains(Option.DoNotHash)) {
            logger
                .logError(
                    "Illegal to use both %s and %s options in an %s annotation. (%s#%s)",
                    Option.DoNotHash, Option.IgnoreRequireHashCode,
                    ModelProp::class.java.simpleName, rootClass, viewAttributeName
                )
        }

        if (options.contains(Option.GenerateStringOverloads) && !isAssignable(
            getTypeMirror(CharSequence::class.java, elements), typeMirror, types
        )
        ) {
            logger
                .logError(
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
            return capitalizeFirstLetter(name.toString())
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
        paramElement: VariableElement,
        types: Types,
        markedNullable: Boolean,
        typeName: TypeName
    ) {
        for (annotationMirror in paramElement.annotationMirrors) {
            val annotationType = types.asElement(
                annotationMirror.annotationType
            ) as? TypeElement ?: continue

            val elementClassName = ClassName.get(annotationType)

            if (elementClassName in ModelViewProcessor.modelPropAnnotations.map {
                it.className()
            }
            ) {
                continue
            }

            val builder = AnnotationSpec.builder(elementClassName)

            for ((key, value) in annotationMirror.elementValues) {
                val paramName = key.simpleName.toString()
                val paramValue = value.value.toString()
                builder.addMember(paramName, paramValue)
            }

            val annotationSpec = builder.build()
            setterAnnotations.add(annotationSpec)
            getterAnnotations.add(annotationSpec)
        }

        // If a param is an object and not nullable we add a non null annotation if it doesn't
        // already have it. This is to make the generated interface more effective for IDE
        // nullability tooling since we know epoxy will enforce that the param value is
        // non null at run time.
        if (!typeName.isPrimitive &&
            !markedNullable &&
            NON_NULL_ANNOTATION_SPEC !in setterAnnotations &&
            NOT_NULL_ANNOTATION_SPEC !in setterAnnotations
        ) {
            setterAnnotations.add(NON_NULL_ANNOTATION_SPEC)
            getterAnnotations.add(NON_NULL_ANNOTATION_SPEC)
        }
    }

    private fun createJavaDoc(
        docComment: String?,
        codeToSetDefault: AttributeInfo.DefaultValue,
        constantFieldNameForDefaultValue: String?,
        viewElement: TypeElement,
        typeMirror: TypeMirror,
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
                        "Default value is <b>{@value \$T#\$L}</b>", ClassName.get(viewElement),
                        constantFieldNameForDefaultValue
                    )
                }
            }
        }

        if (viewAttributeTypeName == ViewAttributeType.Field) {
            builder.add("\n\n@see \$T#\$L", viewElement.asType(), viewAttributeName)
        } else {
            builder.add(
                "\n\n@see \$T#\$L(\$T)", viewElement.asType(), viewAttributeName,
                typeMirror
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
                "view='" + viewElement.simpleName + '\'' +
                ", name='" + viewAttributeName + '\'' +
                ", type=" + typeName +
                ", hasDefaultKotlinValue=" + hasDefaultKotlinValue +
                '}'
            )
    }
}
