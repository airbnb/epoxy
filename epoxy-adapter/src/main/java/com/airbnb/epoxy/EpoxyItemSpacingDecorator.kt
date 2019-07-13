package com.airbnb.epoxy

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

class EpoxyItemSpacingDecorator(
    @Px var pxBetweenItems: Int = 0
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val padding = pxBetweenItems / 2
        outRect.let {
            it.setEmpty()
            it.bottom = padding
            it.left = padding
            it.right = padding
            it.top = padding
        }
    }
}