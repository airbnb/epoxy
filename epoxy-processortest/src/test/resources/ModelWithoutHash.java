package com.airbnb.epoxy;

public class ModelWithoutHash extends EpoxyModel<Object> {
  @EpoxyAttribute int value;
  @EpoxyAttribute(hash = false) int value2;
  @EpoxyAttribute(hash = false) String value3;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}