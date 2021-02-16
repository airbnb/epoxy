package com.airbnb.epoxy.paging3

import android.annotation.SuppressLint
import android.os.Handler
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import com.airbnb.epoxy.EpoxyAsyncUtil
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyViewHolder
import kotlinx.coroutines.ObsoleteCoroutinesApi

/**
 * An [EpoxyController] that can work with a [PagingData].
 *
 * Internally, it caches the model for each item in the [PagingData]. You must override
 * [buildItemModel] method to build the model for the given item. Since [PagingData] might include
 * `null` items if placeholders are enabled, this method needs to handle `null` values in the list.
 *
 * By default, the model for each item is added  to the model list. To change this behavior (to
 * filter items or inject extra items), you can override [addModels] function and manually add built
 * models.
 *
 * @param T The type of the item in the [PagingData].
 */
@ObsoleteCoroutinesApi
abstract class PagingDataEpoxyController<T : Any>(
    /**
     * The handler to use for building models. By default this uses the main thread, but you can use
     * [EpoxyAsyncUtil.getAsyncBackgroundHandler] to do model building in the background.
     *
     * The notify thread of your PagedList (from setNotifyExecutor in the pagingData Builder) must be
     * the same as this thread. Otherwise Epoxy will crash.
     */
    modelBuildingHandler: Handler = EpoxyAsyncUtil.getAsyncBackgroundHandler(),
    /**
     * The handler to use when calculating the diff between built model lists.
     * By default this uses the main thread, but you can use
     * [EpoxyAsyncUtil.getAsyncBackgroundHandler] to do diffing in the background.
     */
    diffingHandler: Handler = EpoxyAsyncUtil.getAsyncBackgroundHandler(),
    /**
     * [PagingDataEpoxyController] uses an [DiffUtil.ItemCallback] to detect changes between
     * [PagingData]s. By default, it relies on simple object equality but you can provide a custom
     * one if you don't use all fields in the object in your models.
     */
    itemDiffCallback: DiffUtil.ItemCallback<T> = DEFAULT_ITEM_DIFF_CALLBACK as DiffUtil.ItemCallback<T>
) : EpoxyController(modelBuildingHandler, diffingHandler) {
    // this is where we keep the already built models
    private val modelCache = PagedDataModelCache(
        modelBuilder = { pos, item ->
            buildItemModel(pos, item)
        },
        rebuildCallback = {
            requestModelBuild()
        },
        itemDiffCallback = itemDiffCallback,
        modelBuildingHandler = modelBuildingHandler
    )

    final override fun buildModels() {
        addModels(modelCache.getModels())
    }

    /**
     * This function adds all built models to the adapter. You can override this method to add extra
     * items into the model list or remove some.
     */
    open fun addModels(models: List<EpoxyModel<*>>) {
        super.add(models)
    }

    /**
     * Builds the model for a given item. This must return a single model for each item. If you want
     * to inject headers etc, you can override [addModels] function.
     *
     * If the `item` is `null`, you should provide the placeholder. If your [PagingData] is
     * configured without placeholders, you don't need to handle the `null` case.
     */
    abstract fun buildItemModel(currentPosition: Int, item: T?): EpoxyModel<*>

    override fun onModelBound(
        holder: EpoxyViewHolder,
        boundModel: EpoxyModel<*>,
        position: Int,
        previouslyBoundModel: EpoxyModel<*>?
    ) {
        // TODO the position may not be a good value if there are too many injected items.
        modelCache.loadAround(position)
    }

    /**
     * Submit a new pagingData.
     *
     * A diff will be calculated between this pagingData and the previous pagingData so you may still get calls
     * to [buildItemModel] with items from the previous PagingData.
     */
    open suspend fun submitData(pagingData: PagingData<T>) {
        modelCache.submitData(pagingData)
    }

    companion object {
        /**
         * [PagingDataEpoxyController] calculates a diff on top of the PagingData to check which
         * models are invalidated.
         * This is the default [DiffUtil.ItemCallback] which uses object equality.
         */
        val DEFAULT_ITEM_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any) = oldItem == newItem

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Any, newItem: Any) = oldItem == newItem
        }
    }
}
