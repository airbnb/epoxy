package com.airbnb.epoxy.integrationtest;

import android.view.View;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;

import androidx.annotation.NonNull;

public class ModelChangesDuringBind extends EpoxyModel<View> {
  @EpoxyAttribute public int value;

  @Override
  protected int getDefaultLayout() {
    return R.layout.model_with_click_listener;
  }

  @Override
  public void bind(@NonNull View view) {
    super.bind(view);
    value = 3;
  }
}
