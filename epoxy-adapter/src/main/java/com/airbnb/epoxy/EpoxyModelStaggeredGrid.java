package com.airbnb.epoxy;

import android.view.View;

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
   * The attribute to make a view can be full span or not in StaggeredLayoutManager
   */
  @EpoxyAttribute
  public boolean staggeredFullSpan = false;

  @Override
  public void bind(@NonNull T view) {
    syncStaggeredLayoutSpan(view);
    super.bind(view);
  }

  @Override
  public void bind(@NonNull T view, @NonNull EpoxyModel<?> previouslyBoundModel) {
    if (!(previouslyBoundModel instanceof EpoxyModelStaggeredGrid)) {
      super.bind(view, previouslyBoundModel);
      return;
    }

    EpoxyModelStaggeredGrid that = (EpoxyModelStaggeredGrid) previouslyBoundModel;

    if (staggeredFullSpan != that.staggeredFullSpan) {
      bind(view);
      return;
    }
    super.bind(view, previouslyBoundModel);
  }

  /**
   * Make the model view full span. If its layout manager isn't the StaggeredGridLayoutManager, it
   * will be throw {@link ClassCastException} when try to cast layout params.
   *
   * @param view The view to set full span.
   */
  private void syncStaggeredLayoutSpan(@NonNull T view) {
    try {
      StaggeredGridLayoutManager.LayoutParams layoutParams =
          (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
      if (staggeredFullSpan && !layoutParams.isFullSpan()) {
        layoutParams.setFullSpan(true);
      } else if (!staggeredFullSpan && layoutParams.isFullSpan()) {
        layoutParams.setFullSpan(false);
      }
    } catch (ClassCastException e) {
      throw new ClassCastException("Please use attribute "
          + "staggeredFullSpan within StaggeredLayoutManager.");
    }
  }
}
