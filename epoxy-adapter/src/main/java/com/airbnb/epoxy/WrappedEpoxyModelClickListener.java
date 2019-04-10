package com.airbnb.epoxy;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import androidx.recyclerview.widget.RecyclerView;

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

  public WrappedEpoxyModelClickListener(OnModelClickListener<T, V> clickListener) {
    if (clickListener == null) {
      throw new IllegalArgumentException("Click listener cannot be null");
    }

    this.originalClickListener = clickListener;
    originalLongClickListener = null;
  }

  public WrappedEpoxyModelClickListener(OnModelLongClickListener<T, V> clickListener) {
    if (clickListener == null) {
      throw new IllegalArgumentException("Click listener cannot be null");
    }

    this.originalLongClickListener = clickListener;
    originalClickListener = null;
  }

  @Override
  public void onClick(View v) {
    EpoxyViewHolder epoxyHolder = ListenersUtils.getEpoxyHolderForChildView(v);
    if (epoxyHolder == null) {
      throw new IllegalStateException("Could not find RecyclerView holder for clicked view");
    }

    final int adapterPosition = epoxyHolder.getAdapterPosition();
    if (adapterPosition != RecyclerView.NO_POSITION) {
      //noinspection unchecked
      originalClickListener
          .onClick((T) epoxyHolder.getModel(), (V) epoxyHolder.objectToBind(), v, adapterPosition);
    }
  }

  @Override
  public boolean onLongClick(View v) {
    EpoxyViewHolder epoxyHolder = ListenersUtils.getEpoxyHolderForChildView(v);
    if (epoxyHolder == null) {
      throw new IllegalStateException("Could not find RecyclerView holder for clicked view");
    }

    final int adapterPosition = epoxyHolder.getAdapterPosition();
    if (adapterPosition != RecyclerView.NO_POSITION) {
      //noinspection unchecked
      return originalLongClickListener
          .onLongClick((T) epoxyHolder.getModel(), (V) epoxyHolder.objectToBind(), v,
              adapterPosition);
    }

    return false;
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
