package com.airbnb.epoxy.integrationtest;

import android.support.annotation.NonNull;
import android.view.View;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;

public class ModelWithLongClickListener extends EpoxyModel<View> {

  @EpoxyAttribute View.OnLongClickListener clickListener;

  @Override
  protected int getDefaultLayout() {
    return R.layout.model_with_click_listener;
  }

  @Override
  public void bind(@NonNull View view) {
    view.setOnLongClickListener(clickListener);
  }
}
