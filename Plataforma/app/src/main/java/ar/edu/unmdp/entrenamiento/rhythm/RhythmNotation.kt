package ar.edu.unmdp.entrenamiento.rhythm

import kotlinx.serialization.Serializable

/** Projection for engraving only. Rational onsets and attack ownership stay in Kotlin. */
@Serializable
internal data class RhythmNotationNote(val id: String, val duration: String, val dotted: Boolean,
    val rest: Boolean, val triplet: Boolean, val tie: Boolean, val beat: Double,
    val endBeat: Double, val attackIndex: Int?, val occurrenceIndex: Int, val attackNanos: Long?)

@Serializable
internal data class RhythmNotationBar(val index: Int, val notes: List<RhythmNotationNote>)

@Serializable
internal data class RhythmNotationScore(val key: String, val meter: Int, val bpm: Int,
    val bars: List<RhythmNotationBar>) {
    val notes: List<RhythmNotationNote> get() = bars.flatMap { it.notes }
    fun feedback(state: RhythmState): List<RhythmNoteFeedback> {
        val attacks = (state.result?.attacks ?: state.attacks).associateBy { it.index }
        return notes.map { RhythmNoteFeedback(it.id, attacks[it.attackIndex]?.kind?.name ?: "PENDING") }
    }
}
internal data class RhythmNoteFeedback(val id: String, val kind: String)

internal fun RhythmScore.toNotation(): RhythmNotationScore {
    var nextAttack = 0
    var tiedAttack: Int? = null
    var tiedOnset: Long? = null
    return RhythmNotationScore(fingerprint, beatsPerBar, bpm, bars.map { bar ->
        RhythmNotationBar(bar.index, bar.occurrences.flatMap { occurrence ->
            var at = Beat(bar.index * beatsPerBar) + occurrence.beat
            occurrence.notes.mapIndexed { index, note ->
                val onset = at
                at += note.duration
                val attack = if (note.rest) null else tiedAttack ?: nextAttack++
                val attackAt = if (note.rest) null else tiedOnset ?: onset.nanos(bpm)
                tiedAttack = if (note.tieToNext) attack else null
                tiedOnset = if (note.tieToNext) attackAt else null
                val duration = when {
                    occurrence.category == RhythmCategory.BAR_REST -> "w"
                    note.tuplet == 3 -> "8"
                    note.duration == Beat(4) -> "w"
                    note.duration == Beat(2) -> "h"
                    note.duration == Beat(1) || note.duration == Beat(3, 2) -> "q"
                    note.duration == Beat(1, 2) || note.duration == Beat(3, 4) -> "8"
                    note.duration == Beat(1, 4) -> "16"
                    else -> error("Figura fuera del catálogo: ${note.duration}")
                }
                RhythmNotationNote("${occurrence.index}:$index", duration,
                    note.duration == Beat(3, 2) || note.duration == Beat(3, 4),
                    note.rest, note.tuplet == 3, note.tieToNext, onset.asDouble(), at.asDouble(),
                    attack, occurrence.index, attackAt)
            }
        })
    })
}
