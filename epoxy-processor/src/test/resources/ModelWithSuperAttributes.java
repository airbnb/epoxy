package com.airbnb.epoxy;

public class ModelWithSuperAttributes extends EpoxyModel<Object> {

  @EpoxyAttribute int superValue;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }

  public static class SubModelWithSuperAttributes extends ModelWithSuperAttributes {
    @EpoxyAttribute int subValue;
  }
}