package com.airbnb.epoxy;

public class ModelFactoryBasicModelWithAttribute extends EpoxyModel<Object> {
  @EpoxyAttribute int value;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}