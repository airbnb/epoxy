package com.airbnb.epoxy;

public final class ModelWithFinalClass extends EpoxyModel<Object> {
  @EpoxyAttribute int valueInt;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}