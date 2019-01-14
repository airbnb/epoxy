package com.airbnb.epoxy

import com.airbnb.epoxy.GeneratedModelInfo.AttributeGroup
import com.airbnb.epoxy.Utils.EPOXY_MODEL_TYPE
import com.airbnb.epoxy.Utils.isSubtypeOfType
import com.airbnb.epoxy.Utils.isViewClickListenerType
import com.airbnb.epoxy.Utils.isViewLongClickListenerType
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.TypeName
import javax.lang.model.type.TypeMirror

internal abstract class AttributeInfo {

    lateinit var fieldName: String
        protected set
    lateinit var typeMirror: TypeMirror
        protected set
    lateinit var modelName: String
        protected set
    var packageName: String? = null
        protected set
    var useInHash: Boolean = false
        protected set
    var ignoreRequireHashCode: Boolean = false
        protected set
    var doNotUseInToString: Boolean = false
        protected set
        get() {
            if (field) {
                return true
            }

            // Do not include Kotlin lambdas in toString because there is a bug where they sometimes
            // crash.
            // see https://github.com/airbnb/epoxy/issues/455
            return isSubtypeOfType(typeMirror, "kotlin.Function<?>")
        }

    var generateSetter: Boolean = false
        protected set
    var setterAnnotations: MutableList<AnnotationSpec> = mutableListOf()
        protected set
    var generateGetter: Boolean = false
        protected set
    var getterAnnotations: MutableList<AnnotationSpec> = mutableListOf()
        protected set
    var hasFinalModifier: Boolean = false
        protected set
    var isPackagePrivate: Boolean = false
        protected set
    var javaDoc: CodeBlock? = null
        protected set

    /** If this attribute is in an attribute group this is the name of the group.  */
    var groupKey: String? = null
    var attributeGroup: AttributeGroup? = null

    /**
     * Track whether there is a setter method for this attribute on a super class so that we can call
     * through to super.
     */
    var hasSuperSetter: Boolean = false
    // for private fields (Kotlin case)
    var isPrivate: Boolean = false
    protected var getterMethodName: String? = null

    protected var setterMethodName: String? = null

    /**
     * True if this attribute is completely generated as a field on the generated model. False if it
     * exists as a user defined attribute in a model super class.
     */
    var isGenerated: Boolean = false
        protected set
    /** If [.isGenerated] is true, a default value for the field can be set here.  */
    val codeToSetDefault = DefaultValue()

    /**
     * If [.isGenerated] is true, this represents whether null is a valid value to set on the
     * attribute. If this is true, then the [.codeToSetDefault] should be null unless a
     * different default value is explicitly set.
     *
     *
     * This is Boolean to have null represent that nullability was not explicitly set, eg for
     * primitives or legacy attributes that weren't made with nullability support in mind.
     */
    var isNullable: Boolean? = null
        protected set

    val isPrimitive: Boolean
        get() = typeName.isPrimitive

    open val isRequired: Boolean
        get() = isGenerated && codeToSetDefault.isEmpty

    val typeName: TypeName
        get() = TypeName.get(typeMirror)

    val isViewClickListener: Boolean
        get() = isViewClickListenerType(typeMirror)

    val isViewLongClickListener: Boolean
        get() = isViewLongClickListenerType(typeMirror)

    val isBoolean: Boolean
        get() = Utils.isBooleanType(typeMirror)

    val isCharSequenceOrString: Boolean
        get() = Utils.isCharSequenceOrStringType(typeMirror)

    val isStringList: Boolean
        get() = Utils.isListOfType(typeMirror, String::class.java.canonicalName)

    val isEpoxyModelList: Boolean
        get() = Utils.isListOfType(typeMirror, "? extends $EPOXY_MODEL_TYPE")

    val isDouble: Boolean
        get() = Utils.isDoubleType(typeMirror)

    val isDrawableRes: Boolean
        get() = isInt && hasAnnotation("DrawableRes")

    val isRawRes: Boolean
        get() = isInt && hasAnnotation("RawRes")

    private fun hasAnnotation(annotationSimpleName: String): Boolean {
        return setterAnnotations
            .map { it.type }
            .filterIsInstance<ClassName>()
            .any { it.simpleName() == annotationSimpleName }
    }

    val isInt: Boolean
        get() = Utils.isIntType(typeMirror)

    val isLong: Boolean
        get() = Utils.isLongType(typeMirror)

    val isStringAttributeData: Boolean
        get() = Utils.isType(typeMirror, ClassNames.EPOXY_STRING_ATTRIBUTE_DATA)

    val isOverload: Boolean
        get() = attributeGroup != null && attributeGroup!!.attributes.size > 1

    internal class DefaultValue {
        /** An explicitly defined default via the default param in the prop annotation.  */
        var explicit: CodeBlock? = null
        /**
         * An implicitly assumed default, either via an @Nullable annotation or a primitive's default
         * value. This is overridden if an explicit value is set.
         */
        var implicit: CodeBlock? = null

        val isPresent: Boolean
            get() = explicit != null || implicit != null

        val isEmpty: Boolean
            get() = !isPresent

        fun value(): CodeBlock? = explicit ?: implicit
    }

    protected fun setJavaDocString(docComment: String?) {
        javaDoc = docComment?.trim()
            ?.let { if (it.isNotEmpty()) CodeBlock.of(it) else null }
    }

    fun isNullable(): Boolean {
        if (!hasSetNullability()) {
            throw IllegalStateException("Nullability has not been set")
        }

        return isNullable == true
    }

    fun hasSetNullability(): Boolean = isNullable != null

    fun setNullable(nullable: Boolean) {
        if (isPrimitive) {
            throw IllegalStateException("Primitives cannot be nullable")
        }

        isNullable = nullable
    }

    fun getterCode(): String = if (isPrivate) getterMethodName!! + "()" else fieldName

    // Special case to avoid generating recursive getter if field and its getter names are the same
    fun superGetterCode(): String =
        if (isPrivate) String.format("super.%s()", getterMethodName) else fieldName

    fun setterCode(): String =
        (if (isGenerated) "this." else "super.") + if (isPrivate) setterMethodName!! + "(\$L)" else "$fieldName = \$L"

    open fun generatedSetterName(): String = fieldName

    open fun generatedGetterName(): String = fieldName

    override fun toString(): String {
        return ("Attribute {" +
                "model='" + modelName + '\''.toString() +
                ", name='" + fieldName + '\''.toString() +
                ", type=" + typeName +
                '}'.toString())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is AttributeInfo) {
            return false
        }

        val that = other as AttributeInfo?

        return if (fieldName != that!!.fieldName) {
            false
        } else typeName == that.typeName
    }

    override fun hashCode(): Int {
        var result = fieldName.hashCode()
        result = 31 * result + typeName.hashCode()
        return result
    }
}
