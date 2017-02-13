package com.airbnb.epoxy;

@EpoxyConfig(requireHashCode = true)
public class ModelRequiresHashCodeArraySucceeds extends EpoxyModel<Object> {
  @EpoxyAttribute String[] clickListener;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}