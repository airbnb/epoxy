package com.airbnb.epoxy

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible

/**
 * A helper to allow binding EpoxyModels to views outside of a RecyclerView. It is recommended to
 * use the extension functions [epoxyView] and [optionalEpoxyView] on Activity/Fragment/View Groups.
 *
 * This is helpful in two cases:
 * 1. You want to dynamically insert a view (of unknown or dynamic type) into a layout
 * 2. You have a predefined view in a layout and you want to update its data functionally with an
 * EpoxyModel
 */
class EpoxyViewBinder : ModelCollector {

    private var tempModel: EpoxyModel<*>? = null

    override fun add(model: EpoxyModel<*>) {
        require(tempModel == null) {
            "A model was already added to the ModelCollector. Only one should be added."
        }
        tempModel = model
    }

    /**
     * Bind the model to the view.
     * - If the model is null, the view will be hidden.
     * - If the view has previously been bound to another model, a partial bind will be done to only
     * update the properties that have changed.
     */
    fun <T : View> bind(view: T, model: EpoxyModel<T>?) {
        val newModel = model ?: run {
            // No model added, so the view is hidden
            view.isVisible = false
            return
        }
        view.isVisible = true

        val existingHolder = view.viewHolder

        val viewHolder =
            if (existingHolder == null || !newModel.hasSameViewType(existingHolder.model)) {
                EpoxyViewHolder(view.parent, view, false)
            } else {
                existingHolder
            }

        bind(viewHolder, newModel, existingHolder?.model)
    }

    /**
     * Replaces an existing view if it exists, else creates a new view. This is similar to
     * [replaceView] but it does not add new views to the parent [ViewGroup]. This allows for custom
     * layout handling (e.g. adding constraints for a ConstraintLayout).
     *
     * If the previous view is the same view type then it is reused. The new view is set to have the
     * same ID as the old view if it exists. If not then a new ID is generated.
     *
     * @return the [View] the model has been bound to.
     */
    fun replaceOrCreateView(
        parentView: ViewGroup,
        previousView: View?,
        model: EpoxyModel<*>
    ): View {
        val existingHolder = previousView?.viewHolder

        val viewHolder =
            if (existingHolder == null || !model.hasSameViewType(existingHolder.model)) {
                val newView = model.buildView(parentView)
                newView.id = previousView?.id ?: ViewCompat.generateViewId()

                EpoxyViewHolder(parentView, newView, false)
            } else {
                existingHolder
            }

        bind(viewHolder, model, null)
        return viewHolder.itemView
    }

    /**
     * Similar to [replaceView] but the model is provided via a lambda that adds it to this
     * [ModelCollector].
     */
    fun replaceView(
        previousView: View,
        modelProvider: ModelCollector.(context: Context) -> Unit
    ): View {
        @Suppress("UNUSED_EXPRESSION")
        modelProvider(previousView.context)
        val model = tempModel
        tempModel = null

        return replaceView(previousView, model)
    }

    /**
     * Creates a view for the model and swaps it in place in the parent of the previous view.
     * - If the previous view is the same view type, it is reused.
     * - If the model is null, the view is hidden.
     * - The new view is set to have the same ID as the old view.
     */
    fun replaceView(previousView: View, model: EpoxyModel<*>?): View {
        val newModel = model ?: run {
            // No model added, so the view is hidden
            previousView.isVisible = false
            return previousView
        }

        val existingHolder = previousView.viewHolder

        val viewHolder =
            if (existingHolder == null || !newModel.hasSameViewType(existingHolder.model)) {
                val parent = previousView.parent as ViewGroup
                val newView = newModel.buildView(parent)
                newView.id = previousView.id

                val index = parent.indexOfChild(previousView)
                parent.removeViewInLayout(previousView)
                parent.addView(newView, index, previousView.layoutParams)

                EpoxyViewHolder(parent, newView, false)
            } else {
                existingHolder
            }

        val newView = viewHolder.itemView.apply {
            isVisible = true
            id = previousView.id
        }

        bind(viewHolder, newModel, existingHolder?.model)
        return newView
    }

    /**
     * Takes an [EpoxyModel] added by the lambda, and creates a view for it in the given container.
     * The container should be empty, and the view will be added to it and bound to the model. If
     * the container was previously bound to another model, the existing view will be reused and
     * updated if necessary.
     *
     * @param modelProvider this lambda should be used to add a model to the [ModelCollector]
     * receiver. If no model is added the container will be cleared.
     */
    fun insertInto(container: ViewGroup, modelProvider: ModelCollector.() -> Unit) {
        require(container.childCount <= 1) { "Container cannot have more than one child" }

        // This lambda should add a model to the ModelCollector, which will end up calling
        // "addInternal" and set tempModel
        modelProvider()

        val newModel = tempModel ?: run {
            container.removeAllViews()
            return
        }

        val existingView: View? = container.getChildAt(0)
        val existingHolder = existingView?.viewHolder

        val viewHolder =
            if (existingHolder == null || !newModel.hasSameViewType(existingHolder.model)) {
                container.removeAllViews()
                val view = newModel.buildView(container)
                container.addView(view)
                EpoxyViewHolder(container, view, false)
            } else {
                existingHolder
            }

        bind(viewHolder, newModel, existingHolder?.model)
        tempModel = null
    }

    /**
     * Unbinds any model that is currently bound to this view. If no model is bound this is a no-op.
     */
    fun unbind(view: View) {
        val viewHolder = view.viewHolder ?: return
        viewHolder.unbind()
        view.viewHolder = null
    }

    private fun bind(
        viewHolder: EpoxyViewHolder,
        newModel: EpoxyModel<*>,
        existingModel: EpoxyModel<*>?
    ) {
        if (existingModel != newModel) {
            viewHolder.bind(newModel, existingModel, emptyList(), 0)
            viewHolder.itemView.viewHolder = viewHolder
        }
    }

    private fun EpoxyModel<*>.hasSameViewType(model: EpoxyModel<*>): Boolean =
        ViewTypeManager.getViewType(this) == ViewTypeManager.getViewType(model)

    internal fun onException(exception: RuntimeException) {
        globalExceptionHandler(this, exception)
    }

    companion object {
        /**
         * A callback to be notified when a recoverable exception occurs at runtime.  By default
         * these are ignored and Epoxy will recover, but you can override this to be aware of when
         * they happen.
         * <p>
         * For example, you could choose to rethrow the exception in development builds, or log
         * them in production.
         * <p>
         * A common use for this is being aware of views not being found for the binder to populate
         * with a model.
         * <p>
         * This callback will be used in all [EpoxyViewBinder] classes.
         */
        var globalExceptionHandler: ((EpoxyViewBinder, RuntimeException) -> Unit) = { _, _ -> }
    }
}
