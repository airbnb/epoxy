package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XElement

internal class EpoxyProcessorException(
    message: String,
    cause: Throwable? = null,
    val element: XElement? = null
) : RuntimeException(message, cause)
