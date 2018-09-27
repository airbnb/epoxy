package com.airbnb.epoxy.kotlinsample.models

import android.content.Context
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.epoxy.AfterPropsSet
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.OnVisibilityEvent
import com.airbnb.epoxy.TextProp
import com.airbnb.epoxy.kotlinsample.R

// The ModelView annotation is used on Views to have models generated from those views.
// This is pretty straightforward with Kotlin, but properties need some special handling.
@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class ItemCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val textView: TextView
    init {
        inflate(context, R.layout.custom_view_item, this)
        orientation = VERTICAL
        textView = (findViewById<TextView>(R.id.title))
    }

    // You can annotate your methods with @ModelProp
    @ModelProp
    fun color(@ColorInt color: Int) {
        textView.setTextColor(color)
    }

    // Or if you need to store data in properties there are two options

    // 1.You can make it nullable like this and annotate the setter
    var listener: View.OnClickListener? = null
        @CallbackProp set

    // 2. Or you can use lateinit
    @TextProp lateinit var title: CharSequence


    @AfterPropsSet
    fun useProps() {
        // This is optional, and is called after the annotated properties above are set.
        // This is useful for using several properties in one method to guarantee they are all set first.
        textView.text = title
        textView.setOnClickListener(listener)
    }

    @OnVisibilityEvent(OnVisibilityEvent.Event.Changed)
    fun onChanged(
        visibleHeight: Float,
        visibleWidth: Float,
        percentVisibleHeight: Int,
        percentVisibleWidth: Int
    ) {
        textView.text = "onVisibilityChanged $visibleHeight $visibleWidth $percentVisibleHeight $percentVisibleWidth"
    }

    @OnVisibilityEvent(OnVisibilityEvent.Event.Visible)
    fun onVisible() {
    }

    @OnVisibilityEvent(OnVisibilityEvent.Event.FocusedVisible)
    fun onFocusedVisible() {
    }

    @OnVisibilityEvent(OnVisibilityEvent.Event.FullImpressionVisible)
    fun onFullImpressionVisible() {
    }

    @OnVisibilityEvent(OnVisibilityEvent.Event.Invisible)
    fun onInvisible() {
    }

    @OnVisibilityEvent(OnVisibilityEvent.Event.UnfocusedVisible)
    fun onUnfocusedVisible() {
    }
}