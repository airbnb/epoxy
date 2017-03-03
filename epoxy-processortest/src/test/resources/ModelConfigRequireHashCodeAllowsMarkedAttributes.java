package com.airbnb.epoxy.configtest;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyAttribute.Option;
import com.airbnb.epoxy.EpoxyModel;

public class ModelConfigRequireHashCodeAllowsMarkedAttributes extends EpoxyModel<Object> {

  public static class ClassWithoutHashCode {

  }

  @EpoxyAttribute(Option.IgnoreRequireHashCode) ClassWithoutHashCode classWithoutHashCode;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}