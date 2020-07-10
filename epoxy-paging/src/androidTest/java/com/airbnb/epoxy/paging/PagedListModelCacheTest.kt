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

import android.arch.core.executor.testing.CountingTaskExecutorRule
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.view.View
import androidx.paging.PagedList
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class PagedListModelCacheTest {
    @Rule
    @JvmField
    val archExecutor = CountingTaskExecutorRule()
    /**
     * Simple mode builder for [Item]
     */
    private var modelBuildCounter = 0
    private val modelBuilder: (Int, Item?) -> EpoxyModel<*> = { pos, item ->
        modelBuildCounter++
        if (item == null) {
            FakePlaceholderModel(pos)
        } else {
            FakeModel(item)
        }
    }

    /**
     * Number of times a rebuild is requested
     */
    private var rebuildCounter = 0
    private val rebuildCallback: () -> Unit = {
        rebuildCounter++
    }

    private val pagedListModelCache = PagedListModelCache(
        modelBuilder = modelBuilder,
        rebuildCallback = rebuildCallback,
        itemDiffCallback = Item.DIFF_CALLBACK,
        diffExecutor = Executor {
            it.run()
        },
        modelBuildingHandler = EpoxyController.defaultModelBuildingHandler
    )

    @Test
    fun empty() {
        assertThat(pagedListModelCache.getModels(), `is`(emptyList()))
    }

    @Test
    fun simple() {
        val items = createItems(PAGE_SIZE)
        val (pagedList, _) = createPagedList(items)
        pagedListModelCache.submitList(pagedList)
        assertModelItems(items)
        assertAndResetRebuildModels()
    }

    @Test
    fun partialLoad() {
        val items = createItems(INITIAL_LOAD_SIZE + 2)
        val (pagedList, dataSource) = createPagedList(items)
        dataSource.stop()
        pagedListModelCache.submitList(pagedList)
        assertModelItems(items.subList(0, INITIAL_LOAD_SIZE) + listOf(20, 21))
        assertAndResetRebuildModels()
        pagedListModelCache.loadAround(INITIAL_LOAD_SIZE)
        assertModelItems(items.subList(0, INITIAL_LOAD_SIZE) + listOf(20, 21))
        assertThat(rebuildCounter, `is`(0))
        dataSource.start()
        assertModelItems(items)
        assertAndResetRebuildModels()
    }

    @Test
    fun partialLoad_jumpToPosition() {
        val items = createItems(PAGE_SIZE * 10)
        val (pagedList, _) = createPagedList(items)
        pagedListModelCache.submitList(pagedList)
        drain()
        assertAndResetRebuildModels()
        pagedListModelCache.loadAround(PAGE_SIZE * 8)
        drain()
        val models = collectModelItems()
        assertAndResetRebuildModels()
        // We cannot be sure what will be loaded but we can be sure that
        // a ) around PAGE_SIZE * 8 will be loaded
        // b ) there will be null items in between
        assertThat(models[PAGE_SIZE * 8], `is`(items[PAGE_SIZE * 8] as Any))
        assertThat(models[PAGE_SIZE * 5], `is`((PAGE_SIZE * 5) as Any))
    }

    @Test
    fun deletion() {
        testListUpdate { items, models ->
            Modification(
                newList = items.copyToMutable().also {
                    it.removeAt(3)
                },
                expectedModels = models.toMutableList().also {
                    it.removeAt(3)
                }
            )
        }
    }

    @Test
    fun deletion_range() {
        testListUpdate { items, models ->
            Modification(
                newList = items.copyToMutable().also {
                    it.removeAll(items.subList(3, 5))
                },
                expectedModels = models.toMutableList().also {
                    it.removeAll(models.subList(3, 5))
                }
            )
        }
    }

    @Test
    fun append() {
        val newItem = Item(id = 100, value = "newItem")
        testListUpdate { items, models ->
            Modification(
                newList = items.copyToMutable().also {
                    it.add(newItem)
                },
                expectedModels = models.toMutableList().also {
                    it.add(newItem)
                }
            )
        }
    }

    @Test
    fun append_many() {
        val newItems = (100 until 105).map {
            Item(id = it, value = "newItem $it")
        }
        testListUpdate { items, models ->
            Modification(
                newList = items.copyToMutable().also {
                    it.addAll(newItems)
                },
                expectedModels = models.toMutableList().also {
                    it.addAll(newItems)
                }
            )
        }
    }

    @Test
    fun insert() {
        testListUpdate { items, models ->
            val newItem = Item(id = 100, value = "item x")
            Modification(
                newList = items.copyToMutable().also {
                    it.add(5, newItem)
                },
                expectedModels = models.toMutableList().also {
                    it.add(5, newItem)
                }
            )
        }
    }

    @Test
    fun insert_many() {
        testListUpdate { items, models ->
            val newItems = (100 until 105).map {
                Item(id = it, value = "newItem $it")
            }
            Modification(
                newList = items.copyToMutable().also {
                    it.addAll(5, newItems)
                },
                expectedModels = models.toMutableList().also {
                    it.addAll(5, newItems)
                }
            )
        }
    }

    @Test
    fun move() {
        testListUpdate { items, models ->
            Modification(
                newList = items.toMutableList().also {
                    it.add(3, it.removeAt(5))
                },
                expectedModels = models.toMutableList().also {
                    it.add(3, it.removeAt(5))
                }
            )
        }
    }

    @Test
    fun move_multiple() {
        testListUpdate { items, models ->
            Modification(
                newList = items.toMutableList().also {
                    it.add(3, it.removeAt(5))
                    it.add(1, it.removeAt(8))
                },
                expectedModels = models.toMutableList().also {
                    it.add(3, it.removeAt(5))
                    it.add(1, it.removeAt(8))
                }
            )
        }
    }

    @Test
    fun clear() {
        val items = createItems(PAGE_SIZE)
        val (pagedList, _) = createPagedList(items)
        pagedListModelCache.submitList(pagedList)
        drain()
        pagedListModelCache.getModels()
        assertAndResetModelBuild()
        pagedListModelCache.clearModels()
        pagedListModelCache.getModels()
        assertAndResetModelBuild()
    }

    private fun assertAndResetModelBuild() {
        assertThat(modelBuildCounter > 0, CoreMatchers.`is`(true))
        modelBuildCounter = 0
    }

    private fun assertAndResetRebuildModels() {
        assertThat(rebuildCounter > 0, CoreMatchers.`is`(true))
        rebuildCounter = 0
    }

    /**
     * Helper method to verify multiple list update scenarios
     */
    private fun testListUpdate(update: (items: List<Item>, models: List<Any?>) -> Modification) {
        val items = createItems(PAGE_SIZE)
        val (pagedList, _) = createPagedList(items)
        pagedListModelCache.submitList(pagedList)
        val (updatedList, expectedModels) = update(items, collectModelItems())
        pagedListModelCache.submitList(createPagedList(updatedList).first)

        val updatedModels = collectModelItems()
        assertThat(updatedModels.size, `is`(expectedModels.size))
        updatedModels.forEachIndexed { index, item ->
            when (item) {
                is Item -> {
                    assertThat(item, CoreMatchers.sameInstance(expectedModels[index]))
                }
                else -> {
                    assertThat(item, `is`(expectedModels[index]))
                }
            }
        }
    }

    private fun assertModelItems(expected: List<Any?>) {
        assertThat(collectModelItems(), `is`(expected))
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun collectModelItems(): List<Any?> {
        drain()
        return pagedListModelCache.getModels().map {
            when (it) {
                is FakeModel -> it.item
                is FakePlaceholderModel -> it.pos
                else -> null
            }
        }
    }

    private fun drain() {
        archExecutor.drainTasks(4, TimeUnit.SECONDS)
        InstrumentationRegistry.getInstrumentation().runOnMainSync { }
        archExecutor.drainTasks(4, TimeUnit.SECONDS)
        InstrumentationRegistry.getInstrumentation().runOnMainSync { }
    }

    private fun createItems(cnt: Int): List<Item> {
        return (0 until cnt).map {
            Item(id = it, value = "Item $it")
        }
    }

    private fun createPagedList(items: List<Item>): Pair<PagedList<Item>, ListDataSource<Item>> {
        val dataSource = ListDataSource(items)
        val pagedList = PagedList.Builder<Int, Item>(
            dataSource,
            PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(PAGE_SIZE * 2)
                .setPageSize(PAGE_SIZE)
                .build()
        ).setFetchExecutor { it.run() }
            .setNotifyExecutor { it.run() }
            .build()
        return pagedList to dataSource
    }

    class FakePlaceholderModel(val pos: Int) : EpoxyModel<View>(-pos.toLong()) {
        override fun getDefaultLayout() = throw NotImplementedError("not needed for this test")
    }

    class FakeModel(val item: Item) : EpoxyModel<View>(item.id.toLong()) {
        override fun getDefaultLayout() = throw NotImplementedError("not needed for this test")
    }

    data class Modification(
        val newList: List<Item>,
        val expectedModels: List<Any?>
    )

    private fun List<Item>.copyToMutable(): MutableList<Item> {
        return mapTo(arrayListOf()) {
            it.copy()
        }
    }

    companion object {
        private const val PAGE_SIZE = 10
        private const val INITIAL_LOAD_SIZE = PAGE_SIZE * 2
    }
}
