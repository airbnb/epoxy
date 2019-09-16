package com.airbnb.epoxy

import com.airbnb.epoxy.ProcessorTestUtils.assertGeneration
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ModelFactoryViewProcessorTest {

    @Test
    fun baseModel() {
        assertGeneration(
            "ModelFactoryBasicModelWithAttribute.java",
            "ModelFactoryBasicModelWithAttribute_.java"
        )
    }

    @Test
    fun baseModelWithFinalAttribute() {
        assertGeneration(
            "BasicModelWithFinalAttribute.java",
            "BasicModelWithFinalAttribute_.java"
        )
    }

    @Test
    fun baseModelView() {
        assertGeneration(
            "ModelFactoryBaseModelView.java",
            "ModelFactoryBaseModelViewModel_.java"
        )
    }

    @Test
    fun groupPropSingleSupportedAttribute() {
        assertGeneration(
            "GroupPropSingleSupportedAttributeModelView.java",
            "GroupPropSingleSupportedAttributeModelViewModel_.java"
        )
    }

    @Test
    fun groupPropMultipleSupportedAttributeDifferentName() {
        // Should generate a from method for both attributes since they have different names,
        // despite being in the same group
        assertGeneration(
            "GroupPropMultipleSupportedAttributeDifferentNameModelView.java",
            "GroupPropMultipleSupportedAttributeDifferentNameModelViewModel_.java"
        )
    }

    @Test
    fun groupPropMultipleSupportedAttributeSameName() {
        // Shouldn't generate a from method, groups with multiple supported attribute types aren't
        // supported
        assertGeneration(
            "GroupPropMultipleSupportedAttributeSameNameModelView.java",
            "GroupPropMultipleSupportedAttributeSameNameModelViewModel_.java"
        )
    }

    @Test
    fun callbackPropModelView() {
        assertGeneration(
            "CallbackPropModelView.java",
            "CallbackPropModelViewModel_.java"
        )
    }

    @Test
    fun textPropModelView() {
        assertGeneration(
            "TextPropModelView.java",
            "TextPropModelViewModel_.java"
        )
    }

    @Test
    fun allTypesModelView() {
        assertGeneration(
            "AllTypesModelView.java",
            "AllTypesModelViewModel_.java"
        )
    }

    @Test
    fun listSubtypeModelView() {
        // Subtypes of List<String> are not supported so no from method should be generated here
        assertGeneration(
            "ListSubtypeModelView.java",
            "ListSubtypeModelViewModel_.java"
        )
    }
}
