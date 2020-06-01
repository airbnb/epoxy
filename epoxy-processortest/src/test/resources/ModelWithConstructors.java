package com.airbnb.epoxy;

public class ModelWithConstructors extends EpoxyModel<Object> {
  @EpoxyAttribute int valueInt;

  public ModelWithConstructors(long id, int valueInt) {
    super(id);
    this.valueInt = valueInt;
  }

  public ModelWithConstructors(int valueInt) {
    this.valueInt = valueInt;
  }

  public ModelWithConstructors() {
  }

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}