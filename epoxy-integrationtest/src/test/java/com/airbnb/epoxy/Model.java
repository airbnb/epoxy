package com.airbnb.epoxy;

import android.view.View;

@EpoxyModelClass
abstract class Model extends EpoxyModel<View> {
  @EpoxyAttribute int value;

  @Override
  protected int getDefaultLayout() {
    return R.layout.model_with_click_listener;
  }
}
