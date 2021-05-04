package com.airbnb.epoxy

import android.content.Context
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Space
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.runner.AndroidJUnit4
import com.airbnb.epoxy.integrationtest.R
import com.airbnb.epoxy.integrationtest.TestActivity
import com.airbnb.epoxy.models.TrackerTestModel
import com.airbnb.epoxy.models.trackerTestModel
import com.airbnb.epoxy.models.trackerTestModelGroup
import com.airbnb.epoxy.utils.VisibilityAssertHelper
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import kotlin.math.roundToInt

@Config(qualifiers = "h831dp-mdpi")
@RunWith(AndroidJUnit4::class)
class EpoxyViewBinderVisibilityTrackerTest {

    @get:Rule
    var activityRule = activityScenarioRule<TestActivity>()

    private lateinit var linearLayout: LinearLayout
    private lateinit var scrollView: ScrollView
    private val scrollViewId = View.generateViewId()
    private var ids = 0

    @Before
    fun setUp() {
        activityRule.scenario.onActivity {
            linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            scrollView = ScrollView(it)
            scrollView.addView(linearLayout)
            scrollView.id = scrollViewId
            it.setContentView(scrollView)
            shadowOf(Looper.getMainLooper()).idle()
        }
    }

    @After
    fun tearDown() {
        ids = 0
    }

    @Test
    fun testModelInitiallyVisible_full() {
        activityRule.scenario.onActivity {
            val helper = nextAssertHelper()
            val itemHeight = scrollView.measuredHeight / 2
            it.withModel(itemHeight) {
                trackerTestModel("model", itemHeight, helper = helper)
            }

            helper.assert(
                visibleHeight = itemHeight,
                percentVisibleHeight = 100f,
                visible = true,
                partialImpression = true,
                fullImpression = true,
                visitedStates = intArrayOf(
                    VisibilityState.VISIBLE,
                    VisibilityState.FOCUSED_VISIBLE,
                    VisibilityState.PARTIAL_IMPRESSION_VISIBLE,
                    VisibilityState.FULL_IMPRESSION_VISIBLE
                )
            )
        }
    }

    @Test
    fun testModelInitiallyVisible_partial() {
        activityRule.scenario.onActivity {
            val helper = nextAssertHelper()
            val itemHeight = scrollView.measuredHeight / 2
            val spaceHeight = (itemHeight * 1.5).toInt()
            it.addSpace(spaceHeight)
            it.withModel(itemHeight) {
                trackerTestModel("model", itemHeight, helper = helper)
            }

            helper.assert(
                visibleHeight = scrollView.measuredHeight - spaceHeight,
                percentVisibleHeight = (scrollView.measuredHeight - spaceHeight).toFloat() / itemHeight * 100,
                visible = true,
                partialImpression = false,
                fullImpression = false,
                visitedStates = intArrayOf(
                    VisibilityState.VISIBLE
                )
            )
        }
    }

    @Test
    fun testModelInitiallyVisible_partial_scrolledToBottom() {
        activityRule.scenario.onActivity {
            val helper = nextAssertHelper()
            val itemHeight = scrollView.measuredHeight / 2
            val spaceHeight = (itemHeight * 1.5).toInt()
            it.addSpace(spaceHeight)
            it.withModel(itemHeight) {
                trackerTestModel("model", itemHeight, helper = helper)
            }

            scrollView.scrollTo(0, spaceHeight - itemHeight)
            shadowOf(Looper.getMainLooper()).idle()

            // The binder currently doesn't get updates when scrolled so visibility remains the same prior to scrolling
            helper.assert(
                visibleHeight = scrollView.measuredHeight - spaceHeight,
                percentVisibleHeight = (scrollView.measuredHeight - spaceHeight).toFloat() / itemHeight * 100,
                visible = true,
                partialImpression = false,
                fullImpression = false,
                visitedStates = intArrayOf(
                    VisibilityState.VISIBLE
                )
            )
        }
    }

    @Test
    fun testModelRemoved() {
        activityRule.scenario.onActivity {
            val helper = nextAssertHelper()
            val itemHeight = scrollView.measuredHeight / 2
            var addModel = true
            val binder = it.withModel(itemHeight) {
                if (addModel) {
                    trackerTestModel("model", itemHeight, helper = helper)
                }
            }

            addModel = false
            helper.reset()
            binder.invalidate()
            shadowOf(Looper.getMainLooper()).idle()

            helper.assert(
                visibleHeight = 0,
                percentVisibleHeight = 0f,
                visible = false,
                partialImpression = false,
                fullImpression = false,
                visitedStates = intArrayOf(
                    VisibilityState.INVISIBLE,
                    VisibilityState.UNFOCUSED_VISIBLE,
                    VisibilityState.PARTIAL_IMPRESSION_INVISIBLE
                )
            )
        }
    }

    @Test
    fun testModelAdded() {
        activityRule.scenario.onActivity {
            val helper = nextAssertHelper()
            val itemHeight = scrollView.measuredHeight / 3
            var addModel = false
            val binder = it.withModel(itemHeight) {
                if (addModel) {
                    trackerTestModel("model", itemHeight, helper = helper)
                }
            }

            addModel = true
            binder.invalidate()
            shadowOf(Looper.getMainLooper()).idle()

            helper.assert(
                visibleHeight = itemHeight,
                percentVisibleHeight = 100f,
                visible = true,
                partialImpression = true,
                fullImpression = true,
                visitedStates = intArrayOf(
                    VisibilityState.VISIBLE,
                    VisibilityState.FOCUSED_VISIBLE,
                    VisibilityState.PARTIAL_IMPRESSION_VISIBLE,
                    VisibilityState.FULL_IMPRESSION_VISIBLE
                )
            )
        }
    }

    @Test
    fun testModelReplaced_notSupported() {
        activityRule.scenario.onActivity {
            val helper = nextAssertHelper()
            val replacementHelper = nextAssertHelper()
            val itemHeight = scrollView.measuredHeight / 3
            var useReplacement = false
            val binder = it.withModel(itemHeight) {
                if (useReplacement) {
                    trackerTestModel("replacementModel", itemHeight, helper = replacementHelper)
                } else {
                    trackerTestModel("model", itemHeight, helper = helper)
                }
            }

            useReplacement = true
            helper.reset()
            binder.invalidate()
            shadowOf(Looper.getMainLooper()).idle()

            // Replacement of models that use the same view are not currently supported so there should be no changes
            helper.assertDefault()
            replacementHelper.assertDefault()
        }
    }

    @Test
    fun testModelReplaced_differentModel() {
        activityRule.scenario.onActivity {
            val helper = nextAssertHelper()
            val replacementGroupHelper = nextAssertHelper()
            val replacementNestedHelper = nextAssertHelper()
            val itemHeight = scrollView.measuredHeight / 3
            var useReplacement = false
            val binder = it.withModel(itemHeight) {
                if (useReplacement) {
                    trackerTestModelGroup("group", replacementGroupHelper) {
                        layout(R.layout.view_holder_no_databinding)
                        setModels(
                            TrackerTestModel(
                                "innerModel",
                                itemHeight,
                                helper = replacementNestedHelper
                            )
                        )
                    }
                } else {
                    trackerTestModel("model", itemHeight, helper = helper)
                }
            }

            useReplacement = true
            helper.reset()
            binder.invalidate()
            shadowOf(Looper.getMainLooper()).idle()

            // Replacement of new models is only partially supported. The old model will not receive events but the new
            // one will.
            helper.assertDefault()

            replacementGroupHelper.assert(
                visibleHeight = itemHeight,
                percentVisibleHeight = 100f,
                visible = true,
                partialImpression = true,
                fullImpression = true,
                visitedStates = intArrayOf(
                    VisibilityState.VISIBLE,
                    VisibilityState.FOCUSED_VISIBLE,
                    VisibilityState.PARTIAL_IMPRESSION_VISIBLE,
                    VisibilityState.FULL_IMPRESSION_VISIBLE
                )
            )
            replacementNestedHelper.assert(
                visibleHeight = itemHeight,
                percentVisibleHeight = 100f,
                visible = true,
                partialImpression = true,
                fullImpression = true,
                visitedStates = intArrayOf(
                    VisibilityState.VISIBLE,
                    VisibilityState.FOCUSED_VISIBLE,
                    VisibilityState.PARTIAL_IMPRESSION_VISIBLE,
                    VisibilityState.FULL_IMPRESSION_VISIBLE
                )
            )
        }
    }

    @Test
    fun testModelGroup() {
        activityRule.scenario.onActivity {
            val groupHelper = nextAssertHelper()
            val nestedHelper = nextAssertHelper()
            val itemHeight = scrollView.measuredHeight / 2
            it.withModel(itemHeight) {
                trackerTestModelGroup("group", groupHelper) {
                    layout(R.layout.view_holder_no_databinding)
                    setModels(
                        TrackerTestModel("innerModel", itemHeight, helper = nestedHelper)
                    )
                }
            }

            groupHelper.assert(
                visibleHeight = itemHeight,
                percentVisibleHeight = 100f,
                visible = true,
                partialImpression = true,
                fullImpression = true,
                visitedStates = intArrayOf(
                    VisibilityState.VISIBLE,
                    VisibilityState.FOCUSED_VISIBLE,
                    VisibilityState.PARTIAL_IMPRESSION_VISIBLE,
                    VisibilityState.FULL_IMPRESSION_VISIBLE
                )
            )
            nestedHelper.assert(
                visibleHeight = itemHeight,
                percentVisibleHeight = 100f,
                visible = true,
                partialImpression = true,
                fullImpression = true,
                visitedStates = intArrayOf(
                    VisibilityState.VISIBLE,
                    VisibilityState.FOCUSED_VISIBLE,
                    VisibilityState.PARTIAL_IMPRESSION_VISIBLE,
                    VisibilityState.FULL_IMPRESSION_VISIBLE
                )
            )
        }
    }

    @Test
    fun testModelGroup_withCarousel() {
        activityRule.scenario.onActivity {
            val groupHelper = nextAssertHelper()
            val helper1 = nextAssertHelper()
            val helper2 = nextAssertHelper()
            val helper3 = nextAssertHelper()
            val itemHeight = scrollView.measuredHeight / 2
            val itemWidth = (scrollView.measuredWidth / 1.5).roundToInt()
            it.withModel(itemHeight) {
                trackerTestModelGroup("group", groupHelper) {
                    layout(R.layout.view_holder_no_databinding)
                    setModels(
                        CarouselModel_().apply {
                            id("carousel")
                            paddingDp(0)
                            models(
                                listOf(
                                    TrackerTestModel(
                                        "carouselItem1",
                                        itemHeight = itemHeight,
                                        itemWidth = itemWidth,
                                        helper = helper1
                                    ),
                                    TrackerTestModel(
                                        "carouselItem1",
                                        itemHeight = itemHeight,
                                        itemWidth = itemWidth,
                                        helper = helper2
                                    ),
                                    TrackerTestModel(
                                        "carouselItem1",
                                        itemHeight = itemHeight,
                                        itemWidth = itemWidth,
                                        helper = helper3
                                    )
                                )
                            )
                        }
                    )
                }
            }

            groupHelper.assert(
                visibleHeight = itemHeight,
                percentVisibleHeight = 100f,
                visible = true,
                partialImpression = true,
                fullImpression = true,
                visitedStates = intArrayOf(
                    VisibilityState.VISIBLE,
                    VisibilityState.FOCUSED_VISIBLE,
                    VisibilityState.PARTIAL_IMPRESSION_VISIBLE,
                    VisibilityState.FULL_IMPRESSION_VISIBLE
                )
            )

            helper1.assert(
                visibleHeight = itemHeight,
                visibleWidth = itemWidth,
                percentVisibleHeight = 100f,
                percentVisibleWidth = 100f,
                visible = true,
                partialImpression = true,
                fullImpression = true,
                visitedStates = intArrayOf(
                    VisibilityState.VISIBLE,
                    VisibilityState.FOCUSED_VISIBLE,
                    VisibilityState.PARTIAL_IMPRESSION_VISIBLE,
                    VisibilityState.FULL_IMPRESSION_VISIBLE
                )
            )

            helper2.assert(
                visibleHeight = itemHeight,
                visibleWidth = itemWidth / 2,
                percentVisibleHeight = 100f,
                percentVisibleWidth = (scrollView.measuredWidth - itemWidth).toFloat() / itemWidth * 100,
                visible = true,
                partialImpression = false,
                fullImpression = false,
                visitedStates = intArrayOf(
                    VisibilityState.VISIBLE
                )
            )

            helper3.assertDefault()
        }
    }

    @Test
    fun testModelGroup_withCarousel_scrolledToEnd() {
        activityRule.scenario.onActivity {
            val groupHelper = nextAssertHelper()
            val helper1 = nextAssertHelper()
            val helper2 = nextAssertHelper()
            val helper3 = nextAssertHelper()
            val itemHeight = scrollView.measuredHeight / 2
            val itemWidth = (scrollView.measuredWidth / 1.5).roundToInt()
            val binder = it.withModel(itemHeight) {
                trackerTestModelGroup("group", groupHelper) {
                    layout(R.layout.view_holder_no_databinding)
                    setModels(
                        CarouselModel_().apply {
                            id("carousel")
                            paddingDp(0)
                            models(
                                listOf(
                                    TrackerTestModel(
                                        "carouselItem1",
                                        itemHeight = itemHeight,
                                        itemWidth = itemWidth,
                                        helper = helper1
                                    ),
                                    TrackerTestModel(
                                        "carouselItem1",
                                        itemHeight = itemHeight,
                                        itemWidth = itemWidth,
                                        helper = helper2
                                    ),
                                    TrackerTestModel(
                                        "carouselItem1",
                                        itemHeight = itemHeight,
                                        itemWidth = itemWidth,
                                        helper = helper3
                                    )
                                )
                            )
                        }
                    )
                }
            }

            // Scroll so last carousel model is fully visible
            ((binder.view as ViewGroup).getChildAt(0) as RecyclerView)
                .layoutManager!!
                .scrollToPosition(2)
            shadowOf(Looper.getMainLooper()).idle()

            groupHelper.assert(
                visibleHeight = itemHeight,
                percentVisibleHeight = 100f,
                visible = true,
                partialImpression = true,
                fullImpression = true,
                visitedStates = intArrayOf(
                    VisibilityState.VISIBLE,
                    VisibilityState.FOCUSED_VISIBLE,
                    VisibilityState.PARTIAL_IMPRESSION_VISIBLE,
                    VisibilityState.FULL_IMPRESSION_VISIBLE
                )
            )

            helper1.assert(
                visibleHeight = 0,
                visibleWidth = 0,
                percentVisibleHeight = 0f,
                percentVisibleWidth = 0f,
                visible = false,
                partialImpression = false,
                fullImpression = false,
                visitedStates = intArrayOf(
                    VisibilityState.VISIBLE,
                    VisibilityState.FOCUSED_VISIBLE,
                    VisibilityState.PARTIAL_IMPRESSION_VISIBLE,
                    VisibilityState.FULL_IMPRESSION_VISIBLE,
                    VisibilityState.INVISIBLE,
                    VisibilityState.UNFOCUSED_VISIBLE,
                    VisibilityState.PARTIAL_IMPRESSION_INVISIBLE
                )
            )

            helper2.assert(
                visibleHeight = itemHeight,
                visibleWidth = itemWidth / 2,
                percentVisibleHeight = 100f,
                percentVisibleWidth = (scrollView.measuredWidth - itemWidth).toFloat() / itemWidth * 100,
                visible = true,
                partialImpression = false,
                fullImpression = false,
                visitedStates = intArrayOf(
                    VisibilityState.VISIBLE
                )
            )

            helper3.assert(
                visibleHeight = itemHeight,
                visibleWidth = itemWidth,
                percentVisibleHeight = 100f,
                percentVisibleWidth = 100f,
                visible = true,
                partialImpression = true,
                fullImpression = true,
                visitedStates = intArrayOf(
                    VisibilityState.VISIBLE,
                    VisibilityState.FOCUSED_VISIBLE,
                    VisibilityState.PARTIAL_IMPRESSION_VISIBLE,
                    VisibilityState.FULL_IMPRESSION_VISIBLE
                )
            )
        }
    }

    @Test
    fun testCarousel() {
        activityRule.scenario.onActivity {
            val helper1 = nextAssertHelper()
            val helper2 = nextAssertHelper()
            val helper3 = nextAssertHelper()
            val itemHeight = scrollView.measuredHeight / 2
            val itemWidth = (scrollView.measuredWidth / 1.5).roundToInt()
            it.withModel(itemHeight) {
                add(
                    CarouselModel_().apply {
                        id("carousel")
                        paddingDp(0)
                        models(
                            listOf(
                                TrackerTestModel(
                                    "carouselItem1",
                                    itemHeight = itemHeight,
                                    itemWidth = itemWidth,
                                    helper = helper1
                                ),
                                TrackerTestModel(
                                    "carouselItem1",
                                    itemHeight = itemHeight,
                                    itemWidth = itemWidth,
                                    helper = helper2
                                ),
                                TrackerTestModel(
                                    "carouselItem1",
                                    itemHeight = itemHeight,
                                    itemWidth = itemWidth,
                                    helper = helper3
                                )
                            )
                        )
                    }
                )
            }

            helper1.assert(
                visibleHeight = itemHeight,
                visibleWidth = itemWidth,
                percentVisibleHeight = 100f,
                percentVisibleWidth = 100f,
                visible = true,
                partialImpression = true,
                fullImpression = true,
                visitedStates = intArrayOf(
                    VisibilityState.VISIBLE,
                    VisibilityState.FOCUSED_VISIBLE,
                    VisibilityState.PARTIAL_IMPRESSION_VISIBLE,
                    VisibilityState.FULL_IMPRESSION_VISIBLE
                )
            )

            helper2.assert(
                visibleHeight = itemHeight,
                visibleWidth = itemWidth / 2,
                percentVisibleHeight = 100f,
                percentVisibleWidth = (scrollView.measuredWidth - itemWidth).toFloat() / itemWidth * 100,
                visible = true,
                partialImpression = false,
                fullImpression = false,
                visitedStates = intArrayOf(
                    VisibilityState.VISIBLE
                )
            )

            helper3.assertDefault()
        }
    }

    @Test
    fun testCarousel_scrolledToEnd() {
        activityRule.scenario.onActivity {
            val helper1 = nextAssertHelper()
            val helper2 = nextAssertHelper()
            val helper3 = nextAssertHelper()
            val itemHeight = scrollView.measuredHeight / 2
            val itemWidth = (scrollView.measuredWidth / 1.5).roundToInt()
            val binder = it.withModel(itemHeight) {
                add(
                    CarouselModel_().apply {
                        id("carousel")
                        paddingDp(0)
                        models(
                            listOf(
                                TrackerTestModel(
                                    "carouselItem1",
                                    itemHeight = itemHeight,
                                    itemWidth = itemWidth,
                                    helper = helper1
                                ),
                                TrackerTestModel(
                                    "carouselItem1",
                                    itemHeight = itemHeight,
                                    itemWidth = itemWidth,
                                    helper = helper2
                                ),
                                TrackerTestModel(
                                    "carouselItem1",
                                    itemHeight = itemHeight,
                                    itemWidth = itemWidth,
                                    helper = helper3
                                )
                            )
                        )
                    }
                )
            }

            // Scroll so last carousel model is fully visible
            (binder.view as RecyclerView).layoutManager!!.scrollToPosition(2)
            shadowOf(Looper.getMainLooper()).idle()

            helper1.assert(
                visibleHeight = 0,
                visibleWidth = 0,
                percentVisibleHeight = 0f,
                percentVisibleWidth = 0f,
                visible = false,
                partialImpression = false,
                fullImpression = false,
                visitedStates = intArrayOf(
                    VisibilityState.VISIBLE,
                    VisibilityState.FOCUSED_VISIBLE,
                    VisibilityState.PARTIAL_IMPRESSION_VISIBLE,
                    VisibilityState.FULL_IMPRESSION_VISIBLE,
                    VisibilityState.INVISIBLE,
                    VisibilityState.UNFOCUSED_VISIBLE,
                    VisibilityState.PARTIAL_IMPRESSION_INVISIBLE
                )
            )

            helper2.assert(
                visibleHeight = itemHeight,
                visibleWidth = itemWidth / 2,
                percentVisibleHeight = 100f,
                percentVisibleWidth = (scrollView.measuredWidth - itemWidth).toFloat() / itemWidth * 100,
                visible = true,
                partialImpression = false,
                fullImpression = false,
                visitedStates = intArrayOf(
                    VisibilityState.VISIBLE
                )
            )

            helper3.assert(
                visibleHeight = itemHeight,
                visibleWidth = itemWidth,
                percentVisibleHeight = 100f,
                percentVisibleWidth = 100f,
                visible = true,
                partialImpression = true,
                fullImpression = true,
                visitedStates = intArrayOf(
                    VisibilityState.VISIBLE,
                    VisibilityState.FOCUSED_VISIBLE,
                    VisibilityState.PARTIAL_IMPRESSION_VISIBLE,
                    VisibilityState.FULL_IMPRESSION_VISIBLE
                )
            )
        }
    }

    /**
     * Set up an [EpoxyViewBinder] and add models to it. Do not call this multiple times in a test as it will re-set up
     * the view binder, not make incremental changes. If incremental changes are needed, add logic to the
     * [modelProvider].
     *
     * @see [LifecycleAwareEpoxyViewBinder]
     *
     * @param stubHeight the height to use for the view binder's stub. This will inherently be used for the populated
     * model's height per [EpoxyViewBinder] logic.
     * @param modelProvider the provider block used for the [EpoxyViewBinder]
     */
    private fun TestActivity.withModel(
        stubHeight: Int,
        modelProvider: ModelCollector.(Context) -> Unit
    ): LifecycleAwareEpoxyViewBinder {
        val viewBinderContainer = EpoxyViewStub(this)
        viewBinderContainer.layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, stubHeight)
        viewBinderContainer.id = View.generateViewId()
        linearLayout.addView(viewBinderContainer)

        val binder by epoxyView(
            viewBinderContainer.id,
            useVisibilityTracking = true,
            modelProvider = { modelProvider(this@withModel) }
        )

        binder.invalidate()
        shadowOf(Looper.getMainLooper()).idle()
        return binder
    }

    /**
     * Adds an empty view of the desired height to the linear layout in the fragment. Useful for testing partial
     * visibility.
     */
    private fun TestActivity.addSpace(height: Int) {
        val spaceView = Space(this)
        spaceView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        linearLayout.addView(spaceView)
    }

    /** Get a [VisibilityAssertHelper] using an auto-incrementing ID. */
    private fun nextAssertHelper() = VisibilityAssertHelper(ids++)
}
