package com.airbnb.epoxy;

import android.support.annotation.Nullable;

public abstract class TypedEpoxyController<T> extends EpoxyController {
  private T currentData;
  private boolean insideSetData;

  public final void setData(T data) {
    currentData = data;
    insideSetData = true;
    requestModelBuild();
    insideSetData = false;
  }

  @Override
  public final void requestModelBuild() {
    if (!insideSetData) {
      throw new IllegalStateException(
          "You cannot call `requestModelBuild` directly. Call `setData` instead to trigger a "
              + "model refresh with new data.");
    }
    super.requestModelBuild();
  }

  @Nullable
  public final T getCurrentData() {
    return currentData;
  }

  @Override
  protected final void buildModels() {
    if (!isBuildingModels()) {
      throw new IllegalStateException(
          "You cannot call `buildModels` directly. Call `setData` instead to trigger a model "
              + "refresh with new data.");
    }
    buildModels(currentData);
  }

  protected abstract void buildModels(T data);
}
