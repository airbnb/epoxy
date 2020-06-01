package com.airbnb.epoxy.preload

import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.utils.isDebuggable

/**
 * Helper to create and add an [EpoxyPreloader] to this RecyclerView.
 *
 * If you are using [com.airbnb.epoxy.EpoxyRecyclerView], prefer[com.airbnb.epoxy.EpoxyRecyclerView.addPreloader]
 * instead.
 *
 * @param maxPreloadDistance How many items to prefetch ahead of the last bound item
 * @param errorHandler Called when the preloader encounters an exception. By default this throws only
 * if the app is not in a debuggle model
 * @param preloader Describes how view content for the EpoxyModel should be preloaded
 * @param requestHolderFactory Should create and return a new [PreloadRequestHolder] each time it is invoked
 */
fun <T : EpoxyModel<*>, U : ViewMetadata?, P : PreloadRequestHolder> RecyclerView.addEpoxyPreloader(
    epoxyController: EpoxyController,
    maxPreloadDistance: Int = 3,
    errorHandler: PreloadErrorHandler = { context, err -> if (!context.isDebuggable) throw err },
    preloader: EpoxyModelPreloader<T, U, P>,
    requestHolderFactory: () -> P
) {
    EpoxyPreloader.with(
        epoxyController,
        requestHolderFactory,
        errorHandler,
        maxPreloadDistance,
        preloader
    ).let {
        addOnScrollListener(it)
    }
}
