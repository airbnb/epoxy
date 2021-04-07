package com.airbnb.epoxy

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView

class ComposeEpoxyModel(
    private val composeFunction: @Composable () -> Unit
) : EpoxyModelWithView<ComposeView>() {
    override fun buildView(parent: ViewGroup): ComposeView = ComposeView(parent.context)

    override fun bind(view: ComposeView) {
        super.bind(view)
        view.setContent(composeFunction)
    }
}

fun EpoxyController.composableInterop(
    id: String,
    composeFunction: @Composable () -> Unit
) {
    add(ComposeEpoxyModel(composeFunction).apply {
        id(id)
    })
}