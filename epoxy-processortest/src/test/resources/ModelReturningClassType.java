package com.airbnb.epoxy;

import java.util.List;

public class ModelReturningClassType extends EpoxyModel<Object> {
  @EpoxyAttribute int value;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }

  public ModelReturningClassType classType(int classType) {
    return this;
  }

  public ModelReturningClassType classType(int param1, int param2) {
    return this;
  }

  public ModelReturningClassType list(List<String> list) {
    return this;
  }
}
