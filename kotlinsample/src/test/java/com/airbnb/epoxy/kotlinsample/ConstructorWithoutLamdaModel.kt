package com.airbnb.epoxy.kotlinsample

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder

@EpoxyModelClass(layout = R.layout.activity)
abstract class KotlinNonConstructorSample() :
    EpoxyModelWithHolder<KotlinNonConstructorSampleHolder>() {
    @EpoxyAttribute
    lateinit var listener: ((boolean: Boolean) -> Unit)
}

class KotlinNonConstructorSampleHolder : EpoxyHolder() {
    override fun bindView(itemView: View) {
    }
}
