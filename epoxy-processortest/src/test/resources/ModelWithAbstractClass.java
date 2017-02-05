package com.airbnb.epoxy;

public abstract class ModelWithAbstractClass extends EpoxyModel<Object> {

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}
