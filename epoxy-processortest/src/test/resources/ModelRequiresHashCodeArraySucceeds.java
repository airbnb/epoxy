package com.airbnb.epoxy.configtest;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;

public class ModelRequiresHashCodeArraySucceeds extends EpoxyModel<Object> {
  @EpoxyAttribute String[] clickListener;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}