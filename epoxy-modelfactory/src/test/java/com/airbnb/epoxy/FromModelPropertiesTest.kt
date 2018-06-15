package com.airbnb.epoxy

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import com.airbnb.paris.styles.Style
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Asserts that using from(ModelProperties) to create a model applies the property values correctly
 */
class FromModelPropertiesTest {

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
    fun getDrawable() {
        val drawable = ColorDrawable(Color.GREEN)
        val model = TestModelPropertiesViewModel_.from(TestModelProperties(drawable = drawable))
        assertEquals(drawable, model.drawable())
    }

    @Test
    fun getInt() {
        val model = TestModelPropertiesViewModel_.from(TestModelProperties(intValue = 51))
        assertEquals(51, model.intValue())
    }

    @Test
    fun getOnClickListener() {
        val clickListener = View.OnClickListener {  }
        val model = TestModelPropertiesViewModel_.from(TestModelProperties(onClickListener = clickListener))
        assertEquals(clickListener, model.onClickListener())
    }

    @Test
    fun getString() {
        val model = TestModelPropertiesViewModel_.from(TestModelProperties(stringValue = "ModelFactory"))
        assertEquals("ModelFactory", model.stringValue())
    }

    @Test
    fun getStringList() {
        val stringList = listOf("Model", "Factory")
        val model = TestModelPropertiesViewModel_.from(TestModelProperties(stringList = stringList))
        assertEquals(stringList, model.stringList())
    }

    class TestModelProperties(
        private val id: String? = "",
        private val booleanValue: Boolean? = null,
        private val doubleValue: Double? = null,
        private val drawable: Drawable? = null,
        private val intValue: Int? = null,
        private val onClickListener: View.OnClickListener? = null,
        private val stringValue: String? = null,
        private val stringList: List<String>? = null,
        private val styleValue: Style? = null
    ) : ModelProperties {

        override fun has(propertyName: String): Boolean {
            return mapOf(
                "id" to id,
                "booleanValue" to booleanValue,
                "doubleValue" to doubleValue,
                "drawable" to drawable,
                "intValue" to intValue,
                "onClickListener" to onClickListener,
                "stringList" to stringList,
                "stringValue" to stringValue
            )[propertyName] != null
        }

        override fun getBoolean(propertyName: String) = booleanValue!!

        override fun getDouble(propertyName: String) = doubleValue!!

        override fun getDrawable(propertyName: String) = drawable!!

        override fun getInt(propertyName: String) = intValue!!

        override fun getOnClickListener(propertyName: String) = onClickListener!!

        override fun getString(propertyName: String): String {
            return if (propertyName == "id") id!! else stringValue!!
        }

        override fun getStringList(propertyName: String) = stringList!!

        override fun getStyle() = styleValue
    }
}
