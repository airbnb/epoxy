package com.airbnb.epoxy

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View

/**
 * A view to be used with [EpoxyViewBinder] when an epoxy model is used outside of a RecyclerView. This view will be
 * replaced completely when a model is bound to it.
 */
class EpoxyViewStub @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    override fun onRestoreInstanceState(state: Parcelable?) {
        // Restoring the state is not supported due to EpoxyViewBinder having replaced this view with another view type.
        // If the activity/fragment is restoring its state, it's possible that EpoxyViewBinder has not yet replaced this
        // view with the type that was used prior to saving the state. This would mean that the view types will differ
        // during the state restoration process since EpoxyViewBinder uses the same view ID for the replacement view.
        // This can cause crashes because the saved state types will differ (e.g. a RecyclerView's SavedState cannot be
        // applied to a normal View). To solve this, ignore any state restoration as this is an empty view and never
        // shows anything.
        super.onRestoreInstanceState(null)
    }
}
