package com.airbnb.epoxy;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Used in the generated models to transform normal checked change listener to model
 * checked change.
 */
public class WrappedEpoxyModelCheckedChangeListener<T extends EpoxyModel<?>, V>
    implements OnCheckedChangeListener {

  private final OnModelCheckedChangeListener<T, V> originalCheckedChangeListener;

  public WrappedEpoxyModelCheckedChangeListener(
      OnModelCheckedChangeListener<T, V> checkedListener
  ) {
    if (checkedListener == null) {
      throw new IllegalArgumentException("Checked change listener cannot be null");
    }

    this.originalCheckedChangeListener = checkedListener;
  }

  @Override
  public void onCheckedChanged(CompoundButton button, boolean isChecked) {
    EpoxyViewHolder epoxyHolder = ListenersUtils.getEpoxyHolderForChildView(button);
    if (epoxyHolder == null) {
      throw new IllegalStateException("Could not find RecyclerView holder for clicked view");
    }

    final int adapterPosition = epoxyHolder.getAdapterPosition();
    if (adapterPosition != RecyclerView.NO_POSITION) {
      originalCheckedChangeListener
          .onChecked((T) epoxyHolder.getModel(), (V) epoxyHolder.objectToBind(), button,
          isChecked, adapterPosition);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof WrappedEpoxyModelCheckedChangeListener)) {
      return false;
    }

    WrappedEpoxyModelCheckedChangeListener<?, ?>
        that = (WrappedEpoxyModelCheckedChangeListener<?, ?>) o;

    return originalCheckedChangeListener.equals(that.originalCheckedChangeListener);
  }

  @Override
  public int hashCode() {
    return originalCheckedChangeListener.hashCode();
  }
}
