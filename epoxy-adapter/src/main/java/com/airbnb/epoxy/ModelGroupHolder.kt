package com.airbnb.epoxy

import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.recyclerview.widget.RecyclerView

import com.airbnb.viewmodeladapter.R

import java.util.ArrayList

class ModelGroupHolder : EpoxyHolder() {
    val viewHolders = ArrayList<EpoxyViewHolder>(4)
    private lateinit var poolReference: PoolReference

    /**
     * Get the root view group (aka
     * [androidx.recyclerview.widget.RecyclerView.ViewHolder.itemView].
     * You can override [EpoxyModelGroup.bind] and use this method to make custom
     * changes to the root view.
     */
    lateinit var rootView: ViewGroup
        private set

    private lateinit var childContainer: ViewGroup
    private lateinit var stubs: List<ViewStubData>
    private var boundGroup: EpoxyModelGroup? = null

    private fun usingStubs(): Boolean = stubs.isNotEmpty()

    override fun bindView(itemView: View) {
        if (itemView !is ViewGroup) {
            throw IllegalStateException(
                "The layout provided to EpoxyModelGroup must be a ViewGroup"
            )
        }

        rootView = itemView
        childContainer = findChildContainer(rootView)
        poolReference = ACTIVITY_RECYCLER_POOL.getPool(itemView.context)

        stubs = if (childContainer.childCount != 0) {
            createViewStubData(childContainer)
        } else {
            emptyList()
        }
    }

    /**
     * By default the outermost viewgroup is used as the container that views are added to. However,
     * users can specify a different, nested view group to use as the child container by marking it
     * with a special id.
     */
    private fun findChildContainer(outermostRoot: ViewGroup): ViewGroup {
        val customRoot = outermostRoot.findViewById<View>(R.id.epoxy_model_group_child_container)

        return customRoot as? ViewGroup ?: outermostRoot
    }

    private fun createViewStubData(viewGroup: ViewGroup): List<ViewStubData> {
        val stubs = ArrayList<ViewStubData>(4)

        while (true) {
            val stubData = getNextViewStub(viewGroup)
            if (stubData != null) {
                stubs.add(stubData)
                stubData.removeView()
            } else {
                break
            }
        }

        if (stubs.isEmpty()) {
            throw IllegalStateException(
                "No view stubs found. If viewgroup is not empty it must contain ViewStubs."
            )
        }

        return stubs
    }

    private fun getNextViewStub(viewGroup: ViewGroup): ViewStubData? {
        val childCount = viewGroup.childCount
        for (i in 0 until childCount) {
            val child = viewGroup.getChildAt(i)

            if (child is ViewGroup) {
                getNextViewStub(child)?.let { return it }
            } else if (child is ViewStub) {
                return ViewStubData(viewGroup, child, i)
            }
        }

        return null
    }

    fun bindGroupIfNeeded(group: EpoxyModelGroup) {
        val previouslyBoundGroup = this.boundGroup

        if (previouslyBoundGroup === group) {
            return
        } else if (previouslyBoundGroup != null) {
            // A different group is being bound; this can happen when an onscreen model is changed.
            // The models or their layouts could have changed, so views may need to be updated

            if (previouslyBoundGroup.models.size > group.models.size) {
                for (i in previouslyBoundGroup.models.size - 1 downTo group.models.size) {
                    removeAndRecycleView(i)
                }
            }
        }

        this.boundGroup = group
        val models = group.models
        val modelCount = models.size

        if (usingStubs() && stubs.size < modelCount) {
            throw IllegalStateException(
                "Insufficient view stubs for EpoxyModelGroup. " + modelCount +
                        " models were provided but only " + stubs.size + " view stubs exist."
            )
        }
        viewHolders.ensureCapacity(modelCount)

        for (i in 0 until modelCount) {
            val model = models[i]
            val previouslyBoundModel = previouslyBoundGroup?.models?.getOrNull(i)
            val stubData = stubs.getOrNull(i)
            val parent = stubData?.viewGroup ?: childContainer

            if (previouslyBoundModel != null) {
                if (areSameViewType(previouslyBoundModel, model)) {
                    continue
                }

                removeAndRecycleView(i)
            }

            val holder = getViewHolder(parent, model)

            if (stubData == null) {
                childContainer.addView(holder.itemView, i)
            } else {
                stubData.setView(holder.itemView, group.useViewStubLayoutParams(model, i))
            }

            viewHolders.add(i, holder)
        }
    }

    private fun areSameViewType(model1: EpoxyModel<*>, model2: EpoxyModel<*>?): Boolean {
        return ViewTypeManager.getViewType(model1) == ViewTypeManager.getViewType(model2)
    }

    private fun getViewHolder(parent: ViewGroup, model: EpoxyModel<*>): EpoxyViewHolder {
        val viewType = ViewTypeManager.getViewType(model)
        val recycledView = poolReference.viewPool.getRecycledView(viewType)

        return recycledView as? EpoxyViewHolder
            ?: HELPER_ADAPTER.createViewHolder(
                model,
                parent,
                viewType
            )
    }

    fun unbindGroup() {
        if (boundGroup == null) {
            throw IllegalStateException("Group is not bound")
        }

        repeat(viewHolders.size) {
            // Remove from the end for more efficient list actions
            removeAndRecycleView(viewHolders.size - 1)
        }

        poolReference.clearIfDestroyed()
        boundGroup = null
    }

    private fun removeAndRecycleView(modelPosition: Int) {
        if (usingStubs()) {
            stubs[modelPosition].removeView()
        } else {
            childContainer.removeViewAt(modelPosition)
        }

        val viewHolder = viewHolders.removeAt(modelPosition)
        viewHolder.unbind()
        poolReference.viewPool.putRecycledView(viewHolder)
    }

    companion object {

        private val HELPER_ADAPTER = HelperAdapter()
        private val ACTIVITY_RECYCLER_POOL = ActivityRecyclerPool()
    }
}

private class ViewStubData(
    val viewGroup: ViewGroup,
    val viewStub: ViewStub,
    val position: Int
) {

    fun setView(view: View, useStubLayoutParams: Boolean) {
        // Carry over the stub id manually since we aren't inflating via the stub
        val inflatedId = viewStub.inflatedId
        if (inflatedId != View.NO_ID) {
            view.id = inflatedId
        }

        if (useStubLayoutParams) {
            viewGroup.addView(view, position, viewStub.layoutParams)
        } else {
            viewGroup.addView(view, position)
        }
    }

    fun removeView() {
        val view = viewGroup.getChildAt(position)
            ?: throw IllegalStateException("No view exists at position $position")
        viewGroup.removeView(view)
    }
}

/**
 * A viewholder's viewtype can only be set internally in an adapter when the viewholder
 * is created. To work around that we do the creation in an adapter.
 */
private class HelperAdapter : RecyclerView.Adapter<EpoxyViewHolder>() {

    private var model: EpoxyModel<*>? = null

    fun createViewHolder(model: EpoxyModel<*>, parent: ViewGroup, viewType: Int): EpoxyViewHolder {
        this.model = model
        val viewHolder = createViewHolder(parent, viewType)
        this.model = null
        return viewHolder
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpoxyViewHolder {
        return EpoxyViewHolder(model!!.buildView(parent), model!!.shouldSaveViewState())
    }

    override fun onBindViewHolder(holder: EpoxyViewHolder, position: Int) {
    }

    override fun getItemCount() = 1
}
