package com.airbnb.epoxy

/**
 * Interface to support Async layout inflation with EPoxy
 */
interface AsyncInflatedView {
    /**
     * isInflated flag is set to true once the async inflation is completed.
     * It is used to run any methods/runnables that requires the view to be inflated.
     */
    var isInflated : Boolean

    /**
     * pendingRunnables keep a list of runnables, in order, that are waiting for the view to be
     * inflated.
     */
    var pendingRunnables : ArrayList<Runnable>

    /**
     * onInflationComplete method that MUST be called after the view is asyncronously inflated.
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
     * executeWhenInflated method is called by EPoxy to execute a runnable that depend on the
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
}