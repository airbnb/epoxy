package com.airbnb.epoxy;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.annotation.CallSuper;
import android.support.annotation.DimenRes;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.ViewGroup;

import com.airbnb.viewmodeladapter.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * <i>This feature is in Beta - please report bugs, feature requests, or other feedback at
 * https://github.com/airbnb/epoxy by creating a new issue. Thanks!</i>
 * <p>
 * A RecyclerView implementation that makes for easier integration with Epoxy. The goal of this
 * class is to reduce boilerplate in setting up a RecyclerView by applying common defaults.
 * Additionally, several performance optimizations are made.
 * <p>
 * Improvements in this class are:
 * <p>
 * 1. A single view pool is automatically shared between all {@link EpoxyRecyclerView} instances in
 * the same activity. This should increase view recycling potential and increase performance when
 * nested RecyclerViews are used. See {@link #initViewPool()}.
 * <p>
 * 2. A layout manager is automatically added with assumed defaults. See {@link
 * #createLayoutManager()}
 * <p>
 * 3. Fixed size is enabled if this view's size is MATCH_PARENT
 * <p>
 * 4. If a {@link GridLayoutManager} is used this will automatically sync the span count with the
 * {@link EpoxyController}. See {@link #syncSpanCount()}
 * <p>
 * 5. Helper methods like {@link #setModels(List)}, {@link #buildModelsWith(ModelBuilderCallback)}
 * make it simpler to set up simple RecyclerViews.
 * <p>
 * 6. Set an EpoxyController and build models in one step -
 * {@link #setControllerAndBuildModels(EpoxyController)}
 * (EpoxyController)}
 * <p>
 * 7. Support for automatic item spacing. See {@link #setItemSpacingPx}
 * <p>
 * 8. Defaults for usage as a nested recyclerview are provided in {@link Carousel}.
 * <p>
 * 9. {@link #setClipToPadding(boolean)} is set to false by default since that behavior is commonly
 * desired in a scrolling list
 */
public class EpoxyRecyclerView extends RecyclerView {

  private static final int DEFAULT_ADAPTER_REMOVAL_DELAY_MS = 2000;

  /**
   * Store one unique pool per activity. They are cleared out when activities are destroyed, so this
   * only needs to hold pools for active activities.
   */
  private static final List<PoolReference> RECYCLER_POOLS = new ArrayList<>(5);

  protected final EpoxyItemSpacingDecorator spacingDecorator = new EpoxyItemSpacingDecorator();

  private EpoxyController epoxyController;

  /**
   * The adapter that was removed because the RecyclerView was detached from the window. We save it
   * so we can reattach it if the RecyclerView is reattached to window. This allows us to
   * automatically restore the adapter, without risking leaking the RecyclerView if this view is
   * never used again.
   * <p>
   * Since the adapter is removed this recyclerview won't get adapter changes, but that's fine since
   * the view isn't attached to window and isn't being drawn.
   * <p>
   * This reference is cleared if another adapter is manually set, so we don't override the user's
   * adapter choice.
   *
   * @see #setRemoveAdapterWhenDetachedFromWindow(boolean)
   */
  private RecyclerView.Adapter removedAdapter;

  private boolean removeAdapterWhenDetachedFromWindow = true;

  /**
   * If set to true, any adapter set on this recyclerview will be removed when this view is detached
   * from the window. This is useful to prevent leaking a reference to this RecyclerView. This is
   * useful in cases where the same adapter can be used across multiple views (views which can be
   * destroyed and recreated), such as with fragments. In that case the adapter is not necessarily
   * cleared from previous RecyclerViews, so the adapter will continue to hold a reference to those
   * views and leak them. More details at https://github
   * .com/airbnb/epoxy/wiki/Avoiding-Memory-Leaks#parent-view
   * <p>
   * The default is true, but you can disable this if you don't want your adapter detached
   * automatically.
   * <p>
   * If the adapter is removed via this setting, it will be re-set on the RecyclerView if the
   * RecyclerView is re-attached to the window at a later point.
   */
  public void setRemoveAdapterWhenDetachedFromWindow(boolean removeAdapterWhenDetachedFromWindow) {
    this.removeAdapterWhenDetachedFromWindow = removeAdapterWhenDetachedFromWindow;
  }

  private int delayMsWhenRemovingAdapterOnDetach = DEFAULT_ADAPTER_REMOVAL_DELAY_MS;

  /**
   * If {@link #setRemoveAdapterWhenDetachedFromWindow(boolean)} is set to true, this is the delay
   * in milliseconds between when {@link #onDetachedFromWindow()} is called and when the adapter is
   * actually removed.
   * <p>
   * By default a delay of {@value #DEFAULT_ADAPTER_REMOVAL_DELAY_MS} ms is used so that view
   * transitions can complete before the adapter is removed. Otherwise if the adapter is removed
   * before transitions finish it can clear the screen and break the transition. A notable case is
   * fragment transitions, in which the fragment view is detached from window before the transition
   * ends.
   */
  public void setDelayMsWhenRemovingAdapterOnDetach(int delayMsWhenRemovingAdapterOnDetach) {
    this.delayMsWhenRemovingAdapterOnDetach = delayMsWhenRemovingAdapterOnDetach;
  }

  /**
   * Tracks whether {@link #removeAdapterRunnable} has been posted to run
   * later. This lets us know if we should cancel the runnable at certain times. This removes the
   * overhead of needlessly attemping to remove the runnable when it isn't posted.
   */
  private boolean isRemoveAdapterRunnablePosted;
  private final Runnable removeAdapterRunnable = new Runnable() {
    @Override
    public void run() {
      if (isRemoveAdapterRunnablePosted) {
        // Canceling a runnable doesn't work accurately when a view switches between
        // attached/detached, so we manually check that this should still be run
        isRemoveAdapterRunnablePosted = false;
        removeAdapter();
      }
    }
  };

  public EpoxyRecyclerView(Context context) {
    this(context, null);
  }

  public EpoxyRecyclerView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public EpoxyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    if (attrs != null) {
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EpoxyRecyclerView,
          defStyleAttr, 0);
      setItemSpacingPx(a.getDimensionPixelSize(R.styleable.EpoxyRecyclerView_itemSpacing, 0));
      a.recycle();
    }

    init();
  }

  @CallSuper
  protected void init() {
    setClipToPadding(false);
    initViewPool();
  }

  /**
   * Get or create a view pool to use for this RecyclerView. By default the same pool is shared for
   * all {@link EpoxyRecyclerView} usages in the same Activity.
   *
   * @see #createViewPool()
   * @see #shouldShareViewPoolAcrossContext()
   */
  private void initViewPool() {
    if (!shouldShareViewPoolAcrossContext()) {
      setRecycledViewPool(createViewPool());
      return;
    }

    Context context = getContext();
    Iterator<PoolReference> iterator = RECYCLER_POOLS.iterator();
    PoolReference poolToUse = null;

    while (iterator.hasNext()) {
      PoolReference poolReference = iterator.next();
      if (poolReference.context() == null) {
        // Clean up entries from old activities so the list doesn't grow large
        iterator.remove();
      } else if (poolReference.context() == context) {
        if (poolToUse != null) {
          throw new IllegalStateException("A pool was already found");
        }
        poolToUse = poolReference;
        // finish iterating to remove any old contexts
      } else {
        // A pool from a different activity, it may be removed soon once the activity reference
        // is GC'd, but until then we can at least clear the pool references, which may
        // be keeping the activity from getting GC'd
        poolReference.clearIfActivityIsDestroyed();
      }
    }

    if (poolToUse == null) {
      poolToUse = new PoolReference(context, createViewPool());
      RECYCLER_POOLS.add(poolToUse);
    }

    setRecycledViewPool(poolToUse.viewPool);
  }

  /**
   * Create a new instance of a view pool to use with this recyclerview. By default a {@link
   * UnboundedViewPool} is used.
   */
  @NonNull
  protected RecycledViewPool createViewPool() {
    return new UnboundedViewPool();
  }

  /**
   * To maximize view recycling by default we share the same view pool across all {@link
   * EpoxyRecyclerView} instances in the same Activity. This behavior can be disabled by returning
   * false here.
   */
  public boolean shouldShareViewPoolAcrossContext() {
    return true;
  }

  @Override
  public void setLayoutParams(ViewGroup.LayoutParams params) {
    boolean isFirstParams = getLayoutParams() == null;
    super.setLayoutParams(params);

    if (isFirstParams) {
      // Set a default layout manager if one was not set via xml
      // We need layout params for this to guess at the right size and type
      if (getLayoutManager() == null) {
        setLayoutManager(createLayoutManager());
      }
    }
  }

  /**
   * Create a new {@link android.support.v7.widget.RecyclerView.LayoutManager} instance to use for
   * this RecyclerView.
   * <p>
   * By default a LinearLayoutManager is used, and a reasonable default is chosen for scrolling
   * direction based on layout params.
   * <p>
   * If the RecyclerView is set to match parent size then the scrolling orientation is set to
   * vertical and {@link #setHasFixedSize(boolean)} is set to true.
   * <p>
   * If the height is set to wrap_content then the scrolling orientation is set to horizontal, and
   * {@link #setClipToPadding(boolean)} is set to false.
   */
  @NonNull
  protected LayoutManager createLayoutManager() {
    ViewGroup.LayoutParams layoutParams = getLayoutParams();

    if (layoutParams.height == LayoutParams.MATCH_PARENT
        // 0 represents matching constraints in a LinearLayout or ConstraintLayout
        || layoutParams.height == 0) {

      if (layoutParams.width == LayoutParams.MATCH_PARENT
          || layoutParams.width == 0) {
        // If we are filling as much space as possible then we usually are fixed size
        setHasFixedSize(true);
      }

      // A sane default is a vertically scrolling linear layout
      return new LinearLayoutManager(getContext());
    } else {
      // This is usually the case for horizontally scrolling carousels and should be a sane
      // default
      return new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
    }
  }

  @Override
  public void setLayoutManager(@Nullable LayoutManager layout) {
    super.setLayoutManager(layout);
    syncSpanCount();
  }

  /**
   * If a grid layout manager is set we sync the span count between the layout and the epoxy
   * adapter automatically.
   */
  private void syncSpanCount() {
    LayoutManager layout = getLayoutManager();
    if (layout instanceof GridLayoutManager && epoxyController != null) {
      GridLayoutManager grid = (GridLayoutManager) layout;

      if (epoxyController.getSpanCount() != grid.getSpanCount()
          || grid.getSpanSizeLookup() != epoxyController.getSpanSizeLookup()) {
        epoxyController.setSpanCount(grid.getSpanCount());
        grid.setSpanSizeLookup(epoxyController.getSpanSizeLookup());
      }
    }
  }

  @Override
  public void requestLayout() {
    // Grid layout manager calls this when the span count is changed. Its the easiest way to
    // detect a span count change and update our controller accordingly.
    syncSpanCount();
    super.requestLayout();
  }

  public void setItemSpacingRes(@DimenRes int itemSpacingRes) {
    setItemSpacingPx(resToPx(itemSpacingRes));
  }

  public void setItemSpacingDp(@Dimension(unit = Dimension.DP) int dp) {
    setItemSpacingPx(dpToPx(dp));
  }

  /**
   * Set a pixel value to use as spacing between items. If this is a positive number an item
   * decoration will be added to space all items this far apart from each other. If the value is 0
   * or negative no extra spacing will be used, and any previous spacing will be removed.
   * <p>
   * This only works if a {@link LinearLayoutManager} or {@link GridLayoutManager} is used with this
   * RecyclerView.
   * <p>
   * This can also be set via the {@code app:itemSpacing} styleable attribute.
   *
   * @see #setItemSpacingDp(int)
   * @see #setItemSpacingRes(int)
   */
  public void setItemSpacingPx(@Px int spacingPx) {
    removeItemDecoration(spacingDecorator);
    spacingDecorator.setPxBetweenItems(spacingPx);

    if (spacingPx > 0) {
      addItemDecoration(spacingDecorator);
    }
  }

  /**
   * Set a list of {@link EpoxyModel}'s to show in this RecyclerView.
   * <p>
   * Alternatively you can set an {@link EpoxyController} to handle building models dynamically.
   *
   * @see #setController(EpoxyController)
   * @see #setControllerAndBuildModels(EpoxyController)
   * @see #buildModelsWith(ModelBuilderCallback)
   */

  public void setModels(@NonNull List<? extends EpoxyModel<?>> models) {
    if (!(epoxyController instanceof SimpleEpoxyController)) {
      setController(new SimpleEpoxyController());
    }

    ((SimpleEpoxyController) epoxyController).setModels(models);
  }

  /**
   * Set an EpoxyController to populate this RecyclerView. This does not make the controller build
   * its models, that must be done separately via {@link #requestModelBuild()}.
   * <p>
   * Use this if you don't want {@link #requestModelBuild()} called automatically. Common cases
   * are if you are using {@link TypedEpoxyController} (in which case you must call setData on the
   * controller), or if you have not otherwise populated your controller's data yet.
   * <p>
   * Otherwise if you want models built automatically for you use {@link
   * #setControllerAndBuildModels(EpoxyController)}
   * <p>
   * The controller can be cleared with {@link #clear()}
   *
   * @see #setControllerAndBuildModels(EpoxyController)
   * @see #buildModelsWith(ModelBuilderCallback)
   * @see #setModels(List)
   */

  public void setController(@NonNull EpoxyController controller) {
    epoxyController = controller;
    setAdapter(controller.getAdapter());
    syncSpanCount();
  }

  /**
   * Set an EpoxyController to populate this RecyclerView, and tell the controller to build
   * models.
   * <p>
   * The controller can be cleared with {@link #clear()}
   *
   * @see #setController(EpoxyController)
   * @see #buildModelsWith(ModelBuilderCallback)
   * @see #setModels(List)
   */
  public void setControllerAndBuildModels(@NonNull EpoxyController controller) {
    controller.requestModelBuild();
    setController(controller);
  }

  /**
   * Allows you to build models via a callback instead of needing to create a new EpoxyController
   * class. This is useful if your models are simple and you would like to simply declare them in
   * your activity/fragment.
   * <p>
   * Another useful pattern is having your Activity or Fragment implement {@link
   * ModelBuilderCallback}.
   *
   * @see #setController(EpoxyController)
   * @see #setControllerAndBuildModels(EpoxyController)
   * @see #setModels(List)
   */
  public void buildModelsWith(@NonNull final ModelBuilderCallback callback) {
    setControllerAndBuildModels(new EpoxyController() {
      @Override
      protected void buildModels() {
        callback.buildModels(this);
      }
    });
  }

  /**
   * A callback for creating models without needing a custom EpoxyController class. Used with {@link
   * #buildModelsWith(ModelBuilderCallback)}
   */
  public interface ModelBuilderCallback {
    /**
     * Analagous to {@link EpoxyController#buildModels()}. You should create new model instances and
     * add them to the given controller. {@link AutoModel} cannot be used with models added this
     * way.
     */
    void buildModels(@NonNull EpoxyController controller);
  }

  /**
   * Request that the currently set EpoxyController has its models rebuilt. You can use this to
   * avoid saving your controller as a field.
   * <p>
   * You cannot use this if your controller is a {@link TypedEpoxyController} or if you set
   * models via {@link #setModels(List)}. In that case you must set data directly on the
   * controller or set models again.
   */
  public void requestModelBuild() {
    if (epoxyController == null) {
      throw new IllegalStateException("A controller must be set before requesting a model build.");
    }

    if (epoxyController instanceof SimpleEpoxyController) {
      throw new IllegalStateException("Models were set with #setModels, they can not be rebuilt.");
    }

    epoxyController.requestModelBuild();
  }

  /**
   * Clear the currently set EpoxyController or Adapter as well as any models that are displayed.
   * <p>
   * Any pending requests to the EpoxyController to build models are canceled.
   * <p>
   * Any existing child views are recycled to the view pool.
   */
  public void clear() {
    if (epoxyController != null) {
      // The controller is cleared so the next time models are set we can create a fresh one.
      epoxyController.cancelPendingModelBuild();
      epoxyController = null;
    }

    // We use swapAdapter instead of setAdapter so that the view pool is not cleared.
    // 'removeAndRecycleExistingViews=true' is used in case this is a nested recyclerview
    // and we want to recycle the views back to a shared view pool
    swapAdapter(null, true);
  }

  @Px
  protected int dpToPx(@Dimension(unit = Dimension.DP) int dp) {
    return (int) TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
            getResources().getDisplayMetrics());
  }

  @Px
  protected int resToPx(@DimenRes int itemSpacingRes) {
    return getResources().getDimensionPixelOffset(itemSpacingRes);
  }

  @Override
  public void setAdapter(RecyclerView.Adapter adapter) {
    super.setAdapter(adapter);

    clearRemovedAdapterAndCancelRunnable();
  }

  @Override
  public void swapAdapter(Adapter adapter, boolean removeAndRecycleExistingViews) {
    super.swapAdapter(adapter, removeAndRecycleExistingViews);

    clearRemovedAdapterAndCancelRunnable();
  }

  @Override
  public void onAttachedToWindow() {
    super.onAttachedToWindow();

    if (removedAdapter != null) {
      // Restore the adapter that was removed when the view was detached from window
      swapAdapter(removedAdapter, false);
    }
    clearRemovedAdapterAndCancelRunnable();
  }

  @Override
  public void onDetachedFromWindow() {
    super.onDetachedFromWindow();

    if (removeAdapterWhenDetachedFromWindow) {
      if (delayMsWhenRemovingAdapterOnDetach > 0) {

        isRemoveAdapterRunnablePosted = true;
        postDelayed(removeAdapterRunnable, delayMsWhenRemovingAdapterOnDetach);
      } else {
        removeAdapter();
      }
    }
    clearPoolIfActivityIsDestroyed();
  }

  private void removeAdapter() {
    Adapter currentAdapter = getAdapter();
    if (currentAdapter != null) {
      // Clear the adapter so the adapter releases its reference to this RecyclerView.
      // Views are recycled so they can return to a view pool (default behavior is to not recycle
      // them).
      swapAdapter(null, true);
      // Keep a reference to the removed adapter so we can add it back if the recyclerview is
      // attached again.
      removedAdapter = currentAdapter;
    }

    // Do this after clearing the adapter, since that sends views back to the pool
    clearPoolIfActivityIsDestroyed();
  }

  private void clearRemovedAdapterAndCancelRunnable() {
    removedAdapter = null;
    if (isRemoveAdapterRunnablePosted) {
      removeCallbacks(removeAdapterRunnable);
      isRemoveAdapterRunnablePosted = false;
    }
  }

  private void clearPoolIfActivityIsDestroyed() {
    // Views in the pool hold context references which can keep the activity from being GC'd,
    // plus they can hold significant memory resources. We should clear it asap after the pool
    // is no longer needed - the main signal we use for this is that the activity is destroyed.
    if (isActivityDestroyed(getContext())) {
      getRecycledViewPool().clear();
    }
  }

  private static class PoolReference {
    private final WeakReference<Context> contextReference;
    private final RecycledViewPool viewPool;

    private PoolReference(Context context,
        RecycledViewPool viewPool) {
      this.contextReference = new WeakReference<>(context);
      this.viewPool = viewPool;
    }

    @Nullable
    private Context context() {
      return contextReference.get();
    }

    void clearIfActivityIsDestroyed() {
      if (isActivityDestroyed(context())) {
        viewPool.clear();
      }
    }
  }

  private static boolean isActivityDestroyed(@Nullable Context context) {
    if (context == null) {
      return true;
    }

    if (!(context instanceof Activity)) {
      return false;
    }

    Activity activity = (Activity) context;
    if (activity.isFinishing()) {
      return true;
    }

    if (VERSION.SDK_INT >= 17) {
      return activity.isDestroyed();
    } else {
      // Use this as a proxy for being destroyed on older devices
      return !ViewCompat.isAttachedToWindow(activity.getWindow().getDecorView());
    }
  }

  /**
   * Like its parent, UnboundedViewPool lets you share Views between multiple RecyclerViews. However
   * there is no maximum number of recycled views that it will store. This usually ends up being
   * optimal, barring any hard memory constraints, as RecyclerViews do not recycle more Views than
   * they need.
   */
  private static class UnboundedViewPool extends RecycledViewPool {

    private final SparseArray<Queue<ViewHolder>> scrapHeaps = new SparseArray<>();

    @Override
    public void clear() {
      scrapHeaps.clear();
    }

    @Override
    public void setMaxRecycledViews(int viewType, int max) {
      throw new UnsupportedOperationException(
          "UnboundedViewPool does not support setting a maximum number of recycled views");
    }

    @Override
    @Nullable
    public ViewHolder getRecycledView(int viewType) {
      final Queue<ViewHolder> scrapHeap = scrapHeaps.get(viewType);
      return scrapHeap != null ? scrapHeap.poll() : null;
    }

    @Override
    public void putRecycledView(ViewHolder viewHolder) {
      getScrapHeapForType(viewHolder.getItemViewType()).add(viewHolder);
    }

    private Queue<ViewHolder> getScrapHeapForType(int viewType) {
      Queue<ViewHolder> scrapHeap = scrapHeaps.get(viewType);
      if (scrapHeap == null) {
        scrapHeap = new LinkedList<>();
        scrapHeaps.put(viewType, scrapHeap);
      }
      return scrapHeap;
    }
  }
}
