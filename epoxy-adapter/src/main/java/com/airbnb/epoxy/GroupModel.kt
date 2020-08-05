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
abstract class GroupModel(@LayoutRes layoutRes: Int = 0) : EpoxyModelGroup(layoutRes),
    ModelCollector {

    override fun add(model: EpoxyModel<*>) {
        super.addModel(model)
    }
}
