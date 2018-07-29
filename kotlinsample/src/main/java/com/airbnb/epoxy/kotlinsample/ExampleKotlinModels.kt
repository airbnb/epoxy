package com.airbnb.epoxy.kotlinsample

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.AfterPropsSet
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp

// The ModelView annotation is used on Views to have models generated from those views.
// This is pretty straightforward with Kotlin, but properties need some special handling.
@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class KotlinView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    @ModelProp
    fun setValue(value: Int) {
        // Do something here with value
    }

    // Or if you need to store data in properties there are two options

    // Can make it nullable like this and annotate the setter
    var myListener: View.OnClickListener? = null
        @CallbackProp set

    // Or use lateinit
    @TextProp lateinit var myText: CharSequence

    @AfterPropsSet
    fun useProps() {
        // This is optional, and is called after the annotated properties above are set.
        // This is useful for using several properties in one method to guarantee they are all set first.
    }
}

// This more traditional style uses an Epoxy view holder pattern.
// The KotlinHolder is used to cache the view look ups, but uses property delegates to simplify it.
// The annotations allow for code generation of a subclass, which has equals/hashcode, and some other
// helpers. An extension function is also generated to make it easier to use this in an EpoxyController.
@EpoxyModelClass(layout = R.layout.view_holder_page_header)
abstract class SampleKotlinModelWithHolder : EpoxyModelWithHolder<Holder>() {

    @EpoxyAttribute lateinit var callback: () -> Unit
    @EpoxyAttribute lateinit var imageUrl: Uri

    override fun bind(holder: Holder) {
        holder.imageView.setImageURI(imageUrl)
        holder.imageView.setOnClickListener { callback() }
    }
}

class Holder : KotlinHolder() {
    val titleView by bind<TextView>(R.id.title)
    val imageView by bind<ImageView>(R.id.image)
}

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
