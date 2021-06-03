package com.airbnb.epoxy

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * A wrapper around [androidx.recyclerview.widget.ItemTouchHelper.Callback] to cast all
 * view holders to [com.airbnb.epoxy.EpoxyViewHolder] for simpler use with Epoxy.
 */
abstract class EpoxyTouchHelperCallback : ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int = getMovementFlags(recyclerView, viewHolder as EpoxyViewHolder)

    /**
     * @see getMovementFlags
     */
    protected abstract fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: EpoxyViewHolder
    ): Int

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = onMove(recyclerView, viewHolder as EpoxyViewHolder, target as EpoxyViewHolder)

    /**
     * @see onMove
     */
    protected abstract fun onMove(
        recyclerView: RecyclerView,
        viewHolder: EpoxyViewHolder,
        target: EpoxyViewHolder
    ): Boolean

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int): Unit =
        onSwiped(viewHolder as EpoxyViewHolder, direction)

    /**
     * @see onSwiped
     */
    protected abstract fun onSwiped(viewHolder: EpoxyViewHolder, direction: Int)

    override fun canDropOver(
        recyclerView: RecyclerView,
        current: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = canDropOver(recyclerView, current as EpoxyViewHolder, target as EpoxyViewHolder)

    /**
     * @see canDropOver
     */
    protected open fun canDropOver(
        recyclerView: RecyclerView,
        current: EpoxyViewHolder,
        target: EpoxyViewHolder
    ): Boolean = super.canDropOver(recyclerView, current, target)

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float =
        getSwipeThreshold(viewHolder as EpoxyViewHolder)

    /**
     * @see getSwipeThreshold
     */
    protected fun getSwipeThreshold(viewHolder: EpoxyViewHolder): Float =
        super.getSwipeThreshold(viewHolder)

    override fun getMoveThreshold(viewHolder: RecyclerView.ViewHolder): Float =
        getMoveThreshold(viewHolder as EpoxyViewHolder)

    /**
     * @see getMoveThreshold
     */
    protected fun getMoveThreshold(viewHolder: EpoxyViewHolder): Float =
        super.getMoveThreshold(viewHolder)

    @Suppress("UNCHECKED_CAST")
    override fun chooseDropTarget(
        selected: RecyclerView.ViewHolder,
        dropTargets: List<RecyclerView.ViewHolder>,
        curX: Int,
        curY: Int
    ): EpoxyViewHolder? = chooseDropTarget(
        selected as EpoxyViewHolder,
        dropTargets as List<EpoxyViewHolder>,
        curX,
        curY
    )

    /**
     * @see chooseDropTarget
     */
    protected fun chooseDropTarget(
        selected: EpoxyViewHolder,
        dropTargets: List<EpoxyViewHolder>,
        curX: Int,
        curY: Int
    ): EpoxyViewHolder? =
        super.chooseDropTarget(selected, dropTargets, curX, curY) as? EpoxyViewHolder

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int): Unit =
        onSelectedChanged(viewHolder as EpoxyViewHolder?, actionState)

    /**
     * @see onSelectedChanged
     */
    protected open fun onSelectedChanged(viewHolder: EpoxyViewHolder?, actionState: Int): Unit =
        super.onSelectedChanged(viewHolder, actionState)

    override fun onMoved(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        fromPos: Int,
        target: RecyclerView.ViewHolder,
        toPos: Int,
        x: Int,
        y: Int
    ): Unit = onMoved(
        recyclerView,
        viewHolder as EpoxyViewHolder,
        fromPos,
        target as EpoxyViewHolder,
        toPos,
        x,
        y
    )

    /**
     * @see onMoved
     */
    protected fun onMoved(
        recyclerView: RecyclerView,
        viewHolder: EpoxyViewHolder,
        fromPos: Int,
        target: EpoxyViewHolder,
        toPos: Int,
        x: Int,
        y: Int
    ): Unit = super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Unit =
        clearView(recyclerView, viewHolder as EpoxyViewHolder)

    /**
     * @see clearView
     */
    protected open fun clearView(recyclerView: RecyclerView, viewHolder: EpoxyViewHolder): Unit =
        super.clearView(recyclerView, viewHolder)

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ): Unit = onChildDraw(
        c,
        recyclerView,
        viewHolder as EpoxyViewHolder,
        dX,
        dY,
        actionState,
        isCurrentlyActive
    )

    /**
     * @see onChildDraw
     */
    protected open fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: EpoxyViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ): Unit = super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

    override fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder?,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        onChildDrawOver(
            c, recyclerView, viewHolder as? EpoxyViewHolder, dX, dY, actionState,
            isCurrentlyActive
        )
    }

    /**
     * @see onChildDrawOver
     */
    protected fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: EpoxyViewHolder?,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ): Unit = super.onChildDrawOver(
        c,
        recyclerView,
        viewHolder,
        dX,
        dY,
        actionState,
        isCurrentlyActive
    )
}
