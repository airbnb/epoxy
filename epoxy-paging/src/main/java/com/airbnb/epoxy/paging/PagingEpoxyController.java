package com.airbnb.epoxy.paging;

import android.arch.paging.PagedList;
import android.arch.paging.PagedList.Callback;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.airbnb.epoxy.EpoxyController;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyViewHolder;

import java.util.Collections;
import java.util.List;

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
 */
public abstract class PagingEpoxyController<T> extends EpoxyController {
  private static final int DEFAULT_PAGE_SIZE_HINT = 10;
  private static final int DEFAULT_NUM_PAGES_T0_LOAD = 10;

  @Nullable private PagedList<T> pagedList;
  @NonNull private List<T> list = Collections.emptyList();

  private int pageSizeHint = DEFAULT_PAGE_SIZE_HINT;
  private int numPagesToLoad = DEFAULT_NUM_PAGES_T0_LOAD;

  // TODO: (eli_hart 10/13/17) Save this in saved state and restore in constructor
  private int lastBoundPositionWithinList = 0;
  private boolean scrollingTowardsEnd = true;
  private int numBoundModels;
  private int lastBuiltLowerBound = 0;
  private int lastBuiltUpperBound = 0;

  /**
   * Set an estimate of how many items will be shown on screen. This number will be used to
   * calculate how many models should be built.
   * <p>
   * Setting this is optional - once the screen is fully populated Epoxy can track the number of
   * bound items to determine the page size, so this is mostly useful for initial page load.
   * <p>
   * The default is {@link #DEFAULT_PAGE_SIZE_HINT}
   */
  public void setPageSizeHint(int pageSizeHint) {
    this.pageSizeHint = pageSizeHint;
  }

  /**
   * Set how many pages of items in the list should be built as EpoxyModels at a time. The lower the
   * number the faster the model build and diff times will be, but it will also require more calls
   * to rebuild models as the user scrolls.
   * <p>
   * The default is {@link #DEFAULT_NUM_PAGES_T0_LOAD}
   */
  public void setNumPagesToLoad(int numPagesToLoad) {
    this.numPagesToLoad = numPagesToLoad;
  }

  @Override
  protected final void buildModels() {
    int numListItemsToUse =
        numBoundModels != 0 ? numBoundModels * numPagesToLoad : pageSizeHint * numPagesToLoad;

    // If we are scrolling towards one end of the list we can build slightly more models in that
    // direction in anticipation of needing to show more there soon
    float ratioOfEndItems = scrollingTowardsEnd ? .6f : .4f;

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

    if (pagedList != null) {
      pagedList.loadAround(positionWithinList);
    }

    scrollingTowardsEnd = lastBoundPositionWithinList < positionWithinCurrentModels;
    lastBoundPositionWithinList = positionWithinList;
    numBoundModels++;

    // TODO: (eli_hart 9/19/17) different prefetch depending on scroll direction?
    // build again?
    int prefetchDistance = numBoundModels;
    int currentModelCount = getAdapter().getItemCount();
    if (((currentModelCount - positionWithinCurrentModels - 1 < prefetchDistance)
        || (positionWithinCurrentModels < prefetchDistance && lastBuiltLowerBound != 0))) {
      requestModelBuild();
    }
  }

  @CallSuper
  @Override
  protected void onModelUnbound(@NonNull EpoxyViewHolder holder, @NonNull EpoxyModel<?> model) {
    numBoundModels--;
  }

  public void setList(@Nullable List<T> list) {
    if (list == this.list) {
      return;
    }

    if (pagedList != null) {
      setList((PagedList<T>) null);
    }

    this.list = list == null ? Collections.<T>emptyList() : list;
    requestModelBuild();
  }

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

    updatePagedListSnapshot();
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
