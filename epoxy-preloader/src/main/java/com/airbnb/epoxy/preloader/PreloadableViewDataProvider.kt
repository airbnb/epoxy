package com.airbnb.epoxy.preloader

import android.support.v4.view.ViewCompat
import android.view.View
import com.airbnb.epoxy.BaseEpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import kotlin.reflect.KClass

/**
 * In order to preload images we need to know the size of the view that they will be loaded into.
 * This class provides the view size, as well as other view metadata that might be necessary to construct the image request.
 */
internal class PreloadableViewDataProvider(
        val adapter: BaseEpoxyAdapter,
        val errorHandler: PreloadErrorHandler
) {

    /**
     * A given model class might have different sized images depending on configuration. We use this cache key to separate view configurations.
     */
    private data class CacheKey(
            val epoxyModelClass: KClass<out EpoxyModel<*>>,
            val spanSize: Int,
            val viewType: Int,
            /** An optional, custom signature provided by the model preloader. This allows the user to specify custom cache mixins */
            val signature: Any?
    )

    private val cache = mutableMapOf<CacheKey, List<ViewData<*>>?>()

    /** @return A list containing the data necessary to load each view in the given model. */
    fun <T : EpoxyModel<*>, U> dataForModel(
            preloader: EpoxyModelPreloader<T, U>,
            epoxyModel: T,
            position: Int
    ): List<ViewData<U>> {
        val cacheKey = cacheKey(preloader, epoxyModel, position)

        @Suppress("UNCHECKED_CAST")
        return cache.getOrPut(cacheKey) {
            // Look up view data based on currently bound views. This can be null if a matching view type is not found.
            // In that case we save the null so we know to try the lookup again next time.
            findViewData(preloader, epoxyModel, cacheKey)
        } as? List<ViewData<U>> ?: return emptyList()
    }

    private fun <T : EpoxyModel<*>> cacheKey(
            preloader: EpoxyModelPreloader<T, *>,
            epoxyModel: T,
            position: Int
    ): CacheKey {
        val modelSpanSize = if (adapter.isMultiSpan) {
            epoxyModel.getSpanSizeExternal(adapter.spanCount, position, adapter.itemCount)
        } else {
            1
        }

        return CacheKey(epoxyModel::class, modelSpanSize, epoxyModel.viewTypePublic, preloader.viewSignature(epoxyModel))
    }

    private fun <T : EpoxyModel<*>, U> findViewData(preloader: EpoxyModelPreloader<T, U>, epoxyModel: T, cacheKey: CacheKey): List<ViewData<U>>? {
        // It is a bit tricky to get details on the view to be preloaded, since the view doesn't necessarily exist at the time of preload.
        // This approach looks at currently bound views and tries to get one who's cache key is the same as what we need.
        // This should mostly work, since RecyclerViews generally the same type of views shown repeatedly.
        // If a model is only shown sporadically we may never be able to get data about it with this approach, which we could address in the future.

        val holderMatch = adapter.boundViewHoldersPublic.find {
            val boundModel = it.model
            if (boundModel::class == epoxyModel::class) {
                @Suppress("UNCHECKED_CAST")
                // We need the view sizes, but viewholders can be bound without actually being laid out on screen yet
                ViewCompat.isAttachedToWindow(it.itemView)
                        && ViewCompat.isLaidOut(it.itemView)
                        && cacheKey(preloader, boundModel as T, it.adapterPosition) == cacheKey
            } else {
                false
            }
        }

        val rootView = holderMatch?.itemView ?: return null

        val boundObject = holderMatch.objectToBindPublic() // Allows usage of view holder models

        val imageViews: List<View> = when {
            preloader.imageViewIds.isNotEmpty() -> rootView.findViews(preloader.imageViewIds, epoxyModel)
            rootView is Preloadable             -> rootView.imageViewsToPreload
            boundObject is Preloadable          -> boundObject.imageViewsToPreload
            else                                -> emptyList()
        }

        if (imageViews.isEmpty()) {
            errorHandler(GlidePreloadException("No preloadable views were found in ${epoxyModel::class.simpleName}"))
        }

        return imageViews
                .flatMap { it.recursePreloadableViews() }
                .mapNotNull { it.buildData(preloader, epoxyModel) }
    }

    /** Returns child views with the given view ids. */
    private fun <T : EpoxyModel<*>> View.findViews(
            viewIds: List<Int>,
            epoxyModel: T
    ): List<View> {
        return viewIds.mapNotNull { id ->
            findViewById<View>(id).apply {
                if (this == null) errorHandler(GlidePreloadException("View with id $id in ${epoxyModel::class.simpleName} could not be found."))
            }
        }
    }

    /** If a View with the [Preloadable] interface is used we want to get all of the image views contained in that Preloadable as well. */
    private fun <T : View> T.recursePreloadableViews(): List<View> {
        return if (this is Preloadable) imageViewsToPreload.flatMap { it.recursePreloadableViews() } else listOf(this)
    }

    private fun <T : EpoxyModel<*>, U> View.buildData(
            preloader: EpoxyModelPreloader<T, U>,
            epoxyModel: T
    ): ViewData<U>? {

        // Glide's internal size determiner takes view dimensions and subtracts padding to get target size.
        // TODO: We could support size overrides by allowing the preloader to specify a size override callback
        val width = width - paddingLeft - paddingRight
        val height = height - paddingTop - paddingBottom

        if (width <= 0 || height <= 0) {
            // If no placeholder or aspect ratio is used then the view might be empty before an image loads
            errorHandler(GlidePreloadException("${this::class.simpleName} in ${epoxyModel::class.simpleName} has zero size. A size must be set to allow preloading an image."))
            return null
        }

        return ViewData(id, width, height, preloader.buildViewMetadata(this))
    }
}