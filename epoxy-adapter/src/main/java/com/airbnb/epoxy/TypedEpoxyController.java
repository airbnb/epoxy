package com.airbnb.epoxy;

import android.support.annotation.Nullable;

/**
 * This is a simple wrapper around {@link com.airbnb.epoxy.EpoxyController} to simplify how data is
 * accessed. Use this if the data required to build your models is represented by a single object.
 * <p>
 * To use this, create a subclass typed with your data object. Then, call {@link #setData(Object)}
 * whenever that data changes. This class will handle calling {@link #buildModels(Object)} with the
 * latest data.
 * <p>
 * You should NOT call {@link #requestModelBuild()} directly.
 */
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
