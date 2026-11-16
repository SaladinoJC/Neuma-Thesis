package ar.edu.unmdp.entrenamiento.rhythm

import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.roundToInt

/** Alineación personal con una salida de audio; no mide por separado latencia física y reacción. */
@Serializable
data class RhythmCalibration(val offsetNanos: Long, val spreadNanos: Long, val sampleCount: Int,
    val bpm: Int = 80, val method: String = "user-tap-alignment-v1", val sampleOffsetsNanos: List<Long> = emptyList()) {
    init {
        require(offsetNanos in -250_000_000L..250_000_000L)
        require(spreadNanos in 0..45_000_000L && sampleCount in 8..12 && bpm == 80)
        require(sampleOffsetsNanos.isEmpty() || sampleOffsetsNanos.size == sampleCount)
    }
}

data class RhythmCalibrationState(val active: Boolean = false, val finished: Boolean = false,
    val pulse: Int? = null, val samples: Int = 0, val candidate: RhythmCalibration? = null,
    val error: String? = null)

class RhythmCalibrationSession {
    var state = RhythmCalibrationState()
        private set
    private var origin = 0L
    private val samples = linkedMapOf<Int, Long>()
    private var duplicates = 0
    private val beat = Beat(1).nanos(80)
    fun reset() { samples.clear(); duplicates = 0; state = RhythmCalibrationState() }

    fun start(originNanos: Long) {
        origin = originNanos; samples.clear(); duplicates = 0
        state = RhythmCalibrationState(active = true)
    }
    fun tap(timestampNanos: Long) {
        if (!state.active) return
        val relative = timestampNanos - origin
        val pulse = (relative.toDouble() / beat).roundToInt()
        if (pulse !in 4..15) return
        if (pulse in samples) duplicates++ else samples[pulse] = relative - pulse * beat
        state = state.copy(samples = samples.size)
    }
    fun advance(nowNanos: Long) {
        if (!state.active) return
        val relative = nowNanos - origin
        if (relative < beat * 15 + beat / 2) {
            state = state.copy(pulse = if (relative < 0) null else (relative / beat).toInt().coerceAtMost(15) + 1)
            return
        }
        state = state.copy(active = false, finished = true, pulse = null)
        if (samples.size < 8) { state = state.copy(error = "Faltan golpes: se necesitan al menos 8 de los 12 pulsos."); return }
        val values = samples.values.sorted()
        val offset = median(values)
        val spread = median(values.map { abs(it - offset) }.sorted())
        val consistent = values.count { abs(it - offset) <= 60_000_000L }
        if (abs(offset) > 250_000_000L || spread > 45_000_000L || consistent * 4 < values.size * 3 || duplicates > 3) {
            state = state.copy(error = "Los golpes no fueron suficientemente regulares. Repetí siguiendo el sonido, con un golpe por pulso.")
            return
        }
        state = state.copy(candidate = RhythmCalibration(offset, spread, values.size,
            sampleOffsetsNanos = samples.toSortedMap().values.toList()))
    }
    fun interrupt() { if (state.active) state = state.copy(active = false, error = "Calibración interrumpida. Repetila para obtener un ajuste.") }
    private fun median(sorted: List<Long>): Long = if (sorted.size % 2 == 1) sorted[sorted.size / 2]
        else (sorted[sorted.size / 2 - 1] + sorted[sorted.size / 2]) / 2
}
