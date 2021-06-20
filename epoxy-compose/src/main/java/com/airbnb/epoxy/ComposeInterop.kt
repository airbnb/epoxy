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
    private lateinit var keys: Array<Any>

    override fun buildView(parent: ViewGroup): ComposeView = ComposeView(parent.context)

    override fun bind(view: ComposeView) {
        super.bind(view)
        view.setContent(composeFunction)
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is ComposeEpoxyModel) return false
        if (this.hashCode() != other.hashCode()) return false

        for (i in keys.indices) {
            if (keys[i] != other.keys[i]) return false
        }

        return true
    }

    override fun hashCode(): Int {
        var code = super.hashCode()

        keys.forEach {
            code = 31 * code + it.hashCode()
        }

        return code
    }

    /**
     * This key will contribute to calculate hashcode & equals
     * */
    fun keys(vararg keys: Any) {
        this.keys = arrayOf(*keys)
    }
}

fun ModelCollector.composableInterop(
    id: String,
    vararg keys: Any,
    composeFunction: @Composable () -> Unit
) {
    add(
        ComposeEpoxyModel(composeFunction).apply {
            id(id)
            keys(*keys)
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
