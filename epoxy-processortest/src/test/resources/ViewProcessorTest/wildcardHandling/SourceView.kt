package com.airbnb.epoxy

import android.content.Context
import android.view.View
import android.view.View.OnClickListener

@ModelView(
    autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT,
    baseModelClass = AirEpoxyModel::class
)
class SourceView(context: Context) : View(context) {

    @CallbackProp
    fun setKeyedListener(listener: KeyedListener<*, OnClickListener>?) {}

}

data class KeyedListener<Key, Listener>(val identifier: Key, val listener: Listener)
