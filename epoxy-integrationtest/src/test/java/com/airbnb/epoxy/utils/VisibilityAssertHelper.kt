package com.airbnb.epoxy.utils

import android.util.Log
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.airbnb.epoxy.VisibilityState
import org.junit.Assert
import kotlin.math.abs

/**
 * Helper for asserting visibility.
 */
internal class VisibilityAssertHelper(val id: Int) {

    var visitedStates = mutableListOf<Int>()
    var visibleHeight = 0
    var visibleWidth = 0
    var percentVisibleHeight = 0.0f
    var percentVisibleWidth = 0.0f
    var visible = false
    var focused = false
    var partialImpression = false
    var fullImpression = false

    /**
     * Resets the attributes to the state where no visibility changes occurred.
     */
    fun reset() {
        visitedStates = mutableListOf()
        visibleHeight = 0
        visibleWidth = 0
        percentVisibleHeight = 0.0f
        percentVisibleWidth = 0.0f
        visible = false
        focused = false
        partialImpression = false
        fullImpression = false
    }

    /**
     * Asserts that no visibility changes occurred.
     */
    fun assertDefault() {
        assert(
            id = id,
            visibleHeight = 0,
            visibleWidth = 0,
            percentVisibleHeight = 0.0f,
            percentVisibleWidth = 0.0f,
            visible = false,
            partialImpression = false,
            fullImpression = false,
            visitedStates = IntArray(0)
        )
    }

    /**
     * Asserts that the desired visibility changes occurred. Use null to omit an assertion for that
     * attribute.
     */
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
                abs(it - this.visibleHeight) <= TOLERANCE_PIXELS
            )
        }
        visibleWidth?.let {
            // assert using tolerance, see TOLERANCE_PIXELS
            log("assert visibleWidth, got $it, expected ${this.visibleWidth}")
            Assert.assertTrue(
                "visibleWidth expected ${it}px got ${this.visibleWidth}px",
                abs(it - this.visibleWidth) <= TOLERANCE_PIXELS
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

    companion object {
        private const val TAG = "VisibilityAssertHelper"
        /**
         * Tolerance used for robolectric ui assertions when comparing data in pixels
         */
        private const val TOLERANCE_PIXELS = 1

        private val ALL_STATES = intArrayOf(
            VisibilityState.VISIBLE,
            VisibilityState.INVISIBLE,
            VisibilityState.FOCUSED_VISIBLE,
            VisibilityState.UNFOCUSED_VISIBLE,
            VisibilityState.PARTIAL_IMPRESSION_VISIBLE,
            VisibilityState.PARTIAL_IMPRESSION_INVISIBLE,
            VisibilityState.FULL_IMPRESSION_VISIBLE
        )

        /**
         * Logs debug messages based on the flag in [EpoxyVisibilityTracker].
         */
        fun log(message: String) {
            if (EpoxyVisibilityTracker.DEBUG_LOG) {
                Log.d(TAG, message)
            }
        }

        /**
         * List of Int to VisibilityState constant names.
         */
        fun Int.description(): String {
            return when (this) {
                VisibilityState.VISIBLE -> "VISIBLE"
                VisibilityState.INVISIBLE -> "INVISIBLE"
                VisibilityState.FOCUSED_VISIBLE -> "FOCUSED_VISIBLE"
                VisibilityState.UNFOCUSED_VISIBLE -> "UNFOCUSED_VISIBLE"
                VisibilityState.PARTIAL_IMPRESSION_VISIBLE -> "PARTIAL_IMPRESSION_VISIBLE"
                VisibilityState.PARTIAL_IMPRESSION_INVISIBLE -> "PARTIAL_IMPRESSION_INVISIBLE"
                VisibilityState.FULL_IMPRESSION_VISIBLE -> "FULL_IMPRESSION_VISIBLE"
                else -> throw IllegalStateException("Please declare new state here")
            }
        }
    }
}
