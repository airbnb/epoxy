package com.airbnb.epoxy.adapter;

import com.airbnb.epoxy.AutoEpoxyAdapter;
import com.airbnb.epoxy.AutoModel;

public class AutoModelNotOnModelField extends AutoEpoxyAdapter {

  @AutoModel String value;

  @Override
  protected void buildModels() {

  }
}