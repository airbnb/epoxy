package com.airbnb.epoxy;

public class ModelWithAnnotatedClassAndSuperAttributes extends EpoxyModel<Object> {

  @EpoxyAttribute int superValue;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }

  @EpoxyClass
  public static class SubModelWithAnnotatedClassAndSuperAttributes extends ModelWithAnnotatedClassAndSuperAttributes {

  }
}
