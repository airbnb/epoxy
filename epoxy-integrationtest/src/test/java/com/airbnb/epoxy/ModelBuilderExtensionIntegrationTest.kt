package com.airbnb.epoxy

import com.airbnb.epoxy.integrationtest.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.*
import org.robolectric.*
import org.robolectric.annotation.*

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(21))
class ModelBuilderExtensionIntegrationTest {

    @Test
    fun basicAutoModels() {
        Controller().run {
            requestModelBuild()

            assertEquals(adapter.copyOfModels.size, 1)

            val model = adapter.copyOfModels.first() as Model
            assertEquals(model.value, 5)
            assertEquals(model.id(), 10)
        }

    }
}

private class Controller : EpoxyController() {
    override fun buildModels() {
        model {
            id(10)
            value(5)
        }
    }

}