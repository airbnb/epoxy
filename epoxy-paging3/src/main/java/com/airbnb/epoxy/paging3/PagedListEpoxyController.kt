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
package com.airbnb.epoxy.paging3

import android.annotation.SuppressLint
import android.os.Handler
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyViewHolder

/**
 * An [EpoxyController] that can work with a [PagedList].
 *
 * Internally, it caches the model for each item in the [PagedList]. You should override
 * [buildItemModel] method to build the model for the given item. Since [PagedList] might include
 * `null` items if placeholders are enabled, this method needs to handle `null` values in the list.
 *
 * By default, the model for each item is added  to the model list. To change this behavior (to
 * filter items or inject extra items), you can override [addModels] function and manually add built
 * models.
 *
 * @param T The type of the items in the [PagedList].
 */
abstract class PagedListEpoxyController<T : Any>(
    /**
     * The handler to use for building models. By default this uses the main thread, but you can use
     * [EpoxyAsyncUtil.getAsyncBackgroundHandler] to do model building in the background.
     *
     * The notify thread of your PagedList (from setNotifyExecutor in the PagedList Builder) must be
     * the same as this thread. Otherwise Epoxy will crash.
     */
    modelBuildingHandler: Handler = EpoxyController.defaultModelBuildingHandler,
    /**
     * The handler to use when calculating the diff between built model lists.
     * By default this uses the main thread, but you can use
     * [EpoxyAsyncUtil.getAsyncBackgroundHandler] to do diffing in the background.
     */
    diffingHandler: Handler = EpoxyController.defaultDiffingHandler,
    /**
     * [PagedListEpoxyController] uses an [DiffUtil.ItemCallback] to detect changes between
     * [PagedList]s. By default, it relies on simple object equality but you can provide a custom
     * one if you don't use all fields in the object in your models.
     */
    itemDiffCallback: DiffUtil.ItemCallback<T> = DEFAULT_ITEM_DIFF_CALLBACK as DiffUtil.ItemCallback<T>
) : EpoxyController(modelBuildingHandler, diffingHandler) {
    // this is where we keep the already built models
    private val modelCache = PagedListModelCache(
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
     * If the `item` is `null`, you should provide the placeholder. If your [PagedList] is
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
     * Submit a new paged list.
     *
     * A diff will be calculated between this list and the previous list so you may still get calls
     * to [buildItemModel] with items from the previous list.
     */
    fun submitList(newList: PagedList<T>?) {
        modelCache.submitList(newList)
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
         * [PagedListEpoxyController] calculates a diff on top of the PagedList to check which
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
