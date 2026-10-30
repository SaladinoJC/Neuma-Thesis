package ar.edu.unmdp.entrenamiento.voice

import ar.edu.unmdp.entrenamiento.dda.*
import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.sqrt

/** Experimental order, not a validated musical curriculum. */
val vocalCurriculum = listOf(setOf(0, 12, -12), setOf(7, -7, 5, -5),
    setOf(4, -4, 3, -3, 9, -9, 8, -8), setOf(2, -2, 1, -1, 11, -11, 10, -10), setOf(6, -6))

@Serializable
data class VocalSeriesStep(val id: String, val ordinal: Int) {
    init { require(id.isNotBlank() && ordinal in 1..5) }
}

@Serializable
data class VocalIntervalProgress(val toleranceCents: Int = 50, val highSeries: Int = 0,
    val mastered: Boolean = false, val masteryEvidence: List<String> = emptyList(),
    val streakEvidence: List<String> = emptyList()) {
    init { require(toleranceCents in 30..80 && toleranceCents % 5 == 0 && highSeries in 0..2) }
}

@Serializable
data class VocalProgress(val version: String = "vocal-progress-v1", val intervals: Map<Int, VocalIntervalProgress> = emptyMap(),
    val givenAnchorIntervals: Map<Int, VocalIntervalProgress> = emptyMap(),
    val freeProductionIntervals: Map<Int, VocalIntervalProgress> = emptyMap()) {
    init { require(version == "vocal-progress-v1") { "Versión de progreso vocal no compatible." } }
    operator fun get(semitones: Int) = intervals[semitones] ?: VocalIntervalProgress()
    fun forLevel(semitones: Int, level: Int) =
        (when (level) { 3 -> freeProductionIntervals; 2 -> givenAnchorIntervals; else -> intervals })[semitones] ?: VocalIntervalProgress()
    fun withInterval(semitones: Int, level: Int, state: VocalIntervalProgress) =
        if (level == 3) copy(freeProductionIntervals = freeProductionIntervals + (semitones to state))
        else if (level == 2) copy(givenAnchorIntervals = givenAnchorIntervals + (semitones to state))
        else copy(intervals = intervals + (semitones to state))
    fun unlockedFor(level: Int): Set<Int> = when (level) {
        1 -> unlocked
        2 -> unlocked.filter { get(it).mastered }.toSet()
        3 -> unlockedFor(2).filter { forLevel(it, 2).mastered }.toSet()
        else -> emptySet()
    }
    val unlocked: Set<Int> get() {
        val available = mutableSetOf<Int>()
        for (group in vocalCurriculum) {
            available += group
            if (!group.all { get(it).mastered }) break
        }
        return available
    }
}

fun vocalInference() = MamdaniConfiguration("vocal-mamdani-v1",
    inputs = mapOf(
        "precision" to mapOf("baja" to Trapezoid(0.0, 0.0, .55, .75),
            "media" to Trapezoid(.55, .75, .85, .95), "alta" to Trapezoid(.85, .95, 1.0, 1.0)),
        "regularidad" to mapOf("baja" to Trapezoid(0.0, 0.0, .50, .80), "alta" to Trapezoid(.65, .90, 1.0, 1.0))),
    outputs = mapOf("bajo" to Trapezoid(0.0, 0.0, .25, .45),
        "medio" to Trapezoid(.30, .45, .55, .70), "alto" to Trapezoid(.60, .80, 1.0, 1.0)),
    rules = listOf(
        FuzzyRule("V1", "Precisión baja → bajo", listOf(FuzzyCondition("precision", "baja")), "bajo"),
        FuzzyRule("V2", "Precisión media y regularidad baja → bajo", listOf(FuzzyCondition("precision", "media"), FuzzyCondition("regularidad", "baja")), "bajo"),
        FuzzyRule("V3", "Precisión media y regularidad alta → medio", listOf(FuzzyCondition("precision", "media"), FuzzyCondition("regularidad", "alta")), "medio"),
        FuzzyRule("V4", "Precisión alta y regularidad baja → medio", listOf(FuzzyCondition("precision", "alta"), FuzzyCondition("regularidad", "baja")), "medio"),
        FuzzyRule("V5", "Precisión alta y regularidad alta → alto", listOf(FuzzyCondition("precision", "alta"), FuzzyCondition("regularidad", "alta")), "alto")),
    outputMinimum = 0.0, outputMaximum = 1.0, samplingPoints = 1001)

@Serializable
enum class VocalDdaReason(val label: String) {
    LOCKED("Intervalo bloqueado"), INVALID_SERIES("Serie fuera de secuencia"),
    STALE("Tolerancia distinta de la recomendada"), INTERRUPTED("Ronda interrumpida"),
    INSUFFICIENT("Sostenido sin evidencia suficiente o protocolo distinto"),
    ADAPTED("Tolerancia preparada para la siguiente ronda"),
    FIRST_HIGH("Primera serie alta: falta otra"), MASTERED("Dos series altas: hito registrado"),
    NOT_HIGH("Serie no alta: reinicia la racha")
}

@Serializable
data class VocalDdaCommit(val sequence: Int, val configuration: MamdaniConfiguration,
    val before: VocalProgress, val after: VocalProgress, val eligible: Boolean,
    val reason: VocalDdaReason, val inference: FuzzyInference? = null,
    val samples: Int = 0, val meanAbsoluteCents: Double? = null, val deviationCents: Double? = null,
    val precision: Double? = null, val regularity: Double? = null, val band: String? = null,
    val proposedDelta: Int = 0, val appliedDelta: Int = 0, val seriesComplete: Boolean = false,
    val seriesHigh: Boolean = false, val abandonedPreviousSeries: Boolean = false,
    val unlocked: Set<Int> = emptySet(), val givenAnchorUnlocked: Set<Int> = emptySet(),
    val freeProductionUnlocked: Set<Int> = emptySet())

fun vocalProgress(records: List<VocalIntervalRecord>): VocalProgress =
    records.mapNotNull { it.dda }.maxByOrNull { it.sequence }?.after ?: VocalProgress()

/** Invoked under the same lock as the result write. Pending/uncommitted results never grant progress. */
fun commitVocalRecord(record: VocalIntervalRecord, records: List<VocalIntervalRecord>): VocalIntervalRecord {
    if (record.origin != "dda") return record.copy(dda = null)
    val before = vocalProgress(records)
    val key = record.result.targetSemitones
    val level = record.scaffoldingLevel
    val step = record.series
    val config = vocalInference()
    val sequence = (records.mapNotNull { it.dda?.sequence }.maxOrNull() ?: 0) + 1
    val earlier = records.filter { step != null && it.series?.id == step.id }.sortedBy { it.series!!.ordinal }
    val lastForKey = records.lastOrNull { it.origin == "dda" && it.scaffoldingLevel == level &&
        it.result.targetSemitones == key && it.dda?.eligible == true }
    val abandoned = step?.ordinal == 1 && lastForKey?.series?.let { it.id != step.id && it.ordinal < 5 } == true
    var state = before.forLevel(key, level)
    if (abandoned) state = state.copy(highSeries = 0, streakEvidence = emptyList())
    fun done(reason: VocalDdaReason): VocalIntervalRecord {
        val after = if (reason == VocalDdaReason.INTERRUPTED || reason == VocalDdaReason.INSUFFICIENT)
            before.withInterval(key, level, state.copy(highSeries = 0, streakEvidence = emptyList())) else before
        return record.copy(dda = VocalDdaCommit(sequence, config, before, after, false, reason,
            abandonedPreviousSeries = abandoned))
    }
    if (level !in 1..3) return done(VocalDdaReason.INVALID_SERIES)
    if (key !in before.unlockedFor(level)) return done(VocalDdaReason.LOCKED)
    if (step == null || earlier.size != step.ordinal - 1 ||
        earlier.withIndex().any { (i, old) -> old.series!!.ordinal != i + 1 || old.origin != "dda" ||
            old.result.targetSemitones != key || old.scaffoldingLevel != level || old.dda?.eligible != true }) return done(VocalDdaReason.INVALID_SERIES)
    if (record.result.configuration.toleranceCents != state.toleranceCents.toDouble()) return done(VocalDdaReason.STALE)
    if (record.result.interruption != null) return done(VocalDdaReason.INTERRUPTED)
    if (level != 2 && record.result.anchorSource != VocalAnchorSource.SUNG ||
        level == 2 && (record.result.anchorSource != VocalAnchorSource.GIVEN ||
            record.referenceAnchorMidi == null || record.referenceAnchorMidi !in 24..96 || record.result.anchorHz != referenceFrequency(record.referenceAnchorMidi) ||
            record.result.measurements.any { it.phase != VocalIntervalPhase.TARGET })) return done(VocalDdaReason.INSUFFICIENT)
    val c = record.result.configuration
    if (level == 3) {
        val anchor = record.result.measurements.filter { it.phase == VocalIntervalPhase.ANCHOR }
        val anchorEnd = anchor.lastOrNull()?.elapsedMillis ?: return done(VocalDdaReason.INSUFFICIENT)
        val held = anchor.filter { it.elapsedMillis >= anchorEnd - c.holdMillis }
        if (held.size < 6 || anchorEnd - held.first().elapsedMillis < c.holdMillis ||
            held.any { it.frequency?.let { hz -> hz.isFinite() && hz in c.minHz..c.maxHz } != true } ||
            held.zipWithNext().any { (a, b) -> b.elapsedMillis - a.elapsedMillis !in 1..100 })
            return done(VocalDdaReason.INSUFFICIENT)
    }
    val target = record.result.measurements.filter { it.phase == VocalIntervalPhase.TARGET }
    val end = target.lastOrNull()?.elapsedMillis ?: return done(VocalDdaReason.INSUFFICIENT)
    val window = target.filter { it.elapsedMillis >= end - c.holdMillis }
    if (c.holdMillis != 500L || c.maximumGapMillis != 100L || c.stabilityCents != 35.0 ||
        window.size < 5 || window.any { it.errorCents?.isFinite() != true || it.frequency == null } ||
        end - window.first().elapsedMillis < 400 || window.zipWithNext().any { (a, b) -> b.elapsedMillis - a.elapsedMillis !in 1..100 })
        return done(VocalDdaReason.INSUFFICIENT)
    val errors = window.map { it.errorCents!! }
    val mae = errors.map(::abs).average()
    val mean = errors.average()
    val deviation = sqrt(errors.sumOf { (it - mean) * (it - mean) } / errors.size)
    val precision = (1 - mae / 100).coerceIn(0.0, 1.0)
    val regularity = (1 - deviation / 35).coerceIn(0.0, 1.0)
    val inference = inferMamdani(config, mapOf("precision" to precision, "regularidad" to regularity))
    val centroid = inference.centroid ?: return done(VocalDdaReason.INSUFFICIENT)
    val band = if (centroid >= .70) "alto" else if (centroid >= .40) "medio" else "bajo"
    val delta = when (band) { "alto" -> -5; "bajo" -> 5; else -> 0 }
    val tolerance = (state.toleranceCents + delta).coerceIn(30, 80)
    val complete = step.ordinal == 5
    val high = complete && band == "alto" && earlier.all { it.dda?.band == "alto" }
    var reason = VocalDdaReason.ADAPTED
    if (complete) {
        val streak = if (high) (state.highSeries + 1).coerceAtMost(2) else 0
        val evidence = if (high) (state.streakEvidence + step.id).takeLast(2) else emptyList()
        val mastered = state.mastered || streak == 2
        state = state.copy(highSeries = streak, streakEvidence = evidence, mastered = mastered,
            masteryEvidence = if (state.mastered) state.masteryEvidence else if (streak == 2) evidence else emptyList())
        reason = if (!high) VocalDdaReason.NOT_HIGH else if (streak == 2) VocalDdaReason.MASTERED else VocalDdaReason.FIRST_HIGH
    }
    val after = before.withInterval(key, level, state.copy(toleranceCents = tolerance))
    return record.copy(dda = VocalDdaCommit(sequence, config, before, after, true, reason, inference,
        errors.size, mae, deviation, precision, regularity, band, delta, tolerance - before.forLevel(key, level).toleranceCents,
        complete, high, abandoned, after.unlockedFor(level) - before.unlockedFor(level),
        after.unlockedFor(2) - before.unlockedFor(2),
        after.unlockedFor(3) - before.unlockedFor(3)))
}
