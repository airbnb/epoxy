package com.airbnb.epoxy

import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.viewmodeladapter.R
import java.util.HashMap

/**
 * A simple way to track visibility events on [EpoxyModel] within an [EpoxyViewBinder].
 *
 * [EpoxyViewBinderVisibilityTracker] works with any [View] backed by an [EpoxyModel]. Once attached
 * the events will be forwarded to the Epoxy model (or to the Epoxy view when using annotations).
 *
 * **There are a few exceptions where events are not forwarded:**
 *  * If a model is replaced with a model of the same class a new impression will not be logged.
 *  This is due to the view being the same instance and no insight into view holder changes.
 *  * View binders in scrollable views will only forward the initial visibility state. New events
 *  will not be emitted on scroll actions. This is due to not knowing when the outer view scrolls.
 */
class EpoxyViewBinderVisibilityTracker {

    /** Maintain visibility item indexed by view id (identity hashcode)  */
    private val visibilityIdToItemMap = SparseArray<EpoxyVisibilityItem>()

    /**
     * Enable or disable visibility changed event. Default is `true`, disable it if you don't need
     * (triggered by every pixel scrolled).
     *
     * @see OnVisibilityChanged
     *
     * @see OnModelVisibilityChangedListener
     */
    var onChangedEnabled = true

    /** The view that's currently attached and whose layout is being observed. */
    private var attachedView: View? = null

    /** The listener for the currently attached view. */
    private var attachedListener: Listener? = null

    /** All nested visibility trackers  */
    private val nestedTrackers: MutableMap<RecyclerView, EpoxyVisibilityTracker> = HashMap()

    /**
     * The threshold of percentage visible area to identify the partial impression view state. This
     * is in the range of [0..100] and defaults to `null`, which disables
     * [VisibilityState.PARTIAL_IMPRESSION_VISIBLE] and
     * [VisibilityState.PARTIAL_IMPRESSION_INVISIBLE] events.
     */
    @setparam:IntRange(from = 0, to = 100)
    var partialImpressionThresholdPercentage: Int? = null

    /**
     * Attaches the tracker.
     *
     * @param view The view that is backed by an [EpoxyModel].
     */
    fun attach(view: View) {
        if (attachedView !== view) {
            // Detach the old view if it exists because there is a different view
            detach()
        }
        attachedView = view
        attachedListener = Listener(view)

        // When reattaching the view, process the children even if the view instance is the same.
        // Since the view could have been recycled to be bound to another model, make sure the
        // callback is happening on the appropriate model instance.
        processChild(view, false, "attach")
        (view as? RecyclerView)?.let {
            processChildRecyclerViewAttached(it)
        }
    }

    /**
     * Detaches the tracker.
     */
    fun detach() {
        attachedView?.let { view ->
            processChild(view, true, "detach")
            (view as? RecyclerView)?.let {
                processChildRecyclerViewDetached(it)
            }
        }
        attachedView = null
        attachedListener?.detach()
    }

    /**
     * Process visibility events for a view and propagate to children of a model group if needed.
     */
    private fun processChild(child: View, detachEvent: Boolean, eventOriginForDebug: String) {
        child.viewHolder?.let { viewHolder ->
            val epoxyHolder = viewHolder.holder
            processChild(child, detachEvent, eventOriginForDebug, viewHolder)
            if (epoxyHolder is ModelGroupHolder) {
                processModelGroupChildren(epoxyHolder, detachEvent, eventOriginForDebug)
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
        epoxyHolder: ModelGroupHolder,
        detachEvent: Boolean,
        eventOriginForDebug: String
    ) {
        // Iterate through models in the group and process each of them instead of the group
        for (groupChildHolder in epoxyHolder.viewHolders) {
            // Since the group is likely using a ViewGroup other than a RecyclerView we need to
            // handle the potential of a nested RecyclerView.
            (groupChildHolder.itemView as? RecyclerView)?.let {
                if (detachEvent) {
                    processChildRecyclerViewDetached(it)
                } else {
                    processChildRecyclerViewAttached(it)
                }
            }
            processChild(
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
     */
    private fun processChild(
        child: View,
        detachEvent: Boolean,
        eventOriginForDebug: String,
        viewHolder: EpoxyViewHolder
    ) {
        val changed = processVisibilityEvents(viewHolder, detachEvent, eventOriginForDebug)
        if (changed && child is RecyclerView) {
            val tracker = nestedTrackers[child]
            tracker?.requestVisibilityCheck()
        }
    }

    /** Attach a tracker to a nested [RecyclerView]. */
    private fun processChildRecyclerViewAttached(childRecyclerView: RecyclerView) {
        // Register itself in the EpoxyVisibilityTracker. This will take care of nested list
        // tracking (ex: carousel)
        var tracker = getTracker(childRecyclerView)
        if (tracker == null) {
            tracker = EpoxyVisibilityTracker()
            tracker.partialImpressionThresholdPercentage = partialImpressionThresholdPercentage
            tracker.attach(childRecyclerView)
        }
        nestedTrackers[childRecyclerView] = tracker
    }

    /** Detach trackers from a nested [RecyclerView]. */
    private fun processChildRecyclerViewDetached(childRecyclerView: RecyclerView) {
        nestedTrackers.remove(childRecyclerView)
    }

    /**
     * Call this method every time something related to the UI changes
     * (visibility, screen position, etc).
     *
     * @param epoxyHolder the view holder for the view.
     * @return true if changed
     */
    private fun processVisibilityEvents(
        epoxyHolder: EpoxyViewHolder,
        detachEvent: Boolean,
        eventOriginForDebug: String
    ): Boolean {
        if (DEBUG_LOG) {
            Log.d(
                TAG,
                "$eventOriginForDebug.processVisibilityEvents " +
                    "${System.identityHashCode(epoxyHolder)}, $detachEvent"
            )
        }
        val itemView = epoxyHolder.itemView
        val id = System.identityHashCode(itemView)
        var vi = visibilityIdToItemMap[id]
        if (vi == null) {
            // New view discovered, assign an EpoxyVisibilityItem
            vi = EpoxyVisibilityItem()
            visibilityIdToItemMap.put(id, vi)
        }
        var changed = false
        val parent = itemView.parent as? ViewGroup ?: return changed
        if (vi.update(itemView, parent, detachEvent)) {
            // View is measured, process events
            vi.handleVisible(epoxyHolder, detachEvent)
            if (partialImpressionThresholdPercentage != null) {
                vi.handlePartialImpressionVisible(
                    epoxyHolder,
                    detachEvent,
                    partialImpressionThresholdPercentage!!
                )
            }
            vi.handleFocus(epoxyHolder, detachEvent)
            vi.handleFullImpressionVisible(epoxyHolder, detachEvent)
            changed = vi.handleChanged(epoxyHolder, onChangedEnabled)
        }
        return changed
    }

    private inner class Listener(private val view: View) : ViewTreeObserver.OnGlobalLayoutListener {

        init {
            view.viewTreeObserver.addOnGlobalLayoutListener(this)
        }

        override fun onGlobalLayout() {
            processChild(view, !view.isVisible, "onGlobalLayout")
        }

        fun detach() {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            } else {
                view.viewTreeObserver.removeGlobalOnLayoutListener(this)
            }
        }
    }

    companion object {
        private const val TAG = "EpoxyVBVisTracker"

        // Not actionable at runtime. It is only useful for internal test-troubleshooting.
        const val DEBUG_LOG = false

        @IdRes
        private val TAG_ID = R.id.epoxy_visibility_tracker

        private fun getTracker(recyclerView: RecyclerView): EpoxyVisibilityTracker? {
            return recyclerView.getTag(TAG_ID) as EpoxyVisibilityTracker?
        }
    }
}
