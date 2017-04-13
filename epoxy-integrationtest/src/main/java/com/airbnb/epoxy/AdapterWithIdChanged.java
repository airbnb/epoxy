package com.airbnb.epoxy;

class AdapterWithIdChanged extends EpoxyController {

  @AutoModel Model_ model1 = new Model_();

  @Override
  protected void buildModels() {
    add(model1.id(23));
  }
}
