package com.airbnb.epoxy;

public class ModelWithPrivateFieldWithSetterWithWrongParamType extends EpoxyModel<Object> {
  @EpoxyAttribute private int valueInt;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }

  public int getValueInt() {
    return valueInt;
  }

  public void setValueInt(String valueString) {
  }
}