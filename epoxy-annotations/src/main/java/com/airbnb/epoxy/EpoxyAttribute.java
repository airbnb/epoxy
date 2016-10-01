
package com.airbnb.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate fields on EpoxyModel classes in order to generate a
 * subclass of that model with getters, setters, equals, and hashcode for the annotated fields.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface EpoxyAttribute {
  /**
   * Whether or not to include this attribute in equals and hashCode calculations.
   *
   * It may be useful to disable this for objects that get recreated without the underlying data
   * changing such as a click listener that gets created inline in every bind call.
   */
  boolean hash() default true;

  /**
   * Whether or not to generate setter for this attribute.
   *
   * It may be useful to disable this for attribute which can be immutable and doesn't require
   * setter.
   */
  boolean setter() default true;
}
