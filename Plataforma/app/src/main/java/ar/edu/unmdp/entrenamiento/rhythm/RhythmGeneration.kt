package ar.edu.unmdp.entrenamiento.rhythm

import kotlin.math.abs
import kotlin.random.Random

internal class RhythmGenerationFailure(message: String) : Exception(message)

internal val rhythmR3Categories = setOf(RhythmCategory.SIXTEENTHS, RhythmCategory.EIGHTH_TWO_SIXTEENTHS,
    RhythmCategory.TWO_SIXTEENTHS_EIGHTH, RhythmCategory.SIXTEENTH_EIGHTH_SIXTEENTH,
    RhythmCategory.DOTTED_EIGHTH_SIXTEENTH, RhythmCategory.SIXTEENTH_DOTTED_EIGHTH,
    RhythmCategory.REST_SIXTEENTH, RhythmCategory.SIXTEENTHS_REST)
internal val rhythmR4Categories = setOf(RhythmCategory.EIGHTH_TRIPLET, RhythmCategory.SYNCOPATION, RhythmCategory.TIED_QUARTERS)

private fun List<RhythmNote>.wholeBeats(): Int {
    val duration = fold(Beat(0)) { sum, note -> sum + note.duration }
    check(duration.denominator == 1) { "La plantilla debe ocupar tiempos enteros." }
    return duration.numerator
}

internal fun RhythmCategory.notes(meter: Int): List<RhythmNote> = when (this) {
    RhythmCategory.QUARTER -> listOf(RhythmNote(Beat(1)))
    RhythmCategory.EIGHTHS -> listOf(RhythmNote(Beat(1, 2)), RhythmNote(Beat(1, 2)))
    RhythmCategory.HALF -> listOf(RhythmNote(Beat(2)))
    RhythmCategory.WHOLE -> listOf(RhythmNote(Beat(4)))
    RhythmCategory.DOTTED_QUARTER_EIGHTH -> listOf(RhythmNote(Beat(3, 2)), RhythmNote(Beat(1, 2)))
    RhythmCategory.REST_EIGHTH -> listOf(RhythmNote(Beat(1, 2), rest = true), RhythmNote(Beat(1, 2)))
    RhythmCategory.EIGHTH_REST -> listOf(RhythmNote(Beat(1, 2)), RhythmNote(Beat(1, 2), rest = true))
    RhythmCategory.QUARTER_REST -> listOf(RhythmNote(Beat(1), rest = true))
    RhythmCategory.HALF_REST -> listOf(RhythmNote(Beat(2), rest = true))
    RhythmCategory.BAR_REST -> listOf(RhythmNote(Beat(meter), rest = true))
    RhythmCategory.SIXTEENTHS -> List(4) { RhythmNote(Beat(1, 4)) }
    RhythmCategory.EIGHTH_TWO_SIXTEENTHS -> listOf(RhythmNote(Beat(1, 2)), RhythmNote(Beat(1, 4)), RhythmNote(Beat(1, 4)))
    RhythmCategory.TWO_SIXTEENTHS_EIGHTH -> listOf(RhythmNote(Beat(1, 4)), RhythmNote(Beat(1, 4)), RhythmNote(Beat(1, 2)))
    RhythmCategory.SIXTEENTH_EIGHTH_SIXTEENTH -> listOf(RhythmNote(Beat(1, 4)), RhythmNote(Beat(1, 2)), RhythmNote(Beat(1, 4)))
    RhythmCategory.DOTTED_EIGHTH_SIXTEENTH -> listOf(RhythmNote(Beat(3, 4)), RhythmNote(Beat(1, 4)))
    RhythmCategory.SIXTEENTH_DOTTED_EIGHTH -> listOf(RhythmNote(Beat(1, 4)), RhythmNote(Beat(3, 4)))
    RhythmCategory.REST_SIXTEENTH -> List(4) { RhythmNote(Beat(1, 4), rest = it == 0) }
    RhythmCategory.SIXTEENTHS_REST -> List(4) { RhythmNote(Beat(1, 4), rest = it == 3) }
    RhythmCategory.EIGHTH_TRIPLET -> List(3) { RhythmNote(Beat(1, 3), tuplet = 3) }
    RhythmCategory.SYNCOPATION -> listOf(RhythmNote(Beat(1, 2)), RhythmNote(Beat(1)), RhythmNote(Beat(1, 2)))
    RhythmCategory.TIED_QUARTERS -> listOf(RhythmNote(Beat(1), tieToNext = true), RhythmNote(Beat(1)))
}

/** Exact feasibility first, then bounded rejection of randomized placements. All R2–R4 cells
 * occupy integer quarter beats, so connection beats can fill every residual slot. */
internal fun generateSimpleRhythm(selected: Set<RhythmCategory>, seed: Long, meter: Int,
    maxCandidates: Int, checkCancelled: () -> Unit): RhythmScore {
    val started = System.nanoTime()
    fun budget() {
        checkCancelled()
        if (System.nanoTime() - started > 1_000_000_000L) throw RhythmGenerationFailure(
            "Se agotó el presupuesto de búsqueda. Reintentá; no demuestra que la selección sea imposible.")
    }
    fun impossible(reason: String): Nothing = throw RhythmGenerationFailure("$reason Reducí la selección o cambiá el compás.")
    if (meter !in 2..4) impossible("El compás debe ser 2/4, 3/4 o 4/4.")
    if (RhythmCategory.WHOLE in selected && meter != 4) impossible("La redonda solo cabe en 4/4 sin cruzar barras.")
    val ordered = selected.sortedBy { it.code }
    val lengths = ordered.map { category -> category.notes(meter).wholeBeats() }
    val total = 4 * meter
    val minimum = total / 2
    val maximum = total * 3 / 4
    if (lengths.sum() * 2 > maximum) impossible("Dos apariciones por categoría exceden la cuota máxima del 75 %.")

    fun packable(items: List<Int>): Boolean {
        val sorted = items.sortedDescending()
        val capacities = IntArray(4) { meter }
        val failed = hashSetOf<String>()
        fun pack(index: Int): Boolean {
            budget()
            if (index == sorted.size) return true
            val key = "$index:${capacities.sorted()}"
            if (key in failed) return false
            val seen = hashSetOf<Int>()
            for (bar in capacities.indices) if (capacities[bar] >= sorted[index] && seen.add(capacities[bar])) {
                capacities[bar] -= sorted[index]
                if (pack(index + 1)) return true
                capacities[bar] += sorted[index]
            }
            failed.add(key)
            return false
        }
        return pack(0)
    }

    val plans = mutableListOf<List<RhythmCategory>>()
    fun enumerate(index: Int, used: Int, cells: List<RhythmCategory>) {
        budget()
        if (index == ordered.size) {
            if (used in minimum..maximum && packable(cells.map { lengths[ordered.indexOf(it)] })) plans.add(cells)
            return
        }
        val reserve = lengths.drop(index + 1).sum() * 2
        for (count in 2..((maximum - used - reserve) / lengths[index])) {
            enumerate(index + 1, used + count * lengths[index], cells + List(count) { ordered[index] })
        }
    }
    enumerate(0, 0, emptyList())
    if (plans.isEmpty()) impossible("La selección no cabe en cuatro compases con la cuota y apariciones requeridas.")
    fun duration(plan: List<RhythmCategory>) = plan.sumOf { lengths[ordered.indexOf(it)] }
    val bestDistance = plans.minOf { abs(duration(it) * 5 - total * 3) }
    val best = plans.filter { abs(duration(it) * 5 - total * 3) == bestDistance }
    val random = Random(seed)
    repeat(maxCandidates.coerceIn(0, 500)) {
        budget()
        val targets = best[random.nextInt(best.size)]
        val remaining = (targets.map { it to true } + List(total - duration(targets)) {
            (if (random.nextBoolean()) RhythmCategory.QUARTER else RhythmCategory.EIGHTHS) to false
        }).toMutableList()
        val occurrences = mutableListOf<RhythmOccurrence>()
        var failed = false
        for (bar in 0 until 4) {
            var beat = 0
            while (beat < meter) {
                budget()
                val candidates = remaining.indices.filter { index ->
                    remaining[index].first.notes(meter).wholeBeats() <= meter - beat
                }
                if (candidates.isEmpty()) { failed = true; break }
                val (category, target) = remaining.removeAt(candidates[random.nextInt(candidates.size)])
                val notes = category.notes(meter)
                occurrences.add(RhythmOccurrence(occurrences.size, bar, Beat(beat), category, target, notes))
                beat += notes.wholeBeats()
            }
            if (failed) break
        }
        if (!failed && remaining.isEmpty()) return RhythmScore(seed, ordered,
            List(4) { bar -> RhythmBar(bar, occurrences.filter { it.bar == bar }) },
            version = when {
                selected.any { it in rhythmR4Categories } -> "rhythm-simple-v4"
                selected.any { it in rhythmR3Categories } -> "rhythm-simple-v3"
                else -> "rhythm-simple-v2"
            }, beatsPerBar = meter)
    }
    throw RhythmGenerationFailure("Se agotó la búsqueda aleatoria. Reintentá; no demuestra que la selección sea imposible.")
}
