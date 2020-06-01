package com.airbnb.epoxy.processor

import com.squareup.javapoet.ParameterSpec
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier

data class MethodInfo(
    val name: String?,
    val modifiers: Set<Modifier>,
    val params: List<ParameterSpec>,
    val varargs: Boolean,
    val isEpoxyAttribute: Boolean,
    val methodElement: ExecutableElement
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MethodInfo

        if (name != other.name) return false
        if (params != other.params) return false
        if (varargs != other.varargs) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + params.hashCode()
        result = 31 * result + varargs.hashCode()
        return result
    }
}
