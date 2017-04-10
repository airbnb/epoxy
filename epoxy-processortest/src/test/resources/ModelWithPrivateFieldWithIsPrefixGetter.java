package com.airbnb.epoxy;

public class ModelWithPrivateFieldWithIsPrefixGetter extends EpoxyModel<Object> {
  @EpoxyAttribute private boolean valueBoolean;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }

  public boolean isValueBoolean() {
    return valueBoolean;
  }

  public void setValueBoolean(boolean valueBoolean) {
    this.valueBoolean = valueBoolean;
  }
}