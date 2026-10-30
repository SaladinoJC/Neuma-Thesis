package ar.edu.unmdp.entrenamiento.voice

import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.pow
import kotlinx.serialization.Serializable

@Serializable
enum class VocalIntervalPhase { READY_ANCHOR, ANCHOR, READY_TARGET, TARGET, COMPLETED, INTERRUPTED }

@Serializable
enum class VocalAnchorSource { SUNG, GIVEN }

/** Experimental parameters, not a calibrated criterion for vocal proficiency. */
@Serializable
data class VocalIntervalConfiguration(
    val holdMillis: Long = 500,
    val maximumGapMillis: Long = 100,
    val stabilityCents: Double = 35.0,
    val toleranceCents: Double = 50.0,
    val timeoutMillis: Long = 60_000,
    val maximumMeasurements: Int = 10_000,
    val minHz: Double = 55.0,
    val maxHz: Double = 1000.0,
) {
    init {
        require(holdMillis in 1..10_000 && maximumGapMillis in 1..holdMillis)
        require(stabilityCents.isFinite() && stabilityCents > 0)
        require(toleranceCents.isFinite() && toleranceCents > 0)
        require(timeoutMillis in holdMillis..600_000 && maximumMeasurements in 2..100_000)
        require(minHz.isFinite() && maxHz.isFinite() && minHz > 0 && maxHz > minHz)
    }
}

@Serializable
data class VocalIntervalMeasurement(val elapsedMillis: Long, val phase: VocalIntervalPhase,
    val frequency: Double?, val errorCents: Double?, val rejection: String?,
    val processingMicros: Long? = null, val periodicity: Double? = null, val rms: Double? = null)

@Serializable
data class VocalIntervalResult(val targetSemitones: Int, val configuration: VocalIntervalConfiguration,
    val anchorHz: Double?, val targetHz: Double?, val intervalCents: Double?, val errorCents: Double?,
    val inTune: Boolean?, val interruption: String?, val measurements: List<VocalIntervalMeasurement>,
    val anchorSource: VocalAnchorSource = VocalAnchorSource.SUNG)

data class VocalIntervalState(val phase: VocalIntervalPhase = VocalIntervalPhase.READY_ANCHOR,
    val anchorHz: Double? = null, val latestErrorCents: Double? = null,
    val result: VocalIntervalResult? = null)

/** Relative interval round with a sung anchor (imitation/free) or a provided anchor.
 * Input time is monotonic milliseconds since round creation.
 * The caller explicitly separates anchor and target so unison also works without onset guessing.
 * Any reference playback must finish before capture; no audio, clock or Android dependency lives here.
 */
class VocalIntervalSession(val targetSemitones: Int,
    val configuration: VocalIntervalConfiguration = VocalIntervalConfiguration(),
    private val givenAnchorHz: Double? = null) {
    init {
        require(targetSemitones in -12..12)
        require(givenAnchorHz == null || givenAnchorHz.isFinite() && givenAnchorHz in configuration.minHz..configuration.maxHz)
    }
    var state = if (givenAnchorHz == null) VocalIntervalState() else
        VocalIntervalState(phase = VocalIntervalPhase.READY_TARGET, anchorHz = givenAnchorHz)
        private set
    private val measurements = mutableListOf<VocalIntervalMeasurement>()
    private val window = ArrayDeque<Pair<Long, Double>>()
    private var lastTime = -1L

    fun beginAnchor() {
        if (state.phase == VocalIntervalPhase.READY_ANCHOR) state = state.copy(phase = VocalIntervalPhase.ANCHOR)
    }

    fun beginTarget() {
        if (state.phase != VocalIntervalPhase.READY_TARGET) return
        window.clear()
        state = state.copy(phase = VocalIntervalPhase.TARGET)
    }

    /** False means the frame was ignored (wrong phase or stale timestamp). */
    fun accept(elapsedMillis: Long, estimate: PitchEstimate, processingMicros: Long? = null): Boolean {
        if (state.phase != VocalIntervalPhase.ANCHOR && state.phase != VocalIntervalPhase.TARGET) return false
        if (!advanceTime(elapsedMillis)) return false
        if (measurements.size >= configuration.maximumMeasurements) {
            interrupt("measurement-limit")
            return false
        }
        val hz = estimate.frequency?.takeIf { it.isFinite() && it in configuration.minHz..configuration.maxHz }
        val error = hz?.let { frequency -> state.anchorHz?.let { cents(frequency, it) - targetSemitones * 100 } }
        measurements += VocalIntervalMeasurement(elapsedMillis, state.phase, hz, error,
            if (hz == null) estimate.reason ?: "invalid-frequency" else null,
            processingMicros?.takeIf { it >= 0 }, estimate.periodicity.takeIf { it.isFinite() && it in 0.0..1.0 },
            estimate.rms.takeIf { it.isFinite() && it >= 0 })
        state = state.copy(latestErrorCents = error)
        if (hz == null) { window.clear(); return true }
        if (window.isNotEmpty() && elapsedMillis - window.last().first > configuration.maximumGapMillis) window.clear()
        window.addLast(elapsedMillis to hz)
        while (window.size > 1 && window.elementAt(1).first <= elapsedMillis - configuration.holdMillis) window.removeFirst()
        if (elapsedMillis - window.first().first < configuration.holdMillis) return true
        val frequencies = window.map { it.second }.sorted()
        if (cents(frequencies.last(), frequencies.first()) > configuration.stabilityCents) return true
        val middle = frequencies.size / 2
        val stable = if (frequencies.size % 2 == 1) frequencies[middle]
            else 2.0.pow((log2(frequencies[middle - 1]) + log2(frequencies[middle])) / 2)
        if (state.phase == VocalIntervalPhase.ANCHOR) {
            state = state.copy(phase = VocalIntervalPhase.READY_TARGET, anchorHz = stable)
            window.clear()
        } else finish(stable, null)
        return true
    }

    /** Call even in silence or while waiting between notes; callbacks alone cannot enforce a deadline. */
    fun tick(elapsedMillis: Long) { if (!terminal()) advanceTime(elapsedMillis) }

    fun interrupt(reason: String = "user") {
        if (!terminal()) finish(null, reason)
    }

    private fun advanceTime(elapsedMillis: Long): Boolean {
        if (elapsedMillis < 0 || elapsedMillis <= lastTime) return false
        lastTime = elapsedMillis
        if (elapsedMillis >= configuration.timeoutMillis) { interrupt("timeout"); return false }
        return true
    }

    private fun terminal() = state.phase == VocalIntervalPhase.COMPLETED || state.phase == VocalIntervalPhase.INTERRUPTED

    private fun finish(targetHz: Double?, interruption: String?) {
        val interval = targetHz?.let { cents(it, requireNotNull(state.anchorHz)) }
        val error = interval?.minus(targetSemitones * 100)
        val result = VocalIntervalResult(targetSemitones, configuration, state.anchorHz, targetHz,
            interval, error, error?.let { abs(it) <= configuration.toleranceCents }, interruption, measurements.toList(),
            if (givenAnchorHz == null) VocalAnchorSource.SUNG else VocalAnchorSource.GIVEN)
        state = state.copy(phase = if (interruption == null) VocalIntervalPhase.COMPLETED else VocalIntervalPhase.INTERRUPTED,
            latestErrorCents = error, result = result)
        window.clear()
    }

    private fun cents(hz: Double, anchor: Double) = 1200 * log2(hz / anchor)
}
