package com.airbnb.epoxy.kotlinsample.views

import android.content.Context
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.ModelView.Size

@ModelView(saveViewState = true, autoLayout = Size.MATCH_WIDTH_WRAP_HEIGHT)
class CarouselNoSnap(context: Context) : Carousel(context) {

    override fun getSnapHelperFactory(): Nothing? = null
}
