package com.airbnb.epoxy;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public final class EpoxyControllerAdapter extends BaseEpoxyAdapter {
  private final DiffHelper diffHelper = new DiffHelper(this);
  private final NotifyBlocker notifyBlocker = new NotifyBlocker();
  private final EpoxyController epoxyController;
  private List<EpoxyModel<?>> currentModels = Collections.emptyList();
  private List<EpoxyModel<?>> copyOfCurrentModels;

  EpoxyControllerAdapter(EpoxyController epoxyController) {
    this.epoxyController = epoxyController;
    registerAdapterDataObserver(notifyBlocker);
  }

  @Override
  protected void onExceptionSwallowed(RuntimeException exception) {
    epoxyController.onExceptionSwallowed(exception);
  }

  @Override
  List<EpoxyModel<?>> getCurrentModels() {
    return currentModels;
  }

  void setModels(List<EpoxyModel<?>> models) {
    copyOfCurrentModels = null;
    this.currentModels = models;
    notifyBlocker.allowChanges();
    diffHelper.notifyModelChanges();
    notifyBlocker.blockChanges();
  }

  @Override
  public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    epoxyController.onAttachedToRecyclerView(recyclerView);
  }

  @Override
  public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
    epoxyController.onDetachedFromRecyclerView(recyclerView);
  }

  /** Get an unmodifiable copy of the current models set on the adapter. */
  public List<EpoxyModel<?>> getCopyOfModels() {
    if (copyOfCurrentModels == null) {
      copyOfCurrentModels = new UnmodifiableList<>(currentModels);
    }

    return copyOfCurrentModels;
  }

  public EpoxyModel<?> getModelAtPosition(int position) {
    return currentModels.get(position);
  }

  /**
   * Searches the current model list for the model with the given id. Returns the matching model if
   * one is found, otherwise null is returned.
   */
  @Nullable
  public EpoxyModel<?> getModelById(long id) {
    for (EpoxyModel<?> model : currentModels) {
      if (model.id() == id) {
        return model;
      }
    }

    return null;
  }

  protected int getModelPosition(EpoxyModel<?> targetModel) {
    int size = currentModels.size();
    for (int i = 0; i < size; i++) {
      EpoxyModel<?> model = currentModels.get(i);
      if (model.id() == targetModel.id()) {
        return i;
      }
    }

    return -1;
  }
}
