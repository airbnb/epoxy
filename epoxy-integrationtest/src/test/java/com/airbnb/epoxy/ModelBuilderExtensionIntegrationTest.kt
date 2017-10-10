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
    fun generatedExtensionFunction() {
        Controller().run {
            requestModelBuild()

            val model = adapter.copyOfModels.first() as Model
            assertEquals(model.value, 5)
            assertEquals(model.id(), 1)
        }

    }

    @Test
    fun modelsWithConstructors() {
        Controller().run {
            requestModelBuild()

            val model = adapter.copyOfModels.get(1) as ModelWithConstructors
            assertEquals(model.value, 10)
            assertEquals(model.id(), 2)
        }

    }
}

private class Controller : EpoxyController() {
    override fun buildModels() {
        model {
            id(1)
            value(5)
        }

        modelWithConstructors(value = 10, id = 2) { }
    }

}