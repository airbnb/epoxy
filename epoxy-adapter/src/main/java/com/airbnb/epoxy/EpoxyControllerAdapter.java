package com.airbnb.epoxy;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.util.DiffUtil.ItemCallback;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public final class EpoxyControllerAdapter extends BaseEpoxyAdapter {
  private final NotifyBlocker notifyBlocker = new NotifyBlocker(this);
  private final AsyncListDifferWithPayload<EpoxyModel<?>> differ;
  private final EpoxyController epoxyController;
  private int itemCount;

  EpoxyControllerAdapter(@NonNull EpoxyController epoxyController, Handler diffingHandler) {
    this.epoxyController = epoxyController;
    differ = new AsyncListDifferWithPayload<>(
        diffingHandler,
        notifyBlocker,
        ITEM_CALLBACK
    );
    registerAdapterDataObserver(notifyBlocker);
  }

  @Override
  protected void onExceptionSwallowed(@NonNull RuntimeException exception) {
    epoxyController.onExceptionSwallowed(exception);
  }

  @NonNull
  @Override
  List<EpoxyModel<?>> getCurrentModels() {
    return differ.getCurrentList();
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
    notifyBlocker.allowChanges();
    differ.submitList(models);
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
    return getCurrentModels();
  }

  /**
   * @throws IndexOutOfBoundsException If the given position is out of range of the current model
   *                                   list.
   */
  @NonNull
  public EpoxyModel<?> getModelAtPosition(int position) {
    return getCurrentModels().get(position);
  }

  /**
   * Searches the current model list for the model with the given id. Returns the matching model if
   * one is found, otherwise null is returned.
   */
  @Nullable
  public EpoxyModel<?> getModelById(long id) {
    for (EpoxyModel<?> model : getCurrentModels()) {
      if (model.id() == id) {
        return model;
      }
    }

    return null;
  }

  @Override
  public int getModelPosition(@NonNull EpoxyModel<?> targetModel) {
    int size = getCurrentModels().size();
    for (int i = 0; i < size; i++) {
      EpoxyModel<?> model = getCurrentModels().get(i);
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

  @UiThread
  void moveModel(int fromPosition, int toPosition) {
    ArrayList<EpoxyModel<?>> updatedList = new ArrayList<>(getCurrentModels());

    updatedList.add(toPosition, updatedList.remove(fromPosition));
    notifyBlocker.allowChanges();
    notifyItemMoved(fromPosition, toPosition);
    notifyBlocker.blockChanges();

    boolean interruptedDiff = differ.isDiffInProgress();
    differ.forceListOverride(updatedList);

    if (interruptedDiff) {
      // The move interrupted a model rebuild/diff that was in progress,
      // so models may be out of date and we should force them to rebuilt
      epoxyController.requestModelBuild();
    }
  }

  private static final ItemCallback<EpoxyModel<?>> ITEM_CALLBACK =
      new ItemCallback<EpoxyModel<?>>() {
        @Override
        public boolean areItemsTheSame(EpoxyModel<?> oldItem, EpoxyModel<?> newItem) {
          return oldItem.id() == newItem.id();
        }

        @Override
        public boolean areContentsTheSame(EpoxyModel<?> oldItem, EpoxyModel<?> newItem) {
          return oldItem.equals(newItem);
        }

        @Override
        public Object getChangePayload(EpoxyModel<?> oldItem, EpoxyModel<?> newItem) {
          return new DiffPayload(oldItem);
        }
      };
}
