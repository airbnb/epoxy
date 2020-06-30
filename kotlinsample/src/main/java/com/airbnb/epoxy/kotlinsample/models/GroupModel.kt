package com.airbnb.epoxy.kotlinsample.models

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelGroup
import com.airbnb.epoxy.ModelCollector

/**
 * An [EpoxyModelGroup] that implements [ModelCollector] so we can use DSL for model building.
 */
@EpoxyModelClass
abstract class GroupModel : EpoxyModelGroup(), ModelCollector {

    override fun add(model: EpoxyModel<*>) {
        super.models.add(model)
    }
}
