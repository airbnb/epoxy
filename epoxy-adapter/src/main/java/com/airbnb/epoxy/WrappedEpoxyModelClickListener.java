package com.airbnb.epoxy;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * Used in the generated models to transform normal view click listeners to model click
 * listeners.
 */
public abstract class WrappedEpoxyModelClickListener implements OnClickListener {
  // Save the original click listener so if it gets changed on
  // the generated model this click listener won't be affected
  // if it is still bound to a view. This also lets us call back to the original hashCode and
  // equals methods
  private final OnModelClickListener originalClickListener;

  public WrappedEpoxyModelClickListener(OnModelClickListener originalClickListener) {
    this.originalClickListener = originalClickListener;
  }

  @Override
  public void onClick(View v) {
    wrappedOnClick(v, originalClickListener);
  }

  protected abstract void wrappedOnClick(View v, OnModelClickListener originalClickListener);

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
