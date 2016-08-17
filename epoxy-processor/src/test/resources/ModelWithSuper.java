package com.airbnb.epoxy;

public class ModelWithSuper extends EpoxyModel<Object> {
  @EpoxyAttribute int valueInt;

  public ModelWithSuper valueInt(int valueInt) {
    this.valueInt = valueInt;
    return this;
  }

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}