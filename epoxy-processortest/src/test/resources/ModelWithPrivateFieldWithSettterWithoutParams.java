package com.airbnb.epoxy;

public class ModelWithPrivateFieldWithSettterWithoutParams extends EpoxyModel<Object> {
  @EpoxyAttribute private int valueInt;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }

  public int getValueInt() {
    return valueInt;
  }

  public void setValueInt() {
    this.valueInt = 0;
  }
}