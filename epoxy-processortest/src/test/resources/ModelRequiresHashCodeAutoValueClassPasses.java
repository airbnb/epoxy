package com.airbnb.epoxy;

import com.google.auto.value.AutoValue;

@EpoxyConfig(requireHashCode = true)
public class ModelRequiresHashCodeAutoValueClassPasses extends EpoxyModel<Object> {

  @AutoValue
  public static abstract class AutoValueClass {

  }

  @EpoxyAttribute AutoValueClass autoValueClass;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}