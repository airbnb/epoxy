package com.airbnb.epoxy;

import java.util.List;

@EpoxyConfig(requireHashCode = true)
public class ModelRequiresHashCodeIterableSucceeds extends EpoxyModel<Object> {
  @EpoxyAttribute List<String> clickListener;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}