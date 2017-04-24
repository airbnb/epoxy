package com.airbnb.epoxy;

public class ControllerWithoutImplicityAdding extends EpoxyController {
  @AutoModel Model_ model1;

  @Override
  protected void buildModels() {
    model1.value(3);
  }
}
