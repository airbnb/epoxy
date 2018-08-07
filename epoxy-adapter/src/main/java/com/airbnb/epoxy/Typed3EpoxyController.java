package com.airbnb.epoxy;

import android.os.Handler;

/**
 * This is a wrapper around {@link com.airbnb.epoxy.EpoxyController} to simplify how data is
 * accessed. Use this if the data required to build your models is represented by three objects.
 * <p>
 * To use this, create a subclass typed with your data object. Then, call {@link #setData}
 * whenever that data changes. This class will handle calling {@link #buildModels} with the
 * latest data.
 * <p>
 * You should NOT call {@link #requestModelBuild()} directly.
 *
 * @see TypedEpoxyController
 * @see Typed2EpoxyController
 * @see Typed4EpoxyController
 */
public abstract class Typed3EpoxyController<T, U, V> extends EpoxyController {

  private T data1;
  private U data2;
  private V data3;
  private boolean allowModelBuildRequests;

  public Typed3EpoxyController() {
  }

  public Typed3EpoxyController(Handler modelBuildingHandler, Handler diffingHandler) {
    super(modelBuildingHandler, diffingHandler);
  }

  /**
   * Call this with the latest data when you want models to be rebuilt. The data will be passed on
   * to {@link #buildModels(Object, Object, Object)}
   */
  public void setData(T data1, U data2, V data3) {
    this.data1 = data1;
    this.data2 = data2;
    this.data3 = data3;
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

  @Override
  protected final void buildModels() {
    if (!isBuildingModels()) {
      throw new IllegalStateException(
          "You cannot call `buildModels` directly. Call `setData` instead to trigger a model "
              + "refresh with new data.");
    }
    buildModels(data1, data2, data3);
  }

  protected abstract void buildModels(T data1, U data2, V data3);
}
