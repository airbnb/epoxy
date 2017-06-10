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
@Target(ElementType.PACKAGE)
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
}
