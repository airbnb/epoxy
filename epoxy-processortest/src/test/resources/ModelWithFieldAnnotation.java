package com.airbnb.epoxy;

import androidx.annotation.Nullable;

public class ModelWithFieldAnnotation extends EpoxyModel<Object> {
  @EpoxyAttribute @Nullable String title;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }
}
