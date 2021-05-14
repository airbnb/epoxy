package com.airbnb.epoxy

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.app.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

/**
 * Shortcut for creating a [LifecycleAwareEpoxyViewBinder] in a lazy way.
 * See [LifecycleAwareEpoxyViewBinder] for pass-through parameter documentation.
 *
 * @param initializer a lambda that is run directly after instantiation of the
 * [LifecycleAwareEpoxyViewBinder].
 */
fun ComponentActivity.epoxyView(
    @IdRes viewId: Int,
    useVisibilityTracking: Boolean = false,
    fallbackToNameLookup: Boolean = false,
    initializer: LifecycleAwareEpoxyViewBinder.() -> Unit = { },
    modelProvider: ModelCollector.(Context) -> Unit
) = lazy {
    return@lazy epoxyViewInternal(
        viewId = viewId,
        useVisibilityTracking = useVisibilityTracking,
        fallbackToNameLookup = fallbackToNameLookup,
        initializer = initializer,
        modelProvider = modelProvider
    )
}

/**
 * Shortcut for creating a [LifecycleAwareEpoxyViewBinder] in a lazy way.
 * See [LifecycleAwareEpoxyViewBinder] for pass-through parameter documentation.
 *
 * @param initializer a lambda that is run directly after instantiation of the
 * [LifecycleAwareEpoxyViewBinder].
 */
fun Fragment.epoxyView(
    @IdRes viewId: Int,
    useVisibilityTracking: Boolean = false,
    fallbackToNameLookup: Boolean = false,
    initializer: LifecycleAwareEpoxyViewBinder.() -> Unit = { },
    modelProvider: ModelCollector.(Context) -> Unit
) = lazy {
    return@lazy epoxyViewInternal(
        viewId = viewId,
        useVisibilityTracking = useVisibilityTracking,
        fallbackToNameLookup = fallbackToNameLookup,
        initializer = initializer,
        modelProvider = modelProvider
    )
}

/**
 * Shortcut for creating a [LifecycleAwareEpoxyViewBinder] in a lazy way.
 * See [LifecycleAwareEpoxyViewBinder] for pass-through parameter documentation.
 *
 * @param initializer a lambda that is run directly after instantiation of the
 * [LifecycleAwareEpoxyViewBinder].
 */
fun ViewGroup.epoxyView(
    @IdRes viewId: Int,
    useVisibilityTracking: Boolean = false,
    fallbackToNameLookup: Boolean = false,
    initializer: LifecycleAwareEpoxyViewBinder.() -> Unit = { },
    modelProvider: ModelCollector.(Context) -> Unit
) = lazy {
    return@lazy epoxyViewInternal(
        viewId = viewId,
        useVisibilityTracking = useVisibilityTracking,
        fallbackToNameLookup = fallbackToNameLookup,
        initializer = initializer,
        modelProvider = modelProvider
    )
}

/**
 * Shortcut for creating a [LifecycleAwareEpoxyViewBinder] in a lazy way.
 * See [LifecycleAwareEpoxyViewBinder] for pass-through parameter documentation.
 *
 * @param initializer a lambda that is run directly after instantiation of the
 * [LifecycleAwareEpoxyViewBinder].
 *
 * @return a view binder or null if a view with the [viewId] could not be found.
 */
fun ComponentActivity.optionalEpoxyView(
    @IdRes viewId: Int,
    useVisibilityTracking: Boolean = false,
    fallbackToNameLookup: Boolean = false,
    initializer: LifecycleAwareEpoxyViewBinder.() -> Unit = { },
    modelProvider: ModelCollector.(Context) -> Unit
) = lazy {
    val view = findViewById<View>(android.R.id.content)
    // View id is not present, we just return null in that case.
    if (view.maybeFindViewByIdName<View>(viewId, fallbackToNameLookup) == null) return@lazy null

    return@lazy epoxyViewInternal(
        viewId = viewId,
        useVisibilityTracking = useVisibilityTracking,
        fallbackToNameLookup = fallbackToNameLookup,
        initializer = initializer,
        modelProvider = modelProvider
    )
}

/**
 * Shortcut for creating a [LifecycleAwareEpoxyViewBinder] in a lazy way.
 * See [LifecycleAwareEpoxyViewBinder] for pass-through parameter documentation.
 *
 * @param initializer a lambda that is run directly after instantiation of the
 * [LifecycleAwareEpoxyViewBinder].
 *
 * @return a view binder or null if a view with the [viewId] could not be found.
 */
fun Fragment.optionalEpoxyView(
    @IdRes viewId: Int,
    useVisibilityTracking: Boolean = false,
    fallbackToNameLookup: Boolean = false,
    initializer: (LifecycleAwareEpoxyViewBinder.() -> Unit) = { },
    modelProvider: ModelCollector.(Context) -> Unit
) = lazy {
    val view = view ?: error("Fragment view has not been created")
    // View id is not present, we just return null in that case.
    if (view.maybeFindViewByIdName<View>(viewId, fallbackToNameLookup) == null) return@lazy null

    return@lazy epoxyViewInternal(
        viewId = viewId,
        useVisibilityTracking = useVisibilityTracking,
        fallbackToNameLookup = fallbackToNameLookup,
        initializer = initializer,
        modelProvider = modelProvider
    )
}

/**
 * Shortcut for creating a [LifecycleAwareEpoxyViewBinder] in a lazy way.
 * See [LifecycleAwareEpoxyViewBinder] for pass-through parameter documentation.
 *
 * @param initializer a lambda that is run directly after instantiation of the
 * [LifecycleAwareEpoxyViewBinder].
 *
 * @return a view binder or null if a view with the [viewId] could not be found.
 */
fun ViewGroup.optionalEpoxyView(
    @IdRes viewId: Int,
    useVisibilityTracking: Boolean = false,
    fallbackToNameLookup: Boolean = false,
    initializer: LifecycleAwareEpoxyViewBinder.() -> Unit = { },
    modelProvider: ModelCollector.(Context) -> Unit
) = lazy {
    val view = this
    // View id is not present, we just return null in that case.
    if (view.maybeFindViewByIdName<View>(viewId, fallbackToNameLookup) == null) return@lazy null

    return@lazy epoxyViewInternal(
        viewId = viewId,
        useVisibilityTracking = useVisibilityTracking,
        fallbackToNameLookup = fallbackToNameLookup,
        initializer = initializer,
        modelProvider = modelProvider
    )
}

private fun ComponentActivity.epoxyViewInternal(
    @IdRes viewId: Int,
    useVisibilityTracking: Boolean = false,
    fallbackToNameLookup: Boolean = false,
    initializer: LifecycleAwareEpoxyViewBinder.() -> Unit,
    modelProvider: ModelCollector.(Context) -> Unit
) = LifecycleAwareEpoxyViewBinder(
    this,
    { findViewById(android.R.id.content) },
    viewId,
    useVisibilityTracking = useVisibilityTracking,
    fallbackToNameLookup = fallbackToNameLookup,
    modelProvider = modelProvider
).apply(initializer)

private fun Fragment.epoxyViewInternal(
    @IdRes viewId: Int,
    useVisibilityTracking: Boolean = false,
    fallbackToNameLookup: Boolean = false,
    initializer: LifecycleAwareEpoxyViewBinder.() -> Unit,
    modelProvider: ModelCollector.(Context) -> Unit
) = LifecycleAwareEpoxyViewBinder(
    viewLifecycleOwner,
    { view },
    viewId,
    useVisibilityTracking = useVisibilityTracking,
    fallbackToNameLookup = fallbackToNameLookup,
    modelProvider = modelProvider
).apply(initializer)

private fun ViewGroup.epoxyViewInternal(
    @IdRes viewId: Int,
    useVisibilityTracking: Boolean = false,
    fallbackToNameLookup: Boolean = false,
    initializer: LifecycleAwareEpoxyViewBinder.() -> Unit,
    modelProvider: ModelCollector.(Context) -> Unit
) = LifecycleAwareEpoxyViewBinder(
    (this.context.unwrapContextForLifecycle() as? LifecycleOwner)
        ?: error("LifecycleOwner required as view's context "),
    { this },
    viewId,
    useVisibilityTracking = useVisibilityTracking,
    fallbackToNameLookup = fallbackToNameLookup,
    modelProvider = modelProvider
).apply(initializer)

/**
 * Attempts to find this view's parent Activity in order to find its lifecycle owner. If this view's
 * `context` is a ContextWrapper it will continually unwrap it until it finds the Activity. If
 * no Activity is found it will return the the view's context.
 */
private fun Context.unwrapContextForLifecycle(): Context {
    var workingContext = this
    while (workingContext is ContextWrapper) {
        if (workingContext is Activity) {
            return workingContext
        }
        workingContext = workingContext.baseContext
    }
    return this
}

/**
 * This class uses an epoxy model to update a view. The view reference is cleared when the fragment
 * is stopped. Call [invalidate] to have the model rebuilt and rebound to the view.
 *
 * @param rootView a lambda returning the parent [ViewGroup] that will be used to search for the
 * [viewId].
 * @param viewId the ID of the view stub that the Epoxy view should replace. This tells the
 * view binder where in the layout to put the view. This should correspond to a view of type
 * [EpoxyViewStub] for state restoration reasons.
 * @param useVisibilityTracking true to get visibility callbacks using a partial impression
 * percentage threshold of 100%, false to not track view visibility. See
 * [EpoxyViewBinderVisibilityTracker] for more information.
 * @param fallbackToNameLookup true to also include searching by the entry name
 * ([Resources.getResourceEntryName]) should the [viewId] not be found. Useful for dynamic features
 * as it's possible the generated ID is not the same should the same view ID exist in both the base
 * APK and the dynamic feature.
 * @param modelProvider a lambda for building the [EpoxyModel]. It expects a single model to be
 * added to the controller receiver. If no model is added the view will be hidden.
 */
class LifecycleAwareEpoxyViewBinder(
    private val lifecycleOwner: LifecycleOwner,
    private val rootView: (() -> View?),
    @IdRes private val viewId: Int,
    private val useVisibilityTracking: Boolean = false,
    private val fallbackToNameLookup: Boolean = false,
    private val modelProvider: ModelCollector.(Context) -> Unit,
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
                val nonNullRootView = rootView() ?: error("Root view is not created")
                lazyView = nonNullRootView.maybeFindViewByIdName(viewId, fallbackToNameLookup)
                    ?: error(
                        "View could not be found, fallbackToNameLookup: $fallbackToNameLookup," +
                            " view id name: ${nonNullRootView.resources.getResourceEntryName(viewId)}"
                    )
                // Propagate an error if a non EpoxyViewStub is used
                if (lazyView !is EpoxyViewStub) {
                    val resourceNameWithFallback = try {
                        nonNullRootView.resources.getResourceName(viewId)
                    } catch (e: Resources.NotFoundException) {
                        "$viewId (name not found)"
                    }
                    viewBinder.onException(
                        IllegalStateException(
                            "View binder should be using EpoxyViewStub. " +
                                "View ID: $resourceNameWithFallback"
                        )
                    )
                }

                // Register this for view lifecycle callbacks so that it can clear the view when it
                // is destroyed. This both prevents a memory leak, and ensures that if the view is
                // recreated it can look up the reference again. This MUST register the observer
                // again each time the view is created because the fragment's viewLifecycleOwner
                // is updated to a new instance for each new fragment view.
                lifecycleOwner.lifecycle.addObserver(this)
            }

            return lazyView!!
        }

    /**
     * Replace or update the [View] with the model produced by the [modelProvider] lambda, depending
     * on if the view for the model is the same as the existing view.
     *
     * @see [EpoxyViewBinder.replaceView]
     */
    fun invalidate() {
        lazyView = viewBinder.replaceView(view, modelProvider).also {
            if (useVisibilityTracking) {
                visibilityTracker.attach(it)
            }
        }
    }

    /**
     * Unbinds the [EpoxyViewBinder]. It is normally not necessary to call this as this object
     * registers itself with the provided [lifecycleOwner].
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onViewDestroyed() {
        lazyView?.let { viewBinder.unbind(it) }
        lazyView = null
        if (useVisibilityTracking) {
            visibilityTracker.detach()
        }
    }
}
