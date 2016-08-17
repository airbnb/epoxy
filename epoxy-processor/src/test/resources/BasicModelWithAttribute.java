package com.airbnb.epoxy;

public class BasicModelWithAttribute extends EpoxyModel<Object> {
  @EpoxyAttribute int value;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}