package com.airbnb.epoxy

import android.content.Context
import android.content.pm.ApplicationInfo
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.preload.EpoxyModelPreloader
import com.airbnb.epoxy.preload.EpoxyPreloader
import com.airbnb.epoxy.preload.PreloadErrorHandler
import com.airbnb.epoxy.preload.ViewData
import com.airbnb.epoxy.preload.ViewMetadata
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager

/**
 * Helper to create and add an [EpoxyPreloader] to this RecyclerView that preloads Glide image requests.
 *
 * If you are using [com.airbnb.epoxy.EpoxyRecyclerView], prefer the [addGlidePreloader] for EpoxyRecyclerView instead.
 *
 * @param maxPreloadDistance How many items to prefetch ahead of the last bound item
 * @param errorHandler Called when the preloader encounters an exception. By default this throws only
 * if the app is not in a debuggable model
 * @param preloader Describes how Glide should preload images for the EpoxyModel. Use [glidePreloader]
 * to easily create this.
 */
fun <T : EpoxyModel<*>, U : ViewMetadata?> RecyclerView.addGlidePreloader(
    epoxyController: EpoxyController,
    requestManager: RequestManager,
    maxPreloadDistance: Int = 3,
    errorHandler: PreloadErrorHandler = { context, err -> if (!context.isDebuggable) throw err },
    preloader: EpoxyModelPreloader<T, U, GlidePreloadRequestHolder>
) {
    EpoxyPreloader.with(
        epoxyController = epoxyController,
        requestHolderFactory = { GlidePreloadRequestHolder(requestManager) },
        errorHandler = errorHandler,
        maxItemsToPreload = maxPreloadDistance,
        modelPreloader = preloader
    ).let {
        addOnScrollListener(it)
    }
}

/**
 * Helper to create and add an [EpoxyPreloader] to this RecyclerView that preloads Glide image requests.
 *
 *
 * @param maxPreloadDistance How many items to prefetch ahead of the last bound item
 * @param errorHandler Called when the preloader encounters an exception. By default this throws only
 * if the app is not in a debuggable model
 * @param preloader Describes how Glide should preload images for the EpoxyModel. Use [glidePreloader]
 * to easily create this.
 */
fun <T : EpoxyModel<*>, U : ViewMetadata?> EpoxyRecyclerView.addGlidePreloader(
    requestManager: RequestManager,
    maxPreloadDistance: Int = 3,
    errorHandler: PreloadErrorHandler = { context, err -> if (!context.isDebuggable) throw err },
    preloader: EpoxyModelPreloader<T, U, GlidePreloadRequestHolder>
) {
    addPreloader(
        maxPreloadDistance = maxPreloadDistance,
        errorHandler = errorHandler,
        preloader = preloader,
        requestHolderFactory = { GlidePreloadRequestHolder(requestManager) }
    )
}

/**
 * Creates a [EpoxyModelPreloader] using [GlidePreloadRequestHolder] as the request holder.
 * This allows your Glide preload request to be automatically managed for you.
 *
 * This is similar to the other [glidePreloader], but with default viewMetadata
 */
inline fun <reified T : EpoxyModel<*>> glidePreloader(
    preloadableViewIds: List<Int> = emptyList(),
    noinline viewSignature: (T) -> Any? = { _ -> null },
    noinline buildRequest: (RequestManager, epoxyModel: T, ViewData<ViewMetadata?>) -> RequestBuilder<out Any>
): EpoxyModelPreloader<T, ViewMetadata?, GlidePreloadRequestHolder> {
    return glidePreloader(
        preloadableViewIds,
        { ViewMetadata.getDefault(it) },
        viewSignature,
        buildRequest
    )
}

/**
 * Creates a [EpoxyModelPreloader] using [GlidePreloadRequestHolder] as the request holder.
 * This allows your Glide preload request to be automatically managed for you.
 *
 * @param preloadableViewIds see [EpoxyModelPreloader.preloadableViewIds]
 * @param viewMetadata see [EpoxyModelPreloader.buildViewMetadata]
 * @param viewSignature see [EpoxyModelPreloader.viewSignature]
 * @param buildRequest Create and return a new Glide request to prefetch images for the given model
 * and using the given [RequestManager]
 */
inline fun <reified T : EpoxyModel<*>, U : ViewMetadata?> glidePreloader(
    preloadableViewIds: List<Int> = emptyList(),
    noinline viewMetadata: (View) -> U,
    noinline viewSignature: (T) -> Any? = { _ -> null },
    noinline buildRequest: (RequestManager, epoxyModel: T, ViewData<U>) -> RequestBuilder<out Any>
): EpoxyModelPreloader<T, U, GlidePreloadRequestHolder> {
    return EpoxyModelPreloader.with(
        preloadableViewIds = preloadableViewIds,
        viewMetadata = viewMetadata,
        viewSignature = viewSignature,
        doPreload = { model: T, target: GlidePreloadRequestHolder, viewData ->
            target.startRequest(viewData) { requestManager ->
                buildRequest(requestManager, model, viewData)
            }
        }
    )
}

internal val Context.isDebuggable: Boolean get() = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
