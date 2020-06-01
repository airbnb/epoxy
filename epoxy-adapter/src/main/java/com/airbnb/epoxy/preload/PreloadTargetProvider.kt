package com.airbnb.epoxy.preload

import java.util.ArrayDeque

internal class PreloadTargetProvider<P : PreloadRequestHolder>(
    maxPreload: Int,
    requestHolderFactory: () -> P
) {
    private val queue = ArrayDeque<P>((0 until maxPreload).map { requestHolderFactory() })

    internal fun next(): P {
        val result = queue.poll()
        queue.offer(result)
        result.clear()
        return result
    }

    fun clearAll() {
        queue.forEach { it.clear() }
    }
}

/**
 * This is responsible for holding details for a preloading request.
 * Your implementation can do anything it wants with the request, but it must
 * cancel and clear itself when [clear] is called.
 *
 * It is also recommended that your implementation calls [clear] when your request finishes loading
 * to avoid unnecessarily hanging onto the request result (assuming the result is also stored in
 * cache). Otherwise this holder can be stored in a pool for later use and may leak the preloaded
 * data.
 */
interface PreloadRequestHolder {
    /** Clear any ongoing preload request. */
    fun clear()
}
