package com.airbnb.epoxy;

public class ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName extends EpoxyModel<Object> {
  @EpoxyAttribute private boolean isValue;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }

  public boolean isValue() {
    return isValue;
  }

  public void setValue(boolean isValue) {
    this.isValue = isValue;
  }
}
