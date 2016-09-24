package com.airbnb.epoxy;

public class TestModel extends EpoxyModel<Object> {
  @EpoxyAttribute public int publicValue;
  @EpoxyAttribute protected int protectedValue;
  @EpoxyAttribute int packagePrivateValue;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}
