package com.airbnb.epoxy;

@ModuleEpoxyConfig(requireHashCode = true)
public class ModelRequiresHashCodeFailsBasicObject extends EpoxyModel<Object> {

  public static class ClassWithoutHashCode {

  }

  @EpoxyAttribute ClassWithoutHashCode classWithoutHashCode;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}