package com.airbnb.epoxy;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Used to register an onVisibilityChanged callback with a generated model. */
public interface OnModelVisibilityStateChangedListener<T extends EpoxyModel<V>, V> {

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({VISIBLE, INVISIBLE, FOCUSED_VISIBLE, UNFOCUSED_VISIBLE, FULL_IMPRESSION_VISIBLE})
  @interface VisibilityState {
  }

  /**
   * Event triggered when a Component enters the Visible Range. This happens when at least a pixel
   * of the Component is visible.
   */
  int VISIBLE = 0;

  /**
   * Event triggered when a Component becomes invisible. This is the same with exiting the Visible
   * Range, the Focused Range and the Full Impression Range. All the code that needs to be executed
   * when a component leaves any of these ranges should be written in the handler for this event.
   */
  int INVISIBLE = 1;

  /**
   * Event triggered when a Component enters the Focused Range. This happens when either the
   * Component occupies at least half of the viewport or, if the Component is smaller than half of
   * the viewport, when the it is fully visible.
   */
  int FOCUSED_VISIBLE = 2;

  /**
   * Event triggered when a Component exits the Focused Range. The Focused Range is defined as at
   * least half of the viewport or, if the Component is smaller than half of the viewport, when the
   * it is fully visible.
   */
  int UNFOCUSED_VISIBLE = 3;

  /**
   * Event triggered when a Component enters the Full Impression Range. This happens, for instance
   * in the case of a vertical RecyclerView, when both the top and bottom edges of the component
   * become visible.
   */
  int FULL_IMPRESSION_VISIBLE = 4;

  /**
   * This will be called once the visibility changed.
   * <p>
   * @param model           The model being bound
   * @param view            The view that is being bound to the model
   * @param visibilityState The new visibility
   */
  void onVisibilityStateChanged(T model, V view, @VisibilityState int visibilityState);
}
