package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XMethodElement
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import javax.lang.model.element.Modifier

data class MethodInfo(
    val name: String?,
    val modifiers: Set<Modifier>,
    val params: List<ParameterSpec>,
    val varargs: Boolean,
    val isEpoxyAttribute: Boolean,
    val methodElement: XMethodElement
) {
    private val paramTypes: List<TypeName> get() = params.map { it.type }

    // Use an equals/hashcode that matches method signature, but doesn't count non signature
    // changes such as annotations, return type, or param names.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MethodInfo

        if (name != other.name) return false
        if (paramTypes != other.paramTypes) return false
        if (varargs != other.varargs) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + paramTypes.hashCode()
        result = 31 * result + varargs.hashCode()
        return result
    }
}
