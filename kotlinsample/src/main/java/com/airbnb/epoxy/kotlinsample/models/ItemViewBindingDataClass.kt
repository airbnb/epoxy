package com.airbnb.epoxy.kotlinsample.models

import com.airbnb.epoxy.kotlinsample.R
import com.airbnb.epoxy.kotlinsample.databinding.DataClassItemBinding
import com.airbnb.epoxy.kotlinsample.helpers.KotlinViewBindingModel

// This does not require annotations or annotation processing.
// The data class is required to generated equals/hashcode which Epoxy needs for diffing.
// Views are easily declared via property delegates
data class ItemViewBindingDataClass(
    val title: String
) : KotlinViewBindingModel<DataClassItemBinding>(R.layout.data_class_item, DataClassItemBinding::bind) {
    override fun bind() {
        binding.title.text = title
    }
}
