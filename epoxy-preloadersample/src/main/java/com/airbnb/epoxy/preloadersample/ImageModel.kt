package com.airbnb.epoxy.preloadersample

import android.view.View
import android.view.ViewParent
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.preload.Preloadable
import com.bumptech.glide.Glide

@EpoxyModelClass(layout = R.layout.list_item)
abstract class ImageModel : EpoxyModelWithHolder<ImageHolder>() {

    @EpoxyAttribute
    lateinit var imageUrl: String
    @EpoxyAttribute
    lateinit var text: String

    @EpoxyAttribute
    var preloading: Boolean = false

    override fun bind(holder: ImageHolder) {
        holder.glide.loadImage(imageUrl, preloading).into(holder.image)
        holder.text.text = text
    }

    override fun unbind(holder: ImageHolder) {
        holder.glide.clear(holder.image)
        holder.image.setImageDrawable(null)
    }
}

class ImageHolder(parent: ViewParent) : KotlinHolder(), Preloadable {
    val image by bind<ImageView>(R.id.image_view)
    val text by bind<TextView>(R.id.text_view)
    val glide = Glide.with((parent as View).context)
    override val viewsToPreload by lazy { listOf(image) }
}
