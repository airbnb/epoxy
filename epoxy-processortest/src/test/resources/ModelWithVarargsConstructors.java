package com.airbnb.epoxy;

public class ModelWithVarargsConstructors extends EpoxyModel<Object> {
  @EpoxyAttribute int valueInt;
  @EpoxyAttribute String[] varargs;

  public ModelWithVarargsConstructors(String... varargs) {
    this.varargs = varargs;
  }

  public ModelWithVarargsConstructors(int valueInt, String... varargs) {
    this.valueInt = valueInt;
    this.varargs = varargs;
  }

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}