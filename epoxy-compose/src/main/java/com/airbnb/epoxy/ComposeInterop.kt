package com.airbnb.epoxy

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView

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
    add(
        ComposeEpoxyModel(composeFunction).apply {
            id(id)
        }
    )
}

@Composable
inline fun <reified T : EpoxyModel<*>> ExpoxyInterop(
    crossinline modelBuilder: T.() -> Unit,
    index: Int = 1,
    modifier: Modifier = Modifier
) {
    val model = T::class.java.newInstance().apply(modelBuilder)

    AndroidView(
        factory = { context ->
            FrameLayout(context).apply {
                addView((model.buildView(this)))
            }
        },
        modifier = modifier,
    ) { view ->
        (model as EpoxyModel<View>).bind(view.getChildAt(0))
    }
}
