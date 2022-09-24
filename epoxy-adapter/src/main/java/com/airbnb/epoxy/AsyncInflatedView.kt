package com.airbnb.epoxy

/**
 * Interface to support Async layout inflation with Epoxy
 * This is useful if we want to inflate views asyncronously, thereby improving the first layout time
 *
 * For example, let's say you have a page with first view as a header and then a video. The recycler
 * view will inflate both views before showing the first frame. Since video is a heavy view and take
 * a longer time to inflate, we can inflate it asyncronously and improve the time taken to show the
 * first frame.
 */
interface AsyncInflatedView {
    /**
     * isInflated flag is set to true once the async inflation is completed.
     * It is used to run any methods/runnables that requires the view to be inflated.
     */
    var isInflated: Boolean

    /**
     * pendingRunnables keep a list of runnables, in order, that are waiting for the view to be
     * inflated.
     */
    var pendingRunnables: ArrayList<Runnable>

    /**
     * onInflationComplete method MUST be called after the view is asyncronously inflated.
     * It runs all pending runnables waiting for view inflation.
     */
    fun onInflationComplete() {
        isInflated = true
        for (runnable in pendingRunnables) {
            runnable.run()
        }
        pendingRunnables.clear()
    }

    /**
     * executeWhenInflated method is called by Epoxy to execute a runnable that depend on the
     * inflated view. If the view is already inflated, the runnable will immediately run,
     * otherwise it is added to the list of pending runnables.
     */
    fun executeWhenInflated(runnable: Runnable) {
        if (isInflated) {
            runnable.run()
        } else {
            pendingRunnables.add(runnable)
        }
    }

    /**
     * onViewRecycled method MUST be called when the view is recycled. It clears pending
     * runnable It runs all pending runnables waiting for view inflation.
     */
    fun onViewRecycled() {
        pendingRunnables.clear()
    }
}
