package com.airbnb.epoxy

import android.support.annotation.DrawableRes
import android.view.View.OnClickListener
import com.airbnb.paris.styles.Style

interface ModelProperties {

    fun getId(): String

    fun has(propertyName: String): Boolean

    fun getBoolean(propertyName: String): Boolean

    fun getDouble(propertyName: String): Double

    @DrawableRes
    fun getDrawableRes(propertyName: String): Int

    fun getInt(propertyName: String): Int

    fun getOnClickListener(propertyName: String): OnClickListener

    fun getString(propertyName: String): String

    fun getStringList(propertyName: String): List<String>

    /**
     * @return Null to apply the default style.
     */
    fun getStyle(): Style?
}
