package com.airbnb.epoxy;

@ModuleEpoxyConfig(requireHashCode = true)
public class ModelRequiresHashCodeArrayFails extends EpoxyModel<Object> {
  @EpoxyAttribute Object[] clickListener;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}