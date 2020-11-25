package com.airbnb.epoxy

import android.os.Looper
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
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

/**
 * Tests visibility tracking of [EpoxyModelGroup]s inside of a [RecyclerView].
 */
@Config(sdk = [21], qualifiers = "h831dp-mdpi")
@RunWith(AndroidJUnit4::class)
class EpoxyVisibilityTrackerModelGroupTest {

    @get:Rule
    var activityRule = activityScenarioRule<TestActivity>()

    private lateinit var recyclerView: EpoxyRecyclerView
    private lateinit var epoxyVisibilityTracker: EpoxyVisibilityTracker

    private var ids = 0

    @Before
    fun setUp() {
        epoxyVisibilityTracker = EpoxyVisibilityTracker()
        activityRule.scenario.onActivity {
            recyclerView = EpoxyRecyclerView(it)
            epoxyVisibilityTracker.attach(recyclerView)
            it.setContentView(recyclerView)
            shadowOf(Looper.getMainLooper()).idle()
        }
    }

    @After
    fun tearDown() {
        epoxyVisibilityTracker.detach(recyclerView)
        ids = 0
    }

    @Test
    fun testGroupInitiallyVisible_full() {
        val groupHelper = nextAssertHelper()
        val nestedHelper = nextAssertHelper()
        val itemHeight = recyclerView.measuredHeight / 2
        withModels {
            trackerTestModelGroup("group", groupHelper) {
                layout(R.layout.vertical_linear_group)
                setModels(
                    TrackerTestModel("innerModel", itemHeight, helper = nestedHelper)
                )
            }
        }

        groupHelper.assert(
            visibleHeight = itemHeight,
            percentVisibleHeight = 100f,
            visible = true,
            partialImpression = false,
            fullImpression = true,
            visitedStates = intArrayOf(
                VisibilityState.VISIBLE,
                VisibilityState.FOCUSED_VISIBLE,
                VisibilityState.FULL_IMPRESSION_VISIBLE
            )
        )
        nestedHelper.assert(
            visibleHeight = itemHeight,
            percentVisibleHeight = 100f,
            visible = true,
            partialImpression = false,
            fullImpression = true,
            visitedStates = intArrayOf(
                VisibilityState.VISIBLE,
                VisibilityState.FOCUSED_VISIBLE,
                VisibilityState.FULL_IMPRESSION_VISIBLE
            )
        )
    }

    @Test
    fun testGroupInitiallyVisible_partial() {
        epoxyVisibilityTracker.partialImpressionThresholdPercentage = 25
        val groupHelper = nextAssertHelper()
        val nestedHelper = nextAssertHelper()
        val firstItemHeight = recyclerView.measuredHeight - 100
        val groupHeight = 200

        withModels {
            trackerTestModel("firstModel", firstItemHeight, helper = nextAssertHelper())
            trackerTestModelGroup("group", groupHelper) {
                layout(R.layout.vertical_linear_group)
                setModels(
                    TrackerTestModel("innerModel", groupHeight, helper = nestedHelper)
                )
            }
        }

        groupHelper.assert(
            visibleHeight = 100,
            percentVisibleHeight = 50f,
            visible = true,
            partialImpression = true,
            fullImpression = false,
            visitedStates = intArrayOf(
                VisibilityState.VISIBLE,
                VisibilityState.PARTIAL_IMPRESSION_VISIBLE
            )
        )
        nestedHelper.assert(
            visibleHeight = 100,
            percentVisibleHeight = 50f,
            visible = true,
            partialImpression = true,
            fullImpression = false,
            visitedStates = intArrayOf(
                VisibilityState.VISIBLE,
                VisibilityState.PARTIAL_IMPRESSION_VISIBLE
            )
        )
    }

    @Test
    fun testGroupInitiallyVisible_partial_underThreshold() {
        epoxyVisibilityTracker.partialImpressionThresholdPercentage = 51
        val groupHelper = nextAssertHelper()
        val nestedHelper = nextAssertHelper()
        val firstItemHeight = recyclerView.measuredHeight - 100
        val groupHeight = 200

        withModels {
            trackerTestModel("firstModel", firstItemHeight, helper = nextAssertHelper())
            trackerTestModelGroup("group", groupHelper) {
                layout(R.layout.vertical_linear_group)
                setModels(
                    TrackerTestModel("innerModel", groupHeight, helper = nestedHelper)
                )
            }
        }

        groupHelper.assert(
            visibleHeight = 100,
            percentVisibleHeight = 50f,
            visible = true,
            partialImpression = false,
            fullImpression = false,
            visitedStates = intArrayOf(
                VisibilityState.VISIBLE
            )
        )
        nestedHelper.assert(
            visibleHeight = 100,
            percentVisibleHeight = 50f,
            visible = true,
            partialImpression = false,
            fullImpression = false,
            visitedStates = intArrayOf(
                VisibilityState.VISIBLE
            )
        )
    }

    @Test
    fun testGroupInitiallyVisible_partial_hiddenChild() {
        epoxyVisibilityTracker.partialImpressionThresholdPercentage = 25
        val groupHelper = nextAssertHelper()
        val nestedHelper = nextAssertHelper()
        val offScreenHelper = nextAssertHelper()
        val firstItemHeight = recyclerView.measuredHeight - 100
        val groupChildHeight = 200

        withModels {
            trackerTestModel("firstModel", firstItemHeight, helper = nextAssertHelper())
            trackerTestModelGroup("group", groupHelper) {
                layout(R.layout.vertical_linear_group)
                setModels(
                    TrackerTestModel("innerModel", groupChildHeight, helper = nestedHelper),
                    TrackerTestModel("offScreenModel", groupChildHeight, helper = offScreenHelper)
                )
            }
        }

        groupHelper.assert(
            visibleHeight = 100,
            percentVisibleHeight = 25f, // half of first child and none of the second
            visible = true,
            partialImpression = true,
            fullImpression = false,
            visitedStates = intArrayOf(
                VisibilityState.VISIBLE,
                VisibilityState.PARTIAL_IMPRESSION_VISIBLE
            )
        )
        nestedHelper.assert(
            visibleHeight = 100,
            percentVisibleHeight = 50f,
            visible = true,
            partialImpression = true,
            fullImpression = false,
            visitedStates = intArrayOf(
                VisibilityState.VISIBLE,
                VisibilityState.PARTIAL_IMPRESSION_VISIBLE
            )
        )
        offScreenHelper.assert(
            visibleHeight = 0,
            percentVisibleHeight = 0f,
            visible = false,
            partialImpression = false,
            fullImpression = false,
            visitedStates = IntArray(0)
        )
    }

    @Test
    fun testGroupInitiallyOffScreen() {
        val groupHelper = nextAssertHelper()
        val nestedHelper = nextAssertHelper()
        val nestedItemHeight = 100
        withModels {
            trackerTestModel(
                "firstModel",
                itemHeight = recyclerView.height,
                helper = nextAssertHelper()
            )
            trackerTestModelGroup("group", groupHelper) {
                layout(R.layout.vertical_linear_group)
                setModels(
                    TrackerTestModel("innerModel", nestedItemHeight, helper = nestedHelper)
                )
            }
        }

        groupHelper.assert(
            visibleHeight = 0,
            percentVisibleHeight = 0f,
            visible = false,
            partialImpression = false,
            fullImpression = false,
            visitedStates = IntArray(0)
        )
        nestedHelper.assert(
            visibleHeight = 0,
            percentVisibleHeight = 0f,
            visible = false,
            partialImpression = false,
            fullImpression = false,
            visitedStates = IntArray(0)
        )
    }

    @Test
    fun testGroupInitiallyOffScreen_scrolledToPartial() {
        epoxyVisibilityTracker.partialImpressionThresholdPercentage = 50
        val groupHelper = nextAssertHelper()
        val nestedHelper = nextAssertHelper()
        val nestedItemHeight = 100
        withModels {
            trackerTestModel(
                "firstModel",
                itemHeight = recyclerView.height,
                helper = nextAssertHelper()
            )
            trackerTestModelGroup("group", groupHelper) {
                layout(R.layout.vertical_linear_group)
                setModels(
                    TrackerTestModel("innerModel", nestedItemHeight, helper = nestedHelper)
                )
            }
        }

        // Scroll so model is partially visible
        (recyclerView.layoutManager as LinearLayoutManager)
            .scrollToPositionWithOffset(0, -nestedItemHeight / 2)
        shadowOf(Looper.getMainLooper()).idle()

        groupHelper.assert(
            visibleHeight = nestedItemHeight / 2,
            percentVisibleHeight = 50f,
            visible = true,
            partialImpression = true,
            fullImpression = false,
            visitedStates = intArrayOf(
                VisibilityState.VISIBLE,
                VisibilityState.PARTIAL_IMPRESSION_VISIBLE
            )

        )
        nestedHelper.assert(
            visibleHeight = nestedItemHeight / 2,
            percentVisibleHeight = 50f,
            visible = true,
            partialImpression = true,
            fullImpression = false,
            visitedStates = intArrayOf(
                VisibilityState.VISIBLE,
                VisibilityState.PARTIAL_IMPRESSION_VISIBLE
            )
        )
    }

    @Test
    fun testGroupInitiallyOffScreen_scrolledToFull() {
        epoxyVisibilityTracker.partialImpressionThresholdPercentage = 50
        val groupHelper = nextAssertHelper()
        val nestedHelper = nextAssertHelper()
        val nestedItemHeight = 100
        withModels {
            trackerTestModel(
                "firstModel",
                itemHeight = recyclerView.height,
                helper = nextAssertHelper()
            )
            trackerTestModelGroup("group", groupHelper) {
                layout(R.layout.vertical_linear_group)
                setModels(
                    TrackerTestModel("innerModel", nestedItemHeight, helper = nestedHelper)
                )
            }
        }

        // Scroll so model is fully visible
        (recyclerView.layoutManager as LinearLayoutManager)
            .scrollToPositionWithOffset(0, -nestedItemHeight)
        shadowOf(Looper.getMainLooper()).idle()

        groupHelper.assert(
            visibleHeight = 100,
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
            visibleHeight = 100,
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

    @Test
    fun testAddModelToGroup() {
        val itemHeight = recyclerView.measuredHeight / 3
        val groupHelper = nextAssertHelper()
        val nestedHelper = nextAssertHelper()
        withModels {
            trackerTestModel("firstModel", itemHeight, helper = nextAssertHelper())
            trackerTestModelGroup("group", groupHelper) {
                layout(R.layout.vertical_linear_group)
                setModels(
                    TrackerTestModel("innerModel", itemHeight, helper = nestedHelper)
                )
            }
        }

        // Reset the existing helpers to test what happens when a model is added
        groupHelper.reset()
        nestedHelper.reset()
        val newHelper = nextAssertHelper()
        withModels {
            trackerTestModel("firstModel", itemHeight, helper = nextAssertHelper())
            trackerTestModelGroup("group", groupHelper) {
                layout(R.layout.vertical_linear_group)
                setModels(
                    TrackerTestModel("innerModel", itemHeight, helper = nestedHelper),
                    TrackerTestModel("newModel", itemHeight, helper = newHelper)
                )
            }
        }

        // Group only has a visible height change as other attributes didn't change
        groupHelper.assert(
            visibleHeight = itemHeight * 2,
            percentVisibleHeight = 100f,
            visible = false,
            partialImpression = false,
            fullImpression = false,
            visitedStates = IntArray(0)
        )

        // Existing nested model doesn't have visibility changes because there was no size change
        nestedHelper.assertDefault()

        // New model has visibility changes
        newHelper.assert(
            visibleHeight = itemHeight,
            percentVisibleHeight = 100f,
            visible = true,
            partialImpression = false,
            fullImpression = true,
            visitedStates = intArrayOf(
                VisibilityState.VISIBLE,
                VisibilityState.FOCUSED_VISIBLE,
                VisibilityState.FULL_IMPRESSION_VISIBLE
            )
        )
    }

    @Test
    fun testRemoveModelFromGroup() {
        val itemHeight = recyclerView.measuredHeight / 3
        val groupHelper = nextAssertHelper()
        val nestedHelper = nextAssertHelper()
        val toRemoveHelper = nextAssertHelper()
        withModels {
            trackerTestModel("firstModel", itemHeight, helper = nextAssertHelper())
            trackerTestModelGroup("group", groupHelper) {
                layout(R.layout.vertical_linear_group)
                setModels(
                    TrackerTestModel("innerModel", itemHeight, helper = nestedHelper),
                    TrackerTestModel("toRemove", itemHeight, helper = toRemoveHelper)
                )
            }
        }

        // Reset the existing helpers to test what happens when a model is removed
        groupHelper.reset()
        nestedHelper.reset()
        toRemoveHelper.reset()
        withModels {
            trackerTestModel("firstModel", itemHeight, helper = nextAssertHelper())
            trackerTestModelGroup("group", groupHelper) {
                layout(R.layout.vertical_linear_group)
                setModels(
                    TrackerTestModel("innerModel", itemHeight, helper = nestedHelper)
                )
            }
        }

        // Group only has a visible height change as other attributes didn't change
        groupHelper.assert(
            visibleHeight = itemHeight,
            percentVisibleHeight = 100f,
            visible = false,
            partialImpression = false,
            fullImpression = false,
            visitedStates = IntArray(0)
        )

        // Existing nested model doesn't have visibility changes because there was no size change
        nestedHelper.assertDefault()

        // Removed model will have no visited states because the model group does not have a
        // reference to it anymore for visibility changes.
        toRemoveHelper.assertDefault()
    }

    @Test
    fun testModelVisibility_existingHiddenModel() {
        val itemHeight = recyclerView.measuredHeight / 3
        val groupHelper = nextAssertHelper()
        val nestedHelper = nextAssertHelper()
        val hiddenHelper = nextAssertHelper()
        withModels {
            trackerTestModel("firstModel", itemHeight, helper = nextAssertHelper())
            trackerTestModelGroup("group", groupHelper) {
                layout(R.layout.vertical_linear_group)
                setModels(
                    TrackerTestModel("innerModel", itemHeight, helper = nestedHelper),
                    TrackerTestModel("toHide", itemHeight, helper = hiddenHelper).hide()
                )
            }
        }

        groupHelper.assert(
            visibleHeight = itemHeight,
            percentVisibleHeight = 100f,
            visible = true,
            partialImpression = false,
            fullImpression = true,
            visitedStates = intArrayOf(
                VisibilityState.VISIBLE,
                VisibilityState.FOCUSED_VISIBLE,
                VisibilityState.FULL_IMPRESSION_VISIBLE
            )
        )
        nestedHelper.assert(
            visibleHeight = itemHeight,
            percentVisibleHeight = 100f,
            visible = true,
            partialImpression = false,
            fullImpression = true,
            visitedStates = intArrayOf(
                VisibilityState.VISIBLE,
                VisibilityState.FOCUSED_VISIBLE,
                VisibilityState.FULL_IMPRESSION_VISIBLE
            )
        )
        // Hidden model is not initially traversed
        hiddenHelper.assert(
            visibleHeight = 0,
            percentVisibleHeight = 0f,
            visible = false,
            partialImpression = false,
            fullImpression = false,
            visitedStates = IntArray(0)
        )
    }

    @Test
    fun testModelVisibility_hideModel() {
        val itemHeight = recyclerView.measuredHeight / 3
        val groupHelper = nextAssertHelper()
        val nestedHelper = nextAssertHelper()
        val toHideHelper = nextAssertHelper()
        withModels {
            trackerTestModel("firstModel", itemHeight, helper = nextAssertHelper())
            trackerTestModelGroup("group", groupHelper) {
                layout(R.layout.vertical_linear_group)
                setModels(
                    TrackerTestModel("innerModel", itemHeight, helper = nestedHelper),
                    TrackerTestModel("toHide", itemHeight, helper = toHideHelper)
                )
            }
        }

        // Reset the existing helpers to test what happens when a model is hidden
        groupHelper.reset()
        nestedHelper.reset()
        toHideHelper.reset()
        withModels {
            trackerTestModel("firstModel", itemHeight, helper = nextAssertHelper())
            trackerTestModelGroup("group", groupHelper) {
                layout(R.layout.vertical_linear_group)
                setModels(
                    TrackerTestModel("innerModel", itemHeight, helper = nestedHelper),
                    TrackerTestModel("toHide", itemHeight, helper = toHideHelper).hide()
                )
            }
        }

        // Group only has a visible height change as other attributes didn't change
        groupHelper.assert(
            visibleHeight = itemHeight,
            percentVisibleHeight = 100f,
            visible = false,
            partialImpression = false,
            fullImpression = false,
            visitedStates = IntArray(0)
        )

        // Existing nested model doesn't have visibility changes because there was no size change
        nestedHelper.assertDefault()

        // Hidden model receives appropriate visibility changes
        toHideHelper.assert(
            visibleHeight = 0,
            percentVisibleHeight = 0f,
            visible = false,
            partialImpression = false,
            fullImpression = false,
            visitedStates = intArrayOf(
                VisibilityState.INVISIBLE,
                VisibilityState.UNFOCUSED_VISIBLE
            )
        )
    }

    @Test
    fun testModelVisibility_showModel() {
        val itemHeight = recyclerView.measuredHeight / 3
        val groupHelper = nextAssertHelper()
        val nestedHelper = nextAssertHelper()
        val toShowHelper = nextAssertHelper()
        withModels {
            trackerTestModel("firstModel", itemHeight, helper = nextAssertHelper())
            trackerTestModelGroup("group", groupHelper) {
                layout(R.layout.vertical_linear_group)
                setModels(
                    TrackerTestModel("innerModel", itemHeight, helper = nestedHelper),
                    TrackerTestModel("toShow", itemHeight, helper = toShowHelper).hide()
                )
            }
        }

        // Reset the existing helpers to test what happens when a hidden model is shown
        groupHelper.reset()
        nestedHelper.reset()
        toShowHelper.reset()
        withModels {
            trackerTestModel("firstModel", itemHeight, helper = nextAssertHelper())
            trackerTestModelGroup("group", groupHelper) {
                layout(R.layout.vertical_linear_group)
                setModels(
                    TrackerTestModel("innerModel", itemHeight, helper = nestedHelper),
                    TrackerTestModel("toShow", itemHeight, helper = toShowHelper)
                )
            }
        }

        // Group only has a visible height change as other attributes didn't change
        groupHelper.assert(
            visibleHeight = itemHeight * 2,
            percentVisibleHeight = 100f,
            visible = false,
            partialImpression = false,
            fullImpression = false,
            visitedStates = IntArray(0)
        )

        // Existing nested model doesn't have visibility changes because there was no size change
        nestedHelper.assertDefault()

        // Shown model receives appropriate visibility changes
        toShowHelper.assert(
            visibleHeight = itemHeight,
            percentVisibleHeight = 100f,
            visible = true,
            partialImpression = false,
            fullImpression = true,
            visitedStates = intArrayOf(
                VisibilityState.VISIBLE,
                VisibilityState.FOCUSED_VISIBLE,
                VisibilityState.FULL_IMPRESSION_VISIBLE
            )
        )
    }

    @Test
    fun testGroupWithCarousel() {
        epoxyVisibilityTracker.partialImpressionThresholdPercentage = 50
        val groupHelper = nextAssertHelper()
        val helper1 = nextAssertHelper()
        val helper2 = nextAssertHelper()
        val helper3 = nextAssertHelper()
        val itemHeight = recyclerView.measuredHeight / 2
        val itemWidth = (recyclerView.measuredWidth / 1.5).roundToInt()
        withModels {
            trackerTestModelGroup("group", groupHelper) {
                layout(R.layout.vertical_linear_group)
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
            percentVisibleWidth = (recyclerView.measuredWidth - itemWidth).toFloat() / itemWidth * 100,
            visible = true,
            partialImpression = true,
            fullImpression = false,
            visitedStates = intArrayOf(
                VisibilityState.VISIBLE,
                VisibilityState.PARTIAL_IMPRESSION_VISIBLE
            )
        )

        helper3.assertDefault()
    }

    @Test
    fun testGroupWithCarousel_noPartialImpression() {
        val groupHelper = nextAssertHelper()
        val helper1 = nextAssertHelper()
        val helper2 = nextAssertHelper()
        val helper3 = nextAssertHelper()
        val itemHeight = recyclerView.measuredHeight / 2
        val itemWidth = (recyclerView.measuredWidth / 1.5).roundToInt()
        withModels {
            trackerTestModelGroup("group", groupHelper) {
                layout(R.layout.vertical_linear_group)
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
            partialImpression = false,
            fullImpression = true,
            visitedStates = intArrayOf(
                VisibilityState.VISIBLE,
                VisibilityState.FOCUSED_VISIBLE,
                VisibilityState.FULL_IMPRESSION_VISIBLE
            )
        )

        helper1.assert(
            visibleHeight = itemHeight,
            visibleWidth = itemWidth,
            percentVisibleHeight = 100f,
            percentVisibleWidth = 100f,
            visible = true,
            partialImpression = false,
            fullImpression = true,
            visitedStates = intArrayOf(
                VisibilityState.VISIBLE,
                VisibilityState.FOCUSED_VISIBLE,
                VisibilityState.FULL_IMPRESSION_VISIBLE
            )
        )

        helper2.assert(
            visibleHeight = itemHeight,
            visibleWidth = itemWidth / 2,
            percentVisibleHeight = 100f,
            percentVisibleWidth = (recyclerView.measuredWidth - itemWidth).toFloat() / itemWidth * 100,
            visible = true,
            partialImpression = false,
            fullImpression = false,
            visitedStates = intArrayOf(
                VisibilityState.VISIBLE
            )
        )

        helper3.assertDefault()
    }

    @Test
    fun testGroupWithCarousel_scrolledToEnd() {
        epoxyVisibilityTracker.partialImpressionThresholdPercentage = 50
        val groupHelper = nextAssertHelper()
        val helper1 = nextAssertHelper()
        val helper2 = nextAssertHelper()
        val helper3 = nextAssertHelper()
        val itemHeight = recyclerView.measuredHeight / 2
        val itemWidth = (recyclerView.measuredWidth / 1.5).roundToInt()

        withModels {
            trackerTestModelGroup("group", groupHelper) {
                layout(R.layout.vertical_linear_group)
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
        ((recyclerView.getChildAt(0) as ViewGroup).getChildAt(0) as RecyclerView)
            .layoutManager
            ?.scrollToPosition(2)
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
            percentVisibleWidth = (recyclerView.measuredWidth - itemWidth).toFloat() / itemWidth * 100,
            visible = true,
            partialImpression = true,
            fullImpression = false,
            visitedStates = intArrayOf(
                VisibilityState.VISIBLE,
                VisibilityState.PARTIAL_IMPRESSION_VISIBLE
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

    /** Get a [VisibilityAssertHelper] using an auto-incrementing ID. */
    private fun nextAssertHelper() = VisibilityAssertHelper(ids++)

    /** Sets the models in the [recyclerView]. */
    private fun withModels(buildModels: EpoxyController.() -> Unit) {
        activityRule.scenario.onActivity {
            recyclerView.withModels {
                buildModels.invoke(this)
            }
            shadowOf(Looper.getMainLooper()).idle()
        }
    }
}
