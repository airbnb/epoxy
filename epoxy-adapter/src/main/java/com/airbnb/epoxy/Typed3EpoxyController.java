package com.airbnb.epoxy;

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
  private boolean insideSetData;

  /**
   * Call this with the latest data when you want models to be rebuilt. The data will be passed on
   * to {@link #buildModels(Object, Object, Object)}
   */
  public void setData(T data1, U data2, V data3) {
    this.data1 = data1;
    this.data2 = data2;
    this.data3 = data3;
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
