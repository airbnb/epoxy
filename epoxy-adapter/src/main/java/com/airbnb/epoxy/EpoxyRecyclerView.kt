package com.airbnb.epoxy

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.annotation.Px
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.preload.EpoxyModelPreloader
import com.airbnb.epoxy.preload.EpoxyPreloader
import com.airbnb.epoxy.preload.PreloadErrorHandler
import com.airbnb.epoxy.preload.PreloadRequestHolder
import com.airbnb.epoxy.preload.ViewMetadata
import com.airbnb.viewmodeladapter.R

/**
 * A RecyclerView implementation that makes for easier integration with Epoxy. The goal of this
 * class is to reduce boilerplate in setting up a RecyclerView by applying common defaults.
 * Additionally, several performance optimizations are made.
 *
 * Improvements in this class are:
 *
 * 1. A single view pool is automatically shared between all [EpoxyRecyclerView] instances in
 * the same activity. This should increase view recycling potential and increase performance when
 * nested RecyclerViews are used. See [.initViewPool].
 *
 * 2. A layout manager is automatically added with assumed defaults. See [createLayoutManager]
 *
 * 3. Fixed size is enabled if this view's size is MATCH_PARENT
 *
 * 4. If a [GridLayoutManager] is used this will automatically sync the span count with the
 * [EpoxyController]. See [syncSpanCount]
 *
 * 5. Helper methods like [withModels], [setModels], [buildModelsWith]
 * make it simpler to set up simple RecyclerViews.
 *
 * 6. Set an EpoxyController and build models in one step -
 * [setControllerAndBuildModels] or [withModels]
 *
 * 7. Support for automatic item spacing. See [.setItemSpacingPx]
 *
 * 8. Defaults for usage as a nested recyclerview are provided in [Carousel].
 *
 * 9. [setClipToPadding] is set to false by default since that behavior is commonly
 * desired in a scrolling list
 */
open class EpoxyRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    protected val spacingDecorator = EpoxyItemSpacingDecorator()

    private var epoxyController: EpoxyController? = null

    /**
     * The adapter that was removed because the RecyclerView was detached from the window. We save it
     * so we can reattach it if the RecyclerView is reattached to window. This allows us to
     * automatically restore the adapter, without risking leaking the RecyclerView if this view is
     * never used again.
     *
     * Since the adapter is removed this recyclerview won't get adapter changes, but that's fine since
     * the view isn't attached to window and isn't being drawn.
     *
     * This reference is cleared if another adapter is manually set, so we don't override the user's
     * adapter choice.
     *
     * @see .setRemoveAdapterWhenDetachedFromWindow
     */
    private var removedAdapter: RecyclerView.Adapter<*>? = null

    private var removeAdapterWhenDetachedFromWindow = true

    private var delayMsWhenRemovingAdapterOnDetach: Int = DEFAULT_ADAPTER_REMOVAL_DELAY_MS

    /**
     * Tracks whether [.removeAdapterRunnable] has been posted to run
     * later. This lets us know if we should cancel the runnable at certain times. This removes the
     * overhead of needlessly attempting to remove the runnable when it isn't posted.
     */
    private var isRemoveAdapterRunnablePosted: Boolean = false
    private val removeAdapterRunnable = Runnable {
        if (isRemoveAdapterRunnablePosted) {
            // Canceling a runnable doesn't work accurately when a view switches between
            // attached/detached, so we manually check that this should still be run
            isRemoveAdapterRunnablePosted = false
            removeAdapter()
        }
    }

    private val preloadScrollListeners: MutableList<EpoxyPreloader<*>> = mutableListOf()

    private val preloadConfigs: MutableList<PreloadConfig<*, *, *>> = mutableListOf()

    private class PreloadConfig<T : EpoxyModel<*>, U : ViewMetadata?, P : PreloadRequestHolder>(
        val maxPreload: Int,
        val errorHandler: PreloadErrorHandler,
        val preloader: EpoxyModelPreloader<T, U, P>,
        val requestHolderFactory: () -> P
    )

    /**
     * Setup a preloader to fetch content for a model's view before it is bound.
     * This can be called multiple times if you would like to add separate preloaders
     * for different models or content types.
     *
     * Preloaders are automatically attached and run, and are updated if the adapter changes.
     *
     * @param maxPreloadDistance How many items to prefetch ahead of the last bound item
     * @param errorHandler Called when the preloader encounters an exception. We recommend throwing an
     * exception in debug builds, and logging an error in production.
     * @param preloader Describes how view content for the EpoxyModel should be preloaded
     * @param requestHolderFactory Should create and return a new [PreloadRequestHolder] each time it is invoked
     */
    fun <T : EpoxyModel<*>, U : ViewMetadata?, P : PreloadRequestHolder> addPreloader(
        maxPreloadDistance: Int = 3,
        errorHandler: PreloadErrorHandler,
        preloader: EpoxyModelPreloader<T, U, P>,
        requestHolderFactory: () -> P
    ) {
        preloadConfigs.add(
            PreloadConfig(
                maxPreloadDistance,
                errorHandler,
                preloader,
                requestHolderFactory
            )
        )

        updatePreloaders()
    }

    /**
     * Clears all preloaders added with [addPreloader]
     */
    fun clearPreloaders() {
        preloadConfigs.clear()
        updatePreloaders()
    }

    private fun updatePreloaders() {
        preloadScrollListeners.forEach { removeOnScrollListener(it) }
        preloadScrollListeners.clear()
        val currAdapter = adapter ?: return

        preloadConfigs.forEach { preloadConfig ->

            if (currAdapter is EpoxyAdapter) {
                EpoxyPreloader.with(
                    currAdapter,
                    preloadConfig.requestHolderFactory,
                    preloadConfig.errorHandler,
                    preloadConfig.maxPreload,
                    listOf(preloadConfig.preloader)
                )
            } else {
                epoxyController?.let {
                    EpoxyPreloader.with(
                        it,
                        preloadConfig.requestHolderFactory,
                        preloadConfig.errorHandler,
                        preloadConfig.maxPreload,
                        listOf(preloadConfig.preloader)
                    )
                }
            }?.let {
                preloadScrollListeners.add(it)
                addOnScrollListener(it)
            }
        }
    }

    /**
     * If set to true, any adapter set on this recyclerview will be removed when this view is detached
     * from the window. This is useful to prevent leaking a reference to this RecyclerView. This is
     * useful in cases where the same adapter can be used across multiple views (views which can be
     * destroyed and recreated), such as with fragments. In that case the adapter is not necessarily
     * cleared from previous RecyclerViews, so the adapter will continue to hold a reference to those
     * views and leak them. More details at https://github.com/airbnb/epoxy/wiki/Avoiding-Memory-Leaks#parent-view
     *
     * The default is true, but you can disable this if you don't want your adapter detached
     * automatically.
     *
     * If the adapter is removed via this setting, it will be re-set on the RecyclerView if the
     * RecyclerView is re-attached to the window at a later point.
     */
    fun setRemoveAdapterWhenDetachedFromWindow(removeAdapterWhenDetachedFromWindow: Boolean) {
        this.removeAdapterWhenDetachedFromWindow = removeAdapterWhenDetachedFromWindow
    }

    /**
     * If [.setRemoveAdapterWhenDetachedFromWindow] is set to true, this is the delay
     * in milliseconds between when [.onDetachedFromWindow] is called and when the adapter is
     * actually removed.
     *
     * By default a delay of {@value #DEFAULT_ADAPTER_REMOVAL_DELAY_MS} ms is used so that view
     * transitions can complete before the adapter is removed. Otherwise if the adapter is removed
     * before transitions finish it can clear the screen and break the transition. A notable case is
     * fragment transitions, in which the fragment view is detached from window before the transition
     * ends.
     */
    fun setDelayMsWhenRemovingAdapterOnDetach(delayMsWhenRemovingAdapterOnDetach: Int) {
        this.delayMsWhenRemovingAdapterOnDetach = delayMsWhenRemovingAdapterOnDetach
    }

    init {

        if (attrs != null) {
            val a = context.obtainStyledAttributes(
                attrs, R.styleable.EpoxyRecyclerView,
                defStyleAttr, 0
            )
            setItemSpacingPx(
                a.getDimensionPixelSize(
                    R.styleable.EpoxyRecyclerView_itemSpacing,
                    0
                )
            )
            a.recycle()
        }

        init()
    }

    @CallSuper
    protected open fun init() {
        clipToPadding = false
        initViewPool()
    }

    /**
     * Get or create a view pool to use for this RecyclerView. By default the same pool is shared for
     * all [EpoxyRecyclerView] usages in the same Activity.
     *
     * @see .createViewPool
     * @see .shouldShareViewPoolAcrossContext
     */
    private fun initViewPool() {
        if (!shouldShareViewPoolAcrossContext()) {
            setRecycledViewPool(createViewPool())
            return
        }

        setRecycledViewPool(
            ACTIVITY_RECYCLER_POOL.getPool(
                getContextForSharedViewPool()
            ) { createViewPool() }.viewPool
        )
    }

    /**
     * Attempts to find this view's parent Activity in order to share the view pool. If this view's
     * `context` is a ContextWrapper it will continually unwrap it until it finds the Activity. If
     * no Activity is found it will return the the view's context.
     */
    private fun getContextForSharedViewPool(): Context {
        var workingContext = this.context
        while (workingContext is ContextWrapper) {
            if (workingContext is Activity) {
                return workingContext
            }
            workingContext = workingContext.baseContext
        }
        return this.context
    }

    /**
     * Create a new instance of a view pool to use with this recyclerview. By default a [ ] is used.
     */
    protected open fun createViewPool(): RecyclerView.RecycledViewPool {
        return UnboundedViewPool()
    }

    /**
     * To maximize view recycling by default we share the same view pool across all instances in the same Activity. This behavior can be disabled by returning
     * false here.
     */
    open fun shouldShareViewPoolAcrossContext(): Boolean {
        return true
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams) {
        val isFirstParams = layoutParams == null
        super.setLayoutParams(params)

        if (isFirstParams) {
            // Set a default layout manager if one was not set via xml
            // We need layout params for this to guess at the right size and type
            if (layoutManager == null) {
                layoutManager = createLayoutManager()
            }
        }
    }

    /**
     * Create a new [androidx.recyclerview.widget.RecyclerView.LayoutManager]
     * instance to use for this RecyclerView.
     *
     * By default a LinearLayoutManager is used, and a reasonable default is chosen for scrolling
     * direction based on layout params.
     *
     * If the RecyclerView is set to match parent size then the scrolling orientation is set to
     * vertical and [.setHasFixedSize] is set to true.
     *
     * If the height is set to wrap_content then the scrolling orientation is set to horizontal, and
     * [.setClipToPadding] is set to false.
     */
    protected open fun createLayoutManager(): RecyclerView.LayoutManager {
        val layoutParams = layoutParams

        // 0 represents matching constraints in a LinearLayout or ConstraintLayout
        if (layoutParams.height == RecyclerView.LayoutParams.MATCH_PARENT || layoutParams.height == 0) {

            if (layoutParams.width == RecyclerView.LayoutParams.MATCH_PARENT || layoutParams.width == 0) {
                // If we are filling as much space as possible then we usually are fixed size
                setHasFixedSize(true)
            }

            // A sane default is a vertically scrolling linear layout
            return LinearLayoutManager(context)
        } else {
            // This is usually the case for horizontally scrolling carousels and should be a sane
            // default
            return LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun setLayoutManager(layout: RecyclerView.LayoutManager?) {
        super.setLayoutManager(layout)
        syncSpanCount()
    }

    /**
     * If a grid layout manager is set we sync the span count between the layout and the epoxy
     * adapter automatically.
     */
    private fun syncSpanCount() {
        val layout = layoutManager
        val controller = epoxyController
        if (layout is GridLayoutManager && controller != null) {

            if (controller.spanCount != layout.spanCount || layout.spanSizeLookup !== controller.spanSizeLookup) {
                controller.spanCount = layout.spanCount
                layout.spanSizeLookup = controller.spanSizeLookup
            }
        }
    }

    override fun requestLayout() {
        // Grid layout manager calls this when the span count is changed. Its the easiest way to
        // detect a span count change and update our controller accordingly.
        syncSpanCount()
        super.requestLayout()
    }

    fun setItemSpacingRes(@DimenRes itemSpacingRes: Int) {
        setItemSpacingPx(resToPx(itemSpacingRes))
    }

    fun setItemSpacingDp(@Dimension(unit = Dimension.DP) dp: Int) {
        setItemSpacingPx(dpToPx(dp))
    }

    /**
     * Set a pixel value to use as spacing between items. If this is a positive number an item
     * decoration will be added to space all items this far apart from each other. If the value is 0
     * or negative no extra spacing will be used, and any previous spacing will be removed.
     *
     * This only works if a [LinearLayoutManager] or [GridLayoutManager] is used with this
     * RecyclerView.
     *
     * This can also be set via the `app:itemSpacing` styleable attribute.
     *
     * @see .setItemSpacingDp
     * @see .setItemSpacingRes
     */
    open fun setItemSpacingPx(@Px spacingPx: Int) {
        removeItemDecoration(spacingDecorator)
        spacingDecorator.pxBetweenItems = spacingPx

        if (spacingPx > 0) {
            addItemDecoration(spacingDecorator)
        }
    }

    /**
     * Set a list of [EpoxyModel]'s to show in this RecyclerView.
     *
     * Alternatively you can set an [EpoxyController] to handle building models dynamically.
     *
     * @see withModels
     * @see setController
     * @see setControllerAndBuildModels
     * @see buildModelsWith
     */

    open fun setModels(models: List<EpoxyModel<*>>) {
        val controller = (epoxyController as? SimpleEpoxyController)
            ?: SimpleEpoxyController().also {
                setController(it)
            }

        controller.setModels(models)
    }

    /**
     * Set an EpoxyController to populate this RecyclerView. This does not make the controller build
     * its models, that must be done separately via [requestModelBuild].
     *
     * Use this if you don't want [requestModelBuild] called automatically. Common cases
     * are if you are using [TypedEpoxyController] (in which case you must call setData on the
     * controller), or if you have not otherwise populated your controller's data yet.
     *
     * Otherwise if you want models built automatically for you use [setControllerAndBuildModels]
     *
     * The controller can be cleared with [clear]
     *
     * @see .setControllerAndBuildModels
     * @see .buildModelsWith
     * @see .setModels
     */

    fun setController(controller: EpoxyController) {
        epoxyController = controller
        adapter = controller.adapter
        syncSpanCount()
    }

    /**
     * Set an EpoxyController to populate this RecyclerView, and tell the controller to build
     * models.
     *
     * The controller can be cleared with [clear]
     *
     * @see setController
     * @see buildModelsWith
     * @see setModels
     */
    fun setControllerAndBuildModels(controller: EpoxyController) {
        controller.requestModelBuild()
        setController(controller)
    }

    /**
     * The simplest way to add models to the RecyclerView without needing to create an EpoxyController.
     * This is intended for Kotlin usage, and has the EpoxyController as the lambda receiver so
     * models can be added easily.
     *
     * Multiple calls to this will reuse the same underlying EpoxyController so views in the
     * RecyclerView will be reused.
     *
     * The Java equivalent is [buildModelsWith].
     */
    fun withModels(buildModels: EpoxyController.() -> Unit) {
        val controller = (epoxyController as? WithModelsController)
            ?: WithModelsController().also { setController(it) }

        controller.callback = buildModels
        controller.requestModelBuild()
    }

    private class WithModelsController : EpoxyController() {
        var callback: EpoxyController.() -> Unit = {}

        override fun buildModels() {
            callback(this)
        }
    }

    /**
     * Allows you to build models via a callback instead of needing to create a new EpoxyController
     * class. This is useful if your models are simple and you would like to simply declare them in
     * your activity/fragment.
     *
     * Multiple calls to this will reuse the same underlying EpoxyController so views in the
     * RecyclerView will be reused.
     *
     * Another useful pattern is having your Activity or Fragment implement [ModelBuilderCallback].
     *
     * If you're using Kotlin, prefer [withModels].
     *
     * @see setController
     * @see setControllerAndBuildModels
     * @see setModels
     */
    fun buildModelsWith(callback: ModelBuilderCallback) {
        val controller = (epoxyController as? ModelBuilderCallbackController)
            ?: ModelBuilderCallbackController().also { setController(it) }

        controller.callback = callback
        controller.requestModelBuild()
    }

    private class ModelBuilderCallbackController : EpoxyController() {
        var callback: ModelBuilderCallback = object : ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
            }
        }

        override fun buildModels() {
            callback.buildModels(this)
        }
    }

    /**
     * A callback for creating models without needing a custom EpoxyController class. Used with [buildModelsWith]
     */
    interface ModelBuilderCallback {
        /**
         * Analagous to [EpoxyController.buildModels]. You should create new model instances and
         * add them to the given controller. [AutoModel] cannot be used with models added this
         * way.
         */
        fun buildModels(controller: EpoxyController)
    }

    /**
     * Request that the currently set EpoxyController has its models rebuilt. You can use this to
     * avoid saving your controller as a field.
     *
     * You cannot use this if your controller is a [TypedEpoxyController] or if you set
     * models via [setModels]. In that case you must set data directly on the
     * controller or set models again.
     */
    fun requestModelBuild() {
        if (epoxyController == null) {
            throw IllegalStateException("A controller must be set before requesting a model build.")
        }

        if (epoxyController is SimpleEpoxyController) {
            throw IllegalStateException("Models were set with #setModels, they can not be rebuilt.")
        }

        epoxyController!!.requestModelBuild()
    }

    /**
     * Clear the currently set EpoxyController or Adapter as well as any models that are displayed.
     *
     * Any pending requests to the EpoxyController to build models are canceled.
     *
     * Any existing child views are recycled to the view pool.
     */
    open fun clear() {
        // The controller is cleared so the next time models are set we can create a fresh one.
        epoxyController?.cancelPendingModelBuild()
        epoxyController = null

        // We use swapAdapter instead of setAdapter so that the view pool is not cleared.
        // 'removeAndRecycleExistingViews=true' is used in case this is a nested recyclerview
        // and we want to recycle the views back to a shared view pool
        swapAdapter(null, true)
    }

    @Px
    protected fun dpToPx(@Dimension(unit = Dimension.DP) dp: Int): Int {
        return TypedValue
            .applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
                resources.displayMetrics
            ).toInt()
    }

    @Px
    protected fun resToPx(@DimenRes itemSpacingRes: Int): Int {
        return resources.getDimensionPixelOffset(itemSpacingRes)
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        super.setAdapter(adapter)

        clearRemovedAdapterAndCancelRunnable()
        updatePreloaders()
    }

    override fun swapAdapter(
        adapter: RecyclerView.Adapter<*>?,
        removeAndRecycleExistingViews: Boolean
    ) {
        super.swapAdapter(adapter, removeAndRecycleExistingViews)

        clearRemovedAdapterAndCancelRunnable()
        updatePreloaders()
    }

    public override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (removedAdapter != null) {
            // Restore the adapter that was removed when the view was detached from window
            swapAdapter(removedAdapter, false)
        }
        clearRemovedAdapterAndCancelRunnable()
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        preloadScrollListeners.forEach { it.cancelPreloadRequests() }

        if (removeAdapterWhenDetachedFromWindow) {
            if (delayMsWhenRemovingAdapterOnDetach > 0) {

                isRemoveAdapterRunnablePosted = true
                postDelayed(removeAdapterRunnable, delayMsWhenRemovingAdapterOnDetach.toLong())
            } else {
                removeAdapter()
            }
        }
        clearPoolIfActivityIsDestroyed()
    }

    private fun removeAdapter() {
        val currentAdapter = adapter
        if (currentAdapter != null) {
            // Clear the adapter so the adapter releases its reference to this RecyclerView.
            // Views are recycled so they can return to a view pool (default behavior is to not recycle
            // them).
            swapAdapter(null, true)
            // Keep a reference to the removed adapter so we can add it back if the recyclerview is
            // attached again.
            removedAdapter = currentAdapter
        }

        // Do this after clearing the adapter, since that sends views back to the pool
        clearPoolIfActivityIsDestroyed()
    }

    private fun clearRemovedAdapterAndCancelRunnable() {
        removedAdapter = null
        if (isRemoveAdapterRunnablePosted) {
            removeCallbacks(removeAdapterRunnable)
            isRemoveAdapterRunnablePosted = false
        }
    }

    private fun clearPoolIfActivityIsDestroyed() {
        // Views in the pool hold context references which can keep the activity from being GC'd,
        // plus they can hold significant memory resources. We should clear it asap after the pool
        // is no longer needed - the main signal we use for this is that the activity is destroyed.
        if (context.isActivityDestroyed()) {
            recycledViewPool.clear()
        }
    }

    companion object {
        private const val DEFAULT_ADAPTER_REMOVAL_DELAY_MS = 2000

        /**
         * Store one unique pool per activity. They are cleared out when activities are destroyed, so this
         * only needs to hold pools for active activities.
         */
        private val ACTIVITY_RECYCLER_POOL = ActivityRecyclerPool()
    }
}
