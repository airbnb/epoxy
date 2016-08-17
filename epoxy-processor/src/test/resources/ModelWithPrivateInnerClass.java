package com.airbnb.epoxy;

public class ModelWithPrivateInnerClass extends EpoxyModel<Object> {

  @Override
  protected int getDefaultLayout() {
    return 0;
  }

  private static class Test extends EpoxyModel<Object> {
    @EpoxyAttribute int value;

    @Override
    protected int getDefaultLayout() {
      return 0;
    }
  }
}