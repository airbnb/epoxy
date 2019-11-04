package com.airbnb.epoxy.sample.models

import android.view.View.OnClickListener
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.sample.R
import com.airbnb.epoxy.sample.models.ImageButtonModel.ImageButtonHolder

@EpoxyModelClass(layout = R.layout.model_image_button)
abstract class ImageButtonModel : EpoxyModelWithHolder<ImageButtonHolder>() {
    @EpoxyAttribute @DrawableRes var imageRes: Int = 0
    @EpoxyAttribute(DoNotHash) var clickListener: OnClickListener? = null

    override fun bind(holder: ImageButtonHolder) {
        holder.button.setImageResource(imageRes)
        holder.button.setOnClickListener(clickListener)
    }

    override fun unbind(holder: ImageButtonHolder) {
        // Release resources and don't leak listeners as this view goes back to the view pool
        holder.button.setOnClickListener(null)
        holder.button.setImageDrawable(null)
    }

    class ImageButtonHolder : BaseEpoxyHolder() {
        val button: ImageView by bind((R.id.button))
    }
}
