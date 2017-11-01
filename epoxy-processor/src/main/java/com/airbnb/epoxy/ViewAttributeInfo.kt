package com.airbnb.epoxy

import android.support.annotation.*
import com.airbnb.epoxy.ModelProp.*
import com.airbnb.epoxy.Utils.*
import com.squareup.javapoet.*
import org.jetbrains.annotations.*
import java.util.*
import javax.lang.model.element.*
import javax.lang.model.type.*
import javax.lang.model.util.*

private val NOT_NULL_ANNOTATION_SPEC = AnnotationSpec.builder(NotNull::class.java).build()
private val NON_NULL_ANNOTATION_SPEC = AnnotationSpec.builder(NonNull::class.java).build()

internal class ViewAttributeInfo(
        private val modelInfo: ModelViewInfo,
        setterElementOnView: ExecutableElement,
        types: Types,
        elements: Elements,
        errorLogger: ErrorLogger,
        resourceProcessor: ResourceProcessor
) : AttributeInfo() {
    val propName: String
    val viewSetterMethodName: String
    val resetWithNull: Boolean
    val generateStringOverloads: Boolean
    var constantFieldNameForDefaultValue: String? = null

    init {
        val propAnnotation = setterElementOnView.getAnnotation(ModelProp::class.java)
        val textAnnotation = setterElementOnView.getAnnotation(TextProp::class.java)
        val callbackAnnotation = setterElementOnView.getAnnotation(CallbackProp::class.java)

        val options = HashSet<Option>()
        val param = setterElementOnView.parameters[0]

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
                        .getStringResourceInAnnotation(setterElementOnView, TextProp::class.java,
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
        packagePrivate = isFieldPackagePrivate(setterElementOnView)
        isGenerated = true

        useInHash = !options.contains(Option.DoNotHash)
        ignoreRequireHashCode = options.contains(Option.IgnoreRequireHashCode)
        resetWithNull = options.contains(Option.NullOnRecycle)
        generateStringOverloads = options.contains(Option.GenerateStringOverloads)

        modelName = modelInfo.generatedName.simpleName()
        modelPackageName = modelInfo.generatedClassName.packageName()

        this.viewSetterMethodName = setterElementOnView.simpleName.toString()
        propName = removeSetPrefix(viewSetterMethodName)
        typeMirror = param.asType()

        assignDefaultValue(defaultConstant, errorLogger, types)
        assignNullability(param, typeMirror)

        // TODO: (eli_hart 9/26/17) Get the javadoc on the super method if this setter overrides
        // something and doesn't have its own javadoc
        createJavaDoc(elements.getDocComment(setterElementOnView), codeToSetDefault,
                      constantFieldNameForDefaultValue,
                      modelInfo.viewElement, typeMirror, viewSetterMethodName)

        validatePropOptions(errorLogger, options, types, elements)

        if (generateStringOverloads) {
            typeMirror = Utils.getTypeMirror(ClassNames.EPOXY_STRING_ATTRIBUTE_DATA, elements,
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
                modelInfo.viewElement.simpleName, viewSetterMethodName, defaultConstant)
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
                        modelInfo.viewElement.simpleName, viewSetterMethodName)
                return true
            }

            // Make sure that the type of the default value is a valid type for the prop
            if (!types.isAssignable(element.asType(), typeMirror)) {
                errorLogger.logError(
                        "The default value for (%s#%s) must be a %s.",
                        modelInfo.viewElement.simpleName, viewSetterMethodName, typeMirror)
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
                              ModelProp::class.java.simpleName, modelName, viewSetterMethodName)
        }

        if (options.contains(Option.GenerateStringOverloads) && !types.isAssignable(
                Utils.getTypeMirror(CharSequence::class.java, elements), typeMirror)) {
            errorLogger
                    .logError("Setters with %s option must be a CharSequence. (%s#%s)",
                              Option.GenerateStringOverloads, modelName, viewSetterMethodName)
        }

        if (options.contains(Option.NullOnRecycle) && (!hasSetNullability() || !isNullable)) {
            errorLogger
                    .logError(
                            "Setters with %s option must have a type that is annotated with @Nullable. (%s#%s)",
                            Option.NullOnRecycle, modelName, viewSetterMethodName)
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
            val annotationElement = types.asElement(annotationMirror.annotationType)
            val builder = AnnotationSpec.builder(ClassName.get(annotationElement as TypeElement))

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
            viewSetterMethodName: String
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

        builder.add("\n\n@see \$T#\$L(\$T)", viewElement.asType(), viewSetterMethodName, typeMirror)

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
                + ", name='" + viewSetterMethodName + '\''
                + ", type=" + typeName
                + '}')
    }
}
