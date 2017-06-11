package com.airbnb.epoxy.sample.models;

import android.support.annotation.Nullable;
import android.view.View;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;

public abstract class BaseTestModel<T extends View> extends EpoxyModel<T> {
  @EpoxyAttribute @Nullable Boolean showDivider;

  @Override
  public void bind(T view, EpoxyModel<?> previouslyBoundModel) {

  }
}
