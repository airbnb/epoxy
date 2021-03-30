package com.airbnb.epoxy.compose

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun EpoxyInterop(callback: () -> View) {
    AndroidView(
        factory = { _ ->
            callback()
        }
    )
}