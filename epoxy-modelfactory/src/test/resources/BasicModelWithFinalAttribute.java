package com.airbnb.epoxy;

public class BasicModelWithFinalAttribute extends EpoxyModel<Object> {
  @EpoxyAttribute final int value;

  public BasicModelWithFinalAttribute() {
    value = 0;
  }

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}