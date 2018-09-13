package com.airbnb.epoxy

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.CompoundButton

// todo only apply DONotHash in callback prop if equals/hashcode does not exist on type?
// Big refactor to remove DoNotHash completely and enforce keyed listeners everywhere?
// Only apply if a build flag is passed?

// Generate a kotlin extension function on builder interfaces for easily passing keyed listeners?
//inline fun CarouselModelBuilder.onClickListener(crossinline onClick: ClickContext<View, Carousel, CarouselModel_>.() -> Unit) {
//    onClickListener((this as CarouselModel_).keyedClickListener(onClick))
//}

// Generate keyed subclass of any type annotated with callback prop?

inline fun <reified V : View, reified P : Any, reified T : EpoxyModel<P>> T.clickContext(
    clickedView: V
): ClickContext<V, P, T>? = ClickContext.from(clickedView)

inline fun <reified V : Any, reified T : EpoxyModel<V>, Listener> T.keyedListener(
    key: Any = this,
    listener: Listener
) = KeyedListener(key, listener)

inline fun <reified V : Any, reified T : EpoxyModel<V>> T.keyedLongClickListener(
    crossinline listener: ClickContext<View, V, T>.() -> Boolean
) = KeyedLongClickListener(this, View.OnLongClickListener { v ->
    val context = ClickContext.from<View, V, T>(v) ?: return@OnLongClickListener false
    return@OnLongClickListener context.listener()
})

class KeyedLongClickListener(
    key: Any,
    listener: View.OnLongClickListener
) : KeyedListener<View.OnLongClickListener>(key, listener), View.OnLongClickListener {

    override fun onLongClick(v: View) = listener.onLongClick(v)
}

inline fun <reified V : Any, reified T : EpoxyModel<V>> T.keyedCheckedChangeListener(
    crossinline listener: ClickContext<CompoundButton, V, T>.(isChecked: Boolean) -> Unit
) = KeyedCheckedChangedListener(this, CompoundButton.OnCheckedChangeListener { v, isChecked ->
    ClickContext.from<CompoundButton, V, T>(v)?.apply {
        listener(isChecked)
    }
})

class KeyedCheckedChangedListener(
    key: Any,
    listener: CompoundButton.OnCheckedChangeListener
) : KeyedListener<CompoundButton.OnCheckedChangeListener>(key, listener),
    CompoundButton.OnCheckedChangeListener {

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        listener.onCheckedChanged(buttonView, isChecked)
    }
}

inline fun <reified V : Any, reified T : EpoxyModel<V>> T.keyedClickListener(
    crossinline listener: ClickContext<View, V, T>.() -> Unit
) = KeyedClickListener(this, View.OnClickListener { v ->
    ClickContext.from<View, V, T>(v)?.apply(listener)
})

class KeyedClickListener(
    key: Any,
    listener: View.OnClickListener
) : KeyedListener<View.OnClickListener>(key, listener), View.OnClickListener {

    override fun onClick(v: View) = listener.onClick(v)
}

open class KeyedListener<Listener>(private val key: Any, val listener: Listener) {

    // Only include the key, and not the listener, in equals/hashcode
    override fun equals(other: Any?): Boolean {
        val model = key as? EpoxyModel<*>
        if (model?.inKeyedListenerEquals == true) return true
        model?.inKeyedListenerEquals = true


        if (this === other) return true
        if (other !is KeyedListener<*>) return false
        val isEqual = key == other.key

        model?.inKeyedListenerEquals = false
        return isEqual
    }

    override fun hashCode(): Int {
        val model = key as? EpoxyModel<*>
        if (model?.inKeyedListenerEquals == true) return 0
        model?.inKeyedListenerEquals = true

        val result = key.hashCode()

        model?.inKeyedListenerEquals = false
        return result
    }
}

data class ClickContext<V : View, P : Any, T : EpoxyModel<P>>(
    val model: T,
    val parentView: P,
    val clickedView: V,
    val position: Int
) {

    companion object {
        inline fun <reified V : View, reified P : Any, reified T : EpoxyModel<P>> from(clickedView: V): ClickContext<V, P, T>? {
            val epoxyHolder = getEpoxyHolderForChildView(clickedView)
                ?: throw IllegalStateException("Could not find RecyclerView holder for clicked view ${V::class.java.simpleName}")

            val adapterPosition = epoxyHolder.adapterPosition
            if (adapterPosition == RecyclerView.NO_POSITION) {
                // View is being removed, ignore the click.
                return null
            }

            val model = epoxyHolder.model as? T ?: run {
                Log.e(
                    "Epoxy",
                    "Failed to cast ${epoxyHolder.model::class.java.simpleName} to model ${T::class.java.simpleName}"
                )
                return null
            }

            val parentView = epoxyHolder.objectToBind() as? P ?: run {
                Log.e(
                    "Epoxy",
                    "Failed to cast ${epoxyHolder.objectToBind()::class.java.simpleName} to view ${V::class.java.simpleName}"
                )
                return null
            }

            return ClickContext(
                model,
                parentView,
                clickedView,
                adapterPosition
            )
        }
    }
}

fun getEpoxyHolderForChildView(v: View): EpoxyViewHolder? {
    val recyclerView = findParentRecyclerView(v) ?: return null
    val viewHolder = recyclerView.findContainingViewHolder(v) ?: return null
    return viewHolder as? EpoxyViewHolder
}

fun findParentRecyclerView(v: View?): RecyclerView? {
    val parent = v?.parent ?: return null

    return when (parent) {
        is RecyclerView -> parent
        is View -> findParentRecyclerView(parent)
        else -> null
    }
}