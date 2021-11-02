package com.airbnb.epoxy

import android.view.View

@EpoxyModelClass
abstract class AbstractModelWithHolder : EpoxyModelWithHolder<Holder>() {
    @EpoxyAttribute
    var value = 0

    override fun getDefaultLayout(): Int {
        return 0
    }
}

class Holder : EpoxyHolder() {
    override fun bindView(itemView: View) {}
}
