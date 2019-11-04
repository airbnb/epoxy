package com.airbnb.epoxy.preload

import android.view.View

/**
 * Declares Views that should be preloaded. This can either be implemented by a custom view or by an [EpoxyHolder].
 *
 * The preloadable views can be recursive ie if [Preloadable.viewsToPreload] includes any views that are themselves Preloadable those nested
 * views will instead by used.
 */
interface Preloadable {
    val viewsToPreload: List<View>
}
