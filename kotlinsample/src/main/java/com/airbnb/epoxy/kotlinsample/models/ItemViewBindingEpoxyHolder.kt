package com.airbnb.epoxy.kotlinsample.models

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.kotlinsample.R
import com.airbnb.epoxy.kotlinsample.databinding.ViewBindingHolderItemBinding
import com.airbnb.epoxy.kotlinsample.helpers.ViewBindingEpoxyModelWithHolder

@EpoxyModelClass(layout = R.layout.view_binding_holder_item)
abstract class ItemViewBindingEpoxyHolder : ViewBindingEpoxyModelWithHolder<ViewBindingHolderItemBinding>() {

    @EpoxyAttribute lateinit var listener: () -> Unit
    @EpoxyAttribute lateinit var title: String

    override fun ViewBindingHolderItemBinding.bind() {
        title.text = this@ItemViewBindingEpoxyHolder.title
        title.setOnClickListener { listener() }
    }

    override fun ViewBindingHolderItemBinding.unbind() {
        // Don't leak listeners as this view goes back to the view pool
        title.setOnClickListener(null)
    }
}
