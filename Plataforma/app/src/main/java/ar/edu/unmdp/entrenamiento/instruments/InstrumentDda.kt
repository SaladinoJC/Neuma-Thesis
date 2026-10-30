package ar.edu.unmdp.entrenamiento.instruments

import ar.edu.unmdp.entrenamiento.dda.*
import kotlinx.serialization.Serializable

@Serializable
data class InstrumentDdaConfiguration(
    val inference: MamdaniConfiguration = defaultHelpInference(),
    val actionThreshold: Double = .25,
) {
    init { require(actionThreshold.isFinite() && actionThreshold > 0.0 && actionThreshold <= 1.0) }
}

fun defaultHelpInference(): MamdaniConfiguration {
    val terms = mapOf("bajo" to Trapezoid(0.0, 0.0, .35, .55),
        "medio" to Trapezoid(.35, .60, .60, .85), "alto" to Trapezoid(.65, .85, 1.0, 1.0))
    fun condition(input: String, term: String) = FuzzyCondition(input, term)
    return MamdaniConfiguration("mamdani-help-v1", mapOf("exactitud" to terms, "autonomía" to terms),
        mapOf("reducir" to Trapezoid(-1.0, -1.0, -.5, 0.0), "mantener" to Trapezoid(-.5, 0.0, 0.0, .5),
            "aumentar" to Trapezoid(0.0, .5, 1.0, 1.0)),
        listOf(
            FuzzyRule("R1", "Exactitud baja → aumentar", listOf(condition("exactitud", "bajo")), "aumentar"),
            FuzzyRule("R2", "Exactitud media → mantener", listOf(condition("exactitud", "medio")), "mantener"),
            FuzzyRule("R3", "Exactitud alta y autonomía baja → mantener", listOf(condition("exactitud", "alto"), condition("autonomía", "bajo")), "mantener"),
            FuzzyRule("R4", "Exactitud alta y autonomía media → reducir", listOf(condition("exactitud", "alto"), condition("autonomía", "medio")), "reducir"),
            FuzzyRule("R5", "Exactitud alta y autonomía alta → reducir", listOf(condition("exactitud", "alto"), condition("autonomía", "alto")), "reducir"),
        ))
}

@Serializable
enum class DdaReason(val label: String) {
    INSUFFICIENT_DATA("Se necesitan tres respuestas"), COOLDOWN("Esperar dos rondas entre cambios"),
    LIMIT("Límite de una a tres escuchas"), MANUAL("Práctica manual: ayuda fija"),
    KEEP("La inferencia propone mantener"), APPLIED("Ajuste para la próxima pregunta"),
    INVALID_OUTPUT("Sin salida válida: mantener ayuda"), FINAL_ROUND("La partida terminó; no hay próxima pregunta"),
}

@Serializable
data class InstrumentDdaDecision(
    val afterRound: Int, val windowRounds: List<Int>, val correctCount: Int, val firstListenCorrectCount: Int,
    val accuracy: Double, val autonomy: Double, val inference: FuzzyInference?,
    val proposedDelta: Int, val appliedDelta: Int, val listensBefore: Int, val listensAfter: Int,
    val reason: DdaReason, val configurationVersion: String,
)

internal fun decideInstrumentHelp(answers: List<InstrumentAnswer>, listens: Int, lastChangeRound: Int?,
    mode: InstrumentMode, configuration: InstrumentDdaConfiguration): InstrumentDdaDecision {
    val recent = answers.takeLast(3)
    val round = answers.last().round
    val correct = recent.count { it.correct }
    val firstListen = recent.count { it.correct && it.listensUsed == 1 }
    val accuracy = correct.toDouble()/recent.size
    val autonomy = if (correct == 0) 0.0 else firstListen.toDouble()/correct
    val inference = if (recent.size == 3) inferMamdani(configuration.inference,
        mapOf("exactitud" to accuracy, "autonomía" to autonomy)) else null
    val output = inference?.centroid
    val proposed = when {
        output == null -> 0
        output <= -configuration.actionThreshold -> -1
        output >= configuration.actionThreshold -> 1
        else -> 0
    }
    val bounded = (listens+proposed).coerceIn(1, 3)
    val reason = when {
        mode == InstrumentMode.MANUAL -> DdaReason.MANUAL
        recent.size < 3 -> DdaReason.INSUFFICIENT_DATA
        output == null -> DdaReason.INVALID_OUTPUT
        answers.size == 10 -> DdaReason.FINAL_ROUND
        proposed == 0 -> DdaReason.KEEP
        lastChangeRound != null && round-lastChangeRound <= 2 -> DdaReason.COOLDOWN
        bounded == listens -> DdaReason.LIMIT
        else -> DdaReason.APPLIED
    }
    val applied = if (reason == DdaReason.APPLIED) bounded-listens else 0
    return InstrumentDdaDecision(round, recent.map { it.round }, correct, firstListen, accuracy, autonomy,
        inference, proposed, applied, listens, listens+applied, reason, configuration.inference.version)
}
