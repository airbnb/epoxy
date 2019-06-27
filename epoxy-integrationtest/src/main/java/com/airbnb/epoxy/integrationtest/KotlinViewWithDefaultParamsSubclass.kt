package com.airbnb.epoxy.integrationtest

import android.content.Context
import android.view.View
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp

@ModelView(autoLayout = ModelView.Size.WRAP_WIDTH_MATCH_HEIGHT)
class KotlinViewWithDefaultParamsSubclass(context: Context) : KotlinViewWithDefaultParams(context) {

}
