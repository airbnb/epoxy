package com.airbnb.epoxy;

import java.util.List;


@EpoxyConfig(requireHashCode = true)
public class ModelRequiresHashCodeIterableFails extends EpoxyModel<Object> {
  @EpoxyAttribute List<Object> clickListener;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}