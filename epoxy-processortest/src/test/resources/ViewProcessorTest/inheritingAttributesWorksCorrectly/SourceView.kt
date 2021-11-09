package com.airbnb.epoxy

import android.content.Context
import android.view.View
import java.lang.IllegalStateException

@ModelView(
    autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT,
    baseModelClass = AirEpoxyModel::class
)
class SourceView(context: Context) : BaseView(context) {

    var sectionId: String? = null
        @ModelProp set
}

abstract class AirEpoxyModel<T : View> : EpoxyModel<T>() {

    @EpoxyAttribute(
        EpoxyAttribute.Option.NoGetter,
        EpoxyAttribute.Option.NoSetter
    )
    var showDivider: Boolean? = null

    @EpoxyAttribute()
    var showDividerWithSetter: Boolean? = null

    open fun showDividerWithOverriddenMethod(showDivider: Boolean): AirEpoxyModel<T> {
        return this
    }

    open fun showDivider(showDivider: Boolean): AirEpoxyModel<T> {
        return this
    }

    open fun showDivider(): Boolean {
        return showDivider == true
    }
}

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
open class BaseView(context: Context) : View(context) {

    @ModelProp
    fun baseViewProp(prop: Int) {
    }

    @JvmOverloads
    @ModelProp
    fun baseViewPropWithDefaultParamValue(prop: Int = 0) {
    }
}

