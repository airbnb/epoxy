package com.airbnb.epoxy.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName

object KClassNames {

    // Annotations
    val DEPRECATED = Deprecated::class.asClassName()
    val KOTLIN_UNIT = ClassName.bestGuess("kotlin.Unit")
}
