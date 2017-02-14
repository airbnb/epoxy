package com.airbnb.epoxy;

@ModuleEpoxyConfig(requireHashCode = true)
public class ModelRequiresHashCodeEnumPasses extends EpoxyModel<Object> {

  public enum MyEnum{
    Value
  }

  @EpoxyAttribute MyEnum enumValue;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}