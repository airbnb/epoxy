package com.airbnb.epoxy.kotlinsample.helpers

import android.view.View
import androidx.viewbinding.ViewBinding
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.util.concurrent.ConcurrentHashMap

abstract class ViewBindingEpoxyModel<in T : ViewBinding> : EpoxyModelWithHolder<ViewBindingHolder>() {
    // Using reflection to get the static binding method.
    // Lazy so it's computed only once by instance, when the 1st ViewHolder is actually created.
    private val actualTypeOfThis by lazy { (this::class.java.genericSuperclass as Class<*>).genericSuperclass as ParameterizedType }
    private val kClass by lazy { (actualTypeOfThis.actualTypeArguments[0] as Class<ViewBinding>) }
    private val bindingMethod by lazy { getBindMethod(kClass) }

    @Suppress("UNCHECKED_CAST")
    override fun bind(holder: ViewBindingHolder) {
        (holder.viewBinding as T).bind()
    }

    abstract fun T.bind()

    override fun createNewHolder(): ViewBindingHolder {
        return ViewBindingHolder(bindingMethod)
    }
}

// Static cache of a method pointer for each type of item used.
private val sBindingMethodByClass = ConcurrentHashMap<Class<out ViewBinding>, Method>()

private fun getBindMethod(javaClass: Class<out ViewBinding>): Method? {
    var method: Method? = sBindingMethodByClass[javaClass]
    if (method == null) {
        // Generated bind method is static and accept only one parameter of type View.
        method = javaClass.getDeclaredMethod("bind", View::class.java)
        if (method != null) {
            sBindingMethodByClass[javaClass] = method
        }
    }
    return method
}

class ViewBindingHolder(private val bindFunction: Method?) : EpoxyHolder() {
    internal lateinit var viewBinding: ViewBinding
    override fun bindView(itemView: View) {
        // The 1st param is null because the binding method is static.
        viewBinding = bindFunction?.invoke(null, itemView) as ViewBinding
    }
}
