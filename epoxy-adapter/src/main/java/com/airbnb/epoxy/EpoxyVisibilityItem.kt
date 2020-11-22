package com.airbnb.epoxy

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.RecyclerView

/**
 * This class represent an item in a [android.view.ViewGroup] and it is
 * being reused with multiple model via the update method. There is 1:1 relationship between an
 * EpoxyVisibilityItem and a child within the [android.view.ViewGroup].
 *
 * It contains the logic to compute the visibility state of an item. It will also invoke the
 * visibility callbacks on [com.airbnb.epoxy.EpoxyViewHolder]
 *
 * This class should remain non-public and is intended to be used by [EpoxyVisibilityTracker]
 * only.
 */
@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
class EpoxyVisibilityItem(adapterPosition: Int? = null) {

    private val localVisibleRect = Rect()

    var adapterPosition = RecyclerView.NO_POSITION
        private set

    @Px
    private var height = 0

    @Px
    private var width = 0

    @Px
    private var visibleHeight = 0

    @Px
    private var visibleWidth = 0

    @Px
    private var viewportHeight = 0

    @Px
    private var viewportWidth = 0
    private var partiallyVisible = false
    private var fullyVisible = false
    private var visible = false
    private var focusedVisible = false
    private var viewVisibility = View.GONE

    /** Store last value for de-duping  */
    private var lastVisibleHeightNotified: Int? = null
    private var lastVisibleWidthNotified: Int? = null
    private var lastVisibilityNotified: Int? = null

    init {
        adapterPosition?.let {
            reset(it)
        }
    }

    /**
     * Update the visibility item according the current layout.
     *
     * @param view        the current [com.airbnb.epoxy.EpoxyViewHolder]'s itemView
     * @param parent      the [android.view.ViewGroup]
     * @return true if the view has been measured
     */
    fun update(view: View, parent: ViewGroup, detachEvent: Boolean): Boolean {
        // Clear the rect before calling getLocalVisibleRect
        localVisibleRect.setEmpty()
        val viewDrawn = view.getLocalVisibleRect(localVisibleRect) && !detachEvent
        height = view.height
        width = view.width
        viewportHeight = parent.height
        viewportWidth = parent.width
        visibleHeight = if (viewDrawn) localVisibleRect.height() else 0
        visibleWidth = if (viewDrawn) localVisibleRect.width() else 0
        viewVisibility = view.visibility
        return height > 0 && width > 0
    }

    fun reset(newAdapterPosition: Int) {
        fullyVisible = false
        visible = false
        focusedVisible = false
        adapterPosition = newAdapterPosition
        lastVisibleHeightNotified = null
        lastVisibleWidthNotified = null
        lastVisibilityNotified = null
    }

    fun handleVisible(epoxyHolder: EpoxyViewHolder, detachEvent: Boolean) {
        val previousVisible = visible
        visible = !detachEvent && isVisible()
        if (visible != previousVisible) {
            if (visible) {
                epoxyHolder.visibilityStateChanged(VisibilityState.VISIBLE)
            } else {
                epoxyHolder.visibilityStateChanged(VisibilityState.INVISIBLE)
            }
        }
    }

    fun handleFocus(epoxyHolder: EpoxyViewHolder, detachEvent: Boolean) {
        val previousFocusedVisible = focusedVisible
        focusedVisible = !detachEvent && isInFocusVisible()
        if (focusedVisible != previousFocusedVisible) {
            if (focusedVisible) {
                epoxyHolder.visibilityStateChanged(VisibilityState.FOCUSED_VISIBLE)
            } else {
                epoxyHolder.visibilityStateChanged(VisibilityState.UNFOCUSED_VISIBLE)
            }
        }
    }

    fun handlePartialImpressionVisible(
        epoxyHolder: EpoxyViewHolder,
        detachEvent: Boolean,
        @IntRange(from = 0, to = 100) thresholdPercentage: Int
    ) {
        val previousPartiallyVisible = partiallyVisible
        partiallyVisible = !detachEvent && isPartiallyVisible(thresholdPercentage)
        if (partiallyVisible != previousPartiallyVisible) {
            if (partiallyVisible) {
                epoxyHolder.visibilityStateChanged(VisibilityState.PARTIAL_IMPRESSION_VISIBLE)
            } else {
                epoxyHolder.visibilityStateChanged(VisibilityState.PARTIAL_IMPRESSION_INVISIBLE)
            }
        }
    }

    fun handleFullImpressionVisible(epoxyHolder: EpoxyViewHolder, detachEvent: Boolean) {
        val previousFullyVisible = fullyVisible
        fullyVisible = !detachEvent && isFullyVisible()
        if (fullyVisible != previousFullyVisible) {
            if (fullyVisible) {
                epoxyHolder.visibilityStateChanged(VisibilityState.FULL_IMPRESSION_VISIBLE)
            }
        }
    }

    fun handleChanged(epoxyHolder: EpoxyViewHolder, visibilityChangedEnabled: Boolean): Boolean {
        var changed = false
        if (visibleHeight != lastVisibleHeightNotified || visibleWidth != lastVisibleWidthNotified || viewVisibility != lastVisibilityNotified) {
            if (visibilityChangedEnabled) {
                if (viewVisibility == View.GONE) {
                    epoxyHolder.visibilityChanged(0f, 0f, 0, 0)
                } else {
                    epoxyHolder.visibilityChanged(
                        100f / height * visibleHeight,
                        100f / width * visibleWidth,
                        visibleHeight, visibleWidth
                    )
                }
            }
            lastVisibleHeightNotified = visibleHeight
            lastVisibleWidthNotified = visibleWidth
            lastVisibilityNotified = viewVisibility
            changed = true
        }
        return changed
    }

    private fun isVisible(): Boolean {
        return viewVisibility == View.VISIBLE && visibleHeight > 0 && visibleWidth > 0
    }

    private fun isInFocusVisible(): Boolean {
        val halfViewportArea = viewportHeight * viewportWidth / 2
        val totalArea = height * width
        val visibleArea = visibleHeight * visibleWidth
        // The model has entered the focused range either if it is larger than half of the viewport
        // and it occupies at least half of the viewport or if it is smaller than half of the viewport
        // and it is fully visible.
        return viewVisibility == View.VISIBLE &&
            if (totalArea >= halfViewportArea) visibleArea >= halfViewportArea else totalArea == visibleArea
    }

    private fun isPartiallyVisible(
        @IntRange(
            from = 0,
            to = 100
        ) thresholdPercentage: Int
    ): Boolean {
        // special case 0%: trigger as soon as some pixels are one the screen
        if (thresholdPercentage == 0) return isVisible()
        val totalArea = height * width
        val visibleArea = visibleHeight * visibleWidth
        val visibleAreaPercentage = visibleArea / totalArea.toFloat() * 100
        return viewVisibility == View.VISIBLE && visibleAreaPercentage >= thresholdPercentage
    }

    private fun isFullyVisible(): Boolean {
        return viewVisibility == View.VISIBLE && visibleHeight == height && visibleWidth == width
    }

    fun shiftBy(offsetPosition: Int) {
        adapterPosition += offsetPosition
    }
}
