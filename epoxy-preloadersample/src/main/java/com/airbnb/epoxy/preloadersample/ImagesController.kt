package com.airbnb.epoxy.preloadersample

import android.content.Context
import com.airbnb.epoxy.TypedEpoxyController
import com.bumptech.glide.Glide

class ImagesController(context: Context, private val isPreloading: Boolean) : TypedEpoxyController<Array<String>>() {

    private val glide = Glide.with(context)

    override fun buildModels(data: Array<String>) {

        data.forEachIndexed { index, url ->

            image {
                id("image_id_$url")
                glide(glide)
                imageUrl(url)
                text("Image Number: $index")
                preloading(isPreloading)
            }
        }
    }
}