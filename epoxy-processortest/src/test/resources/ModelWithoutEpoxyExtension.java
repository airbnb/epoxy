package com.airbnb.epoxy;

public class ModelWithoutEpoxyExtension extends Object {
  @EpoxyAttribute int valueInt;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}