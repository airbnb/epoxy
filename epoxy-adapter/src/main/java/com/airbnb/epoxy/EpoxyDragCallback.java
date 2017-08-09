package com.airbnb.epoxy;

import android.view.View;

/**
 * For use with {@link EpoxyModelTouchCallback}
 */
public interface EpoxyDragCallback<T extends EpoxyModel> extends BaseEpoxyTouchCallback<T> {

  /**
   * Called when the view switches from an idle state to a dragged state, as the user begins a drag
   * interaction with it. You can use this callback to modify the view to indicate it is being
   * dragged.
   * <p>
   * This is the first callback in the lifecycle of a drag event.
   *
   * @param model           The model representing the view that is being dragged
   * @param itemView        The view that is being dragged
   * @param adapterPosition The adapter position of the model
   */
  void onDragStarted(T model, View itemView, int adapterPosition);

  /**
   * Called after {@link #onDragStarted(EpoxyModel, View, int)} when the dragged view is dropped to
   * a new position. The EpoxyController will be updated automatically for you to reposition the
   * models and notify the RecyclerView of the change.
   * <p>
   * You MUST use this callback to modify your data backing the models to reflect the change.
   * <p>
   * The next callback in the drag lifecycle will be {@link #onDragStarted(EpoxyModel, View, int)}
   *
   * @param modelBeingMoved The model representing the view that was moved
   * @param itemView        The view that was moved
   * @param fromPosition    The adapter position that the model came from
   * @param toPosition      The new adapter position of the model
   */
  void onModelMoved(int fromPosition, int toPosition, T modelBeingMoved, View itemView);

  /**
   * Called after {@link #onDragStarted(EpoxyModel, View, int)} when the view being dragged is
   * released. If the view was dragged to a new, valid location then {@link #onModelMoved(int, int,
   * EpoxyModel, View)} will be called before this and the view will settle to the new location.
   * Otherwise the view will animate back to its original position.
   * <p>
   * You can use this callback to modify the view as it animates back into position.
   * <p>
   * {@link BaseEpoxyTouchCallback#clearView(EpoxyModel, View)} will be called after this, when the
   * view has finished animating. Final cleanup of the view should be done there.
   *
   * @param model    The model representing the view that is being released
   * @param itemView The view that was being dragged
   */
  void onDragReleased(T model, View itemView);
}
