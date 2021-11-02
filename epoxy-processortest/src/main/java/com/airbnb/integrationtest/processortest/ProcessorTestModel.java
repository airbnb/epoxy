package com.airbnb.integrationtest.processortest;

import android.view.View;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.processortest2.ProcessorTest2Model;

public class ProcessorTestModel extends ProcessorTest2Model<View> {
  @EpoxyAttribute public int publicValue;
  @EpoxyAttribute protected int protectedValue;
  @EpoxyAttribute int packagePrivateValue;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}
