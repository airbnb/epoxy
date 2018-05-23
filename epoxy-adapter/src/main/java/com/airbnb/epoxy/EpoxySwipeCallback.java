package com.airbnb.epoxy;

import android.graphics.Canvas;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;

/**
 * For use with {@link EpoxyModelTouchCallback}
 */
public interface EpoxySwipeCallback<T extends EpoxyModel> extends BaseEpoxyTouchCallback<T> {

  /**
   * Called when the view switches from an idle state to a swiped state, as the user begins a swipe
   * interaction with it. You can use this callback to modify the view to indicate it is being
   * swiped.
   * <p>
   * This is the first callback made in the lifecycle of a swipe event.
   *
   * @param model           The model representing the view that is being swiped
   * @param itemView        The view that is being swiped
   * @param adapterPosition The adapter position of the model
   */
  void onSwipeStarted(T model, View itemView, int adapterPosition);

  /**
   * Once a view has begun swiping with {@link #onSwipeStarted(EpoxyModel, View, int)} it will
   * receive this callback as the swipe distance changes. This can be called multiple times as the
   * swipe interaction progresses.
   *
   * @param model         The model representing the view that is being swiped
   * @param itemView      The view that is being swiped
   * @param swipeProgress A float from -1 to 1 representing the percentage that the view has been
   *                      swiped relative to its width. This will be positive if the view is being
   *                      swiped to the right and negative if it is swiped to the left. For
   *                      example,
   * @param canvas        The canvas on which RecyclerView is drawing its children. You can draw to
   *                      this to support custom swipe animations.
   */
  void onSwipeProgressChanged(T model, View itemView, float swipeProgress,
      Canvas canvas);

  /**
   * Called when the user has released their touch on the view. If the displacement passed the swipe
   * threshold then {@link #onSwipeCompleted(EpoxyModel, View, int, int)} will be called after this
   * and the view will be animated off screen. Otherwise the view will animate back to its original
   * position.
   *
   * @param model    The model representing the view that was being swiped
   * @param itemView The view that was being swiped
   */
  void onSwipeReleased(T model, View itemView);

  /**
   * Called after {@link #onSwipeReleased(EpoxyModel, View)} if the swipe surpassed the threshold to
   * be considered a full swipe. The view will now be animated off screen.
   * <p>
   * You MUST use this callback to remove this item from your backing data and request a model
   * update.
   * <p>
   * {@link #clearView(EpoxyModel, View)} will be called after this.
   *
   * @param model     The model representing the view that was being swiped
   * @param itemView  The view that was being swiped
   * @param position  The adapter position of the model
   * @param direction The direction that the view was swiped. Can be any of {@link
   *                  ItemTouchHelper#LEFT}, {@link ItemTouchHelper#RIGHT}, {@link
   *                  ItemTouchHelper#UP}, {@link ItemTouchHelper#DOWN} depending on what swipe
   *                  directions were enabled.
   */
  void onSwipeCompleted(T model, View itemView, int position, int direction);
}
