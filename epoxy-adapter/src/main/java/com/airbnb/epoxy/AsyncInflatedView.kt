package com.airbnb.epoxy

interface AsyncInflatedView {
    var isInflated : Boolean
    var pendingFunctions : ArrayList<Runnable>

    fun onInflationComplete() {
        isInflated = true
        for (runnable in pendingFunctions) {
            runnable.run()
        }
        pendingFunctions.clear()
    }

    fun executeWhenInflated(runnable: Runnable) {
        if (isInflated) {
            runnable.run()
        } else {
            pendingFunctions.add(runnable)
        }
    }
}