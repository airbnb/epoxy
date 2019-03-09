package com.airbnb.epoxy.integrationtest;

import android.view.View;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;

import androidx.annotation.NonNull;

public class ModelWithClickListener extends EpoxyModel<View> {

  @EpoxyAttribute public View.OnClickListener clickListener;

  @Override
  protected int getDefaultLayout() {
    return R.layout.model_with_click_listener;
  }

  @Override
  public void bind(@NonNull View view) {
    view.setOnClickListener(clickListener);
  }
}
