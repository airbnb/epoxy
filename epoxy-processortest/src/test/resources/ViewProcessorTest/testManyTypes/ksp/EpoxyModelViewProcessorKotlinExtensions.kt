@file:Suppress("DEPRECATION")

package com.airbnb.epoxy

import kotlin.Suppress
import kotlin.Unit

public inline
    fun ModelCollector.testManyTypesView(modelInitializer: TestManyTypesViewModelBuilder.() -> Unit):
    Unit {
  add(
  TestManyTypesViewModel_().apply {
    modelInitializer()
  }
  )
}
