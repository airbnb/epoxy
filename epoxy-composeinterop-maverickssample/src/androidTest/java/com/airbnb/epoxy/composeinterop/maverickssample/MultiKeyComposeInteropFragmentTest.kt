package com.airbnb.epoxy.composeinterop.maverickssample

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MultiKeyComposeInteropFragmentTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testComposableInInteropUsingMutableStateUpdatesEvenWithoutKey() {
        for (i in 0..10) {
            waitUntil("withoutInteropKey mutableState: $i, recomposingCount: ${i + 1}")

            composeTestRule.onNodeWithText(text = "withoutInteropKey mutableState: $i, recomposingCount: ${i + 1}")
                .performClick()
        }
    }

    @Test
    fun testComposableInteropWithIntegerKeyUpdatesAndWithoutKeyDoesNot() {
        for (i in 0..10) {
            waitUntil("withoutInteropKey Int: 0, recomposingCount: 1")
            waitUntil("withInteropKey Int: $i, recomposingCount: ${i + 1}")

            composeTestRule.onNodeWithText(text = "withoutInteropKey Int: 0, recomposingCount: 1")
                .performClick()
        }
    }

    @Test
    fun testComposableInteropWithStringKeyUpdatesAndWithoutKeyDoesNot() {
        var str = ""

        for (i in 0..10) {
            waitUntil("withoutInteropKey String: , recomposingCount: 1")
            waitUntil("withInteropKey String: $str, recomposingCount: ${i + 1}")

            composeTestRule.onNodeWithText(text = "withoutInteropKey String: , recomposingCount: 1")
                .performClick()

            str += "#"
        }
    }

    @Test
    fun testComposableInteropWithListKeyUpdatesAndWithoutKeyDoesNot() {
        val list = mutableListOf<Int>()

        for (i in 0..10) {
            waitUntil("withoutInteropKey List: [], recomposingCount: 1")
            waitUntil("withInteropKey List: $list, recomposingCount: ${i + 1}")

            composeTestRule.onNodeWithText(text = "withoutInteropKey List: [], recomposingCount: 1")
                .performClick()

            list.add(1)
        }
    }

    @Test
    fun testComposableInteropWithDataClassKeyUpdatesAndWithoutKeyDoesNot() {
        val list = mutableListOf<Int>()

        for (i in 0..10) {
            waitUntil("withoutInteropKey DataClass: DataClassKey(listInDataClass=[]), recomposingCount: 1")
            waitUntil("withInteropKey DataClass: DataClassKey(listInDataClass=$list), recomposingCount: ${i + 1}")

            composeTestRule.onNodeWithText(text = "withoutInteropKey DataClass: DataClassKey(listInDataClass=[]), recomposingCount: 1")
                .performClick()

            list.add(2)
        }
    }

    private fun waitUntil(text: String) {
        composeTestRule.waitUntil {
            composeTestRule.onAllNodesWithText(text)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }
}