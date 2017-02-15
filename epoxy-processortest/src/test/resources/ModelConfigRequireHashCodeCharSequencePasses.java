package com.airbnb.epoxy;

public class ModelConfigRequireHashCodeCharSequencePasses extends EpoxyModel<Object> {

  @EpoxyAttribute CharSequence charSequence;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}