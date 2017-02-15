package com.airbnb.epoxy.configtest;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;

import java.util.List;

public class ModelRequiresHashCodeIterableSucceeds extends EpoxyModel<Object> {
  @EpoxyAttribute List<String> clickListener;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}