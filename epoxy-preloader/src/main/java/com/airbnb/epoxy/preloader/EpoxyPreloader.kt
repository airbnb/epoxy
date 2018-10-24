package com.airbnb.epoxy.preloader

import android.content.Context
import android.graphics.Bitmap
import android.support.annotation.IdRes
import android.support.annotation.Px
import android.support.v7.widget.RecyclerView
import android.view.View
import com.airbnb.epoxy.BaseEpoxyAdapter
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.Target

/**
 * Used to create a scroll listener that prefetches images with Glide.
 *
 * To use this, create implementations of [EpoxyModelPreloader] for each EpoxyModel class that you want to preload.
 * Then, use the [EpoxyPreloader.with] methods to create a ScrollListener that preloads models of that type.
 * Finally, add the resulting scroll listener to your RecyclerView.
 */
class EpoxyPreloader private constructor(
        private val adapter: BaseEpoxyAdapter,
        private val requestManager: RequestManager,
        errorHandler: PreloadErrorHandler,
        maxItemsToPreload: Int,
        vararg models: EpoxyModelPreloader<*, *>
) : RecyclerView.OnScrollListener() {

    constructor(
            epoxyController: EpoxyController,
            requestManager: RequestManager,
            errorHandler: PreloadErrorHandler,
            maxItemsToPreload: Int,
            vararg models: EpoxyModelPreloader<*, *>
    ) : this(epoxyController.adapter, requestManager, errorHandler, maxItemsToPreload, *models)

    constructor(
            adapter: EpoxyAdapter,
            requestManager: RequestManager,
            errorHandler: PreloadErrorHandler,
            maxItemsToPreload: Int,
            vararg models: EpoxyModelPreloader<*, *>
    ) : this(adapter as BaseEpoxyAdapter, requestManager, errorHandler, maxItemsToPreload, *models)

    private val modelPreloaders: Map<Class<out EpoxyModel<*>>, EpoxyModelPreloader<*, *>> = models.associateBy { it.modelType }
    private val viewDataCache = PreloadableViewDataProvider(adapter, errorHandler)

    private val scrollListener = PreloadingScrollListener(this, requestManager, maxItemsToPreload)

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) = scrollListener.onScrolled(recyclerView, dx, dy)

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) = scrollListener.onScrollStateChanged(recyclerView, newState)

    fun getPreloadItems(position: Int): List<EpoxyModelImageData<*, *>> {
        @Suppress("UNCHECKED_CAST")
        val epoxyModel = adapter.getModelForPositionPublic(position) as? EpoxyModel<Any> ?: return emptyList()

        @Suppress("UNCHECKED_CAST")
        val preloader = modelPreloaders[epoxyModel::class.java] as? EpoxyModelPreloader<EpoxyModel<Any>, Any?> ?: return emptyList()

        return viewDataCache
                .dataForModel(preloader, epoxyModel, position)
                .map { EpoxyModelImageData(preloader, epoxyModel, it) }
    }

    fun getPreloadRequestBuilder(modelData: EpoxyModelImageData<*, *>) = modelData.buildRequest(requestManager)

    fun cancelImageLoads() = scrollListener.cancelAll()

    companion object {

        /** Helper to create a preload scroll listener. Add the result to your RecyclerView. */
        fun with(
                context: Context,
                epoxyController: EpoxyController,
                errorHandler: PreloadErrorHandler,
                maxItemsToPreload: Int,
                vararg models: EpoxyModelPreloader<*, *>
        ): EpoxyPreloader {

            val requestManager = Glide.with(context)
            return EpoxyPreloader(epoxyController, requestManager, errorHandler, maxItemsToPreload, *models)
        }

        /** Helper to create a preload scroll listener. Add the result to your RecyclerView. */
        fun with(
                context: Context,
                epoxyAdapter: EpoxyAdapter,
                errorHandler: PreloadErrorHandler,
                maxItemsToPreload: Int,
                vararg models: EpoxyModelPreloader<*, *>
        ): EpoxyPreloader {

            val requestManager = Glide.with(context)
            return EpoxyPreloader(epoxyAdapter, requestManager, errorHandler, maxItemsToPreload, *models)
        }

    }
}

class GlidePreloadException(errorMessage: String) : RuntimeException(errorMessage)

typealias PreloadErrorHandler = (RuntimeException) -> Unit

/**
 * Declares ImageViews that should be preloaded. This can either be implemented by a custom view or by an [EpoxyHolder].
 *
 * The preloadable views can be recursive ie if [Preloadable.imageViewsToPreload] includes any views that are themselves Preloadable those nested
 * views will instead by used.
 *
 */
interface Preloadable {
    val imageViewsToPreload: List<View>
}

/**
 * Data about an image view to be preloaded. This data is used to construct a Glide image request.
 *
 * @param metadata Any custom, additional data that the [EpoxyModelPreloader] chooses to provide that may be necessary to create the image request.
 */
class ViewData<out U>(@IdRes val viewId: Int, @Px val width: Int, @Px val height: Int, val metadata: U)

/**
 * Describes how images for an EpoxyModel should be preloaded.
 *
 * @param T The type of EpoxyModel that this preloader applies to
 * @param U The type of metadata to provide to the request builder. Can be Unit if no extra data is needed.
 */
interface EpoxyModelPreloader<T : EpoxyModel<*>, U> {

    val modelType: Class<T>

    /**
     * A list of view ids, one for each image view that should be preloaded.
     * This should be left empty if the EpoxyModel's type uses the [Preloadable] interface.
     */
    val imageViewIds: List<Int>
        get() = emptyList()

    /**
     * An optional signature to differentiate views with the same model. This is useful if your EpoxyModel can contain varying amounts of image views,
     * or image views of varying sizes.
     *
     * By default the model's class, span size, and layout resource, are used to differentiate views. This signature allows additional differentiation.
     * For example, if your EpoxyModel shows an image that is either portrait of landscape, this orientation will affect the view dimensions.
     * In this case you could return a boolean here to differentiate the two cases so that the preloaded image has the correct orientation.
     *
     * The returned object can be anything, but it must implement [Object.hashCode]
     */
    @Suppress("Detekt.FunctionOnlyReturningConstant")
    fun viewSignature(epoxyModel: T): Any? = null

    /**
     * Provide optional metadata about a view. This can be used in [EpoxyModelPreloader.buildRequest]
     *
     * A preload request works best if it exactly matches the actual image request (in order to match Glide cache keys)
     * Things such as request transformations, thumbnails, or crop type can affect the cache key.
     * If your ImageView is configurable you can capture those options via this metadata.
     */
    fun buildViewMetadata(view: View): U

    /**
     * Create and return a new Glide [RequestBuilder] to request an image for the given model and view.
     *
     * @param epoxyModel The EpoxyModel whose image is being preloaded.
     * @param viewData Information about the view that will hold the image.
     */
    fun buildRequest(
            requestManager: RequestManager,
            epoxyModel: T,
            viewData: ViewData<U>
    ): RequestBuilder<*>

    companion object {

        /**
         * Helper to create a [EpoxyModelPreloader].
         *
         * @param imageViewIds see [EpoxyModelPreloader.imageViewIds].
         * @param requestBuilder see [EpoxyModelPreloader.buildRequest].
         */
        inline fun <reified T : EpoxyModel<*>> with(
                vararg imageViewIds: Int,
                crossinline requestBuilder: (requestManager: RequestManager, epoxyModel: T, viewData: ViewData<Unit>) -> RequestBuilder<*>?
        ): EpoxyModelPreloader<T, Unit> = with(*imageViewIds, viewMetadata = { _ -> }, viewSignature = { _ -> null }, requestBuilder = requestBuilder)

        /**
         * Helper to create a [EpoxyModelPreloader].
         *
         * @param viewSignature see [EpoxyModelPreloader.viewSignature]
         * @param imageViewIds see [EpoxyModelPreloader.imageViewIds]
         * @param viewMetadata see [EpoxyModelPreloader.buildViewMetadata]
         * @param requestBuilder see [EpoxyModelPreloader.buildRequest]
         */
        inline fun <reified T : EpoxyModel<*>, U> with(
                vararg imageViewIds: Int,
                crossinline viewMetadata: (View) -> U,
                crossinline viewSignature: (T) -> Any? = { _ -> null },
                crossinline requestBuilder: (requestManager: RequestManager, epoxyModel: T, viewData: ViewData<U>) -> RequestBuilder<*>?
        ): EpoxyModelPreloader<T, U> = object : EpoxyModelPreloader<T, U> {

            override val modelType = T::class.java

            override val imageViewIds = imageViewIds.asList()

            override fun buildViewMetadata(view: View) = viewMetadata(view)

            override fun viewSignature(epoxyModel: T) = viewSignature(epoxyModel)

            override fun buildRequest(
                    requestManager: RequestManager,
                    epoxyModel: T,
                    viewData: ViewData<U>
            ): RequestBuilder<*> {
                return requestBuilder(requestManager, epoxyModel, viewData) ?: NoOpRequestBuilder(requestManager)
            }

        }

        /**
         * Helper to create a [EpoxyModelPreloader]. This is similar to the other helper methods but not inlined so it can be used with Java.
         *
         * @param epoxyModelClass The specific type of EpoxyModel that this preloader is for.
         * @param viewSignature see [EpoxyModelPreloader.viewSignature]
         * @param imageViewIds see [EpoxyModelPreloader.imageViewIds]
         * @param viewMetadata see [EpoxyModelPreloader.buildViewMetadata]
         * @param requestBuilder see [EpoxyModelPreloader.buildRequest]
         */
        fun <T : EpoxyModel<*>, U> with(
                vararg imageViewIds: Int,
                epoxyModelClass: Class<T>,
                viewMetadata: (View) -> U,
                viewSignature: (T) -> Any? = { _ -> null },
                requestBuilder: (requestManager: RequestManager, epoxyModel: T, viewData: ViewData<U>) -> RequestBuilder<*>?
        ): EpoxyModelPreloader<T, U> = object : EpoxyModelPreloader<T, U> {

            override val modelType = epoxyModelClass

            override val imageViewIds = imageViewIds.asList()

            override fun buildViewMetadata(view: View) = viewMetadata(view)

            override fun viewSignature(epoxyModel: T) = viewSignature(epoxyModel)

            override fun buildRequest(
                    requestManager: RequestManager,
                    epoxyModel: T,
                    viewData: ViewData<U>
            ): RequestBuilder<*> {
                return requestBuilder(requestManager, epoxyModel, viewData) ?: NoOpRequestBuilder(requestManager)
            }

        }
    }
}

class NoOpRequestBuilder(requestManager: RequestManager) : RequestBuilder<Bitmap>(Bitmap::class.java, requestManager.asBitmap()) {

    override fun <Y : Target<Bitmap>?> into(target: Y) = target

}

class EpoxyModelImageData<T : EpoxyModel<*>, U>(
        val preloader: EpoxyModelPreloader<T, U>,
        val epoxyModel: T,
        val viewData: ViewData<U>
) {
    fun buildRequest(requestManager: RequestManager) = preloader.buildRequest(
            requestManager = requestManager,
            epoxyModel = epoxyModel,
            viewData = viewData
    )
}