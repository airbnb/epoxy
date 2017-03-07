package com.airbnb.epoxy.adapter;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

public class AutoModelNotOnModelField extends EpoxyController {

  @AutoModel String value;

  @Override
  protected void buildModels() {

  }
}