package com.airbnb.epoxy;

import android.os.Handler;
import android.view.View;

import com.airbnb.epoxy.AsyncEpoxyDiffer.ResultCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import androidx.recyclerview.widget.RecyclerView;

public final class EpoxyControllerAdapter extends BaseEpoxyAdapter implements ResultCallback {
  private final NotifyBlocker notifyBlocker = new NotifyBlocker();
  private final AsyncEpoxyDiffer differ;
  private final EpoxyController epoxyController;
  private int itemCount;
  private final List<OnModelBuildFinishedListener> modelBuildListeners = new ArrayList<>();

  EpoxyControllerAdapter(@NonNull EpoxyController epoxyController, Handler diffingHandler) {
    this.epoxyController = epoxyController;
    differ = new AsyncEpoxyDiffer(
        diffingHandler,
        this,
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
  List<? extends EpoxyModel<?>> getCurrentModels() {
    return differ.getCurrentList();
  }

  @Override
  public int getItemCount() {
    // RecyclerView calls this A LOT. The base class implementation does
    // getCurrentModels().size() which adds some overhead because of the method calls.
    // We can easily memoize this, which seems to help when there are lots of models.
    return itemCount;
  }

  /** This is set from whatever thread model building happened on, so must be thread safe. */
  void setModels(@NonNull ControllerModelList models) {
    // If debug model validations are on then we should help detect the error case where models
    // were incorrectly mutated once they were added. That check is also done before and after
    // bind, but there is no other check after that to see if a model is incorrectly
    // mutated after being bound.
    // If a data class inside a model is mutated, then when models are rebuilt the differ
    // will still recognize the old and new models as equal, even though the old model was changed.
    // To help catch that error case we check for mutations here, before running the differ.
    //
    // https://github.com/airbnb/epoxy/issues/805
    List<? extends EpoxyModel<?>> currentModels = getCurrentModels();
    if (!currentModels.isEmpty() && currentModels.get(0).isDebugValidationEnabled()) {
      for (int i = 0; i < currentModels.size(); i++) {
        EpoxyModel<?> model = currentModels.get(i);
        model.validateStateHasNotChangedSinceAdded(
            "The model was changed between being bound and when models were rebuilt",
            i
        );
      }
    }

    differ.submitList(models);
  }

  /**
   * @return True if a diff operation is in progress.
   */
  public boolean isDiffInProgress() {
    return differ.isDiffInProgress();
  }

  // Called on diff results from the differ
  @Override
  public void onResult(@NonNull DiffResult result) {
    itemCount = result.newModels.size();
    notifyBlocker.allowChanges();
    result.dispatchTo(this);
    notifyBlocker.blockChanges();

    for (int i = modelBuildListeners.size() - 1; i >= 0; i--) {
      modelBuildListeners.get(i).onModelBuildFinished(result);
    }
  }

  public void addModelBuildListener(OnModelBuildFinishedListener listener) {
    modelBuildListeners.add(listener);
  }

  public void removeModelBuildListener(OnModelBuildFinishedListener listener) {
    modelBuildListeners.remove(listener);
  }

  @Override
  boolean diffPayloadsEnabled() {
    return true;
  }

  @Override
  public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    epoxyController.onAttachedToRecyclerViewInternal(recyclerView);
  }

  @Override
  public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
    super.onDetachedFromRecyclerView(recyclerView);
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
    //noinspection unchecked
    return (List<EpoxyModel<?>>) getCurrentModels();
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

    boolean interruptedDiff = differ.forceListOverride(updatedList);

    if (interruptedDiff) {
      // The move interrupted a model rebuild/diff that was in progress,
      // so models may be out of date and we should force them to rebuilt
      epoxyController.requestModelBuild();
    }
  }

  @UiThread
  void notifyModelChanged(int position) {
    ArrayList<EpoxyModel<?>> updatedList = new ArrayList<>(getCurrentModels());

    notifyBlocker.allowChanges();
    notifyItemChanged(position);
    notifyBlocker.blockChanges();

    boolean interruptedDiff = differ.forceListOverride(updatedList);

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

  /**
   * Delegates the callbacks received in the adapter
   * to the controller.
   */
  @Override
  public boolean isStickyHeader(int position) {
    return epoxyController.isStickyHeader(position);
  }

  /**
   * Delegates the callbacks received in the adapter
   * to the controller.
   */
  @Override
  public void setupStickyHeaderView(@NotNull View stickyHeader) {
    epoxyController.setupStickyHeaderView(stickyHeader);
  }

  /**
   * Delegates the callbacks received in the adapter
   * to the controller.
   */
  @Override
  public void teardownStickyHeaderView(@NotNull View stickyHeader) {
    epoxyController.teardownStickyHeaderView(stickyHeader);
  }
}
