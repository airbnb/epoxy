package com.airbnb.epoxy.kotlinsample.models

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.kotlinsample.R
import com.airbnb.epoxy.kotlinsample.databinding.ViewBindingHolderItemBinding
import com.airbnb.epoxy.kotlinsample.helpers.ViewBindingEpoxyModel

@EpoxyModelClass(layout = R.layout.view_binding_holder_item)
abstract class ItemViewBindingEpoxyHolder : ViewBindingEpoxyModel<ViewBindingHolderItemBinding>() {

    @EpoxyAttribute lateinit var listener: () -> Unit
    @EpoxyAttribute lateinit var title: String

    override fun ViewBindingHolderItemBinding.bind() {
        title.text = this@ItemViewBindingEpoxyHolder.title
        title.setOnClickListener { listener() }
    }
}
