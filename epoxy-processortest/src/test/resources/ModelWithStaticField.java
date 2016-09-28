package com.airbnb.epoxy;

public class ModelWithStaticField extends EpoxyModel<Object> {
  @EpoxyAttribute static int valueInt;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}