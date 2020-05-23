package com.airbnb.epoxy.processor

import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.ParameterSpec

/**
 * Allows an attribute to have multiple parameters in the model setter method. Those params are then
 * combined into a single object to be set on the attribute.
 *
 *
 * This is useful for things like
 * combining a StringRes and format arguments into a single string.
 */
interface MultiParamAttribute {
    val params: List<ParameterSpec>
    /**
     * This code should combine the params into a single object which can then be set on the
     * attribute.
     */
    val valueToSetOnAttribute: CodeBlock

    fun varargs(): Boolean
}
