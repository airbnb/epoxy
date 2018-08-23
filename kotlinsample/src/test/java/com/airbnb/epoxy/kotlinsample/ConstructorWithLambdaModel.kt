package com.airbnb.epoxy.kotlinsample

import android.view.View
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder

@EpoxyModelClass(layout = R.layout.activity_kotlin_sample)
abstract class KotlinConstructorSample(val listener: (someBool: Boolean, anotherInt: Int) -> Unit): EpoxyModelWithHolder<KotlinConstructorHolder>() {

}

class KotlinConstructorHolder(): EpoxyHolder() {
    override fun bindView(itemView: View) {

    }
}
