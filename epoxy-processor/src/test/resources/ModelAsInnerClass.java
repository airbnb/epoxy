package com.airbnb.epoxy;

public class ModelAsInnerClass {

  class InnerClass extends EpoxyModel<Object> {
    @EpoxyAttribute int valueInt;

    @Override
    protected int getDefaultLayout() {
      return 0;
    }
  }
}