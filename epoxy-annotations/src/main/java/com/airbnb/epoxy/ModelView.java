package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ModelView {
  @LayoutRes int defaultLayout() default 0;
  Class<?> baseModelClass() default Void.class;
  boolean saveViewState() default false;
  /**
   * True to have the generated model take up the total available span count. False to instead use a
   * span count of 1. If you need to programmatically determine your model's span size you can use
   * the spanSizeCallback method on EpoxyModel.
   */
  boolean fullSpan() default true;
}
