
package com.airbnb.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate fields on EpoxyModel classes in order to generate a subclass of that model with
 * getters, setters, equals, and hashcode for the annotated fields.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface EpoxyAttribute {
  /**
   * Options that can be included on the attribute to affect how the model's generated class is
   * created.
   */
  enum Option {
    /**
     * A getter is generated for this attribute by default. Add this option to prevent a getter from
     * being generated.
     */
    NoGetter,
    /**
     * A setter is generated for this attribute by default. Add this option to prevent a setter from
     * being generated.
     */
    NoSetter,
    /**
     * By default every attribute's hashCode and equals method is called when determining the
     * model's state. This option can be used to exclude an attribute's hashCode/equals from
     * contributing to the state.
     * <p>
     * This is useful for objects that may change without actually changing the model's state. A
     * common case is an anonymous click listener that gets recreated with every bind call.
     * <p>
     * When this is used, the attribute will affect the model state solely based on whether it is
     * null or non null.
     * <p>
     * A good rule of thumb for whether to use this on an attribute is, "If this is the only
     * attribute that changed do I still need to rebind and update the view?" If the answer if no
     * then you can use this to prevent the rebind.
     */
    DoNotHash,
    /**
     * This is meant to be used in conjunction with {@link PackageEpoxyConfig#requireHashCode()}.
     * When that is enabled every attribute must implement hashCode/equals. However, there are some
     * valid cases where the attribute type does not implement hashCode/equals, but it should still
     * be hashed at runtime and contribute to the model's state. Use this option on an attribute in
     * that case to tell the processor to let it pass the hashCode/equals validation.
     * <p>
     * An example case is AutoValue classes, where the generated class correctly implements
     * hashCode/equals at runtime.
     * <p>
     * If you use this it is your responsibility to ensure that the object assigned to the attribute
     * at runtime correctly implements hashCode/equals. If you don't want the attribute to
     * contribute to model state you should use {@link Option#DoNotHash} instead.
     */
    IgnoreRequireHashCode,
    /**
     * This attribute is used in {@link Object#toString()} implementation by default.
     * Add this option to prevent this attribute being used in {@link Object#toString()}.
     */
    DoNotUseInToString
  }

  /** Specify any {@link Option} values that should be used when generating the model class. */
  Option[] value() default {};

  /**
   * Whether or not to include this attribute in equals and hashCode calculations.
   * <p>
   * It may be useful to disable this for objects that get recreated without the underlying data
   * changing such as a click listener that gets created inline in every bind call.
   *
   * @deprecated Use {@link Option#DoNotHash} instead.
   */
  @Deprecated
  boolean hash() default true;

  /**
   * Whether or not to generate setter for this attribute.
   * <p>
   * It may be useful to disable this for attribute which can be immutable and doesn't require
   * setter.
   *
   * @deprecated Use {@link Option#NoSetter} instead.
   */
  @Deprecated
  boolean setter() default true;
}
