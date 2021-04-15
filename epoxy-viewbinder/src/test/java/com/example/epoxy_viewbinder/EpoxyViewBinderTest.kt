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
    fun unbindWithoutBindWillNoOp() {
        epoxyViewBinder.unbind(view)

        // Won't crash because we are no-op if no model is bound
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
    fun unbindWithViewBound() {
        epoxyViewBinder.replaceView(view, viewModel)
        epoxyViewBinder.unbind(currentView)

        // View is unbound from EpoxyModel
        assertNull(viewModel.boundView)
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
    fun unbindWithViewHolderBound() {
        epoxyViewBinder.replaceView(view, viewHolderModel)
        epoxyViewBinder.unbind(currentView)

        // Holder is unbound from Model
        assertNull(viewHolderModel.boundHolder)
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

    override fun getDefaultLayout() = throw UnsupportedOperationException("No default layout")

    override fun buildView(parent: ViewGroup) = createView()

    override fun bind(view: View) { boundView = view }
    override fun unbind(view: View) {
        if (boundView == view) {
            boundView = null
        }
    }
}

private class EpoxyModelWithHolderMock : EpoxyModelWithHolder<EpoxyHolderMock>() {
    var boundHolder: EpoxyHolderMock? = null

    override fun getDefaultLayout() = throw UnsupportedOperationException("No default layout")

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
