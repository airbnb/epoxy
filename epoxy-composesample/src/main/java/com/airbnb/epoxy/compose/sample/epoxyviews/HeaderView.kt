package com.airbnb.epoxy.compose.sample.epoxyviews

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import com.airbnb.epoxy.compose.sample.R
import com.airbnb.paris.annotations.Style
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.extensions.headerViewStyle
import com.airbnb.paris.extensions.layoutHeight
import com.airbnb.paris.extensions.layoutWidth

@Styleable // Dynamic styling via the Paris library
@ModelView
class HeaderView(context: Context?) : LinearLayout(context) {
    private var title: TextView? = null
    private var caption: TextView? = null

    init {
        setOrientation(VERTICAL)
        inflate(getContext(), R.layout.header_view, this)
        title = findViewById(R.id.title_text)
        caption = findViewById(R.id.caption_text)
    }

    @TextProp(defaultRes = R.string.app_name)
    fun setTitle(title: CharSequence?) {
        println("Aniket, title: $title, this.title: ${this.title}")
        this.title?.text = title
    }

    @TextProp
    fun setCaption(caption: CharSequence?) {
        this.caption?.text = caption
    }

    companion object {
        @Style(isDefault = true)
        val headerStyle: com.airbnb.paris.styles.Style = headerViewStyle {
            layoutWidth(ViewGroup.LayoutParams.MATCH_PARENT)
            layoutHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
}