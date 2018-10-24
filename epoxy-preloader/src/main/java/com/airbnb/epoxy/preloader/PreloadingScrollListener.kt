package com.airbnb.epoxy.preloader

import android.os.Handler
import android.os.Looper
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.BaseTarget
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.util.Util

class PreloadingScrollListener(
        private val preloader: EpoxyPreloader,
        private val requestManager: RequestManager,
        private val maxPreload: Int
) : RecyclerView.OnScrollListener() {
    private val preloadTargetQueue = PreloadTargetQueue(maxPreload + 1, ::onResourceLoaded)
    private var lastVisibleRange: IntRange = IntRange.EMPTY
    private var lastPreloadRange: IntProgression = IntRange.EMPTY
    private var totalItemCount = -1
    private var scrollState: Int = RecyclerView.SCROLL_STATE_IDLE

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
        totalItemCount = recyclerView.adapter.itemCount

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

        val isIncreasing = visibleRange.first > lastVisibleRange.first || visibleRange.last > lastVisibleRange.last

        val preloadRange = calculatePreloadRange(firstVisiblePosition, lastVisiblePosition, isIncreasing)

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

    private fun calculatePreloadRange(firstVisiblePosition: Int, lastVisiblePosition: Int, isIncreasing: Boolean): IntProgression {
        val from = if (isIncreasing) lastVisiblePosition + 1 else firstVisiblePosition - 1
        val to = from + if (isIncreasing) maxPreload - 1 else 1 - maxPreload

        return IntProgression.fromClosedRange(
                rangeStart = from.clampToAdapterRange(),
                rangeEnd = to.clampToAdapterRange(),
                step = if (isIncreasing) 1 else -1
        )
    }

    /** Check if an item index is valid. It may not be if the adapter is empty, or if adapter changes have been dispatched since the last layout pass. */
    private fun Int.isInvalid() = this == RecyclerView.NO_POSITION || this >= totalItemCount

    private fun Int.clampToAdapterRange() = Math.min(totalItemCount - 1, Math.max(this, 0))

    private fun preloadAdapterPosition(position: Int) {
        preloader
                .getPreloadItems(position)
                .forEach { preloadItem(it) }
    }

    private fun preloadItem(@Nullable item: EpoxyModelImageData<*, *>) {
        @Suppress("UNCHECKED_CAST")
        val preloadRequestBuilder = preloader.getPreloadRequestBuilder(item) as? RequestBuilder<Any> ?: return

        val width = item.viewData.width
        val height = item.viewData.height

        val preloadTarget = preloadTargetQueue.next(width, height)
        preloadRequestBuilder.into(preloadTarget)

        // Cancel any previous attempts to clear the target
        mainThreadHandler.removeCallbacksAndMessages(preloadTarget)
    }

    fun cancelAll() {
        for (i in 0 until maxPreload) {
            requestManager.clear(preloadTargetQueue.next(0, 0))
        }
    }

    private fun onResourceLoaded(preloadTarget: PreloadTarget) {
        // Holding on to the bitmap is unnecessary and strains memory usage.
        // Bitmap has been loaded into memory cache so we can remove our reference.
        // Needs to be done async after the onResourceLoaded callback to prevent a Glide crash
        // We use the target as the token so we can remove the runnable if another preload is started
        mainThreadHandler.postAtTime({ requestManager.clear(preloadTarget) }, preloadTarget, 0)
    }

    private class PreloadTargetQueue(size: Int, onResourceLoaded: (PreloadTarget) -> Unit) {
        private val queue = Util.createQueue<PreloadTarget>(size).apply {
            for (i in 0 until size) {
                offer(PreloadTarget(onResourceLoaded))
            }
        }

        fun next(width: Int, height: Int): PreloadTarget {
            val result = queue.poll()
            queue.offer(result)
            result.photoWidth = width
            result.photoHeight = height
            return result
        }
    }

    private class PreloadTarget(val onResourceLoaded: (PreloadTarget) -> Unit) : BaseTarget<Any>() {
        var photoHeight = 0
        var photoWidth = 0

        override fun onResourceReady(resource: Any, transition: Transition<in Any>?) {
            onResourceLoaded(this)
        }

        override fun getSize(@NonNull cb: SizeReadyCallback) {
            cb.onSizeReady(photoWidth, photoHeight)
        }

        override fun removeCallback(@NonNull cb: SizeReadyCallback) {
            // Do nothing because we don't retain references to SizeReadyCallbacks.
        }
    }
}

/**
 *
 * Represents a threshold for fast scrolling.
 * This is a bit arbitrary and was determined by looking at values while flinging vs slow scrolling.
 * Ideally it would be based on DP, but this is simpler.
 */
private const val FLING_THRESHOLD_PX = 75
private val mainThreadHandler = Handler(Looper.getMainLooper())