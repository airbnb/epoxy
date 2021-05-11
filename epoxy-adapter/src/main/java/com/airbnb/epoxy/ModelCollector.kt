package com.airbnb.epoxy

/**
 * Interface used to collect models. Used by [EpoxyController]. It is also convenient to build DSL
 * helpers for carousel: @link https://github.com/airbnb/epoxy/issues/847.
 */
@EpoxyBuildScope
interface ModelCollector {

    fun add(model: EpoxyModel<*>)
}
