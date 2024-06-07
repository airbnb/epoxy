package com.airbnb.epoxy

/**
 * The complete [VisibilityState]s for a single update.
 */
interface AggregateVisibilityState {
    val partiallyVisible: Boolean
    val fullyVisible: Boolean
    val visible: Boolean
    val focusedVisible: Boolean
}
