package com.airbnb.epoxy;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;

import java.util.List;

/**
 * Allows an attribute to have multiple parameters in the model setter method. Those params are then
 * combined into a single object to be set on the attribute.
 * <p>
 * This is useful for things like
 * combining a StringRes and format arguments into a single string.
 */
interface MultiParamAttribute {
  List<ParameterSpec> getParams();
  /**
   * This code should combine the params into a single object which can then be set on the
   * attribute.
   */
  CodeBlock getValueToSetOnAttribute();

  boolean varargs();
}
