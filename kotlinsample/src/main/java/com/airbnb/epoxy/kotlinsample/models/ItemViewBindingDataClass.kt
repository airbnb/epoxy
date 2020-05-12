package com.airbnb.epoxy.kotlinsample.models

import com.airbnb.epoxy.kotlinsample.R
import com.airbnb.epoxy.kotlinsample.databinding.DataClassViewBindingItemBinding
import com.airbnb.epoxy.kotlinsample.helpers.ViewBindingKotlinModel

// This does not require annotations or annotation processing.
// The data class is required to generated equals/hashcode which Epoxy needs for diffing.
// Views are easily declared via property delegates
data class ItemViewBindingDataClass(
    val title: String
) : ViewBindingKotlinModel<DataClassViewBindingItemBinding>(R.layout.data_class_view_binding_item) {
    override fun DataClassViewBindingItemBinding.bind() {
        title.text = this@ItemViewBindingDataClass.title
    }
}
