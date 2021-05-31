package com.tekina.mavericksexample.epoxyviews

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import com.airbnb.paris.annotations.Style
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.extensions.headerViewStyle
import com.airbnb.paris.extensions.layoutHeight
import com.airbnb.paris.extensions.layoutWidth
import com.tekina.mavericksexample.R

@Styleable // Dynamic styling via the Paris library
@ModelView(saveViewState = true)
class HeaderView(context: Context?) : LinearLayout(context) {
    private var title: TextView? = null
    private var caption: TextView? = null
    private var image: ImageView? = null

    init {
        inflate(getContext(), R.layout.header_view, this)

        title = findViewById(R.id.title_text)
        caption = findViewById(R.id.caption_text)
        image = findViewById(R.id.image)
    }

    @TextProp(defaultRes = R.string.app_name)
    fun setTitle(title: CharSequence?) {
        println("Updating setTitle $title")
        this.title?.text = title
    }

    @TextProp
    fun setCaption(caption: CharSequence?) {
        this.caption?.text = caption
    }

    @ModelProp
    fun setShowImage(isVisible: Boolean) {
        image?.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    @CallbackProp
    fun setClickListener(listener: OnClickListener?) {
        this.title?.setOnClickListener(listener)
    }

    companion object {
        @Style(isDefault = true)
        val headerStyle: com.airbnb.paris.styles.Style = headerViewStyle {
            layoutWidth(ViewGroup.LayoutParams.MATCH_PARENT)
            layoutHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
}
