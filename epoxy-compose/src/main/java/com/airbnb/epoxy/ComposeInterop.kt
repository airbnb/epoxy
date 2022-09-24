package com.airbnb.epoxy

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView

/**
 * An epoxy viewModel that can inflate a Composable function
 * The keys parameter is responsible for recomposition of the composable function in epoxy.
 * Make sure the class of the object passed in as keys, have equals implemented, that is
 * their equality can be checked.
 *
 * However if your composeFunction relies on mutableState to describe your UI,
 * then passing in key as parameter is not needed.
 *
 * @param keys              variable number of arguments that are responsible for
 * @param composeFunction   The composable function to display in epoxy
 */
class ComposeEpoxyModel(
    vararg val keys: Any,
    private val composeFunction: @Composable () -> Unit,
) : EpoxyModelWithView<ComposeView>() {

    private val keyedTags by lazy { SparseArray<Any>(2) }

    /**
     * add tag to this epoxy model
     */
    fun addTag(key: Int, tag: Any) {
        keyedTags.put(key, tag)
    }

    fun tag(key: Int): Any? {
        return keyedTags.get(key)
    }

    override fun buildView(parent: ViewGroup): ComposeView = ComposeView(parent.context)

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
}

fun ModelCollector.composableInterop(
    id: String,
    vararg keys: Any,
    composeFunction: @Composable () -> Unit
) {
    add(composeEpoxyModel(id, *keys, composeFunction = composeFunction))
}

/**
 * [composeEpoxyModel] can be used directly in cases where more control over the epoxy model
 * is needed. Eg. When the epoxy model needs to be modified before it's added.
 */
fun composeEpoxyModel(
    id: String,
    vararg keys: Any,
    composeFunction: @Composable () -> Unit
): ComposeEpoxyModel {
    return ComposeEpoxyModel(*keys, composeFunction = composeFunction).apply {
        id(id)
    }
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
        val modelView = view.getChildAt(0)
        (model as EpoxyModel<View>).bind(modelView)
        (model as GeneratedModel<View>)?.handlePostBind(modelView, 0)
    }
}
