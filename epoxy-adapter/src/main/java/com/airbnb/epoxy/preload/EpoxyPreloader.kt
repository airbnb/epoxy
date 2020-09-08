package com.airbnb.epoxy.preload

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.annotation.Px
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.BaseEpoxyAdapter
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.getModelForPositionInternal
import kotlin.math.max
import kotlin.math.min

/**
 * A scroll listener that prefetches view content.
 *
 * To use this, create implementations of [EpoxyModelPreloader] for each EpoxyModel class that you want to preload.
 * Then, use the [EpoxyPreloader.with] methods to create an instance that preloads models of that type.
 * Finally, add the resulting scroll listener to your RecyclerView.
 *
 * If you are using [com.airbnb.epoxy.EpoxyRecyclerView] then use [com.airbnb.epoxy.EpoxyRecyclerView.addPreloader]
 * to setup the preloader as a listener.
 *
 * Otherwise there is a [RecyclerView.addEpoxyPreloader] extension for easy usage.
 */
class EpoxyPreloader<P : PreloadRequestHolder> private constructor(
    private val adapter: BaseEpoxyAdapter,
    preloadTargetFactory: () -> P,
    errorHandler: PreloadErrorHandler,
    private val maxItemsToPreload: Int,
    modelPreloaders: List<EpoxyModelPreloader<*, *, out P>>
) : RecyclerView.OnScrollListener() {

    private var lastVisibleRange: IntRange = IntRange.EMPTY
    private var lastPreloadRange: IntProgression = IntRange.EMPTY
    private var totalItemCount = -1
    private var scrollState: Int = RecyclerView.SCROLL_STATE_IDLE

    private val modelPreloaders: Map<Class<out EpoxyModel<*>>, EpoxyModelPreloader<*, *, out P>> =
        modelPreloaders.associateBy { it.modelType }

    private val requestHolderFactory =
        PreloadTargetProvider(maxItemsToPreload, preloadTargetFactory)

    private val viewDataCache = PreloadableViewDataProvider(adapter, errorHandler)

    constructor(
        epoxyController: EpoxyController,
        requestHolderFactory: () -> P,
        errorHandler: PreloadErrorHandler,
        maxItemsToPreload: Int,
        modelPreloaders: List<EpoxyModelPreloader<*, *, out P>>
    ) : this(
        epoxyController.adapter,
        requestHolderFactory,
        errorHandler,
        maxItemsToPreload,
        modelPreloaders
    )

    constructor(
        adapter: EpoxyAdapter,
        requestHolderFactory: () -> P,
        errorHandler: PreloadErrorHandler,
        maxItemsToPreload: Int,
        modelPreloaders: List<EpoxyModelPreloader<*, *, out P>>
    ) : this(
        adapter as BaseEpoxyAdapter,
        requestHolderFactory,
        errorHandler,
        maxItemsToPreload,
        modelPreloaders
    )

    init {
        require(maxItemsToPreload > 0) {
            "maxItemsToPreload must be greater than 0. Was $maxItemsToPreload"
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        scrollState = newState
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dx == 0 && dy == 0) {
            // Sometimes flings register a bunch of 0 dx/dy scroll events. To avoid redundant prefetching we just skip these
            // Additionally, the first RecyclerView layout notifies a scroll of 0, since that can be an important time for
            // performance (eg page load) we avoid prefetching at the same time.
            return
        }

        if (dx.isFling() || dy.isFling()) {
            // We avoid preloading during flings for two reasons
            // 1. Image requests are expensive and we don't want to drop frames on fling
            // 2. We'll likely scroll past the preloading item anyway
            return
        }

        // Update item count before anything else because validations depend on it
        totalItemCount = recyclerView.adapter?.itemCount ?: 0

        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

        if (firstVisiblePosition.isInvalid() || lastVisiblePosition.isInvalid()) {
            lastVisibleRange = IntRange.EMPTY
            lastPreloadRange = IntRange.EMPTY
            return
        }

        val visibleRange = IntRange(firstVisiblePosition, lastVisiblePosition)
        if (visibleRange == lastVisibleRange) {
            return
        }

        val isIncreasing =
            visibleRange.first > lastVisibleRange.first || visibleRange.last > lastVisibleRange.last

        val preloadRange =
            calculatePreloadRange(firstVisiblePosition, lastVisiblePosition, isIncreasing)

        // Start preload for any items that weren't already preloaded
        preloadRange
            .subtract(lastPreloadRange)
            .forEach { preloadAdapterPosition(it) }

        lastVisibleRange = visibleRange
        lastPreloadRange = preloadRange
    }

    /**
     * @receiver The number of pixels scrolled.
     * @return True if this distance is large enough to be considered a fast fling.
     */
    private fun Int.isFling() = Math.abs(this) > FLING_THRESHOLD_PX

    private fun calculatePreloadRange(
        firstVisiblePosition: Int,
        lastVisiblePosition: Int,
        isIncreasing: Boolean
    ): IntProgression {
        val from = if (isIncreasing) lastVisiblePosition + 1 else firstVisiblePosition - 1
        val to = from + if (isIncreasing) maxItemsToPreload - 1 else 1 - maxItemsToPreload

        return IntProgression.fromClosedRange(
            rangeStart = from.clampToAdapterRange(),
            rangeEnd = to.clampToAdapterRange(),
            step = if (isIncreasing) 1 else -1
        )
    }

    /** Check if an item index is valid. It may not be if the adapter is empty, or if adapter changes have been dispatched since the last layout pass. */
    private fun Int.isInvalid() = this == RecyclerView.NO_POSITION || this >= totalItemCount

    private fun Int.clampToAdapterRange() = min(totalItemCount - 1, max(this, 0))

    private fun preloadAdapterPosition(position: Int) {
        @Suppress("UNCHECKED_CAST")
        val epoxyModel = adapter.getModelForPositionInternal(position) as? EpoxyModel<Any>
            ?: return

        @Suppress("UNCHECKED_CAST")
        val preloader =
            modelPreloaders[epoxyModel::class.java] as? EpoxyModelPreloader<EpoxyModel<*>, ViewMetadata?, P>
                ?: return

        viewDataCache
            .dataForModel(preloader, epoxyModel, position)
            .forEach { viewData ->
                val preloadTarget = requestHolderFactory.next()
                preloader.startPreload(epoxyModel, preloadTarget, viewData)
            }
    }

    /**
     * Cancels all current preload requests in progress.
     */
    fun cancelPreloadRequests() {
        requestHolderFactory.clearAll()
    }

    companion object {

        /**
         *
         * Represents a threshold for fast scrolling.
         * This is a bit arbitrary and was determined by looking at values while flinging vs slow scrolling.
         * Ideally it would be based on DP, but this is simpler.
         */
        private const val FLING_THRESHOLD_PX = 75

        /**
         * Helper to create a preload scroll listener. Add the result to your RecyclerView.
         * for different models or content types.
         *
         * @param maxItemsToPreload How many items to prefetch ahead of the last bound item
         * @param errorHandler Called when the preloader encounters an exception. By default this throws only
         * if the app is not in a debuggle model
         * @param modelPreloader Describes how view content for the EpoxyModel should be preloaded
         * @param requestHolderFactory Should create and return a new [PreloadRequestHolder] each time it is invoked
         */
        fun <P : PreloadRequestHolder> with(
            epoxyController: EpoxyController,
            requestHolderFactory: () -> P,
            errorHandler: PreloadErrorHandler,
            maxItemsToPreload: Int,
            modelPreloader: EpoxyModelPreloader<out EpoxyModel<*>, out ViewMetadata?, out P>
        ): EpoxyPreloader<P> =
            with(
                epoxyController,
                requestHolderFactory,
                errorHandler,
                maxItemsToPreload,
                listOf(modelPreloader)
            )

        fun <P : PreloadRequestHolder> with(
            epoxyController: EpoxyController,
            requestHolderFactory: () -> P,
            errorHandler: PreloadErrorHandler,
            maxItemsToPreload: Int,
            modelPreloaders: List<EpoxyModelPreloader<out EpoxyModel<*>, out ViewMetadata?, out P>>
        ): EpoxyPreloader<P> {

            return EpoxyPreloader(
                epoxyController,
                requestHolderFactory,
                errorHandler,
                maxItemsToPreload,
                modelPreloaders
            )
        }

        /** Helper to create a preload scroll listener. Add the result to your RecyclerView. */
        fun <P : PreloadRequestHolder> with(
            epoxyAdapter: EpoxyAdapter,
            requestHolderFactory: () -> P,
            errorHandler: PreloadErrorHandler,
            maxItemsToPreload: Int,
            modelPreloaders: List<EpoxyModelPreloader<out EpoxyModel<*>, out ViewMetadata?, out P>>
        ): EpoxyPreloader<P> {

            return EpoxyPreloader(
                epoxyAdapter,
                requestHolderFactory,
                errorHandler,
                maxItemsToPreload,
                modelPreloaders
            )
        }
    }
}

class EpoxyPreloadException(errorMessage: String) : RuntimeException(errorMessage)

typealias PreloadErrorHandler = (Context, RuntimeException) -> Unit

/**
 * Data about an image view to be preloaded. This data is used to construct a Glide image request.
 *
 * @param metadata Any custom, additional data that the [EpoxyModelPreloader] chooses to provide that may be necessary to create the image request.
 */
class ViewData<out U : ViewMetadata?>(
    @IdRes val viewId: Int,
    @Px val width: Int,
    @Px val height: Int,
    val metadata: U
)

interface ViewMetadata {
    companion object {
        fun getDefault(view: View): ViewMetadata? {
            return when (view) {
                is ImageView -> ImageViewMetadata(view.scaleType)
                else -> null
            }
        }
    }
}

/**
 * Default implementation of [ViewMetadata] for an ImageView.
 * This data can help the preload request know how to configure itself.
 */
open class ImageViewMetadata(
    val scaleType: ImageView.ScaleType
) : ViewMetadata
