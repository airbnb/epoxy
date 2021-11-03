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
class FromModelPropertiesTest {

    @Test
    fun getId() {
        val model = TestModelPropertiesViewModel_.from(
            TestModelProperties(
                id = "100"
            )
        )
        assertFalse(model.hasDefaultId())
    }

    @Test
    fun getBoolean() {
        val model = TestModelPropertiesViewModel_.from(
            TestModelProperties(
                booleanValue = true
            )
        )
        assertEquals(true, model.booleanValue())
    }

    @Test
    fun getBoxedBoolean() {
        val model =
            TestModelPropertiesViewModel_.from(
                TestModelProperties(
                    boxedBooleanValue = true
                )
            )
        assertEquals(true, model.boxedBooleanValue())
    }

    @Test
    fun getDouble() {
        val model = TestModelPropertiesViewModel_.from(
            TestModelProperties(
                doubleValue = 42.0
            )
        )
        assertEquals(42.0, model.doubleValue(), 0.0)
    }

    @Test
    fun getBoxedDouble() {
        val model = TestModelPropertiesViewModel_.from(
            TestModelProperties(
                boxedDoubleValue = 42.0
            )
        )
        assertEquals(42.0, model.boxedDoubleValue(), 0.0)
    }

    @Test
    fun getDrawableRes() {
        val drawableRes = android.R.drawable.alert_dark_frame
        val model =
            TestModelPropertiesViewModel_.from(
                TestModelProperties(
                    drawableRes = drawableRes
                )
            )
        assertEquals(drawableRes, model.drawableRes())
    }

    @Test
    fun getEpoxyModelList() {
        val epoxyModelList = emptyList<EpoxyModel<*>>()
        val model =
            TestModelPropertiesViewModel_.from(
                TestModelProperties(
                    epoxyModelList = epoxyModelList
                )
            )
        assertEquals(epoxyModelList, model.epoxyModelList())
    }

    @Test
    fun getInt() {
        val model = TestModelPropertiesViewModel_.from(
            TestModelProperties(
                intValue = 51
            )
        )
        assertEquals(51, model.intValue())
    }

    @Test
    fun getBoxedInt() {
        val model = TestModelPropertiesViewModel_.from(
            TestModelProperties(
                boxedIntValue = 51
            )
        )
        assertEquals(51, model.boxedIntValue())
    }

    @Test
    fun getLong() {
        val model = TestModelPropertiesViewModel_.from(
            TestModelProperties(
                longValue = 3000000
            )
        )
        assertEquals(3000000, model.longValue())
    }

    @Test
    fun getBoxedLong() {
        val model =
            TestModelPropertiesViewModel_.from(
                TestModelProperties(
                    boxedLongValue = 3000000
                )
            )
        assertEquals(3000000, model.boxedLongValue())
    }

    @Test
    fun getOnClickListener() {
        val clickListener = View.OnClickListener { }
        val model =
            TestModelPropertiesViewModel_.from(
                TestModelProperties(
                    onClickListener = clickListener
                )
            )
        assertEquals(clickListener, model.onClickListener())
    }

    @Test
    fun getNullOnClickListener() {
        val model = TestModelPropertiesViewModel_.from(
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
            TestModelPropertiesViewModel_.from(
                TestModelProperties(
                    rawRes = 42
                )
            )
        assertEquals(42, model.rawRes())
    }

    @Test
    fun getString() {
        val model =
            TestModelPropertiesViewModel_.from(
                TestModelProperties(
                    stringValue = "ModelFactory"
                )
            )
        assertEquals("ModelFactory", model.stringValue())
    }

    @Test
    fun getStringList() {
        val stringList = listOf("Model", "Factory")
        val model = TestModelPropertiesViewModel_.from(
            TestModelProperties(
                stringList = stringList
            )
        )
        assertEquals(stringList, model.stringList())
    }

    class TestModelProperties(
        private val id: String = "",
        private val booleanValue: Boolean? = null,
        private val boxedBooleanValue: Boolean? = null,
        private val doubleValue: Double? = null,
        private val boxedDoubleValue: Double? = null,
        private val drawableRes: Int? = null,
        private val epoxyModelList: List<EpoxyModel<*>>? = null,
        private val intValue: Int? = null,
        private val boxedIntValue: Int? = null,
        private val longValue: Long? = null,
        private val boxedLongValue: Long? = null,
        private val onClickListener: View.OnClickListener? = null,
        private val rawRes: Int? = null,
        private val stringValue: String? = null,
        private val stringList: List<String>? = null,
        private val styleValue: Style? = null
    ) : ModelProperties {

        private val propertyNameToValue = mapOf(
            "booleanValue" to booleanValue,
            "boxedBooleanValue" to boxedBooleanValue,
            "doubleValue" to doubleValue,
            "boxedDoubleValue" to boxedDoubleValue,
            "drawableRes" to drawableRes,
            "epoxyModelList" to epoxyModelList,
            "intValue" to intValue,
            "boxedIntValue" to boxedIntValue,
            "longValue" to longValue,
            "boxedLongValue" to boxedLongValue,
            "onClickListener" to onClickListener,
            "rawRes" to rawRes,
            "stringList" to stringList,
            "stringValue" to stringValue
        )

        @Suppress("UNCHECKED_CAST")
        private fun <T> getValue(propertyName: String): T = propertyNameToValue[propertyName]!! as T

        override fun getId() = id

        override fun has(propertyName: String) = propertyNameToValue[propertyName] != null

        override fun getBoolean(propertyName: String): Boolean = getValue(propertyName)

        override fun getDouble(propertyName: String): Double = getValue(propertyName)

        override fun getDrawableRes(propertyName: String): Int = getValue(propertyName)

        override fun getEpoxyModelList(propertyName: String): List<EpoxyModel<*>> =
            getValue(propertyName)

        override fun getInt(propertyName: String): Int = getValue(propertyName)

        override fun getLong(propertyName: String): Long = getValue(propertyName)

        override fun getOnClickListener(propertyName: String): View.OnClickListener =
            getValue(propertyName)

        override fun getRawRes(propertyName: String): Int = getValue(propertyName)

        override fun getString(propertyName: String): String = getValue(propertyName)

        override fun getStringList(propertyName: String): List<String> = getValue(propertyName)

        override fun getStyle() = styleValue
    }
}
