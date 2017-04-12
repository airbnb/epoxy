package com.airbnb.epoxy;

import android.view.View;

class ModelChangesDuringBind extends EpoxyModel<View> {
  @EpoxyAttribute int value;

  @Override
  protected int getDefaultLayout() {
    return R.layout.model_with_click_listener;
  }

  @Override
  public void bind(View view) {
    super.bind(view);
    value = 3;
  }
}
