package com.airbnb.epoxy

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.airbnb.epoxy.VisibilityState.FOCUSED_VISIBLE
import com.airbnb.epoxy.VisibilityState.FULL_IMPRESSION_VISIBLE
import com.airbnb.epoxy.VisibilityState.INVISIBLE
import com.airbnb.epoxy.VisibilityState.UNFOCUSED_VISIBLE
import com.airbnb.epoxy.VisibilityState.VISIBLE
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import java.lang.StringBuilder

/**
 * This class test the EpoxyVisibilityTracker by using a RecyclerView that scroll vertically. The
 * view port height is provided by Robolectric.
 *
 * We are just controlling how many items are displayed with VISIBLE_ITEMS constant.
 */
@Config(sdk = [21], manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner::class)
class EpoxyVisibilityTrackerTest {

    companion object {

        /**
         * Make sure the RecyclerView display:
         * - 2 full items
         * - 50% of the next item.
         */
        private const val VISIBLE_ITEMS = 2.5
        private val FULLY_VISIBLE_ITEMS = Math.floor(VISIBLE_ITEMS).toInt()
        private val ALL_STATES = intArrayOf(
            VISIBLE,
            INVISIBLE,
            FOCUSED_VISIBLE,
            UNFOCUSED_VISIBLE,
            FULL_IMPRESSION_VISIBLE
        )

        private const val DEBUG_LOG = true
        private fun log(message: String) {
            if (DEBUG_LOG) System.out.println(message)
        }
    }

    private lateinit var activity: Activity
    private lateinit var recyclerView: RecyclerView
    private var viewportHeight: Int = 0
    private var itemHeight: Int = 0

    private val epoxyVisibilityTracker = EpoxyVisibilityTracker()

    /**
     * Test visibility events when loading a recycler view
     */
    @Test
    fun testDataAttachedToRecyclerView() {
        val testHelper = buildTestData(10)

        val firstHalfVisibleItem = FULLY_VISIBLE_ITEMS
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
                            fullImpression = false,
                            visitedStates = intArrayOf(VISIBLE)
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
     * Test visibility events using scrollToPosition on the recycler view
     */
    @Test
    fun testScrollBy() {
        val testHelper = buildTestData(10)

        // At this point we have the 1st and 2nd item visible
        // The 3rd item is 50% visible

        // Now scroll to the end
        for(to in 0..testHelper.size) {
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(to, 10)
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
                            fullImpression = false,
                            visitedStates = intArrayOf(
                                VISIBLE,
                                FOCUSED_VISIBLE,
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
                            fullImpression = true,
                            visitedStates = intArrayOf(
                                VISIBLE,
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
     * Test visibility events using scrollToPosition on the recycler view
     */
    @Test
    fun testScrollToPosition() {
        val testHelper = buildTestData(10)

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
                            fullImpression = false,
                            visitedStates = intArrayOf(
                                VISIBLE,
                                FOCUSED_VISIBLE,
                                FULL_IMPRESSION_VISIBLE,
                                UNFOCUSED_VISIBLE,
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
                            fullImpression = false,
                            visitedStates = intArrayOf(VISIBLE, INVISIBLE)
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
                            fullImpression = false,
                            visitedStates = intArrayOf(VISIBLE)
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
                            fullImpression = true,
                            visitedStates = intArrayOf(
                                VISIBLE,
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
    private fun buildTestData(sampleSize: Int): MutableList<AssertHelper> {
        // Build a test sample of  0 items
        val testHelper = mutableListOf<AssertHelper>().apply {
            for (index in 0 until sampleSize) add(AssertHelper())
        }

        // Plug an epoxy controller
        val controller = object : TypedEpoxyController<Int>() {
            override fun buildModels(data: Int?) {
                data?.let { size ->
                    for (index in 0 until size) {
                        add(TestModel(index, itemHeight, testHelper[index]).id(index))
                    }
                }
            }
        }
        recyclerView.adapter = controller.adapter
        controller.setData(testHelper.size)
        return testHelper
    }

    /**
     * Setup a RecyclerView and compute item height so we have 3.5 items on screen
     */
    @Before
    fun setup() {
        Robolectric.setupActivity(Activity::class.java).apply {
            setContentView(EpoxyRecyclerView(this).apply {
                epoxyVisibilityTracker.attach(this)
                recyclerView = this
            })
            viewportHeight = recyclerView.measuredHeight
            itemHeight = (recyclerView.measuredHeight / VISIBLE_ITEMS).toInt()
            activity = this
        }
    }

    @After
    fun tearDown() {
        epoxyVisibilityTracker.detach(recyclerView)
    }

    /**
     * Epoxy model used for test
     */
    internal class TestModel(
        private val itemPosition: Int,
        private val itemHeight: Int,
        private val helper: AssertHelper
    ) : EpoxyModelWithView<View>() {

        override fun buildView(parent: ViewGroup): View {
            log("buildView[$itemPosition]")
            return TextView(parent.context).apply {
                // Force height
                layoutParams = RecyclerView.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    itemHeight
                )
            }
        }

        override fun onVisibilityChanged(ph: Float, pw: Float, vh: Int, vw: Int, view: View) {
            helper.percentVisibleHeight = ph
            helper.visibleHeight = vh
            if (ph.toInt() != 100) helper.fullImpression = false
        }

        override fun onVisibilityStateChanged(state: Int, view: View) {
            log("onVisibilityStateChanged[$itemPosition]=${state.description()}")
            helper.visitedStates.add(state)
            when (state) {
                VISIBLE, INVISIBLE -> helper.visible = state == VISIBLE
                FOCUSED_VISIBLE, UNFOCUSED_VISIBLE -> helper.focused = state == FOCUSED_VISIBLE
                FULL_IMPRESSION_VISIBLE -> helper.fullImpression = state ==
                        FULL_IMPRESSION_VISIBLE
            }
        }
    }

    /**
     * Helper for asserting visibility
     */
    internal class AssertHelper {

        var created = false
        var visitedStates = mutableListOf<Int>()
        var visibleHeight = 0
        var percentVisibleHeight = 0.0f
        var visible = false
        var focused = false
        var fullImpression = false

        fun assert(
            visibleHeight: Int? = null,
            percentVisibleHeight: Float? = null,
            visible: Boolean? = null,
            fullImpression: Boolean? = null,
            visitedStates: IntArray? = null
        ) {
            visibleHeight?.let {
                // assert with 1px precision
                Assert.assertTrue(
                    "visibleHeight expected $it got ${this.visibleHeight}",
                    Math.abs(it - this.visibleHeight) < 1
                )
            }
            percentVisibleHeight?.let {
                Assert.assertEquals(
                    "percentVisibleHeight expected $it got ${this.percentVisibleHeight}",
                    it,
                    this.percentVisibleHeight
                )
            }
            visible?.let {
                Assert.assertEquals(
                    "visible expected $it got ${this.visible}",
                    it,
                    this.visible
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
                    Assert.fail("Expected visited ${expectedStates.description()}, got ${visitedStates.description()}")
                }
            }
            for (state in ALL_STATES) {
                if (!expectedStates.contains(state) && visitedStates.contains(state)) {
                    Assert.fail("Expected ${state.description()} not visited, got ${visitedStates.description()}")
                }
            }
        }
    }

}

/**
 * List of Int to VisibilityState constant names.
 */
private fun List<Int>.description(): String {
    val builder = StringBuilder("[")
    forEachIndexed { index, state ->
        builder.append(state.description())
        builder.append(if (index < size - 1) "," else "]")
    }
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
        FULL_IMPRESSION_VISIBLE -> "FULL_IMPRESSION_VISIBLE"
        else -> throw IllegalStateException("Please declare new state here")
    }
}
