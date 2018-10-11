package com.airbnb.epoxy;

import com.airbnb.epoxy.VisibilityState.Visibility;

/** Used to register an onVisibilityChanged callback with a generated model. */
public interface OnModelVisibilityStateChangedListener<T extends EpoxyModel<V>, V> {

  /**
   * This will be called once the visibility changed.
   * <p>
   * @param model           The model being bound
   * @param view            The view that is being bound to the model
   * @param visibilityState The new visibility
   * <p>
   * @see VisibilityState
   */
  void onVisibilityStateChanged(T model, V view, @Visibility int visibilityState);
}
