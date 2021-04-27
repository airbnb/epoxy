package com.airbnb.epoxy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.airbnb.epoxy.integrationtest.TestActivity
import com.airbnb.epoxy.integrationtest.model
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EpoxyViewBinderIntegrationTest {

    @get:Rule
    var activityRule = activityScenarioRule<TestActivity>()

    @Test
    fun testActivity() {
        var initializedValue = 0
        activityRule.scenario.onActivity {
            val frameLayout = FrameLayout(it)
            val stubId = addViewBinderContainer(frameLayout)
            it.setContentView(frameLayout)

            val binder by it.epoxyView(
                stubId,
                initializer = { initializedValue = 1 },
                modelProvider = { buildTestBinderModel() }
            )
            binder.invalidate()

            onView(withText("5")).check(matches(isDisplayed()))
            assertEquals(1, initializedValue)
        }
    }

    @Test
    fun testActivity_optionalBinder() {
        var initializedValue = 0
        activityRule.scenario.onActivity {
            val frameLayout = FrameLayout(it)
            val stubId = addViewBinderContainer(frameLayout)
            it.setContentView(frameLayout)

            val binder by it.optionalEpoxyView(
                stubId,
                initializer = { initializedValue = 1 },
                modelProvider = { buildTestBinderModel() }
            )
            binder!!.invalidate()

            onView(withText("5")).check(matches(isDisplayed()))
            assertEquals(1, initializedValue)
        }
    }

    @Test
    fun testActivity_optionalBinder_noView() {
        var initializedValue = 0
        activityRule.scenario.onActivity {
            val frameLayout = FrameLayout(it)
            addViewBinderContainer(frameLayout)
            it.setContentView(frameLayout)

            val binder by it.optionalEpoxyView(
                View.generateViewId(),
                initializer = { initializedValue = 1 },
                modelProvider = { buildTestBinderModel() }
            )
            assertNull(binder)
            assertEquals(0, initializedValue)
        }
    }

    @Test
    fun testFragment() {
        var initializedValue = 0
        launchFragmentInContainer<TestFragment>().onFragment {
            val stubId = addViewBinderContainer(it.view as ViewGroup)

            val binder by it.epoxyView(
                stubId,
                initializer = { initializedValue = 1 },
                modelProvider = { buildTestBinderModel() }
            )
            binder.invalidate()

            onView(withText("5")).check(matches(isDisplayed()))
            assertEquals(1, initializedValue)
        }
    }

    @Test
    fun testFragment_optionalBinder() {
        var initializedValue = 0
        launchFragmentInContainer<TestFragment>().onFragment {
            val stubId = addViewBinderContainer(it.view as ViewGroup)

            val binder by it.optionalEpoxyView(
                stubId,
                initializer = { initializedValue = 1 },
                modelProvider = { buildTestBinderModel() }
            )
            binder!!.invalidate()

            onView(withText("5")).check(matches(isDisplayed()))
            assertEquals(1, initializedValue)
        }
    }

    @Test
    fun testFragment_optionalBinder_noView() {
        var initializedValue = 0
        launchFragmentInContainer<TestFragment>().onFragment {
            addViewBinderContainer(it.view as ViewGroup)

            val binder by it.optionalEpoxyView(
                View.generateViewId(),
                initializer = { initializedValue = 1 },
                modelProvider = { buildTestBinderModel() }
            )
            assertNull(binder)
            assertEquals(0, initializedValue)
        }
    }

    @Test
    fun testViewGroup() {
        var initializedValue = 0
        activityRule.scenario.onActivity {
            val frameLayout = FrameLayout(it)
            val stubId = addViewBinderContainer(frameLayout)
            it.setContentView(frameLayout)

            val binder by frameLayout.epoxyView(
                stubId,
                initializer = { initializedValue = 1 },
                modelProvider = { buildTestBinderModel() }
            )
            binder.invalidate()

            onView(withText("5")).check(matches(isDisplayed()))
            assertEquals(1, initializedValue)
        }
    }

    @Test
    fun testViewGroup_optionalBinder() {
        var initializedValue = 0
        activityRule.scenario.onActivity {
            val frameLayout = FrameLayout(it)
            val stubId = addViewBinderContainer(frameLayout)
            it.setContentView(frameLayout)

            val binder by frameLayout.optionalEpoxyView(
                stubId,
                initializer = { initializedValue = 1 },
                modelProvider = { buildTestBinderModel() }
            )
            binder!!.invalidate()
            onView(withText("5")).check(matches(isDisplayed()))
            assertEquals(1, initializedValue)
        }
    }

    @Test
    fun testViewGroup_optionalBinder_noView() {
        var initializedValue = 0
        activityRule.scenario.onActivity {
            val frameLayout = FrameLayout(it)
            addViewBinderContainer(frameLayout)
            it.setContentView(frameLayout)

            val binder by frameLayout.optionalEpoxyView(
                View.generateViewId(),
                initializer = { initializedValue = 1 },
                modelProvider = { buildTestBinderModel() }
            )
            assertNull(binder)
            assertEquals(0, initializedValue)
        }
    }

    @Test
    fun testModelUpdated() {
        var initializedValue = 0
        var displayedValue = 5
        activityRule.scenario.onActivity {
            val frameLayout = FrameLayout(it)
            val stubId = addViewBinderContainer(frameLayout)
            it.setContentView(frameLayout)

            val binder by it.epoxyView(
                stubId,
                initializer = { ++initializedValue },
                modelProvider = {
                    model {
                        id(1)
                        value(displayedValue)
                    }
                }
            )
            binder.invalidate()
            onView(withText("5")).check(matches(isDisplayed()))
            assertEquals(1, initializedValue)

            displayedValue = 10
            binder.invalidate()
            onView(withText("5")).check(doesNotExist())
            onView(withText("10")).check(matches(isDisplayed()))
            assertEquals(1, initializedValue) // No change expected
        }
    }

    @Test(expected = IllegalStateException::class)
    fun testNotAnEpoxyViewStub() {
        EpoxyViewBinder.globalExceptionHandler = { _, exception ->
            // Reset back to a no-op handler
            EpoxyViewBinder.globalExceptionHandler = { _, _ -> }
            throw exception
        }
        activityRule.scenario.onActivity {
            val frameLayout = FrameLayout(it)
            val stubId = View.generateViewId()
            View(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = stubId
                frameLayout.addView(this)
            }
            it.setContentView(frameLayout)

            val binder by it.epoxyView(
                stubId,
                initializer = { },
                modelProvider = { buildTestBinderModel() }
            )
            binder.invalidate()
        }
    }

    @Test
    fun testNotAnEpoxyViewStub_exceptionSwallowed() {
        activityRule.scenario.onActivity {
            val frameLayout = FrameLayout(it)
            val stubId = View.generateViewId()
            View(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = stubId
                frameLayout.addView(this)
            }
            it.setContentView(frameLayout)

            val binder by it.epoxyView(
                stubId,
                initializer = { },
                modelProvider = { buildTestBinderModel() }
            )
            binder.invalidate()
        }
        // Not setting an exception handler should not cause a crash
    }

    @Test
    fun testModelUpdate() {
        var initializedValue = 0
        var displayedValue = 5
        activityRule.scenario.onActivity {
            val frameLayout = FrameLayout(it)
            val stubId = addViewBinderContainer(frameLayout)
            it.setContentView(frameLayout)

            val binder by it.epoxyView(
                stubId,
                initializer = { initializedValue = 1 },
                modelProvider = {
                    model {
                        id(1)
                        value(displayedValue)
                    }
                }
            )
            binder.invalidate()
            displayedValue = 10
            binder.invalidate()

            onView(withText("10")).check(matches(isDisplayed()))
            assertEquals(1, initializedValue)
        }
    }

    /** Creates and adds a simple model with a text view showing the text "5". */
    private fun ModelCollector.buildTestBinderModel() {
        model {
            id(1)
            value(5)
        }
    }

    /** Adds an [EpoxyViewStub] as a child of the [root] and returns the view stub's ID. */
    private fun addViewBinderContainer(root: ViewGroup): Int {
        EpoxyViewStub(root.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            id = View.generateViewId()
            root.addView(this)
            return id
        }
    }

    /** Simple fragment with a [FrameLayout] for a root view. */
    class TestFragment : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            return FrameLayout(inflater.context)
        }
    }
}
