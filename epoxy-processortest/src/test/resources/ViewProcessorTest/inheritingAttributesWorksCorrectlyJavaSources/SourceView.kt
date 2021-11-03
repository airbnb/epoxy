package com.airbnb.epoxy

import android.content.Context
import android.view.View

@ModelView(
    autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT,
    baseModelClass = AirEpoxyModel::class
)
class SourceView(context: Context) : BaseView(context) {

    var sectionId: String? = null
        @ModelProp set
}

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
open class BaseView(context: Context) : View(context) {

    @ModelProp
    fun baseViewProp(prop: Int) {
    }
}

