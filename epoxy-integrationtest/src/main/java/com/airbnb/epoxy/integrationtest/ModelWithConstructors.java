package com.airbnb.epoxy.integrationtest;

import android.widget.TextView;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;

@EpoxyModelClass
public abstract class ModelWithConstructors extends EpoxyModel<TextView> {
  @EpoxyAttribute public int value;

  public ModelWithConstructors(long id, int value) {
    super(id);
    this.value = value;
  }

  public ModelWithConstructors(int value) {
    this.value = value;
  }

  public ModelWithConstructors(long id) {
    super(id);
  }

  @Override
  protected int getDefaultLayout() {
    return R.layout.model_with_click_listener;
  }
}
