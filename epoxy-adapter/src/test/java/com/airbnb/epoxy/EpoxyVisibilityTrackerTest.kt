package com.airbnb.epoxy

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyVisibilityTracker.Companion.DEBUG_LOG
import com.airbnb.epoxy.VisibilityState.FOCUSED_VISIBLE
import com.airbnb.epoxy.VisibilityState.FULL_IMPRESSION_VISIBLE
import com.airbnb.epoxy.VisibilityState.INVISIBLE
import com.airbnb.epoxy.VisibilityState.PARTIAL_IMPRESSION_INVISIBLE
import com.airbnb.epoxy.VisibilityState.PARTIAL_IMPRESSION_VISIBLE
import com.airbnb.epoxy.VisibilityState.UNFOCUSED_VISIBLE
import com.airbnb.epoxy.VisibilityState.VISIBLE
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowLog

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
@LooperMode(LooperMode.Mode.LEGACY)
class EpoxyVisibilityTrackerTest {

    companion object {

        private const val TAG = "EpoxyVisibilityTrackerTest"

        /**
         * Make sure the RecyclerView display:
         * - 2 full items
         * - 50% of the next item.
         */
        internal const val TWO_AND_HALF_VISIBLE = 2.5f

        internal val ALL_STATES = intArrayOf(
            VISIBLE,
            INVISIBLE,
            FOCUSED_VISIBLE,
            UNFOCUSED_VISIBLE,
            PARTIAL_IMPRESSION_VISIBLE,
            PARTIAL_IMPRESSION_INVISIBLE,
            FULL_IMPRESSION_VISIBLE
        )

        /**
         * Tolerance used for robolectric ui assertions when comparing data in pixels
         */
        private const val TOLERANCE_PIXELS = 1

        private fun log(message: String) {
            if (DEBUG_LOG) {
                Log.d(TAG, message)
            }
        }

        private var ids = 0
    }

    private lateinit var activity: Activity
    private lateinit var recyclerView: RecyclerView
    private lateinit var epoxyController: TypedEpoxyController<List<AssertHelper>>
    private var viewportHeight: Int = 0
    private var itemHeight: Int = 0

    private val epoxyVisibilityTracker = EpoxyVisibilityTracker()

    /**
     * Test visibility events when loading a recycler view
     */
    @Test
    fun testDataAttachedToRecyclerView() {
        val testHelper = buildTestData(10, TWO_AND_HALF_VISIBLE)

        val firstHalfVisibleItem = 2
        val firstInvisibleItem = firstHalfVisibleItem + 1

        // Verify visibility event
        testHelper.forEachIndexed { index, helper ->
            when {

                index in 0 until firstHalfVisibleItem -> {

                    // Item expected to be 100% visible

                    with(helper) {
                        assert(
                            visibleHeight = itemHeight,
                            percentVisibleHeight = 100.0f,
                            visible = true,
                            partialImpression = true,
                            fullImpression = true,
                            visitedStates = intArrayOf(
                                VISIBLE,
                                FOCUSED_VISIBLE,
                                PARTIAL_IMPRESSION_VISIBLE,
                                FULL_IMPRESSION_VISIBLE
                            )
                        )
                    }
                }

                index == firstHalfVisibleItem -> {

                    // Item expected to be 50% visible

                    with(helper) {
                        assert(
                            visibleHeight = itemHeight / 2,
                            percentVisibleHeight = 50.0f,
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

                index in firstInvisibleItem..9 -> {

                    // Item expected not to be visible

                    with(helper) {
                        assert(
                            visibleHeight = 0,
                            percentVisibleHeight = 0.0f,
                            visible = false,
                            partialImpression = false,
                            fullImpression = false,
                            visitedStates = intArrayOf()
                        )
                    }
                }

                else -> throw IllegalStateException("index should not be bigger than 9")
            }

            log("$index valid")
        }
    }

    /**
     * Test visibility events when loading a recycler view but without any partial visible states
     */
    @Test
    fun testDataAttachedToRecyclerView_WithoutPartial() {
        // disable partial visibility states
        epoxyVisibilityTracker.partialImpressionThresholdPercentage = null

        val testHelper = buildTestData(10, TWO_AND_HALF_VISIBLE)

        val firstHalfVisibleItem = 2
        val firstInvisibleItem = firstHalfVisibleItem + 1

        // Verify visibility event
        testHelper.forEachIndexed { index, helper ->
            when {

                index in 0 until firstHalfVisibleItem -> {

                    // Item expected to be 100% visible

                    with(helper) {
                        assert(
                            visibleHeight = itemHeight,
                            percentVisibleHeight = 100.0f,
                            visible = true,
                            partialImpression = false,
                            fullImpression = true,
                            visitedStates = intArrayOf(
                                VISIBLE,
                                FOCUSED_VISIBLE,
                                FULL_IMPRESSION_VISIBLE
                            )
                        )
                    }
                }

                index == firstHalfVisibleItem -> {

                    // Item expected to be 50% visible

                    with(helper) {
                        assert(
                            visibleHeight = itemHeight / 2,
                            percentVisibleHeight = 50.0f,
                            visible = true,
                            partialImpression = false,
                            fullImpression = false,
                            visitedStates = intArrayOf(
                                VISIBLE
                            )
                        )
                    }
                }

                index in firstInvisibleItem..9 -> {

                    // Item expected not to be visible

                    with(helper) {
                        assert(
                            visibleHeight = 0,
                            percentVisibleHeight = 0.0f,
                            visible = false,
                            partialImpression = false,
                            fullImpression = false,
                            visitedStates = intArrayOf()
                        )
                    }
                }

                else -> throw IllegalStateException("index should not be bigger than 9")
            }

            log("$index valid")
        }
    }

    /**
     * Test partial visibility events when loading a recycler view
     */
    @Test
    fun testDataAttachedToRecyclerView_OneElementJustBelowPartialThreshold() {
        val testHelper = buildTestData(2, 1.49f)

        val firstAlmostPartiallyVisibleItem = 1

        // Verify visibility event
        testHelper.forEachIndexed { index, helper ->
            when {

                index in 0 until firstAlmostPartiallyVisibleItem -> {

                    // Item expected to be 100% visible

                    with(helper) {
                        assert(
                            visibleHeight = itemHeight,
                            percentVisibleHeight = 100.0f,
                            visible = true,
                            partialImpression = true,
                            fullImpression = true,
                            visitedStates = intArrayOf(
                                VISIBLE,
                                FOCUSED_VISIBLE,
                                PARTIAL_IMPRESSION_VISIBLE,
                                FULL_IMPRESSION_VISIBLE
                            )
                        )
                    }
                }

                index == firstAlmostPartiallyVisibleItem -> {

                    // Item expected to be 49% visible

                    with(helper) {
                        assert(
                            visibleHeight = (itemHeight * 0.49).toInt(),
                            percentVisibleHeight = 49.0f,
                            visible = true,
                            partialImpression = false,
                            fullImpression = false,
                            visitedStates = intArrayOf(
                                VISIBLE
                            )
                        )
                    }
                }

                else -> throw IllegalStateException("index should not be bigger than 9")
            }

            log("$index valid")
        }
    }

    /**
     * Test visibility events when adding data to a recycler view (item inserted from adapter)
     */
    @Test
    fun testInsertData() {

        // Build initial list
        val testHelper = buildTestData(10, TWO_AND_HALF_VISIBLE)
        val secondFullyVisibleItemBeforeInsert = testHelper[1]
        val halfVisibleItemBeforeInsert = testHelper[2]

        // Insert in visible area
        val position = 1
        val inserted = insertAt(testHelper, position)

        with(testHelper[position]) {
            assert(
                id = inserted.id,
                visibleHeight = itemHeight,
                percentVisibleHeight = 100.0f,
                visible = true,
                partialImpression = true,
                fullImpression = true,
                visitedStates = intArrayOf(
                    VISIBLE,
                    FOCUSED_VISIBLE,
                    PARTIAL_IMPRESSION_VISIBLE,
                    FULL_IMPRESSION_VISIBLE
                )
            )
        }

        with(secondFullyVisibleItemBeforeInsert) {
            assert(
                visibleHeight = itemHeight / 2,
                percentVisibleHeight = 50.0f,
                visible = true,
                partialImpression = true,
                fullImpression = false,
                visitedStates = intArrayOf(
                    VISIBLE,
                    FOCUSED_VISIBLE,
                    PARTIAL_IMPRESSION_VISIBLE,
                    UNFOCUSED_VISIBLE,
                    FULL_IMPRESSION_VISIBLE
                )
            )
        }

        with(halfVisibleItemBeforeInsert) {
            assert(
                visibleHeight = 0,
                percentVisibleHeight = 0.0f,
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

    /**
     * Test visibility events when removing data from a recycler view (item removed from adapter)
     */
    @Test
    fun testDeleteData() {

        // Build initial list
        val testHelper = buildTestData(10, TWO_AND_HALF_VISIBLE)
        val halfVisibleItemBeforeDelete = testHelper[2]
        val firstNonVisibleItemBeforeDelete = testHelper[3]

        // Delete from visible area
        val position = 1
        val deleted = deleteAt(testHelper, position)

        with(deleted) {
            assert(
                visibleHeight = 0,
                percentVisibleHeight = 0.0f,
                visible = false,
                partialImpression = false,
                fullImpression = false,
                visitedStates = ALL_STATES
            )
        }

        with(halfVisibleItemBeforeDelete) {
            assert(
                visibleHeight = itemHeight,
                percentVisibleHeight = 100.0f,
                visible = true,
                partialImpression = true,
                fullImpression = true,
                visitedStates = intArrayOf(
                    VISIBLE,
                    FOCUSED_VISIBLE,
                    PARTIAL_IMPRESSION_VISIBLE,
                    FULL_IMPRESSION_VISIBLE
                )
            )
        }

        with(firstNonVisibleItemBeforeDelete) {
            assert(
                visibleHeight = itemHeight / 2,
                percentVisibleHeight = 50.0f,
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

    /**
     * Test visibility events when moving data from a recycler view (item moved within adapter)
     *
     * This test is a bit more complex so we will add more data in the sample size so we can test
     * moving a range.
     *
     * What is done :
     * - build a test adapter with a larger sample : 100 items (20 items per screen)
     * - make sure first item is in focus
     * - move the 2 first items to the position 14
     * - make sure recycler view is still displaying the focused item (scrolled to ~14)
     * - make sure the 3rd item is not visible
     */
    @Test
    fun testMoveDataUp() {

        val llm = recyclerView.layoutManager as LinearLayoutManager

        // Build initial list
        val itemsPerScreen = 20
        val testHelper = buildTestData(100, itemsPerScreen.toFloat())

        // First item should be visible and in focus
        Assert.assertEquals(0, llm.findFirstCompletelyVisibleItemPosition())
        Assert.assertEquals(20, llm.findLastVisibleItemPosition())

        // Move the 2 first items to the position 24
        val moved1 = testHelper[0]
        val moved2 = testHelper[1]
        val item3 = testHelper[2]
        moveTwoItems(testHelper, from = 0, to = 14)

        // Because we moved the item in focus (item 0) and the layout manager will maintain the
        // focus the recycler view should scroll to end

        Assert.assertEquals(14, llm.findFirstVisibleItemPosition())
        Assert.assertEquals(
            14 + itemsPerScreen - 1,
            llm.findLastCompletelyVisibleItemPosition()
        )

        with(moved1) {
            // moved 1 should still be in focus so still 100% visible
            assert(
                visibleHeight = itemHeight,
                percentVisibleHeight = 100.0f,
                visible = true,
                partialImpression = true,
                fullImpression = true,
                visitedStates = intArrayOf(
                    VISIBLE,
                    FOCUSED_VISIBLE,
                    PARTIAL_IMPRESSION_VISIBLE,
                    FULL_IMPRESSION_VISIBLE
                )
            )
        }

        with(moved2) {
            // moved 2 should still be in focus so still 100% visible
            assert(
                visibleHeight = itemHeight,
                percentVisibleHeight = 100.0f,
                visible = true,
                partialImpression = true,
                fullImpression = true,
                visitedStates = intArrayOf(
                    VISIBLE,
                    FOCUSED_VISIBLE,
                    PARTIAL_IMPRESSION_VISIBLE,
                    FULL_IMPRESSION_VISIBLE
                )
            )
        }

        with(item3) {
            // the item after moved2 should not be visible now
            assert(
                visibleHeight = 0,
                percentVisibleHeight = 0.0f,
                visible = false,
                partialImpression = false,
                fullImpression = false,
                visitedStates = ALL_STATES
            )
        }
    }

    /**
     * Same kind of test than `testMoveDataUp()` but we move from 24 to 0.
     */
    @Test
    fun testMoveDataDown() {

        val llm = recyclerView.layoutManager as LinearLayoutManager

        // Build initial list
        val itemsPerScreen = 20
        val testHelper = buildTestData(100, itemsPerScreen.toFloat())

        // Scroll to item 24, sharp
        (recyclerView.layoutManager as LinearLayoutManager)
            .scrollToPositionWithOffset(24, 0)

        // First item should be visible and in focus
        Assert.assertEquals(24, llm.findFirstCompletelyVisibleItemPosition())
        Assert.assertEquals(44, llm.findLastVisibleItemPosition())

        // Move the 2 first items to the position 24
        val moved1 = testHelper[24]
        val moved2 = testHelper[25]
        val item3 = testHelper[26]
        moveTwoItems(testHelper, from = 24, to = 0)

        // Because we moved the item in focus (item 0) and the layout manager will maintain the
        // focus the recycler view should scroll to end

        Assert.assertEquals(0, llm.findFirstVisibleItemPosition())
        Assert.assertEquals(19, llm.findLastCompletelyVisibleItemPosition())

        with(moved1) {
            // moved 1 should still be in focus so still 100% visible
            assert(
                visibleHeight = itemHeight,
                percentVisibleHeight = 100.0f,
                visible = true,
                partialImpression = true,
                fullImpression = true,
                visitedStates = intArrayOf(
                    VISIBLE,
                    FOCUSED_VISIBLE,
                    PARTIAL_IMPRESSION_VISIBLE,
                    FULL_IMPRESSION_VISIBLE
                )
            )
        }

        with(moved2) {
            // moved 2 should still be in focus so still 100% visible
            assert(
                visibleHeight = itemHeight,
                percentVisibleHeight = 100.0f,
                visible = true,
                partialImpression = true,
                fullImpression = true,
                visitedStates = intArrayOf(
                    VISIBLE,
                    FOCUSED_VISIBLE,
                    PARTIAL_IMPRESSION_VISIBLE,
                    FULL_IMPRESSION_VISIBLE
                )
            )
        }

        with(item3) {
            // the item after moved2 should not be visible now
            assert(
                visibleHeight = 0,
                percentVisibleHeight = 0.0f,
                visible = false,
                partialImpression = false,
                fullImpression = false,
                visitedStates = ALL_STATES
            )
        }
    }

    /**
     * Test visibility events using scrollToPosition on the recycler view
     */
    @Test
    fun testScrollBy() {
        val testHelper = buildTestData(10, TWO_AND_HALF_VISIBLE)

        // At this point we have the 1st and 2nd item visible
        // The 3rd item is 50% visible

        // Now scroll to the end
        for (to in 0..testHelper.size) {
            (recyclerView.layoutManager as LinearLayoutManager)
                .scrollToPositionWithOffset(to, 10)
        }

        // Verify visibility event
        testHelper.forEachIndexed { index, helper ->
            when {

                index in 0..1 -> {

                    // Item expected not to be visible but should have visited all states

                    with(helper) {
                        assert(
                            visibleHeight = 0,
                            percentVisibleHeight = 0.0f,
                            visible = false,
                            partialImpression = false,
                            fullImpression = false,
                            visitedStates = ALL_STATES
                        )
                    }
                }

                index == 2 -> {

                    // This item was only half visible, it was never fully visible

                    with(helper) {
                        assert(
                            visibleHeight = 0,
                            percentVisibleHeight = 0.0f,
                            visible = false,
                            partialImpression = false,
                            fullImpression = false,
                            visitedStates = ALL_STATES
                        )
                    }
                }

                index in 3..6 -> {

                    // Theses items were never rendered

                    with(helper) {
                        assert(
                            visibleHeight = 0,
                            percentVisibleHeight = 0.0f,
                            visible = false,
                            partialImpression = false,
                            fullImpression = false,
                            visitedStates = ALL_STATES
                        )
                    }
                }

                index == 7 -> {

                    // Item expected to be 50% visible

                    with(helper) {
                        assert(
                            visibleHeight = itemHeight / 2,
                            percentVisibleHeight = 50.0f,
                            visible = true,
                            partialImpression = true,
                            fullImpression = false,
                            visitedStates = intArrayOf(
                                VISIBLE,
                                FOCUSED_VISIBLE,
                                PARTIAL_IMPRESSION_VISIBLE,
                                FULL_IMPRESSION_VISIBLE,
                                UNFOCUSED_VISIBLE
                            )
                        )
                    }
                }

                index in 8..9 -> {

                    // Item expected to be 100% visible

                    with(helper) {
                        assert(
                            visibleHeight = itemHeight,
                            percentVisibleHeight = 100.0f,
                            visible = true,
                            partialImpression = true,
                            fullImpression = true,
                            visitedStates = intArrayOf(
                                VISIBLE,
                                FOCUSED_VISIBLE,
                                PARTIAL_IMPRESSION_VISIBLE,
                                FULL_IMPRESSION_VISIBLE
                            )
                        )
                    }
                }
                else -> throw IllegalStateException("index should not be bigger than 9")
            }

            log("$index valid")
        }
    }

    /**
     * Test visibility events using scrollToPosition on the recycler view
     */
    @Test
    fun testScrollToPosition() {
        val testHelper = buildTestData(10, TWO_AND_HALF_VISIBLE)

        // At this point we have the 1st and 2nd item visible
        // The 3rd item is 50% visible

        // Now scroll to the end
        val scrollToPosition = testHelper.size - 1
        log("scrollToPosition=$scrollToPosition")
        recyclerView.scrollToPosition(scrollToPosition)

        // Verify visibility event
        testHelper.forEachIndexed { index, helper ->
            when {

                index in 0..1 -> {

                    // Item expected not to be visible but should have visited all states

                    with(helper) {
                        assert(
                            visibleHeight = 0,
                            percentVisibleHeight = 0.0f,
                            visible = false,
                            partialImpression = false,
                            fullImpression = false,
                            visitedStates = intArrayOf(
                                VISIBLE,
                                PARTIAL_IMPRESSION_VISIBLE,
                                FOCUSED_VISIBLE,
                                FULL_IMPRESSION_VISIBLE,
                                UNFOCUSED_VISIBLE,
                                PARTIAL_IMPRESSION_INVISIBLE,
                                INVISIBLE
                            )
                        )
                    }
                }

                index == 2 -> {

                    // This item was only half visible, it was never fully visible

                    with(helper) {
                        assert(
                            visibleHeight = 0,
                            percentVisibleHeight = 0.0f,
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

                index in 3..6 -> {

                    // Theses items were never rendered

                    with(helper) {
                        assert(
                            visibleHeight = 0,
                            percentVisibleHeight = 0.0f,
                            visible = false,
                            partialImpression = false,
                            fullImpression = false,
                            visitedStates = intArrayOf()
                        )
                    }
                }

                index == 7 -> {

                    // Item expected to be 50% visible

                    with(helper) {
                        assert(
                            visibleHeight = itemHeight / 2,
                            percentVisibleHeight = 50.0f,
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

                index in 8..9 -> {

                    // Item expected to be 100% visible

                    with(helper) {
                        assert(
                            visibleHeight = itemHeight,
                            percentVisibleHeight = 100.0f,
                            visible = true,
                            partialImpression = true,
                            fullImpression = true,
                            visitedStates = intArrayOf(
                                VISIBLE,
                                PARTIAL_IMPRESSION_VISIBLE,
                                FOCUSED_VISIBLE,
                                FULL_IMPRESSION_VISIBLE
                            )
                        )
                    }
                }
                else -> throw IllegalStateException("index should not be bigger than 9")
            }

            log("$index valid")
        }
    }

    /**
     * Attach an EpoxyController on the RecyclerView
     */
    private fun buildTestData(
        sampleSize: Int,
        visibleItemsOnScreen: Float
    ): MutableList<AssertHelper> {
        // Compute individual item height
        itemHeight = (recyclerView.measuredHeight / visibleItemsOnScreen).toInt()
        // Build a test sample of sampleSize items
        val helpers = mutableListOf<AssertHelper>().apply {
            for (index in 0 until sampleSize) add(AssertHelper(ids++))
        }
        log(helpers.ids())
        epoxyController.setData(helpers)
        return helpers
    }

    private fun insertAt(helpers: MutableList<AssertHelper>, position: Int): AssertHelper {
        log("insert at $position")
        val helper = AssertHelper(ids++)
        helpers.add(position, helper)
        log(helpers.ids())
        epoxyController.setData(helpers)
        return helper
    }

    private fun deleteAt(helpers: MutableList<AssertHelper>, position: Int): AssertHelper {
        log("delete at $position")
        val helper = helpers.removeAt(position)
        log(helpers.ids())
        epoxyController.setData(helpers)
        return helper
    }

    private fun moveTwoItems(helpers: MutableList<AssertHelper>, from: Int, to: Int) {
        log("move at $from -> $to")
        val helper1 = helpers.removeAt(from)
        val helper2 = helpers.removeAt(from)
        helpers.add(to, helper2)
        helpers.add(to, helper1)
        log(helpers.ids())
        epoxyController.setData(helpers)
    }

    /**
     * Setup a RecyclerView and compute item height so we have 3.5 items on screen
     */
    @Before
    fun setup() {
        Robolectric.setupActivity(Activity::class.java).apply {
            setContentView(
                EpoxyRecyclerView(this).apply {
                    epoxyVisibilityTracker.partialImpressionThresholdPercentage = 50
                    epoxyVisibilityTracker.attach(this)
                    recyclerView = this
                    // Plug an epoxy controller
                    epoxyController = object : TypedEpoxyController<List<AssertHelper>>() {
                        override fun buildModels(data: List<AssertHelper>?) {
                            data?.forEachIndexed { index, helper ->
                                add(
                                    TrackerTestModel(
                                        itemPosition = index,
                                        itemHeight = itemHeight,
                                        helper = helper
                                    ).id(helper.id)
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

    /**
     * Epoxy model used for test
     */
    internal class TrackerTestModel(
        private val itemPosition: Int,
        private val itemHeight: Int,
        private val itemWidth: Int = FrameLayout.LayoutParams.MATCH_PARENT,
        private val helper: AssertHelper
    ) : EpoxyModelWithView<View>() {

        override fun buildView(parent: ViewGroup): View {
            log("buildView[$itemPosition](id=${helper.id})")
            return TextView(parent.context).apply {
                // Force height
                layoutParams = RecyclerView.LayoutParams(itemWidth, itemHeight)
            }
        }

        override fun onVisibilityChanged(ph: Float, pw: Float, vh: Int, vw: Int, view: View) {
            helper.percentVisibleHeight = ph
            helper.percentVisibleWidth = pw
            helper.visibleHeight = vh
            helper.visibleWidth = vw
            if (ph.toInt() != 100) helper.fullImpression = false
        }

        override fun onVisibilityStateChanged(state: Int, view: View) {
            log("onVisibilityStateChanged[$itemPosition](id=${helper.id})=${state.description()}")
            helper.visitedStates.add(state)
            when (state) {
                VISIBLE, INVISIBLE ->
                    helper.visible = state == VISIBLE
                FOCUSED_VISIBLE, UNFOCUSED_VISIBLE ->
                    helper.focused = state == FOCUSED_VISIBLE
                PARTIAL_IMPRESSION_VISIBLE, PARTIAL_IMPRESSION_INVISIBLE ->
                    helper.partialImpression = state == PARTIAL_IMPRESSION_VISIBLE
                FULL_IMPRESSION_VISIBLE ->
                    helper.fullImpression = state == FULL_IMPRESSION_VISIBLE
            }
        }
    }

    /**
     * Helper for asserting visibility
     */
    internal class AssertHelper(val id: Int) {

        var created = false
        var visitedStates = mutableListOf<Int>()
        var visibleHeight = 0
        var visibleWidth = 0
        var percentVisibleHeight = 0.0f
        var percentVisibleWidth = 0.0f
        var visible = false
        var focused = false
        var partialImpression = false
        var fullImpression = false

        fun assert(
            id: Int? = null,
            visibleHeight: Int? = null,
            visibleWidth: Int? = null,
            percentVisibleHeight: Float? = null,
            percentVisibleWidth: Float? = null,
            visible: Boolean? = null,
            partialImpression: Boolean? = null,
            fullImpression: Boolean? = null,
            visitedStates: IntArray? = null
        ) {
            id?.let {
                Assert.assertEquals(
                    "id expected $it got ${this.id}",
                    it,
                    this.id
                )
            }
            visibleHeight?.let {
                // assert using tolerance, see TOLERANCE_PIXELS
                log("assert visibleHeight, got $it, expected ${this.visibleHeight}")
                Assert.assertTrue(
                    "visibleHeight expected ${it}px got ${this.visibleHeight}px",
                    Math.abs(it - this.visibleHeight) <= TOLERANCE_PIXELS
                )
            }
            visibleWidth?.let {
                // assert using tolerance, see TOLERANCE_PIXELS
                log("assert visibleWidth, got $it, expected ${this.visibleWidth}")
                Assert.assertTrue(
                    "visibleWidth expected ${it}px got ${this.visibleWidth}px",
                    Math.abs(it - this.visibleWidth) <= TOLERANCE_PIXELS
                )
            }
            percentVisibleHeight?.let {
                Assert.assertEquals(
                    "percentVisibleHeight expected $it got ${this.percentVisibleHeight}",
                    it,
                    this.percentVisibleHeight,
                    0.05f
                )
            }
            percentVisibleWidth?.let {
                Assert.assertEquals(
                    "percentVisibleWidth expected $it got ${this.percentVisibleWidth}",
                    it,
                    this.percentVisibleWidth,
                    0.05f
                )
            }
            visible?.let {
                Assert.assertEquals(
                    "visible expected $it got ${this.visible}",
                    it,
                    this.visible
                )
            }
            partialImpression?.let {
                Assert.assertEquals(
                    "partialImpression expected $it got ${this.partialImpression}",
                    it,
                    this.partialImpression
                )
            }
            fullImpression?.let {
                Assert.assertEquals(
                    "fullImpression expected $it got ${this.fullImpression}",
                    it,
                    this.fullImpression
                )
            }
            visitedStates?.let { assertVisited(it) }
        }

        private fun assertVisited(states: IntArray) {
            val expectedStates = mutableListOf<Int>()
            states.forEach { expectedStates.add(it) }
            for (state in expectedStates) {
                if (!visitedStates.contains(state)) {
                    Assert.fail(
                        "Expected visited ${expectedStates.description()}, " +
                            "got ${visitedStates.description()}"
                    )
                }
            }
            for (state in ALL_STATES) {
                if (!expectedStates.contains(state) && visitedStates.contains(state)) {
                    Assert.fail(
                        "Expected ${state.description()} not visited, " +
                            "got ${visitedStates.description()}"
                    )
                }
            }
        }
    }
}

internal fun <E> List<E>.ids(): String {
    val builder = StringBuilder("[")
    forEachIndexed { index, element ->
        (element as? EpoxyVisibilityTrackerTest.AssertHelper)?.let {
            builder.append(it.id)
        }
        builder.append(if (index < size - 1) "," else "]")
    }
    return builder.toString()
}

/**
 * List of Int to VisibilityState constant names.
 */
private fun List<Int>.description(): String {
    val builder = StringBuilder("[")
    forEachIndexed { index, state ->
        builder.append(state.description())
        builder.append(if (index < size - 1) "," else "")
    }
    builder.append("]")
    return builder.toString()
}

/**
 * Int to VisibilityState constant name.
 */
private fun Int.description(): String {
    return when (this) {
        VISIBLE -> "VISIBLE"
        INVISIBLE -> "INVISIBLE"
        FOCUSED_VISIBLE -> "FOCUSED_VISIBLE"
        UNFOCUSED_VISIBLE -> "UNFOCUSED_VISIBLE"
        PARTIAL_IMPRESSION_VISIBLE -> "PARTIAL_IMPRESSION_VISIBLE"
        PARTIAL_IMPRESSION_INVISIBLE -> "PARTIAL_IMPRESSION_INVISIBLE"
        FULL_IMPRESSION_VISIBLE -> "FULL_IMPRESSION_VISIBLE"
        else -> throw IllegalStateException("Please declare new state here")
    }
}
