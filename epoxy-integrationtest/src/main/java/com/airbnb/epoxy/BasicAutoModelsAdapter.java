package com.airbnb.epoxy;

class BasicAutoModelsAdapter extends EpoxyController {

  @AutoModel Model_ model1;
  @AutoModel Model_ model2;

  @Override
  protected void buildModels() {
    add(model1.id(1));
    add(model2.id(2));
  }
}
