package com.airbnb.epoxy

import android.view.View
import com.airbnb.epoxymodelfactory.R
import com.airbnb.paris.styles.Style
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * Asserts that using from(ModelProperties) to create a model applies the property values correctly
 */
class FromModelPropertiesTest {

    @Test
    fun getId() {
        val model = TestModelPropertiesViewModel_.from(TestModelProperties(id = "100"))
        assertFalse(model.hasDefaultId())
    }

    @Test
    fun getBoolean() {
        val model = TestModelPropertiesViewModel_.from(TestModelProperties(booleanValue = true))
        assertEquals(true, model.booleanValue())
    }

    @Test
    fun getDouble() {
        val model = TestModelPropertiesViewModel_.from(TestModelProperties(doubleValue = 42.0))
        assertEquals(42.0, model.doubleValue(), 0.0)
    }

    @Test
    fun getDrawableRes() {
        val drawableRes = R.drawable.abc_ic_star_black_48dp
        val model =
            TestModelPropertiesViewModel_.from(TestModelProperties(drawableRes = drawableRes))
        assertEquals(drawableRes, model.drawableRes())
    }

    @Test
    fun getEpoxyModelList() {
        val epoxyModelList = emptyList<EpoxyModel<*>>()
        val model =
            TestModelPropertiesViewModel_.from(TestModelProperties(epoxyModelList = epoxyModelList))
        assertEquals(epoxyModelList, model.epoxyModelList())
    }

    @Test
    fun getInt() {
        val model = TestModelPropertiesViewModel_.from(TestModelProperties(intValue = 51))
        assertEquals(51, model.intValue())
    }

    @Test
    fun getOnClickListener() {
        val clickListener = View.OnClickListener { }
        val model =
            TestModelPropertiesViewModel_.from(TestModelProperties(onClickListener = clickListener))
        assertEquals(clickListener, model.onClickListener())
    }

    @Test
    fun getString() {
        val model =
            TestModelPropertiesViewModel_.from(TestModelProperties(stringValue = "ModelFactory"))
        assertEquals("ModelFactory", model.stringValue())
    }

    @Test
    fun getStringList() {
        val stringList = listOf("Model", "Factory")
        val model = TestModelPropertiesViewModel_.from(TestModelProperties(stringList = stringList))
        assertEquals(stringList, model.stringList())
    }

    class TestModelProperties(
        private val id: String = "",
        private val booleanValue: Boolean? = null,
        private val doubleValue: Double? = null,
        private val drawableRes: Int? = null,
        private val epoxyModelList: List<EpoxyModel<*>>? = null,
        private val intValue: Int? = null,
        private val onClickListener: View.OnClickListener? = null,
        private val stringValue: String? = null,
        private val stringList: List<String>? = null,
        private val styleValue: Style? = null
    ) : ModelProperties {
        override fun getId() = id

        override fun has(propertyName: String): Boolean {
            return mapOf(
                "booleanValue" to booleanValue,
                "doubleValue" to doubleValue,
                "drawableRes" to drawableRes,
                "epoxyModelList" to epoxyModelList,
                "intValue" to intValue,
                "onClickListener" to onClickListener,
                "stringList" to stringList,
                "stringValue" to stringValue
            )[propertyName] != null
        }

        override fun getBoolean(propertyName: String) = booleanValue!!

        override fun getDouble(propertyName: String) = doubleValue!!

        override fun getDrawableRes(propertyName: String) = drawableRes!!

        override fun getEpoxyModelList(propertyName: String) = epoxyModelList!!

        override fun getInt(propertyName: String) = intValue!!

        override fun getOnClickListener(propertyName: String) = onClickListener!!

        override fun getString(propertyName: String) = stringValue!!

        override fun getStringList(propertyName: String) = stringList!!

        override fun getStyle() = styleValue
    }
}
