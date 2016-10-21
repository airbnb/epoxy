package com.airbnb.epoxy.processortest2;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;

public class ProcessorTest2Model extends EpoxyModel<Object> {

  @EpoxyAttribute protected int processorTest2ValueProtected;
  @EpoxyAttribute public int processorTest2ValuePublic;
  @EpoxyAttribute int processorTest2ValuePackagePrivate;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}
