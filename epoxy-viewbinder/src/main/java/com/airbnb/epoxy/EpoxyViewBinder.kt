package com.airbnb.epoxy

import android.content.res.Resources
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.example.epoxy_viewbinder.R

/**
 * A helper to allow binding EpoxyModels to views outside of a RecyclerView.
 *
 * This is helpful in two cases:
 * 1. You want to dynamically insert a view (of unknown or dynamic type) into a layout
 * 2. You have a predefined view in a layout and you want to update it's data functionally with an EpoxyModel
 */
class EpoxyViewBinder : EpoxyController() {

    private var tempModel: EpoxyModel<*>? = null

    private var interceptor: EpoxyController.Interceptor? = null

    override fun addInterceptor(interceptor: Interceptor) {
        this.interceptor = interceptor
    }

    internal override fun addInternal(modelToAdd: EpoxyModel<*>) {
        require(tempModel == null) { "A model was already added to the EpoxyController. Only one should be added. " }
        interceptor?.intercept(listOf(modelToAdd))
        // Don't call super to avoid epoxy complaining that we are not in a buildModels call
        tempModel = modelToAdd
    }

    // This needs to be hardcoded to prevent super from crashing, since we aren't actually in model building mode
    internal override fun isModelAddedMultipleTimes(model: EpoxyModel<*>?) = false

    internal override fun addAfterInterceptorCallback(callback: ModelInterceptorCallback) {
        // no op
    }

    override fun buildModels() {
        // Not used
    }

    /**
     * Bind the model to the view. If the model is null the view will be hidden.
     *
     * If the view has previously been bound to another model a partial bind will be done to only update the props that have changed.
     */
    fun <T : View> bind(view: T, model: EpoxyModel<T>?) {

        val newModel = model ?: run {
            // No model added, so we hide the view
            view.isVisible = false
            return
        }
        view.isVisible = true

        val existingHolder = view.viewHolder

        val viewHolder = if (existingHolder == null || !newModel.hasSameViewType(existingHolder.model)) {
            EpoxyViewHolder(view.parent, view, false)
        } else {
            existingHolder
        }

        bind(viewHolder, newModel, existingHolder?.model)
    }

    /**
     * Replaces an existing view if it exists, else create a new view. This is similar to [replaceView] but it does not add the view to the
     * parent [ViewGroup]. This allows for custom layout handling (e.g. adding constraints for a [androidx.constraintlayout.widget.ConstraintLayout].
     *
     * If the previous view is the same view type then it is reused.
     * The new view is set to have the same id as the old view if it exists. If not then a new ID is generated.
     *
     * @return the [View] the model has been bound to.
     */
    fun replaceOrCreateView(parentView: ViewGroup, previousView: View?, model: EpoxyModel<*>): View {
        val existingHolder = previousView?.viewHolder

        val viewHolder = if (existingHolder == null || !model.hasSameViewType(existingHolder.model)) {
            val newView = model.buildView(parentView)
            newView.id = previousView?.id ?: ViewCompat.generateViewId()
            parentView.addView(newView)

            EpoxyViewHolder(parentView, newView, false)
        } else {
            existingHolder
        }

        bind(viewHolder, model, null)
        return viewHolder.itemView
    }

    /**
     * Similar to [replaceView] but the model is provided via a lambda that adds it to an EpoxyController.
     */
    fun replaceView(previousView: View, modelProvider: EpoxyController.() -> Unit): View {
        @Suppress("UNUSED_EXPRESSION")
        modelProvider()
        val model = tempModel
        tempModel = null

        return replaceView(previousView, model)
    }

    /**
     * Creates a view for the model and swaps it in place in the parent for the previous view.
     * If the previous view is the same view type then it is reused.
     * If the model is null the view is hidden.
     * The new view is set to have the same id as the old view.
     */
    fun replaceView(previousView: View, model: EpoxyModel<*>?): View {

        val newModel = model ?: run {
            // No model added, so we hide the view
            previousView.isVisible = false
            return previousView
        }

        val existingHolder = previousView.viewHolder

        val viewHolder = if (existingHolder == null || !newModel.hasSameViewType(existingHolder.model)) {
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
     * Takes a EpoxyModel added by the lambda, and creates a view for it in the given container.
     * The container should be empty, and the view will be added to it and bound to the model.
     * If the container was previously bound to another model the existing view will be reused and updated if necessary.
     *
     * @param modelProvider This lambda should be used to add a model to the EpoxyController receiver. If no model is added the container will be cleared.
     */
    fun insertInto(container: ViewGroup, modelProvider: EpoxyController.() -> Unit) {
        require(container.childCount <= 1) { "Container cannot have more than one child" }

        // This lambda should add a model to the EpoxyController, which will end up calling "addInternal" and set tempModel
        @Suppress("UNUSED_EXPRESSION")
        modelProvider()

        val newModel = tempModel ?: run {
            container.removeAllViews()
            return
        }

        val existingView: View? = container.getChildAt(0)
        val existingHolder = existingView?.viewHolder

        val viewHolder = if (existingHolder == null || !newModel.hasSameViewType(existingHolder.model)) {
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

    private fun bind(viewHolder: EpoxyViewHolder, newModel: EpoxyModel<*>, existingModel: EpoxyModel<*>?) {
        if (existingModel != newModel) {
            viewHolder.bind(newModel, existingModel, emptyList(), 0)
            viewHolder.itemView.viewHolder = viewHolder
        }
    }

    /**
     * Unbinds any model that is currently bound to this view.
     * If no model is bound this is a no-op.
     */
    fun unbind(view: View) {
        val viewHolder = view.viewHolder ?: return
        viewHolder.unbind()
        view.viewHolder = null
    }

    private fun EpoxyModel<*>.hasSameViewType(model: EpoxyModel<*>): Boolean = ViewTypeManager.getViewType(this) == ViewTypeManager.getViewType(model)
}

/**
 * Shortcut for getting/setting an [EpoxyViewHolder] on a [View]'s tag.
 */
internal var View.viewHolder: EpoxyViewHolder?
    set(value) = setTag(R.id.epoxy_view_binder, value)
    get() = getTag(R.id.epoxy_view_binder) as EpoxyViewHolder?

/**
 * Shortcut for creating a [LifecycleAwareEpoxyViewBinder] in a lazy way.
 *
 * @param viewId resource ID for the view to replace. This should be an [EpoxyViewStub].
 */
fun Fragment.epoxyView(
    @IdRes viewId: Int,
    initializer: LifecycleAwareEpoxyViewBinder.() -> Unit,
    modelProvider: EpoxyController.() -> Unit,
    useVisibilityTracking: Boolean = false
) = lazy {
    return@lazy LifecycleAwareEpoxyViewBinder(
        viewLifecycleOwner,
        { view },
        viewId,
        modelProvider,
        useVisibilityTracking = useVisibilityTracking
    ).apply(initializer)
}

/**
 * Shortcut for creating a [LifecycleAwareEpoxyViewBinder] in a lazy way.
 *
 * @param viewId resource ID for the view to replace. This should be an [EpoxyViewStub].
 */
fun AppCompatActivity.epoxyView(
    @IdRes viewId: Int,
    initializer: LifecycleAwareEpoxyViewBinder.() -> Unit,
    modelProvider: EpoxyController.() -> Unit,
    useVisibilityTracking: Boolean = false
) = lazy {
    return@lazy LifecycleAwareEpoxyViewBinder(
        this,
        { findViewById(android.R.id.content) },
        viewId,
        modelProvider,
        useVisibilityTracking = useVisibilityTracking
    ).apply(initializer)
}

/**
 * Shortcut for creating a [LifecycleAwareEpoxyViewBinder] in a lazy way.
 *
 * @param viewId resource ID for the view to replace. This should be an [EpoxyViewStub].
 */
fun ViewGroup.epoxyView(
    @IdRes viewId: Int,
    initializer: LifecycleAwareEpoxyViewBinder.() -> Unit,
    modelProvider: EpoxyController.() -> Unit,
    useVisibilityTracking: Boolean = false
) = lazy {
    return@lazy LifecycleAwareEpoxyViewBinder(
        (this.context as? LifecycleOwner) ?: error("LifecycleOwner required as view's context "),
        { this },
        viewId,
        modelProvider,
        useVisibilityTracking = useVisibilityTracking
    ).apply(initializer)
}

/**
 * Shortcut for creating a [LifecycleAwareEpoxyViewBinder] in a lazy way.
 *
 * If the returned view binder is null it means that the view could not be found.
 *
 * @param viewId resource ID for the view to replace. This should be an [EpoxyViewStub].
 */
fun Fragment.optionalEpoxyView(
    @IdRes viewId: Int,
    modelProvider: EpoxyController.() -> Unit,
    initializer: (LifecycleAwareEpoxyViewBinder.() -> Unit)? = null,
    fallbackToNameLookup: Boolean = false,
    useVisibilityTracking: Boolean = false
) = lazy {
    val view = view ?: error("Fragment view has not been created")
    // View id is not present, we just return null in that case.
    if (view.maybeFindViewByIdName<View>(viewId, fallbackToNameLookup) == null) return@lazy null

    return@lazy LifecycleAwareEpoxyViewBinder(
        viewLifecycleOwner,
        { view },
        viewId,
        modelProvider,
        fallbackToNameLookup = fallbackToNameLookup,
        useVisibilityTracking = useVisibilityTracking
    ).apply { initializer?.invoke(this) }
}

/**
 * This class uses an epoxy model to update a view. The view reference is cleared when the fragment is stopped.
 * Call [invalidate] to have the model rebuilt and rebound to the view.
 *
 * @param viewId The id of the view stub that the Epoxy view should replace. This tells the ViewBinder where in the
 * layout to put the view. This should correspond to a view of type [EpoxyViewStub].
 * @param modelProvider A lambda for building the EpoxyModel. It expects a single model to be added to the controller
 * receiver. If no model is added the view will be hidden.
 */
class LifecycleAwareEpoxyViewBinder(
    private val lifecycleOwner: LifecycleOwner,
    private val rootView: (() -> View?),
    @IdRes private val viewId: Int,
    private val modelProvider: EpoxyController.() -> Unit,
    private val fallbackToNameLookup: Boolean = false,
    private val useVisibilityTracking: Boolean = false
) : LifecycleObserver {
    private val viewBinder = EpoxyViewBinder()
    private var lazyView: View? = null

    private val visibilityTracker: EpoxyViewBinderVisibilityTracker by lazy {
        EpoxyViewBinderVisibilityTracker().apply {
            this.partialImpressionThresholdPercentage = 100
        }
    }

    val view: View
        get() {
            if (lazyView == null) {
                val nonNullRootView = rootView() ?: error("Fragment view is not created")
                lazyView = nonNullRootView.maybeFindViewByIdName(viewId, fallbackToNameLookup)
                    ?: error(
                        "View could not be found, fallbackToNameLookup: $fallbackToNameLookup," +
                            " view id name: ${nonNullRootView.resources.getResourceEntryName(viewId)}"
                    )
                // There's not yet a way to enforce EpoxyViewStub usage so notify in Bugsnag so we can manually fix.
                if (lazyView !is EpoxyViewStub) {
                    viewBinder.onExceptionSwallowed(
                        IllegalStateException(
                            "View binder should be using EpoxyViewStub. " +
                                "View ID: ${nonNullRootView.resources.getResourceName(viewId)}"
                        )
                    )
                }

                // We register this for view lifecycle callbacks so that we can clear the view when it is destroyed.
                // This both prevents a memory leak, and ensures that if the view is recreated we know we must look up
                // the reference again.
                // We MUST register the observer again each time the view is created because the fragment's viewLifecycleOwner
                // is updated to a new instance for each new fragment view.
                lifecycleOwner.lifecycle.addObserver(this)
            }

            return lazyView!!
        }

    fun addInterceptor(interceptor: EpoxyController.Interceptor) {
        viewBinder.addInterceptor(interceptor)
    }

    fun invalidate() {
        lazyView = viewBinder.replaceView(view, modelProvider).also {
            if (useVisibilityTracking) {
                visibilityTracker.attach(it)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onViewDestroyed() {
        lazyView?.let { viewBinder.unbind(it) }
        lazyView = null
        if (useVisibilityTracking) {
            visibilityTracker.detach()
        }
    }
}

inline fun <reified V : View> View.maybeFindViewByIdName(id: Int, fallbackToNameLookup: Boolean): V? =
    findViewById<V>(id) ?: run {
        if (!fallbackToNameLookup || id == -1) return@run null

        try {
            resources?.getResourceEntryName(id)
        } catch (e: Resources.NotFoundException) {
            Log.e("TODO", "Id not found in ${this::class}, fallbackToNameLookup: $fallbackToNameLookup, error message: ${e.localizedMessage}")
//            throwOrNotify("Id not found in ${this::class}, fallbackToNameLookup: $fallbackToNameLookup, error message: ${e.localizedMessage}")
            null
        }?.let { idName ->
            findViewByIdName<V>(this, idName)
        }
    }
@PublishedApi
internal inline fun <reified V : View> findViewByIdName(view: View, idName: String): V? {
    // The view instance check is not necessary but is done to avoid looking up id name for all views
    // as an optimization
    if (view is V && view.idName == idName) return view

    if (view is ViewGroup) {
        return view.allRecursiveChildren.filterIsInstance<V>()
            .firstOrNull { it.idName == idName }
    }

    return null
}

@PublishedApi
internal val View.idName: String?
    get() = try {
        if (id != -1) resources?.getResourceEntryName(id) else null
    } catch (e: Resources.NotFoundException) {
        null
    }

/**
 * Returns every child view nested at all layers inside this ViewGroup.
 */
val ViewGroup.allRecursiveChildren: Sequence<View>
    get() {
        return children.flatMap {
            sequenceOf(it) + if (it is ViewGroup) it.allRecursiveChildren else emptySequence()
        }
    }
