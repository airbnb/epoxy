package com.airbnb.epoxy

import android.view.View
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@Config(sdk = [21])
@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.LEGACY)
class EpoxyRecyclerViewTest {

    private class TestModel : EpoxyModel<View>() {
        override fun getDefaultLayout(): Int = 0
    }

    @Test
    fun withModels() {
        val epoxyRecyclerView = EpoxyRecyclerView(ApplicationProvider.getApplicationContext())

        var modelsToBuild = 1
        epoxyRecyclerView.withModels {
            repeat(modelsToBuild) {
                TestModel().id(it).addTo(this)
            }
        }

        // Initial call should build models
        assertEquals(1, epoxyRecyclerView.adapter?.itemCount)

        // Can rebuild models with requestModelBuild
        modelsToBuild = 4
        epoxyRecyclerView.requestModelBuild()
        assertEquals(4, epoxyRecyclerView.adapter?.itemCount)

        // Setting a different callback should override previous one
        epoxyRecyclerView.withModels {
            TestModel().id(1).addTo(this)
        }

        assertEquals(1, epoxyRecyclerView.adapter?.itemCount)

        // requestModelBuild uses new callback
        epoxyRecyclerView.requestModelBuild()
        assertEquals(1, epoxyRecyclerView.adapter?.itemCount)
    }

    @Test
    fun buildModelsWith() {
        val epoxyRecyclerView = EpoxyRecyclerView(ApplicationProvider.getApplicationContext())

        var modelsToBuild = 1
        epoxyRecyclerView.buildModelsWith(object : EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                repeat(modelsToBuild) {
                    TestModel().id(it).addTo(controller)
                }
            }
        })

        // Initial call should build models
        assertEquals(1, epoxyRecyclerView.adapter?.itemCount)

        // Can rebuild models with requestModelBuild
        modelsToBuild = 4
        epoxyRecyclerView.requestModelBuild()
        assertEquals(4, epoxyRecyclerView.adapter?.itemCount)

        // Setting a different callback should override previous one
        epoxyRecyclerView.buildModelsWith(object : EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                TestModel().id(1).addTo(controller)
            }
        })

        assertEquals(1, epoxyRecyclerView.adapter?.itemCount)

        // requestModelBuild uses new callback
        epoxyRecyclerView.requestModelBuild()
        assertEquals(1, epoxyRecyclerView.adapter?.itemCount)
    }
}
