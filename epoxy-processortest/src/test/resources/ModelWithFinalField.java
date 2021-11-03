package com.airbnb.epoxy;

public class ModelWithFinalField extends EpoxyModel<Object> {
  @EpoxyAttribute final int finalValueInt;
  @EpoxyAttribute int nonFinalValueInt;

  public ModelWithFinalField(long id, int valueInt) {
    super(id);
    this.finalValueInt = valueInt;
  }

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}