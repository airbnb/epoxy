package com.airbnb.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify a naming pattern for the databinding layouts that you want models generated for.
 * Use this instead of {@link EpoxyDataBindingLayouts} to avoid having to explicitly list every
 * databinding layout.
 * <p>
 * The layouts must not specify a custom databinding class name or package via the
 * class="com.example.CustomClassName" override in the layout xml.
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.CLASS)
public @interface EpoxyDataBindingPattern {
  /**
   * The R class used in this module (eg "com.example.app.R.class"). This is needed so Epoxy can
   * look up layout files.
   */
  Class<?> rClass();
  /**
   * A string prefix that your databinding layouts start with. Epoxy will generate a model for each
   * databinding layout whose name starts with this.
   * <p>
   * For example, if you set this prefix to "view_holder" and you have a "view_holder_header.xml"
   * databinding layout, Epoxy will generate a HeaderBindingModel_ class for that layout.
   */
  String layoutPrefix();
}
