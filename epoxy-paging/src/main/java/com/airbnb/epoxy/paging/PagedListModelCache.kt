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
import android.arch.paging.AsyncPagedListDiffer
import android.arch.paging.PagedList
import android.os.Handler
import android.support.v7.recyclerview.extensions.AsyncDifferConfig
import android.support.v7.util.DiffUtil
import android.support.v7.util.ListUpdateCallback
import android.util.Log
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import java.lang.IllegalStateException
import java.util.concurrent.Executor

/**
 * A PagedList stream wrapper that caches models built for each item. It tracks changes in paged lists and caches
 * models for each item when they are invalidated to avoid rebuilding models for the whole list when PagedList is
 * updated.
 */
internal class PagedListModelCache<T>(
    private val modelBuilder: (itemIndex: Int, item: T?) -> EpoxyModel<*>,
    private val rebuildCallback: () -> Unit,
    private val itemDiffCallback : DiffUtil.ItemCallback<T>,
    private val diffExecutor : Executor? = null,
    private val modelBuildingHandler : Handler
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
   * Observer for the PagedList changes that invalidates the model cache when data is updated.
   */
  private val updateCallback = object : ListUpdateCallback {
    override fun onChanged(position: Int, count: Int, payload: Any?) {
      (position until (position + count)).forEach {
        modelCache[it] = null
      }
      rebuildCallback()
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        val model = modelCache.removeAt(fromPosition)
        modelCache.add(toPosition, model)
        rebuildCallback()
    }

    override fun onInserted(position: Int, count: Int) {
        (0 until count).forEach { _ ->
          modelCache.add(position, null)
        }
        rebuildCallback()
    }

    override fun onRemoved(position: Int, count: Int) {
        (0 until count).forEach { _ ->
          modelCache.removeAt(position)
        }
        rebuildCallback()
    }
  }

  private val asyncDiffer = @SuppressLint("RestrictedApi")
  object : AsyncPagedListDiffer<T>(
      updateCallback,
      AsyncDifferConfig.Builder<T>(
          itemDiffCallback
      ).also {builder ->
        if (diffExecutor != null) {
          builder.setBackgroundThreadExecutor(diffExecutor)
        }
        // we have to reply on this private API, otherwise, paged list might be changed when models are being built,
        // potentially creating concurrent modification problems.
        builder.setMainThreadExecutor {runnable : Runnable ->
          modelBuildingHandler.post(runnable)
        }
      }.build()
  ){
      init {
          if (modelBuildingHandler != EpoxyController.defaultModelBuildingHandler) {
              try {
                  // looks like AsyncPagedListDiffer in 1.x ignores the config.
                  // Reflection to the rescue.
                  val mainThreadExecutorField =
                      AsyncPagedListDiffer::class.java.getDeclaredField("mMainThreadExecutor")
                  mainThreadExecutorField.isAccessible = true
                  mainThreadExecutorField.set(this, Executor {
                      modelBuildingHandler.post(it)
                  })
              } catch (t : Throwable) {
                  val msg = "Failed to hijack update handler in AsyncPagedListDiffer." +
                          "You can only build models on the main thread"
                  Log.e("PagedListModelCache", msg, t)
                  throw IllegalStateException(msg, t)
              }
          }
      }
  }

  fun submitList(pagedList: PagedList<T>?) {
    asyncDiffer.submitList(pagedList)
  }

  private fun getOrBuildModel(pos: Int): EpoxyModel<*> {
    modelCache[pos]?.let {
      return it
    }
    return modelBuilder(pos, asyncDiffer.currentList?.get(pos)).also {
      modelCache[pos] = it
    }
  }

  fun getModels(): List<EpoxyModel<*>> {
    (0 until modelCache.size).forEach {
      getOrBuildModel(it)
    }
    lastPosition?.let {
      triggerLoadAround(it)
    }
    @Suppress("UNCHECKED_CAST")
    return modelCache as List<EpoxyModel<*>>
  }

  fun clearModels() {
    (0 until modelCache.size).forEach {
      modelCache[it] = null
    }
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