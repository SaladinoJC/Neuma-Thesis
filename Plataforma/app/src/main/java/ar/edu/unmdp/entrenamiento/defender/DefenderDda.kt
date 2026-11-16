package ar.edu.unmdp.entrenamiento.defender

import ar.edu.unmdp.entrenamiento.dda.*
import kotlinx.serialization.Serializable

internal fun defenderSolidityTerms() = mapOf("baja" to Trapezoid(0.0, 0.0, .40, .60),
    "media" to Trapezoid(.40, .60, .70, .85), "alta" to Trapezoid(.70, .85, 1.0, 1.0))

fun defenderTravelInference(): MamdaniConfiguration {
    fun rule(id: String, q: String, a: String?, out: String): FuzzyRule = FuzzyRule(id,
        "Solidez $q${a?.let { " y rapidez $it" } ?: ""} → $out",
        listOf(FuzzyCondition("solidez", q)) + listOfNotNull(a?.let { FuzzyCondition("rapidez", it) }), out)
    return MamdaniConfiguration("defender-travel-v1",
        mapOf("solidez" to defenderSolidityTerms(), "rapidez" to mapOf(
            "lenta" to Trapezoid(0.0, 0.0, .25, .45), "media" to Trapezoid(.25, .45, .60, .80),
            "rápida" to Trapezoid(.60, .80, 1.0, 1.0))),
        mapOf("alargar" to Trapezoid(.20, .60, 1.0, 1.0), "mantener" to Trapezoid(-.30, -.10, .10, .30),
            "acortar" to Trapezoid(-1.0, -1.0, -.60, -.20)), listOf(
            rule("R1", "baja", null, "alargar"), rule("R2", "media", "lenta", "alargar"),
            rule("R3", "media", "media", "mantener"), rule("R4", "media", "rápida", "mantener"),
            rule("R5", "alta", "lenta", "alargar"), rule("R6", "alta", "media", "mantener"),
            rule("R7", "alta", "rápida", "acortar")))
}

fun defenderMasteryInference(): MamdaniConfiguration {
    fun rule(id: String, s: String, p: String?, out: String) = FuzzyRule(id,
        "Supervivencia $s${p?.let { " y precisión $it" } ?: ""} → $out",
        listOf(FuzzyCondition("supervivencia", s)) + listOfNotNull(p?.let { FuzzyCondition("precision", it) }), out)
    return MamdaniConfiguration("defender-mastery-v1",
        mapOf("supervivencia" to defenderSolidityTerms(), "precision" to defenderSolidityTerms()),
        mapOf("bajo" to Trapezoid(0.0, 0.0, .25, .45), "medio" to Trapezoid(.30, .45, .55, .70),
            "alto" to Trapezoid(.60, .80, 1.0, 1.0)), listOf(
            rule("R1", "baja", null, "bajo"), rule("R2", "media", "baja", "bajo"),
            rule("R3", "media", "media", "medio"), rule("R4", "media", "alta", "medio"),
            rule("R5", "alta", "baja", "bajo"), rule("R6", "alta", "media", "medio"),
            rule("R7", "alta", "alta", "alto")), outputMinimum = 0.0, outputMaximum = 1.0)
}

@Serializable
data class DefenderDdaConfiguration(val inference: MamdaniConfiguration = defenderTravelInference(),
    val windowSize: Int = 5, val cooldownResolutions: Int = 3, val minimumTravelMillis: Long = 3000,
    val maximumTravelMillis: Long = 14_000, val stepMillis: Long = 1000, val threshold: Double = .25,
    val masteryInference: MamdaniConfiguration = defenderMasteryInference()) {
    init {
        require(windowSize in 2..20 && cooldownResolutions in 1..20)
        require(minimumTravelMillis > 0 && minimumTravelMillis <= maximumTravelMillis && stepMillis in 1..1000)
        require(threshold.isFinite() && threshold > 0 && threshold < 1)
    }
}

@Serializable
enum class DefenderDdaReason(val label: String) {
    INSUFFICIENT("Se necesitan cinco enemigos resueltos"), COOLDOWN("Esperar tres nuevas resoluciones"),
    MANUAL("Práctica: viaje manual, propuesta sin aplicar"), INVALID_OUTPUT("Sin salida válida: conservar viaje"),
    KEEP("La inferencia propone mantener"), LIMIT("Límite experimental de tiempo de viaje"),
    APPLIED("Cambio aplicado solo a futuros enemigos")
}

@Serializable
data class DefenderDdaDecision(val afterResolved: Int, val enemyIds: List<Int>,
    val eventStartIndex: Int, val eventEndIndex: Int, val correctShots: Int, val failedShots: Int,
    val survival: Double, val precision: Double, val rapidity: Double, val solidity: Double,
    val inference: FuzzyInference?, val beforeTravelMillis: Long, val afterTravelMillis: Long,
    val proposedDeltaMillis: Long, val appliedDeltaMillis: Long, val reason: DefenderDdaReason,
    val evaluated: Boolean, val configurationVersion: String) {
    val attempts: Int get() = correctShots + failedShots
}

internal fun defenderTravelDecision(state: DefenderState): DefenderDdaDecision {
    val policy = state.configuration.dda
    val resolved = state.results.takeLast(policy.windowSize)
    // Event indices, rather than timestamp-only cuts, disambiguate simultaneous resolutions.
    val resolutionIndices = state.events.indices.filter { state.events[it].kind in setOf("SHOT_HIT", "ARRIVAL") }
    val start = resolutionIndices.getOrNull(resolutionIndices.size - resolved.size - 1)?.plus(1) ?: 0
    val events = state.events.drop(start)
    val correct = events.count { it.kind == "SHOT_HIT" }
    val failed = events.count { it.kind == "SHOT_MISS" }
    val survival = resolved.count { it.resolution == DefenderResolution.ELIMINATED }.toDouble() / resolved.size
    val precision = if (correct + failed == 0) 0.0 else correct.toDouble() / (correct + failed)
    val rapidity = 1.0 - resolved.map {
        if (it.resolution == DefenderResolution.ARRIVED) 1.0 else
            ((it.atMillis - it.enemy.appearedAtMillis).toDouble() / it.enemy.travelMillis).coerceIn(0.0, 1.0)
    }.average()
    val solidity = minOf(survival, precision)
    val enough = resolved.size >= policy.windowSize
    val inference = if (enough) inferMamdani(policy.inference, mapOf("solidez" to solidity, "rapidez" to rapidity)) else null
    val centroid = inference?.centroid
    val proposed = when {
        centroid == null -> 0L
        centroid > policy.threshold -> policy.stepMillis
        centroid < -policy.threshold -> -policy.stepMillis
        else -> 0L
    }
    val lastEvaluation = state.ddaDecisions.lastOrNull { it.evaluated }?.afterResolved
    val cooled = lastEvaluation == null || state.results.size - lastEvaluation >= policy.cooldownResolutions
    val before = state.currentTravelMillis
    val bounded = (before + proposed).coerceIn(policy.minimumTravelMillis, policy.maximumTravelMillis)
    val reason = when {
        !enough -> DefenderDdaReason.INSUFFICIENT
        !cooled -> DefenderDdaReason.COOLDOWN
        centroid == null -> DefenderDdaReason.INVALID_OUTPUT
        state.origin != "progression" -> DefenderDdaReason.MANUAL
        proposed == 0L -> DefenderDdaReason.KEEP
        bounded == before -> DefenderDdaReason.LIMIT
        else -> DefenderDdaReason.APPLIED
    }
    val after = if (reason == DefenderDdaReason.APPLIED) bounded else before
    return DefenderDdaDecision(state.results.size, resolved.map { it.enemy.id }, start, state.events.size,
        correct, failed, survival, precision, rapidity, solidity, inference, before, after, proposed,
        after - before, reason, enough && cooled && centroid != null, policy.inference.version)
}
