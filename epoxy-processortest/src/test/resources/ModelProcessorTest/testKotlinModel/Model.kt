package com.airbnb.epoxy

import android.view.View
import androidx.annotation.DrawableRes

@EpoxyModelClass
abstract class Model : EpoxyModelWithHolder<Model.Holder>() {
    @EpoxyAttribute @DrawableRes
    var imageRes: Int = 0

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var clickListener: View.OnClickListener? = null

    override fun getDefaultLayout(): Int = 0

    override fun bind(holder: Holder) {
    }

    override fun unbind(holder: Holder) {
    }

    class Holder : EpoxyHolder() {
        override fun bindView(itemView: View) {

        }
    }
}

