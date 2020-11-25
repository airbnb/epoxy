package com.airbnb.epoxy

import android.util.Log
import android.util.SparseArray
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.viewmodeladapter.R
import java.util.ArrayList
import java.util.HashMap

/**
 * A simple way to track visibility events on [com.airbnb.epoxy.EpoxyModel].
 *
 * [EpoxyVisibilityTracker] works with any [androidx.recyclerview.widget.RecyclerView]
 * backed by an Epoxy controller. Once attached the events will be forwarded to the Epoxy model (or
 * to the Epoxy view when using annotations).
 *
 * Note that support for visibility events on an [EpoxyModelGroup] is somewhat limited. Only model
 * additions will receive visibility events. Models that are removed from the group will not receive
 * events (e.g. [VisibilityState.INVISIBLE]) because the model group does not keep a reference,
 * nor does it get notified of model removals.
 *
 * @see OnVisibilityChanged
 *
 * @see OnVisibilityStateChanged
 *
 * @see OnModelVisibilityChangedListener
 *
 * @see OnModelVisibilityStateChangedListener
 */
class EpoxyVisibilityTracker {

    /**
     * Used to listen to [RecyclerView.ItemAnimator] ending animations.
     */
    private val itemAnimatorFinishedListener =
        RecyclerView.ItemAnimator.ItemAnimatorFinishedListener {
            processChangeEvent(
                "ItemAnimatorFinishedListener.onAnimationsFinished",
                /* don't check item animator to prevent recursion */ false
            )
        }

    /** Maintain visibility item indexed by view id (identity hashcode)  */
    private val visibilityIdToItemMap = SparseArray<EpoxyVisibilityItem>()
    private val visibilityIdToItems: MutableList<EpoxyVisibilityItem> = ArrayList()

    /** listener used to process scroll, layout and attach events  */
    private val listener = Listener()

    /** listener used to process data events  */
    private val observer = DataObserver()

    private var attachedRecyclerView: RecyclerView? = null

    private var lastAdapterSeen: RecyclerView.Adapter<*>? = null

    /** All nested visibility trackers  */
    private val nestedTrackers: MutableMap<RecyclerView, EpoxyVisibilityTracker> = HashMap()

    /** This flag is for optimizing the process on detach. If detach is from data changed then it
     * need to re-process all views, else no need (ex: scroll). */
    private var visibleDataChanged = false

    /**
     * Enable or disable visibility changed event. Default is `true`, disable it if you don't need
     * (triggered by every pixel scrolled).
     *
     * @see OnVisibilityChanged
     *
     * @see OnModelVisibilityChangedListener
     */
    var onChangedEnabled = true

    /**
     * Set the threshold of percentage visible area to identify the partial impression view state.
     *
     * @param thresholdPercentage Percentage of visible area of an element in the range [0..100].
     * Defaults to `null`, which disables
     * [VisibilityState.PARTIAL_IMPRESSION_VISIBLE] and
     * [VisibilityState.PARTIAL_IMPRESSION_INVISIBLE] events.
     */
    @IntRange(from = 0, to = 100)
    var partialImpressionThresholdPercentage: Int? = null

    /**
     * Attach the tracker.
     *
     * @param recyclerView The recyclerview that the EpoxyController has its adapter added to.
     */
    fun attach(recyclerView: RecyclerView) {
        attachedRecyclerView = recyclerView
        recyclerView.addOnScrollListener(listener)
        recyclerView.addOnLayoutChangeListener(listener)
        recyclerView.addOnChildAttachStateChangeListener(listener)
        setTracker(recyclerView, this)
    }

    /**
     * Detach the tracker
     *
     * @param recyclerView The recycler view that the EpoxyController has its adapter added to.
     */
    fun detach(recyclerView: RecyclerView) {
        recyclerView.removeOnScrollListener(listener)
        recyclerView.removeOnLayoutChangeListener(listener)
        recyclerView.removeOnChildAttachStateChangeListener(listener)
        setTracker(recyclerView, null)
        attachedRecyclerView = null
    }

    /**
     * The tracker is storing visibility states internally and is using if to send events, only the
     * difference is sent. Use this method to clear the states and thus regenerate the visibility
     * events. This may be useful when you change the adapter on the [RecyclerView].
     */
    fun clearVisibilityStates() {
        // Clear our visibility items
        visibilityIdToItemMap.clear()
        visibilityIdToItems.clear()
    }

    /**
     * Calling this method will make the visibility tracking check and trigger events if necessary. It
     * is particularly useful when the visibility of an Epoxy model is changed outside of an Epoxy
     * RecyclerView.
     *
     * An example is when you nest an horizontal Epoxy backed RecyclerView in a non Epoxy vertical
     * RecyclerView. When the vertical RecyclerView scroll you want to notify the visibility tracker
     * attached on the horizontal RecyclerView.
     */
    fun requestVisibilityCheck() {
        processChangeEvent("requestVisibilityCheck")
    }

    /**
     * Process a change event.
     * @param debug: string for debug usually the source of the call
     * @param checkItemAnimator: true if it need to check if ItemAnimator is running
     */
    private fun processChangeEvent(debug: String, checkItemAnimator: Boolean = true) {

        // Only if attached
        val recyclerView = attachedRecyclerView ?: return

        val itemAnimator = recyclerView.itemAnimator
        if (checkItemAnimator && itemAnimator != null) {
            // `itemAnimatorFinishedListener.onAnimationsFinished` will process visibility check
            // - If the animations are running `onAnimationsFinished` will be invoked on animations end.
            // - If the animations are not running `onAnimationsFinished` will be invoked right away.
            if (itemAnimator.isRunning(itemAnimatorFinishedListener)) {
                // If running process visibility now as `onAnimationsFinished` was not yet called
                processChangeEventWithDetachedView(null, debug)
            }
        } else {
            processChangeEventWithDetachedView(null, debug)
        }
    }

    private fun processChangeEventWithDetachedView(detachedView: View?, debug: String) {

        // Only if attached
        val recyclerView = attachedRecyclerView ?: return

        // On every every events lookup for a new adapter
        processNewAdapterIfNecessary()

        // Process the detached child if any
        detachedView?.let { processChild(it, true, debug) }

        // Process all attached children
        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            if (child != null && child !== detachedView) {
                // Is some case the detached child is still in the recycler view. Don't process it as it
                // was already processed.
                processChild(child, false, debug)
            }
        }
    }

    /**
     * If there is a new adapter on the attached RecyclerView it will register the data observer and
     * clear the current visibility states
     */
    private fun processNewAdapterIfNecessary() {
        attachedRecyclerView?.adapter?.let { adapter ->
            if (lastAdapterSeen != adapter) {
                // Unregister the old adapter
                lastAdapterSeen?.unregisterAdapterDataObserver(observer)
                // Register the new adapter
                adapter.registerAdapterDataObserver(observer)
                lastAdapterSeen = adapter
            }
        }
    }

    /**
     * Don't call this method directly, it is called from
     * [EpoxyVisibilityTracker.processVisibilityEvents]
     *
     * @param child               the view to process for visibility event
     * @param detachEvent         true if the child was just detached
     * @param eventOriginForDebug a debug strings used for logs
     */
    private fun processChild(child: View, detachEvent: Boolean, eventOriginForDebug: String) {

        // Only if attached
        val recyclerView = attachedRecyclerView ?: return

        // Preemptive check for child's parent validity to prevent `IllegalArgumentException` in
        // `getChildViewHolder`.
        val isParentValid = child.parent == null || child.parent === recyclerView
        val viewHolder = if (isParentValid) recyclerView.getChildViewHolder(child) else null
        if (viewHolder is EpoxyViewHolder) {
            val epoxyHolder = viewHolder.holder
            processChild(recyclerView, child, detachEvent, eventOriginForDebug, viewHolder)
            if (epoxyHolder is ModelGroupHolder) {
                processModelGroupChildren(recyclerView, epoxyHolder, detachEvent, eventOriginForDebug)
            }
        }
    }

    /**
     * Loop through the children of the model group and process visibility events on each one in
     * relation to the model group's layout. This will attach or detach trackers to any nested
     * [RecyclerView]s.
     *
     * @param epoxyHolder         the [ModelGroupHolder] with children to process
     * @param detachEvent         true if the child was just detached
     * @param eventOriginForDebug a debug strings used for logs
     */
    private fun processModelGroupChildren(
        recyclerView: RecyclerView,
        epoxyHolder: ModelGroupHolder,
        detachEvent: Boolean,
        eventOriginForDebug: String
    ) {
        // Iterate through models in the group and process each of them instead of the group
        for (groupChildHolder in epoxyHolder.viewHolders) {
            // Since the group is likely using a ViewGroup other than a RecyclerView, handle the
            // potential of a nested RecyclerView. This cannot be done through the normal flow
            // without recursively searching through the view children.
            if (groupChildHolder.itemView is RecyclerView) {
                if (detachEvent) {
                    processChildRecyclerViewDetached(groupChildHolder.itemView)
                } else {
                    processChildRecyclerViewAttached(groupChildHolder.itemView)
                }
            }
            processChild(
                recyclerView,
                groupChildHolder.itemView,
                detachEvent,
                eventOriginForDebug,
                groupChildHolder
            )
        }
    }

    /**
     * Process visibility events for a view and propagate to a nested tracker if the view is a
     * [RecyclerView].
     *
     * @param child               the view to process for visibility event
     * @param detachEvent         true if the child was just detached
     * @param eventOriginForDebug a debug strings used for logs
     * @param viewHolder          the view holder for the child view
     */
    private fun processChild(
        recyclerView: RecyclerView,
        child: View,
        detachEvent: Boolean,
        eventOriginForDebug: String,
        viewHolder: EpoxyViewHolder
    ) {
        val changed = processVisibilityEvents(
            recyclerView,
            viewHolder,
            detachEvent,
            eventOriginForDebug
        )
        if (changed && child is RecyclerView) {
            nestedTrackers[child]?.processChangeEvent("parent")
        }
    }

    /**
     * Call this methods every time something related to ui (scroll, layout, ...) or something related
     * to data changed.
     *
     * @param recyclerView        the recycler view
     * @param epoxyHolder         the [RecyclerView]
     * @param detachEvent         true if the event originated from a view detached from the
     * recycler view
     * @param eventOriginForDebug a debug strings used for logs
     * @return true if changed
     */
    private fun processVisibilityEvents(
        recyclerView: RecyclerView,
        epoxyHolder: EpoxyViewHolder,
        detachEvent: Boolean,
        eventOriginForDebug: String
    ): Boolean {
        if (DEBUG_LOG) {
            Log.d(
                TAG,
                "$eventOriginForDebug.processVisibilityEvents " +
                    "${System.identityHashCode(epoxyHolder)}, " +
                    "$detachEvent, ${epoxyHolder.adapterPosition}"
            )
        }
        val itemView = epoxyHolder.itemView
        val id = System.identityHashCode(itemView)
        var vi = visibilityIdToItemMap[id]
        if (vi == null) {
            // New view discovered, assign an EpoxyVisibilityItem
            vi = EpoxyVisibilityItem(epoxyHolder.adapterPosition)
            visibilityIdToItemMap.put(id, vi)
            visibilityIdToItems.add(vi)
        } else if (epoxyHolder.adapterPosition != RecyclerView.NO_POSITION &&
            vi.adapterPosition != epoxyHolder.adapterPosition
        ) {
            // EpoxyVisibilityItem being re-used for a different adapter position
            vi.reset(epoxyHolder.adapterPosition)
        }
        var changed = false
        if (vi.update(itemView, recyclerView, detachEvent)) {
            // View is measured, process events
            vi.handleVisible(epoxyHolder, detachEvent)
            partialImpressionThresholdPercentage?.let { percentage ->
                vi.handlePartialImpressionVisible(
                    epoxyHolder, detachEvent,
                    percentage
                )
            }
            vi.handleFocus(epoxyHolder, detachEvent)
            vi.handleFullImpressionVisible(epoxyHolder, detachEvent)
            changed = vi.handleChanged(epoxyHolder, onChangedEnabled)
        }
        return changed
    }

    private fun processChildRecyclerViewAttached(childRecyclerView: RecyclerView) {
        // Register itself in the EpoxyVisibilityTracker. This will take care of nested list
        // tracking (ex: carousel)
        val tracker = getTracker(childRecyclerView) ?: EpoxyVisibilityTracker().let { nested ->
            nested.partialImpressionThresholdPercentage = partialImpressionThresholdPercentage
            nested.attach(childRecyclerView)
            nested
        }
        nestedTrackers[childRecyclerView] = tracker
    }

    private fun processChildRecyclerViewDetached(childRecyclerView: RecyclerView) {
        nestedTrackers.remove(childRecyclerView)
    }

    /**
     * Helper class that host the [androidx.recyclerview.widget.RecyclerView] listener
     * implementations
     */
    private inner class Listener :
        RecyclerView.OnScrollListener(),
        View.OnLayoutChangeListener,
        RecyclerView.OnChildAttachStateChangeListener {
        override fun onLayoutChange(
            recyclerView: View,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            oldLeft: Int,
            oldTop: Int,
            oldRight: Int,
            oldBottom: Int
        ) {
            processChangeEvent("onLayoutChange")
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            processChangeEvent("onScrolled")
        }

        override fun onChildViewAttachedToWindow(child: View) {
            if (child is RecyclerView) {
                processChildRecyclerViewAttached(child)
            }
            processChild(child, false, "onChildViewAttachedToWindow")
        }

        override fun onChildViewDetachedFromWindow(child: View) {
            if (child is RecyclerView) {
                processChildRecyclerViewDetached(child)
            }
            if (visibleDataChanged) {
                // On detach event caused by data set changed we need to re-process all children because
                // the removal caused the others views to changes.
                processChangeEventWithDetachedView(child, "onChildViewDetachedFromWindow")
                visibleDataChanged = false
            } else {
                processChild(child, true, "onChildViewDetachedFromWindow")
            }
        }
    }

    /**
     * The layout/scroll events are not enough to detect all sort of visibility changes. We also
     * need to look at the data events from the adapter.
     */
    internal inner class DataObserver : RecyclerView.AdapterDataObserver() {
        /**
         * Clear the current visibility statues
         */
        override fun onChanged() {
            if (notEpoxyManaged(attachedRecyclerView)) {
                return
            }
            if (DEBUG_LOG) {
                Log.d(TAG, "onChanged()")
            }
            visibilityIdToItemMap.clear()
            visibilityIdToItems.clear()
            visibleDataChanged = true
        }

        /**
         * For all items after the inserted range shift each [EpoxyVisibilityTracker] adapter
         * position by inserted item count.
         */
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            if (notEpoxyManaged(attachedRecyclerView)) {
                return
            }
            if (DEBUG_LOG) {
                Log.d(TAG, "onItemRangeInserted($positionStart, $itemCount)")
            }
            for (item in visibilityIdToItems) {
                if (item.adapterPosition >= positionStart) {
                    visibleDataChanged = true
                    item.shiftBy(itemCount)
                }
            }
        }

        /**
         * For all items after the removed range reverse-shift each [EpoxyVisibilityTracker]
         * adapter position by removed item count
         */
        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            if (notEpoxyManaged(attachedRecyclerView)) {
                return
            }
            if (DEBUG_LOG) {
                Log.d(TAG, "onItemRangeRemoved($positionStart, $itemCount)")
            }
            for (item in visibilityIdToItems) {
                if (item.adapterPosition >= positionStart) {
                    visibleDataChanged = true
                    item.shiftBy(-itemCount)
                }
            }
        }

        /**
         * This is a bit more complex, for move we need to first swap the moved position then shift the
         * items between the swap. To simplify we split any range passed to individual item moved.
         *
         * ps: anyway [androidx.recyclerview.widget.AdapterListUpdateCallback]
         * does not seem to use range for moved items.
         */
        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            if (notEpoxyManaged(attachedRecyclerView)) {
                return
            }
            for (i in 0 until itemCount) {
                onItemMoved(fromPosition + i, toPosition + i)
            }
        }

        private fun onItemMoved(fromPosition: Int, toPosition: Int) {
            if (notEpoxyManaged(attachedRecyclerView)) {
                return
            }
            if (DEBUG_LOG) {
                Log.d(TAG, "onItemRangeMoved($fromPosition, $fromPosition, 1)")
            }
            for (item in visibilityIdToItems) {
                val position = item.adapterPosition
                if (position == fromPosition) {
                    // We found the item to be moved, just swap the position.
                    item.shiftBy(toPosition - fromPosition)
                    visibleDataChanged = true
                } else if (fromPosition < toPosition) {
                    // Item will be moved down in the list
                    if (position in (fromPosition + 1)..toPosition) {
                        // Item is between the moved from and to indexes, it should move up
                        item.shiftBy(-1)
                        visibleDataChanged = true
                    }
                } else if (fromPosition > toPosition) {
                    // Item will be moved up in the list
                    if (position in toPosition until fromPosition) {
                        // Item is between the moved to and from indexes, it should move down
                        item.shiftBy(1)
                        visibleDataChanged = true
                    }
                }
            }
        }

        /**
         * @param recyclerView the recycler view
         * @return true if managed by an [BaseEpoxyAdapter]
         */
        private fun notEpoxyManaged(recyclerView: RecyclerView?): Boolean {
            return recyclerView == null || recyclerView.adapter !is BaseEpoxyAdapter
        }
    }

    companion object {
        private const val TAG = "EpoxyVisibilityTracker"

        @IdRes
        private val TAG_ID = R.id.epoxy_visibility_tracker

        /**
         * @param recyclerView the view.
         * @return the tracker for the given [RecyclerView]. Null if no tracker was attached.
         */
        private fun getTracker(recyclerView: RecyclerView): EpoxyVisibilityTracker? {
            return recyclerView.getTag(TAG_ID) as EpoxyVisibilityTracker?
        }

        /**
         * Store the tracker for the given [RecyclerView].
         * @param recyclerView the view
         * @param tracker the tracker
         */
        private fun setTracker(
            recyclerView: RecyclerView,
            tracker: EpoxyVisibilityTracker?
        ) {
            recyclerView.setTag(TAG_ID, tracker)
        }

        // Not actionable at runtime. It is only useful for internal test-troubleshooting.
        const val DEBUG_LOG = false
    }
}
