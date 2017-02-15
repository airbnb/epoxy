package com.airbnb.epoxy.configtest;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;

public class ModelRequiresHashCodeEnumPasses extends EpoxyModel<Object> {

  public enum MyEnum {
    Value
  }

  @EpoxyAttribute MyEnum enumValue;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}