package com.airbnb.epoxy.composeinterop.maverickssample

import androidx.compose.ui.test.assertIsDisplayed
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
            val textToAssert = "withoutInteropKey mutableState: $i, recomposingCount: ${i + 1}"
            waitUntil(textToAssert)
            checkIfTextIsDisplayed(textToAssert)

            composeTestRule.onNodeWithText(textToAssert).performClick()
        }

        waitUntil("withoutInteropKey mutableState: 11, recomposingCount: 12")
        checkIfTextIsDisplayed("withoutInteropKey mutableState: 11, recomposingCount: 12")

        Thread.sleep(2000)

        waitUntil("withoutInteropKey mutableState: 11, recomposingCount: 12")
        checkIfTextIsDisplayed("withoutInteropKey mutableState: 11, recomposingCount: 12")
    }

    @Test
    fun testComposableInteropWithIntegerKeyUpdatesAndWithoutKeyDoesNot() {
        val withoutInteropKeyText = "withoutInteropKey Int: 0, recomposingCount: 1"

        for (i in 0..10) {
            waitUntil(withoutInteropKeyText)
            waitUntil("withInteropKey Int: $i, recomposingCount: ${i + 1}")

            checkIfTextIsDisplayed(withoutInteropKeyText)
            checkIfTextIsDisplayed("withInteropKey Int: $i, recomposingCount: ${i + 1}")

            composeTestRule.onNodeWithText(withoutInteropKeyText).performClick()
        }

        waitUntil("withInteropKey Int: 11, recomposingCount: 12")
        checkIfTextIsDisplayed(withoutInteropKeyText)
        checkIfTextIsDisplayed("withInteropKey Int: 11, recomposingCount: 12")

        Thread.sleep(2000)

        checkIfTextIsDisplayed(withoutInteropKeyText)
        checkIfTextIsDisplayed("withInteropKey Int: 11, recomposingCount: 12")
    }

    @Test
    fun testComposableInteropWithStringKeyUpdatesAndWithoutKeyDoesNot() {
        var str = ""
        val withoutInteropKeyText = "withoutInteropKey String: , recomposingCount: 1"

        for (i in 0..10) {
            waitUntil(withoutInteropKeyText)
            waitUntil("withInteropKey String: $str, recomposingCount: ${i + 1}")

            checkIfTextIsDisplayed(withoutInteropKeyText)
            checkIfTextIsDisplayed("withInteropKey String: $str, recomposingCount: ${i + 1}")

            composeTestRule.onNodeWithText(withoutInteropKeyText).performClick()

            str += "#"
        }

        waitUntil("withInteropKey String: $str, recomposingCount: 12")
        checkIfTextIsDisplayed(withoutInteropKeyText)
        checkIfTextIsDisplayed("withInteropKey String: $str, recomposingCount: 12")

        Thread.sleep(2000)

        checkIfTextIsDisplayed(withoutInteropKeyText)
        checkIfTextIsDisplayed("withInteropKey String: $str, recomposingCount: 12")
    }

    @Test
    fun testComposableInteropWithListKeyUpdatesAndWithoutKeyDoesNot() {
        val list = mutableListOf<Int>()
        val withoutInteropKeyText = "withoutInteropKey List: [], recomposingCount: 1"

        for (i in 0..10) {
            waitUntil(withoutInteropKeyText)
            waitUntil("withInteropKey List: $list, recomposingCount: ${i + 1}")

            checkIfTextIsDisplayed(withoutInteropKeyText)
            checkIfTextIsDisplayed("withInteropKey List: $list, recomposingCount: ${i + 1}")

            composeTestRule.onNodeWithText(withoutInteropKeyText).performClick()

            list.add(1)
        }

        waitUntil("withInteropKey List: $list, recomposingCount: 12")
        checkIfTextIsDisplayed(withoutInteropKeyText)
        checkIfTextIsDisplayed("withInteropKey List: $list, recomposingCount: 12")

        Thread.sleep(2000)

        checkIfTextIsDisplayed(withoutInteropKeyText)
        checkIfTextIsDisplayed("withInteropKey List: $list, recomposingCount: 12")
    }

    @Test
    fun testComposableInteropWithDataClassKeyUpdatesAndWithoutKeyDoesNot() {
        val list = mutableListOf<Int>()
        val withoutInteropKeyText = "withoutInteropKey DataClass: DataClassKey(listInDataClass=[]), recomposingCount: 1"

        for (i in 0..10) {
            waitUntil(withoutInteropKeyText)
            waitUntil("withInteropKey DataClass: DataClassKey(listInDataClass=$list), recomposingCount: ${i + 1}")

            checkIfTextIsDisplayed(withoutInteropKeyText)
            checkIfTextIsDisplayed("withInteropKey DataClass: DataClassKey(listInDataClass=$list), recomposingCount: ${i + 1}")

            composeTestRule.onNodeWithText(withoutInteropKeyText).performClick()

            list.add(2)
        }

        waitUntil("withInteropKey DataClass: DataClassKey(listInDataClass=$list), recomposingCount: 12")
        checkIfTextIsDisplayed(withoutInteropKeyText)
        checkIfTextIsDisplayed("withInteropKey DataClass: DataClassKey(listInDataClass=$list), recomposingCount: 12")

        Thread.sleep(2000)

        checkIfTextIsDisplayed(withoutInteropKeyText)
        checkIfTextIsDisplayed("withInteropKey DataClass: DataClassKey(listInDataClass=$list), recomposingCount: 12")
    }

    private fun waitUntil(text: String) {
        composeTestRule.waitUntil {
            composeTestRule.onAllNodesWithText(text)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun checkIfTextIsDisplayed(text: String) {
        composeTestRule.onNodeWithText(text).assertIsDisplayed()
    }
}
