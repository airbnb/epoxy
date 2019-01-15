package com.airbnb.epoxy.kotlinsample

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.annotation.StringRes
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder

@EpoxyModelClass(layout = R.layout.activity)
abstract class AnnotationModel(
    @StringRes val resId: Int,
    @FloatRange(from = 0.0, to = 1.0) val range: Float
) : EpoxyModelWithHolder<AnnotationHolder>() {

    @EpoxyAttribute @DrawableRes var drawable: Int? = null
}

class AnnotationHolder : EpoxyHolder() {
    override fun bindView(itemView: View) {
    }
}
