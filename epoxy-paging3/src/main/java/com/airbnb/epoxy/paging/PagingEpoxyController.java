package com.airbnb.epoxy.paging;

import com.airbnb.epoxy.EpoxyController;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyViewHolder;

import java.util.Collections;
import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.paging.PagedList.Callback;
import androidx.paging.PagedList.Config;

/**
 * An {@link com.airbnb.epoxy.EpoxyController} that meant for large lists of items.
 * <p>
 * Normally {@link #buildModels()} should build a unique model to represent every item in the
 * RecyclerView; however, that can be slow if there are more than a few hundred items. This
 * controller will manage your list for you and only request models to be built for the items in the
 * list around the current position on screen. As the user scrolls models will be rebuilt around the
 * scroll position as needed.
 * <p>
 * Additionally, this works with the Android Architecture component {@link PagedList} to load more
 * items as needed.
 * <p>
 * To use, simply call {@link #setList} with either a normal Java {@link List} or an Android
 * PagedList.
 * <p>
 * {@link #buildModels(List)} will be called with the subset of items that should have models built
 * for them.
 *
 * @param <T> The type of item in the list
 *
 * @deprecated Use {@link PagedListEpoxyController} instead.
 */
@Deprecated()
public abstract class PagingEpoxyController<T> extends EpoxyController {

  private static final Config DEFAULT_CONFIG = new Config.Builder()
      .setEnablePlaceholders(false)
      .setPageSize(100)
      .setInitialLoadSizeHint(100)
      .setPrefetchDistance(20)
      .build();

  @Nullable private PagedList<T> pagedList;
  @NonNull private List<T> list = Collections.emptyList();

  // TODO: (eli_hart 10/13/17) Save this in saved state and restore in constructor
  private int lastBoundPositionWithinList = 0;
  private boolean scrollingTowardsEnd = true;
  private int lastBuiltLowerBound = 0;
  private int lastBuiltUpperBound = 0;
  @Nullable private Config customConfig = null;
  private boolean isFirstBuildForList = true;
  /** Prevent excessively throwing this exception. */
  private boolean hasNotifiedInsufficientPageSize;

  @Override
  protected final void buildModels() {
    int numListItemsToUse = isFirstBuildForList ? config().initialLoadSizeHint : config().pageSize;
    if (!list.isEmpty()) {
      isFirstBuildForList = false;
    }

    int numBoundViews = getAdapter().getBoundViewHolders().size();
    if (!hasNotifiedInsufficientPageSize && numBoundViews > numListItemsToUse) {
      onExceptionSwallowed(new IllegalStateException(
          "The page size specified in your PagedList config is smaller than the number of items "
              + "shown on screen. Increase your page size and/or initial load size."));
      hasNotifiedInsufficientPageSize = true;
    }

    // If we are scrolling towards one end of the list we can build more models in that
    // direction in anticipation of needing to show more there soon
    float ratioOfEndItems = scrollingTowardsEnd ? .7f : .3f;

    int itemsToBuildTowardsEnd = (int) (numListItemsToUse * ratioOfEndItems);
    int itemsToBuildTowardsStart = numListItemsToUse - itemsToBuildTowardsEnd;

    int numItemsUntilEnd = list.size() - lastBoundPositionWithinList - 1;
    int leftOverItemsAtEnd = itemsToBuildTowardsEnd - numItemsUntilEnd;
    if (leftOverItemsAtEnd > 0) {
      itemsToBuildTowardsStart += leftOverItemsAtEnd;
      itemsToBuildTowardsEnd -= leftOverItemsAtEnd;
    }

    int numItemsUntilStart = lastBoundPositionWithinList;
    int leftOverItemsAtStart = itemsToBuildTowardsStart - numItemsUntilStart;
    if (leftOverItemsAtStart > 0) {
      itemsToBuildTowardsStart -= leftOverItemsAtStart;
      itemsToBuildTowardsEnd += leftOverItemsAtStart;
    }

    lastBuiltLowerBound = Math.max(lastBoundPositionWithinList - itemsToBuildTowardsStart, 0);
    lastBuiltUpperBound =
        Math.min(lastBoundPositionWithinList + itemsToBuildTowardsEnd, list.size());
    buildModels(list.subList(lastBuiltLowerBound, lastBuiltUpperBound));
  }

  /**
   * Build models to represent the items in the given list. The list is a subset of the list set in
   * {@link #setList(List)}, and it represents the items around the current scroll position of the
   * RecyclerView.
   * <p>
   * You can still build multiple model types like a normal EpoxyController, you are not restricted
   * to models that represent items in the list.
   */
  protected abstract void buildModels(@NonNull List<T> list);

  @CallSuper
  @Override
  protected void onModelBound(@NonNull EpoxyViewHolder holder, @NonNull EpoxyModel<?> boundModel,
      int positionWithinCurrentModels, @Nullable EpoxyModel<?> previouslyBoundModel) {

    int positionWithinList = positionWithinCurrentModels + lastBuiltLowerBound;

    if (pagedList != null && !pagedList.isEmpty()) {
      pagedList.loadAround(positionWithinList);
    }

    scrollingTowardsEnd = lastBoundPositionWithinList < positionWithinList;
    lastBoundPositionWithinList = positionWithinList;

    int prefetchDistance = config().prefetchDistance;
    final int distanceToEndOfPage = getAdapter().getItemCount() - positionWithinCurrentModels;
    final int distanceToStartOfPage = positionWithinCurrentModels;

    if ((distanceToEndOfPage < prefetchDistance && !hasBuiltLastItem() && scrollingTowardsEnd)
        || (distanceToStartOfPage < prefetchDistance && !hasBuiltFirstItem()
        && !scrollingTowardsEnd)) {
      requestModelBuild();
    }
  }

  private boolean hasBuiltFirstItem() {
    return lastBuiltLowerBound == 0;
  }

  private boolean hasBuiltLastItem() {
    return lastBuiltUpperBound >= totalListSize();
  }

  public int totalListSize() {
    return pagedList != null ? pagedList.size() : list.size();
  }

  public void setList(@Nullable List<T> list) {
    if (list == this.list) {
      return;
    }

    if (pagedList != null) {
      setList((PagedList<T>) null);
    }

    this.list = list == null ? Collections.<T>emptyList() : list;
    isFirstBuildForList = true;
    requestModelBuild();
  }

  /**
   * Set a PagedList that should be used to build models in this controller. A listener will be
   * attached to the list so that models are rebuilt as new list items are loaded.
   * <p>
   * By default the Config setting on the PagedList will dictate how many models are built at once,
   * and what prefetch thresholds should be used. This can be overridden with a separate Config via
   * {@link #setConfig(Config)}.
   * <p>
   * See {@link #setConfig(Config)} for details on how the Config settings are used, and for
   * recommended values.
   */
  public void setList(@Nullable PagedList<T> list) {
    if (list == this.pagedList) {
      return;
    }

    PagedList<T> previousList = this.pagedList;
    this.pagedList = list;

    if (previousList != null) {
      previousList.removeWeakCallback(callback);
    }

    if (list != null) {
      list.addWeakCallback(null, callback);
    }

    isFirstBuildForList = true;
    updatePagedListSnapshot();
  }

  /**
   * Set a Config value to specify how many models should be built at a time.
   * <p>
   * If not set, or set to null, the config value off of the currently set PagedList is used.
   * <p>
   * If no PagedList is set, {@link #DEFAULT_CONFIG} is used.
   * <p>
   * {@link Config#initialLoadSizeHint} dictates how many models are built on first load. This
   * should be several times the number of items shown on screen, and is generally equal to or
   * larger than pageSize.
   * <p>
   * {@link Config#pageSize} dictates how many models are built at a time after first load. This
   * should be several times the number of items shown on screen (roughly 10x, and at least 5x). If
   * this value is too small models will be rebuilt very often as the user scrolls, potentially
   * hurting performance. In the worst case, if this value is too small, not enough models will be
   * created to fill the whole screen and the controller will enter an infinite loop of rebuilding
   * models.
   * <p>
   * {@link Config#prefetchDistance} defines how far from the edge of built models the user must
   * scroll to trigger further model building. Should be significantly less than page size (roughly
   * 1/4), and more than the number of items shown on screen. If this value is too big models will
   * be rebuilt very often as the user scrolls, potentially hurting performance. If this number is
   * too small then the user may have to wait while models are rebuilt as they scroll.
   * <p>
   * For example, if 5 items are shown on screen at once you might have a initialLoadSizeHint of 50,
   * a pageSize of 50, and a prefetchDistance of 10.
   */
  public void setConfig(@Nullable Config config) {
    customConfig = config;
  }

  private Config config() {
    if (customConfig != null) {
      return customConfig;
    }

    if (pagedList != null) {
      return pagedList.getConfig();
    }

    return DEFAULT_CONFIG;
  }

  /**
   * @return The list currently being displayed by the EpoxyController. This is either the Java List
   * set with {@link #setList(List)}, the latest snapshot if a PagedList is set, or an empty list if
   * nothing was set.
   */
  @NonNull
  public List<T> getCurrentList() {
    return this.list;
  }

  /**
   * @return The pagedList currently being displayed. Null if one has not been set.
   */
  @Nullable
  public PagedList<T> getPagedList() {
    return this.pagedList;
  }

  private final Callback callback = new Callback() {
    @Override
    public void onChanged(int position, int count) {
      updatePagedListSnapshot();
    }

    @Override
    public void onInserted(int position, int count) {
      updatePagedListSnapshot();
    }

    @Override
    public void onRemoved(int position, int count) {
      updatePagedListSnapshot();
    }
  };

  private void updatePagedListSnapshot() {
    list = pagedList == null ? Collections.<T>emptyList() : pagedList.snapshot();
    requestModelBuild();
  }
}
