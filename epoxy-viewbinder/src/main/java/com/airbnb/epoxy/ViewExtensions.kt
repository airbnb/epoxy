package com.airbnb.epoxy

import android.content.res.Resources
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.airbnb.epoxy.viewbinder.R

/**
 * Shortcut for getting/setting an [EpoxyViewHolder] on a [View]'s tag.
 */
internal var View.viewHolder: EpoxyViewHolder?
    set(value) = setTag(R.id.epoxy_view_binder, value)
    get() = getTag(R.id.epoxy_view_binder) as EpoxyViewHolder?

/**
 * Finds a view with the resource ID using [ViewGroup.findViewById]. Should a view _not_ be found
 * and the [fallbackToNameLookup] is true, then this recursively iterates through all children and
 * tries to find a match based on [View.idName]. This is useful if using dynamic features. For
 * example, if there is a layout provided by a dynamic feature with an ID for a view, and a view
 * in the base APK that also uses that same view ID, the ID resource will be different but
 * the name will be the same.
 */
@PublishedApi
internal inline fun <reified V : View> View.maybeFindViewByIdName(
    id: Int,
    fallbackToNameLookup: Boolean
): V? =
    findViewById(id) ?: run {
        if (!fallbackToNameLookup || id == -1) return@run null

        try {
            resources?.getResourceEntryName(id)
        } catch (e: Resources.NotFoundException) {
            Log.e(
                "ViewBinderViewExt",
                "Id not found in ${this::class}, fallbackToNameLookup: $fallbackToNameLookup, " +
                    "error message: ${e.localizedMessage}"
            )
            null
        }?.let { idName ->
            findViewByIdName(this, idName)
        }
    }

/**
 * Searches itself and all children, including nested children, for a view that has the [idName] for
 * its [View.idName]. Returns the found view or null if no view is found.
 */
@PublishedApi
internal inline fun <reified V : View> findViewByIdName(view: View, idName: String): V? {
    // The view instance check is not necessary but is done to avoid looking up id name for all
    // views as an optimization
    if (view is V && view.idName == idName) return view

    if (view is ViewGroup) {
        return view.allRecursiveChildren.filterIsInstance<V>()
            .firstOrNull { it.idName == idName }
    }

    return null
}

/**
 * Use [Resources.getResourceEntryName] to get the name of the ID from the resource ID. Returns
 * null if the View's ID is -1 or does not exist.
 */
@PublishedApi
internal val View.idName: String?
    get() = try {
        if (id != -1) resources?.getResourceEntryName(id) else null
    } catch (e: Resources.NotFoundException) {
        null
    }

/**
 * Returns every child view nested at all layers inside this ViewGroup.
 */
@PublishedApi
internal val ViewGroup.allRecursiveChildren: Sequence<View>
    get() {
        return children.flatMap {
            sequenceOf(it) + if (it is ViewGroup) it.allRecursiveChildren else emptySequence()
        }
    }
