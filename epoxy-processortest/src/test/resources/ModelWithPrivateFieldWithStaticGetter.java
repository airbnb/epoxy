package com.airbnb.epoxy;

public class ModelWithPrivateFieldWithStaticGetter extends EpoxyModel<Object> {
  @EpoxyAttribute private int valueInt;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }

  public static int getValueInt() {
    return 0;
  }

  public void setValueInt(int valueInt) {
    this.valueInt = valueInt;
  }
}