package com.airbnb.epoxy.kotlinsample.models

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.epoxy.AfterPropsSet
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.OnViewRecycled
import com.airbnb.epoxy.OnVisibilityChanged
import com.airbnb.epoxy.OnVisibilityStateChanged
import com.airbnb.epoxy.TextProp
import com.airbnb.epoxy.VisibilityState
import com.airbnb.epoxy.kotlinsample.R

@ModelView(autoLayout = ModelView.Size.WRAP_WIDTH_WRAP_HEIGHT)
class CarouselItemCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val onVisibilityEventDrawable = OnVisibilityEventDrawable(context)

    private val textView: TextView

    init {
        inflate(context, R.layout.carousel_custom_view_item, this)
        orientation = VERTICAL
        textView = (findViewById(R.id.title))
        textView.setCompoundDrawables(null, null, null, onVisibilityEventDrawable)
        textView.compoundDrawablePadding = (4 * resources.displayMetrics.density).toInt()
    }

    @TextProp
    lateinit var title: CharSequence

    @AfterPropsSet
    fun useProps() {
        textView.text = title
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
                "$visibleHeight $visibleWidth ${System.identityHashCode(this)}"
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
        private const val TAG = "CarouselItemCustomView"
    }
}
