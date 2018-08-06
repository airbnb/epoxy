package com.airbnb.epoxy;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.AdapterListUpdateCallback;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView.Adapter;

import java.util.Collections;
import java.util.List;

/**
 * Wraps the result of {@link AsyncEpoxyDiffer#submitList(List)}.
 * <p>
 * If {@link #differResult} is non null it means the full differ ran and the result is contained
 * in that object. If it is null, it means that either the old list or the new list was empty, so
 * we can simply add all or clear all items and skipped running the full diffing.
 */
public class DiffResult {
  @NonNull final List<? extends EpoxyModel<?>> previousModels;
  @NonNull final List<? extends EpoxyModel<?>> newModels;

  @Nullable final DiffUtil.DiffResult differResult;

  @SuppressWarnings("unchecked")
  public DiffResult(
      @Nullable List<? extends EpoxyModel<?>> previousModels,
      @Nullable List<? extends EpoxyModel<?>> newModels,
      @Nullable DiffUtil.DiffResult differResult
  ) {
    this.previousModels = previousModels != null ? previousModels : Collections.EMPTY_LIST;
    this.newModels = newModels != null ? newModels : Collections.EMPTY_LIST;
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
