package com.airbnb.epoxy;

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;

/**
 * Used in conjunction with {@link com.airbnb.epoxy.EpoxyModelWithHolder} to provide a view holder
 * pattern when binding to a model.
 */
public abstract class EpoxyHolder {

  public EpoxyHolder(@NonNull ViewParent parent) {
    this();
  }

  public EpoxyHolder() {
  }

  /**
   * Called when this holder is created, with the view that it should hold. You can use this
   * opportunity to find views by id, and do any other initialization you need. This is called only
   * once for the lifetime of the class.
   *
   * @param itemView A view inflated from the layout provided by
   * {@link EpoxyModelWithHolder#getLayout()}
   */
  protected abstract void bindView(@NonNull View itemView);
}
