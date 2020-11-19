package com.airbnb.epoxy

import android.app.Activity
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyVisibilityTracker.Companion.DEBUG_LOG
import com.airbnb.epoxy.VisibilityState.INVISIBLE
import com.airbnb.epoxy.VisibilityState.PARTIAL_IMPRESSION_INVISIBLE
import com.airbnb.epoxy.VisibilityState.PARTIAL_IMPRESSION_VISIBLE
import com.airbnb.epoxy.VisibilityState.VISIBLE
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

private typealias AssertHelper = EpoxyVisibilityTrackerTest.AssertHelper
private typealias TrackerTestModel = EpoxyVisibilityTrackerTest.TrackerTestModel

/**
 * This class test the EpoxyVisibilityTracker by using a RecyclerView that scroll vertically. The
 * view port height is provided by Robolectric.
 *
 * We are just controlling how many items are displayed with VISIBLE_ITEMS constant.
 *
 * In order to control the RecyclerView's height we are using theses qualifiers:
 * - `mdpi` for density factor 1
 * - `h831dp` where : 831 = 56 (ToolBar) + 775 (RecyclerView)
 */
@Config(sdk = [21], qualifiers = "h831dp-mdpi")
@RunWith(RobolectricTestRunner::class)
class EpoxyVisibilityTrackerNestedTest {
    companion object {
        private const val TAG = "EpoxyVisibilityTrackerNestedTest"
        /**
         * Visibility ratio for horizontal carousel
         */
        private const val ONE_AND_HALF_VISIBLE = 1.5f

        private fun log(message: String) {
            if (DEBUG_LOG) {
                Log.d(TAG, message)
            }
        }

        private var ids = 0
    }

    private lateinit var activity: Activity
    private lateinit var recyclerView: RecyclerView
    private lateinit var epoxyController: TypedEpoxyController<List<List<AssertHelper>>>
    private var viewportHeight: Int = 0
    private var itemHeight: Int = 0
    private var itemWidth: Int = 0
    private val epoxyVisibilityTracker = EpoxyVisibilityTracker()
    /**
     * For nested visibility what we want is to scroll the parent recycler view and see of the
     * nested recycler view get visibility updates.
     */
    @Test
    fun testScrollBy() {
        if (true) return
        val testHelper = buildTestData(
            10,
            10,
            EpoxyVisibilityTrackerTest.TWO_AND_HALF_VISIBLE,
            ONE_AND_HALF_VISIBLE
        )
        // At this point we have the 1st and 2nd item visible
        // The 3rd item is 50% visible
        // Now scroll to the end
        for (to in 0..testHelper.size) {
            var str = "visible : "
            testHelper.forEachIndexed { y, helpers ->
                if (helpers[0].visible) {
                    str = "$str[$y ${helpers[0].visibleHeight}] "
                }
            }
            log(str)
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(to, 10)
        }
        // Verify visibility event. We will do a pass on every items and assert visiblity for the
        // first and second items in the carousel.
        testHelper.forEachIndexed { y, helpers ->
            helpers.forEachIndexed { x, helper ->

                when {

                    // From 0 to 6 nothing should be visible but they should have been visible
                    // during the scroll

                    y < 7 && x == 0 -> {
                        with(helper) {
                            assert(
                                visibleHeight = 0,
                                percentVisibleHeight = 0.0f,
                                percentVisibleWidth = 0.0f,
                                visible = false,
                                partialImpression = false,
                                fullImpression = false,
                                visitedStates = EpoxyVisibilityTrackerTest.ALL_STATES
                            )
                        }
                    }
                    y < 7 && x == 1 -> {
                        with(helper) {
                            assert(
                                visibleHeight = 0,
                                percentVisibleHeight = 0.0f,
                                percentVisibleWidth = 0.0f,
                                visible = false,
                                partialImpression = false,
                                fullImpression = false,
                                visitedStates = intArrayOf(
                                    VISIBLE,
                                    PARTIAL_IMPRESSION_VISIBLE,
                                    PARTIAL_IMPRESSION_INVISIBLE,
                                    INVISIBLE
                                )
                            )
                        }
                    }

                    // Items at row 7 should be partially visible

                    y == 7 && x == 0 -> {
                        with(helper) {
                            assert(
                                visibleHeight = 50,
                                visibleWidth = 100,
                                visible = true,
                                partialImpression = true,
                                fullImpression = false,
                                visitedStates = intArrayOf(
                                    VISIBLE,
                                    PARTIAL_IMPRESSION_VISIBLE
                                )
                            )
                        }
                    }
                    y == 7 && x == 1 -> {
                        with(helper) {
                            assert(
                                visibleHeight = 50,
                                visibleWidth = 50,
                                visible = true,
                                partialImpression = true,
                                fullImpression = false,
                                visitedStates = intArrayOf(
                                    VISIBLE,
                                    PARTIAL_IMPRESSION_VISIBLE
                                )
                            )
                        }
                    }

                    // Items at row 8 and 9 should be entirely visible (on height)

                    y > 7 && x == 0 -> {
                        with(helper) {
                            assert(
                                percentVisibleHeight = 100.0f,
                                percentVisibleWidth = 100.0f,
                                visible = false,
                                partialImpression = true,
                                fullImpression = true,
                                visitedStates = EpoxyVisibilityTrackerTest.ALL_STATES
                            )
                        }
                    }
                    y > 7 && x == 1 -> {
                        with(helper) {
                            assert(
                                percentVisibleHeight = 100.0f,
                                percentVisibleWidth = 50.0f,
                                visible = false,
                                partialImpression = true,
                                fullImpression = false,
                                visitedStates = intArrayOf(
                                    VISIBLE,
                                    PARTIAL_IMPRESSION_VISIBLE
                                )
                            )
                        }
                    }
                }
                log("$y : $x valid")
            }
        }
    }

    /**
     * Attach an EpoxyController on the RecyclerView
     */
    private fun buildTestData(
        verticalSampleSize: Int,
        horizontalSampleSize: Int,
        verticalVisibleItemsOnScreen: Float,
        horizontalVisibleItemsOnScreen: Float
    ): List<List<AssertHelper>> {
        // Compute individual item height
        itemHeight = (recyclerView.measuredHeight / verticalVisibleItemsOnScreen).toInt()
        itemWidth = (recyclerView.measuredWidth / horizontalVisibleItemsOnScreen).toInt()
        // Build a test sample of sampleSize items
        val helpers = mutableListOf<List<AssertHelper>>().apply {
            for (i in 0 until verticalSampleSize) {
                add(
                    mutableListOf<AssertHelper>().apply {
                        for (j in 0 until horizontalSampleSize) {
                            add(AssertHelper(ids++))
                        }
                    }
                )
            }
        }
        log(helpers.ids())
        epoxyController.setData(helpers)
        return helpers
    }

    /**
     * Setup a RecyclerView and compute item height so we have 3.5 items on screen
     */
    @Before
    fun setup() {
        Robolectric.setupActivity(Activity::class.java).apply {
            setContentView(
                EpoxyRecyclerView(this).apply {
                    epoxyVisibilityTracker.attach(this)
                    recyclerView = this
                    // Plug an epoxy controller
                    epoxyController = object : TypedEpoxyController<List<List<AssertHelper>>>() {
                        override fun buildModels(data: List<List<AssertHelper>>?) {
                            data?.forEachIndexed { index, helpers ->
                                val models = mutableListOf<EpoxyModel<*>>()
                                helpers.forEach { helper ->
                                    models.add(
                                        TrackerTestModel(
                                            itemPosition = index,
                                            itemHeight = itemHeight,
                                            itemWidth = itemWidth,
                                            helper = helper
                                        ).id("$index-${helper.id}")
                                    )
                                }
                                add(
                                    CarouselModel_()
                                        .id(index)
                                        .paddingDp(0)
                                        .models(models)
                                )
                            }
                        }
                    }
                    recyclerView.adapter = epoxyController.adapter
                }
            )
            viewportHeight = recyclerView.measuredHeight
            activity = this
        }
        ShadowLog.stream = System.out
    }

    @After
    fun tearDown() {
        epoxyVisibilityTracker.detach(recyclerView)
    }
}
