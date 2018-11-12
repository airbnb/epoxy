package com.airbnb.epoxy;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AdapterListUpdateCallback;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView.Adapter;

/**
 * Wraps the result of {@link AsyncEpoxyDiffer#submitList(List)}.
 */
public class DiffResult {
  @NonNull final List<? extends EpoxyModel<?>> previousModels;
  @NonNull final List<? extends EpoxyModel<?>> newModels;

  /**
   * If this is non null it means the full differ ran and the result is contained
   * in this object. If it is null, it means that either the old list or the new list was empty, so
   * we can simply add all or clear all items and skipped running the full diffing.
   */
  @Nullable final DiffUtil.DiffResult differResult;

  /** No changes were made to the models. */
  static DiffResult noOp(@Nullable List<? extends EpoxyModel<?>> models) {
    if (models == null) {
      models = Collections.emptyList();
    }
    return new DiffResult(models, models, null);
  }

  /** The previous list was empty and the given non empty list was inserted. */
  static DiffResult inserted(@NonNull List<? extends EpoxyModel<?>> newModels) {
    //noinspection unchecked
    return new DiffResult(Collections.EMPTY_LIST, newModels, null);
  }

  /** The previous list was non empty and the new list is empty. */
  static DiffResult clear(@NonNull List<? extends EpoxyModel<?>> previousModels) {
    //noinspection unchecked
    return new DiffResult(previousModels, Collections.EMPTY_LIST, null);
  }

  /**
   * The previous and new models are both non empty and a full differ pass was run on them.
   * There may be no changes, however.
   */
  static DiffResult diff(
      @NonNull List<? extends EpoxyModel<?>> previousModels,
      @NonNull List<? extends EpoxyModel<?>> newModels,
      @NonNull DiffUtil.DiffResult differResult
  ) {
    return new DiffResult(previousModels, newModels, differResult);
  }

  private DiffResult(
      @NonNull List<? extends EpoxyModel<?>> previousModels,
      @NonNull List<? extends EpoxyModel<?>> newModels,
      @Nullable DiffUtil.DiffResult differResult
  ) {
    this.previousModels = previousModels;
    this.newModels = newModels;
    this.differResult = differResult;
  }

  public void dispatchTo(Adapter adapter) {
    dispatchTo(new AdapterListUpdateCallback(adapter));
  }

  public void dispatchTo(ListUpdateCallback callback) {
    if (differResult != null) {
      differResult.dispatchUpdatesTo(callback);
    } else if (newModels.isEmpty() && !previousModels.isEmpty()) {
      callback.onRemoved(0, previousModels.size());
    } else if (!newModels.isEmpty() && previousModels.isEmpty()) {
      callback.onInserted(0, newModels.size());
    }

    // Else nothing changed!
  }
}
