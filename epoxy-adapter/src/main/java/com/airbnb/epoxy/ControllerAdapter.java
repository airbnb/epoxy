package com.airbnb.epoxy;

import android.support.v7.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

class ControllerAdapter extends BaseEpoxyAdapter {
  private final DiffHelper diffHelper = new DiffHelper(this);
  private final NotifyBlocker notifyBlocker = new NotifyBlocker();
  private final EpoxyController epoxyController;
  List<EpoxyModel<?>> currentModels = Collections.emptyList();

  ControllerAdapter(EpoxyController epoxyController) {
    this.epoxyController = epoxyController;
    registerAdapterDataObserver(notifyBlocker);
  }

  @Override
  List<EpoxyModel<?>> getCurrentModels() {
    return currentModels;
  }

  public void setModels(List<EpoxyModel<?>> models) {
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
}
