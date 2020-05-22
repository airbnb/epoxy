package com.airbnb.epoxy.processor

import com.airbnb.epoxy.processor.ClassNames.EPOXY_STRING_ATTRIBUTE_DATA_REFLECTION_NAME
import com.squareup.javapoet.TypeName
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror

/**
 * This helps to memoize the look up of a type's information.
 */
class Type(val typeMirror: TypeMirror, val typeMirrorAsString: String) {
    val typeName: TypeName by lazy { typeMirror.typeNameSynchronized() }
    val typeEnum: TypeEnum by lazy { TypeEnum.from(typeMirror, typeMirrorAsString) }

    enum class TypeEnum {
        StringOrCharSequence,
        Boolean,
        Int,
        Long,
        Double,
        ViewClickListener,
        ViewLongClickListener,
        ViewCheckedChangeListener,
        StringList,
        EpoxyModelList,
        StringAttributeData,
        Unknown;

        companion object {
            fun from(typeMirror: TypeMirror, typeMirrorAsString: String): TypeEnum {
                typeMirror.ensureLoaded()

                val kindMatch = when (typeMirror.kind) {
                    TypeKind.BOOLEAN -> Boolean
                    TypeKind.INT -> Int
                    TypeKind.LONG -> Long
                    TypeKind.DOUBLE -> Double
                    else -> null
                }

                return kindMatch ?: when (typeMirrorAsString) {
                    "java.lang.CharSequence", "java.lang.String" -> StringOrCharSequence
                    "java.lang.Boolean" -> Boolean
                    "java.lang.Integer" -> Int
                    "java.lang.Long" -> Long
                    "java.lang.Double" -> Double
                    EPOXY_STRING_ATTRIBUTE_DATA_REFLECTION_NAME -> StringAttributeData
                    Utils.VIEW_CLICK_LISTENER_TYPE -> ViewClickListener
                    Utils.VIEW_LONG_CLICK_LISTENER_TYPE -> ViewLongClickListener
                    Utils.VIEW_CHECKED_CHANGE_LISTENER_TYPE -> ViewCheckedChangeListener
                    stringListType -> StringList
                    epoxyModelListType -> EpoxyModelList
                    else -> Unknown
                }
            }

            private val epoxyModelListType = "? extends ${Utils.EPOXY_MODEL_TYPE}".asListType()
            private val stringListType = String::class.java.canonicalName.asListType()

            private fun String.asListType() = "java.util.List<$this>"
        }
    }
}
