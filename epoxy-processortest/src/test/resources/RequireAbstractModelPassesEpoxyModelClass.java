package com.airbnb.epoxy.configtest;

import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;

@EpoxyModelClass
public abstract class RequireAbstractModelPassesEpoxyModelClass extends EpoxyModel<Object> {

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}