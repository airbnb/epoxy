package com.airbnb.epoxy.sample.views

import android.content.Context
import android.widget.LinearLayout
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_MATCH_HEIGHT)
class TestView(context: Context?) : LinearLayout(context), TestInterface {
    @TextProp
    override fun setString(str: CharSequence) {
        val a = 5+5
    }
}