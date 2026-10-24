package ar.edu.unmdp.entrenamiento.rhythm

import kotlin.math.abs
import kotlin.random.Random

internal class RhythmGenerationFailure(message: String) : Exception(message)

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
}

/** Exact feasibility first, then bounded rejection of randomized placements. All R2 cells
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
    val lengths = ordered.map { category -> category.notes(meter).sumOf { it.duration.asDouble() }.toInt() }
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
                    remaining[index].first.notes(meter).sumOf { it.duration.asDouble() } <= meter - beat
                }
                if (candidates.isEmpty()) { failed = true; break }
                val (category, target) = remaining.removeAt(candidates[random.nextInt(candidates.size)])
                val notes = category.notes(meter)
                occurrences.add(RhythmOccurrence(occurrences.size, bar, Beat(beat), category, target, notes))
                beat += notes.sumOf { it.duration.asDouble() }.toInt()
            }
            if (failed) break
        }
        if (!failed && remaining.isEmpty()) return RhythmScore(seed, ordered,
            List(4) { bar -> RhythmBar(bar, occurrences.filter { it.bar == bar }) },
            version = "rhythm-simple-v2", beatsPerBar = meter)
    }
    throw RhythmGenerationFailure("Se agotó la búsqueda aleatoria. Reintentá; no demuestra que la selección sea imposible.")
}
