package com.airbnb.epoxy

import android.view.View
import android.view.ViewParent
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.inOrder
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EpoxyViewHolderTest {
    private lateinit var epoxyViewHolder: EpoxyViewHolder

    @Mock
    lateinit var viewParent: ViewParent

    @Mock
    lateinit var view: View

    @Mock
    lateinit var model: TestModel

    @Mock
    lateinit var previousModel: TestModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        epoxyViewHolder = EpoxyViewHolder(viewParent, view, false)
    }

    @Test
    fun testBindCallsPreBindWithPrevious() {
        epoxyViewHolder.bind(model, previousModel, emptyList(), 0)
        val inOrder = inOrder(model)
        inOrder.verify(model).preBind(view, previousModel)
        inOrder.verify(model).bind(view, previousModel)
    }

    @Test
    fun testBindCallsPreBind() {
        epoxyViewHolder.bind(model, null, emptyList(), 0)
        val inOrder = inOrder(model)
        inOrder.verify(model).preBind(view, null)
        inOrder.verify(model).bind(view)
    }
}
