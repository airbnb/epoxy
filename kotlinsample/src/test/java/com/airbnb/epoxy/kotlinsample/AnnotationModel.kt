package com.airbnb.epoxy.kotlinsample

import android.support.annotation.DrawableRes
import android.support.annotation.FloatRange
import android.support.annotation.StringRes
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder

@EpoxyModelClass(layout = R.layout.activity_kotlin_sample)
abstract class AnnotationModel(
    @StringRes val resId: Int,
    @FloatRange(from = 0.0, to = 1.0) val range: Float
): EpoxyModelWithHolder<AnnotationHolder>() {

    @EpoxyAttribute @DrawableRes var drawable: Int? = null
}

class AnnotationHolder: EpoxyHolder() {
    override fun bindView(itemView: View) {

    }

}
