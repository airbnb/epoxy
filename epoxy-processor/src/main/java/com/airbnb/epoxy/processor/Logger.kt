package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XMessager
import com.airbnb.epoxy.processor.Utils.buildEpoxyException
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Collections
import java.util.Stack
import javax.tools.Diagnostic

class Logger(val messager: XMessager, val logTimings: Boolean) {

    private val timings = mutableListOf<Timing>()
    private val currentTimingBlocks = Stack<MutableList<Timing>>()

    private val loggedExceptions: MutableList<Throwable> =
        Collections.synchronizedList(mutableListOf())

    fun writeExceptions() {
        loggedExceptions.forEach {
            val element = (it as? EpoxyProcessorException)?.element
            val msg = "${it.javaClass.simpleName}: ${it.localizedMessage}\n${it.stackTraceString()}"

            if (element != null) {
                messager.printMessage(
                    kind = Diagnostic.Kind.ERROR,
                    msg = msg,
                    element = element
                )
            } else {
                messager.printMessage(
                    kind = Diagnostic.Kind.ERROR,
                    msg = msg,
                )
            }
        }
    }

    private fun Throwable.stackTraceString(): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }

    fun warn(msg: String) {
        messager.printMessage(Diagnostic.Kind.WARNING, msg)
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

    fun logError(element: XElement, msg: String, vararg args: Any) {
        logError(buildEpoxyException(element, msg, *args))
    }

    fun logError(msg: String, element: XElement) {
        logError(EpoxyProcessorException(msg, element = element))
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
    @JvmOverloads
    fun logError(e: Throwable, message: String? = null) {
        loggedExceptions.add(message?.let { EpoxyProcessorException(it, e) } ?: e)
    }

    fun note(message: String) {
        // These don't show up in gradle builds unless --debug verbosity is used, which makes them
        // not so useful :(. We often use warn instead for that reason
        messager.printMessage(Diagnostic.Kind.NOTE, "$message\n ")
    }

    fun <T> measure(
        name: String,
        numItems: Int? = null,
        isParallel: Boolean? = null,
        block: () -> T
    ): T {
        if (!logTimings) return block()
        currentTimingBlocks.add(mutableListOf())

        val start = System.nanoTime()
        val result = block()
        val elapsed = (System.nanoTime() - start) / 1_000_000

        val timing = Timing(
            name = name,
            durationMs = elapsed,
            nestedTimings = currentTimingBlocks.pop(),
            itemCount = numItems,
            isParallel = isParallel
        )

        (currentTimingBlocks.lastOrNull() ?: timings).add(timing)

        return result
    }

    fun printTimings(processorName: String) {
        if (!logTimings) return

        val timingString = timings.joinToString(nesting = 1)
        val totalDuration = timings.sumOf { it.durationMs.toInt() }
        warn(
            "$processorName completed in $totalDuration ms:\n$timingString\n "
        )
    }
}

private fun List<Timing>.joinToString(nesting: Int) = joinToString("") { it.toString(nesting) }

data class Timing(
    val name: String,
    val durationMs: Long,
    val nestedTimings: List<Timing>,
    val itemCount: Int? = null,
    val isParallel: Boolean? = null
) {
    fun toString(nesting: Int = 0): String {
        if (durationMs == 0L) return ""

        val parallel = if (isParallel == true) "in parallel" else ""
        val items = if (itemCount != null) "($itemCount items $parallel)" else ""
        val indent = "  ".repeat(nesting)
        return "$indent$name: $durationMs ms $items\n${nestedTimings.joinToString(nesting + 1)}"
    }
}
