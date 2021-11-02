package com.airbnb.epoxy

import android.content.Context
import android.view.View
import android.view.View.OnClickListener

@Deprecated("some message")
@ModelView(
    autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT,
)
class SourceView(context: Context) : View(context) {

    @ModelProp
    fun foo(bar: Int) {}

}



