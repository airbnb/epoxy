package com.airbnb.epoxy;

import android.view.View;
import android.view.ViewGroup.LayoutParams;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * An base {@link EpoxyModel} allow you set a model view within StaggeredGridLayoutManager is a
 * full span model or not.
 *
 * @param <T> The view of model view
 */
public abstract class EpoxyModelStaggeredGrid<T extends View> extends EpoxyModel<T> {

  /**
   * The flag to mark a model view is full span or not.
   */
  private boolean isFullSpanModel = false;

  /**
   * The method to set a model view can be full span or not.
   *
   * @param isFullSpanModel if true, the model view will be full span.
   * @return The model view.
   */
  public EpoxyModelStaggeredGrid<T> setFullSpanModel(boolean isFullSpanModel) {
    this.isFullSpanModel = isFullSpanModel;
    return this;
  }

  @Override
  public void bind(@NonNull T view) {
    if (isFullSpanModel) {
      makeFullSpan(view);
    }
    super.bind(view);
  }

  /**
   * Make the model view full span. If its layout manager isn't the StaggeredGridLayoutManager, it
   * will be throw {@link ClassCastException} when try to cast layout params.
   *
   * @param view The view to set full span.
   */
  private void makeFullSpan(@NonNull T view) {
    LayoutParams layoutParams = view.getLayoutParams();
    if (layoutParams != null) {
      try {
        StaggeredGridLayoutManager.LayoutParams params =
            (StaggeredGridLayoutManager.LayoutParams) layoutParams;
        params.setFullSpan(true);
      } catch (ClassCastException e) {
        throw new ClassCastException(
            "Couldn't use EpoxyModelStaggeredGrid without the StaggeredGridLayoutManager.");
      }
    }
  }
}
