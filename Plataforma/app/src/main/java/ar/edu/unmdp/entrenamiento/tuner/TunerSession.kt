package ar.edu.unmdp.entrenamiento.tuner

import ar.edu.unmdp.entrenamiento.voice.PitchEstimate
import kotlin.math.abs
import kotlinx.serialization.Serializable

@Serializable enum class TunerCriterion(val margin: Double) { NORMAL(5.0), PRO(2.0) }
@Serializable enum class TunerTracking { CHROMATIC, STRINGS }
data class TunerState(
    val running: Boolean = false,
    val criterion: TunerCriterion = TunerCriterion.NORMAL,
    val tracking: TunerTracking = TunerTracking.STRINGS,
    val fixedString: Int? = null,
    val stringNumber: Int? = null,
    val targetMidi: Int? = null,
    val measuredMidi: Int? = null,
    val frequency: Double? = null,
    val cents: Double? = null,
    val inTune: Boolean = false,
    val stableMillis: Long = 0,
)

/** tuner-continuity-v1: a gap above 250 ms breaks the one-second evidence. */
class TunerSession {
    var state = TunerState()
        private set
    private var since: Long? = null
    private var lastTime: Long? = null
    private var strings = mapOf(6 to 40, 5 to 45, 4 to 50, 3 to 55, 2 to 59, 1 to 64)
    var setup: TunerSetupSnapshot = TunerConfiguration().snapshot()
        private set
    fun configure(snapshot: TunerSetupSnapshot) {
        stop()
        setup = snapshot.copy(tuning = snapshot.tuning.copy(notesSixToFirst = snapshot.tuning.notesSixToFirst.toList()))
        strings = setup.tuning.notesSixToFirst.mapIndexed { index, midi -> (6 - index) to midi }.toMap()
        state = TunerState(criterion = setup.criterion, tracking = setup.tracking)
    }

    fun start() { clear(); state = state.copy(running = true) }
    fun stop() { clear(); state = state.copy(running = false) }
    fun criterion(value: TunerCriterion) { clear(); state = state.copy(criterion = value) }
    fun tracking(value: TunerTracking) { clear(); state = state.copy(tracking = value, fixedString = null) }
    fun fixString(number: Int?) {
        require(number == null || number in strings)
        clear()
        state = state.copy(fixedString = number, tracking = TunerTracking.STRINGS,
            stringNumber = number, targetMidi = number?.let(strings::getValue))
    }
    fun measure(estimate: PitchEstimate, timeMillis: Long) {
        if (!state.running) return
        val hz = estimate.frequency
        if (hz == null || !hz.isFinite() || hz !in 55.0..1000.0) { clear(); return }
        val midi = requireNotNull(estimate.midi)
        val candidates = strings.entries.sortedBy { abs(requireNotNull(estimate.cents(it.value))) }
        // Absolute pitch only: no octave folding. Ambiguous/outlying notes keep chromatic reading.
        val automatic = candidates.first().takeIf {
            abs(requireNotNull(estimate.cents(it.value))) <= 75.0 &&
                abs(requireNotNull(estimate.cents(candidates[1].value))) - abs(requireNotNull(estimate.cents(it.value))) >= 25.0
        }?.key
        val number = state.fixedString ?: automatic.takeIf { state.tracking == TunerTracking.STRINGS }
        val target = number?.let(strings::getValue) ?: midi.takeIf { state.tracking == TunerTracking.CHROMATIC }
        val cents = target?.let(estimate::cents)
        val continuous = lastTime?.let { timeMillis > it && timeMillis - it <= 250 } ?: false
        if (!continuous || state.targetMidi != target || cents == null || abs(cents) > state.criterion.margin + 1e-9) since = null
        if (cents != null && abs(cents) <= state.criterion.margin + 1e-9 && since == null) since = timeMillis
        lastTime = timeMillis
        val stable = since?.let { (timeMillis - it).coerceAtLeast(0) } ?: 0
        state = state.copy(stringNumber = number, targetMidi = target, measuredMidi = midi,
            frequency = hz, cents = cents, stableMillis = stable, inTune = stable >= 1000)
    }
    private fun clear() {
        since = null; lastTime = null
        state = state.copy(stringNumber = state.fixedString, targetMidi = state.fixedString?.let(strings::getValue),
            measuredMidi = null, frequency = null, cents = null, stableMillis = 0, inTune = false)
    }
}
