package com.airbnb.epoxy;

import android.support.annotation.Nullable;

public abstract class TypedAutoEpoxyAdapter<T> extends AutoEpoxyAdapter {
  private T currentData;

  public void setData(T data) {
    currentData = data;
    requestModelUpdate();
  }

  @Nullable
  public T getCurrentData() {
    return currentData;
  }

  @Override
  protected final void buildModels() {
    buildModels(currentData);
  }

  protected abstract void buildModels(T data);
}
