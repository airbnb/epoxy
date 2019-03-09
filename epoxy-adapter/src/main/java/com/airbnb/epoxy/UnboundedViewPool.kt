package com.airbnb.epoxy

import android.util.SparseArray

import java.util.LinkedList
import java.util.Queue
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import androidx.recyclerview.widget.RecyclerView.ViewHolder

/**
 * Like its parent, UnboundedViewPool lets you share Views between multiple RecyclerViews. However
 * there is no maximum number of recycled views that it will store. This usually ends up being
 * optimal, barring any hard memory constraints, as RecyclerViews do not recycle more Views than
 * they need.
 */
internal class UnboundedViewPool : RecycledViewPool() {

    private val scrapHeaps = SparseArray<Queue<ViewHolder>>()

    override fun clear() {
        scrapHeaps.clear()
    }

    override fun setMaxRecycledViews(viewType: Int, max: Int) {
        throw UnsupportedOperationException(
            "UnboundedViewPool does not support setting a maximum number of recycled views"
        )
    }

    override fun getRecycledView(viewType: Int): ViewHolder? {
        val scrapHeap = scrapHeaps.get(viewType)
        return scrapHeap?.poll()
    }

    override fun putRecycledView(viewHolder: ViewHolder) {
        getScrapHeapForType(viewHolder.itemViewType).add(viewHolder)
    }

    private fun getScrapHeapForType(viewType: Int): Queue<ViewHolder> {
        var scrapHeap: Queue<ViewHolder>? = scrapHeaps.get(viewType)
        if (scrapHeap == null) {
            scrapHeap = LinkedList()
            scrapHeaps.put(viewType, scrapHeap)
        }
        return scrapHeap
    }
}
