package com.airbnb.epoxy.configtest;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;
import com.google.auto.value.AutoValue;

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