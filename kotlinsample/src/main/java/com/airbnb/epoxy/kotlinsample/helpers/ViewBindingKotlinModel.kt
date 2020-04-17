package com.airbnb.epoxy.kotlinsample.helpers

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.kotlinsample.R
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.util.concurrent.ConcurrentHashMap
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A pattern for using epoxy models with Kotlin with no annotations or code generation.
 *
 * See [com.airbnb.epoxy.kotlinsample.models.ItemDataClass] for a usage example.
 */
abstract class ViewBindingKotlinModel<T : ViewBinding>(
    @LayoutRes private val layoutRes: Int
) : EpoxyModel<View>() {
    // Using reflection to get the static binding method.
    // Lazy so it's computed only once by instance, when the 1st ViewHolder is actually created.
    private val actualTypeOfThis by lazy { this::class.java.genericSuperclass as ParameterizedType }
    private val kClass by lazy { (actualTypeOfThis.actualTypeArguments[0] as Class<ViewBinding>) }
    private val bindingMethod by lazy { getBindMethod(kClass)!! }


    abstract fun T.bind()

    override fun bind(view: View) {
        var binding = view.getTag(R.id.epoxy_viewbinding) as? T
        if (binding == null) {
            binding = bindingMethod.invoke(null, view) as T
            view.setTag(R.id.epoxy_viewbinding, binding)
        }
        binding.bind()
    }

    override fun getDefaultLayout() = layoutRes
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
