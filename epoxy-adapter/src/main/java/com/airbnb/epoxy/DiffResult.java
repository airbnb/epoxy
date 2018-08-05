package com.airbnb.epoxy;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

public class DiffResult {
  final List<EpoxyModel<?>> previousModels;
  final List<EpoxyModel<?>> newModels;

  @Nullable final DiffUtil.DiffResult differResult;

  public DiffResult(
      List<EpoxyModel<?>> previousModels,
      List<EpoxyModel<?>> newModels,
      @Nullable DiffUtil.DiffResult differResult
  ) {
    this.previousModels = previousModels;
    this.newModels = newModels;
    this.differResult = differResult;
  }
}
