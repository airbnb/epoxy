package com.airbnb.epoxy;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This class represent an item in the {@link androidx.recyclerview.widget.RecyclerView} and it is
 * being reused with multiple model via the update method. There is 1:1 relationship between an
 * EpoxyVisibilityItem and a child within the {@link androidx.recyclerview.widget.RecyclerView}.
 *
 * It contains the logic to compute the visibility state of an item. It will also invoke the
 * visibility callbacks on {@link com.airbnb.epoxy.EpoxyViewHolder}
 *
 * This class should remain non-public and is intended to be used by {@link EpoxyVisibilityTracker}
 * only.
 */
class EpoxyVisibilityItem {

  private static final int NOT_NOTIFIED = -1;

  private final Rect localVisibleRect = new Rect();

  private int adapterPosition = RecyclerView.NO_POSITION;

  @Px
  private int height;
  @Px
  private int width;

  private float percentVisibleHeight = 0.f;
  private float percentVisibleWidth = 0.f;

  @Px
  private int visibleHeight;
  @Px
  private int visibleWidth;

  @Px
  private int viewportHeight;
  @Px
  private int viewportWidth;

  private boolean fullyVisible = false;
  private boolean visible = false;
  private boolean focusedVisible = false;

  /** Store last value for de-duping */
  private int lastVisibleHeightNotified = NOT_NOTIFIED;
  private int lastVisibleWidthNotified = NOT_NOTIFIED;

  EpoxyVisibilityItem(int adapterPosition) {
    reset(adapterPosition);
  }

  /**
   * Update the visibility item according the current layout.
   *
   * @param view        the current {@link com.airbnb.epoxy.EpoxyViewHolder}'s itemView
   * @param parent      the {@link androidx.recyclerview.widget.RecyclerView}
   * @return true if the view has been measured
   */
  boolean update(@NonNull View view, @NonNull RecyclerView parent, boolean detachEvent) {
    // Clear the rect before calling getLocalVisibleRect
    localVisibleRect.setEmpty();
    boolean visibleRect = view.getLocalVisibleRect(localVisibleRect);
    height = view.getMeasuredHeight();
    width = view.getMeasuredWidth();
    viewportHeight = parent.getMeasuredHeight();
    viewportWidth = parent.getMeasuredWidth();
    visibleHeight = detachEvent || !visibleRect ? 0 : localVisibleRect.height();
    visibleWidth = detachEvent || !visibleRect ? 0 : localVisibleRect.width();
    percentVisibleHeight = detachEvent || !visibleRect ? 0 : 100.f / height * visibleHeight;
    percentVisibleWidth = detachEvent || !visibleRect ? 0 : 100.f / width * visibleWidth;
    if (visibleHeight != height || visibleWidth != width) {
      fullyVisible = false;
    }
    return height > 0 && width > 0;
  }

  int getAdapterPosition() {
    return adapterPosition;
  }

  void reset(int newAdapterPosition) {
    fullyVisible = false;
    visible = false;
    focusedVisible = false;
    adapterPosition = newAdapterPosition;
    lastVisibleHeightNotified = NOT_NOTIFIED;
    lastVisibleWidthNotified = NOT_NOTIFIED;
  }

  void handleVisible(@NonNull EpoxyViewHolder epoxyHolder, boolean detachEvent) {
    if (visible && checkAndUpdateInvisible(detachEvent)) {
      epoxyHolder.visibilityStateChanged(VisibilityState.INVISIBLE);
    } else if (!visible && checkAndUpdateVisible()) {
      epoxyHolder.visibilityStateChanged(VisibilityState.VISIBLE);
    }
  }

  void handleFocus(EpoxyViewHolder epoxyHolder, boolean detachEvent) {
    if (focusedVisible && checkAndUpdateUnfocusedVisible(detachEvent)) {
      epoxyHolder.visibilityStateChanged(VisibilityState.UNFOCUSED_VISIBLE);
    } else if (!focusedVisible && checkAndUpdateFocusedVisible()) {
      epoxyHolder.visibilityStateChanged(VisibilityState.FOCUSED_VISIBLE);
    }
  }

  void handleFullImpressionVisible(EpoxyViewHolder epoxyHolder, boolean detachEvent) {
    if (!fullyVisible && checkAndUpdateFullImpressionVisible()) {
      epoxyHolder
          .visibilityStateChanged(VisibilityState.FULL_IMPRESSION_VISIBLE);
    }
  }

  void handleChanged(EpoxyViewHolder epoxyHolder) {
    if (visibleHeight != lastVisibleHeightNotified || visibleWidth != lastVisibleWidthNotified) {
        epoxyHolder.visibilityChanged(percentVisibleHeight, percentVisibleWidth, visibleHeight,
            visibleWidth);
      lastVisibleHeightNotified = visibleHeight;
      lastVisibleWidthNotified = visibleWidth;
    }
  }

  /**
   * @return true when at least one pixel of the component is visible
   */
  private boolean checkAndUpdateVisible() {
    return visible = visibleHeight > 0 && visibleWidth > 0;
  }

  /**
   * @param detachEvent true if initiated from detach event
   * @return true when when the component no longer has any pixels on the screen
   */
  private boolean checkAndUpdateInvisible(boolean detachEvent) {
    boolean invisible = visibleHeight <= 0 && visibleWidth <= 0 || detachEvent;
    if (invisible) {
      visible = false;
    }
    return !visible;
  }

  /**
   * @return true when either the component occupies at least half of the viewport, or, if the
   * component is smaller than half the viewport, when it is fully visible.
   */
  private boolean checkAndUpdateFocusedVisible() {
    boolean focusedVisibleHeight = height >= viewportHeight / 2 || (visibleHeight == height
        && height < viewportHeight / 2);
    boolean focusedVisibleWidth = width >= viewportWidth / 2 || (visibleWidth == width
        && width < viewportWidth / 2);
    return focusedVisible = focusedVisibleHeight && focusedVisibleWidth;
  }

  /**
   * @param detachEvent true if initiated from detach event
   * @return true when the component is no longer focused, i.e. it is not fully visible and does
   * not occupy at least half the viewport.
   */
  private boolean checkAndUpdateUnfocusedVisible(boolean detachEvent) {
    boolean unfocusedVisibleHeight = !(height >= viewportHeight / 2 || (
        visibleHeight == height && height < viewportHeight / 2));
    boolean unfocusedVisibleWidth = !(width >= viewportWidth / 2 || (
        viewportWidth == width && width < viewportWidth / 2));
    boolean unfocusedVisible = detachEvent || unfocusedVisibleHeight || unfocusedVisibleWidth;
    if (unfocusedVisible) {
      focusedVisible = false;
    }
    return !focusedVisible;
  }

  /**
   * @return true when the entire component has passed through the viewport at some point.
   */
  private boolean checkAndUpdateFullImpressionVisible() {
    return fullyVisible = visibleHeight == height && visibleWidth == width;
  }

  void shiftBy(int offsetPosition) {
    adapterPosition += offsetPosition;
  }
}
