package com.airbnb.epoxy

import android.view.View
import com.airbnb.epoxy.integrationtest.ViewWithAnnotationsForIntegrationTest
import com.airbnb.epoxy.integrationtest.ViewWithAnnotationsForIntegrationTestModel_
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Tests that a partial bind of model (from a diff) binds the correct props. This is particularly tricky with prop groups. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
class BindDiffTest {

    private inline fun validateDiff(
        model1Props: ViewWithAnnotationsForIntegrationTestModel_.() -> Unit,
        model2Props: ViewWithAnnotationsForIntegrationTestModel_.() -> Unit,
        viewCallVerifications: ViewWithAnnotationsForIntegrationTest.() -> Unit
    ) {
        val model1 = ViewWithAnnotationsForIntegrationTestModel_().id(1).apply(model1Props)
        val model2 = ViewWithAnnotationsForIntegrationTestModel_().id(1).apply(model2Props)

        val viewMock = mock(ViewWithAnnotationsForIntegrationTest::class.java)
        model2.bind(viewMock, model1)

        verify(viewMock).viewCallVerifications()
    }

    @Test
    fun singlePropChanged() {
        validateDiff(
            model1Props = {
                requiredText("hello")
                groupWithNoDefault("text")
                groupWithDefault("text")
            },
            model2Props = {
                requiredText("hello2")
                groupWithNoDefault("text")
                groupWithDefault("text")
            },
            viewCallVerifications = {
                setRequiredText("hello2")
            }
        )
    }

    @Test
    fun multiplePropsChanged() {
        validateDiff(
            model1Props = {
                requiredText("hello")
                groupWithNoDefault("text")
            },
            model2Props = {
                requiredText("hello2")
                groupWithNoDefault("text2")
            },
            viewCallVerifications = {
                setGroupWithNoDefault("text2")
                setRequiredText("hello2")
            }
        )
    }

    @Test
    fun propGroupChangedFromOneAttributeToAnother() {
        val clickListener = View.OnClickListener {}
        validateDiff(
            model1Props = {
                requiredText("hello")
                groupWithNoDefault("text")
            },
            model2Props = {
                requiredText("hello")
                groupWithNoDefault(clickListener)
            },
            viewCallVerifications = {
                setGroupWithNoDefault(clickListener)
            }
        )
    }

    @Test
    fun propGroupChangedToDefault() {
        validateDiff(
            model1Props = {
                requiredText("hello")
                groupWithNoDefault("text")
                groupWithDefault("custom value")
            },
            model2Props = {
                requiredText("hello")
                groupWithNoDefault("text")
            },
            viewCallVerifications = {
                setGroupWithDefault(null as View.OnClickListener?)
            }
        )
    }

    @Test
    fun propGroupChangedFromDefault() {
        validateDiff(
            model1Props = {
                requiredText("hello")
                groupWithNoDefault("text")
            },
            model2Props = {
                requiredText("hello")
                groupWithNoDefault("text")
                groupWithDefault("custom value")
            },
            viewCallVerifications = {
                setGroupWithDefault("custom value")
            }
        )
    }
}
