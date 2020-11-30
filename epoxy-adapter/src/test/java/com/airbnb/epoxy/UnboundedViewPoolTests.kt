package com.airbnb.epoxy

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UnboundedViewPoolTests {

    private val testAdapter = RvAdapter()

    @Test
    fun `correctly stores different viewHolders`() {
        UnboundedViewPool().run {
            addAndAssert(123, 10)
            addAndAssert(321, 5)
        }
    }

    private fun UnboundedViewPool.addAndAssert(expectedId: Int, expectedCount: Int) {
        repeat(expectedCount) {
            putRecycledView(createViewHolder(expectedId))
        }
        assertEquals(expectedCount, getRecycledViewCount(expectedId))
    }

    @Test
    fun `correctly removes from pool`() {
        UnboundedViewPool().run {
            val expectedId = 548
            putRecycledView(createViewHolder(expectedId))
            assertNotNull(getRecycledView(expectedId))
            assertNull(getRecycledView(expectedId))
        }
    }

    @Test
    fun `correctly clears pool`() {
        UnboundedViewPool().run {
            val expectedId = 10
            addAndAssert(expectedId, 10)
            clear()
            assertNull(getRecycledView(expectedId))
            assertEquals(0, getRecycledViewCount(expectedId))
        }
    }

    private fun createViewHolder(id: Int) =
        testAdapter.createViewHolder(FrameLayout(ApplicationProvider.getApplicationContext()), id)

    /**
     * Creating adapter is needed because it is setting viewType to viewHolder internally
     * during creation of new viewHolder
     */
    private class RvAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            EpoxyViewHolder(parent, View(parent.context), false)

        override fun getItemCount() = 0

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}
    }
}
