package com.airbnb.epoxy;

public class ModelWithFinalField extends EpoxyModel<Object> {
  @EpoxyAttribute final int valueInt;

  public ModelWithFinalField(long id, int valueInt) {
    super(id);
    this.valueInt = valueInt;
  }

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}