package com.airbnb.epoxy;

import java.util.List;

public class TestAdapter extends EpoxyAdapter {

  public TestAdapter() {
    enableDiffing();
  }

  public List<EpoxyModel<?>> models() {
    return models;
  }

  @Override
  public void notifyModelsChanged() {
    super.notifyModelsChanged();
  }
}
