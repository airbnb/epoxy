package com.airbnb.epoxy;

import com.airbnb.epoxy.EpoxyAttribute.Option;

public class ModelDoNotUseInToString extends EpoxyModel<Object> {
  @EpoxyAttribute int value;
  @EpoxyAttribute({Option.DoNotUseInToString}) int value2;
  @EpoxyAttribute({Option.DoNotUseInToString}) String value3;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}