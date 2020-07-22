package com.airbnb.epoxy

/**
 * An [EpoxyModelGroup] usable in DSL.
 */
@EpoxyModelClass
abstract class GroupModel : EpoxyModelGroup(), ModelCollector {

    override fun add(model: EpoxyModel<*>) {
        super.addModel(model)
    }
}
