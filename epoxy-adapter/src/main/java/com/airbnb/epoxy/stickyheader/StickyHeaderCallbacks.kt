package com.airbnb.epoxy.stickyheader

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Adds sticky headers capabilities to any [RecyclerView.Adapter]
 * combined with [StickyHeaderLinearLayoutManager].
 */
interface StickyHeaderCallbacks {

    /**
     * Return true if the view at the specified [position] needs to be sticky
     * else false.
     */
    fun isStickyHeader(position: Int): Boolean

    //region Optional callbacks

    /**
     * Callback to adjusts any necessary properties of the [stickyHeader] view
     * that is being used as a sticky, eg. elevation.
     * Default behaviour is no-op.
     *
     * [teardownStickyHeaderView] will be called sometime after this method
     * and before any other calls to this method go through.
     */
    fun setupStickyHeaderView(stickyHeader: View) = Unit

    /**
     * Callback to revert any properties changed in [setupStickyHeaderView].
     * Default behaviour is no-op.
     *
     * Called after [setupStickyHeaderView].
     */
    fun teardownStickyHeaderView(stickyHeader: View) = Unit

    //endregion
}
