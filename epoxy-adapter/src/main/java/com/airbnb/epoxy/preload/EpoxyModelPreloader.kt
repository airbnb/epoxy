package com.airbnb.epoxy.preload

import android.view.View
import com.airbnb.epoxy.EpoxyModel

/**
 * Describes how view content for an EpoxyModel should be preloaded.
 *
 * @param T The type of EpoxyModel that this preloader applies to
 * @param U The type of view metadata to provide to the request builder.
 * @param P The type of [PreloadRequestHolder] that will execute the preload request
 */
abstract class EpoxyModelPreloader<T : EpoxyModel<*>, U : ViewMetadata?, P : PreloadRequestHolder>(

    val modelType: Class<T>,

    /**
     * A list of view ids, one for each view that should be preloaded.
     * This should be left empty if the EpoxyModel's type uses the [Preloadable] interface.
     */
    val preloadableViewIds: List<Int>
) {

    /**
     * An optional signature to differentiate views within the same model. This is useful if your EpoxyModel can contain varying amounts of preloadable views,
     * or preloadable views of varying sizes.
     *
     * By default the model's class, span size, and layout resource, are used to differentiate views. This signature allows additional differentiation.
     * For example, if your EpoxyModel shows an preloadable view that varies between portrait or landscape, this orientation will affect the view dimensions.
     * In this case you could return a boolean here to differentiate the two cases so that the preloaded data has the correct orientation.
     *
     * The returned object can be anything, but it must implement [Object.hashCode]
     */
    open fun viewSignature(epoxyModel: T): Any? = null

    /**
     * Provide optional metadata about a view. This can be used in [EpoxyModelPreloader.buildRequest]
     *
     * A preload request works best if it exactly matches the actual request (in order to match cache keys exactly)
     * Things such as request transformations, thumbnails, or crop type can affect the cache key.
     * If your preloadable view is configurable you can capture those options via this metadata.
     */
    abstract fun buildViewMetadata(view: View): U

    /**
     * Start a preload request with the given target.
     *
     * @param epoxyModel The EpoxyModel whose content is being preloaded.
     * @param preloadTarget The target to ues to create and store the request.
     * @param viewData Information about the view that will hold the preloaded content.
     */
    abstract fun startPreload(
        epoxyModel: T,
        preloadTarget: P,
        viewData: ViewData<U>
    )

    companion object {

        /**
         * Helper to create a [EpoxyModelPreloader].
         *
         * @param viewSignature see [EpoxyModelPreloader.viewSignature]
         * @param preloadableViewIds see [EpoxyModelPreloader.preloadableViewIds]
         * @param viewMetadata see [EpoxyModelPreloader.buildViewMetadata]
         * @param doPreload see [EpoxyModelPreloader.startPreload]
         */
        inline fun <reified T : EpoxyModel<*>, P : PreloadRequestHolder> with(
            preloadableViewIds: List<Int> = emptyList(),
            noinline doPreload: (epoxyModel: T, preloadTarget: P, viewData: ViewData<ViewMetadata?>) -> Unit
        ): EpoxyModelPreloader<T, ViewMetadata?, P> =
            with(
                preloadableViewIds,
                viewMetadata = { ViewMetadata.getDefault(it) },
                viewSignature = { null },
                doPreload = doPreload
            )

        /**
         * Helper to create a [EpoxyModelPreloader].
         *
         * @param viewSignature see [EpoxyModelPreloader.viewSignature]
         * @param preloadableViewIds see [EpoxyModelPreloader.preloadableViewIds]
         * @param viewMetadata see [EpoxyModelPreloader.buildViewMetadata]
         * @param doPreload see [EpoxyModelPreloader.startPreload]
         */
        inline fun <reified T : EpoxyModel<*>, U : ViewMetadata?, P : PreloadRequestHolder> with(
            preloadableViewIds: List<Int> = emptyList(),
            noinline viewMetadata: (View) -> U,
            noinline viewSignature: (T) -> Any? = { _ -> null },
            noinline doPreload: (epoxyModel: T, preloadTarget: P, viewData: ViewData<U>) -> Unit
        ): EpoxyModelPreloader<T, U, P> =
            with(
                preloadableViewIds = preloadableViewIds,
                epoxyModelClass = T::class.java,
                viewMetadata = viewMetadata,
                viewSignature = viewSignature,
                doPreload = doPreload
            )

        /**
         * Helper to create a [EpoxyModelPreloader]. This is similar to the other helper methods but not inlined so it can be used with Java.
         *
         * @param epoxyModelClass The specific type of EpoxyModel that this preloader is for.
         * @param viewSignature see [EpoxyModelPreloader.viewSignature]
         * @param preloadableViewIds see [EpoxyModelPreloader.preloadableViewIds]
         * @param viewMetadata see [EpoxyModelPreloader.buildViewMetadata]
         * @param doPreload see [EpoxyModelPreloader.startPreload]
         */
        fun <T : EpoxyModel<*>, U : ViewMetadata?, P : PreloadRequestHolder> with(
            preloadableViewIds: List<Int> = emptyList(),
            epoxyModelClass: Class<T>,
            viewMetadata: (View) -> U,
            viewSignature: (T) -> Any? = { _ -> null },
            doPreload: (epoxyModel: T, preloadTarget: P, viewData: ViewData<U>) -> Unit
        ): EpoxyModelPreloader<T, U, P> = object : EpoxyModelPreloader<T, U, P>(
            modelType = epoxyModelClass,
            preloadableViewIds = preloadableViewIds
        ) {

            override fun buildViewMetadata(view: View) = viewMetadata(view)

            override fun viewSignature(epoxyModel: T) = viewSignature(epoxyModel)

            override fun startPreload(epoxyModel: T, preloadTarget: P, viewData: ViewData<U>) {
                doPreload(epoxyModel, preloadTarget, viewData)
            }
        }
    }
}
