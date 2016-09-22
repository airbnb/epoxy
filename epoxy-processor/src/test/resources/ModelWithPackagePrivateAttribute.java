package com.airbnb.epoxy;

public class ModelWithPackagePrivateAttribute extends EpoxyModel<Object> {
  public @EpoxyAttribute int value1;

  @EpoxyAttribute int value2;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}