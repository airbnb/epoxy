package com.airbnb.epoxy;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

public final class VisibilityState {

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({VISIBLE,
           INVISIBLE,
           FOCUSED_VISIBLE,
           UNFOCUSED_VISIBLE,
           FULL_IMPRESSION_VISIBLE,
           PARTIAL_IMPRESSION_VISIBLE,
           PARTIAL_IMPRESSION_INVISIBLE})
  public @interface Visibility {
  }

  /**
   * Event triggered when a Component enters the Visible Range. This happens when at least a pixel
   * of the Component is visible.
   */
  public static final int VISIBLE = 0;

  /**
   * Event triggered when a Component becomes invisible. This is the same with exiting the Visible
   * Range, the Focused Range and the Full Impression Range. All the code that needs to be executed
   * when a component leaves any of these ranges should be written in the handler for this event.
   */
  public static final int INVISIBLE = 1;

  /**
   * Event triggered when a Component enters the Focused Range. This happens when either the
   * Component occupies at least half of the viewport or, if the Component is smaller than half of
   * the viewport, when the it is fully visible.
   */
  public static final int FOCUSED_VISIBLE = 2;

  /**
   * Event triggered when a Component exits the Focused Range. The Focused Range is defined as at
   * least half of the viewport or, if the Component is smaller than half of the viewport, when the
   * it is fully visible.
   */
  public static final int UNFOCUSED_VISIBLE = 3;

  /**
   * Event triggered when a Component enters the Full Impression Range. This happens, for instance
   * in the case of a vertical RecyclerView, when both the top and bottom edges of the component
   * become visible.
   */
  public static final int FULL_IMPRESSION_VISIBLE = 4;

  /**
   * Event triggered when a Component enters the Partial Impression Range. This happens, for
   * instance in the case of a vertical RecyclerView, when the percentage of the visible area is
   * at least the specified threshold. The threshold can be set in
   * {@link EpoxyVisibilityTracker#setPartialImpressionThresholdPercentage(int)}.
   */
  public static final int PARTIAL_IMPRESSION_VISIBLE = 5;

  /**
   * Event triggered when a Component exits the Partial Impression Range. This happens, for
   * instance in the case of a vertical RecyclerView, when the percentage of the visible area is
   * less than a specified threshold. The threshold can be set in
   * {@link EpoxyVisibilityTracker#setPartialImpressionThresholdPercentage(int)}.
   */
  public static final int PARTIAL_IMPRESSION_INVISIBLE = 6;
}
