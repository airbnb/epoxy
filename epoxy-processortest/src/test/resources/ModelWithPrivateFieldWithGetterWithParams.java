package com.airbnb.epoxy;

public class ModelWithPrivateFieldWithGetterWithParams extends EpoxyModel<Object> {
  @EpoxyAttribute private int valueInt;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }

  public int getValueInt(int param) {
    return valueInt;
  }

  public void setValueInt(int valueInt) {
    this.valueInt = valueInt;
  }
}