package ar.edu.unmdp.entrenamiento.progress

import ar.edu.unmdp.entrenamiento.dda.*
import kotlinx.serialization.Serializable

@Serializable
data class Mastery(val inputs: Map<String, Double>, val configuration: MamdaniConfiguration,
    val inference: FuzzyInference, val reference: Int? = null, val threshold: Double = .75) {
    val high: Boolean get() = inference.centroid?.let { it >= threshold } == true
}
@Serializable
data class Progress(val level: Int = 0, val streak: Int = 0)

data class ProgressEntry(val id: String, val level: Int, val origin: String, val completed: Boolean, val mastery: Mastery?)

/** Replay in persisted order makes result + progress a single atomic write. */
fun progression(entries: List<ProgressEntry>): Progress {
    var progress = Progress()
    val seen = mutableSetOf<String>()
    for (entry in entries) {
        if (entry.id.isEmpty() || !seen.add(entry.id) || !entry.completed || entry.origin != "automatic" ||
            entry.mastery?.inference?.centroid == null || entry.level != progress.level) continue
        val streak = if (entry.mastery.high) progress.streak + 1 else 0
        progress = if (streak >= 2 && progress.level < 2) Progress(progress.level + 1)
            else progress.copy(streak = streak.coerceAtMost(2))
    }
    return progress
}

fun mastery(inputs: Map<String, Double>, reference: Int? = null): Mastery {
    val terms = mapOf("bajo" to Trapezoid(0.0, 0.0, .35, .55),
        "medio" to Trapezoid(.35, .60, .60, .85), "alto" to Trapezoid(.65, .85, 1.0, 1.0))
    fun condition(input: String, term: String) = FuzzyCondition(input, term)
    val rules = if (reference != null) terms.keys.mapIndexed { i, term ->
        FuzzyRule("S${i+1}", "Longitud $term → dominio $term", listOf(condition("longitud", term)), term)
    } else listOf(
        FuzzyRule("I1", "Exactitud baja → bajo", listOf(condition("exactitud", "bajo")), "bajo"),
        FuzzyRule("I2", "Exactitud media → medio", listOf(condition("exactitud", "medio")), "medio"),
        FuzzyRule("I3", "Exactitud alta y autonomía baja → medio", listOf(condition("exactitud", "alto"), condition("autonomía", "bajo")), "medio"),
        FuzzyRule("I4", "Exactitud alta y autonomía media → alto", listOf(condition("exactitud", "alto"), condition("autonomía", "medio")), "alto"),
        FuzzyRule("I5", "Exactitud alta y autonomía alta → alto", listOf(condition("exactitud", "alto"), condition("autonomía", "alto")), "alto"))
    val config = MamdaniConfiguration(if (reference != null) "mastery-simon-v1" else "mastery-instruments-v1",
        inputs.keys.associateWith { terms }, mapOf("bajo" to Trapezoid(0.0, 0.0, .20, .35),
            "medio" to Trapezoid(.25, .50, .50, .75), "alto" to Trapezoid(.65, .80, 1.0, 1.0)),
        rules, outputMinimum = 0.0, outputMaximum = 1.0)
    return Mastery(inputs, config, inferMamdani(config, inputs), reference)
}
