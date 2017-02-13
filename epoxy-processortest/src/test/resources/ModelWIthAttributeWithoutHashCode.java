package com.airbnb.epoxy;

@EpoxyConfig(requireHashCode = true)
public class ModelWithAttributeWithoutHashCode extends EpoxyModel<Object> {

  public static class ClassWithoutHashCode {

  }

  @EpoxyAttribute ClassWithoutHashCode classWithoutHashCode;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}