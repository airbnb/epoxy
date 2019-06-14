package com.airbnb.epoxy

/**
 * Exposes package private things as internal so files in other packages can use them.
 */

internal fun EpoxyViewHolder.objectToBindInternal() = objectToBind()

internal fun EpoxyModel<*>.viewTypeInternal() = viewType
internal fun BaseEpoxyAdapter.boundViewHoldersInternal() = boundViewHolders
internal fun BaseEpoxyAdapter.getModelForPositionInternal(position: Int): EpoxyModel<*>? {
    return getModelForPosition(position)
}
