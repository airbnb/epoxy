package com.airbnb.epoxy

import android.support.annotation.NonNull
import com.airbnb.epoxy.ModelProp.Option
import com.airbnb.epoxy.Utils.capitalizeFirstLetter
import com.airbnb.epoxy.Utils.getDefaultValue
import com.airbnb.epoxy.Utils.isFieldPackagePrivate
import com.airbnb.epoxy.Utils.removeSetPrefix
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeVariableName
import org.jetbrains.annotations.NotNull
import java.util.*
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

internal class ViewAttributeInfo(
        private val modelInfo: ModelViewInfo,
        viewAttributeElement: Element,
        types: Types,
        elements: Elements,
        errorLogger: ErrorLogger,
        resourceProcessor: ResourceProcessor
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
        val param = when (viewAttributeElement) {
            is ExecutableElement -> viewAttributeElement.parameters[0]
            is VariableElement -> viewAttributeElement
            else -> throw IllegalStateException("Unsuppported element type $viewAttributeElement")
        }

        viewAttributeTypeName = getViewAttributeType(viewAttributeElement, errorLogger);

        groupKey = ""
        var defaultConstant = ""
        if (propAnnotation != null) {
            defaultConstant = propAnnotation.defaultValue
            groupKey = propAnnotation.group
            options.addAll(Arrays.asList(*propAnnotation.options))
            options.addAll(Arrays.asList(*propAnnotation.value))
        } else if (textAnnotation != null) {
            val stringResValue = textAnnotation.defaultRes
            if (stringResValue != 0) {
                val stringResource = resourceProcessor
                        .getStringResourceInAnnotation(viewAttributeElement, TextProp::class.java,
                                                       stringResValue)
                codeToSetDefault.explicit = stringResource.code
            }
            options.add(Option.GenerateStringOverloads)
        } else if (callbackAnnotation != null) {
            options.add(Option.DoNotHash)
            if (isMarkedNullable(param)) {
                options.add(Option.NullOnRecycle)
            } else {
                errorLogger.logError("Setters with %s must be marked Nullable",
                                     CallbackProp::class.java.simpleName)
            }
        }

        generateSetter = true
        generateGetter = true
        hasFinalModifier = false
        packagePrivate = isFieldPackagePrivate(viewAttributeElement)
        isGenerated = true

        useInHash = !options.contains(Option.DoNotHash)
        ignoreRequireHashCode = options.contains(Option.IgnoreRequireHashCode)
        resetWithNull = options.contains(Option.NullOnRecycle)
        generateStringOverloads = options.contains(Option.GenerateStringOverloads)

        modelName = modelInfo.generatedName.simpleName()
        modelPackageName = modelInfo.generatedClassName.packageName()

        this.viewAttributeName = viewAttributeElement.simpleName.toString()
        propName = removeSetPrefix(viewAttributeName)
        typeMirror = param.asType()

        assignDefaultValue(defaultConstant, errorLogger, types)
        assignNullability(param, typeMirror)

        // TODO: (eli_hart 9/26/17) Get the javadoc on the super method if this setter overrides
        // something and doesn't have its own javadoc
        createJavaDoc(elements.getDocComment(viewAttributeElement), codeToSetDefault,
                      constantFieldNameForDefaultValue,
                      modelInfo.viewElement, typeMirror, viewAttributeName)

        validatePropOptions(errorLogger, options, types, elements)

        if (generateStringOverloads) {
            typeMirror = getTypeMirror(ClassNames.EPOXY_STRING_ATTRIBUTE_DATA, elements,
                                       types)

            if (codeToSetDefault.isPresent) {
                if (codeToSetDefault.explicit != null) {
                    codeToSetDefault.explicit = CodeBlock.of(" new \$T(\$L)", typeMirror,
                                                             codeToSetDefault.explicit)
                }

                if (codeToSetDefault.implicit != null) {
                    codeToSetDefault.implicit = CodeBlock.of(" new \$T(\$L)", typeMirror,
                                                             codeToSetDefault.implicit)
                }
            } else {
                codeToSetDefault.implicit = CodeBlock.of(" new \$T()", typeMirror)
            }
        }

        // Suffix the field name with the type to prevent collisions from overloaded setter methods
        this.fieldName = propName + "_" + getSimpleName(typeName)

        parseAnnotations(param, types, isMarkedNullable(param), TypeName.get(typeMirror))
        if (generateStringOverloads) {
            // Since we generate other setters like @StringRes it doesn't make sense to carryover
            // annotations that might not apply to other param types
            setterAnnotations.clear()
            getterAnnotations.clear()
        }
    }

    internal override fun isRequired() =
            if (generateStringOverloads) {
                !isNullable && constantFieldNameForDefaultValue == null
            } else {
                super.isRequired()
            }

    private fun getViewAttributeType(
            element: Element,
            errorLogger: ErrorLogger
    ): ViewAttributeType? = when {
        element.kind == ElementKind.METHOD -> ViewAttributeType.Method
        element.kind == ElementKind.FIELD -> ViewAttributeType.Field
        else -> {
            errorLogger.logError("Element must be either method or field (element: %s)",
                                 element)
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

    private fun isMarkedNullable(paramElement: VariableElement)
            = paramElement.annotationMirrors.any {
        // There are multiple packages/frameworks that define a Nullable annotation and we want to
        // support all of them. We just check for a class named Nullable and ignore the package.
        it.annotationType.asElement().simpleName.toString() == "Nullable"
    }

    private fun assignDefaultValue(
            defaultConstant: String,
            errorLogger: ErrorLogger,
            types: Types
    ) {

        if (defaultConstant.isEmpty()) {
            if (isPrimitive) {
                codeToSetDefault.implicit = CodeBlock.of(getDefaultValue(typeName))
            }

            return
        }

        var viewClass: TypeElement? = modelInfo.viewElement
        while (viewClass != null) {
            for (element in viewClass.enclosedElements) {
                if (checkElementForConstant(element, defaultConstant, types, errorLogger)) {
                    return
                }
            }

            viewClass = modelInfo.viewElement.getParentClassElement(types)
        }

        errorLogger.logError(
                "The default value for (%s#%s) could not be found. Expected a constant named '%s' in the " + "view class.",
                modelInfo.viewElement.simpleName, viewAttributeName, defaultConstant)
    }

    private fun checkElementForConstant(
            element: Element,
            constantName: String,
            types: Types,
            errorLogger: ErrorLogger
    ): Boolean {
        if (element.kind == ElementKind.FIELD && element.simpleName.toString() == constantName) {

            val modifiers = element.modifiers
            if (!modifiers.contains(Modifier.FINAL)
                    || !modifiers.contains(Modifier.STATIC)
                    || modifiers.contains(Modifier.PRIVATE)) {

                errorLogger.logError(
                        "Default values for view props must be static, final, and not private. (%s#%s)",
                        modelInfo.viewElement.simpleName, viewAttributeName)
                return true
            }

            // Make sure that the type of the default value is a valid type for the prop
            if (!types.isAssignable(element.asType(), typeMirror)) {
                errorLogger.logError(
                        "The default value for (%s#%s) must be a %s.",
                        modelInfo.viewElement.simpleName, viewAttributeName, typeMirror)
                return true
            }
            constantFieldNameForDefaultValue = constantName

            codeToSetDefault.explicit = CodeBlock.of("\$T.\$L",
                                                     ClassName.get(modelInfo.viewElement),
                                                     constantName)

            return true
        }

        return false
    }

    private fun validatePropOptions(
            errorLogger: ErrorLogger,
            options: Set<Option>,
            types: Types,
            elements: Elements
    ) {
        if (options.contains(Option.IgnoreRequireHashCode) && options.contains(Option.DoNotHash)) {
            errorLogger
                    .logError("Illegal to use both %s and %s options in an %s annotation. (%s#%s)",
                              Option.DoNotHash, Option.IgnoreRequireHashCode,
                              ModelProp::class.java.simpleName, modelName, viewAttributeName)
        }

        if (options.contains(Option.GenerateStringOverloads) && !types.isAssignable(
                getTypeMirror(CharSequence::class.java, elements), typeMirror)) {
            errorLogger
                    .logError("Setters with %s option must be a CharSequence. (%s#%s)",
                              Option.GenerateStringOverloads, modelName, viewAttributeName)
        }

        if (options.contains(Option.NullOnRecycle) && (!hasSetNullability() || !isNullable)) {
            errorLogger
                    .logError(
                            "Setters with %s option must have a type that is annotated with @Nullable. (%s#%s)",
                            Option.NullOnRecycle, modelName, viewAttributeName)
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
                    annotationMirror.annotationType) as? TypeElement ?: continue

            val elementClassName = ClassName.get(annotationType)

            if (elementClassName in ModelViewProcessor.modelPropAnnotations.map { it.className() }) {
                continue
            }

            val builder = AnnotationSpec.builder(elementClassName)

            for ((key, value) in annotationMirror
                    .elementValues) {
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
        if (!typeName.isPrimitive
                && !markedNullable
                && NON_NULL_ANNOTATION_SPEC !in setterAnnotations
                && NOT_NULL_ANNOTATION_SPEC !in setterAnnotations) {
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

        val builder = javaDoc.toBuilder()

        if (!javaDoc.isEmpty) {
            builder.add("\n<p>\n")
        }

        if (isRequired) {
            builder.add("<i>Required.</i>")
        } else {
            builder.add("<i>Optional</i>: ")
            if (constantFieldNameForDefaultValue == null) {
                builder.add("Default value is \$L", codeToSetDefault.value())
            } else {
                builder.add("Default value is <b>{@value \$T#\$L}</b>", ClassName.get(viewElement),
                            constantFieldNameForDefaultValue)
            }
        }

        if (viewAttributeTypeName == ViewAttributeType.Field) {
            builder.add("\n\n@see \$T#\$L", viewElement.asType(), viewAttributeName)
        } else {
            builder.add("\n\n@see \$T#\$L(\$T)", viewElement.asType(), viewAttributeName,
                        typeMirror)
        }

        javaDoc = builder
                .add("\n").build()
    }

    internal override fun generatedSetterName(): String = propName

    internal override fun generatedGetterName(): String {
        if (isOverload) {
            // Avoid method name collisions for overloaded method by appending the return type
            return propName + getSimpleName(typeName)!!
        } else if (generateStringOverloads) {
            return "get" + capitalizeFirstLetter(propName)
        }

        return propName
    }

    override fun toString(): String {
        return ("View Prop {"
                + "view='" + modelInfo.viewElement.simpleName + '\''
                + ", name='" + viewAttributeName + '\''
                + ", type=" + typeName
                + '}')
    }
}
