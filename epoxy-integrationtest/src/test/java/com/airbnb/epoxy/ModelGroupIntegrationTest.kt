package com.airbnb.epoxy

import com.airbnb.epoxy.integrationtest.ModelGroupWithAnnotation_
import com.airbnb.epoxy.integrationtest.Model_
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
class ModelGroupIntegrationTest {

    @Test
    fun modelGroupSubclassIsGenerated() {
        // Just checking that the generated class exists and compiles
        ModelGroupWithAnnotation_(listOf(Model_()))
    }
}
