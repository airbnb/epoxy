package com.airbnb.epoxy;

import android.os.Handler;
import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.DiffUtil.ItemCallback;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * An adaptation of Google's {@link android.support.v7.recyclerview.extensions.AsyncListDiffer}
 * that adds support for payloads in changes.
 * <p>
 * Also adds support for canceling an in progress diff, and makes everything thread safe.
 */
class AsyncEpoxyDiffer {

  interface ResultCallack {
    void onResult(@NonNull DiffResult result);
  }

  private final Executor executor;
  private final ResultCallack resultCallack;
  private final ItemCallback<EpoxyModel<?>> diffCallback;
  private final GenerationTracker generationTracker = new GenerationTracker();

  AsyncEpoxyDiffer(
      @NonNull Handler handler,
      @NonNull ResultCallack resultCallack,
      @NonNull ItemCallback<EpoxyModel<?>> diffCallback
  ) {
    this.executor = new HandlerExecutor(handler);
    this.resultCallack = resultCallack;
    this.diffCallback = diffCallback;
  }

  @Nullable
  private volatile List<? extends EpoxyModel<?>> list;

  /**
   * Non-null, unmodifiable version of list.
   * <p>
   * Collections.emptyList when list is null, wrapped by Collections.unmodifiableList otherwise
   */
  @NonNull
  private volatile List<? extends EpoxyModel<?>> readOnlyList = Collections.emptyList();

  /**
   * Get the current List - any diffing to present this list has already been computed and
   * dispatched via the ListUpdateCallback.
   * <p>
   * If a <code>null</code> List, or no List has been submitted, an empty list will be returned.
   * <p>
   * The returned list may not be mutated - mutations to content must be done through
   * {@link #submitList(List)}.
   *
   * @return current List.
   */
  @AnyThread
  @NonNull
  public List<? extends EpoxyModel<?>> getCurrentList() {
    return readOnlyList;
  }

  /**
   * Prevents any ongoing diff from dispatching results. Returns true if there was an ongoing
   * diff to cancel, false otherwise.
   */
  @SuppressWarnings("WeakerAccess")
  @AnyThread
  public boolean cancelDiff() {
    return generationTracker.finishMaxGeneration();
  }

  /**
   * Set the current list without performing any diffing. Cancels any diff in progress.
   * <p>
   * This can be used if you notified a change to the adapter manually and need this list to be
   * synced.
   */
  @AnyThread
  public boolean forceListOverride(@Nullable List<EpoxyModel<?>> newList) {
    final boolean interruptedDiff;
    synchronized (this) {
      // We need to make sure that generation changes and list updates are synchronized
      interruptedDiff = cancelDiff();
      int generation = generationTracker.incrementAndGetNextScheduled();
      tryLatchList(newList, generation);
    }
    return interruptedDiff;
  }

  /**
   * Set a new List representing your latest data.
   * <p>
   * A diff will be computed between this list and the last list set. If this has not previously
   * been called then an empty list is used as the previous list.
   * <p>
   * The diff computation will be done on the thread given by the handler in the constructor.
   * When the diff is done it will be applied (dispatched to the result callback),
   * and the new List will be swapped in.
   */
  @AnyThread
  @SuppressWarnings("WeakerAccess")
  public void submitList(@Nullable final List<? extends EpoxyModel<?>> newList) {
    final int runGeneration;
    @Nullable final List<? extends EpoxyModel<?>> previousList;

    synchronized (this) {
      // Incrementing generation means any currently-running diffs are discarded when they finish
      // We synchronize to guarantee list object and generation number are in sync
      runGeneration = generationTracker.incrementAndGetNextScheduled();
      previousList = list;
    }

    if (newList == previousList) {
      // nothing to do
      onRunCompleted(runGeneration, newList, new DiffResult(previousList, newList, null));
      return;
    }

    if (newList == null || newList.isEmpty()) {
      // fast simple clear all
      DiffResult result = null;
      if (previousList != null && !previousList.isEmpty()) {
        result = new DiffResult(previousList, null, null);
      }
      onRunCompleted(runGeneration, null, result);
      return;
    }

    if (previousList == null || previousList.isEmpty()) {
      // fast simple first insert
      onRunCompleted(runGeneration, newList, new DiffResult(null, newList, null));
      return;
    }

    final DiffCallback wrappedCallback = new DiffCallback(previousList, newList, diffCallback);

    executor.execute(new Runnable() {
      @Override
      public void run() {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(wrappedCallback);
        onRunCompleted(runGeneration, newList, new DiffResult(previousList, newList, result));
      }
    });
  }

  private void onRunCompleted(
      final int runGeneration,
      @Nullable final List<? extends EpoxyModel<?>> newList,
      @Nullable final DiffResult result
  ) {

    MainThreadExecutor.INSTANCE.execute(new Runnable() {
      @Override
      public void run() {
        final boolean dispatchResult;
        synchronized (this) {
          dispatchResult = tryLatchList(newList, runGeneration);
        }

        if (result != null && dispatchResult) {
          resultCallack.onResult(result);
        }
      }
    });
  }

  /**
   * Marks the generation as done, and updates the list if the generation is the most recent.
   * Calls to this MUST be synchronized on "this" so list object and generation always stay in sync.
   * This method isn't synchronized directly so callers have flexibility to includes other
   * actions in their synchronized block
   *
   * @return True if the given generation is the most recent, in which case the given list was
   * set. False if the generation is old and the list was ignored.
   */
  private boolean tryLatchList(@Nullable List<? extends EpoxyModel<?>> newList, int runGeneration) {
    if (generationTracker.finishGeneration(runGeneration)) {
      list = newList;

      if (newList == null) {
        readOnlyList = Collections.emptyList();
      } else {
        readOnlyList = Collections.unmodifiableList(newList);
      }

      return true;
    }

    return false;
  }

  /**
   * The concept of a "generation" is used to associate a diff result with a point in time when
   * it was created. This allows us to handle list updates concurrently, and ignore outdated diffs.
   * <p>
   * We track the highest start generation, and the highest finished generation, and these must
   * be kept in sync, so all access to this class is synchronized.
   * <p>
   * The general synchronization strategy for this class is that when a generation number
   * is queried that action must be synchronized with accessing the current list, so that the
   * generation number is synced with the list state at the time it was created.
   */
  private static class GenerationTracker {

    // Max generation of currently scheduled runnable
    private volatile int maxScheduledGeneration;
    private volatile int maxFinishedGeneration;

    synchronized int incrementAndGetNextScheduled() {
      return ++maxScheduledGeneration;
    }

    synchronized boolean finishMaxGeneration() {
      boolean isInterrupting = maxScheduledGeneration > maxFinishedGeneration;
      maxFinishedGeneration = maxScheduledGeneration;
      return isInterrupting;
    }

    synchronized boolean finishGeneration(int runGeneration) {
      boolean isLatestGeneration =
          maxScheduledGeneration == runGeneration && runGeneration > maxFinishedGeneration;

      if (isLatestGeneration) {
        maxFinishedGeneration = runGeneration;
      }

      return isLatestGeneration;
    }
  }

  private static class DiffCallback extends DiffUtil.Callback {

    final List<? extends EpoxyModel<?>> oldList;
    final List<? extends EpoxyModel<?>> newList;
    private final ItemCallback<EpoxyModel<?>> diffCallback;

    DiffCallback(List<? extends EpoxyModel<?>> oldList, List<? extends EpoxyModel<?>> newList,
        ItemCallback<EpoxyModel<?>> diffCallback) {
      this.oldList = oldList;
      this.newList = newList;
      this.diffCallback = diffCallback;
    }

    @Override
    public int getOldListSize() {
      return oldList.size();
    }

    @Override
    public int getNewListSize() {
      return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
      return diffCallback.areItemsTheSame(
          oldList.get(oldItemPosition),
          newList.get(newItemPosition)
      );
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
      return diffCallback.areContentsTheSame(
          oldList.get(oldItemPosition),
          newList.get(newItemPosition)
      );
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
      return diffCallback.getChangePayload(
          oldList.get(oldItemPosition),
          newList.get(newItemPosition)
      );
    }
  }
}
