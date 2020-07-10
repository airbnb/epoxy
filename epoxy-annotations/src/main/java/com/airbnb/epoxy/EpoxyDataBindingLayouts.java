package com.airbnb.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.annotation.LayoutRes;

/**
 * Used to specify a list of databinding layout resources that you want EpoxyModels generated for.
 * The models will be generated in the same package as this annotation. Every layout must be a valid
 * databinding layout. The name of the generated model will be based on the layout resource name.
 * <p>
 * The layouts must not specify a custom databinding class name or package via the
 * class="com.example.CustomClassName" override in the layout xml.
 * <p>
 * Alternatively you can use {@link EpoxyDataBindingPattern} to avoid explicitly declaring each
 * layout.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface EpoxyDataBindingLayouts {
  /** A list of databinding layout resources that should have EpoxyModel's generated for them. */
  @LayoutRes int[] value();

  /**
     * If true, any variable whose type does not implement equals and hashcode will have the
   * {@link EpoxyAttribute.Option#DoNotHash} behavior applied to them automatically.
   * <p>
   * This is generally helpful for listeners - other variables should almost always implement
   * equals and hashcode.
   * <p>
   * For details on the nuances of this, see https://github.com/airbnb/epoxy/wiki/DoNotHash
   */
  boolean enableDoNotHash() default true;
}
