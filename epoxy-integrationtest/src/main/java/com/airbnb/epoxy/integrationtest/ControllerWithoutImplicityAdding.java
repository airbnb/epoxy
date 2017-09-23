package com.airbnb.epoxy.integrationtest;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

public class ControllerWithoutImplicityAdding extends EpoxyController {
  @AutoModel Model_ model1;

  @Override
  protected void buildModels() {
    model1.value(3);
  }
}
