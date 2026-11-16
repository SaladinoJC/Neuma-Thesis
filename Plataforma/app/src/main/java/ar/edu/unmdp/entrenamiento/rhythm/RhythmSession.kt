package ar.edu.unmdp.entrenamiento.rhythm

import kotlinx.serialization.Serializable
import kotlin.random.Random
import kotlin.math.abs

/** Duration in quarter-note beats. Arithmetic stays rational until the timeline is fixed. */
@Serializable
data class Beat(val numerator: Int, val denominator: Int = 1) {
    init { require(numerator >= 0 && denominator > 0) }
    operator fun plus(other: Beat): Beat {
        val n = numerator * other.denominator + other.numerator * denominator
        val d = denominator * other.denominator
        var a = n; var b = d
        while (b != 0) { val r = a % b; a = b; b = r }
        return Beat(n / a, d / a)
    }
    fun nanos(bpm: Int): Long = numerator.toLong() * 60_000_000_000L / (denominator.toLong() * bpm)
    fun asDouble(): Double = numerator.toDouble() / denominator
}
@Serializable
enum class RhythmCategory(val code: String, val label: String) {
    QUARTER("R20", "Negras"), EIGHTHS("R21", "Dos corcheas"),
    HALF("R01", "Blancas"), WHOLE("R02", "Redondas"),
    DOTTED_QUARTER_EIGHTH("R09", "Negra con puntillo + corchea"),
    REST_EIGHTH("R10", "Silencio de corchea + corchea"),
    EIGHTH_REST("R11", "Corchea + silencio de corchea"),
    QUARTER_REST("R12", "Silencio de negra"), HALF_REST("R18", "Silencio de blanca"),
    BAR_REST("R19", "Silencio de compás"),
    SIXTEENTHS("R03", "Cuatro semicorcheas"),
    EIGHTH_TWO_SIXTEENTHS("R04", "Corchea + dos semicorcheas"),
    TWO_SIXTEENTHS_EIGHTH("R05", "Dos semicorcheas + corchea"),
    SIXTEENTH_EIGHTH_SIXTEENTH("R06", "Semicorchea + corchea + semicorchea"),
    DOTTED_EIGHTH_SIXTEENTH("R07", "Corchea con puntillo + semicorchea"),
    SIXTEENTH_DOTTED_EIGHTH("R08", "Semicorchea + corchea con puntillo"),
    REST_SIXTEENTH("R13", "Silencio de semicorchea + tres semicorcheas"),
    SIXTEENTHS_REST("R14", "Tres semicorcheas + silencio de semicorchea"),
    EIGHTH_TRIPLET("R15", "Tresillo de corcheas"),
    SYNCOPATION("R16", "Corchea + negra + corchea"),
    TIED_QUARTERS("R17", "Dos negras ligadas")
}
@Serializable
data class RhythmNote(val duration: Beat, val rest: Boolean = false, val tieToNext: Boolean = false, val tuplet: Int? = null)
@Serializable
data class RhythmOccurrence(val index: Int, val bar: Int, val beat: Beat, val category: RhythmCategory,
    val target: Boolean, val notes: List<RhythmNote>)
@Serializable
data class RhythmBar(val index: Int, val occurrences: List<RhythmOccurrence>) {
    val duration: Beat get() = occurrences.flatMap { it.notes }.fold(Beat(0)) { sum, note -> sum + note.duration }
}
@Serializable
data class RhythmScore(val seed: Long, val selected: List<RhythmCategory>, val bars: List<RhythmBar>,
    val version: String = "rhythm-basic-v1", val bpm: Int = 60, val beatsPerBar: Int = 4, val beatUnit: Int = 4) {
    val occurrences: List<RhythmOccurrence> get() = bars.flatMap { it.occurrences }
    val fingerprint: String get() {
        val content = "$beatsPerBar/$beatUnit:" + bars.joinToString("|") { bar ->
            bar.occurrences.flatMap { it.notes }.joinToString(",") { "${it.duration.numerator}/${it.duration.denominator}:${it.rest}:${it.tieToNext}:${it.tuplet}" }
        }
        return java.security.MessageDigest.getInstance("SHA-256").digest(content.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }
}
enum class RhythmPhase { SETUP, PREVIEW, COUNT_IN, PLAYING, RESULTS }
@Serializable enum class TapKind { PREPARATION, ON_TIME, EARLY, LATE, EXTRA, OMITTED }
@Serializable data class RhythmTap(val timestampNanos: Long, val relativeNanos: Long, val kind: TapKind,
    val attackIndex: Int? = null, val deltaNanos: Long? = null, val inputUptimeMillis: Long? = null,
    val uncorrectedRelativeNanos: Long? = null)
@Serializable data class RhythmAttack(val index: Int, val occurrenceIndex: Int, val offsetNanos: Long,
    val narrowNanos: Long, val wideNanos: Long, val kind: TapKind? = null, val deltaNanos: Long? = null)
@Serializable data class RhythmSilence(val occurrenceIndex: Int, val startNanos: Long, val endNanos: Long,
    val extraCount: Int, val respected: Boolean?)
@Serializable data class RhythmResult(val score: RhythmScore, val completed: Boolean, val taps: List<RhythmTap>,
    val attacks: List<RhythmAttack>, val metronome: Boolean, val tapSound: Boolean, val repeated: Boolean,
    val origin: String = "practice", val timingVersion: String = "rhythm-timing-v1",
    val audioClockSource: String, val clockUncertaintyNanos: Long?, val inputClockOffsetNanos: Long,
    val countInStartNanos: Long, val silences: List<RhythmSilence> = emptyList(),
    val calibration: RhythmCalibration? = null) {
    val omissions: Int get() = attacks.count { it.kind == TapKind.OMITTED }
    val extras: Int get() = taps.count { it.kind == TapKind.EXTRA }
}
data class RhythmState(val phase: RhythmPhase = RhythmPhase.SETUP, val score: RhythmScore? = null,
    val taps: List<RhythmTap> = emptyList(), val error: String? = null, val attacks: List<RhythmAttack> = emptyList(),
    val elapsedNanos: Long = 0L, val result: RhythmResult? = null, val repeated: Boolean = false,
    val feedback: TapKind? = null, val origin: String = "practice", val countInStarted: Boolean = false) {
    val countInBeat: Int? get() {
        val current = score ?: return null
        if (phase != RhythmPhase.COUNT_IN || !countInStarted) return null
        return ((elapsedNanos + Beat(current.beatsPerBar).nanos(current.bpm)) / Beat(1).nanos(current.bpm) + 1)
            .toInt().coerceIn(1, current.beatsPerBar)
    }
}

class RhythmSession {
    var state = RhythmState()
        private set
    private var epoch: Long? = null
    private var metronome = true
    private var tapSound = true
    private var audioClockSource = "controlled-clock"
    private var uncertainty: Long? = 0L
    private var inputOffset = 0L
    private var calibration: RhythmCalibration? = null
    val active: Boolean get() = state.phase == RhythmPhase.COUNT_IN || state.phase == RhythmPhase.PLAYING
    val executionStartNanos: Long? get() = epoch?.plus(requireNotNull(state.score).let { Beat(it.beatsPerBar).nanos(it.bpm) })

    fun generate(selected: Set<RhythmCategory>, seed: Long, beatsPerBar: Int = 4, maxCandidates: Int = 500,
        checkCancelled: () -> Unit = {}) {
        if (active) return
        if (selected.isEmpty()) { state = RhythmState(error = "Elegí al menos una categoría."); return }
        if (beatsPerBar != 4 || selected.any { it != RhythmCategory.QUARTER && it != RhythmCategory.EIGHTHS }) {
            try {
                preview(generateSimpleRhythm(selected, seed, beatsPerBar, maxCandidates, checkCancelled))
            } catch (failure: RhythmGenerationFailure) {
                epoch = null
                state = RhythmState(error = failure.message)
            }
            return
        }
        val ordered = selected.sortedBy { it.code }
        val random = Random(seed)
        // R1 has a constructive solution: ten target beats (62.5%) and six connection beats.
        // Provenance is exclusive even when the visible figures happen to coincide.
        val targetSlots = (0 until 16).shuffled(random).take(10).toSet()
        val targetCategories = List(10) { ordered[it % ordered.size] }.shuffled(random)
        var nextTarget = 0
        val occurrences = (0 until 16).map { index ->
            val target = index in targetSlots
            val category = if (target) targetCategories[nextTarget++] else RhythmCategory.entries[random.nextInt(2)]
            val notes = if (category == RhythmCategory.QUARTER) listOf(RhythmNote(Beat(1)))
                else listOf(RhythmNote(Beat(1, 2)), RhythmNote(Beat(1, 2)))
            RhythmOccurrence(index, index / 4, Beat(index % 4), category, target, notes)
        }
        val score = RhythmScore(seed, ordered, List(4) { bar -> RhythmBar(bar, occurrences.filter { it.bar == bar }) })
        state = RhythmState(RhythmPhase.PREVIEW, score)
    }

    fun preview(score: RhythmScore, repeated: Boolean = false, origin: String = "practice") {
        if (active) return
        epoch = null
        require(origin in setOf("practice", "progression"))
        state = if (score.isPlayable()) RhythmState(RhythmPhase.PREVIEW, score, repeated = repeated,
            origin = if (repeated) "practice" else origin)
        else RhythmState(error = "Partitura inválida: revisá compases, posiciones y figuras del catálogo.")
    }
    fun setup() { if (!active) { epoch = null; state = RhythmState() } }
    fun play(originNanos: Long, metronome: Boolean = true, tapSound: Boolean = true,
        clockSource: String = "controlled-clock", uncertaintyNanos: Long? = 0L, inputClockOffsetNanos: Long = 0L,
        calibration: RhythmCalibration? = null) {
        val score = state.score ?: return
        if (state.phase != RhythmPhase.PREVIEW) return
        epoch = originNanos; this.metronome = metronome; this.tapSound = tapSound
        audioClockSource = clockSource; uncertainty = uncertaintyNanos; inputOffset = inputClockOffsetNanos
        this.calibration = calibration
        val raw = mutableListOf<Pair<Int, Long>>()
        var continuation = false
        score.occurrences.forEach { occurrence ->
            var beat = Beat(occurrence.bar * score.beatsPerBar) + occurrence.beat
            occurrence.notes.forEach { note ->
                if (!note.rest && !continuation) raw.add(occurrence.index to beat.nanos(score.bpm))
                continuation = note.tieToNext
                beat += note.duration
            }
        }
        val attacks = raw.mapIndexed { index, (owner, at) ->
            val distances = listOfNotNull(raw.getOrNull(index - 1)?.let { at - it.second }, raw.getOrNull(index + 1)?.let { it.second - at }).filter { it > 0 }
            val d = distances.minOrNull() ?: Beat(1).nanos(score.bpm)
            RhythmAttack(index, owner, at, minOf(60_000_000L, d / 5), minOf(150_000_000L, d * 45 / 100))
        }
        state = state.copy(phase = RhythmPhase.COUNT_IN, taps = emptyList(), attacks = attacks,
            elapsedNanos = -Beat(score.beatsPerBar).nanos(score.bpm), result = null, feedback = null, error = null,
            countInStarted = false)
    }
    fun advance(nowNanos: Long) {
        if (!active) return
        val start = executionStartNanos ?: return
        val elapsed = nowNanos - start
        val effectiveElapsed = maxOf(state.elapsedNanos, elapsed)
        val attacks = state.attacks.map { attack ->
            if (attack.kind == null && effectiveElapsed - (calibration?.offsetNanos ?: 0L) > attack.offsetNanos + attack.wideNanos)
                attack.copy(kind = TapKind.OMITTED) else attack
        }
        val newOmission = attacks.zip(state.attacks).any { (current, previous) -> current.kind == TapKind.OMITTED && previous.kind == null }
        state = state.copy(phase = if (effectiveElapsed < 0) RhythmPhase.COUNT_IN else RhythmPhase.PLAYING,
            elapsedNanos = effectiveElapsed, attacks = attacks, feedback = if (newOmission) TapKind.OMITTED else state.feedback,
            countInStarted = nowNanos >= requireNotNull(epoch))
        val end = endOffsetNanos() + maxOf(0L, calibration?.offsetNanos ?: 0L)
        if (effectiveElapsed >= end) finish(true)
    }
    fun tap(timestampNanos: Long, inputUptimeMillis: Long? = null) {
        val start = executionStartNanos ?: return
        if (!active && state.result?.completed != true) return
        if (timestampNanos < requireNotNull(epoch)) return
        val rawRelative = timestampNanos - start
        val relative = rawRelative - (calibration?.offsetNanos ?: 0L)
        if (relative > endOffsetNanos()) return
        advance(timestampNanos)
        if (relative < 0) {
            state = state.copy(taps = state.taps + RhythmTap(timestampNanos, relative, TapKind.PREPARATION,
                inputUptimeMillis = inputUptimeMillis, uncorrectedRelativeNanos = rawRelative), feedback = TapKind.PREPARATION)
            if (state.phase == RhythmPhase.RESULTS) state = state.copy(result = result(true))
            return
        }
        // A visual tick may expire an attack before Android delivers its original touch timestamp.
        // Only omissions can be replaced; a confirmed tap is never reassigned.
        val match = state.attacks.firstOrNull { (it.kind == null || it.kind == TapKind.OMITTED) && abs(relative - it.offsetNanos) <= it.wideNanos }
        val delta = match?.let { relative - it.offsetNanos }
        val kind = when {
            match == null -> TapKind.EXTRA
            abs(requireNotNull(delta)) <= match.narrowNanos -> TapKind.ON_TIME
            delta < 0 -> TapKind.EARLY
            else -> TapKind.LATE
        }
        state = state.copy(taps = state.taps + RhythmTap(timestampNanos, relative, kind, match?.index, delta, inputUptimeMillis, rawRelative),
            attacks = state.attacks.map { if (it.index == match?.index) it.copy(kind = kind, deltaNanos = delta) else it }, feedback = kind)
        if (state.phase == RhythmPhase.RESULTS) state = state.copy(result = result(true))
    }
    fun interrupt() { if (active) finish(false) }
    fun repeat() {
        if (state.phase != RhythmPhase.RESULTS) return
        val score = state.score ?: return
        epoch = null; state = RhythmState(RhythmPhase.PREVIEW, score, repeated = true)
    }
    private fun endOffsetNanos(): Long = maxOf(requireNotNull(state.score).let { Beat(it.beatsPerBar * it.bars.size).nanos(it.bpm) },
        state.attacks.lastOrNull()?.let { it.offsetNanos + it.wideNanos + 1 } ?: 0)
    private fun finish(completed: Boolean) {
        state = state.copy(phase = RhythmPhase.RESULTS,
            attacks = state.attacks.map { if (completed && it.kind == null) it.copy(kind = TapKind.OMITTED) else it })
        state = state.copy(result = result(completed))
    }
    private fun result(completed: Boolean) = RhythmResult(requireNotNull(state.score), completed,
        state.taps.toList(), state.attacks.toList(), metronome, tapSound, state.repeated, origin = state.origin,
        audioClockSource = audioClockSource, clockUncertaintyNanos = uncertainty, inputClockOffsetNanos = inputOffset,
        countInStartNanos = requireNotNull(epoch), silences = silenceResults(completed), calibration = calibration,
        timingVersion = "rhythm-timing-v2-user-calibration")

    private fun silenceResults(completed: Boolean): List<RhythmSilence> {
        val score = requireNotNull(state.score)
        return score.occurrences.flatMap { occurrence ->
            var position = Beat(occurrence.bar * score.beatsPerBar) + occurrence.beat
            occurrence.notes.mapNotNull { note ->
                val start = position.nanos(score.bpm)
                position += note.duration
                val end = position.nanos(score.bpm)
                if (!note.rest) null else {
                    val extras = state.taps.count { it.kind == TapKind.EXTRA && it.relativeNanos >= start && it.relativeNanos < end }
                    RhythmSilence(occurrence.index, start, end, extras,
                        if (completed || state.elapsedNanos >= end) extras == 0 else null)
                }
            }
        }
    }
}

/** Rechaza contenido musical incompleto antes de fijar su reloj. Las plantillas aprobadas
 * son el límite actual: no aceptar variantes de tresillo/ligadura bajo la misma categoría. */
private fun RhythmScore.isPlayable(): Boolean {
    if (beatsPerBar !in 2..4 || beatUnit != 4 || bpm <= 0 || bars.map { it.index } != (0..3).toList()) return false
    if (selected.isEmpty() || (RhythmCategory.WHOLE in selected && beatsPerBar != 4)) return false
    var nextIndex = 0
    for (bar in bars) {
        var position = Beat(0)
        for (cell in bar.occurrences) {
            if (cell.bar != bar.index || cell.index != nextIndex++ || cell.beat != position) return false
            if (cell.category == RhythmCategory.WHOLE && beatsPerBar != 4) return false
            if (cell.notes != cell.category.notes(beatsPerBar)) return false
            if (cell.target && cell.category !in selected) return false
            if (!cell.target && cell.category !in setOf(RhythmCategory.QUARTER, RhythmCategory.EIGHTHS)) return false
            position = cell.notes.fold(position) { sum, note -> sum + note.duration }
        }
        if (position != Beat(beatsPerBar)) return false
    }
    return true
}
