package com.airbnb.epoxy.integrationtest;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

public class AdapterWithIdChanged extends EpoxyController {

  @AutoModel Model_ model1 = new Model_();

  @Override
  protected void buildModels() {
    add(model1.id(23));
  }
}
