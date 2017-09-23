package com.airbnb.epoxy.integrationtest;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

public class ControllerWithAutoModel extends EpoxyController {

  @AutoModel Model_ model;

  @Override
  protected void buildModels() {
    add(model);
  }
}
