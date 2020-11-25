package com.airbnb.epoxy.paging3

import androidx.recyclerview.widget.DiffUtil

/**
 * Dummy item for testing.
 */
data class DummyItem(val id: Int, val value: String) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DummyItem>() {
            override fun areItemsTheSame(oldItem: DummyItem, newItem: DummyItem) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: DummyItem, newItem: DummyItem) = oldItem == newItem
        }
    }
}
