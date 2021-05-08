package com.airbnb.epoxy.preloadersample

import com.airbnb.epoxy.TypedEpoxyController

class ImagesController(private val isPreloading: Boolean) : TypedEpoxyController<Array<String>>() {

    override fun buildModels(data: Array<String>) {

        data.forEachIndexed { index, url ->

            image {
                id("image_id_$url")
                imageUrl(url)
                text("Image Number: $index")
                preloading(this@ImagesController.isPreloading)
            }
        }
    }
}
