package com.airbnb.epoxy;

import android.content.Context;
import android.support.annotation.DimenRes;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.SnapHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.epoxy.ModelView.Size;
import com.airbnb.viewmodeladapter.R;

import java.util.List;

/**
 * <i>This feature is in Beta - please report bugs, feature requests, or other feedback at
 * https://github.com/airbnb/epoxy by creating a new issue. Thanks!</i>
 * <p>
 * This is intended as a plug and play "Carousel" view - a Recyclerview with horizontal scrolling.
 * It comes with common defaults and performance optimizations and can be either used as a top level
 * RecyclerView, or nested within a vertical recyclerview.
 * <p>
 * This class provides:
 * <p>
 * 1. Automatic integration with Epoxy. A {@link CarouselModel_} is generated from this class, which
 * you can use in your EpoxyController. Just call {@link #setModels(List)} to provide the list of
 * models to show in the carousel.
 * <p>
 * 2. Default padding for carousel peeking, and an easy way to change this padding - {@link
 * #setPaddingDp(int)}
 * <p>
 * 3. Easily control how many items are shown on screen in the carousel at a time - {@link
 * #setNumViewsToShowOnScreen(float)}
 * <p>
 * 4. Easy snap support. By default a {@link LinearSnapHelper} is used, but you can set a global
 * default for all Carousels with {@link #setDefaultGlobalSnapHelperFactory(SnapHelperFactory)}
 * <p>
 * 5. All of the benefits of {@link EpoxyRecyclerView}
 * <p>
 * If you need further flexibility you can subclass this view to change its width, height, scrolling
 * direction, etc. You can annotate a subclass with {@link ModelView} to generate a new EpoxyModel.
 */
@ModelView(saveViewState = true, autoLayout = Size.MATCH_WIDTH_WRAP_HEIGHT)
public class Carousel extends EpoxyRecyclerView {
  public static final int NO_VALUE_SET = -1;

  private static SnapHelperFactory defaultGlobalSnapHelperFactory = new SnapHelperFactory() {

    @Override
    @NonNull
    public SnapHelper buildSnapHelper(Context context) {
      return new LinearSnapHelper();
    }
  };

  @Dimension(unit = Dimension.DP)
  private static int defaultSpacingBetweenItemsDp = 8;
  private float numViewsToShowOnScreen;

  public Carousel(Context context) {
    super(context);
  }

  public Carousel(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public Carousel(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void init() {
    super.init();
    // When used as a model the padding can't be set via xml so we set it programmatically
    int defaultSpacingDp = getDefaultSpacingBetweenItemsDp();

    if (defaultSpacingDp >= 0) {
      setItemSpacingDp(defaultSpacingDp);

      if (getPaddingLeft() == 0
          && getPaddingRight() == 0
          && getPaddingTop() == 0
          && getPaddingBottom() == 0) {
        // Use the item spacing as the default padding if no other padding has been set
        setPaddingDp(defaultSpacingDp);
      }
    }

    SnapHelperFactory snapHelperFactory = getSnapHelperFactory();
    if (snapHelperFactory != null) {
      snapHelperFactory.buildSnapHelper(getContext()).attachToRecyclerView(this);
    }
  }

  /**
   * Return a {@link SnapHelperFactory} instance to use with this Carousel. The {@link SnapHelper}
   * created by the factory will be attached to this Carousel on view creation. Return null for no
   * snap helper to be attached automatically.
   */
  @Nullable
  protected SnapHelperFactory getSnapHelperFactory() {
    return defaultGlobalSnapHelperFactory;
  }

  /**
   * Set a {@link SnapHelperFactory} instance to use with all Carousels by default. The {@link
   * SnapHelper} created by the factory will be attached to each Carousel on view creation. Set null
   * for no snap helper to be attached automatically.
   * <p>
   * A Carousel subclass can implement {@link #getSnapHelperFactory()} to override the global
   * default.
   */
  public static void setDefaultGlobalSnapHelperFactory(@Nullable SnapHelperFactory factory) {
    defaultGlobalSnapHelperFactory = factory;
  }

  @ModelProp
  @Override
  public void setHasFixedSize(boolean hasFixedSize) {
    super.setHasFixedSize(hasFixedSize);
  }

  /**
   * Set the number of views to show on screen in this carousel at a time, partial numbers are
   * allowed.
   * <p>
   * This is useful where you want to easily control for the number of items on screen, regardless
   * of screen size. For example, you could set this to 1.2f so that one view is shown in full and
   * 20% of the next view "peeks" from the edge to indicate that there is more content to scroll
   * to.
   * <p>
   * Another pattern is setting a different view count depending on whether the device is phone or
   * tablet.
   * <p>
   * Additionally, if a LinearLayoutManager is used this value will be forwarded to {@link
   * LinearLayoutManager#setInitialPrefetchItemCount(int)} as a performance optimization.
   * <p>
   * If you want to only change the prefetch count without changing the view size you can simply use
   * {@link #setInitialPrefetchItemCount(int)}
   */
  @ModelProp(group = "prefetch")
  public void setNumViewsToShowOnScreen(float viewCount) {
    numViewsToShowOnScreen = viewCount;
    setInitialPrefetchItemCount((int) Math.ceil(viewCount));
  }

  /**
   * If you are using a Linear or Grid layout manager you can use this to set the item prefetch
   * count. Only use this if you are not using {@link #setNumViewsToShowOnScreen(float)}
   *
   * @see #setNumViewsToShowOnScreen(float)
   * @see LinearLayoutManager#setInitialPrefetchItemCount(int)
   */
  @ModelProp(group = "prefetch")
  public void setInitialPrefetchItemCount(int numItemsToPrefetch) {
    if (numItemsToPrefetch < 0) {
      throw new IllegalStateException("numItemsToPrefetch must be greater than 0");
    }

    // Use the linearlayoutmanager default of 2 if the user did not specify one
    int prefetchCount = numItemsToPrefetch == 0 ? 2 : numItemsToPrefetch;

    LayoutManager layoutManager = getLayoutManager();
    if (layoutManager instanceof LinearLayoutManager) {
      ((LinearLayoutManager) layoutManager).setInitialPrefetchItemCount(prefetchCount);
    }
  }

  @Override
  public void onChildAttachedToWindow(View child) {
    if (numViewsToShowOnScreen > 0) {
      ViewGroup.LayoutParams childLayoutParams = child.getLayoutParams();
      child.setTag(R.id.epoxy_recycler_view_child_initial_size_id, childLayoutParams.width);

      int itemSpacingPx = spacingDecorator.getPxBetweenItems();
      int spaceBetweenItems = 0;
      if (itemSpacingPx > 0) {
        // The item decoration space is not counted in the width of the view
        spaceBetweenItems = (int) (itemSpacingPx * numViewsToShowOnScreen);
      }

      boolean isScrollingHorizontally = getLayoutManager().canScrollHorizontally();
      int itemSizeInScrollingDirection =
          (int) ((getSpaceForChildren(isScrollingHorizontally) - spaceBetweenItems)
              / numViewsToShowOnScreen);

      if (isScrollingHorizontally) {
        childLayoutParams.width = itemSizeInScrollingDirection;
      } else {
        childLayoutParams.height = itemSizeInScrollingDirection;
      }

      // We don't need to request layout because the layout manager will do that for us next
    }
  }

  private int getSpaceForChildren(boolean horizontal) {
    if (horizontal) {
      return getTotalWidthPx(this)
          - getPaddingLeft()
          - (getClipToPadding() ? getPaddingRight() : 0);
      // If child views will be showing through padding than we include just one side of padding
      // since when the list is at position 0 only the child towards the end of the list will show
      // through the padding.
    } else {
      return getTotalHeightPx(this)
          - getPaddingTop()
          - (getClipToPadding() ? getPaddingBottom() : 0);
    }
  }

  @Px
  private static int getTotalWidthPx(View view) {
    if (view.getWidth() > 0) {
      // Can only get a width if we are laid out
      return view.getWidth();
    }

    // Fall back to assuming we want the full screen width
    DisplayMetrics metrics = view.getContext().getResources().getDisplayMetrics();
    return metrics.widthPixels;
  }

  @Px
  private static int getTotalHeightPx(View view) {
    if (view.getHeight() > 0) {
      return view.getHeight();
    }

    // Fall back to assuming we want the full screen width
    DisplayMetrics metrics = view.getContext().getResources().getDisplayMetrics();
    return metrics.heightPixels;
  }

  @Override
  public void onChildDetachedFromWindow(View child) {
    // Restore the view width that existed before we modified it
    Object initialWidth = child.getTag(R.id.epoxy_recycler_view_child_initial_size_id);

    if (initialWidth instanceof Integer) {
      ViewGroup.LayoutParams params = child.getLayoutParams();
      params.width = (int) initialWidth;
      child.setTag(R.id.epoxy_recycler_view_child_initial_size_id, null);
      // No need to request layout since the view is unbound and not attached to window
    }
  }

  /**
   * Set a global default to use as the item spacing for all Carousels. Set to 0 for no item
   * spacing.
   */
  public static void setDefaultItemSpacingDp(@Dimension(unit = Dimension.DP) int dp) {
    defaultSpacingBetweenItemsDp = dp;
  }

  /**
   * Return the item spacing to use in this carousel, or 0 for no spacing.
   * <p>
   * By default this uses the global default set in {@link #setDefaultItemSpacingDp(int)}, but
   * subclasses can override this to specify their own value.
   */
  @Dimension(unit = Dimension.DP)
  protected int getDefaultSpacingBetweenItemsDp() {
    return defaultSpacingBetweenItemsDp;
  }

  /**
   * Set a dimension resource to specify the padding value to use on each side of the carousel and
   * in between carousel items.
   */
  @ModelProp(group = "padding")
  public void setPaddingRes(@DimenRes int paddingRes) {
    int px = resToPx(paddingRes);
    setPadding(px, px, px, px);
    setItemSpacingPx(px);
  }

  /**
   * Set a DP value to use as the padding on each side of the carousel and in between carousel
   * items.
   * <p>
   * The default as the value returned by {@link #getDefaultSpacingBetweenItemsDp()}
   */
  @ModelProp(defaultValue = "NO_VALUE_SET", group = "padding")
  public void setPaddingDp(@Dimension(unit = Dimension.DP) int paddingDp) {
    int px = dpToPx(paddingDp != NO_VALUE_SET ? paddingDp : getDefaultSpacingBetweenItemsDp());
    setPadding(px, px, px, px);
    setItemSpacingPx(px);
  }

  /**
   * Use the {@link Padding} class to specify individual padding values for each side of the
   * carousel, as well as item spacing.
   * <p>
   * A value of null will set all padding and item spacing to 0.
   */
  @ModelProp(group = "padding")
  public void setPadding(@Nullable Padding padding) {
    if (padding == null) {
      setPaddingDp(0);
    } else {
      setPadding(padding.leftPx, padding.topPx, padding.rightPx, padding.bottomPx);
      setItemSpacingPx(padding.itemSpacingPx);
    }
  }

  /**
   * Used to specify individual padding values programmatically.
   *
   * @see #setPadding(Padding)
   */
  public static class Padding {
    @Px public final int topPx;
    @Px public final int bottomPx;
    @Px public final int leftPx;
    @Px public final int rightPx;
    @Px public final int itemSpacingPx;

    /**
     * @param paddingPx     Padding in pixels to add on all sides of the carousel
     * @param itemSpacingPx Space in pixels to add between each carousel item. Will be implemented
     *                      via an item decoration.
     */
    public Padding(@Px int paddingPx, @Px int itemSpacingPx) {
      this(paddingPx, paddingPx, paddingPx, paddingPx, itemSpacingPx);
    }

    /**
     * @param topPx         Top padding in pixels.
     * @param bottomPx      Bottom padding in pixels.
     * @param leftPx        Left padding in pixels.
     * @param rightPx       Right padding in pixels.
     * @param itemSpacingPx Space in pixels to add between each carousel item. Will be implemented
     *                      via an item decoration.
     */
    public Padding(@Px int topPx, @Px int bottomPx, @Px int leftPx, @Px int rightPx,
        @Px int itemSpacingPx) {

      this.topPx = topPx;
      this.bottomPx = bottomPx;
      this.leftPx = leftPx;
      this.rightPx = rightPx;
      this.itemSpacingPx = itemSpacingPx;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      Padding padding = (Padding) o;

      if (topPx != padding.topPx) {
        return false;
      }
      if (bottomPx != padding.bottomPx) {
        return false;
      }
      if (leftPx != padding.leftPx) {
        return false;
      }
      if (rightPx != padding.rightPx) {
        return false;
      }
      return itemSpacingPx == padding.itemSpacingPx;
    }

    @Override
    public int hashCode() {
      int result = topPx;
      result = 31 * result + bottomPx;
      result = 31 * result + leftPx;
      result = 31 * result + rightPx;
      result = 31 * result + itemSpacingPx;
      return result;
    }
  }

  @ModelProp
  public void setModels(@NonNull List<? extends EpoxyModel<?>> models) {
    super.setModels(models);
  }

  @OnViewRecycled
  public void clear() {
    super.clear();
  }

  /** Provide a SnapHelper implementation you want to use with a Carousel. */
  public abstract static class SnapHelperFactory {
    /**
     * Create and return a new instance of a {@link android.support.v7.widget.SnapHelper} for use
     * with a Carousel.
     */
    @NonNull
    public abstract SnapHelper buildSnapHelper(Context context);
  }
}
