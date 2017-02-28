package com.airbnb.epoxy;

public interface OnModelClickListener<T extends EpoxyModel<?>, V> {
  void onClick(T model, V view, int position);
}
