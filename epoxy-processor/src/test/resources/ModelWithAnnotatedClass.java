package com.airbnb.epoxy;

@EpoxyModelClass
public class ModelWithAnnotatedClass extends EpoxyModel<Object> {

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}
