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
enum class RhythmCategory(val code: String, val label: String) { QUARTER("R20", "Negras"), EIGHTHS("R21", "Dos corcheas") }
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
    val attackIndex: Int? = null, val deltaNanos: Long? = null, val inputUptimeMillis: Long? = null)
@Serializable data class RhythmAttack(val index: Int, val occurrenceIndex: Int, val offsetNanos: Long,
    val narrowNanos: Long, val wideNanos: Long, val kind: TapKind? = null, val deltaNanos: Long? = null)
@Serializable data class RhythmResult(val score: RhythmScore, val completed: Boolean, val taps: List<RhythmTap>,
    val attacks: List<RhythmAttack>, val metronome: Boolean, val tapSound: Boolean, val repeated: Boolean,
    val origin: String = "practice", val timingVersion: String = "rhythm-timing-v1",
    val audioClockSource: String, val clockUncertaintyNanos: Long?, val inputClockOffsetNanos: Long,
    val countInStartNanos: Long) {
    val omissions: Int get() = attacks.count { it.kind == TapKind.OMITTED }
    val extras: Int get() = taps.count { it.kind == TapKind.EXTRA }
}
data class RhythmState(val phase: RhythmPhase = RhythmPhase.SETUP, val score: RhythmScore? = null,
    val taps: List<RhythmTap> = emptyList(), val error: String? = null, val attacks: List<RhythmAttack> = emptyList(),
    val elapsedNanos: Long = 0L, val result: RhythmResult? = null, val repeated: Boolean = false,
    val feedback: TapKind? = null)

class RhythmSession {
    var state = RhythmState()
        private set
    private var epoch: Long? = null
    private var metronome = true
    private var tapSound = true
    private var audioClockSource = "controlled-clock"
    private var uncertainty: Long? = 0L
    private var inputOffset = 0L
    val active: Boolean get() = state.phase == RhythmPhase.COUNT_IN || state.phase == RhythmPhase.PLAYING
    val executionStartNanos: Long? get() = epoch?.plus(Beat(4).nanos(requireNotNull(state.score).bpm))

    fun generate(selected: Set<RhythmCategory>, seed: Long) {
        if (active) return
        if (selected.isEmpty()) { state = state.copy(error = "Elegí al menos una categoría."); return }
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

    fun preview(score: RhythmScore) {
        if (!active) { epoch = null; state = RhythmState(RhythmPhase.PREVIEW, score) }
    }
    fun setup() { if (!active) { epoch = null; state = RhythmState() } }
    fun play(originNanos: Long, metronome: Boolean = true, tapSound: Boolean = true,
        clockSource: String = "controlled-clock", uncertaintyNanos: Long? = 0L, inputClockOffsetNanos: Long = 0L) {
        val score = state.score ?: return
        if (state.phase != RhythmPhase.PREVIEW) return
        epoch = originNanos; this.metronome = metronome; this.tapSound = tapSound
        audioClockSource = clockSource; uncertainty = uncertaintyNanos; inputOffset = inputClockOffsetNanos
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
            elapsedNanos = -Beat(4).nanos(score.bpm), result = null, feedback = null, error = null)
    }
    fun advance(nowNanos: Long) {
        if (!active) return
        val start = executionStartNanos ?: return
        val elapsed = nowNanos - start
        val effectiveElapsed = maxOf(state.elapsedNanos, elapsed)
        val attacks = state.attacks.map { attack ->
            if (attack.kind == null && effectiveElapsed > attack.offsetNanos + attack.wideNanos) attack.copy(kind = TapKind.OMITTED) else attack
        }
        val newOmission = attacks.zip(state.attacks).any { (current, previous) -> current.kind == TapKind.OMITTED && previous.kind == null }
        state = state.copy(phase = if (effectiveElapsed < 0) RhythmPhase.COUNT_IN else RhythmPhase.PLAYING,
            elapsedNanos = effectiveElapsed, attacks = attacks, feedback = if (newOmission) TapKind.OMITTED else state.feedback)
        val end = endOffsetNanos()
        if (effectiveElapsed >= end) finish(true)
    }
    fun tap(timestampNanos: Long, inputUptimeMillis: Long? = null) {
        val start = executionStartNanos ?: return
        if (!active && state.result?.completed != true) return
        if (timestampNanos < requireNotNull(epoch)) return
        val relative = timestampNanos - start
        if (relative > endOffsetNanos()) return
        advance(timestampNanos)
        if (relative < 0) {
            state = state.copy(taps = state.taps + RhythmTap(timestampNanos, relative, TapKind.PREPARATION,
                inputUptimeMillis = inputUptimeMillis), feedback = TapKind.PREPARATION)
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
        state = state.copy(taps = state.taps + RhythmTap(timestampNanos, relative, kind, match?.index, delta, inputUptimeMillis),
            attacks = state.attacks.map { if (it.index == match?.index) it.copy(kind = kind, deltaNanos = delta) else it }, feedback = kind)
        if (state.phase == RhythmPhase.RESULTS) state = state.copy(result = result(true))
    }
    fun interrupt() { if (active) finish(false) }
    fun repeat() {
        if (state.phase != RhythmPhase.RESULTS) return
        val score = state.score ?: return
        epoch = null; state = RhythmState(RhythmPhase.PREVIEW, score, repeated = true)
    }
    private fun endOffsetNanos(): Long = maxOf(Beat(16).nanos(requireNotNull(state.score).bpm),
        state.attacks.lastOrNull()?.let { it.offsetNanos + it.wideNanos + 1 } ?: 0)
    private fun finish(completed: Boolean) {
        state = state.copy(phase = RhythmPhase.RESULTS,
            attacks = state.attacks.map { if (completed && it.kind == null) it.copy(kind = TapKind.OMITTED) else it })
        state = state.copy(result = result(completed))
    }
    private fun result(completed: Boolean) = RhythmResult(requireNotNull(state.score), completed,
        state.taps.toList(), state.attacks.toList(), metronome, tapSound, state.repeated,
        audioClockSource = audioClockSource, clockUncertaintyNanos = uncertainty, inputClockOffsetNanos = inputOffset,
        countInStartNanos = requireNotNull(epoch))
}
