package com.airbnb.epoxy.kotlinsample.models

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.kotlinsample.R

@ModelView(defaultLayout = R.layout.colored_square_view)
class ColoredSquareView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    @JvmOverloads
    @ModelProp
    fun color(@ColorInt color: Int = Color.RED) {
        setBackgroundColor(color)
    }
}
