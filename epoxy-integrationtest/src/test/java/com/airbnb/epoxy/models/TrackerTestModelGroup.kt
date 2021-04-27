package com.airbnb.epoxy.models

import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelGroup
import com.airbnb.epoxy.ModelCollector
import com.airbnb.epoxy.ModelGroupHolder
import com.airbnb.epoxy.VisibilityState
import com.airbnb.epoxy.utils.VisibilityAssertHelper
import com.airbnb.epoxy.utils.VisibilityAssertHelper.Companion.description
import com.airbnb.epoxy.utils.VisibilityAssertHelper.Companion.log

/**
 * Epoxy model group used for visibility testing.
 */
internal class TrackerTestModelGroup(
    private val debugTag: String,
    private val helper: VisibilityAssertHelper
) : EpoxyModelGroup() {

    init {
        id(helper.id)
    }

    /**
     * Add models to this group, removing any that existed prior.
     */
    fun setModels(vararg modelsToAdd: EpoxyModel<*>) {
        models.clear()
        for (model in modelsToAdd) {
            addModel(model)
        }
    }

    override fun onVisibilityChanged(
        percentVisibleHeight: Float,
        percentVisibleWidth: Float,
        visibleHeight: Int,
        visibleWidth: Int,
        holder: ModelGroupHolder
    ) {
        helper.percentVisibleHeight = percentVisibleHeight
        helper.percentVisibleWidth = percentVisibleWidth
        helper.visibleHeight = visibleHeight
        helper.visibleWidth = visibleWidth
        if (percentVisibleHeight.toInt() != 100) helper.fullImpression = false
    }

    override fun onVisibilityStateChanged(visibilityState: Int, holder: ModelGroupHolder) {
        log("onVisibilityStateChanged[$debugTag](id=${helper.id})=${visibilityState.description()}")
        helper.visitedStates.add(visibilityState)
        when (visibilityState) {
            VisibilityState.VISIBLE, VisibilityState.INVISIBLE ->
                helper.visible = visibilityState == VisibilityState.VISIBLE
            VisibilityState.FOCUSED_VISIBLE, VisibilityState.UNFOCUSED_VISIBLE ->
                helper.focused = visibilityState == VisibilityState.FOCUSED_VISIBLE
            VisibilityState.PARTIAL_IMPRESSION_VISIBLE, VisibilityState.PARTIAL_IMPRESSION_INVISIBLE ->
                helper.partialImpression = visibilityState == VisibilityState.PARTIAL_IMPRESSION_VISIBLE
            VisibilityState.FULL_IMPRESSION_VISIBLE ->
                helper.fullImpression = visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE
        }
    }
}

/**
 * Helper function to create a [TrackerTestModelGroup] and add it to the [EpoxyController].
 */
internal fun ModelCollector.trackerTestModelGroup(
    debugTag: String,
    helper: VisibilityAssertHelper,
    modelInitializer: (TrackerTestModelGroup.() -> Unit)? = null
) {
    add(
        TrackerTestModelGroup(debugTag, helper)
            .apply { modelInitializer?.invoke(this) }
    )
}
