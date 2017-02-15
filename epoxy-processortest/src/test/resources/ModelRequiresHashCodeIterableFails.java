package com.airbnb.epoxy.configtest;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;

import java.util.List;

public class ModelRequiresHashCodeIterableFails extends EpoxyModel<Object> {
  @EpoxyAttribute List<Object> clickListener;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}