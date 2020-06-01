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
)
