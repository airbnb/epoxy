package com.airbnb.epoxy;

import android.view.ViewParent;

import com.airbnb.epoxy.VisibilityState.Visibility;

import java.util.List;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Px;

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
  protected abstract T createNewHolder(@NonNull ViewParent parent);

  @Override
  public void bind(@NonNull T holder) {
    super.bind(holder);
  }

  @Override
  public void bind(@NonNull T holder, @NonNull List<Object> payloads) {
    super.bind(holder, payloads);
  }

  @Override
  public void bind(@NonNull T holder, @NonNull EpoxyModel<?> previouslyBoundModel) {
    super.bind(holder, previouslyBoundModel);
  }

  @Override
  public void unbind(@NonNull T holder) {
    super.unbind(holder);
  }


  @Override
  public void onVisibilityStateChanged(@Visibility int visibilityState, @NonNull T holder) {
    super.onVisibilityStateChanged(visibilityState, holder);
  }

  @Override
  public void onVisibilityChanged(
      @FloatRange(from = 0, to = 100) float percentVisibleHeight,
      @FloatRange(from = 0, to = 100) float percentVisibleWidth,
      @Px int visibleHeight, @Px int visibleWidth,
      @NonNull T holder) {
    super.onVisibilityChanged(
        percentVisibleHeight, percentVisibleWidth,
        visibleHeight, visibleWidth,
        holder);
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
