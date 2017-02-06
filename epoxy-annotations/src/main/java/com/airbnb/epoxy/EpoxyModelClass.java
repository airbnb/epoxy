
package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
}
