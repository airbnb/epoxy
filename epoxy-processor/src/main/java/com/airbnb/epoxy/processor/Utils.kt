package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XExecutableParameterElement
import androidx.room.compiler.processing.XFieldElement
import androidx.room.compiler.processing.XHasModifiers
import androidx.room.compiler.processing.XMethodElement
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XRawType
import androidx.room.compiler.processing.XType
import androidx.room.compiler.processing.XTypeElement
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import java.util.regex.Pattern

internal object Utils {
    private val PATTERN_STARTS_WITH_SET =
        Pattern.compile("set[A-Z]\\w*")
    const val EPOXY_MODEL_TYPE = "com.airbnb.epoxy.EpoxyModel<?>"
    const val UNTYPED_EPOXY_MODEL_TYPE = "com.airbnb.epoxy.EpoxyModel"
    const val EPOXY_VIEW_HOLDER_TYPE = "com.airbnb.epoxy.EpoxyViewHolder"
    const val EPOXY_HOLDER_TYPE = "com.airbnb.epoxy.EpoxyHolder"
    const val ANDROID_VIEW_TYPE = "android.view.View"
    const val EPOXY_CONTROLLER_TYPE = "com.airbnb.epoxy.EpoxyController"
    const val VIEW_CLICK_LISTENER_TYPE = "android.view.View.OnClickListener"
    const val VIEW_LONG_CLICK_LISTENER_TYPE = "android.view.View.OnLongClickListener"
    const val VIEW_CHECKED_CHANGE_LISTENER_TYPE =
        "android.widget.CompoundButton.OnCheckedChangeListener"
    const val GENERATED_MODEL_INTERFACE = "com.airbnb.epoxy.GeneratedModel"
    const val MODEL_CLICK_LISTENER_TYPE = "com.airbnb.epoxy.OnModelClickListener"
    const val MODEL_LONG_CLICK_LISTENER_TYPE = "com.airbnb.epoxy.OnModelLongClickListener"
    const val MODEL_CHECKED_CHANGE_LISTENER_TYPE =
        "com.airbnb.epoxy.OnModelCheckedChangeListener"
    const val ON_BIND_MODEL_LISTENER_TYPE = "com.airbnb.epoxy.OnModelBoundListener"
    const val ON_UNBIND_MODEL_LISTENER_TYPE = "com.airbnb.epoxy.OnModelUnboundListener"
    const val WRAPPED_LISTENER_TYPE = "com.airbnb.epoxy.WrappedEpoxyModelClickListener"
    const val WRAPPED_CHECKED_LISTENER_TYPE =
        "com.airbnb.epoxy.WrappedEpoxyModelCheckedChangeListener"
    const val ON_VISIBILITY_STATE_MODEL_LISTENER_TYPE =
        "com.airbnb.epoxy.OnModelVisibilityStateChangedListener"
    const val ON_VISIBILITY_MODEL_LISTENER_TYPE =
        "com.airbnb.epoxy.OnModelVisibilityChangedListener"

    @JvmStatic
    @Throws(EpoxyProcessorException::class)
    fun throwError(msg: String?, vararg args: Any?) {
        throw EpoxyProcessorException(String.format(msg!!, *args))
    }

    @JvmStatic
    fun buildEpoxyException(
        msg: String?,
        vararg args: Any?
    ): EpoxyProcessorException {
        return EpoxyProcessorException(String.format(msg!!, *args))
    }

    fun buildEpoxyException(
        element: XElement,
        msg: String?,
        vararg args: Any?
    ): EpoxyProcessorException {
        return EpoxyProcessorException(
            message = String.format(msg!!, *args),
            element = element
        )
    }

    @JvmStatic
    fun isIterableType(element: XType, memoizer: Memoizer): Boolean {
        return element.isSubTypeOf(memoizer.iterableType)
    }

    fun isMapType(element: XType): Boolean {
        return element.rawType.toString().endsWith(".Map")
    }

    /**
     * Checks if the given field has package-private visibility
     */
    @JvmStatic
    fun isFieldPackagePrivate(element: XElement): Boolean {
        if (element !is XHasModifiers) return false

        return !element.isPrivate() && !element.isProtected() && !element.isPublic()
    }

    /**
     * @return True if the clazz (or one of its superclasses) implements the given method. Returns
     * false if the method doesn't exist anywhere in the class hierarchy or it is abstract.
     */
    fun implementsMethod(
        clazz: XTypeElement,
        method: MethodSpec,
        environment: XProcessingEnv
    ): Boolean {
        val methodOnClass = getMethodOnClass(clazz, method, environment) ?: return false
        return !methodOnClass.isAbstract()
    }

    /**
     * @return The first element matching the given method in the class's hierarchy, or null if there
     * is no match.
     */
    @JvmStatic
    fun getMethodOnClass(
        clazz: XTypeElement,
        method: MethodSpec,
        environment: XProcessingEnv,
    ): XMethodElement? {
        clazz.getDeclaredMethods()
            .firstOrNull { methodElement ->
                methodElement.name == method.name && areParamsTheSame(
                    methodElement,
                    method,
                    environment
                )
            }?.let { return it }

        val superClazz = clazz.superType?.typeElement ?: return null
        return getMethodOnClass(superClazz, method, environment)
    }

    private fun areParamsTheSame(
        method1: XMethodElement,
        method2: MethodSpec,
        environment: XProcessingEnv,
    ): Boolean {
        val params1 = method1.parameters
        val params2 = method2.parameters
        if (params1.size != params2.size) {
            return false
        }

        for (i in params1.indices) {
            val param1: XExecutableParameterElement = params1[i]
            val param2: ParameterSpec = params2[i]
            val param1Type: XRawType = param1.type.rawType

            val param2Type: XRawType = environment.requireType(param2.type).rawType

            // If a param is a type variable then we don't need an exact type match, it just needs to
            // be assignable
            if (param1.type.extendsBound() == null) {
                if (!param1Type.isAssignableFrom(param2Type)) {
                    return false
                }
            } else if (param1Type != param2Type) {
                return false
            }
        }
        return true
    }

    /**
     * Returns the type of the Epoxy model.
     *
     * Eg for "class MyModel extends EpoxyModel<TextView>" it would return TextView.
     </TextView> */
    fun getEpoxyObjectType(
        clazz: XTypeElement,
        memoizer: Memoizer
    ): XType? {
        val superTypeElement = clazz.superType?.typeElement ?: return null

        val recursiveResult = getEpoxyObjectType(superTypeElement, memoizer)
        if (recursiveResult != null &&
            // Make sure that the type isn't just the generic "T" and that it has at least
            // some type information. if it has a type element then it is a concrete type
            // or if it has some upper bound then it extends something concrete.
            (recursiveResult.typeElement != null || recursiveResult.extendsBound()?.typeElement != null)
        ) {
            // Use the type on the parent highest in the class hierarchy so we can find the original type.
            return recursiveResult
        }

        // Note, the "superTypeElement" loses the typing information, so we must use the
        // superType directly off the class.
        val superTypeArguments = clazz.superType?.typeArguments ?: emptyList()

        // If there is only one type then we use that
        superTypeArguments.singleOrNull()?.let { return it }

        for (superTypeArgument in superTypeArguments) {
            // The user might have added additional types to their class which makes it more difficult
            // to figure out the base model type. We just look for the first type that is a view or
            // view holder.
            // Also, XProcessing does not expose the type kind, so we can't directly tell if it is
            // a bounded "T" type var, or a concrete type. We check for this instead by
            // making sure a type element exists which indicates a concrete type.
            if (superTypeArgument.isSubTypeOf(memoizer.androidViewType) ||
                superTypeArgument.isSubTypeOf(memoizer.epoxyHolderType)
            ) {
                return superTypeArgument
            }
        }
        return null
    }

    @JvmOverloads
    fun validateFieldAccessibleViaGeneratedCode(
        fieldElement: XElement,
        annotationClass: Class<*>,
        logger: Logger,
        // KSP sees the backing field, not the property, which is private, and there isn't an
        // easy way to lookup the corresponding property to check its visibility, so we just
        // skip that for KSP since this is a legacy processor anyway.
        skipPrivateFieldCheck: Boolean = fieldElement.isKsp
    ): Boolean {
        if (fieldElement !is XHasModifiers) return false
        val enclosingElement = fieldElement.enclosingTypeElement!!

        if (fieldElement !is XFieldElement) {
            logger.logError(
                fieldElement,
                "%s annotation must be on field. (class: %s, element: %s)",
                annotationClass.simpleName,
                enclosingElement.expectName,
                fieldElement.expectName
            )
            return false
        }

        if (fieldElement.isPrivate() && !skipPrivateFieldCheck) {
            logger.logError(
                fieldElement,
                "%s annotations must not be on private fields. (class: %s, field: %s)",
                annotationClass.simpleName,
                enclosingElement.expectName,
                fieldElement.expectName
            )
            return false
        }

        if (fieldElement.isStatic()) {
            logger.logError(
                fieldElement,
                "%s annotations must not be on static fields. (class: %s, field: %s)",
                annotationClass.simpleName,
                enclosingElement.expectName,
                fieldElement.expectName
            )
            return false
        }

        // Nested classes must be static
        if (enclosingElement.enclosingTypeElement != null && !enclosingElement.isStatic()) {
            logger.logError(
                fieldElement,
                "Nested classes with %s annotations must be static. (class: %s, field: %s)",
                annotationClass.simpleName,
                enclosingElement.expectName,
                fieldElement.expectName
            )
            return false
        }

        // Verify containing type.
        if (!enclosingElement.isClass()) {
            logger.logError(
                fieldElement,
                "%s annotations may only be contained in classes. (class: %s, field: %s)",
                annotationClass.simpleName,
                enclosingElement.expectName, fieldElement.expectName
            )
            return false
        }

        // Verify containing class visibility is not private.
        if (enclosingElement.isPrivate()) {
            logger.logError(
                fieldElement,
                "%s annotations may not be contained in private classes. (class: %s, field: %s)",
                annotationClass.simpleName,
                enclosingElement.expectName, fieldElement.expectName
            )
            return false
        }

        return true
    }

    @JvmStatic
    fun capitalizeFirstLetter(original: String?): String? {
        return if (original == null || original.isEmpty()) {
            original
        } else original.substring(0, 1).toUpperCase() + original.substring(1)
    }

    @JvmStatic
    fun startsWithIs(original: String): Boolean {
        return original.startsWith("is") && original.length > 2 && Character.isUpperCase(original[2])
    }

    fun isSetterMethod(element: XElement): Boolean {
        val method = element as? XMethodElement ?: return false
        return PATTERN_STARTS_WITH_SET.matcher(method.name).matches() &&
            method.parameters.size == 1
    }

    fun removeSetPrefix(string: String): String {
        return if (!PATTERN_STARTS_WITH_SET.matcher(string).matches()) {
            string
        } else string[3].toString()
            .toLowerCase() + string.substring(4)
    }

    fun toSnakeCase(s: String): String {
        return s.replace("([^_A-Z])([A-Z])".toRegex(), "$1_$2").toLowerCase()
    }

    fun getDefaultValue(attributeType: TypeName): String {
        return when {
            attributeType === TypeName.BOOLEAN -> "false"
            attributeType === TypeName.INT -> "0"
            attributeType === TypeName.BYTE -> "(byte) 0"
            attributeType === TypeName.CHAR -> "(char) 0"
            attributeType === TypeName.SHORT -> "(short) 0"
            attributeType === TypeName.LONG -> "0L"
            attributeType === TypeName.FLOAT -> "0.0f"
            attributeType === TypeName.DOUBLE -> "0.0d"
            else -> "null"
        }
    }
}
