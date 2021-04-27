package com.airbnb.epoxy.models

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModelWithView
import com.airbnb.epoxy.ModelCollector
import com.airbnb.epoxy.VisibilityState
import com.airbnb.epoxy.utils.VisibilityAssertHelper
import com.airbnb.epoxy.utils.VisibilityAssertHelper.Companion.description

/**
 * Epoxy model used for visibility testing.
 */
internal class TrackerTestModel(
    private val debugTag: String,
    private val itemHeight: Int,
    private val itemWidth: Int = FrameLayout.LayoutParams.MATCH_PARENT,
    private val helper: VisibilityAssertHelper
) : EpoxyModelWithView<View>() {

    init {
        id(helper.id)
    }

    override fun buildView(parent: ViewGroup): View {
        VisibilityAssertHelper.log("buildView[$debugTag](id=${helper.id})")
        return TextView(parent.context).apply {
            // Force height
            layoutParams = RecyclerView.LayoutParams(itemWidth, itemHeight)
        }
    }

    override fun onVisibilityChanged(ph: Float, pw: Float, vh: Int, vw: Int, view: View) {
        helper.percentVisibleHeight = ph
        helper.percentVisibleWidth = pw
        helper.visibleHeight = vh
        helper.visibleWidth = vw
        if (ph.toInt() != 100) helper.fullImpression = false
    }

    override fun onVisibilityStateChanged(state: Int, view: View) {
        VisibilityAssertHelper.log("onVisibilityStateChanged[$debugTag](id=${helper.id})=${state.description()}")
        helper.visitedStates.add(state)
        when (state) {
            VisibilityState.VISIBLE, VisibilityState.INVISIBLE ->
                helper.visible = state == VisibilityState.VISIBLE
            VisibilityState.FOCUSED_VISIBLE, VisibilityState.UNFOCUSED_VISIBLE ->
                helper.focused = state == VisibilityState.FOCUSED_VISIBLE
            VisibilityState.PARTIAL_IMPRESSION_VISIBLE, VisibilityState.PARTIAL_IMPRESSION_INVISIBLE ->
                helper.partialImpression = state == VisibilityState.PARTIAL_IMPRESSION_VISIBLE
            VisibilityState.FULL_IMPRESSION_VISIBLE ->
                helper.fullImpression = state == VisibilityState.FULL_IMPRESSION_VISIBLE
        }
    }
}

/**
 * Helper function to create a [TrackerTestModel] and add it to the [EpoxyController].
 */
internal fun ModelCollector.trackerTestModel(
    debugTag: String,
    itemHeight: Int,
    itemWidth: Int = FrameLayout.LayoutParams.MATCH_PARENT,
    helper: VisibilityAssertHelper,
    modelInitializer: (TrackerTestModel.() -> Unit)? = null
) {
    add(
        TrackerTestModel(debugTag, itemHeight, itemWidth, helper)
            .apply { modelInitializer?.invoke(this) }
    )
}
