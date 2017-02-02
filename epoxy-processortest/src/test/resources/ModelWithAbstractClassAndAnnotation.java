package com.airbnb.epoxy;

@EpoxyModelClass
public abstract class ModelWithAbstractClassAndAnnotation extends EpoxyModel<Object> {

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}
