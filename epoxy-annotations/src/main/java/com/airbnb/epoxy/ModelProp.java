package com.airbnb.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used in conjunction with {@link ModelView} to automatically generate EpoxyModels from custom
 * views - https://github.com/airbnb/epoxy/wiki/Generating-Models-from-View-Annotations
 * <p>
 * This annotation should be used on setter methods within a custom view class. Setters annotated
 * with this will have a corresponding field on the generated model.
 * <p>
 * Alternatively, if your setter has no side effects, you can use this annotation on a field to have
 * Epoxy set that field directly and avoid the boiler plate of a setter.
 * <p>
 * For convenience you can use {@link TextProp} instead for props representing text.
 * <p>
 * Similarly you can use {@link CallbackProp} for props representing listeners or callbacks.
 * <p>
 * Alternatively, the {@link #options()} parameter can be used to configure a prop.
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface ModelProp {

  enum Option {
    /**
     * By default every prop's hashCode and equals method is called when determining the
     * model's state. This option can be used to exclude an prop's hashCode/equals from
     * contributing to the state.
     * <p>
     * This is useful for props that may change without actually changing the model's state. A
     * common case is an anonymous click listener that gets recreated with every bind call.
     * <p>
     * When this is used, the prop will affect the model state solely based on whether it is
     * null or non null.
     * <p>
     * A good rule of thumb for whether to use this on an prop is, "If this is the only
     * prop that changed do I still need to rebind and update the view?" If the answer if no
     * then you can use this to prevent the rebind.
     */
    DoNotHash,
    /**
     * This is meant to be used in conjunction with {@link PackageEpoxyConfig#requireHashCode()}.
     * When that is enabled every prop must implement hashCode/equals. However, there are some
     * valid cases where the prop type does not implement hashCode/equals, but it should still
     * be hashed at runtime and contribute to the model's state. Use this option on an prop in
     * that case to tell the processor to let it pass the hashCode/equals validation.
     * <p>
     * An example case is AutoValue classes, where the generated class correctly implements
     * hashCode/equals at runtime.
     * <p>
     * If you use this it is your responsibility to ensure that the object assigned to the prop
     * at runtime correctly implements hashCode/equals. If you don't want the prop to
     * contribute to model state you should use {@link Option#DoNotHash} instead.
     */
    IgnoreRequireHashCode,
    /**
     * Setters with a type of {@link CharSequence} can add this option to have {@link
     * androidx.annotation.StringRes} and {@link androidx.annotation.PluralsRes}
     * overload methods generated on the model, so users can set the string via a resource.
     */
    GenerateStringOverloads,
    /**
     * Setters with a param annotated with @Nullable can use this to have null set when the view is
     * recycled.
     */
    NullOnRecycle
  }

  /** Specify any {@link Option} values that should be used when generating the model class. */
  Option[] options() default {};

  /**
   * The same as {@link #options()}, but this allows the shortcut of setting an option eg
   * "@ModelProp(DoNotHash)".
   */
  Option[] value() default {};

  /**
   * The name of the constant field that should be used as the default value for this prop. The
   * default value will be used if the prop value isn't set on the model.
   * <p>
   * For example, you would define a constant in your view class like <code>static final int
   * DEFAULT_NUM_LINES = 3</code>, and then set this parameter to "DEFAULT_NUM_LINES" so that the
   * annotation processor knows what constant to reference.
   * <p>
   * The name of the constant must be used instead of referencing the constant directly since
   * objects are not valid annotation parameters.
   */
  String defaultValue() default "";

  /**
   * Specify an optional group name. Multiple props with the same group name will only allow one of
   * the props to be set on the view.
   * <p>
   * https://github.com/airbnb/epoxy/wiki/Generating-Models-from-View-Annotations#prop-groups
   */
  String group() default "";
}
