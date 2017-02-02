package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;

import java.util.List;

/**
 * Helper to bind data to a view using a builder style. The parameterized type should extend
 * Android's View.
 */
public abstract class EpoxyModel<T> {

  /**
   * Counts how many of these objects are created, so that each new object can have a unique id .
   * Uses negative values so that these autogenerated ids don't clash with database ids that may be
   * set with {@link #id(long)}
   */
  private static long idCounter = -1;

  /**
   * An id that can be used to uniquely identify this {@link EpoxyModel} for use in RecyclerView
   * stable ids. It defaults to a unique id for this object instance, if you want to maintain the
   * same id across instances use {@link #id(long)}
   */
  private long id;
  @LayoutRes private int layout;
  private boolean shown = true;
  /** Set to true once this model is added to an adapter. */
  boolean addedToAdapter;

  protected EpoxyModel(long id) {
    id(id);
  }

  public EpoxyModel() {
    this(idCounter--);
  }

  /**
   * Binds the current data to the given view. You should bind all fields including unset/empty
   * fields to ensure proper recycling.
   */
  public void bind(T view) {

  }

  /**
   * Similar to {@link #bind(Object)}, but provides a non null, non empty list of payloads
   * describing what changed. This is the payloads list specified in the adapter's notifyItemChanged
   * method. This is a useful optimization to allow you to only change part of a view instead of
   * updating the whole thing, which may prevent unnecessary layout calls. If there are no payloads
   * then {@link #bind(Object)} is called instead.
   */
  public void bind(T view, List<Object> payloads) {
    bind(view);
  }

  /** Subclasses can override this if their view needs to release resources when it's recycled. */
  public void unbind(T view) {
  }

  public long id() {
    return id;
  }

  /**
   * Override the default id in cases where the data subject naturally has an id, like an object
   * from a database. This id can only be set before the model is added to the adapter, it is an
   * error to change the id after that.
   */
  public EpoxyModel<T> id(long id) {
    if (addedToAdapter && id != this.id) {
      throw new IllegalStateException(
          "Cannot change a model's id after it has been added to the adapter.");
    }

    this.id = id;
    return this;
  }

  @LayoutRes
  protected abstract int getDefaultLayout();

  public EpoxyModel<T> layout(@LayoutRes int layoutRes) {
    layout = layoutRes;
    return this;
  }

  @LayoutRes
  public final int getLayout() {
    if (layout == 0) {
      return getDefaultLayout();
    }

    return layout;
  }

  /**
   * Sets fields of the model to default ones.
   */
  public EpoxyModel<T> reset() {
    layout = 0;
    shown = true;

    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EpoxyModel)) {
      return false;
    }

    EpoxyModel<?> that = (EpoxyModel<?>) o;

    if (id != that.id) {
      return false;
    }
    if (getLayout() != that.getLayout()) {
      return false;
    }
    return shown == that.shown;
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + getLayout();
    result = 31 * result + (shown ? 1 : 0);
    return result;
  }

  /**
   * Subclasses can override this if they want their view to take up more than one span in a grid
   * layout.
   *
   * @param totalSpanCount The number of spans in the grid
   * @param position       The position of the model
   * @param itemCount      The total number of items in the adapter
   */
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return 1;
  }

  public EpoxyModel<T> show() {
    shown = true;
    return this;
  }

  public EpoxyModel<T> show(boolean show) {
    shown = show;
    return this;
  }

  public EpoxyModel<T> hide() {
    shown = false;
    return this;
  }

  /**
   * Whether the model's view should be shown on screen. If false it won't be inflated and drawn,
   * and will be like it was never added to the recycler view.
   */
  public boolean isShown() {
    return shown;
  }

  /**
   * Whether the adapter should save the state of the view bound to this model.
   */
  public boolean shouldSaveViewState() {
    return false;
  }

  /**
   * Called if the RecyclerView failed to recycle this model's view. You can take this opportunity
   * to clear the animation(s) that affect the View's transient state and return <code>true</code>
   * so that the View can be recycled. Keep in mind that the View in question is already removed
   * from the RecyclerView.
   *
   * @return True if the View should be recycled, false otherwise
   * @see EpoxyAdapter#onFailedToRecycleView(android.support.v7.widget.RecyclerView.ViewHolder)
   */
  public boolean onFailedToRecycleView(T view) {
    return false;
  }

  /**
   * Called when this model's view is attached to the window.
   *
   * @see EpoxyAdapter#onViewAttachedToWindow(android.support.v7.widget.RecyclerView.ViewHolder)
   */
  public void onViewAttachedToWindow(T view) {

  }

  /**
   * Called when this model's view is detached from the the window.
   *
   * @see EpoxyAdapter#onViewDetachedFromWindow(android.support.v7.widget.RecyclerView.ViewHolder)
   */
  public void onViewDetachedFromWindow(T view) {

  }

  @Override
  public String toString() {
    return "{" + getClass().getSimpleName()
        + "id=" + id
        + ", layout=" + getLayout()
        + ", shown=" + shown
        + '}';
  }
}
