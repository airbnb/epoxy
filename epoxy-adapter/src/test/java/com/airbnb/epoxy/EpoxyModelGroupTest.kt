package com.airbnb.epoxy

import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Space
import androidx.recyclerview.widget.RecyclerView
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Config(sdk = [21], manifest = TestRunner.MANIFEST_PATH)
@RunWith(ParameterizedRobolectricTestRunner::class)
class EpoxyModelGroupTest(val useViewStubs: Boolean) {

    private lateinit var recyclerView: RecyclerView
    private var topLevelHolder: EpoxyViewHolder? = null

    private val modelGroupHolder get() = topLevelHolder!!.objectToBind() as ModelGroupHolder

    @Before
    fun init() {
        recyclerView = RecyclerView(RuntimeEnvironment.application)
        topLevelHolder?.unbind()
        topLevelHolder = null
    }

    @After
    fun unbind() {
        topLevelHolder!!.unbind()
        assertEquals(0, modelGroupHolder.viewHolders.size)
    }

    private fun bind(modelGroup: EpoxyModelGroup) {
        if (topLevelHolder == null) {
            topLevelHolder = EpoxyViewHolder(modelGroup.buildView(recyclerView), false)
        }
        topLevelHolder!!.bind(modelGroup, null, emptyList(), 0)
    }

    private fun assertModelsBound(modelGroup: EpoxyModelGroup) {
        assertEquals(modelGroupHolder.viewHolders.size, modelGroup.models.size)
        modelGroupHolder.viewHolders.forEachIndexed { index, viewHolder ->
            assertEquals("Model at index $index", viewHolder.model, modelGroup.models[index])
        }
    }

    @Test
    fun bindLinearLayout() {
        createFrameLayoutGroup(3).let {
            bind(it)
            assertModelsBound(it)
        }
    }

    @Test
    fun bind_Unbind_Rebind_LinearLayoutWithLessModels() {
        bind(createFrameLayoutGroup(3))
        unbind()
        createSpaceGroup(2).let {
            bind(it)
            assertModelsBound(it)
        }
    }

    @Test
    fun bind_Unbind_Rebind_LinearLayoutWithMoreModels() {
        bind(createFrameLayoutGroup(3))
        unbind()
        createSpaceGroup(4).let {
            bind(it)
            assertModelsBound(it)
        }
    }

    @Test
    fun rebind_LinearLayoutWithSameViewTypes() {
        bind(createFrameLayoutGroup(3))
        createFrameLayoutGroup(4).let {
            bind(it)
            assertModelsBound(it)
        }
    }

    @Test
    fun rebind_LinearLayoutWithMoreModels() {
        bind(createFrameLayoutGroup(3))
        createSpaceGroup(4).let {
            bind(it)
            assertModelsBound(it)
        }
    }

    @Test
    fun rebind_LinearLayoutWithLessModels() {
        bind(createFrameLayoutGroup(3))
        createSpaceGroup(2).let {
            bind(it)
            assertModelsBound(it)
        }
    }

    @Test
    fun viewholdersAreRecycled() {
        bind(createFrameLayoutGroup(3))
        val firstHolders = modelGroupHolder.viewHolders.toSet()

        unbind()

        bind(createFrameLayoutGroup(3))
        val secondHolders = modelGroupHolder.viewHolders.toSet()

        assertEquals(firstHolders, secondHolders)
    }

    private fun createFrameLayoutGroup(modelCount: Int): EpoxyModelGroup {
        val models = (0 until modelCount).map { NestedModelFrameLayout().id(it) }
        return if (useViewStubs) ViewStubsGroupModel(models) else LinerLayoutGroupModel(models)
    }

    private fun createSpaceGroup(modelCount: Int): EpoxyModelGroup {
        val models = (0 until modelCount).map { NestedModelSpace().id(it) }
        return if (useViewStubs) ViewStubsGroupModel(models) else LinerLayoutGroupModel(models)
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "Use viewstubs: {0}")
        fun useViewStubsParameters() = listOf(
            arrayOf(true),
            arrayOf(false)
        )
    }
}

private class LinerLayoutGroupModel(models: List<EpoxyModel<*>>) : EpoxyModelGroup(0, models) {
    public override fun buildView(parent: ViewGroup): View {
        return LinearLayout(parent.context)
    }
}

private class ViewStubsGroupModel(models: List<EpoxyModel<*>>) : EpoxyModelGroup(0, models) {
    public override fun buildView(parent: ViewGroup): View {
        fun LinearLayout.addStubLayer(): LinearLayout {
            addView(ViewStub(parent.context))

            LinearLayout(parent.context).let {
                addView(it)
                return it
            }
        }

        return (0 until models.size).fold(LinearLayout(parent.context)) { linearLayout, _ -> linearLayout.addStubLayer() }
    }
}

private class NestedModelFrameLayout : EpoxyModelWithView<FrameLayout>() {
    override fun buildView(parent: ViewGroup): FrameLayout {
        return FrameLayout(parent.context)
    }
}

private class NestedModelSpace : EpoxyModelWithView<Space>() {
    override fun buildView(parent: ViewGroup): Space {
        return Space(parent.context)
    }
}