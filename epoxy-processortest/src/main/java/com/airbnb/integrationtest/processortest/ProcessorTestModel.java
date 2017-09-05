package com.airbnb.integrationtest.processortest;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.processortest2.ProcessorTest2Model;

public class ProcessorTestModel extends ProcessorTest2Model {
  @EpoxyAttribute public int publicValue;
  @EpoxyAttribute protected int protectedValue;
  @EpoxyAttribute int packagePrivateValue;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}
