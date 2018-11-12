package com.airbnb.epoxy;

import androidx.annotation.FloatRange;
import androidx.annotation.Px;

/** Used to register an onVisibilityChanged callback with a generated model. */
public interface OnModelVisibilityChangedListener<T extends EpoxyModel<V>, V> {

  /**
   * This will be called once the view visible part changes.
   * <p>
   * OnModelVisibilityChangedListener should be used with particular care since they will be
   * dispatched on every frame while scrolling. No heavy work should be done inside the
   * implementation. Using {@link OnModelVisibilityStateChangedListener} is recommended whenever
   * possible.
   * <p>
   * @param model                The model being bound
   * @param view                 The view that is being bound to the model
   * @param percentVisibleHeight The percentage of height visible (0-100)
   * @param percentVisibleWidth  The percentage of width visible (0-100)
   * @param heightVisible        The visible height in pixel
   * @param widthVisible         The visible width in pixel
   */
  void onVisibilityChanged(T model, V view,
      @FloatRange(from = 0, to = 100) float percentVisibleHeight,
      @FloatRange(from = 0, to = 100) float percentVisibleWidth,
      @Px int heightVisible, @Px int widthVisible);
}
