package com.example.epoxy_viewbinder

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.EpoxyViewBinder
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EpoxyViewBinderTest {

    private val epoxyViewBinder = EpoxyViewBinder()

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()
    private val view = View(context)
    private val viewGroup = FrameLayout(context)
    private val currentView: View
        get() = viewGroup.getChildAt(0) // We always have children in viewGroup

    private val viewModel = EpoxyModelMock()
    private val viewHolderModel = EpoxyModelWithHolderMock()

    @Before
    fun setUp() {
        viewGroup.addView(view)
    }

    @After
    fun tearDown() {
        viewGroup.removeAllViews()
    }

    @Test
    fun bindWithView() {
        epoxyViewBinder.replaceView(view, viewModel)

        // Old view is removed from parent
        assertNull(view.parent)
        // A new view is added
        assertEquals(viewGroup, currentView.parent)
        // New view is bound to EpoxyModel
        assertEquals(currentView, viewModel.boundView)
    }

    @Test
    fun bindWithView_usingModelProvider() {
        epoxyViewBinder.replaceView(view) {
            add(viewModel)
        }

        // Old view is removed from parent
        assertNull(view.parent)
        // A new view is added
        assertEquals(viewGroup, currentView.parent)
        // New view is bound to EpoxyModel
        assertEquals(currentView, viewModel.boundView)
    }

    @Test
    fun bindWithViewHolder() {
        epoxyViewBinder.replaceView(view, viewHolderModel)

        // Old view is removed from parent
        assertNull(view.parent)
        // A new view is added
        assertEquals(viewGroup, currentView.parent)
        // New view is bound to ViewHolder
        assertEquals(currentView, viewHolderModel.boundHolder?.boundView)
    }

    @Test
    fun bindWithViewHolder_usingModelProvider() {
        epoxyViewBinder.replaceView(view) {
            add(viewHolderModel)
        }

        // Old view is removed from parent
        assertNull(view.parent)
        // A new view is added
        assertEquals(viewGroup, currentView.parent)
        // New view is bound to ViewHolder
        assertEquals(currentView, viewHolderModel.boundHolder?.boundView)
    }

    @Test
    fun bindWithViewGroup() {
        val newView = epoxyViewBinder.replaceOrCreateView(viewGroup, view, viewModel)

        // Old view is retained as it doesn't have a model bound to it
        assertNotNull(view.parent)
        assertEquals(viewGroup, currentView.parent)
        // No new view is added
        assertEquals(1, viewGroup.childCount)
        assertNull(newView.parent)
        // New view is bound to EpoxyModel
        assertEquals(newView, viewModel.boundView)
    }

    @Test
    fun bindWithViewGroup_modelUpdate() {
        epoxyViewBinder.replaceView(view, viewModel)
        val newViewModel = EpoxyModelMock()
        val newView = epoxyViewBinder.replaceOrCreateView(viewGroup, currentView, newViewModel)

        // No new view added and current view is still in the layout
        assertEquals(1, viewGroup.childCount)
        assertEquals(viewGroup, currentView.parent)
        assertEquals(currentView, newView)
        assertEquals(currentView, newViewModel.boundView)
    }

    @Test
    fun unbindWithoutBindWillNoOp() {
        epoxyViewBinder.unbind(view)

        // Won't crash because we are no-op if no model is bound
    }

    @Test
    fun unbindWithViewBound() {
        epoxyViewBinder.replaceView(view, viewModel)
        epoxyViewBinder.unbind(currentView)

        // View is unbound from EpoxyModel
        assertNull(viewModel.boundView)
    }

    @Test
    fun unbindWithViewHolderBound() {
        epoxyViewBinder.replaceView(view, viewHolderModel)
        epoxyViewBinder.unbind(currentView)

        // Holder is unbound from Model
        assertNull(viewHolderModel.boundHolder)
    }

    @Test
    fun insertInto() {
        epoxyViewBinder.insertInto(viewGroup) {
            add(viewModel)
        }

        // Old view is removed from parent
        assertNull(view.parent)
        // A new view is added
        assertEquals(viewGroup, currentView.parent)
        // New view is bound to EpoxyModel
        assertEquals(currentView, viewModel.boundView)
    }

    @Test
    fun insertInto_noModelAddedClearsContainer() {
        epoxyViewBinder.insertInto(viewGroup) { }

        // Old view is removed from parent
        assertNull(view.parent)
        assertEquals(0, viewGroup.childCount)
    }

    @Test(expected = IllegalArgumentException::class)
    fun insertInto_tooManyChildren() {
        viewGroup.addView(View(context))

        epoxyViewBinder.insertInto(viewGroup) {
            add(viewModel)
        }
    }
}

private class EpoxyHolderMock : EpoxyHolder() {
    var boundView: View? = null
    override fun bindView(itemView: View) {
        boundView = itemView
    }
}

private class EpoxyModelMock : EpoxyModel<View>() {
    var boundView: View? = null

    override fun getDefaultLayout() = 1

    override fun buildView(parent: ViewGroup) = createView()

    override fun bind(view: View) {
        boundView = view
    }

    override fun unbind(view: View) {
        if (boundView == view) {
            boundView = null
        }
    }
}

private class EpoxyModelWithHolderMock : EpoxyModelWithHolder<EpoxyHolderMock>() {
    var boundHolder: EpoxyHolderMock? = null

    override fun getDefaultLayout() = 2

    override fun buildView(parent: ViewGroup) = createView()

    override fun createNewHolder(parent: ViewParent) = EpoxyHolderMock()

    override fun bind(holder: EpoxyHolderMock) {
        boundHolder = holder
    }

    override fun unbind(holder: EpoxyHolderMock) {
        if (boundHolder == holder) {
            boundHolder = null
        }
    }
}

private fun createView() = View(ApplicationProvider.getApplicationContext())
