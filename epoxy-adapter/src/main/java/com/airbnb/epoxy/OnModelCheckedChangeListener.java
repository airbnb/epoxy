package com.airbnb.epoxy;

import android.widget.CompoundButton;

public interface OnModelCheckedChangeListener<T extends EpoxyModel<?>, V> {
  /**
   * Called when the view bound to the model is checked.
   *
   * @param model       The model that the view is bound to.
   * @param parentView  The view bound to the model which received the click.
   * @param checkedView The view that received the click. This is either a child of the parentView
   *                    or the parentView itself
   * @param isChecked   The new value for isChecked property.
   * @param position    The position of the model in the adapter.
   */
  void onChecked(T model, V parentView,
      CompoundButton checkedView, boolean isChecked, int position);
}
