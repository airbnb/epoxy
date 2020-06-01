package com.airbnb.epoxy.kotlinsample.models

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.kotlinsample.R
import com.airbnb.epoxy.kotlinsample.helpers.KotlinEpoxyHolder

// This more traditional style uses an Epoxy view holder pattern.
// The KotlinHolder is used to cache the view look ups, but uses property delegates to simplify it.
// The annotations allow for code generation of a subclass, which has equals/hashcode, and some other
// helpers. An extension function is also generated to make it easier to use this in an EpoxyController.
@EpoxyModelClass(layout = R.layout.view_holder_item)
abstract class ItemEpoxyHolder : EpoxyModelWithHolder<Holder>() {

    @EpoxyAttribute lateinit var listener: () -> Unit
    @EpoxyAttribute lateinit var title: String

    override fun bind(holder: Holder) {
        holder.titleView.setText(title)
        holder.titleView.setOnClickListener { listener() }
    }
}

class Holder : KotlinEpoxyHolder() {
    val titleView by bind<TextView>(R.id.title)
}
