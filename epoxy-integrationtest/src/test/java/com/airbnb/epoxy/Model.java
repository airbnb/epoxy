package com.airbnb.epoxy;

import android.widget.TextView;

@EpoxyModelClass
abstract class Model extends EpoxyModel<TextView> {
  @EpoxyAttribute int value;

  @Override
  protected int getDefaultLayout() {
    return R.layout.model_with_click_listener;
  }
}
