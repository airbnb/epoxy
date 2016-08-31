package com.airbnb.epoxy;

public class ModelWithoutHash extends EpoxyModel<Object> {
  @EpoxyAttribute int value;
  @EpoxyAttribute(hash=false) int value2;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}