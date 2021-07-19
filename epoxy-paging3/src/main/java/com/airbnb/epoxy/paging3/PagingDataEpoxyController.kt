package com.airbnb.epoxy.paging3

import android.annotation.SuppressLint
import android.os.Handler
import androidx.paging.CombinedLoadStates
import androidx.paging.ItemSnapshotList
import androidx.paging.LoadState
import androidx.paging.LoadType
import androidx.paging.LoadType.REFRESH
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.RemoteMediator
import androidx.recyclerview.widget.DiffUtil
import com.airbnb.epoxy.EpoxyAsyncUtil
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyViewHolder
import kotlinx.coroutines.flow.Flow

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
abstract class PagingDataEpoxyController<T : Any>(
    /**
     * The handler to use for building models. By default this uses the main thread, but you can use
     * [EpoxyAsyncUtil.getAsyncBackgroundHandler] to do model building in the background.
     *
     * The notify thread of your PagedList (from setNotifyExecutor in the pagingData Builder) must be
     * the same as this thread. Otherwise Epoxy will crash.
     */
    modelBuildingHandler: Handler = defaultModelBuildingHandler,
    /**
     * The handler to use when calculating the diff between built model lists.
     * By default this uses the main thread, but you can use
     * [EpoxyAsyncUtil.getAsyncBackgroundHandler] to do diffing in the background.
     */
    diffingHandler: Handler = defaultDiffingHandler,
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

    /**
     * A hot [Flow] of [CombinedLoadStates] that emits a snapshot whenever the loading state of the
     * current [PagingData] changes.
     *
     * This flow is conflated, so it buffers the last update to [CombinedLoadStates] and
     * immediately delivers the current load states on collection.
     */
    val loadStateFlow: Flow<CombinedLoadStates> get() = modelCache.loadStateFlow

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

    /**
     * Retry any failed load requests that would result in a [LoadState.Error] update to this
     * [PagingDataEpoxyController]
     *
     * [LoadState.Error] can be generated from two types of load requests:
     *  * [PagingSource.load] returning [PagingSource.LoadResult.Error]
     *  * [RemoteMediator.load] returning [RemoteMediator.MediatorResult.Error]
     */
    fun retry() {
        modelCache.retry()
    }

    /**
     * Refresh the data presented by this [PagingDataEpoxyController].
     *
     * [refresh] triggers the creation of a new [PagingData] with a new instance of [PagingSource]
     * to represent an updated snapshot of the backing dataset. If a [RemoteMediator] is set,
     * calling [refresh] will also trigger a call to [RemoteMediator.load] with [LoadType] [REFRESH]
     * to allow [RemoteMediator] to check for updates to the dataset backing [PagingSource].
     */
    fun refresh() {
        modelCache.refresh()
    }

    /**
     * Add a [CombinedLoadStates] listener to observe the loading state of the current [PagingData].
     *
     * As new [PagingData] generations are submitted and displayed, the listener will be notified to
     * reflect the current [CombinedLoadStates].
     */
    fun addLoadStateListener(listener: (CombinedLoadStates) -> Unit) {
        modelCache.addLoadStateListener(listener)
    }

    /**
     * Remove a previously registered [CombinedLoadStates] listener.
     */
    fun removeLoadStateListener(listener: (CombinedLoadStates) -> Unit) {
        modelCache.removeLoadStateListener(listener)
    }

    /**
     * Returns a new [ItemSnapshotList] representing the currently presented items, including any
     * placeholders if they are enabled.
     */
    fun snapshot(): ItemSnapshotList<T> {
        return modelCache.snapshot()
    }

    /**
     * Requests a model build that will run for every model, including the ones created for the paged
     * list.
     *
     * Clears the current model cache to make sure that happens.
     */
    fun requestForcedModelBuild() {
        modelCache.clearModels()
        requestModelBuild()
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
