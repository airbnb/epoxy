package com.airbnb.epoxy;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public final class EpoxyControllerAdapter extends BaseEpoxyAdapter {
  private final DiffHelper diffHelper = new DiffHelper(this, true);
  private final NotifyBlocker notifyBlocker = new NotifyBlocker();
  private final EpoxyController epoxyController;
  private List<EpoxyModel<?>> currentModels = Collections.emptyList();
  private List<EpoxyModel<?>> copyOfCurrentModels;
  private int itemCount;

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

  @Override
  public int getItemCount() {
    // RecyclerView calls this A LOT. The base class implementation does
    // getCurrentModels().size() which adds some overhead because of the method calls.
    // We can easily memoize this, which seems to help when there are lots of models.
    return itemCount;
  }

  void setModels(List<EpoxyModel<?>> models) {
    itemCount = models.size();
    copyOfCurrentModels = null;
    this.currentModels = models;
    notifyBlocker.allowChanges();
    diffHelper.notifyModelChanges();
    notifyBlocker.blockChanges();
  }

  @Override
  boolean diffPayloadsEnabled() {
    return true;
  }

  @Override
  public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    epoxyController.onAttachedToRecyclerViewInternal(recyclerView);
  }

  @Override
  public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
    epoxyController.onDetachedFromRecyclerViewInternal(recyclerView);
  }

  @Override
  public void onViewAttachedToWindow(EpoxyViewHolder holder) {
    super.onViewAttachedToWindow(holder);
    epoxyController.onViewAttachedToWindow(holder, holder.getModel());
  }

  @Override
  public void onViewDetachedFromWindow(EpoxyViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    epoxyController.onViewDetachedFromWindow(holder, holder.getModel());
  }

  @Override
  protected void onModelBound(EpoxyViewHolder holder, EpoxyModel<?> model, int position,
      @Nullable EpoxyModel<?> previouslyBoundModel) {
    epoxyController.onModelBound(holder, model, position, previouslyBoundModel);
  }

  @Override
  protected void onModelUnbound(EpoxyViewHolder holder, EpoxyModel<?> model) {
    epoxyController.onModelUnbound(holder, model);
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

  @Override
  public int getModelPosition(EpoxyModel<?> targetModel) {
    int size = currentModels.size();
    for (int i = 0; i < size; i++) {
      EpoxyModel<?> model = currentModels.get(i);
      if (model.id() == targetModel.id()) {
        return i;
      }
    }

    return -1;
  }

  @Override
  public BoundViewHolders getBoundViewHolders() {
    return super.getBoundViewHolders();
  }
}
