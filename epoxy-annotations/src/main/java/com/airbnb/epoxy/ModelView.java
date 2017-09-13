package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation on custom view classes to automatically generate an EpoxyModel for that view. Used
 * in conjunction with {@link ModelProp}
 * <p>
 * See https://github.com/airbnb/epoxy/wiki/Generating-Models-from-View-Annotations
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ModelView {

  enum Size {
    NONE,
    WRAP_WIDTH_WRAP_HEIGHT,
    WRAP_WIDTH_MATCH_HEIGHT,
    MATCH_WIDTH_WRAP_HEIGHT,
    MATCH_WIDTH_MATCH_HEIGHT
  }

  Size autoLayout() default Size.NONE;

  /**
   * The layout file to use in the generated model to inflate the view. This is required unless a
   * default pattern is set via {@link PackageModelViewConfig}.
   * <p>
   * Overrides any default set in {@link PackageModelViewConfig}
   */
  @LayoutRes int defaultLayout() default 0;
  /**
   * An optional EpoxyModel subclass to use as the base class of the generated view. A default can
   * also be set with {@link PackageModelViewConfig}
   * <p>
   * * Overrides any default set in {@link PackageModelViewConfig}
   */
  Class<?> baseModelClass() default Void.class;
  /**
   * Whether the model should save view state when unbound.
   * <p>
   * see: EpoxyModel#shouldSaveViewState
   */
  boolean saveViewState() default false;
  /**
   * True to have the generated model take up the total available span count. False to instead use a
   * span count of 1. If you need to programmatically determine your model's span size you can use
   * the spanSizeCallback method on EpoxyModel.
   */
  boolean fullSpan() default true;
}
