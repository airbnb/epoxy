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

import android.arch.paging.PagedList
import android.os.Handler
import android.support.v7.util.DiffUtil
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyViewHolder

/**
 * Creates an [EpoxyController] that can work with a [PagedList].
 * Internally, it caches the model for each item in the [PagedList]. You should override [buildItemModel] method to
 * build the model for the given item. Since paged list might include `null` items if placeholders are enabled, this
 * method needs to handle null values in the list.
 *
 * By default, the model for each item is added  to the model list. To change this behavior (to filter items or
 * inject extra items), you can override [addModels] function and manually add built models.
 */
abstract class CachingPagingEpoxyController<T>(
    modelBuildingHandler: Handler = EpoxyController.defaultModelBuildingHandler,
    diffingHandler: Handler = EpoxyController.defaultDiffingHandler,
    itemDiffCallback: DiffUtil.ItemCallback<T> = DEFAULT_ITEM_DIFF_CALLBACK as DiffUtil.ItemCallback<T>
) : EpoxyController(modelBuildingHandler, diffingHandler) {
  private val modelCache = PagedListModelCache(
      modelBuilder = { pos, item ->
        buildItemModel(pos, item)
      },
      rebuildCallback = {
        requestDelayedModelBuild(0)
      },
      itemDiffCallback = itemDiffCallback,
      modelBuildingHandler = modelBuildingHandler
  )

  final override fun buildModels() {
    addModels(modelCache.getModels())
  }

  /**
   * This function adds all built models to the adapter. You can override this method to add extra items into the model
   * list or remove some.
   */
  open fun addModels(models: List<EpoxyModel<*>>) {
    super.add(models)
  }

  /**
   * Builds the model for a given item. This must return a single model for each item. If you want to inject headers
   * etc, you can override [addModels] function.
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
   * Submit a new paged list.
   */
  fun submitList(newList: PagedList<T>?) {
    modelCache.submitList(newList)
  }

  companion object {
    /**
     * [CachingPagingEpoxyController] calculates a diff on top of the PagedList to check which models are invalidated.
     * This is the default [DiffUtil.ItemCallback] which uses object equality.
     */
    val DEFAULT_ITEM_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Any>() {
      override fun areItemsTheSame(oldItem: Any?, newItem: Any?) = oldItem == newItem

      override fun areContentsTheSame(oldItem: Any?, newItem: Any?) = oldItem == newItem
    }
  }
}