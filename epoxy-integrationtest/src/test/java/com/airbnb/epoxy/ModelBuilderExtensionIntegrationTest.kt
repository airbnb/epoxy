package com.airbnb.epoxy

import org.junit.*
import org.junit.runner.*
import org.robolectric.*
import org.robolectric.annotation.*

import com.airbnb.epoxy.model

import org.junit.*
import org.junit.runner.*
import org.robolectric.*
import org.robolectric.annotation.*

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(21))
class ModelBuilderExtensionIntegrationTest {

    @Test
    fun basicAutoModels() {
        Controller().requestModelBuild()
    }
}

private class Controller: EpoxyController() {
    override fun buildModels() {
        model {
            id("model")

        }
    }

}