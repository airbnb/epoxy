package com.airbnb.epoxy;

public class ModelWithFinalField extends EpoxyModel<Object> {
  @EpoxyAttribute final int valueInt;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}