package com.airbnb.epoxy;

import androidx.annotation.NonNull;

/**
 * Used with {@link EpoxyController#addModelBuildListener(OnModelBuildFinishedListener)} to be
 * alerted to new model changes.
 */
public interface OnModelBuildFinishedListener {
  /**
   * Called after {@link EpoxyController#buildModels()} has run and changes have been notified to
   * the adapter. This will be called even if no changes existed.
   */
  void onModelBuildFinished(@NonNull DiffResult result);
}
