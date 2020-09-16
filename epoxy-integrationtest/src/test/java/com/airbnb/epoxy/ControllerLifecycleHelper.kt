package com.airbnb.epoxy

import android.view.View
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider

internal class ControllerLifecycleHelper {
    private var viewHolder: EpoxyViewHolder? = null
    fun buildModelsAndBind(controller: EpoxyController) {
        controller.requestModelBuild()
        bindModels(controller)
    }

    fun bindModels(controller: EpoxyController) {
        bindModels(controller.adapter)
    }

    fun bindModels(adapter: BaseEpoxyAdapter) {
        val models = adapter.currentModels
        for (i in models.indices) {
            viewHolder = createViewHolder(adapter, i)
            adapter.onBindViewHolder(viewHolder!!, i)
        }
    }

    fun bindModel(model: EpoxyModel<*>): View {
        val controller = SimpleEpoxyController()
        controller.setModels(listOf(model))
        bindModels(controller)
        return viewHolder!!.itemView
    }

    fun recycleLastBoundModel(controller: EpoxyController) {
        controller.adapter.onViewRecycled(viewHolder!!)
    }

    companion object {
        @JvmStatic
        fun createViewHolder(adapter: BaseEpoxyAdapter, position: Int): EpoxyViewHolder {
            return adapter.onCreateViewHolder(
                FrameLayout(ApplicationProvider.getApplicationContext()),
                adapter.getItemViewType(position)
            )
        }
    }
}
