
package com.airbnb.epoxy;

import android.widget.Space;

import com.airbnb.viewmodeladapter.R;

/**
 * Used by the {@link EpoxyAdapter} as a placeholder for when {@link EpoxyModel#isShown()} is false.
 * Using a zero height and width {@link Space} view, as well as 0 span size, to exclude itself from
 * view.
 */
class HiddenEpoxyModel extends EpoxyModel<Space> {
  @Override
  public int getDefaultLayout() {
    return R.layout.view_holder_empty_view;
  }

  @Override
  public int getSpanSize(int spanCount, int position, int itemCount) {
    return 0;
  }
}
