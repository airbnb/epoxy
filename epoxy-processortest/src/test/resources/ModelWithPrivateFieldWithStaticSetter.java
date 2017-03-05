package com.airbnb.epoxy;

public class ModelWithPrivateFieldWithStaticSetter extends EpoxyModel<Object> {
  @EpoxyAttribute private int valueInt;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }

  public int getValueInt() {
    return valueInt;
  }

  public static void setValueInt(int valueInt) {

  }
}