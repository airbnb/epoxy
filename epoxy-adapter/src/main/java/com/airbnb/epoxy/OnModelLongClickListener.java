package com.airbnb.epoxy;

import android.view.View;

public interface OnModelLongClickListener<T extends EpoxyModel<?>, V> {
  /**
   * Called when the view bound to the model is clicked.
   *
   * @param model       The model that the view is bound to.
   * @param parentView  The view bound to the model which received the click.
   * @param clickedView The view that received the click. This is either a child of the parentView
   *                    or the parentView itself
   * @param position    The position of the model in the adapter.
   */
  boolean onLongClick(T model, V parentView, View clickedView, int position);
}
