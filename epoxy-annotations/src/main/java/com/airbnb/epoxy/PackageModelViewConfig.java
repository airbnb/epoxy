package com.airbnb.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Settings that apply to all views annotated with {@link com.airbnb.epoxy.ModelView} in this
 * package. Also applies to subpackages, unless other package config values are set in those sub
 * packages.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface PackageModelViewConfig {
  /**
   * The R class used in this module (eg "com.example.app.R.class"). This is needed so Epoxy can
   * look up layout files.
   */
  Class<?> rClass();
  /**
   * A default layout pattern to be used for specifying layouts for generated models. If this is set
   * then a layout can be omitted from a view's {@link com.airbnb.epoxy.ModelView} annotation.
   * <p>
   * The "%s" placeholder represents the view's name in snack case. For example, the default value
   * will use a layout resource of "R.layout.my_view" for the MyView class. If the layout name is
   * changed to "view_holder_%s" then the layout used would be "R.layout.view_holder_my_view".
   */
  String defaultLayoutPattern() default "%s";

  /** An optional EpoxyModel subclass that generated models should extend. */
  Class<?> defaultBaseModelClass() default Void.class;

  /**
   * If true, any layout file name that has a view's default layout as a prefix will be included as
   * a method on the generated model for that view.
   * <p>
   * For example, if the layout is "R.layout.my_view" then any layouts in the form of
   * "R.layout.my_view_*" will result in a generated method like "with*Layout" that will apply that
   * other layout instead of the default.
   */
  boolean useLayoutOverloads() default false;

  /**
   * Suffix, which will be appended to generated model's names. "Model_" is a default value.
   */
  String generatedModelSuffix() default "Model_";

  /**
   * Controls whether "builder" setter functions that returns the model type will be duplicated
   * from super model classes with the function return type updated to use the generated model name.
   * This helps make all setters (such as id(...) ) return the same generated model so they can be
   * chained in a builder pattern. This is mainly intended for Java usage and is generally
   * unnecessary when using models in kotlin, especially if the generated kotlin model
   * build extension functions are used. Disabling this can greatly reduce the number of
   * methods generated on models.
   *
   * Default is false. This may also be set project wide with an annotation processor option.
   */
  Option disableGenerateBuilderOverloads() default Option.Default;

  /**
   * Controls whether getter functions (that return the value of each attribute) are generated
   * on models.
   *
   * Disabling this can greatly reduce the number of methods generated on models.
   *
   * Default is false. This may also be set project wide with an annotation processor option.
   */
  Option disableGenerateGetters() default Option.Default;

  /**
   * Controls whether the "reset" function (that clears all attribute values) are generated
   * on models. This function is generally legacy and is not recommended to be used with the modern
   * immutable model approach of EpoxyControllers.
   *
   * Disabling this reduces the amount of generated code.
   *
   * Default is false. This may also be set project wide with an annotation processor option.
   */
  Option disableGenerateReset() default Option.Default;

  /**
   * Enable or Disable an option, or inherit the default.
   */
  enum Option {
    Default,
    Enabled,
    Disabled
  }
}
