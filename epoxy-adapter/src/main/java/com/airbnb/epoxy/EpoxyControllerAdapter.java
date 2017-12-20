package com.airbnb.epoxy;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public final class EpoxyControllerAdapter extends BaseEpoxyAdapter {
  private final DiffHelper diffHelper = new DiffHelper(this, true);
  private final NotifyBlocker notifyBlocker = new NotifyBlocker();
  private final EpoxyController epoxyController;
  private ControllerModelList currentModels = new ControllerModelList(20);
  private List<EpoxyModel<?>> copyOfCurrentModels;
  private int itemCount;

  EpoxyControllerAdapter(@NonNull EpoxyController epoxyController) {
    this.epoxyController = epoxyController;
    registerAdapterDataObserver(notifyBlocker);
  }

  @Override
  protected void onExceptionSwallowed(@NonNull RuntimeException exception) {
    epoxyController.onExceptionSwallowed(exception);
  }

  @NonNull
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

  void setModels(@NonNull ControllerModelList models) {
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
  public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
    epoxyController.onAttachedToRecyclerViewInternal(recyclerView);
  }

  @Override
  public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
    epoxyController.onDetachedFromRecyclerViewInternal(recyclerView);
  }

  @Override
  public void onViewAttachedToWindow(@NonNull EpoxyViewHolder holder) {
    super.onViewAttachedToWindow(holder);
    epoxyController.onViewAttachedToWindow(holder, holder.getModel());
  }

  @Override
  public void onViewDetachedFromWindow(@NonNull EpoxyViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    epoxyController.onViewDetachedFromWindow(holder, holder.getModel());
  }

  @Override
  protected void onModelBound(@NonNull EpoxyViewHolder holder, @NonNull EpoxyModel<?> model,
      int position, @Nullable EpoxyModel<?> previouslyBoundModel) {
    epoxyController.onModelBound(holder, model, position, previouslyBoundModel);
  }

  @Override
  protected void onModelUnbound(@NonNull EpoxyViewHolder holder, @NonNull EpoxyModel<?> model) {
    epoxyController.onModelUnbound(holder, model);
  }

  /** Get an unmodifiable copy of the current models set on the adapter. */
  @NonNull
  public List<EpoxyModel<?>> getCopyOfModels() {
    if (copyOfCurrentModels == null) {
      copyOfCurrentModels = Collections.unmodifiableList(currentModels);
    }

    return copyOfCurrentModels;
  }

  /**
   * @throws IndexOutOfBoundsException If the given position is out of range of the current model
   *                                   list.
   */
  @NonNull
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
  public int getModelPosition(@NonNull EpoxyModel<?> targetModel) {
    int size = currentModels.size();
    for (int i = 0; i < size; i++) {
      EpoxyModel<?> model = currentModels.get(i);
      if (model.id() == targetModel.id()) {
        return i;
      }
    }

    return -1;
  }

  @NonNull
  @Override
  public BoundViewHolders getBoundViewHolders() {
    return super.getBoundViewHolders();
  }

  void moveModel(int fromPosition, int toPosition) {
    copyOfCurrentModels = null;

    currentModels.pauseNotifications();
    currentModels.add(toPosition, currentModels.remove(fromPosition));
    currentModels.resumeNotifications();

    notifyBlocker.allowChanges();
    notifyItemMoved(fromPosition, toPosition);
    notifyBlocker.blockChanges();
  }
}
