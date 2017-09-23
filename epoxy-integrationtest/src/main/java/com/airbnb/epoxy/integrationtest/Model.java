package com.airbnb.epoxy.integrationtest;

import android.widget.TextView;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;

@EpoxyModelClass
public abstract class Model extends EpoxyModel<TextView> {
  @EpoxyAttribute public int value;

  @Override
  protected int getDefaultLayout() {
    return R.layout.model_with_click_listener;
  }
}
