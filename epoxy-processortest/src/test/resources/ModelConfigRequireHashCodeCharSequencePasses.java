package com.airbnb.epoxy;

import com.google.auto.value.AutoValue;

@ModuleEpoxyConfig(requireHashCode = true)
public class ModelConfigRequireHashCodeCharSequencePasses extends EpoxyModel<Object> {

  @EpoxyAttribute CharSequence charSequence;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}