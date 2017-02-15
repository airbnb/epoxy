package com.airbnb.epoxy;

@ModuleEpoxyConfig(requireHashCode = true)
public class ModelConfigRequireHashCodeInterfaceWithHashCodePasses extends EpoxyModel<Object> {

  interface MyInterface {
    int hashCode();
  }

  @EpoxyAttribute MyInterface myInterfaceImplementation;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}