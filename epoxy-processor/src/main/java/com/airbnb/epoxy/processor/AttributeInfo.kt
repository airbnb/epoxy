package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XType
import com.airbnb.epoxy.processor.Type.TypeEnum
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.TypeName

abstract class AttributeInfo(val memoizer: Memoizer) : Comparable<AttributeInfo> {

    lateinit var fieldName: String
        protected set

    lateinit var type: Type
        private set

    lateinit var xType: XType
        private set

    protected fun setXType(xType: XType, memoizer: Memoizer) {
        this.xType = xType
        type = memoizer.getType(xType)
    }

    lateinit var rootClass: String
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
            // Avoid type lookup as it is expensive in KSP, so just check for the functions package.
            return "kotlin.jvm.functions" in typeName.toString()
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

    /**
     * Track whether there is a setter method for this attribute on a super class so that we can
     * call through to super.
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
     * If [isGenerated] is true, this represents whether null is a valid value to set on the
     * attribute. If this is true, then the [codeToSetDefault] should be null unless a
     * different default value is explicitly set.
     *
     *
     * This is Boolean to have null represent that nullability was not explicitly set, eg for
     * primitives or legacy attributes that weren't made with nullability support in mind.
     */
    var isNullable: Boolean? = null
        protected set(value) {
            check(!isPrimitive) {
                "Primitives cannot be nullable"
            }
            field = value
        }

    val isPrimitive: Boolean
        get() = typeName.isPrimitive

    open val isRequired: Boolean
        get() = isGenerated && codeToSetDefault.isEmpty

    val typeName: TypeName get() = type.typeName

    val isViewClickListener: Boolean get() = type.typeEnum == TypeEnum.ViewClickListener

    val isViewLongClickListener: Boolean get() = type.typeEnum == TypeEnum.ViewLongClickListener

    val isViewCheckedChangeListener: Boolean get() = type.typeEnum == TypeEnum.ViewCheckedChangeListener

    val isBoolean: Boolean get() = type.typeEnum == TypeEnum.Boolean

    val isCharSequenceOrString: Boolean get() = type.typeEnum == TypeEnum.StringOrCharSequence

    val isStringList: Boolean
        get() = type.typeEnum == TypeEnum.StringList

    val isEpoxyModelList: Boolean
        get() = type.typeEnum == TypeEnum.EpoxyModelList

    val isInt: Boolean
        get() = type.typeEnum == TypeEnum.Int

    val isLong: Boolean
        get() = type.typeEnum == TypeEnum.Long

    val isStringAttributeData: Boolean
        get() = type.typeEnum == TypeEnum.StringAttributeData

    val isDouble: Boolean get() = type.typeEnum == TypeEnum.Double

    val isDrawableRes: Boolean get() = isInt && hasAnnotation("DrawableRes")

    val isRawRes: Boolean get() = isInt && hasAnnotation("RawRes")

    private fun hasAnnotation(annotationSimpleName: String): Boolean {
        return setterAnnotations
            .map { it.type }
            .filterIsInstance<ClassName>()
            .any { it.simpleName() == annotationSimpleName }
    }

    class DefaultValue {
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

    fun getterCode(): String = if (isPrivate) getterMethodName!! + "()" else fieldName

    // Special case to avoid generating recursive getter if field and its getter names are the same
    fun superGetterCode(): String =
        if (isPrivate) String.format("super.%s()", getterMethodName) else fieldName

    fun setterCode(): String =
        (if (isGenerated) "this." else "super.") +
            if (isPrivate)
                setterMethodName!! + "(\$L)"
            else
                "$fieldName = \$L"

    open fun generatedSetterName(): String = fieldName

    open fun generatedGetterName(isOverload: Boolean): String = fieldName

    override fun toString(): String {
        return (
            "Attribute {" +
                "model='" + rootClass + '\''.toString() +
                ", name='" + fieldName + '\''.toString() +
                ", type=" + typeName +
                '}'.toString()
            )
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

    override fun compareTo(other: AttributeInfo): Int {
        // sort attributes alphabetically for consistent code generation when attributes
        // are added concurrently.
        return fieldName.compareTo(other.fieldName)
    }
}
