package com.airbnb.epoxy

import com.airbnb.epoxy.Utils.buildEpoxyException
import javax.annotation.processing.Messager
import javax.tools.Diagnostic

class ErrorLogger {

    private val loggedExceptions: MutableList<Exception> = mutableListOf()

    fun writeExceptions(messager: Messager) {
        loggedExceptions.forEach { messager.printMessage(Diagnostic.Kind.ERROR, it.toString()) }
    }

    fun logErrors(exceptions: List<Exception>) {
        exceptions.forEach { logError(it) }
    }

    /**
     * Errors logged with this should describe exactly what is wrong. These won't show the stacktrace
     * in the error output to reduce confusion.
     */
    fun logError(msg: String, vararg args: Any) {
        logError(buildEpoxyException(msg, *args))
    }

    /**
     * Errors are logged and saved until after classes are generating. Otherwise if we throw
     * immediately the models are not generated which leads to lots of other compiler errors which
     * mask the actual issues.
     *
     *
     * If the exception is not an [EpoxyProcessorException] then the stacktrace will be shown in
     * the output.
     */
    @JvmOverloads fun logError(e: Exception, message: String = "") {
        logEpoxyError(e as? EpoxyProcessorException ?: EpoxyProcessorException(e, message))
    }

    private fun logEpoxyError(e: EpoxyProcessorException) {
        loggedExceptions += e
    }
}
