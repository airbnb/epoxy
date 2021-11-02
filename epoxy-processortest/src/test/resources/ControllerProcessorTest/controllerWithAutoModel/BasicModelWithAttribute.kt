package com.airbnb.epoxy

open class BasicModelWithAttribute : EpoxyModel<Any?>() {
    @EpoxyAttribute
    var value = 0

    override fun getDefaultLayout(): Int {
        return 0
    }
}
