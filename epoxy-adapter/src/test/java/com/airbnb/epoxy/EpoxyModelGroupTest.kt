package com.airbnb.epoxy

import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Space
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@Config(sdk = [21])
@RunWith(ParameterizedRobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.LEGACY)
class EpoxyModelGroupTest(val useViewStubs: Boolean) {

    private lateinit var recyclerView: RecyclerView
    private var topLevelHolder: EpoxyViewHolder? = null

    private val modelGroupHolder get() = topLevelHolder!!.objectToBind() as ModelGroupHolder

    @Before
    fun init() {
        recyclerView = RecyclerView(ApplicationProvider.getApplicationContext())
        topLevelHolder?.unbind()
        topLevelHolder = null
    }

    @After
    fun unbind() {
        topLevelHolder!!.unbind()
        assertEquals(0, modelGroupHolder.viewHolders.size)
    }

    private fun bind(modelGroup: EpoxyModelGroup, previousGroup: EpoxyModelGroup? = null) {
        if (topLevelHolder == null) {
            topLevelHolder =
                EpoxyViewHolder(recyclerView, modelGroup.buildView(recyclerView), false)
        }
        topLevelHolder!!.bind(modelGroup, previousGroup, emptyList(), 0)
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
        val firstGroup = createFrameLayoutGroup(3)
        bind(firstGroup)
        unbind()

        createSpaceGroup(2).let {
            bind(it, firstGroup)
            assertModelsBound(it)
        }
    }

    @Test
    fun bind_Unbind_Rebind_LinearLayoutWithMoreModels() {
        val firstGroup = createFrameLayoutGroup(3)
        bind(firstGroup)
        unbind()
        createSpaceGroup(4).let {
            bind(it, firstGroup)
            assertModelsBound(it)
        }
    }

    @Test
    fun rebind_LinearLayoutWithSameViewTypes() {
        val firstGroup = createFrameLayoutGroup(3)
        bind(firstGroup)
        createFrameLayoutGroup(4).let {
            bind(it, firstGroup)
            assertModelsBound(it)
        }
    }

    @Test
    fun rebind_LinearLayoutWithMoreModels() {
        val firstGroup = createFrameLayoutGroup(3)
        bind(firstGroup)
        createSpaceGroup(4).let {
            bind(it, firstGroup)
            assertModelsBound(it)
        }
    }

    @Test
    fun rebind_LinearLayoutWithLessModels() {
        val firstGroup = createFrameLayoutGroup(3)
        bind(firstGroup)
        createSpaceGroup(2).let {
            bind(it, firstGroup)
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

    @Test
    fun viewStubsOutOfOrder() {
        val models = (0 until 4).map { NestedModelFrameLayout().id(it) }

        val modelGroup = object : EpoxyModelGroup(0, models) {
            public override fun buildView(parent: ViewGroup): View {
                return LinearLayout(parent.context).apply {

                    addView(
                        ViewStub(parent.context).apply {
                            inflatedId = 0
                        }
                    )

                    addView(
                        LinearLayout(parent.context).apply {
                            addView(
                                ViewStub(parent.context).apply {
                                    inflatedId = 1
                                }
                            )

                            addView(Space(parent.context))

                            addView(
                                ViewStub(parent.context).apply {
                                    inflatedId = 2
                                }
                            )
                        }
                    )

                    addView(
                        ViewStub(parent.context).apply {
                            inflatedId = 3
                        }
                    )
                }
            }
        }

        bind(modelGroup)

        modelGroupHolder.viewHolders.forEachIndexed { index, viewholder ->

            val view = viewholder.itemView
            assertEquals(index, view.id)

            val indexInsideParentView = (view.parent as ViewGroup).indexOfChild(view)
            when (view.id) {
                0 -> assertEquals(0, indexInsideParentView)
                1 -> assertEquals(0, indexInsideParentView)
                2 -> assertEquals(2, indexInsideParentView)
                3 -> assertEquals(2, indexInsideParentView)
            }
        }
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
