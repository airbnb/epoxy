package com.airbnb.epoxy;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Px;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

class EpoxyVisibilityItem {

  private final Rect localVisibleRect = new Rect();

  private int adapterPosition = RecyclerView.NO_POSITION;

  @Px
  int size;

  private int otherSize;

  private boolean vertical;

  private float percentVisibleSize = 0.f;

  private int visibleSize;

  private int viewportSize;

  private boolean fullyVisible = false;
  private boolean visible = false;
  private boolean focusedVisible = false;

  /**
   * Update the visibility item according the current layout.
   *
   * @param view the current {@link com.airbnb.epoxy.EpoxyViewHolder}'s itemView
   * @param parent the {@link android.support.v7.widget.RecyclerView}
   * @param orientation the {@link android.support.v7.widget.LinearLayoutManager}'s orientation
   *
   * @return true if the view has been measured
   */
  boolean update(@NonNull View view, @NonNull RecyclerView parent, int orientation) {
    view.getLocalVisibleRect(localVisibleRect);
    vertical = orientation == LinearLayoutManager.VERTICAL;
    if (vertical) {
      size = view.getMeasuredHeight();
      otherSize = view.getMeasuredWidth();
      viewportSize = parent.getMeasuredHeight();
      visibleSize = localVisibleRect.height();
    } else {
      otherSize = view.getMeasuredHeight();
      size = view.getMeasuredWidth();
      viewportSize = parent.getMeasuredWidth();
      visibleSize = localVisibleRect.width();
    }
    percentVisibleSize = 100.f / size * visibleSize;
    if (visibleSize != size) {
      fullyVisible = false;
    }
    return size > 0;
  }

  void reset(int newAdapterPosition) {
    fullyVisible = false;
    visible = false;
    focusedVisible = false;
    adapterPosition = newAdapterPosition;
  }

  void handleVisible(@NonNull EpoxyViewHolder epoxyHolder) {
    if (!wasVisible() && isVisible()) {
      epoxyHolder.visibilityVisible();
    }
  }

  void handleInvisible(EpoxyViewHolder epoxyHolder, boolean detachEvent) {
    if (!wasInvisible() && isInvisible(detachEvent)) {
      epoxyHolder.visibilityInvisible();
    }
  }

  void handleFocusedVisible(EpoxyViewHolder epoxyHolder) {
    if (!wasFocusedVisible() && isFocusedVisible()) {
      epoxyHolder.visibilityFocusedVisible();
    }
  }

  void handleUnfocusedVisible(EpoxyViewHolder epoxyHolder) {
    if (!wasUnfocusedVisible() && isUnfocusedVisible()) {
      epoxyHolder.visibilityUnfocusedVisible();
    }
  }

  void handleFullImpressionVisible(EpoxyViewHolder epoxyHolder) {
    if (!wasFullImpressionVisible() && isFullImpressionVisible()) {
      epoxyHolder.visibilityFullImpressionVisible();
    }
  }

  void handleChanged(EpoxyViewHolder epoxyHolder) {
    if (vertical) {
      epoxyHolder.visibilityChanged(percentVisibleSize, 100.f, size, otherSize);
    } else {
      epoxyHolder.visibilityChanged(100.f, percentVisibleSize, otherSize, size);
    }
  }

  int getAdapterPosition() {
    return adapterPosition;
  }

  private boolean isVisible() {
    // true when at least one pixel of the Component is visible
    return visible = visibleSize > 0;
  }

  private boolean wasVisible() {
    return visible;
  }

  private boolean isInvisible(boolean detachEvent) {
    // true when when the Component no longer has any pixels on the screen
    boolean invisible = visibleSize <= 0 || detachEvent;
    if (invisible) {
      visible = false;
    }
    return !visible;
  }

  private boolean wasInvisible() {
    return !visible;
  }

  private boolean isFocusedVisible() {
    // true when either the Component occupies at least half of the viewport, or, if the Component
    // is smaller than half the viewport, when it is fully visible.
    return focusedVisible =
        size >= viewportSize / 2 || (visibleSize == size && size < viewportSize / 2);
  }

  private boolean wasFocusedVisible() {
    return focusedVisible;
  }

  private boolean isUnfocusedVisible() {
    // true when the Component is no longer focused, i.e. it is not fully visible and does not
    // occupy at least half the viewport.
    boolean unfocusedVisible =
        !(size >= viewportSize / 2 || (visibleSize == size && size < viewportSize / 2));
    if (unfocusedVisible) {
      focusedVisible = false;
    }
    return !focusedVisible;
  }

  private boolean wasUnfocusedVisible() {
    return !focusedVisible;
  }

  private boolean wasFullImpressionVisible() {
    return fullyVisible;
  }

  private boolean isFullImpressionVisible() {
    // true when the entire Component has passed through the viewport at some point.
    return fullyVisible = visibleSize == size;
  }
}

