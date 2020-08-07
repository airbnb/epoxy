package com.airbnb.epoxy

import androidx.annotation.LayoutRes

/**
 * An [EpoxyModelGroup] usable in a DSL manner via the [group] extension.
 * <p>
 * Example:
 * ```
 * group {
 *   id("photos")
 *   layout(R.layout.photo_grid)
 *
 *   // add your models here, example:
 *   for (photo in photos) {
 *     imageView {
 *       id(photo.id)
 *       url(photo.url)
 *     }
 *   }
 * }
 * ```
 */
@EpoxyModelClass
abstract class GroupModel : EpoxyModelGroup, ModelCollector {
    constructor() : super()
    constructor(@LayoutRes layoutRes: Int) : super(layoutRes)

    override fun add(model: EpoxyModel<*>) {
        super.addModel(model)
    }
}
