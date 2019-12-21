package com.airbnb.epoxy

import com.airbnb.epoxy.ModelList.ModelListObserver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ModelListTest {
    private val observer = Mockito.mock(ModelListObserver::class.java)
    private val modelList = ModelList()

    @Before
    fun before() {
        modelList.add(TestModel())
        modelList.add(TestModel())
        modelList.add(TestModel())

        modelList.setObserver(observer)
    }

    @Test
    fun testSet() {
        modelList[0] = TestModel()

        Mockito.verify(observer).onItemRangeRemoved(0, 1)
        Mockito.verify(observer).onItemRangeInserted(0, 1)
    }

    @Test
    fun testSetSameIdDoesntNotify() {
        val newModelWithSameId = TestModel()
        newModelWithSameId.id(modelList[0].id())

        modelList[0] = newModelWithSameId
        verifyNoMoreInteractions(observer)
        assertEquals(newModelWithSameId, modelList[0])
    }

    @Test
    fun testAdd() {
        modelList.add(TestModel())
        modelList.add(TestModel())

        verify(observer).onItemRangeInserted(3, 1)
        verify(observer).onItemRangeInserted(4, 1)
    }

    @Test
    fun testAddAtIndex() {
        modelList.add(0, TestModel())
        modelList.add(2, TestModel())

        verify(observer).onItemRangeInserted(0, 1)
        verify(observer).onItemRangeInserted(2, 1)
    }

    @Test
    fun testAddAll() {
        val newModels = mutableListOf<EpoxyModel<*>>()
        newModels.add(TestModel())
        newModels.add(TestModel())

        modelList.addAll(newModels)
        verify(observer).onItemRangeInserted(3, 2)
    }

    @Test
    fun testAddAllAtIndex() {
        val newModels = mutableListOf<EpoxyModel<*>>()
        newModels.add(TestModel())
        newModels.add(TestModel())

        modelList.addAll(0, newModels)
        verify(observer).onItemRangeInserted(0, 2)
    }

    @Test
    fun testRemoveIndex() {
        val removedModel = modelList.removeAt(0)
        assertFalse(modelList.contains(removedModel))

        assertEquals(2, modelList.size.toLong())
        verify(observer).onItemRangeRemoved(0, 1)
    }

    @Test
    fun testRemoveObject() {
        val model = modelList[0]
        val model1Removed = modelList.remove(model)

        assertEquals(2, modelList.size.toLong())
        assertTrue(model1Removed)
        assertFalse(modelList.contains(model))

        verify(observer).onItemRangeRemoved(0, 1)
    }

    @Test
    fun testRemoveObjectNotAdded() {
        val removed = modelList.remove(TestModel())
        assertFalse(removed)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun testClear() {
        modelList.clear()
        verify(observer).onItemRangeRemoved(0, 3)
    }

    @Test
    fun testClearWhenAlreadyEmpty() {
        modelList.clear()
        modelList.clear()
        verify(observer).onItemRangeRemoved(0, 3)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun testSublistClear() {
        modelList.subList(0, 2).clear()
        verify(observer).onItemRangeRemoved(0, 2)
    }

    @Test
    fun testNoClearWhenEmpty() {
        modelList.clear()
        modelList.clear()
        verify(observer).onItemRangeRemoved(0, 3)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun testRemoveRange() {
        modelList.removeRange(0, 2)
        assertEquals(1, modelList.size.toLong())
        verify(observer).onItemRangeRemoved(0, 2)
    }

    @Test
    fun testRemoveEmptyRange() {
        modelList.removeRange(1, 1)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun testIteratorRemove() {
        val iterator = modelList.iterator()
        iterator.next()
        iterator.remove()

        verify(observer).onItemRangeRemoved(0, 1)
    }

    @Test
    fun testRemoveAll() {
        val modelsToRemove = mutableListOf<EpoxyModel<*>>()
        modelsToRemove.add(modelList[0])
        modelsToRemove.add(modelList[1])

        modelList.removeAll(modelsToRemove)
        verify(observer, times(2)).onItemRangeRemoved(0, 1)
    }

    @Test
    fun testRetainAll() {
        val modelsToRetain = mutableListOf<EpoxyModel<*>>()
        modelsToRetain.add(modelList[0])

        modelList.retainAll(modelsToRetain)
        verify(observer, times(2)).onItemRangeRemoved(1, 1)
    }
}
