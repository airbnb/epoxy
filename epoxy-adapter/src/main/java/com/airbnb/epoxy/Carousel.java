package com.airbnb.epoxy;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.epoxy.ModelView.Size;
import com.airbnb.viewmodeladapter.R;

import java.util.List;

import androidx.annotation.DimenRes;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

/**
 * <i>This feature is in Beta - please report bugs, feature requests, or other feedback at
 * https://github.com/airbnb/epoxy by creating a new issue. Thanks!</i>
 *
 * <p>This is intended as a plug and play "Carousel" view - a Recyclerview with horizontal
 * scrolling. It comes with common defaults and performance optimizations and can be either used as
 * a top level RecyclerView, or nested within a vertical recyclerview.
 *
 * <p>This class provides:
 *
 * <p>1. Automatic integration with Epoxy. A {@link CarouselModel_} is generated from this class,
 * which you can use in your EpoxyController. Just call {@link #setModels(List)} to provide the list
 * of models to show in the carousel.
 *
 * <p>2. Default padding for carousel peeking, and an easy way to change this padding - {@link
 * #setPaddingDp(int)}
 *
 * <p>3. Easily control how many items are shown on screen in the carousel at a time - {@link
 * #setNumViewsToShowOnScreen(float)}
 *
 * <p>4. Easy snap support. By default a {@link LinearSnapHelper} is used, but you can set a global
 * default for all Carousels with {@link #setDefaultGlobalSnapHelperFactory(SnapHelperFactory)}
 *
 * <p>5. All of the benefits of {@link EpoxyRecyclerView}
 *
 * <p>If you need further flexibility you can subclass this view to change its width, height,
 * scrolling direction, etc. You can annotate a subclass with {@link ModelView} to generate a new
 * EpoxyModel.
 */
@ModelView(saveViewState = true, autoLayout = Size.MATCH_WIDTH_WRAP_HEIGHT)
public class Carousel extends EpoxyRecyclerView {
  public static final int NO_VALUE_SET = -1;

  private static SnapHelperFactory defaultGlobalSnapHelperFactory =
      new SnapHelperFactory() {

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

    // Carousels will be detached when their parent recyclerview is
    setRemoveAdapterWhenDetachedFromWindow(false);
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
   *
   * <p>A Carousel subclass can implement {@link #getSnapHelperFactory()} to override the global
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
   *
   * <p>This is useful where you want to easily control for the number of items on screen,
   * regardless of screen size. For example, you could set this to 1.2f so that one view is shown in
   * full and 20% of the next view "peeks" from the edge to indicate that there is more content to
   * scroll to.
   *
   * <p>Another pattern is setting a different view count depending on whether the device is phone
   * or tablet.
   *
   * <p>Additionally, if a LinearLayoutManager is used this value will be forwarded to {@link
   * LinearLayoutManager#setInitialPrefetchItemCount(int)} as a performance optimization.
   *
   * <p>If you want to only change the prefetch count without changing the view size you can simply
   * use {@link #setInitialPrefetchItemCount(int)}
   */
  @ModelProp(group = "prefetch")
  public void setNumViewsToShowOnScreen(float viewCount) {
    numViewsToShowOnScreen = viewCount;
    setInitialPrefetchItemCount((int) Math.ceil(viewCount));
  }

  /**
   * @return The number of views to show on screen in this carousel at a time.
   */
  public float getNumViewsToShowOnScreen() {
      return numViewsToShowOnScreen;
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

      int itemSpacingPx = getSpacingDecorator().getPxBetweenItems();
      int spaceBetweenItems = 0;
      if (itemSpacingPx > 0) {
        // The item decoration space is not counted in the width of the view
        spaceBetweenItems = (int) (itemSpacingPx * numViewsToShowOnScreen);
      }

      boolean isScrollingHorizontally = getLayoutManager().canScrollHorizontally();
      int itemSizeInScrollingDirection =
          (int)
              ((getSpaceForChildren(isScrollingHorizontally) - spaceBetweenItems)
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

    if (view.getMeasuredWidth() > 0) {
      return view.getMeasuredWidth();
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

    if (view.getMeasuredHeight() > 0) {
      return view.getMeasuredHeight();
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
   *
   * <p>By default this uses the global default set in {@link #setDefaultItemSpacingDp(int)}, but
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
   *
   * <p>The default as the value returned by {@link #getDefaultSpacingBetweenItemsDp()}
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
   *
   * <p>A value of null will set all padding and item spacing to 0.
   */
  @ModelProp(group = "padding")
  public void setPadding(@Nullable Padding padding) {
    if (padding == null) {
      setPaddingDp(0);
    } else if (padding.paddingType == Padding.PaddingType.PX) {
      setPadding(padding.left, padding.top, padding.right, padding.bottom);
      setItemSpacingPx(padding.itemSpacing);
    } else if (padding.paddingType == Padding.PaddingType.DP) {
      setPadding(
          dpToPx(padding.left), dpToPx(padding.top), dpToPx(padding.right), dpToPx(padding.bottom));
      setItemSpacingPx(dpToPx(padding.itemSpacing));
    } else if (padding.paddingType == Padding.PaddingType.RESOURCE) {
      setPadding(
          resToPx(padding.left),
          resToPx(padding.top),
          resToPx(padding.right),
          resToPx(padding.bottom));
      setItemSpacingPx(resToPx(padding.itemSpacing));
    }
  }

  /**
   * Used to specify individual padding values programmatically.
   *
   * @see #setPadding(Padding)
   */
  public static class Padding {
    public final int left;
    public final int top;
    public final int right;
    public final int bottom;
    public final int itemSpacing;
    public final PaddingType paddingType;

    enum PaddingType {
      PX,
      DP,
      RESOURCE
    }

    /**
     * @param paddingRes Padding as dimension resource.
     * @param itemSpacingRes Space as dimension resource to add between each carousel item. Will be
     *     implemented via an item decoration.
     */
    public static Padding resource(@DimenRes int paddingRes, @DimenRes int itemSpacingRes) {
      return new Padding(
          paddingRes, paddingRes, paddingRes, paddingRes, itemSpacingRes, PaddingType.RESOURCE);
    }

    /**
     * @param leftRes Left padding as dimension resource.
     * @param topRes Top padding as dimension resource.
     * @param rightRes Right padding as dimension resource.
     * @param bottomRes Bottom padding as dimension resource.
     * @param itemSpacingRes Space as dimension resource to add between each carousel item. Will be
     *     implemented via an item decoration.
     */
    public static Padding resource(
        @DimenRes int leftRes,
        @DimenRes int topRes,
        @DimenRes int rightRes,
        @DimenRes int bottomRes,
        @DimenRes int itemSpacingRes) {
      return new Padding(
          leftRes, topRes, rightRes, bottomRes, itemSpacingRes, PaddingType.RESOURCE);
    }

    /**
     * @param paddingDp Padding in dp.
     * @param itemSpacingDp Space in dp to add between each carousel item. Will be implemented via
     *     an item decoration.
     */
    public static Padding dp(
        @Dimension(unit = Dimension.DP) int paddingDp,
        @Dimension(unit = Dimension.DP) int itemSpacingDp) {
      return new Padding(paddingDp, paddingDp, paddingDp, paddingDp, itemSpacingDp, PaddingType.DP);
    }

    /**
     * @param leftDp Left padding in dp.
     * @param topDp Top padding in dp.
     * @param rightDp Right padding in dp.
     * @param bottomDp Bottom padding in dp.
     * @param itemSpacingDp Space in dp to add between each carousel item. Will be implemented via
     *     an item decoration.
     */
    public static Padding dp(
        @Dimension(unit = Dimension.DP) int leftDp,
        @Dimension(unit = Dimension.DP) int topDp,
        @Dimension(unit = Dimension.DP) int rightDp,
        @Dimension(unit = Dimension.DP) int bottomDp,
        @Dimension(unit = Dimension.DP) int itemSpacingDp) {
      return new Padding(leftDp, topDp, rightDp, bottomDp, itemSpacingDp, PaddingType.DP);
    }

    /**
     * @param paddingPx Padding in pixels to add on all sides of the carousel
     * @param itemSpacingPx Space in pixels to add between each carousel item. Will be implemented
     *     via an item decoration.
     */
    public Padding(@Px int paddingPx, @Px int itemSpacingPx) {
      this(paddingPx, paddingPx, paddingPx, paddingPx, itemSpacingPx, PaddingType.PX);
    }

    /**
     * @param leftPx Left padding in pixels.
     * @param topPx Top padding in pixels.
     * @param rightPx Right padding in pixels.
     * @param bottomPx Bottom padding in pixels.
     * @param itemSpacingPx Space in pixels to add between each carousel item. Will be implemented
     *     via an item decoration.
     */
    public Padding(
        @Px int leftPx, @Px int topPx, @Px int rightPx, @Px int bottomPx, @Px int itemSpacingPx) {
      this(leftPx, topPx, rightPx, bottomPx, itemSpacingPx, PaddingType.PX);
    }

    /**
     * @param left Left padding.
     * @param top Top padding.
     * @param right Right padding.
     * @param bottom Bottom padding.
     * @param itemSpacing Space to add between each carousel item. Will be implemented via an item
     *     decoration.
     * @param paddingType Unit / Type of the given paddings/ itemspacing.
     */
    private Padding(
        int left, int top, int right, int bottom, int itemSpacing, PaddingType paddingType) {

      this.left = left;
      this.top = top;
      this.right = right;
      this.bottom = bottom;
      this.itemSpacing = itemSpacing;
      this.paddingType = paddingType;
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

      if (left != padding.left) {
        return false;
      }
      if (top != padding.top) {
        return false;
      }
      if (right != padding.right) {
        return false;
      }
      if (bottom != padding.bottom) {
        return false;
      }
      return itemSpacing == padding.itemSpacing;
    }

    @Override
    public int hashCode() {
      int result = left;
      result = 31 * result + top;
      result = 31 * result + right;
      result = 31 * result + bottom;
      result = 31 * result + itemSpacing;
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
     * Create and return a new instance of a {@link androidx.recyclerview.widget.SnapHelper} for use
     * with a Carousel.
     */
    @NonNull
    public abstract SnapHelper buildSnapHelper(Context context);
  }
}
