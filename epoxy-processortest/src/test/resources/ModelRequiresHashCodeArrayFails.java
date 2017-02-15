package com.airbnb.epoxy.configtest;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;

public class ModelRequiresHashCodeArrayFails extends EpoxyModel<Object> {
  @EpoxyAttribute Object[] clickListener;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}