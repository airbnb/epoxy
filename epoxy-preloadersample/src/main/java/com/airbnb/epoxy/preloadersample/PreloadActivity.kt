package com.airbnb.epoxy.preloadersample

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.epoxy.addGlidePreloader
import com.airbnb.epoxy.glidePreloader
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import kotlinx.android.synthetic.main.list_activity.recycler_view

class PreloadActivity : AppCompatActivity() {

    private val images by lazy { intent.getStringArrayExtra(MainActivity.IMAGES_LIST_TAG) }
    private val controller by lazy { ImagesController(true) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_activity)

        recycler_view.setHasFixedSize(true)
        recycler_view.setController(controller)

        controller.setFilterDuplicates(true)
        controller.setData(images)

        recycler_view.addGlidePreloader(
            Glide.with(this),
            preloader = glidePreloader { requestManager, model: ImageModel_, _ ->
                requestManager.loadImage(model.imageUrl, true)
            }
        )
    }
}

fun RequestManager.loadImage(url: String, isPreloading: Boolean): RequestBuilder<Bitmap> {

    val options = RequestOptions
        .diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC)
        .dontAnimate()
        .signature(ObjectKey(url.plus(if (isPreloading) "_preloading" else "_not_preloading")))

    return asBitmap()
        .apply(options)
        .load(url)
}
