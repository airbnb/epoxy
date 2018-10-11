package com.airbnb.epoxy;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Px;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * This class represent an item in the {@link android.support.v7.widget.RecyclerView} and it is
 * being reused with multiple model via the update method. There is 1:1 relationship between an
 * EpoxyVisibilityItem and a child within the {@link android.support.v7.widget.RecyclerView}.
 *
 * It contains the logic to compute the visibility state of an item. It will also invoke the
 * visibility callbacks on {@link com.airbnb.epoxy.EpoxyViewHolder}
 *
 * This class should remain non-public and is intended to be used by {@link EpoxyVisibilityTracker}
 * only.
 */
class EpoxyVisibilityItem {

  private final Rect localVisibleRect = new Rect();

  private int adapterPosition = RecyclerView.NO_POSITION;

  @Px
  private int sizeInScrollingDirection;

  private int sizeNotInScrollingDirection;

  private boolean verticalScrolling;

  private float percentVisibleSize = 0.f;

  private int visibleSize;

  private int viewportSize;

  private boolean fullyVisible = false;
  private boolean visible = false;
  private boolean focusedVisible = false;

  /** Store last value for de-duping */
  private int lastVisibleSizeNotified = -1;

  /**
   * Update the visibility item according the current layout.
   *
   * @param view        the current {@link com.airbnb.epoxy.EpoxyViewHolder}'s itemView
   * @param parent      the {@link android.support.v7.widget.RecyclerView}
   * @param vertical    true if it scroll vertically
   * @return true if the view has been measured
   */
  boolean update(@NonNull View view, @NonNull RecyclerView parent, boolean vertical) {
    view.getLocalVisibleRect(localVisibleRect);
    this.verticalScrolling = vertical;
    if (vertical) {
      sizeInScrollingDirection = view.getMeasuredHeight();
      sizeNotInScrollingDirection = view.getMeasuredWidth();
      viewportSize = parent.getMeasuredHeight();
      visibleSize = localVisibleRect.height();
    } else {
      sizeNotInScrollingDirection = view.getMeasuredHeight();
      sizeInScrollingDirection = view.getMeasuredWidth();
      viewportSize = parent.getMeasuredWidth();
      visibleSize = localVisibleRect.width();
    }
    percentVisibleSize = 100.f / sizeInScrollingDirection * visibleSize;
    if (visibleSize != sizeInScrollingDirection) {
      fullyVisible = false;
    }
    return sizeInScrollingDirection > 0;
  }

  int getAdapterPosition() {
    return adapterPosition;
  }

  void reset(int newAdapterPosition) {
    fullyVisible = false;
    visible = false;
    focusedVisible = false;
    adapterPosition = newAdapterPosition;
    lastVisibleSizeNotified = -1;
  }

  void handleVisible(@NonNull EpoxyViewHolder epoxyHolder) {
    if (!visible && isVisible()) {
      epoxyHolder.visibilityStateChanged(VisibilityState.VISIBLE);
    }
  }

  void handleInvisible(EpoxyViewHolder epoxyHolder, boolean detachEvent) {
    if (visible && isInvisible(detachEvent)) {
      epoxyHolder.visibilityStateChanged(VisibilityState.INVISIBLE);
    }
  }

  void handleFocusedVisible(EpoxyViewHolder epoxyHolder) {
    if (!focusedVisible && isFocusedVisible()) {
      epoxyHolder.visibilityStateChanged(VisibilityState.FOCUSED_VISIBLE);
    }
  }

  void handleUnfocusedVisible(EpoxyViewHolder epoxyHolder, boolean detachEvent) {
    if (focusedVisible && isUnfocusedVisible(detachEvent)) {
      epoxyHolder.visibilityStateChanged(VisibilityState.UNFOCUSED_VISIBLE);
    }
  }

  void handleFullImpressionVisible(EpoxyViewHolder epoxyHolder) {
    if (!fullyVisible && isFullImpressionVisible()) {
      epoxyHolder
          .visibilityStateChanged(VisibilityState.FULL_IMPRESSION_VISIBLE);
    }
  }

  void handleChanged(EpoxyViewHolder epoxyHolder) {
    if (visibleSize != lastVisibleSizeNotified) {
      if (verticalScrolling) {
        epoxyHolder.visibilityChanged(percentVisibleSize, 100.f, visibleSize,
            sizeNotInScrollingDirection);
      } else {
        epoxyHolder.visibilityChanged(100.f, percentVisibleSize,
            sizeNotInScrollingDirection, visibleSize);
      }
      lastVisibleSizeNotified = visibleSize;
    }
  }

  private boolean isVisible() {
    // true when at least one pixel of the Component is visible
    return visible = visibleSize > 0;
  }

  private boolean isInvisible(boolean detachEvent) {
    // true when when the Component no longer has any pixels on the screen
    boolean invisible = visibleSize <= 0 || detachEvent;
    if (invisible) {
      visible = false;
    }
    return !visible;
  }

  private boolean isFocusedVisible() {
    // true when either the Component occupies at least half of the viewport, or, if the Component
    // is smaller than half the viewport, when it is fully visible.
    return focusedVisible =
        sizeInScrollingDirection >= viewportSize / 2 || (visibleSize == sizeInScrollingDirection
            && sizeInScrollingDirection < viewportSize / 2);
  }

  private boolean isUnfocusedVisible(boolean detachEvent) {
    // true when the Component is no longer focused, i.e. it is not fully visible and does not
    // occupy at least half the viewport.
    boolean unfocusedVisible = detachEvent
        || !(sizeInScrollingDirection >= viewportSize / 2 || (
        visibleSize == sizeInScrollingDirection && sizeInScrollingDirection < viewportSize / 2));
    if (unfocusedVisible) {
      focusedVisible = false;
    }
    return !focusedVisible;
  }

  private boolean isFullImpressionVisible() {
    // true when the entire Component has passed through the viewport at some point.
    return fullyVisible = visibleSize == sizeInScrollingDirection;
  }
}

