package com.airbnb.epoxy;

import android.view.View;

import androidx.annotation.LayoutRes;

/**
 * An {@link EpoxyModel} extension to abstract setup and usage of layout resource and span size.
 *
 * @see HiddenEpoxyModel
 * @see SimpleEpoxyModel
 */
public class EpoxyModelWithLayoutView<T extends View> extends EpoxyModel<T> {

  @LayoutRes protected final int layoutRes;
  protected int spanCount;

  public EpoxyModelWithLayoutView(@LayoutRes int layoutRes, int spanCount) {
    this.layoutRes = layoutRes;
    this.spanCount = spanCount;
  }

  @Override
  protected int getDefaultLayout() {
    return layoutRes;
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return spanCount;
  }
}
