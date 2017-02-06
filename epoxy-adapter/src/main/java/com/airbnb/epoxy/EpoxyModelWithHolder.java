package com.airbnb.epoxy;

import java.util.List;

/**
 * A version of {@link com.airbnb.epoxy.EpoxyModel} that allows you to use a view holder pattern
 * instead of a specific view when binding to your model.
 */
public abstract class EpoxyModelWithHolder<T extends EpoxyHolder> extends EpoxyModel<T> {

  public EpoxyModelWithHolder() {
  }

  public EpoxyModelWithHolder(long id) {
    super(id);
  }

  /** This should return a new instance of your {@link com.airbnb.epoxy.EpoxyHolder} class. */
  protected abstract T createNewHolder();

  @Override
  public void bind(T holder) {
    super.bind(holder);
  }

  @Override
  public void bind(T holder, List<Object> payloads) {
    super.bind(holder, payloads);
  }

  @Override
  public void unbind(T holder) {
    super.unbind(holder);
  }

  @Override
  public boolean onFailedToRecycleView(T holder) {
    return super.onFailedToRecycleView(holder);
  }

  @Override
  public void onViewAttachedToWindow(T holder) {
    super.onViewAttachedToWindow(holder);
  }

  @Override
  public void onViewDetachedFromWindow(T holder) {
    super.onViewDetachedFromWindow(holder);
  }
}
