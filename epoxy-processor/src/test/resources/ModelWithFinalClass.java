package com.airbnb.epoxy;

public final class ModelWithFinalClass extends EpoxyModel<Object> {
  @EpoxyAttribute static int valueInt;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}