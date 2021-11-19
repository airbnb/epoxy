package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XArrayType
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XType
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.isArray
import androidx.room.compiler.processing.isEnum
import androidx.room.compiler.processing.isEnumEntry
import com.airbnb.epoxy.processor.Utils.getMethodOnClass
import com.airbnb.epoxy.processor.Utils.isIterableType
import com.airbnb.epoxy.processor.Utils.isMapType
import com.airbnb.epoxy.processor.Utils.throwError
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName

/** Validates that an attribute implements hashCode and equals.  */
internal class HashCodeValidator(
    private val environment: XProcessingEnv,
    private val memoizer: Memoizer,
    val logger: Logger,
) {

    fun implementsHashCodeAndEquals(mirror: XType): Boolean {
        return try {
            validateImplementsHashCode(mirror)
            true
        } catch (e: EpoxyProcessorException) {
            false
        }
    }

    @Throws(EpoxyProcessorException::class)
    fun validate(attribute: AttributeInfo) {
        try {
            validateImplementsHashCode(attribute.xType)
        } catch (e: EpoxyProcessorException) {
            // Append information about the attribute and class to the existing exception
            logger.logError(
                e.message +
                    " (%s) Epoxy requires every model attribute to implement equals and hashCode " +
                    "so that changes in the model " +
                    "can be tracked. If you want the attribute to be excluded, use " +
                    "the option 'DoNotHash'. If you want to ignore this warning use " +
                    "the option 'IgnoreRequireHashCode'",
                attribute
            )
        }
    }

    @Throws(EpoxyProcessorException::class)
    private fun validateImplementsHashCode(xType: XType) {
        if (xType.isError()) {
            // The class type cannot be resolved. This may be because it is a generated epoxy model and
            // the class hasn't been built yet.
            // We just assume that the class will implement hashCode at runtime.
            return
        }
        if (xType.typeName.isPrimitive || xType.typeName.isBoxedPrimitive) {
            return
        }
        if (xType.isArray()) {
            validateArrayType(xType)
            return
        }

        val xTypeElement = xType.typeElement ?: return

        if (xTypeElement.isDataClass() || xTypeElement.isEnum() || xTypeElement.isEnumEntry() || xTypeElement.isValueClass()) {
            return
        }

        if (isMapType(xType)) {
            // as part of ksp conversion we need to add this to maintain legacy behavior because
            // java Maps implement equals/hashcode so they are automatically approved, even
            // though we never verified the key/value type implements it. Not adding it
            // now to avoid breaking existing code.
            return
        }

        if (isIterableType(xType, memoizer)) {
            validateIterableType(xType)
            return
        }
        if (isAutoValueType(xTypeElement)) {
            return
        }
        if (isWhiteListedType(xTypeElement)) {
            return
        }
        if (!hasHashCodeInClassHierarchy(xTypeElement)) {
            throwError("Attribute does not implement hashCode")
        }
        if (!hasEqualsInClassHierarchy(xTypeElement)) {
            throwError("Attribute does not implement equals")
        }
    }

    private fun hasHashCodeInClassHierarchy(clazz: XTypeElement): Boolean {
        return hasFunctionInClassHierarchy(clazz, HASH_CODE_METHOD)
    }

    private fun hasEqualsInClassHierarchy(clazz: XTypeElement): Boolean {
        return hasFunctionInClassHierarchy(clazz, EQUALS_METHOD)
    }

    private fun hasFunctionInClassHierarchy(clazz: XTypeElement, function: MethodSpec): Boolean {
        val methodOnClass = getMethodOnClass(clazz, function, environment)
            ?: return false

        val implementingClass = methodOnClass.enclosingElement as? XTypeElement
        return implementingClass?.name != "Object" && implementingClass?.type?.isObjectOrAny() != true

        // We don't care if the method is abstract or not, as long as it exists and it isn't the Object
        // implementation then the runtime value will implement it to some degree (hopefully
        // correctly :P)
    }

    @Throws(EpoxyProcessorException::class)
    private fun validateArrayType(mirror: XArrayType) {
        // Check that the type of the array implements hashCode
        val arrayType = mirror.componentType
        try {
            validateImplementsHashCode(arrayType)
        } catch (e: EpoxyProcessorException) {
            throwError(
                "Type in array does not implement hashCode. Type: %s",
                arrayType.toString()
            )
        }
    }

    @Throws(EpoxyProcessorException::class)
    private fun validateIterableType(type: XType) {
        for (typeParameter in type.typeArguments) {
            // check that the type implements hashCode
            try {
                validateImplementsHashCode(typeParameter)
            } catch (e: EpoxyProcessorException) {
                throwError(
                    "Type in Iterable does not implement hashCode. Type: %s",
                    typeParameter.toString()
                )
            }
        }

        // Assume that the iterable class implements hashCode and just return
    }

    private fun isWhiteListedType(element: XTypeElement): Boolean {
        return element.isSubTypeOf(memoizer.charSequenceType)
    }

    /**
     * Returns true if this class is expected to be implemented via a generated autovalue class,
     * which implies it will have equals/hashcode at runtime.
     */
    private fun isAutoValueType(element: XTypeElement): Boolean {
        // For migrating away from autovalue and copying autovalue sources to version control (and therefore
        // removing annotations and compile time generation) the annotation lookup no longer works.
        // Instead, assume that if a type is abstract then it has a runtime implementation the properly
        // implements equals/hashcode.
        if (element.isAbstract() && !element.isInterface()) return true

        // Only works for classes in the module since AutoValue has a retention of Source so it is
        // discarded after compilation.
        for (xAnnotation in element.getAllAnnotations()) {
            // Avoid type resolution as simple name should be enough
            val isAutoValue = xAnnotation.name == "AutoValue"
            if (isAutoValue) {
                return true
            }
        }
        return false
    }

    companion object {
        private val HASH_CODE_METHOD = MethodSpec.methodBuilder("hashCode")
            .returns(TypeName.INT)
            .build()
        private val EQUALS_METHOD = MethodSpec.methodBuilder("equals")
            .addParameter(TypeName.OBJECT, "obj")
            .returns(TypeName.BOOLEAN)
            .build()
    }
}
