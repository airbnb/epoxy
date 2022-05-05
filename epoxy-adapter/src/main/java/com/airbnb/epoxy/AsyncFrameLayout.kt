package com.airbnb.epoxy

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * Base class to support Async layout inflation with EPoxy.
 */
abstract class AsyncFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), AsyncInflatedView {
    override var isInflated : Boolean = false
    override var pendingRunnables : ArrayList<Runnable> = ArrayList()
}