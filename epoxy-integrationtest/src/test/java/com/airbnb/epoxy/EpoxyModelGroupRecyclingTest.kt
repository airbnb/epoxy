package com.airbnb.epoxy

import android.content.Context
import android.os.Looper
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.runner.AndroidJUnit4
import com.airbnb.epoxy.integrationtest.R
import com.airbnb.epoxy.integrationtest.TestActivity
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config

/**
 * Tests view pool of [EpoxyModelGroup] within [EpoxyRecyclerView].
 */
@Config(sdk = [21], qualifiers = "h831dp-mdpi")
@RunWith(AndroidJUnit4::class)
class EpoxyModelGroupRecyclingTest {

    @get:Rule
    var activityRule = activityScenarioRule<TestActivity>()

    private lateinit var recyclerView1: EpoxyRecyclerView

    private lateinit var recyclerView2: EpoxyRecyclerView

    /**
     * Verify that the groups inherit of the view pool configuration from the parent recycler view.
     * Pool is shared within the context.
     */
    @Test
    fun testModelGroupPool_shared() {

        setupRecyclerView(shareViewPoolAcrossContext = true)
        Assert.assertSame(recyclerView1.recycledViewPool, recyclerView2.recycledViewPool)

        var modelsBound = 0
        val assertOnModelBound: (EpoxyModelGroup, ModelGroupHolder, Int) -> Unit = { _, view, _ ->
            modelsBound++
            Assert.assertSame(recyclerView1.recycledViewPool, view.viewPool)
        }
        withModels(
            buildModels1 = {
                group {
                    id("1")
                    layout(R.layout.vertical_linear_group)
                    onBind(assertOnModelBound)

                    group {
                        id("1.1")
                        layout(R.layout.vertical_linear_group)
                        onBind(assertOnModelBound)
                    }

                    group {
                        id("1.1.1")
                        layout(R.layout.vertical_linear_group)
                        onBind(assertOnModelBound)
                    }
                }
            },
            buildModels2 = {
                group {
                    layout(R.layout.vertical_linear_group)
                    id("2")
                    onBind(assertOnModelBound)
                }
            }
        )
        Assert.assertEquals(4, modelsBound)
    }

    /**
     * Verify that the groups inherit of the view pool configuration from the parent recycler view.
     * Pool is not shared within the context.
     */
    @Test
    fun testModelGroupPool_notShared() {

        setupRecyclerView(shareViewPoolAcrossContext = false)
        Assert.assertNotEquals(recyclerView1.recycledViewPool, recyclerView2.recycledViewPool)

        var modelsBound = 0
        val assertOnModelBound1: (EpoxyModelGroup, ModelGroupHolder, Int) -> Unit = { _, view, _ ->
            modelsBound++
            Assert.assertSame(recyclerView1.recycledViewPool, view.viewPool)
        }
        val assertOnModelBound2: (EpoxyModelGroup, ModelGroupHolder, Int) -> Unit = { _, view, _ ->
            modelsBound++
            Assert.assertSame(recyclerView2.recycledViewPool, view.viewPool)
        }
        withModels(
            buildModels1 = {
                group {
                    id("1")
                    layout(R.layout.vertical_linear_group)
                    onBind(assertOnModelBound1)

                    group {
                        id("1.1")
                        layout(R.layout.vertical_linear_group)
                        onBind(assertOnModelBound1)

                        group {
                            id("1.1.1")
                            layout(R.layout.vertical_linear_group)
                            onBind(assertOnModelBound1)
                        }
                    }
                }
            },
            buildModels2 = {
                group {
                    layout(R.layout.vertical_linear_group)
                    id("2")
                    onBind(assertOnModelBound2)
                }
            }
        )
        Assert.assertEquals(4, modelsBound)
    }

    /** Sets the models in the [recyclerView1] and [recyclerView2]. */
    private fun setupRecyclerView(
        shareViewPoolAcrossContext: Boolean
    ) {
        activityRule.scenario.onActivity {
            if (shareViewPoolAcrossContext) {
                recyclerView1 = EpoxyRecyclerView(it)
                recyclerView2 = EpoxyRecyclerView(it)
            } else {
                recyclerView1 = NonSharedEpoxyRecyclerView(it)
                recyclerView2 = NonSharedEpoxyRecyclerView(it)
            }
            it.setContentView(
                FrameLayout(it).apply {
                    addView(
                        recyclerView1,
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    )
                    addView(
                        recyclerView2,
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    )
                }
            )
            Shadows.shadowOf(Looper.getMainLooper()).idle()
        }
    }

    /** Sets the models in the [recyclerView1] and [recyclerView2]. */
    private fun withModels(
        buildModels1: EpoxyController.() -> Unit,
        buildModels2: EpoxyController.() -> Unit
    ) {
        activityRule.scenario.onActivity {
            recyclerView1.withModels {
                buildModels1.invoke(this)
            }
            recyclerView2.withModels {
                buildModels2.invoke(this)
            }
            Shadows.shadowOf(Looper.getMainLooper()).idle()
        }
    }

    private class NonSharedEpoxyRecyclerView(
        context: Context
    ) : EpoxyRecyclerView(context) {

        override fun shouldShareViewPoolAcrossContext() = false
    }
}
