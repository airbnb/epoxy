package com.airbnb.epoxy;

import android.view.View;

public class ModelWithViewClickListener extends EpoxyModel<Object> {
  @EpoxyAttribute View.OnClickListener clickListener;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}