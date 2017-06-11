package com.airbnb.epoxy;

import android.view.View;

public class ModelWithLongClickListener extends EpoxyModel<View> {

  @EpoxyAttribute View.OnLongClickListener clickListener;

  @Override
  protected int getDefaultLayout() {
    return R.layout.model_with_click_listener;
  }

  @Override
  public void bind(View view) {
    view.setOnLongClickListener(clickListener);
  }
}
