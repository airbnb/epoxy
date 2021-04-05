package com.airbnb.epoxy.compose

import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModelWithView

@Composable
fun EpoxyInterop(callback: () -> View) {
    AndroidView(
        factory = { _ ->
            callback()
        }
    )
}

class ComposeEpoxyModel(
    private val composeFunction: @Composable () -> Unit
) : EpoxyModelWithView<ComposeView>() {
    override fun buildView(parent: ViewGroup): ComposeView = ComposeView(parent.context).apply {
        setContent(composeFunction)
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