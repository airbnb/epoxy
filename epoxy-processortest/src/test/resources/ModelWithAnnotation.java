package com.airbnb.epoxy;

@EpoxyModelClass
@Deprecated
public class ModelWithAnnotation extends EpoxyModel<Object> {

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}
