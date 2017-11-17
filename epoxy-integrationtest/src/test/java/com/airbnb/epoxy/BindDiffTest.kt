package com.airbnb.epoxy

import com.airbnb.epoxy.integrationtest.BuildConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Tests that a partial bind of model (from a diff) binds the correct props. This is particularly tricky with prop groups. */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(21))
class BindDiffTest {

    @Test
    fun singlePropChanged() {

    }

    @Test
    fun multiplePropsChanged() {

    }

    @Test
    fun propGroupChangedFromOneAttributeToAnother() {

    }

    @Test
    fun propGroupChangedToDefault() {

    }

    @Test
    fun propGroupChangedFromDefault() {

    }
}