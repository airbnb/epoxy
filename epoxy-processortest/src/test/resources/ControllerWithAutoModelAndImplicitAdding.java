package com.airbnb.epoxy.adapter;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.BasicModelWithAttribute_;
import com.airbnb.epoxy.EpoxyController;

public class ControllerWithAutoModelAndImplicitAdding extends EpoxyController {

  @AutoModel BasicModelWithAttribute_ modelWithAttribute1;
  @AutoModel BasicModelWithAttribute_ modelWithAttribute2;

  @Override
  protected void buildModels() {

  }
}