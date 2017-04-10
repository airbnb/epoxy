package com.airbnb.epoxy;

import com.airbnb.epoxy.EpoxyAttribute.Option;

public class ModelDoNotHash extends EpoxyModel<Object> {
  @EpoxyAttribute int value;
  @EpoxyAttribute({Option.DoNotHash}) int value2;
  @EpoxyAttribute({Option.DoNotHash}) String value3;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}