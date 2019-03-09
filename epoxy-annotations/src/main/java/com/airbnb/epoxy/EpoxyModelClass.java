
package com.airbnb.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.annotation.LayoutRes;

/**
 * Used to annotate EpoxyModel classes in order to generate a subclass of that model with getters,
 * setters, equals, and hashcode for the annotated fields, as well as other helper methods and
 * boilerplate reduction.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface EpoxyModelClass {
  /**
   * A layout resource that should be used as the default layout for the model. If you set this you
   * don't have to implement `getDefaultLayout`; it will be generated for you.
   */
  @LayoutRes int layout() default 0;

  /**
   * If true, any layout file name that has {@link #layout()} as a prefix will be included as a
   * method on the generated model.
   * <p>
   * For example, if the layout is "R.layout.my_view" then any layouts in the form of
   * "R.layout.my_view_*" will result in a generated method like "with*Layout" that will apply that
   * other layout instead of the default.
   */
  boolean useLayoutOverloads() default false;
}
