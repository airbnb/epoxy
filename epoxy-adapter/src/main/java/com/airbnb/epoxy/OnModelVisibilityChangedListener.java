package com.airbnb.epoxy;

import android.support.annotation.Px;

/** Used to register an onVisibilityChanged callback with a generated model. */
public interface OnModelVisibilityChangedListener<T extends EpoxyModel<V>, V> {

  /**
   * This will be called once the view port changed.
   *
   * OnModelVisibilityChangedListener should be used with particular care since they will be
   * dispatched on every frame while scrolling. No heavy work should be done inside the
   * implementation. Using {@link OnModelVisibilityStateChangedListener} is recommended whenever
   * possible.
   *
   * @param model                The model being bound
   * @param view                 The view that is being bound to the model
   * @param percentHeightVisible The percentage of height visible
   * @param percentWidthVisible  The percentage of width visible
   * @param heightVisible        The visible height in pixel
   * @param widthVisible         The visible width  in pixel
   */
   void onVisibilityChanged(T model, V view, float percentHeightVisible, float percentWidthVisible,
      @Px int heightVisible, @Px int widthVisible);
}
