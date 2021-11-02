package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XAnnotation
import androidx.room.compiler.processing.XAnnotationBox
import androidx.room.compiler.processing.XFieldElement
import androidx.room.compiler.processing.XNullability
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XTypeElement
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.processor.Utils.capitalizeFirstLetter
import com.airbnb.epoxy.processor.Utils.isFieldPackagePrivate
import com.airbnb.epoxy.processor.Utils.startsWithIs
import com.google.devtools.ksp.symbol.Origin
import com.squareup.javapoet.ClassName
import java.lang.annotation.ElementType
import java.lang.annotation.Target

internal class BaseModelAttributeInfo(
    attribute: XFieldElement,
    logger: Logger,
    memoizer: Memoizer
) : AttributeInfo(memoizer) {

    private val classElement: XTypeElement = attribute.enclosingElement as XTypeElement

    init {
        fieldName = attribute.name
        setXType(attribute.type, memoizer)
        setJavaDocString(attribute.docComment)
        rootClass = classElement.name
        packageName = classElement.packageName
        hasSuperSetter = classElement.hasSuperMethod(attribute)

        hasFinalModifier = if (memoizer.environment.backend == XProcessingEnv.Backend.KSP) {
            // Kotlin properties don't have a "final" modifier like Java, and the final modifier
            // is incorrectly reported as true from java classpath classes,
            // even when they are mutable. so we check it like this.
            val declaration = attribute.declaration
            if (declaration.origin == Origin.JAVA) {
                attribute.isFinal()
            } else {
                !declaration.isMutable
            }
        } else {
            attribute.isFinal()
        }

        isPackagePrivate = isFieldPackagePrivate(attribute)
        val annotationBox: XAnnotationBox<EpoxyAttribute> =
            attribute.requireAnnotation(EpoxyAttribute::class)
        val options: Set<EpoxyAttribute.Option> = annotationBox.value.value.toSet()
        validateAnnotationOptions(logger, annotationBox.value, options)
        useInHash = annotationBox.value.hash && !options.contains(EpoxyAttribute.Option.DoNotHash)
        ignoreRequireHashCode = options.contains(EpoxyAttribute.Option.IgnoreRequireHashCode)
        doNotUseInToString = options.contains(EpoxyAttribute.Option.DoNotUseInToString)
        generateSetter =
            annotationBox.value.setter && !options.contains(EpoxyAttribute.Option.NoSetter)
        generateGetter = !options.contains(EpoxyAttribute.Option.NoGetter)
        isPrivate = attribute.isPrivate()
        if (isPrivate) {
            findGetterAndSetterForPrivateField(logger)
        }
        buildAnnotationLists(attribute, attribute.getAllAnnotations())
    }

    /**
     * Check if the given class or any of its super classes have a super method with the given name.
     * Private methods are ignored since the generated subclass can't call super on those.
     */
    private fun XTypeElement.hasSuperMethod(attribute: XFieldElement): Boolean {
        if (!type.isEpoxyModel(memoizer)) {
            return false
        }
        val hasImplementation = getDeclaredMethods().any { method ->
            !method.isPrivate() &&
                method.name == attribute.name &&
                method.parameters.singleOrNull()?.type == attribute.type
        }

        return hasImplementation || superType?.typeElement?.hasSuperMethod(attribute) == true
    }

    private fun validateAnnotationOptions(
        logger: Logger,
        annotation: EpoxyAttribute?,
        options: Set<EpoxyAttribute.Option>
    ) {
        if (options.contains(EpoxyAttribute.Option.IgnoreRequireHashCode) && options.contains(
                EpoxyAttribute.Option.DoNotHash
            )
        ) {
            logger.logError(
                "Illegal to use both %s and %s options in an %s annotation. (%s#%s)",
                EpoxyAttribute.Option.DoNotHash,
                EpoxyAttribute.Option.IgnoreRequireHashCode,
                EpoxyAttribute::class.java.simpleName,
                classElement.name,
                fieldName
            )
        }

        // Don't let legacy values be mixed with the new Options values
        if (options.isNotEmpty()) {
            if (!annotation!!.hash) {
                logger.logError(
                    "Don't use hash=false in an %s if you are using options. Instead, use the" +
                        " %s option. (%s#%s)",
                    EpoxyAttribute::class.java.simpleName,
                    EpoxyAttribute.Option.DoNotHash,
                    classElement.name,
                    fieldName
                )
            }
            if (!annotation.setter) {
                logger.logError(
                    "Don't use setter=false in an %s if you are using options. Instead, use the" +
                        " %s option. (%s#%s)",
                    EpoxyAttribute::class.java.simpleName,
                    EpoxyAttribute.Option.NoSetter,
                    classElement.name,
                    fieldName
                )
            }
        }
    }

    /**
     * Checks if the given private field has getter and setter for access to it
     */
    private fun findGetterAndSetterForPrivateField(logger: Logger) {
        classElement.getDeclaredMethods().forEach { method ->
            val methodName = method.name
            val parameters = method.parameters

            // check if it is a valid getter
            if ((
                methodName == String.format(
                        "get%s",
                        capitalizeFirstLetter(fieldName)
                    ) || methodName == String.format(
                        "is%s",
                        capitalizeFirstLetter(fieldName)
                    ) || methodName == fieldName && startsWithIs(fieldName)
                ) &&
                !method.isPrivate() &&
                !method.isStatic() &&
                parameters.isEmpty()
            ) {
                getterMethodName = methodName
            }
            // check if it is a valid setter
            if ((
                methodName == String.format(
                        "set%s",
                        capitalizeFirstLetter(fieldName)
                    ) || startsWithIs(fieldName) && methodName == String.format(
                        "set%s",
                        fieldName.substring(2, fieldName.length)
                    )
                ) &&
                !method.isPrivate() &&
                !method.isStatic() &&
                parameters.size == 1
            ) {
                setterMethodName = methodName
            }
        }
        if (getterMethodName == null || setterMethodName == null) {
            // We disable the "private" field setting so that we can still generate
            // some code that compiles in an ok manner (ie via direct field access)
            isPrivate = false
            logger
                .logError(
                    "%s annotations must not be on private fields" +
                        " without proper getter and setter methods. (class: %s, field: %s)",
                    EpoxyAttribute::class.java.simpleName,
                    classElement.name,
                    fieldName
                )
        }
    }

    /**
     * Keeps track of annotations on the attribute so that they can be used in the generated setter
     * and getter method. Setter and getter annotations are stored separately since the annotation may
     * not target both method and parameter types.
     */
    private fun buildAnnotationLists(attribute: XFieldElement, annotations: List<XAnnotation>) {
        for (annotation in annotations) {
            if (annotation.annotationValues.isNotEmpty()) {
                // Not supporting annotations with values for now
                continue
            }
            if (annotation.type.isTypeOf(EpoxyAttribute::class)) {
                // Don't include our own annotation
                continue
            }
            val annotationType = annotation.type
            // A target may exist on an annotation type to specify where the annotation can
            // be used, for example fields, methods, or parameters.
            val targetAnnotation = annotationType.typeElement?.getAnnotation(Target::class)

            // Allow all target types if no target was specified on the annotation
            val elementTypes = targetAnnotation?.value?.value ?: ElementType.values()
            val annotationSpec = annotation.toAnnotationSpec(memoizer)
            if (elementTypes.contains(ElementType.PARAMETER)) {
                setterAnnotations.add(annotationSpec)
            }
            if (elementTypes.contains(ElementType.METHOD)) {
                getterAnnotations.add(annotationSpec)
            }
        }

        // When KAPT processes kotlin sources it sees java intermediary, which has automatically
        // generated nullability annotations which we inherit. However, with
        // KSP we see the kotlin code directly so we don't get those annotations by default
        // and we lose nullability info, so we add it manually in that case.
        if (memoizer.environment.backend == XProcessingEnv.Backend.KSP && attribute.declaration.isKotlinOrigin()) {
            if (!attribute.type.typeName.isPrimitive) {

                // Look at just simple name of annotation as there are many packages providing them (eg androidx, jetbrains)
                val annotationSimpleNames = setterAnnotations.map { annotation ->
                    when (val type = annotation.type) {
                        is ClassName -> type.simpleName()
                        else -> annotation.toString().substringAfterLast(".")
                    }
                }

                if (attribute.type.nullability == XNullability.NULLABLE) {
                    if (annotationSimpleNames.none { it == "Nullable" }) {
                        setterAnnotations.add(NULLABLE_ANNOTATION_SPEC)
                        getterAnnotations.add(NULLABLE_ANNOTATION_SPEC)
                    }
                } else if (attribute.type.nullability == XNullability.NONNULL) {
                    if (annotationSimpleNames.none { it == "NotNull" || it == "NonNull" }) {
                        setterAnnotations.add(NON_NULL_ANNOTATION_SPEC)
                        getterAnnotations.add(NON_NULL_ANNOTATION_SPEC)
                    }
                }
            }
        }
    }
}
