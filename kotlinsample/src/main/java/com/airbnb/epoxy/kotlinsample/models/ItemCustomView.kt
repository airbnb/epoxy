package com.airbnb.epoxy.kotlinsample.models

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import com.airbnb.epoxy.AfterPropsSet
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.OnViewRecycled
import com.airbnb.epoxy.OnVisibilityChanged
import com.airbnb.epoxy.OnVisibilityStateChanged
import com.airbnb.epoxy.TextProp
import com.airbnb.epoxy.VisibilityState
import com.airbnb.epoxy.kotlinsample.R

// The ModelView annotation is used on Views to have models generated from those views.
// This is pretty straightforward with Kotlin, but properties need some special handling.
@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class ItemCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val onVisibilityEventDrawable = OnVisibilityEventDrawable(context)

    private val textView: TextView

    init {
        inflate(context, R.layout.custom_view_item, this)
        orientation = VERTICAL
        textView = (findViewById(R.id.title))
        textView.setCompoundDrawables(null, null, onVisibilityEventDrawable, null)
        textView.compoundDrawablePadding = (4 * resources.displayMetrics.density).toInt()
    }

    // You can annotate your methods with @ModelProp
    // A default model property value can be set by using Kotlin default arguments, but you
    // must use JvmOverloads for Epoxy to handle it correctly.
    @JvmOverloads
    @ModelProp
    fun color(@ColorInt color: Int = Color.RED) {
        textView.setTextColor(color)
    }

    // Or if you need to store data in properties there are two options

    // 1.You can make it nullable like this and annotate the setter
    var listener: View.OnClickListener? = null
        @CallbackProp set

    // 2. Or you can use lateinit
    @TextProp
    lateinit var title: CharSequence

    @AfterPropsSet
    fun useProps() {
        // This is optional, and is called after the annotated properties above are set.
        // This is useful for using several properties in one method to guarantee they are all set first.
        textView.text = title
        textView.setOnClickListener(listener)
    }

    @OnVisibilityStateChanged
    fun onVisibilityStateChanged(
        @VisibilityState.Visibility visibilityState: Int
    ) {
        when (visibilityState) {
            VisibilityState.VISIBLE -> {
                Log.d(TAG, "$title Visible")
                onVisibilityEventDrawable.visible = true
            }
            VisibilityState.INVISIBLE -> {
                Log.d(TAG, "$title Invisible")
                onVisibilityEventDrawable.visible = false
            }
            VisibilityState.FOCUSED_VISIBLE -> {
                Log.d(TAG, "$title FocusedVisible")
                onVisibilityEventDrawable.focusedVisible = true
            }
            VisibilityState.UNFOCUSED_VISIBLE -> {
                Log.d(TAG, "$title UnfocusedVisible")
                onVisibilityEventDrawable.focusedVisible = false
            }
            VisibilityState.PARTIAL_IMPRESSION_VISIBLE -> {
                Log.d(TAG, "$title PartialImpressionVisible")
                onVisibilityEventDrawable.partialImpression = true
            }
            VisibilityState.PARTIAL_IMPRESSION_INVISIBLE -> {
                Log.d(TAG, "$title PartialImpressionInVisible")
                onVisibilityEventDrawable.partialImpression = false
            }
            VisibilityState.FULL_IMPRESSION_VISIBLE -> {
                Log.d(TAG, "$title FullImpressionVisible")
                onVisibilityEventDrawable.fullImpression = true
            }
        }
    }

    @OnVisibilityChanged
    fun onVisibilityChanged(
        percentVisibleHeight: Float,
        percentVisibleWidth: Float,
        visibleHeight: Int,
        visibleWidth: Int
    ) {
        Log.d(
            TAG,
            "$title onChanged ${percentVisibleHeight.toInt()} ${percentVisibleWidth.toInt()} " +
                "$visibleHeight $visibleWidth ${System.identityHashCode(
                    this
                )}"
        )
        with(onVisibilityEventDrawable) {
            if ((percentVisibleHeight < 100 || percentVisibleWidth < 100) && fullImpression) {
                fullImpression = false
            }
            percentHeight = percentVisibleHeight
            percentWidth = percentVisibleWidth
        }
    }

    @OnViewRecycled
    fun clear() {
        onVisibilityEventDrawable.reset()
    }

    companion object {
        private const val TAG = "ItemCustomView"
    }
}
