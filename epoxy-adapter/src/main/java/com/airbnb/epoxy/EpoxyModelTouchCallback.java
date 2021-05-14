package com.airbnb.epoxy;

import android.graphics.Canvas;
import android.view.View;

import com.airbnb.viewmodeladapter.R;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A wrapper around {@link androidx.recyclerview.widget.ItemTouchHelper.Callback} to enable
 * easier touch support when working with Epoxy models.
 * <p>
 * For simplicity you can use {@link EpoxyTouchHelper} to set up touch handling via this class for
 * you instead of using this class directly. However, you may choose to use this class directly with
 * your own {@link ItemTouchHelper} if you need extra flexibility or customization.
 */
public abstract class EpoxyModelTouchCallback<T extends EpoxyModel>
    extends EpoxyTouchHelperCallback implements EpoxyDragCallback<T>, EpoxySwipeCallback<T> {

  private static final int TOUCH_DEBOUNCE_MILLIS = 300;

  @Nullable private final EpoxyController controller;
  private final Class<T> targetModelClass;
  private EpoxyViewHolder holderBeingDragged;
  private EpoxyViewHolder holderBeingSwiped;

  public EpoxyModelTouchCallback(@Nullable EpoxyController controller, Class<T> targetModelClass) {
    this.controller = controller;
    this.targetModelClass = targetModelClass;
  }

  @Override
  protected int getMovementFlags(RecyclerView recyclerView, EpoxyViewHolder viewHolder) {
    EpoxyModel<?> model = viewHolder.getModel();

    // If multiple touch callbacks are registered on the recyclerview (to support combinations of
    // dragging and dropping) then we won't want to enable anything if another
    // callback has a view actively selected.
    boolean isOtherCallbackActive =
        holderBeingDragged == null
            && holderBeingSwiped == null
            && recyclerViewHasSelection(recyclerView);

    if (!isOtherCallbackActive && isTouchableModel(model)) {
      //noinspection unchecked
      return getMovementFlagsForModel((T) model, viewHolder.getAdapterPosition());
    } else {
      return 0;
    }
  }

  @Override
  protected boolean canDropOver(RecyclerView recyclerView, EpoxyViewHolder current,
      EpoxyViewHolder target) {
    // By default we don't allow dropping on a model that isn't a drag target
    return isTouchableModel(target.getModel());
  }

  protected boolean isTouchableModel(EpoxyModel<?> model) {
    return targetModelClass.isInstance(model);
  }

  @Override
  protected boolean onMove(RecyclerView recyclerView, EpoxyViewHolder viewHolder,
      EpoxyViewHolder target) {

    if (controller == null) {
      throw new IllegalStateException(
          "A controller must be provided in the constructor if dragging is enabled");
    }

    int fromPosition = viewHolder.getAdapterPosition();
    int toPosition = target.getAdapterPosition();
    controller.moveModel(fromPosition, toPosition);

    EpoxyModel<?> model = viewHolder.getModel();
    if (!isTouchableModel(model)) {
      throw new IllegalStateException(
          "A model was dragged that is not a valid target: " + model.getClass());
    }

    //noinspection unchecked
    onModelMoved(fromPosition, toPosition, (T) model, viewHolder.itemView);
    return true;
  }

  @Override
  public void onModelMoved(int fromPosition, int toPosition, T modelBeingMoved, View itemView) {

  }

  @Override
  protected void onSwiped(EpoxyViewHolder viewHolder, int direction) {
    EpoxyModel<?> model = viewHolder.getModel();
    View view = viewHolder.itemView;
    int position = viewHolder.getAdapterPosition();

    if (!isTouchableModel(model)) {
      throw new IllegalStateException(
          "A model was swiped that is not a valid target: " + model.getClass());
    }

    //noinspection unchecked
    onSwipeCompleted((T) model, view, position, direction);
  }

  @Override
  public void onSwipeCompleted(T model, View itemView, int position, int direction) {

  }

  @Override
  protected void onSelectedChanged(@Nullable EpoxyViewHolder viewHolder, int actionState) {
    super.onSelectedChanged(viewHolder, actionState);

    if (viewHolder != null) {
      EpoxyModel<?> model = viewHolder.getModel();
      if (!isTouchableModel(model)) {
        throw new IllegalStateException(
            "A model was selected that is not a valid target: " + model.getClass());
      }

      markRecyclerViewHasSelection((RecyclerView) viewHolder.itemView.getParent());

      if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
        holderBeingSwiped = viewHolder;
        //noinspection unchecked
        onSwipeStarted((T) model, viewHolder.itemView, viewHolder.getAdapterPosition());
      } else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
        holderBeingDragged = viewHolder;
        //noinspection unchecked
        onDragStarted((T) model, viewHolder.itemView, viewHolder.getAdapterPosition());
      }
    } else if (holderBeingDragged != null) {
      //noinspection unchecked
      onDragReleased((T) holderBeingDragged.getModel(), holderBeingDragged.itemView);
      holderBeingDragged = null;
    } else if (holderBeingSwiped != null) {
      //noinspection unchecked
      onSwipeReleased((T) holderBeingSwiped.getModel(), holderBeingSwiped.itemView);
      holderBeingSwiped = null;
    }
  }

  private void markRecyclerViewHasSelection(RecyclerView recyclerView) {
    recyclerView.setTag(R.id.epoxy_touch_helper_selection_status, Boolean.TRUE);
  }

  private boolean recyclerViewHasSelection(RecyclerView recyclerView) {
    return recyclerView.getTag(R.id.epoxy_touch_helper_selection_status) != null;
  }

  private void clearRecyclerViewSelectionMarker(RecyclerView recyclerView) {
    recyclerView.setTag(R.id.epoxy_touch_helper_selection_status, null);
  }

  @Override
  public void onSwipeStarted(T model, View itemView, int adapterPosition) {

  }

  @Override
  public void onSwipeReleased(T model, View itemView) {

  }

  @Override
  public void onDragStarted(T model, View itemView, int adapterPosition) {

  }

  @Override
  public void onDragReleased(T model, View itemView) {

  }

  @Override
  protected void clearView(final RecyclerView recyclerView, EpoxyViewHolder viewHolder) {
    super.clearView(recyclerView, viewHolder);
    //noinspection unchecked
    clearView((T) viewHolder.getModel(), viewHolder.itemView);

    // If multiple touch helpers are in use, one touch helper can pick up buffered touch inputs
    // immediately after another touch event finishes. This leads to things like a view being
    // selected for drag when another view finishes its swipe off animation. To prevent that we
    // keep the recyclerview marked as having an active selection for a brief period after a
    // touch event ends.
    recyclerView.postDelayed(new Runnable() {
      @Override
      public void run() {
        clearRecyclerViewSelectionMarker(recyclerView);
      }
    }, TOUCH_DEBOUNCE_MILLIS);
  }

  @Override
  public void clearView(T model, View itemView) {

  }

  @Override
  protected void onChildDraw(Canvas c, RecyclerView recyclerView, EpoxyViewHolder viewHolder,
      float dX, float dY, int actionState, boolean isCurrentlyActive) {
    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

    EpoxyModel<?> model;
    // It’s possible for a touch helper to still draw if the item is being removed, which means it
    // has technically be unbound by that point. getModel will throw an exception in this case.
    try {
      model = viewHolder.getModel();
    } catch (IllegalStateException ignored) {
      return;
    }

    if (!isTouchableModel(model)) {
      throw new IllegalStateException(
          "A model was selected that is not a valid target: " + model.getClass());
    }

    View itemView = viewHolder.itemView;

    float swipeProgress;
    if (Math.abs(dX) > Math.abs(dY)) {
      swipeProgress = dX / itemView.getWidth();
    } else {
      swipeProgress = dY / itemView.getHeight();
    }

    // Clamp to 1/-1 in the case of side padding where the view can be swiped extra
    float clampedProgress = Math.max(-1f, Math.min(1f, swipeProgress));

    //noinspection unchecked
    onSwipeProgressChanged((T) model, itemView, clampedProgress, c);
  }

  @Override
  public void onSwipeProgressChanged(T model, View itemView, float swipeProgress,
      Canvas canvas) {

  }
}
