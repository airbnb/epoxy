package com.airbnb.epoxy.kotlinsample

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder

// This does not require annotations or annotation processing.
// The data class is required to generated equals/hashcode which Epoxy needs for diffing.
// Views are easily declared via property delegates
data class SampleKotlinModel(
    val title: String,
    val imageUrl: Uri
) : KotlinModel(R.layout.view_holder_page_header) {

    val titleView by bind<TextView>(R.id.title)
    val imageView by bind<ImageView>(R.id.image)

    override fun bind() {
        titleView.text = title
        imageView.setImageURI(imageUrl)
    }
}

// This more traditional style uses an Epoxy view holder pattern.
// The KotlinHolder is used to cache the view look ups, but uses property delegates to simplify it.
// The annotations allow for code generation of a subclass, which has equals/hashcode, and some other
// helpers. An extension function is also generated to make it easier to use this in an EpoxyController.
@EpoxyModelClass(layout = R.layout.view_holder_page_header)
abstract class SampleKotlinModelWithHolder : EpoxyModelWithHolder<Holder>() {

    @EpoxyAttribute lateinit var title: String
    @EpoxyAttribute lateinit var imageUrl: Uri

    override fun bind(holder: Holder) {
        holder.imageView.setImageURI(imageUrl)
        holder.titleView.text = title
    }

}

class Holder : KotlinHolder() {
    val titleView by bind<TextView>(R.id.title)
    val imageView by bind<ImageView>(R.id.image)
}
