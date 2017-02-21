package com.airbnb.epoxy.adapter;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.BasicModelWithAttribute_;
import com.airbnb.epoxy.DiffAdapter;

public class AdapterWithAutoModel extends DiffAdapter {

  @AutoModel BasicModelWithAttribute_ modelWithAttribute_;

  @Override
  protected void buildModels() {

  }
}