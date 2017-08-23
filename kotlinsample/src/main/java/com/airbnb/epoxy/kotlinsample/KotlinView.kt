package com.airbnb.epoxy.kotlinsample

import android.content.Context
import android.view.View
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp


@ModelView(defaultLayout = R.layout.kotlin_view)
class KotlinView(context: Context) : View(context) {

    @TextProp
    fun setTitle(text: CharSequence) {

    }
}