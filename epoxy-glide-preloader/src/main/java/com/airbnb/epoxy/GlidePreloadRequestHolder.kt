package com.airbnb.epoxy

import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import com.airbnb.epoxy.preload.ImageViewMetadata
import com.airbnb.epoxy.preload.PreloadRequestHolder
import com.airbnb.epoxy.preload.ViewData
import com.airbnb.epoxy.preload.ViewMetadata
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.BaseTarget
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.util.Util

/**
 * This class handles preloading a Glide request.
 * To use, call [startRequest] and provide your Glide request.
 *
 * @property requestManager The requestmanager that requests will be created with.
 */
open class GlidePreloadRequestHolder(
    private val requestManager: RequestManager
) : BaseTarget<Any>(), PreloadRequestHolder {
    private var width: Int = 0
    private var height: Int = 0

    override fun getSize(cb: SizeReadyCallback) {
        if (!Util.isValidDimensions(width, height)) {
            error(
                "Width and height must both be > 0 or Target#SIZE_ORIGINAL, but given" + " width: " +
                    width + " and height: " + height + ", either provide dimensions in the constructor" +
                    " or call override()"
            )
        }
        cb.onSizeReady(width, height)
    }

    override fun onResourceReady(resource: Any, transition: Transition<in Any>?) {
        // Requests cannot be cleared in this callback (throws an exception)
        // so we have to post it.
        mainThreadHandler.postAtTime({ clear() }, this, 0)
    }

    /**
     * Call this to create and execute your preload request.
     *
     * @param viewData Pass this so we know what size to downscale the image to. If its metadata
     * is [ImageViewMetadata] then a scaleType transform will also be applied automatically (The same
     * one as Glide's RequestManager applies when loading a request into an ImageView) (this is only
     * (done if no previous transformation was already applied, and not if transformations are disabled)
     *
     * @param requestBuilder Use this lambda to create and return your request. The receiver is the
     * [RequestManager] you should use to create the request.
     *
     */
    open fun <U : ViewMetadata?> startRequest(
        viewData: ViewData<U>,
        requestBuilder: (RequestManager) -> RequestBuilder<out Any>
    ) {
        width = viewData.width
        height = viewData.height

        // Cancel any previous attempts to clear the target that may still be queued
        mainThreadHandler.removeCallbacksAndMessages(this)

        @Suppress("UNCHECKED_CAST")
        (requestBuilder(requestManager) as RequestBuilder<Any>)
            .addTransformationForScaleTypeIfPossible(viewData)
            .into(this)
    }

    /**
     * Adds a transformation to scale the image for its scaleType if the given viewData
     * is [ImageViewMetadata] and no transformation has yet been set.
     *
     * This is taken from RequestBuilder#into(ImageView).
     */
    protected fun RequestBuilder<Any>.addTransformationForScaleTypeIfPossible(
        viewData: ViewData<*>
    ): RequestBuilder<Any> {

        val scaleType = (viewData.metadata as? ImageViewMetadata)?.scaleType ?: return this

        if (isTransformationSet || !isTransformationAllowed) {
            return this
        }

        // This clones the request options
        // so we need to make sure to return the new object.
        return when (scaleType) {
            ImageView.ScaleType.CENTER_CROP -> clone().optionalCenterCrop()
            ImageView.ScaleType.CENTER_INSIDE -> clone().optionalCenterInside()
            ImageView.ScaleType.FIT_CENTER,
            ImageView.ScaleType.FIT_START,
            ImageView.ScaleType.FIT_END -> clone().optionalFitCenter()
            ImageView.ScaleType.FIT_XY -> clone().optionalCenterInside()
            else -> {
                this
            }
        }
    }

    override fun clear() {
        width = 0
        height = 0
        requestManager.clear(this)
    }

    override fun removeCallback(cb: SizeReadyCallback) {
        // N/A
    }

    companion object {
        protected val mainThreadHandler = Handler(Looper.getMainLooper())
    }
}
