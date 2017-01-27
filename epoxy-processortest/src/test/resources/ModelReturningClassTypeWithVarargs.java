package com.airbnb.epoxy;

public class ModelReturningClassTypeWithVarargs extends EpoxyModel<Object> {
  @EpoxyAttribute int value;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }

  public ModelReturningClassTypeWithVarargs classType(String... varargs) {
    return this;
  }

  public ModelReturningClassTypeWithVarargs classType(String first, String... varargs) {
    return this;
  }
}
