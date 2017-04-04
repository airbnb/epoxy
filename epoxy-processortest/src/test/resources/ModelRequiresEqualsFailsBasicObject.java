package com.airbnb.epoxy.configtest;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;

public class ModelRequiresEqualsFailsBasicObject extends EpoxyModel<Object> {

  public static class ClassWithHashCodeAndNotEquals {

    @Override
    public int hashCode() {
      return super.hashCode();
    }
  }

  @EpoxyAttribute ClassWithHashCodeAndNotEquals classWithoutHashCode;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}