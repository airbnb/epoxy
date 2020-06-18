package com.airbnb.epoxy

import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Used in the generated models to transform normal view click listeners to model click
 * listeners.
 */
class WrappedEpoxyModelClickListener<T : EpoxyModel<*>, V> : OnClickListener, OnLongClickListener {
    // Save the original click listener to call back to when clicked.
    // This also lets us call back to the original hashCode and equals methods
    private val originalClickListener: OnModelClickListener<T, V>?
    private val originalLongClickListener: OnModelLongClickListener<T, V>?

    constructor(clickListener: OnModelClickListener<T, V>?) {
        requireNotNull(clickListener) {
            "Click listener cannot be null"
        }

        this.originalClickListener = clickListener
        originalLongClickListener = null
    }

    constructor(clickListener: OnModelLongClickListener<T, V>?) {
        requireNotNull(clickListener) {
            "Click listener cannot be null"
        }

        this.originalLongClickListener = clickListener
        originalClickListener = null
    }

    override fun onClick(view: View) {
        val modelInfo = getClickedModelInfo(view) ?: return

        @Suppress("UNCHECKED_CAST")
        originalClickListener?.onClick(
            modelInfo.model as T,
            modelInfo.boundObject as V,
            view,
            modelInfo.adapterPosition
        ) ?: error("Original click listener is null")
    }

    override fun onLongClick(view: View): Boolean {
        val modelInfo = getClickedModelInfo(view) ?: return false

        @Suppress("UNCHECKED_CAST")
        return originalLongClickListener?.onLongClick(
            modelInfo.model as T,
            modelInfo.boundObject as V,
            view,
            modelInfo.adapterPosition
        ) ?: error("Original long click listener is null")
    }

    private fun getClickedModelInfo(view: View): ClickedModelInfo? {
        val epoxyHolder = ListenersUtils.getEpoxyHolderForChildView(view)
            ?: error("Could not find RecyclerView holder for clicked view")

        val adapterPosition = epoxyHolder.adapterPosition
        if (adapterPosition == RecyclerView.NO_POSITION) return null

        val boundObject = epoxyHolder.objectToBind()

        val holderToUse = if (boundObject is ModelGroupHolder) {
            // For a model group the clicked view could belong to any of the nested models in the group.
            // We check the viewholder of each model to see if the clicked view is in that hierarchy
            // in order to figure out which model it belongs to.
            // If it doesn't match any of the nested models then it could be set by the top level
            // parent model.
            boundObject.viewHolders
                .firstOrNull { view in it.itemView.allViewsInHierarchy }
                ?: epoxyHolder
        } else {
            epoxyHolder
        }

        // We return the holder and position because since we may be returning a nested group
        // holder the callee cannot use that to get the adapter position of the main model.
        return ClickedModelInfo(
            holderToUse.model,
            adapterPosition,
            holderToUse.objectToBind()
        )
    }

    private class ClickedModelInfo(
        val model: EpoxyModel<*>,
        val adapterPosition: Int,
        val boundObject: Any
    )

    /**
     * Returns a sequence of this View plus any and all children, recursively.
     */
    private val View.allViewsInHierarchy: Sequence<View>
        get() {
            return if (this is ViewGroup) {
                children.flatMap {
                    sequenceOf(it) + if (it is ViewGroup) it.allViewsInHierarchy else emptySequence()
                }.plus(this)
            } else {
                sequenceOf(this)
            }
        }

    /** Returns a [Sequence] over the child views in this view group. */
    internal val ViewGroup.children: Sequence<View>
        get() = object : Sequence<View> {
            override fun iterator() = this@children.iterator()
        }

    /** Returns a [MutableIterator] over the views in this view group. */
    internal operator fun ViewGroup.iterator() = object : MutableIterator<View> {
        private var index = 0
        override fun hasNext() = index < childCount
        override fun next() = getChildAt(index++) ?: throw IndexOutOfBoundsException()
        override fun remove() = removeViewAt(--index)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is WrappedEpoxyModelClickListener<*, *>) {
            return false
        }

        if (if (originalClickListener != null) {
            originalClickListener != other.originalClickListener
        } else {
                other.originalClickListener != null
            }
        ) {
            return false
        }
        return if (originalLongClickListener != null) {
            originalLongClickListener == other.originalLongClickListener
        } else {
            other.originalLongClickListener == null
        }
    }

    override fun hashCode(): Int {
        var result = originalClickListener?.hashCode() ?: 0
        result = 31 * result + (originalLongClickListener?.hashCode() ?: 0)
        return result
    }
}
