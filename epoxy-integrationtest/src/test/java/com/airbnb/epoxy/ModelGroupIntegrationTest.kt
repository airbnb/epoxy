package com.airbnb.epoxy

import com.airbnb.epoxy.integrationtest.*
import org.junit.*
import org.junit.runner.*
import org.robolectric.*
import org.robolectric.annotation.*

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(21))
class ModelGroupIntegrationTest {

    @Test
    fun modelGroupSubclassIsGenerated() {
        // Just checking that the generated class exists and compiles
        ModelGroupWithAnnotation_(listOf(Model_()))
    }
}
