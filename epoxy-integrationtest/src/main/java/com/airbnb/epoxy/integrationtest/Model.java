package com.airbnb.epoxy.integrationtest;

import android.widget.TextView;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;

import androidx.annotation.NonNull;

@EpoxyModelClass
public abstract class Model extends EpoxyModel<TextView> {
  @EpoxyAttribute public int value;

  @Override
  protected int getDefaultLayout() {
    return R.layout.model_with_click_listener;
  }

  @Override
  public void bind(@NonNull TextView view) {
    view.setText(String.valueOf(value));
  }
}
