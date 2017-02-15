package com.airbnb.epoxy.configtest;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;

public class RequireAbstractModelFailsClassWithAttribute extends EpoxyModel<Object> {

  @EpoxyAttribute String value;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}