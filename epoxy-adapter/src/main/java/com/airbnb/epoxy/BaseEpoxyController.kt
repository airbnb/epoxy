package com.airbnb.epoxy

abstract class BaseEpoxyController {
    protected abstract fun buildModels()
    abstract fun add(model: EpoxyModel<*>)
}