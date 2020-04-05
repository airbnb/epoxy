package com.airbnb.epoxy.kotlinsample.helpers

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.airbnb.epoxy.EpoxyModel
import kotlin.reflect.KFunction1

abstract class KotlinViewBindingModel<T : ViewBinding>(
    @LayoutRes private val layoutRes: Int,
    private val bindFunction: KFunction1<@ParameterName(name = "view") View, T>
) : EpoxyModel<View>() {

    private var _binding: T? = null

    protected val binding: T
        get() = _binding ?: error("Accessing not bound ViewBinding.")

    abstract fun bind()

    override fun bind(view: View) {
        _binding = bindFunction(view)
        bind()
    }

    override fun buildView(parent: ViewGroup): View {
        return super.buildView(parent)
    }

    override fun unbind(view: View) {
        _binding = null
        super.unbind(view)
    }

    override fun getDefaultLayout() = layoutRes
}
