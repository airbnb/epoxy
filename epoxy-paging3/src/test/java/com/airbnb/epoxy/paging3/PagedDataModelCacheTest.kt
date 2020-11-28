package com.airbnb.epoxy.paging3

import android.view.View
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.LooperMode
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.LEGACY)
class PagedDataModelCacheTest {
    /**
     * test dispatcher used for controlling paging source
     * */
    private val testDispatcher = TestCoroutineDispatcher()

    /**
     * Simple mode builder for [DummyItem]
     */
    private var modelBuildCounter = 0
    private val modelBuilder: (Int, DummyItem?) -> EpoxyModel<*> = { pos, item ->
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

    private val pagedDataModelCache =
        PagedDataModelCache(
            modelBuilder = modelBuilder,
            rebuildCallback = rebuildCallback,
            itemDiffCallback = DummyItem.DIFF_CALLBACK,
            modelBuildingHandler = EpoxyController.defaultModelBuildingHandler
        )

    @Test
    fun empty() {
        MatcherAssert.assertThat(pagedDataModelCache.getModels(), CoreMatchers.`is`(emptyList()))
    }

    @Test
    fun simple() = runBlocking {
        val items = createDummyItems(PAGE_SIZE)
        val pagedData = createPagedData(items)
        pagedDataModelCache.submitData(pagedData)
        assertModelDummyItems(items)
        assertAndResetRebuildModels()
    }

    @Test
    fun partialLoad() = runBlockingTest {
        val items = createDummyItems(INITIAL_LOAD_SIZE + PAGE_SIZE)
        val pager = createPager(testDispatcher, items)
        val deferred = async {
            pager.flow.collect {
                pagedDataModelCache.submitData(it)
            }
        }
        // advance in time to create first page of data
        testDispatcher.advanceTimeBy(DEFAULT_DELAY)

        // wait for pagedDataModelCache submits data
        delay(2000)

        assertModelDummyItems(items.subList(0, INITIAL_LOAD_SIZE))
        assertAndResetRebuildModels()
        pagedDataModelCache.loadAround(INITIAL_LOAD_SIZE - 1)
        assertModelDummyItems(items.subList(0, INITIAL_LOAD_SIZE))
        MatcherAssert.assertThat(rebuildCounter, CoreMatchers.`is`(0))
        // advance in time to create second page of data
        testDispatcher.advanceTimeBy(DEFAULT_DELAY)
        delay(2000)
        assertModelDummyItems(items)
        assertAndResetRebuildModels()

        deferred.cancel()
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
        val newDummyItem = DummyItem(id = 100, value = "newDummyItem")
        testListUpdate { items, models ->
            Modification(
                newList = items.copyToMutable().also {
                    it.add(newDummyItem)
                },
                expectedModels = models.toMutableList().also {
                    it.add(newDummyItem)
                }
            )
        }
    }

    @Test
    fun append_many() {
        val newDummyItems = (100 until 105).map {
            DummyItem(id = it, value = "newDummyItem $it")
        }
        testListUpdate { items, models ->
            Modification(
                newList = items.copyToMutable().also {
                    it.addAll(newDummyItems)
                },
                expectedModels = models.toMutableList().also {
                    it.addAll(newDummyItems)
                }
            )
        }
    }

    @Test
    fun insert() {
        testListUpdate { items, models ->
            val newDummyItem =
                DummyItem(id = 100, value = "item x")
            Modification(
                newList = items.copyToMutable().also {
                    it.add(5, newDummyItem)
                },
                expectedModels = models.toMutableList().also {
                    it.add(5, newDummyItem)
                }
            )
        }
    }

    @Test
    fun insert_many() {
        testListUpdate { items, models ->
            val newDummyItems = (100 until 105).map {
                DummyItem(id = it, value = "newDummyItem $it")
            }
            Modification(
                newList = items.copyToMutable().also {
                    it.addAll(5, newDummyItems)
                },
                expectedModels = models.toMutableList().also {
                    it.addAll(5, newDummyItems)
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
    fun clear() = runBlocking {
        val items = createDummyItems(PAGE_SIZE)
        val pagedData = createPagedData(items)
        pagedDataModelCache.submitData(pagedData)
        pagedDataModelCache.getModels()
        assertAndResetModelBuild()
        pagedDataModelCache.clearModels()
        pagedDataModelCache.getModels()
        assertAndResetModelBuild()
    }

    private fun assertAndResetModelBuild() {
        MatcherAssert.assertThat(modelBuildCounter > 0, CoreMatchers.`is`(true))
        modelBuildCounter = 0
    }

    private fun assertAndResetRebuildModels() {
        MatcherAssert.assertThat(rebuildCounter > 0, CoreMatchers.`is`(true))
        rebuildCounter = 0
    }

    /**
     * Helper method to verify multiple list update scenarios
     */
    private fun testListUpdate(update: (items: List<DummyItem>, models: List<Any?>) -> Modification) =
        runBlocking {
            val items = createDummyItems(PAGE_SIZE)
            pagedDataModelCache.submitData(createPagedData(items))
            val (updatedList, expectedModels) = update(items, collectModelDummyItems())
            pagedDataModelCache.submitData(createPagedData(updatedList))

            val updatedModels = collectModelDummyItems()
            MatcherAssert.assertThat(updatedModels.size, CoreMatchers.`is`(expectedModels.size))
            updatedModels.forEachIndexed { index, item ->
                when (item) {
                    is DummyItem -> {
                        assertEquals(item, expectedModels[index])
                    }
                    else -> {
                        MatcherAssert.assertThat(item, CoreMatchers.`is`(expectedModels[index]))
                    }
                }
            }
        }

    private fun assertModelDummyItems(expected: List<Any?>) {
        MatcherAssert.assertThat(collectModelDummyItems(), CoreMatchers.`is`(expected))
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun collectModelDummyItems(): List<Any?> {
        return pagedDataModelCache.getModels().map {
            when (it) {
                is FakeModel -> it.item
                is FakePlaceholderModel -> it.pos
                else -> null
            }
        }
    }

    private fun createDummyItems(cnt: Int): List<DummyItem> {
        return (0 until cnt).map {
            DummyItem(id = it, value = "DummyItem $it")
        }
    }

    private fun createPagedData(items: List<DummyItem>): PagingData<DummyItem> {
        return PagingData.from(items)
    }

    private fun createPager(
        coroutineContext: CoroutineContext,
        items: List<DummyItem>
    ): Pager<Int, DummyItem> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = INITIAL_LOAD_SIZE,
                enablePlaceholders = true,
            ),
            initialKey = null,
            pagingSourceFactory = {
                ListPagingSource(coroutineContext, DEFAULT_DELAY, items)
            }
        )
    }

    class FakePlaceholderModel(val pos: Int) : EpoxyModel<View>(-pos.toLong()) {
        override fun getDefaultLayout() = throw NotImplementedError("not needed for this test")
    }

    class FakeModel(val item: DummyItem) : EpoxyModel<View>(item.id.toLong()) {
        override fun getDefaultLayout() = throw NotImplementedError("not needed for this test")
    }

    data class Modification(
        val newList: List<DummyItem>,
        val expectedModels: List<Any?>
    )

    private fun List<DummyItem>.copyToMutable(): MutableList<DummyItem> {
        return mapTo(arrayListOf()) {
            it.copy()
        }
    }

    companion object {
        private const val PAGE_SIZE = 10
        private const val INITIAL_LOAD_SIZE = PAGE_SIZE * 2
        private const val DEFAULT_DELAY = 10000L
    }
}
