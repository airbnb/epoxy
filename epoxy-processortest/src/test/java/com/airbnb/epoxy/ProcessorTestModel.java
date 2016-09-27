package com.airbnb.epoxy;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;

public class ProcessorTestModel extends EpoxyModel<Object> {
  @EpoxyAttribute public int publicValue;
  @EpoxyAttribute protected int protectedValue;
  @EpoxyAttribute int packagePrivateValue;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}
