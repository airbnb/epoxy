package com.airbnb.epoxy

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.viewinterop.AndroidView

class ComposeEpoxyModel(
    vararg val keys: Any,
    private val composeFunction: @Composable () -> Unit,
) : EpoxyModelWithView<ComposeView>() {

    override fun buildView(parent: ViewGroup): ComposeView = ComposeView(parent.context).apply {
        setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )
    }

    override fun bind(view: ComposeView) {
        super.bind(view)
        view.setContent(composeFunction)
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is ComposeEpoxyModel) return false

        return keys.contentEquals(other.keys)
    }

    override fun hashCode(): Int {
        var code = super.hashCode()

        keys.forEach {
            code = 31 * code + it.hashCode()
        }

        return code
    }

    override fun unbind(view: ComposeView) {
        super.unbind(view)
        view.disposeComposition()
    }

    override fun onViewDetachedFromWindow(view: ComposeView) {
        super.onViewDetachedFromWindow(view)
        view.disposeComposition()
    }
}

fun ModelCollector.composableInterop(
    id: String,
    vararg keys: Any,
    composeFunction: @Composable () -> Unit
) {
    add(
        ComposeEpoxyModel(*keys, composeFunction = composeFunction).apply {
            id(id)
        }
    )
}

@Composable
inline fun <reified T : EpoxyModel<*>> EpoxyInterop(
    modifier: Modifier = Modifier,
    crossinline modelBuilder: T.() -> Unit,
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
