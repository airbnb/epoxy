package com.airbnb.epoxy;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.Px;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.State;

/**
 * Modifies item spacing in a recycler view so that items are equally spaced no matter where they
 * are on the grid. Only designed to work with standard linear or grid layout managers.
 */
public class EpoxyItemSpacingDecorator extends RecyclerView.ItemDecoration {
  private int pxBetweenItems;
  private boolean verticallyScrolling;
  private boolean horizontallyScrolling;
  private boolean firstItem;
  private boolean lastItem;
  private boolean grid;

  private boolean isFirstItemInRow;
  private boolean fillsLastSpan;
  private boolean isInFirstRow;
  private boolean isInLastRow;

  public EpoxyItemSpacingDecorator() {
    this(0);
  }

  public EpoxyItemSpacingDecorator(@Px int pxBetweenItems) {
    setPxBetweenItems(pxBetweenItems);
  }

  public void setPxBetweenItems(@Px int pxBetweenItems) {
    this.pxBetweenItems = pxBetweenItems;
  }

  @Px
  public int getPxBetweenItems() {
    return pxBetweenItems;
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
    // Zero everything out for the common case
    outRect.setEmpty();

    int position = parent.getChildAdapterPosition(view);
    if (position == RecyclerView.NO_POSITION) {
      // View is not shown
      return;
    }

    RecyclerView.LayoutManager layout = parent.getLayoutManager();
    calculatePositionDetails(parent, position, layout);

    boolean left = useLeftPadding();
    boolean top = useTopPadding();

    if (shouldReverseLayout(layout, horizontallyScrolling)) {
      if (horizontallyScrolling) {
        left = useRightPadding();
      } else {
        top = useBottomPadding();
      }
    }

    outRect.left = left ? pxBetweenItems : 0;
    outRect.top = top ? pxBetweenItems : 0;
  }

  private void calculatePositionDetails(RecyclerView parent, int position, LayoutManager layout) {
    int itemCount = parent.getAdapter().getItemCount();
    firstItem = position == 0;
    lastItem = position == itemCount - 1;
    horizontallyScrolling = layout.canScrollHorizontally();
    verticallyScrolling = layout.canScrollVertically();
    grid = layout instanceof GridLayoutManager;

    if (grid) {
      GridLayoutManager grid = (GridLayoutManager) layout;
      final SpanSizeLookup spanSizeLookup = grid.getSpanSizeLookup();
      int spanSize = spanSizeLookup.getSpanSize(position);
      int spanCount = grid.getSpanCount();
      int spanIndex = spanSizeLookup.getSpanIndex(position, spanCount);
      isFirstItemInRow = spanIndex == 0;
      fillsLastSpan = spanIndex + spanSize == spanCount;
      isInFirstRow = isInFirstRow(position, spanSizeLookup, spanCount);
      isInLastRow =
          !isInFirstRow && isInLastRow(position, itemCount, spanSizeLookup, spanCount);
    }
  }

  private static boolean shouldReverseLayout(LayoutManager layout, boolean horizontallyScrolling) {
    boolean reverseLayout =
        layout instanceof LinearLayoutManager && ((LinearLayoutManager) layout).getReverseLayout();
    boolean rtl = layout.getLayoutDirection() == ViewCompat.LAYOUT_DIRECTION_RTL;
    if (horizontallyScrolling && rtl) {
      // This is how linearlayout checks if it should reverse layout in #resolveShouldLayoutReverse
      reverseLayout = !reverseLayout;
    }

    return reverseLayout;
  }

  private boolean useBottomPadding() {
    if (grid) {
      return (horizontallyScrolling && !fillsLastSpan)
          || (verticallyScrolling && !isInLastRow);
    }

    return verticallyScrolling && !lastItem;
  }

  private boolean useTopPadding() {
    if (grid) {
      return (horizontallyScrolling && !isFirstItemInRow)
          || (verticallyScrolling && !isInFirstRow);
    }

    return verticallyScrolling && !firstItem;
  }

  private boolean useRightPadding() {
    if (grid) {
      return (horizontallyScrolling && !isInLastRow)
          || (verticallyScrolling && !fillsLastSpan);
    }

    return horizontallyScrolling && !lastItem;
  }

  private boolean useLeftPadding() {
    if (grid) {
      return (horizontallyScrolling && !isInFirstRow)
          || (verticallyScrolling && !isFirstItemInRow);
    }

    return horizontallyScrolling && !firstItem;
  }

  private static boolean isInFirstRow(int position, SpanSizeLookup spanSizeLookup, int spanCount) {
    int totalSpan = 0;
    for (int i = 0; i <= position; i++) {
      totalSpan += spanSizeLookup.getSpanSize(i);
      if (totalSpan > spanCount) {
        return false;
      }
    }

    return true;
  }

  private static boolean isInLastRow(int position, int itemCount, SpanSizeLookup spanSizeLookup,
      int spanCount) {
    int totalSpan = 0;
    for (int i = itemCount - 1; i >= position; i--) {
      totalSpan += spanSizeLookup.getSpanSize(i);
      if (totalSpan > spanCount) {
        return false;
      }
    }

    return true;
  }
}
