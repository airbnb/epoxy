package com.airbnb.epoxy;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

/**
 * Used in the generated models to transform normal view click listeners to model click
 * listeners.
 */
public class WrappedEpoxyModelClickListener<T extends EpoxyModel<?>, V>
    implements OnClickListener, OnLongClickListener {
  // Save the original click listener to call back to when clicked.
  // This also lets us call back to the original hashCode and equals methods
  private final OnModelClickListener<T, V> originalClickListener;
  private final OnModelLongClickListener<T, V> originalLongClickListener;
  private EpoxyViewHolder holder;
  private final T model;
  private V object;

  public WrappedEpoxyModelClickListener(T model, OnModelClickListener<T, V> clickListener) {
    if (clickListener == null) {
      throw new IllegalArgumentException("Click listener cannot be null");
    }

    this.model = model;
    this.originalClickListener = clickListener;
    originalLongClickListener = null;
  }

  public WrappedEpoxyModelClickListener(T model, OnModelLongClickListener<T, V> clickListener) {
    if (clickListener == null) {
      throw new IllegalArgumentException("Click listener cannot be null");
    }

    this.model = model;
    this.originalLongClickListener = clickListener;
    originalClickListener = null;
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
    if (originalClickListener == null) {
      throw new IllegalStateException("Long click listener was set.");
    }
    originalClickListener.onClick(model, object, v, holder.getAdapterPosition());
  }

  @Override
  public boolean onLongClick(View v) {
    if (holder == null) {
      throw new IllegalStateException("Holder was not bound");
    }
    if (object == null) {
      throw new IllegalStateException("Object was not bound");
    }
    if (originalLongClickListener == null) {
      throw new IllegalStateException("Normal click listener was set.");
    }
    return originalLongClickListener.onLongClick(model, object, v, holder.getAdapterPosition());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof WrappedEpoxyModelClickListener)) {
      return false;
    }

    WrappedEpoxyModelClickListener<?, ?> that = (WrappedEpoxyModelClickListener<?, ?>) o;

    if (originalClickListener != null ? !originalClickListener.equals(that.originalClickListener)
        : that.originalClickListener != null) {
      return false;
    }
    return originalLongClickListener != null
        ? originalLongClickListener.equals(that.originalLongClickListener)
        : that.originalLongClickListener == null;
  }

  @Override
  public int hashCode() {
    int result = originalClickListener != null ? originalClickListener.hashCode() : 0;
    result =
        31 * result + (originalLongClickListener != null ? originalLongClickListener.hashCode()
            : 0);
    return result;
  }
}
