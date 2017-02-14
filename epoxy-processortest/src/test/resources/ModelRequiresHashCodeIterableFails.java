package com.airbnb.epoxy;

import java.util.List;


@ModuleEpoxyConfig(requireHashCode = true)
public class ModelRequiresHashCodeIterableFails extends EpoxyModel<Object> {
  @EpoxyAttribute List<Object> clickListener;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}