package com.airbnb.epoxy;

import android.view.View;

class ModelWithClickListener extends EpoxyModel<View> {

  @EpoxyAttribute View.OnClickListener clickListener;

  @Override
  protected int getDefaultLayout() {
    return R.layout.model_with_click_listener;
  }
}
