package com.airbnb.epoxy.preloadersample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.airbnb.epoxy.preloader.EpoxyModelPreloader
import com.airbnb.epoxy.preloader.EpoxyPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import kotlinx.android.synthetic.main.list_activity.*

class PreloadActivity : AppCompatActivity() {

    private val images by lazy { intent.getStringArrayExtra(MainActivity.IMAGES_LIST_TAG) }
    private val controller by lazy { ImagesController(this, true) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_activity)

        recycler_view.setHasFixedSize(true)
        recycler_view.setController(controller)

        controller.setFilterDuplicates(true)
        controller.setData(images)

        val model = EpoxyModelPreloader.with(R.id.image_view) { glide: RequestManager, model: ImageModel_, _ ->

            glide.loadImage(model.imageUrl, true)
        }

        val preloader = EpoxyPreloader.with(this, controller, { err: RuntimeException -> err.printStackTrace() }, 10, model)

        recycler_view.addOnScrollListener(preloader)
    }
}

fun RequestManager.loadImage(url: String, isPreloading: Boolean): RequestBuilder<*> {

    val options = RequestOptions
            .diskCacheStrategyOf(DiskCacheStrategy.NONE)
            .dontAnimate()
            .signature(ObjectKey(url.plus(if(isPreloading) "_preloading" else "_not_preloading")))
            .sizeMultiplier(0.3f)

    return asBitmap()
            .apply(options)
            .load(url)
}