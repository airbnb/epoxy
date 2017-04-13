package com.airbnb.epoxy;

class AdapterWithFieldAssigned extends EpoxyController {

  @AutoModel Model_ model1 = new Model_();

  @Override
  protected void buildModels() {
    add(model1);
  }
}
