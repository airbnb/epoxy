package com.airbnb.epoxy.configtest;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;

public class ModelRequiresHashCodeFailsBasicObject extends EpoxyModel<Object> {

  public static class ClassWithoutHashCode {

    @Override
    public boolean equals(Object obj) {
      return super.equals(obj);
    }
  }

  @EpoxyAttribute ClassWithoutHashCode classWithoutHashCode;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}