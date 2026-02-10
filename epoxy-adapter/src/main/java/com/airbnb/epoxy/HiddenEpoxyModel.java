
package com.airbnb.epoxy;

import android.widget.Space;

import com.airbnb.viewmodeladapter.R;

/**
 * Used by the {@link EpoxyAdapter} as a placeholder for when {@link EpoxyModel#isShown()} is false.
 * Using a zero height and width {@link Space} view, as well as 0 span size, to exclude itself from
 * view.
 */
class HiddenEpoxyModel extends EpoxyModelWithLayoutView<Space> {

  HiddenEpoxyModel() {
    super(R.layout.view_holder_empty_view, 0 /*spanCount*/);
  }

}
