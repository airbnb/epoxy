package com.airbnb.epoxy.configtest.sub;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;

public class ModelConfigSubPackageOverridesParent extends EpoxyModel<Object> {

  public static class ClassWithoutHashCode {

  }

  @EpoxyAttribute ClassWithoutHashCode classWithoutHashCode;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}