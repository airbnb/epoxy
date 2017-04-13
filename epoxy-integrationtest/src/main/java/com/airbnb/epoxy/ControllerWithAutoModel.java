package com.airbnb.epoxy;

public class ControllerWithAutoModel extends EpoxyController {

  @AutoModel Model_ model;

  @Override
  protected void buildModels() {
    add(model);
  }
}
