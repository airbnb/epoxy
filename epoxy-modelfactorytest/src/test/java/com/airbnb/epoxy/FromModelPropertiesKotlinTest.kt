package com.airbnb.epoxy

import android.view.View
import com.airbnb.epoxymodelfactory.R
import com.airbnb.paris.styles.Style
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Asserts that using from(ModelProperties) to create a model applies the property values correctly
 */
class FromModelPropertiesKotlinTest {

    @Test
    fun getId() {
        val model = TestModelPropertiesKotlinViewModel_.from(
            TestModelProperties(
                id = "100"
            )
        )
        assertFalse(model.hasDefaultId())
    }

    @Test
    fun getBoolean() {
        val model =
            TestModelPropertiesKotlinViewModel_.from(
                TestModelProperties(
                    booleanValue = true
                )
            )
        assertEquals(true, model.booleanValue())
    }

    @Test
    fun getDouble() {
        val model =
            TestModelPropertiesKotlinViewModel_.from(
                TestModelProperties(
                    doubleValue = 42.0
                )
            )
        assertEquals(42.0, model.doubleValue(), 0.0)
    }

    @Test
    fun getDrawableRes() {
        val drawableRes = android.R.drawable.alert_dark_frame
        val model = TestModelPropertiesKotlinViewModel_.from(
            TestModelProperties(
                drawableRes = drawableRes
            )
        )
        assertEquals(
            drawableRes, model.drawableRes()
        )
    }

    @Test
    fun getEpoxyModelList() {
        val epoxyModelList = emptyList<EpoxyModel<*>>()
        val model = TestModelPropertiesKotlinViewModel_.from(
            TestModelProperties(
                epoxyModelList = epoxyModelList
            )
        )
        assertEquals(epoxyModelList, model.epoxyModelList())
    }

    @Test
    fun getInt() {
        val model = TestModelPropertiesKotlinViewModel_.from(
            TestModelProperties(
                intValue = 51
            )
        )
        assertEquals(51, model.intValue())
    }

    @Test
    fun getLong() {
        val model =
            TestModelPropertiesKotlinViewModel_.from(
                TestModelProperties(
                    longValue = 3000000L
                )
            )
        assertEquals(3000000L, model.longValue())
    }

    @Test
    fun getOnClickListener() {
        val clickListener = View.OnClickListener { }
        val model = TestModelPropertiesKotlinViewModel_.from(
            TestModelProperties(
                onClickListener = clickListener
            )
        )
        assertEquals(clickListener, model.onClickListener())
    }

    @Test
    fun getNullOnClickListener() {
        val model =
            TestModelPropertiesKotlinViewModel_.from(
                TestModelProperties(
                    onClickListener = null
                )
            )
        assertNull(model.onClickListener())
    }

    @Test
    fun getRawRes() {
        // We use an arbitrary int rather than adding a test-only raw resource, which isn't easy
        val model =
            TestModelPropertiesKotlinViewModel_.from(
                TestModelProperties(
                    rawRes = 42
                )
            )
        assertEquals(42, model.rawRes())
    }

    @Test
    fun getString() {
        val model =
            TestModelPropertiesKotlinViewModel_.from(
                TestModelProperties(
                    stringValue = "ModelFactory"
                )
            )
        assertEquals("ModelFactory", model.stringValue())
    }

    @Test
    fun getStringList() {
        val stringList = listOf("Model", "Factory")
        val model =
            TestModelPropertiesKotlinViewModel_.from(
                TestModelProperties(
                    stringList = stringList
                )
            )
        assertEquals(stringList, model.stringList())
    }

    class TestModelProperties(
        private val id: String = "",
        private val booleanValue: Boolean? = null,
        private val doubleValue: Double? = null,
        private val drawableRes: Int? = null,
        private val epoxyModelList: List<EpoxyModel<*>>? = null,
        private val intValue: Int? = null,
        private val longValue: Long? = null,
        private val onClickListener: View.OnClickListener? = null,
        private val rawRes: Int? = null,
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
                "longValue" to longValue,
                "intValue" to intValue,
                "onClickListener" to onClickListener,
                "rawRes" to rawRes,
                "stringList" to stringList,
                "stringValue" to stringValue
            )[propertyName] != null
        }

        override fun getBoolean(propertyName: String) = booleanValue!!

        override fun getDouble(propertyName: String) = doubleValue!!

        override fun getDrawableRes(propertyName: String) = drawableRes!!

        override fun getEpoxyModelList(propertyName: String) = epoxyModelList!!

        override fun getInt(propertyName: String) = intValue!!

        override fun getLong(propertyName: String) = longValue!!

        override fun getOnClickListener(propertyName: String) = onClickListener!!

        override fun getRawRes(propertyName: String) = rawRes!!

        override fun getString(propertyName: String) = stringValue!!

        override fun getStringList(propertyName: String) = stringList!!

        override fun getStyle() = styleValue
    }
}
