package com.airbnb.epoxy.different;

public class ModelWithDifferentPackageParent extends ModelWithPackagePrivateAttribute {
  @EpoxyAttribute int value3;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}