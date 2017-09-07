package com.airbnb.integrationtest.processortest.differentpackage;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.integrationtest.processortest.ProcessorTestModel;

public class Model extends ProcessorTestModel {
  @EpoxyAttribute int subclassValue;
}
