package com.airbnb.epoxy;

/** Used to register an onBind callback with a generated model. */
public interface OnModelBoundListener<T extends EpoxyModel<?>, V> {
  /**
   * This will be called immediately after a model was bound, with the model and view that were
   * bound together.
   */
  void onModelBound(T model, V view);
}
