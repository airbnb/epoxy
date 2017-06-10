package com.airbnb.epoxy;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * Used in the generated models to transform normal view click listeners to model click
 * listeners.
 */
public class WrappedEpoxyModelClickListener<T extends EpoxyModel<?>, V> implements OnClickListener {
  // Save the original click listener to call back to when clicked.
  // This also lets us call back to the original hashCode and equals methods
  private final OnModelClickListener<T, V> originalClickListener;
  private EpoxyViewHolder holder;
  private final T model;
  private V object;

  public WrappedEpoxyModelClickListener(T model, OnModelClickListener<T, V> originalClickListener) {
    this.model = model;
    this.originalClickListener = originalClickListener;
  }

  public void bind(EpoxyViewHolder holder, V object) {
    this.holder = holder;
    this.object = object;
  }


  @Override
  public void onClick(View v) {
    if (holder == null) {
      throw new IllegalStateException("Holder was not bound");
    }
    if (object == null) {
      throw new IllegalStateException("Object was not bound");
    }
    originalClickListener.onClick(model, object, v, holder.getAdapterPosition());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof WrappedEpoxyModelClickListener)) {
      return false;
    }

    WrappedEpoxyModelClickListener that = (WrappedEpoxyModelClickListener) o;

    return originalClickListener.equals(that.originalClickListener);
  }

  @Override
  public int hashCode() {
    return originalClickListener.hashCode();
  }
}
