package com.airbnb.epoxy;

public class ModelWithPrivateFieldWithPrivateSetter extends EpoxyModel<Object> {
  @EpoxyAttribute private int valueInt;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }

  public int getValueInt() {
    return valueInt;
  }

  private void setValueInt(int valueInt) {
    this.valueInt = valueInt;
  }
}