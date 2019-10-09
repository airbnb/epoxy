package com.airbnb.epoxy.stickyheader

import android.view.View

import androidx.recyclerview.widget.RecyclerView

/**
 * Adds sticky headers capabilities to the [RecyclerView.Adapter]. Should return `true` for all
 * positions that represent sticky headers.
 */
interface HasStickyHeader {

    /**
     * Returns true if the view at the specified [position] needs to be sticky
     * else false.
     */
    fun isStickyHeader(position: Int): Boolean

    //region Optional callbacks

    /**
     * Adjusts any necessary properties of the `holder` that is being used as a sticky header.
     *
     * [teardownStickyHeaderView] will be called sometime after this method
     * and before any other calls to this method go through.
     */
    fun setupStickyHeaderView(stickyHeader: View) = Unit

    /**
     * Reverts any properties changed in [setupStickyHeaderView].
     *
     * Called after [setupStickyHeaderView].
     */
    fun teardownStickyHeaderView(stickyHeader: View) = Unit

    //endregion
}