package com.airbnb.epoxy;

import android.os.Handler;
import androidx.annotation.Nullable;

/**
 * This is a wrapper around {@link com.airbnb.epoxy.EpoxyController} to simplify how data is
 * accessed. Use this if the data required to build your models is represented by a single object.
 * <p>
 * To use this, create a subclass typed with your data object. Then, call {@link #setData(Object)}
 * whenever that data changes. This class will handle calling {@link #buildModels(Object)} with the
 * latest data.
 * <p>
 * You should NOT call {@link #requestModelBuild()} directly.
 *
 * @see Typed2EpoxyController
 * @see Typed3EpoxyController
 * @see Typed4EpoxyController
 */
public abstract class TypedEpoxyController<T> extends EpoxyController {
  private T currentData;
  private boolean allowModelBuildRequests;

  public TypedEpoxyController() {
  }

  public TypedEpoxyController(Handler modelBuildingHandler, Handler diffingHandler) {
    super(modelBuildingHandler, diffingHandler);
  }

  public final void setData(T data) {
    currentData = data;
    allowModelBuildRequests = true;
    requestModelBuild();
    allowModelBuildRequests = false;
  }

  @Override
  public final void requestModelBuild() {
    if (!allowModelBuildRequests) {
      throw new IllegalStateException(
          "You cannot call `requestModelBuild` directly. Call `setData` instead to trigger a "
              + "model refresh with new data.");
    }
    super.requestModelBuild();
  }

  @Override
  public void moveModel(int fromPosition, int toPosition) {
    allowModelBuildRequests = true;
    super.moveModel(fromPosition, toPosition);
    allowModelBuildRequests = false;
  }

  @Override
  public void requestDelayedModelBuild(int delayMs) {
    if (!allowModelBuildRequests) {
      throw new IllegalStateException(
          "You cannot call `requestModelBuild` directly. Call `setData` instead to trigger a "
              + "model refresh with new data.");
    }
    super.requestDelayedModelBuild(delayMs);
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
