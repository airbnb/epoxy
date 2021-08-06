package com.airbnb.epoxy.composeinterop.maverickssample

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MultiKeyComposeInteropFragmentTest {
    @get:Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    private lateinit var device: UiDevice

    @Before
    fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Launch the app
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE)?.apply {
            // Clear out any previous instances
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)

        device.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT)
        val isVisible = Until.findObject(By.textContains("withInteropKey Int"))
        device.wait(isVisible, LAUNCH_TIMEOUT)
    }

    @Test
    fun testComposableInteropWithIntegerKeyUpdatesAndWithoutKeyDoesNot() {
        for (i in 0..10) {
            val withInteropKeyInt = device.findObject(By.textContains("withInteropKey Int"))
            val withoutInteropKeyInt = device.findObject(By.textContains("withoutInteropKey Int"))

            assert(withoutInteropKeyInt.text == "withoutInteropKey Int: 0")
            assert(withInteropKeyInt.text == "withInteropKey Int: $i")

            withInteropKeyInt.click()
        }
    }

    @Test
    fun testComposableInteropWithStringKeyUpdatesAndWithoutKeyDoesNot() {
        var stringToMatch = ""

        for (i in 0..10) {
            val withInteropKeyInt = device.findObject(By.textContains("withInteropKey String"))
            val withoutInteropKeyInt =
                device.findObject(By.textContains("withoutInteropKey String"))

            assert(withoutInteropKeyInt.text == "withoutInteropKey String: ")
            assert(withInteropKeyInt.text == "withInteropKey String: $stringToMatch")

            stringToMatch += "#"

            withoutInteropKeyInt.click()
        }
    }

    @Test
    fun testComposableInteropWithListKeyUpdatesAndWithoutKeyDoesNot() {
        val list = mutableListOf<Int>()

        for (i in 0..10) {
            val withInteropKeyList = device.findObject(By.textContains("withInteropKey List"))
            val withoutInteropKeyList = device.findObject(By.textContains("withoutInteropKey List"))

            assert(withoutInteropKeyList.text == "withoutInteropKey List: []")
            assert(withInteropKeyList.text == "withInteropKey List: $list")

            list.add(1)

            withoutInteropKeyList.click()
        }
    }

    @Test
    fun testComposableInteropWithDataClassKeyUpdatesAndWithoutKeyDoesNot() {
        val listInDataClass = mutableListOf<Int>()

        for (i in 0..10) {
            val withInteropKeyDataClass =
                device.findObject(By.textContains("withInteropKey DataClass: DataClassKey"))
            val withoutInteropKeyDataClass =
                device.findObject(By.textContains("withoutInteropKey DataClass: DataClassKey"))

            assert(withoutInteropKeyDataClass.text == "withoutInteropKey DataClass: DataClassKey(listInDataClass=[])")
            assert(withInteropKeyDataClass.text == "withInteropKey DataClass: DataClassKey(listInDataClass=$listInDataClass)")

            listInDataClass.add(2)

            withoutInteropKeyDataClass.click()
        }
    }

    companion object {
        private const val LAUNCH_TIMEOUT = 10000L
        private const val BASIC_SAMPLE_PACKAGE = "com.airbnb.epoxy.composeinterop.maverickssample"
    }
}
