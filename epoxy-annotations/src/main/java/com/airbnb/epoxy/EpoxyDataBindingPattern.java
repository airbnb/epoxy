package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify a list of databinding layout resources that you want EpoxyModels generated for.
 * The models will be generated in the same package as this annotation. Every layout must be a valid
 * databinding layout. The name of the generated model will be based on the layout resource name.
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
   * A default layout pattern to be used for specifying layouts for generated models. If this is set
   * then a layout can be omitted from a view's {@link com.airbnb.epoxy.ModelView} annotation.
   * <p>
   * The "%s" placeholder represents the view's name in snack case. For example, the default value
   * will use a layout resource of "R.layout.my_view" for the MyView class. If the layout name is
   * changed to "view_holder_%s" then the layout used would be "R.layout.view_holder_my_view".
   */
  String layoutPrefix();
}