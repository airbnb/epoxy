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

  /** Store last value for deduping */
  private int lastVisibleSizeNotified = -1;

  /**
   * Update the visibility item according the current layout.
   *
   * @param view        the current {@link com.airbnb.epoxy.EpoxyViewHolder}'s itemView
   * @param parent      the {@link android.support.v7.widget.RecyclerView}
   * @param orientation the {@link android.support.v7.widget.LinearLayoutManager}'s orientation
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
      epoxyHolder.visibilityStateChanged(OnModelVisibilityStateChangedListener.VISIBLE);
    }
  }

  void handleInvisible(EpoxyViewHolder epoxyHolder, boolean detachEvent) {
    if (visible && isInvisible(detachEvent)) {
      epoxyHolder.visibilityStateChanged(OnModelVisibilityStateChangedListener.INVISIBLE);
    }
  }

  void handleFocusedVisible(EpoxyViewHolder epoxyHolder) {
    if (!focusedVisible && isFocusedVisible()) {
      epoxyHolder.visibilityStateChanged(OnModelVisibilityStateChangedListener.FOCUSED_VISIBLE);
    }
  }

  void handleUnfocusedVisible(EpoxyViewHolder epoxyHolder, boolean detachEvent) {
    if (focusedVisible && isUnfocusedVisible(detachEvent)) {
      epoxyHolder.visibilityStateChanged(OnModelVisibilityStateChangedListener.UNFOCUSED_VISIBLE);
    }
  }

  void handleFullImpressionVisible(EpoxyViewHolder epoxyHolder) {
    if (!fullyVisible && isFullImpressionVisible()) {
      epoxyHolder
          .visibilityStateChanged(OnModelVisibilityStateChangedListener.FULL_IMPRESSION_VISIBLE);
    }
  }

  void handleChanged(EpoxyViewHolder epoxyHolder) {
    if (visibleSize != lastVisibleSizeNotified) {
      if (vertical) {
        epoxyHolder.visibilityChanged(percentVisibleSize, 100.f, visibleSize, otherSize);
      } else {
        epoxyHolder.visibilityChanged(100.f, percentVisibleSize, otherSize, visibleSize);
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
        size >= viewportSize / 2 || (visibleSize == size && size < viewportSize / 2);
  }

  private boolean isUnfocusedVisible(boolean detachEvent) {
    // true when the Component is no longer focused, i.e. it is not fully visible and does not
    // occupy at least half the viewport.
    boolean unfocusedVisible = detachEvent
        || !(size >= viewportSize / 2 || (visibleSize == size && size < viewportSize / 2));
    if (unfocusedVisible) {
      focusedVisible = false;
    }
    return !focusedVisible;
  }

  private boolean isFullImpressionVisible() {
    // true when the entire Component has passed through the viewport at some point.
    return fullyVisible = visibleSize == size;
  }
}

