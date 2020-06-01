package com.airbnb.epoxy;

public class ModelWithoutSetter extends EpoxyModel<Object> {
  @EpoxyAttribute(setter = false) int value;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}