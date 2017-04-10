package com.airbnb.epoxy;

/** Used to register an onBind callback with a generated model. */
public interface OnModelBoundListener<T extends EpoxyModel<?>, V> {
  /**
   * This will be called immediately after a model was bound, with the model and view that were
   * bound together.
   *
   * @param model    The model being bound
   * @param view     The view that is being bound to the model
   * @param position The adapter position of the model
   */
  void onModelBound(T model, V view, int position);
}
