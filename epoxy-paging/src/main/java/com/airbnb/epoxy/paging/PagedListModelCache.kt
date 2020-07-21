/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.airbnb.epoxy.paging

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import java.util.concurrent.Executor

/**
 * A PagedList stream wrapper that caches models built for each item. It tracks changes in paged lists and caches
 * models for each item when they are invalidated to avoid rebuilding models for the whole list when PagedList is
 * updated.
 *
 * The PagedList submitted to this cache must be kept in sync with the model cache. To do this,
 * the executor of the PagedList differ is set to the same thread as the model building handler.
 * However, change notifications from the PageList happen on that list's notify executor which is
 * out of our control, and we require the user to configure that properly, or an error is thrown.
 *
 * There are two special cases:
 *
 * 1. The first time models are built happens synchronously for immediate UI. In this case we don't
 * use the model cache (to avoid data synchronization issues), but attempt to fill the cache with
 * the models later.
 *
 * 2. When a list is submitted it can trigger update callbacks synchronously. Since we don't control
 * that thread we allow a special case of cache modification when a new list is being submitted,
 * and all cache access is marked with @Synchronize to ensure safety when this happens.
 */
internal class PagedListModelCache<T>(
    private val modelBuilder: (itemIndex: Int, item: T?) -> EpoxyModel<*>,
    private val rebuildCallback: () -> Unit,
    private val itemDiffCallback: DiffUtil.ItemCallback<T>,
    private val diffExecutor: Executor? = null,
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
            (0 until count).forEach {
                modelCache.add(position, null)
            }
            rebuildCallback()
        }

        override fun onRemoved(position: Int, count: Int) = synchronizedWithCache {
            assertUpdateCallbacksAllowed()
            (0 until count).forEach {
                modelCache.removeAt(position)
            }
            rebuildCallback()
        }

        private fun synchronizedWithCache(block: () -> Unit) {
            synchronized(this@PagedListModelCache) {
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
     * Synchronization on [submitList] and other model cache access methods prevent issues when
     * that happens.
     */
    private fun assertUpdateCallbacksAllowed() {
        require(inSubmitList || Looper.myLooper() == modelBuildingHandler.looper) {
            "The notify executor for your PagedList must use the same thread as the model building handler set in PagedListEpoxyController.modelBuildingHandler"
        }
    }

    @SuppressLint("RestrictedApi")
    private val asyncDiffer = object : AsyncPagedListDiffer<T>(
        updateCallback,
        AsyncDifferConfig.Builder<T>(
            itemDiffCallback
        ).also { builder ->
            if (diffExecutor != null) {
                builder.setBackgroundThreadExecutor(diffExecutor)
            }

            // we have to reply on this private API, otherwise, paged list might be changed when models are being built,
            // potentially creating concurrent modification problems.
            builder.setMainThreadExecutor { runnable: Runnable ->
                modelBuildingHandler.post(runnable)
            }
        }.build()
    ) {
        init {
            if (modelBuildingHandler != EpoxyController.defaultModelBuildingHandler) {
                try {
                    // looks like AsyncPagedListDiffer in 1.x ignores the config.
                    // Reflection to the rescue.
                    val mainThreadExecutorField =
                        AsyncPagedListDiffer::class.java.getDeclaredField("mMainThreadExecutor")
                    mainThreadExecutorField.isAccessible = true
                    mainThreadExecutorField.set(
                        this,
                        Executor {
                            modelBuildingHandler.post(it)
                        }
                    )
                } catch (t: Throwable) {
                    val msg = "Failed to hijack update handler in AsyncPagedListDiffer." +
                        "You can only build models on the main thread"
                    Log.e("PagedListModelCache", msg, t)
                    throw IllegalStateException(msg, t)
                }
            }
        }
    }

    @Synchronized
    fun submitList(pagedList: PagedList<T>?) {
        inSubmitList = true
        asyncDiffer.submitList(pagedList)
        inSubmitList = false
    }

    @Synchronized
    fun getModels(): List<EpoxyModel<*>> {
        val currentList = asyncDiffer.currentList ?: emptyList<T>()

        // The first time models are built the EpoxyController does so synchronously, so that
        // the UI can be ready immediately. To avoid concurrent modification issues with the PagedList
        // and model cache we can't allow that first build to touch the cache.
        if (Looper.myLooper() != modelBuildingHandler.looper) {
            val initialModels = currentList.mapIndexed { position, item ->
                modelBuilder(position, item)
            }

            // If the paged list still hasn't changed then we can populate the cache
            // with the models we built to avoid needing to rebuild them later.
            modelBuildingHandler.post {
                setCacheValues(currentList, initialModels)
            }

            return initialModels
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

    @Synchronized
    private fun setCacheValues(
        originatingList: List<T>,
        initialModels: List<EpoxyModel<*>>
    ) {
        if (asyncDiffer.currentList === originatingList) {
            modelCache.clear()
            modelCache.addAll(initialModels)
        }
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

    @Synchronized
    private fun clearModelsSynchronized() {
        modelCache.fill(null)
    }

    fun loadAround(position: Int) {
        triggerLoadAround(position)
        lastPosition = position
    }

    private fun triggerLoadAround(position: Int) {
        asyncDiffer.currentList?.let {
            if (it.size > 0) {
                it.loadAround(Math.min(position, it.size - 1))
            }
        }
    }
}
