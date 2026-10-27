package ar.edu.unmdp.entrenamiento.tuner

import ar.edu.unmdp.entrenamiento.voice.PitchEstimate
import kotlin.math.abs

enum class GuidedPhase { READY, FIRST_PASS, REVIEW, CORRECTION, COMPLETE, INTERRUPTED }
data class GuidedStringResult(val number: Int, val midi: Int, val passed: Boolean, val cents: Double?)
data class GuidedTunerState(
    val phase: GuidedPhase = GuidedPhase.READY,
    val setup: TunerSetupSnapshot? = null,
    val finalReview: Boolean = true,
    val measurement: TunerState = TunerState(),
    val firstPass: List<GuidedStringResult> = emptyList(),
    val review: List<GuidedStringResult> = emptyList(),
    val corrections: List<GuidedStringResult> = emptyList(),
    val pending: Set<Int> = emptySet(),
    val farWarning: Boolean = false,
) {
    val active get() = phase in setOf(GuidedPhase.FIRST_PASS, GuidedPhase.REVIEW, GuidedPhase.CORRECTION)
    val checked get() = phase == GuidedPhase.COMPLETE && finalReview && pending.isEmpty() && review.size == 6
}

/** Reglas puras. guided-distance-v1: 700/500 cents, 400 ms; hueco máximo 250 ms. */
class GuidedTunerSession {
    private val meter = TunerSession()
    var state = GuidedTunerState()
        private set
    private var farSince: Long? = null
    private var lastTime: Long? = null
    fun begin(snapshot: TunerSetupSnapshot, finalReview: Boolean) {
        meter.configure(snapshot)
        state = GuidedTunerState(phase = GuidedPhase.FIRST_PASS, setup = meter.setup, finalReview = finalReview)
        target(6)
    }
    private fun target(number: Int) {
        meter.fixString(number); meter.start()
        farSince = null; lastTime = null
        state = state.copy(measurement = meter.state, farWarning = false)
    }
    fun measure(estimate: PitchEstimate, timeMillis: Long) {
        if (!state.active || !state.measurement.running) return
        meter.measure(estimate, timeMillis)
        val cents = meter.state.cents
        val continuous = lastTime?.let { timeMillis > it && timeMillis - it <= 250 } ?: false
        if (!continuous) farSince = null
        var warning = state.farWarning && continuous
        if (cents == null || abs(cents) <= 500) { farSince = null; warning = false }
        else if (abs(cents) >= 700) {
            if (farSince == null) farSince = timeMillis
            if (timeMillis - requireNotNull(farSince) >= 400) warning = true
        } else farSince = null
        lastTime = timeMillis
        state = state.copy(measurement = meter.state, farWarning = warning)
        if (meter.state.inTune) finishCurrent(true)
    }
    /** En revisión, un objetivo sin aprobación queda pendiente, conservando lectura válida o null. */
    fun leavePending() { if (state.phase == GuidedPhase.REVIEW) finishCurrent(false) }
    private fun finishCurrent(passed: Boolean) {
        val number = requireNotNull(meter.state.fixedString)
        val result = GuidedStringResult(number, requireNotNull(meter.state.targetMidi), passed, meter.state.cents)
        when (state.phase) {
            GuidedPhase.FIRST_PASS -> state = state.copy(firstPass = state.firstPass + result)
            GuidedPhase.REVIEW -> state = state.copy(review = state.review + result,
                pending = if (passed) state.pending else state.pending + number)
            GuidedPhase.CORRECTION -> state = state.copy(corrections = state.corrections + result, pending = state.pending - number)
            else -> return
        }
        if (state.phase == GuidedPhase.CORRECTION) {
            state.pending.maxOrNull()?.let { target(it); return }
        } else if (number > 1) { target(number - 1); return }
        else if (state.phase == GuidedPhase.FIRST_PASS && state.finalReview) {
            state = state.copy(phase = GuidedPhase.REVIEW); target(6); return
        } else if (state.phase == GuidedPhase.REVIEW && state.pending.isNotEmpty()) {
            state = state.copy(phase = GuidedPhase.CORRECTION); target(state.pending.max()); return
        }
        meter.stop()
        state = state.copy(phase = GuidedPhase.COMPLETE, measurement = meter.state, farWarning = false)
    }
    fun interrupt() {
        if (!state.active) return
        meter.stop(); farSince = null; lastTime = null
        state = state.copy(phase = GuidedPhase.INTERRUPTED, measurement = meter.state, farWarning = false)
    }
    fun suspendMeasurement() {
        if (!state.active) return
        meter.stop(); farSince = null; lastTime = null
        state = state.copy(measurement = meter.state, farWarning = false)
    }
    fun resumeMeasurement() {
        if (!state.active) return
        meter.start(); farSince = null; lastTime = null
        state = state.copy(measurement = meter.state, farWarning = false)
    }
}
