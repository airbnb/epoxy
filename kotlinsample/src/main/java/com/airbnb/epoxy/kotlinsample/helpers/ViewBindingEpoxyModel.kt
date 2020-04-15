package com.airbnb.epoxy.kotlinsample.helpers

import android.view.View
import androidx.viewbinding.ViewBinding
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import kotlin.reflect.KFunction1

abstract class ViewBindingEpoxyModel<T : ViewBinding>(
    private val bindFunction: KFunction1<@ParameterName(name = "view") View, T>
) : EpoxyModelWithHolder<ViewBindingHolder>() {

    @Suppress("UNCHECKED_CAST")
    override fun bind(holder: ViewBindingHolder) {
        (holder.viewBinding as T).bind()
    }

    abstract fun T.bind()

    override fun createNewHolder() = ViewBindingHolder(bindFunction)
}

class ViewBindingHolder(private val bindFunction: KFunction1<@ParameterName(name = "view") View, ViewBinding>) : EpoxyHolder() {
    internal lateinit var viewBinding: ViewBinding
    override fun bindView(itemView: View) {
        viewBinding = bindFunction(itemView)
    }
}
