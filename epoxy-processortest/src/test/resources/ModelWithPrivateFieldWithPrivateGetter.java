package com.airbnb.epoxy;

public class ModelWithPrivateFieldWithPrivateGetter extends EpoxyModel<Object> {
  @EpoxyAttribute private int valueInt;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }

  private int getValueInt() {
    return valueInt;
  }

  public void setValueInt(int valueInt) {
    this.valueInt = valueInt;
  }
}