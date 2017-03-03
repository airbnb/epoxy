package com.airbnb.epoxy;

/** Used to register a click listener on a generated model. */
public interface OnModelClickListener<T extends EpoxyModel<?>, V> {
  /**
   * Called when the view bound to the model is clicked.
   *
   * @param model    The model that the view is bound to.
   * @param view     The view bound to the model which received the click.
   * @param position The position of the model in the adapter.
   */
  void onClick(T model, V view, int position);
}
