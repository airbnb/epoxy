package com.airbnb.epoxy.paging3

import android.os.Handler
import android.os.Looper
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.CombinedLoadStates
import androidx.paging.ItemSnapshotList
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.airbnb.epoxy.EpoxyModel
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow

/**
 * A pagingData stream wrapper that caches models built for each item. It tracks changes in paged Data and caches
 * models for each item when they are invalidated to avoid rebuilding models for the whole list when pagingData is
 * updated.
 *
 * The pagingData submitted to this cache must be kept in sync with the model cache. To do this,
 * the executor of the pagingData differ is set to the same thread as the model building handler.
 * However, change notifications from the PageList happen on that list's notify executor which is
 * out of our control, and we require the user to configure that properly, or an error is thrown.
 *
 * There are two special cases:
 *
 * 1. The first time models are built happens synchronously for immediate UI. In this case we don't
 * use the model cache (to avoid data synchronization issues), but attempt to fill the cache with
 * the models later.
 *
 * 2. When a pagingData is submitted it can trigger update callbacks synchronously. Since we don't control
 * that thread we allow a special case of cache modification when a new list is being submitted,
 * and all cache access is marked with @Synchronize to ensure safety when this happens.
 */
internal class PagedDataModelCache<T : Any>(
    private val modelBuilder: (itemIndex: Int, item: T?) -> EpoxyModel<*>,
    private val rebuildCallback: () -> Unit,
    itemDiffCallback: DiffUtil.ItemCallback<T>,
    private val modelBuildingHandler: Handler
) {
    /**
     * Backing list for built models. This is a full array list that has null items for not yet build models.
     */
    private val modelCache = arrayListOf<EpoxyModel<*>?>()

    /**
     * Tracks the last accessed position so that we can report it back to the paged list when models are built.
     */
    private var lastPosition: Int? = null

    /**
     * Set to true while a new list is being submitted, so that we can ignore the update callback
     * thread restriction.
     */
    private var inSubmitList: Boolean = false

    /**
     * Observer for the PagedList changes that invalidates the model cache when data is updated.
     */
    private val updateCallback = object : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) = synchronizedWithCache {
            assertUpdateCallbacksAllowed()
            (position until (position + count)).forEach {
                modelCache[it] = null
            }
            rebuildCallback()
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) = synchronizedWithCache {
            assertUpdateCallbacksAllowed()
            val model = modelCache.removeAt(fromPosition)
            modelCache.add(toPosition, model)
            rebuildCallback()
        }

        override fun onInserted(position: Int, count: Int) = synchronizedWithCache {
            assertUpdateCallbacksAllowed()
            repeat(count) {
                modelCache.add(position, null)
            }
            rebuildCallback()
        }

        override fun onRemoved(position: Int, count: Int) = synchronizedWithCache {
            assertUpdateCallbacksAllowed()
            repeat(count) {
                modelCache.removeAt(position)
            }
            rebuildCallback()
        }

        private fun synchronizedWithCache(block: () -> Unit) {
            synchronized(this@PagedDataModelCache) {
                block()
            }
        }
    }

    /**
     * Changes to the paged list must happen on the same thread as changes to the model cache to
     * ensure they stay in sync.
     *
     * We can't force this to happen, and must instead rely on user's configuration, but we can alert
     * when it is not configured correctly.
     *
     * An exception is thrown if the callback happens due to a new paged list being submitted, which can
     * trigger a synchronous callback if the list goes from null to non null, or vice versa.
     *
     * Synchronization on [submitData] and other model cache access methods prevent issues when
     * that happens.
     */
    private fun assertUpdateCallbacksAllowed() {
        require(inSubmitList || Looper.myLooper() == modelBuildingHandler.looper) {
            "The notify executor for your PagedList must use the same thread as the model building handler set in PagedListEpoxyController.modelBuildingHandler"
        }
    }

    private val dispatcher = modelBuildingHandler.asCoroutineDispatcher()
    private val asyncDiffer = AsyncPagingDataDiffer(
        diffCallback = itemDiffCallback,
        updateCallback = updateCallback,
        mainDispatcher = dispatcher,
        workerDispatcher = dispatcher
    )

    val loadStateFlow: Flow<CombinedLoadStates> get() = asyncDiffer.loadStateFlow

    @Synchronized
    suspend fun submitData(pagingData: PagingData<T>) {
        inSubmitList = true
        asyncDiffer.submitData(pagingData)
        inSubmitList = false
    }

    @Synchronized
    fun getModels(): List<EpoxyModel<*>> {
        val currentList = asyncDiffer.snapshot()

        // The first time models are built the EpoxyController does so synchronously, so that
        // the UI can be ready immediately. To avoid concurrent modification issues with the PagedList
        // and model cache we can't allow that first build to touch the cache.
        if (Looper.myLooper() != modelBuildingHandler.looper) {
            return currentList.mapIndexed { position, item ->
                modelBuilder(position, item)
            }
        }

        (0 until modelCache.size).forEach { position ->
            if (modelCache[position] == null) {
                modelCache[position] = modelBuilder(position, currentList[position])
            }
        }

        lastPosition?.let {
            triggerLoadAround(it)
        }
        @Suppress("UNCHECKED_CAST")
        return modelCache as List<EpoxyModel<*>>
    }

    /**
     * Clears all cached models to force them to be rebuilt next time models are retrieved.
     * This is posted to the model building thread to maintain data synchronicity.
     */
    fun clearModels() {
        modelBuildingHandler.post {
            clearModelsSynchronized()
        }
    }

    fun retry() {
        asyncDiffer.retry()
    }

    fun refresh() {
        asyncDiffer.refresh()
    }

    fun addLoadStateListener(listener: (CombinedLoadStates) -> Unit) {
        asyncDiffer.addLoadStateListener(listener)
    }

    fun removeLoadStateListener(listener: (CombinedLoadStates) -> Unit) {
        asyncDiffer.removeLoadStateListener(listener)
    }

    fun snapshot(): ItemSnapshotList<T> {
        return asyncDiffer.snapshot()
    }

    @Synchronized
    private fun clearModelsSynchronized() {
        modelCache.fill(null)
    }

    fun loadAround(position: Int) {
        triggerLoadAround(position)
        lastPosition = position
    }

    private fun triggerLoadAround(position: Int) {
        if (asyncDiffer.itemCount > 0) {
            asyncDiffer.getItem(position.coerceIn(0, asyncDiffer.itemCount - 1))
        }
    }
}
