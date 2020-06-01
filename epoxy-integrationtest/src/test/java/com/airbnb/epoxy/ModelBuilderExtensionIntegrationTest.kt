package com.airbnb.epoxy

import com.airbnb.epoxy.integrationtest.Model
import com.airbnb.epoxy.integrationtest.ModelWithConstructors
import com.airbnb.epoxy.integrationtest.model
import com.airbnb.epoxy.integrationtest.modelWithConstructors
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
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
