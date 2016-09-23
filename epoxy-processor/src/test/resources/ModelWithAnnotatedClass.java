package com.airbnb.epoxy;

@EpoxyClass
public class ModelWithAnnotatedClass extends EpoxyModel<Object> {

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}
