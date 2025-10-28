package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XElement

internal class EpoxyProcessorException(
    message: String,
    cause: Throwable? = null,
    element: XElement? = null
) : RuntimeException(buildMessage(message, element), cause) {

    companion object {
        private fun buildMessage(message: String, element: XElement?): String {
            if (element == null) return message

            // Extract element metadata immediately while PSI is still valid.
            // In KSP2, accessing elements in finish() callback throws:
            // "Access to invalid KotlinAlwaysAccessibleLifetimeToken: PSI has changed since creation"
            val elementInfo = buildString {
                append(" [element=${element.name}")

                element.enclosingElement?.let { enclosing ->
                    append(" in ${enclosing.name}")
                }

                append("]")
            }

            return message + elementInfo
        }
    }
}
