package com.airbnb.epoxy;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.List;

/**
 * A wrapper around {@link android.support.v7.widget.helper.ItemTouchHelper.Callback} to cast all
 * view holders to {@link com.airbnb.epoxy.EpoxyViewHolder} for simpler use with Epoxy.
 */
public abstract class EpoxyTouchHelperCallback extends ItemTouchHelper.Callback {

  @Override
  public final int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
    return getMovementFlags(recyclerView, (EpoxyViewHolder) viewHolder);
  }

  /**
   * @see #getMovementFlags(RecyclerView, ViewHolder)
   */
  protected abstract int getMovementFlags(RecyclerView recyclerView, EpoxyViewHolder viewHolder);

  @Override
  public final boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
    return onMove(recyclerView, (EpoxyViewHolder) viewHolder, (EpoxyViewHolder) target);
  }

  /**
   * @see #onMove(RecyclerView, ViewHolder, ViewHolder)
   */
  protected abstract boolean onMove(RecyclerView recyclerView, EpoxyViewHolder viewHolder,
      EpoxyViewHolder target);

  @Override
  public final void onSwiped(ViewHolder viewHolder, int direction) {
    onSwiped((EpoxyViewHolder) viewHolder, direction);
  }

  /**
   * @see #onSwiped(ViewHolder, int)
   */
  protected abstract void onSwiped(EpoxyViewHolder viewHolder, int direction);

  @Override
  public final boolean canDropOver(RecyclerView recyclerView, ViewHolder current,
      ViewHolder target) {
    return canDropOver(recyclerView, (EpoxyViewHolder) current, (EpoxyViewHolder) target);
  }

  /**
   * @see #canDropOver(RecyclerView, ViewHolder, ViewHolder)
   */
  protected boolean canDropOver(RecyclerView recyclerView, EpoxyViewHolder current,
      EpoxyViewHolder target) {
    return super.canDropOver(recyclerView, current, target);
  }

  @Override
  public final float getSwipeThreshold(ViewHolder viewHolder) {
    return getSwipeThreshold((EpoxyViewHolder) viewHolder);
  }

  /**
   * @see #getSwipeThreshold(ViewHolder)
   */
  protected float getSwipeThreshold(EpoxyViewHolder viewHolder) {
    return super.getSwipeThreshold(viewHolder);
  }

  @Override
  public final float getMoveThreshold(ViewHolder viewHolder) {
    return getMoveThreshold((EpoxyViewHolder) viewHolder);
  }

  /**
   * @see #getMoveThreshold(ViewHolder)
   */
  protected float getMoveThreshold(EpoxyViewHolder viewHolder) {
    return super.getMoveThreshold(viewHolder);
  }

  @Override
  public final ViewHolder chooseDropTarget(ViewHolder selected, List dropTargets, int curX,
      int curY) {
    //noinspection unchecked
    return chooseDropTarget((EpoxyViewHolder) selected, (List<EpoxyViewHolder>) dropTargets, curX,
        curY);
  }

  /**
   * @see #chooseDropTarget(ViewHolder, List, int, int)
   */
  protected EpoxyViewHolder chooseDropTarget(EpoxyViewHolder selected,
      List<EpoxyViewHolder> dropTargets, int curX, int curY) {

    //noinspection unchecked
    return (EpoxyViewHolder) super.chooseDropTarget(selected, (List) dropTargets, curX, curY);
  }

  @Override
  public final void onSelectedChanged(ViewHolder viewHolder, int actionState) {
    onSelectedChanged((EpoxyViewHolder) viewHolder, actionState);
  }

  /**
   * @see #onSelectedChanged(ViewHolder, int)
   */
  protected void onSelectedChanged(EpoxyViewHolder viewHolder, int actionState) {
    super.onSelectedChanged(viewHolder, actionState);
  }

  @Override
  public final void onMoved(RecyclerView recyclerView, ViewHolder viewHolder, int fromPos,
      ViewHolder target, int toPos, int x, int y) {

    onMoved(recyclerView, (EpoxyViewHolder) viewHolder, fromPos, (EpoxyViewHolder) target, toPos, x,
        y);
  }

  /**
   * @see #onMoved(RecyclerView, ViewHolder, int, ViewHolder, int, int, int)
   */
  protected void onMoved(RecyclerView recyclerView, EpoxyViewHolder viewHolder, int fromPos,
      EpoxyViewHolder target, int toPos, int x, int y) {
    super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
  }

  @Override
  public final void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
    clearView(recyclerView, (EpoxyViewHolder) viewHolder);
  }

  /**
   * @see #clearView(RecyclerView, ViewHolder)
   */
  protected void clearView(RecyclerView recyclerView, EpoxyViewHolder viewHolder) {
    super.clearView(recyclerView, viewHolder);
  }

  @Override
  public final void onChildDraw(Canvas c, RecyclerView recyclerView, ViewHolder viewHolder,
      float dX,
      float dY, int actionState, boolean isCurrentlyActive) {

    onChildDraw(c, recyclerView, (EpoxyViewHolder) viewHolder, dX, dY, actionState,
        isCurrentlyActive);
  }

  /**
   * @see #onChildDraw(Canvas, RecyclerView, ViewHolder, float, float, int, boolean)
   */
  protected void onChildDraw(Canvas c, RecyclerView recyclerView, EpoxyViewHolder viewHolder,
      float dX, float dY, int actionState, boolean isCurrentlyActive) {
    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
  }

  @Override
  public final void onChildDrawOver(Canvas c, RecyclerView recyclerView, ViewHolder viewHolder,
      float dX,
      float dY, int actionState, boolean isCurrentlyActive) {

    onChildDrawOver(c, recyclerView, (EpoxyViewHolder) viewHolder, dX, dY, actionState,
        isCurrentlyActive);
  }

  /**
   * @see #onChildDrawOver(Canvas, RecyclerView, ViewHolder, float, float, int, boolean)
   */
  protected void onChildDrawOver(Canvas c, RecyclerView recyclerView, EpoxyViewHolder viewHolder,
      float dX, float dY, int actionState, boolean isCurrentlyActive) {

    super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
  }
}
