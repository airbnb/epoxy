package com.airbnb.epoxy;

public class ModelWithPrivateFieldWithoutGetterAndSetter extends EpoxyModel<Object> {
  @EpoxyAttribute private int valueInt;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}