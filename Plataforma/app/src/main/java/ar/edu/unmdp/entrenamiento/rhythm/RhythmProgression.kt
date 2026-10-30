package ar.edu.unmdp.entrenamiento.rhythm

import ar.edu.unmdp.entrenamiento.dda.*
import ar.edu.unmdp.entrenamiento.rhythm.RhythmCategory.*
import kotlinx.serialization.Serializable

/** Configuración de prueba aprobada para experimentar; no es una escala pedagógica validada. */
val rhythmPrerequisites: Map<RhythmCategory, Set<RhythmCategory>> =
    mapOf(HALF to setOf(QUARTER), WHOLE to setOf(HALF), QUARTER_REST to setOf(QUARTER),
        HALF_REST to setOf(QUARTER_REST, HALF), BAR_REST to setOf(QUARTER_REST),
        REST_EIGHTH to setOf(EIGHTHS, QUARTER_REST), EIGHTH_REST to setOf(EIGHTHS, QUARTER_REST),
        SIXTEENTHS to setOf(EIGHTHS), EIGHTH_TWO_SIXTEENTHS to setOf(SIXTEENTHS),
        TWO_SIXTEENTHS_EIGHTH to setOf(SIXTEENTHS), SIXTEENTH_EIGHTH_SIXTEENTH to setOf(SIXTEENTHS),
        DOTTED_EIGHTH_SIXTEENTH to setOf(EIGHTH_TWO_SIXTEENTHS, TWO_SIXTEENTHS_EIGHTH),
        SIXTEENTH_DOTTED_EIGHTH to setOf(EIGHTH_TWO_SIXTEENTHS, TWO_SIXTEENTHS_EIGHTH),
        DOTTED_QUARTER_EIGHTH to setOf(HALF, EIGHTHS),
        REST_SIXTEENTH to setOf(SIXTEENTHS, REST_EIGHTH, EIGHTH_REST),
        SIXTEENTHS_REST to setOf(SIXTEENTHS, REST_EIGHTH, EIGHTH_REST),
        EIGHTH_TRIPLET to setOf(EIGHTHS), SYNCOPATION to setOf(REST_EIGHTH, EIGHTH_REST, DOTTED_QUARTER_EIGHTH),
        TIED_QUARTERS to setOf(HALF, SYNCOPATION))

fun rhythmInference() = MamdaniConfiguration("rhythm-mamdani-v1",
    inputs = mapOf(
        "precision" to mapOf("baja" to Trapezoid(0.0, 0.0, .50, .70),
            "media" to Trapezoid(.50, .70, .80, .90), "alta" to Trapezoid(.80, .90, 1.0, 1.0)),
        "control" to mapOf("bajo" to Trapezoid(0.0, 0.0, .60, .85), "alto" to Trapezoid(.70, .90, 1.0, 1.0))),
    outputs = mapOf("bajo" to Trapezoid(0.0, 0.0, .25, .45),
        "medio" to Trapezoid(.30, .45, .55, .70), "alto" to Trapezoid(.60, .80, 1.0, 1.0)),
    rules = listOf(
        FuzzyRule("R1", "Precisión baja → bajo", listOf(FuzzyCondition("precision", "baja")), "bajo"),
        FuzzyRule("R2", "Precisión media y control bajo → bajo", listOf(FuzzyCondition("precision", "media"), FuzzyCondition("control", "bajo")), "bajo"),
        FuzzyRule("R3", "Precisión media y control alto → medio", listOf(FuzzyCondition("precision", "media"), FuzzyCondition("control", "alto")), "medio"),
        FuzzyRule("R4", "Precisión alta y control bajo → medio", listOf(FuzzyCondition("precision", "alta"), FuzzyCondition("control", "bajo")), "medio"),
        FuzzyRule("R5", "Precisión alta y control alto → alto", listOf(FuzzyCondition("precision", "alta"), FuzzyCondition("control", "alto")), "alto")),
    outputMinimum = 0.0, outputMaximum = 1.0, samplingPoints = 1001)

@Serializable
data class RhythmCategoryProgress(val bpm: Int = 60, val highStreak: Int = 0,
    val mastered: Boolean = false, val masteryEvidence: List<String> = emptyList(), val streakEvidence: List<String> = emptyList()) {
    init { require(bpm in 60..120 && bpm % 5 == 0 && highStreak in 0..2) }
}

@Serializable
data class RhythmProgress(val version: String = "rhythm-progress-v1",
    val categories: Map<RhythmCategory, RhythmCategoryProgress> = emptyMap()) {
    init { require(version == "rhythm-progress-v1") { "Versión de progreso rítmico no compatible." } }
    operator fun get(category: RhythmCategory) = categories[category] ?: RhythmCategoryProgress()
    val unlocked: Set<RhythmCategory> get() = setOf(RhythmCategory.QUARTER, RhythmCategory.EIGHTHS) +
        rhythmPrerequisites.filterValues { required -> required.all { get(it).mastered } }.keys
    fun tempo(selected: Set<RhythmCategory>): Int = selected.minOfOrNull { get(it).bpm } ?: 60
}

@Serializable
enum class RhythmDdaReason(val label: String) {
    PRACTICE("Práctica: conserva el progreso"), REPEATED("Partitura ya evaluada: práctica"),
    INTERRUPTED("Ejercicio interrumpido: conserva el progreso"), LOCKED("Categoría bloqueada"),
    INSUFFICIENT("Se requieren dos apariciones y dos unidades evaluables"),
    LOWER_TEMPO("Evidencia a tempo inferior: conserva la racha"),
    INVALID_TEMPO("Tempo distinto del recomendado para esta selección"),
    INVALID_OUTPUT("Sin inferencia válida: conserva el progreso"),
    FIRST_HIGH("Primer resultado alto: falta otro ejercicio nuevo"),
    INCREASED("Dos resultados altos: +5 BPM para el próximo ejercicio"),
    CEILING("Dos resultados altos: techo experimental de 120 BPM"),
    NOT_HIGH("Resultado no alto: reinicia la racha")
}

@Serializable
data class RhythmDdaDecision(val category: RhythmCategory, val occurrences: Int, val units: Int,
    val successes: Int, val extras: Int, val precision: Double?, val control: Double?,
    val inference: FuzzyInference?, val mastery: String?, val reason: RhythmDdaReason,
    val before: RhythmCategoryProgress, val after: RhythmCategoryProgress, val proposedDelta: Int = 0,
    val appliedDelta: Int = 0)

@Serializable
data class RhythmProgressCommit(val sequence: Long, val evaluatedRevision: Int,
    val before: RhythmProgress, val after: RhythmProgress, val decisions: List<RhythmDdaDecision>,
    val configuration: MamdaniConfiguration = rhythmInference(), val newlyUnlocked: Set<RhythmCategory> = emptySet())

/** Only committed records count. Progress and its evidence live in the same atomic JSON as the result. */
fun rhythmProgress(records: List<RhythmRecord>): RhythmProgress =
    records.mapNotNull { it.dda }.maxByOrNull { it.sequence }?.after ?: RhythmProgress()

internal fun prepareRhythmCommit(record: RhythmRecord, stored: List<RhythmRecord>): RhythmRecord {
    val previous = stored.firstOrNull { it.id == record.id }
    // R1–R5 records remain byte-compatible practice data and never acquire progress retroactively.
    if (record.result.origin != "progression" && previous?.dda == null) return record
    val last = stored.mapNotNull { it.dda }.maxOfOrNull { it.sequence } ?: 0L
    // A correction to an older exercise cannot replay decisions already used by subsequent exercises.
    // Its raw evaluation is updated, while evaluatedRevision identifies the sealed progress evidence.
    if (previous?.dda != null && previous.dda.sequence < last)
        return record.copy(dda = previous.dda, result = record.result.copy(origin = previous.result.origin))
    val before = previous?.dda?.before ?: rhythmProgress(stored)
    val others = stored.filter { it.id != record.id }
    val repeated = record.result.repeated || record.repeatedFromId != null ||
        others.any { it.result.completed && it.fingerprint == record.fingerprint }
    val result = if (repeated) record.result.copy(origin = "practice") else record.result
    val configuration = previous?.dda?.configuration ?: rhythmInference()
    val decisions = result.score.selected.distinct().map { category ->
        decideRhythmCategory(record.id, result, category, before, repeated, configuration)
    }
    var after = before.copy(categories = before.categories + decisions.associate { it.category to it.after })
    // Acquired tempo/mastery survive a later timestamp revision. Re-evaluate from the original
    // snapshot, never from the already advanced tempo, so a duplicate cannot count twice.
    previous?.dda?.after?.let { earned ->
        after = after.copy(categories = RhythmCategory.entries.associateWith { category ->
            val current = after[category]; val old = earned[category]
            current.copy(bpm = maxOf(current.bpm, old.bpm), mastered = current.mastered || old.mastered,
                masteryEvidence = if (old.mastered) old.masteryEvidence else current.masteryEvidence)
        })
    }
    return record.copy(result = result, dda = RhythmProgressCommit(previous?.dda?.sequence ?: last + 1,
        record.revision, before, after, decisions, configuration, after.unlocked - before.unlocked))
}

fun rhythmDiagnostics(record: RhythmRecord, progress: RhythmProgress): List<RhythmDdaDecision> =
    record.dda?.decisions ?: record.result.score.selected.map {
        decideRhythmCategory(record.id, record.result, it, progress, record.result.repeated, rhythmInference())
    }

private fun decideRhythmCategory(id: String, result: RhythmResult, category: RhythmCategory,
    progress: RhythmProgress, repeated: Boolean, configuration: MamdaniConfiguration): RhythmDdaDecision {
    val score = result.score
    val occurrences = score.occurrences.filter { it.target && it.category == category }
    val indices = occurrences.map { it.index }.toSet()
    val attacks = result.attacks.filter { it.occurrenceIndex in indices }
    val silences = result.silences.filter { it.occurrenceIndex in indices }
    val units = attacks.size + silences.size
    val successes = attacks.count { it.kind == TapKind.ON_TIME } + silences.count { it.respected == true }
    val extras = result.taps.count { tap -> tap.kind == TapKind.EXTRA && occurrences.any { cell ->
        val start = Beat(cell.bar * score.beatsPerBar) + cell.beat
        val end = cell.notes.fold(start) { sum, note -> sum + note.duration }
        tap.relativeNanos >= start.nanos(score.bpm) && tap.relativeNanos < end.nanos(score.bpm)
    } }
    val precision = if (units > 0) successes.toDouble() / units else null
    val control = if (units > 0) 1.0 - minOf(1.0, extras.toDouble() / units) else null
    val enough = occurrences.size >= 2 && units >= 2
    val inference = if (enough) inferMamdani(configuration, mapOf("precision" to precision!!, "control" to control!!)) else null
    val mastery = inference?.centroid?.let { if (it >= .75) "alto" else if (it >= .40) "medio" else "bajo" }
    val before = progress[category]
    val reason = when {
        repeated -> RhythmDdaReason.REPEATED
        result.origin != "progression" -> RhythmDdaReason.PRACTICE
        !result.completed -> RhythmDdaReason.INTERRUPTED
        !progress.unlocked.containsAll(score.selected) -> RhythmDdaReason.LOCKED
        !enough -> RhythmDdaReason.INSUFFICIENT
        score.bpm != progress.tempo(score.selected.toSet()) -> RhythmDdaReason.INVALID_TEMPO
        score.bpm < before.bpm -> RhythmDdaReason.LOWER_TEMPO
        mastery == null -> RhythmDdaReason.INVALID_OUTPUT
        mastery != "alto" -> RhythmDdaReason.NOT_HIGH
        before.highStreak == 0 -> RhythmDdaReason.FIRST_HIGH
        before.bpm >= 120 -> RhythmDdaReason.CEILING
        else -> RhythmDdaReason.INCREASED
    }
    val pair = reason == RhythmDdaReason.INCREASED || reason == RhythmDdaReason.CEILING
    val evidence = (before.streakEvidence + id).takeLast(2)
    val after = when (reason) {
        RhythmDdaReason.FIRST_HIGH -> before.copy(highStreak = 1, streakEvidence = listOf(id))
        RhythmDdaReason.INCREASED, RhythmDdaReason.CEILING -> before.copy(bpm = minOf(120, before.bpm + 5),
            highStreak = 0, streakEvidence = emptyList(), mastered = true,
            masteryEvidence = if (before.mastered) before.masteryEvidence else evidence)
        RhythmDdaReason.NOT_HIGH -> before.copy(highStreak = 0, streakEvidence = emptyList())
        else -> before
    }
    return RhythmDdaDecision(category, occurrences.size, units, successes, extras, precision, control,
        inference, mastery, reason, before, after, if (pair) 5 else 0, after.bpm - before.bpm)
}
