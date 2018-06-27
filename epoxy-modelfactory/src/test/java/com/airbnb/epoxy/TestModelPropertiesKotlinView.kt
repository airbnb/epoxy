package com.airbnb.epoxy

import android.content.Context
import android.support.annotation.DrawableRes
import android.view.View
import android.widget.FrameLayout

import com.airbnb.epoxy.ModelView.Size

@ModelView(autoLayout = Size.MATCH_WIDTH_WRAP_HEIGHT)
class TestModelPropertiesKotlinView(context: Context) : FrameLayout(context) {

    @ModelProp
    fun setBooleanValue(value: Boolean) {

    }

    @ModelProp
    fun setDoubleValue(value: Double) {

    }

    @ModelProp
    fun setDrawableRes(@DrawableRes value: Int) {

    }

    @ModelProp
    fun setEpoxyModelList(value: List<EpoxyModel<*>>) {

    }

    @ModelProp
    fun setIntValue(value: Int) {

    }

    @CallbackProp
    override fun setOnClickListener(value: View.OnClickListener?) {

    }

    @ModelProp
    fun setStringValue(value: CharSequence) {

    }

    @ModelProp
    fun setStringList(value: List<String>) {

    }
}
