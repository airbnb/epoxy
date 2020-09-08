package com.airbnb.epoxy

import com.airbnb.epoxy.integrationtest.KotlinViewWithDefaultParams
import com.airbnb.epoxy.integrationtest.KotlinViewWithDefaultParamsModel_
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
@LooperMode(LooperMode.Mode.LEGACY)
class KotlinDefaultParamTest {

    private lateinit var testController: TestController

    @Before
    fun setup() {
        testController = TestController()
    }

    private class TestController : EpoxyController() {
        private val lifecycleHelper = ControllerLifecycleHelper()

        private var model: EpoxyModel<*>? = null

        override fun buildModels() {
            add(model!!)
        }

        fun buildWithModel(model: EpoxyModel<*>) {
            this.model = model.id(1)
            lifecycleHelper.buildModelsAndBind(this)
        }
    }

    class Model(block: Model.() -> Unit) : KotlinViewWithDefaultParamsModel_() {
        var view: KotlinViewWithDefaultParams? = null

        init {
            onBind { _, boundView, _ -> view = boundView }
            block()
        }
    }

    @Test
    fun defaultParamIsUsed() {
        val model = Model {}

        testController.buildWithModel(model)

        assertEquals(KotlinViewWithDefaultParams.IntDefault, model.view?.someIntWithDefaultValue)
    }

    @Test
    fun customValueOverridesDefault() {
        val model = Model {
            someIntWithDefault(100)
        }

        testController.buildWithModel(model)

        assertEquals(100, model.view?.someIntWithDefaultValue)
    }

    @Test
    fun defaultTextPropValueIsUsed() {
        val model = Model {}

        testController.buildWithModel(model)

        assertEquals(KotlinViewWithDefaultParams.TextDefault, model.view?.someTextWithDefaultValue)
    }

    @Test
    fun defaultTextPropValueIsOverwrittenByManualValue() {
        val model = Model {
            someTextWithDefault("my custom value")
        }

        testController.buildWithModel(model)

        assertEquals("my custom value", model.view?.someTextWithDefaultValue)
    }

    @Test
    fun groupHasDefaultValue() {
        val model = Model {}

        testController.buildWithModel(model)

        assertEquals(KotlinViewWithDefaultParams.IntGroupDefault, model.view?.groupValue)
    }

    @Test
    fun overrideGroupValue() {
        val model = Model {
            propInGroupWithDefaultParam(100)
        }

        testController.buildWithModel(model)

        assertEquals(100, model.view?.groupValue)
    }

    @Test
    fun overrideGroupOtherValue() {
        val model = Model {
            otherPropInGroup(100)
        }

        testController.buildWithModel(model)

        assertEquals(100, model.view?.groupValue)
    }
}
