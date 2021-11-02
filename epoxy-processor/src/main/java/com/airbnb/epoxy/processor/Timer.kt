package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XMessager
import javax.tools.Diagnostic
import kotlin.math.pow
import kotlin.math.roundToInt

class Timer(val name: String) {
    private val timingSteps = mutableListOf<TimingStep>()
    private var startNanos: Long? = null
    private var lastTimingNanos: Long? = null

    fun start() {
        timingSteps.clear()
        startNanos = System.nanoTime()
        lastTimingNanos = startNanos
    }

    fun markStepCompleted(stepDescription: String) {
        val nowNanos = System.nanoTime()
        val lastNanos = lastTimingNanos ?: error("Timer was not started")
        lastTimingNanos = nowNanos

        timingSteps.add(TimingStep(nowNanos - lastNanos, stepDescription))
    }

    fun finishAndPrint(messager: XMessager) {
        val start = startNanos ?: error("Timer was not started")
        val message = buildString {
            appendLine("$name finished in ${formatNanos(System.nanoTime() - start)}")
            timingSteps.forEach { step ->
                appendLine(" - ${step.description} (${formatNanos(step.durationNanos)})")
            }
        }

        messager.printMessage(Diagnostic.Kind.WARNING, message)
    }

    private class TimingStep(val durationNanos: Long, val description: String)

    private fun formatNanos(nanos: Long): String {
        val diffMs = nanos.div(1_000_000.0).roundTo(3)
        return "$diffMs ms"
    }

    private fun Double.roundTo(numFractionDigits: Int): Double {
        val factor = 10.0.pow(numFractionDigits.toDouble())
        return (this * factor).roundToInt() / factor
    }
}
