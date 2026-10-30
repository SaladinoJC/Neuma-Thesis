package ar.edu.unmdp.entrenamiento.dda

import kotlinx.serialization.Serializable
import kotlin.math.min

@Serializable
data class Trapezoid(val a: Double, val b: Double, val c: Double, val d: Double) {
    init { require(listOf(a, b, c, d).all { it.isFinite() } && a <= b && b <= c && c <= d && a < d) }
    fun membership(x: Double): Double = when {
        !x.isFinite() || x < a || x > d -> 0.0
        x >= b && x <= c -> 1.0
        x < b -> (x-a)/(b-a)
        else -> (d-x)/(d-c)
    }
}

@Serializable
data class FuzzyCondition(val input: String, val term: String)

@Serializable
data class FuzzyRule(val id: String, val label: String, val conditions: List<FuzzyCondition>, val outputTerm: String)

@Serializable
data class MamdaniConfiguration(
    val version: String, val inputs: Map<String, Map<String, Trapezoid>>,
    val outputs: Map<String, Trapezoid>, val rules: List<FuzzyRule>,
    val outputMinimum: Double = -1.0, val outputMaximum: Double = 1.0,
    val samplingPoints: Int = 1001,
) {
    init {
        require(outputMinimum.isFinite() && outputMaximum.isFinite() && outputMinimum < outputMaximum)
        require(samplingPoints in 3..10001)
        require(rules.all { rule -> rule.conditions.isNotEmpty() && rule.outputTerm in outputs &&
            rule.conditions.all { it.term in inputs[it.input].orEmpty() } })
    }
}

@Serializable
data class RuleActivation(val id: String, val label: String, val strength: Double, val outputTerm: String)

@Serializable
data class FuzzyInference(
    val memberships: Map<String, Map<String, Double>>, val rules: List<RuleActivation>,
    val centroid: Double?, val diagnostic: String? = null,
)

/** AND/implicación mínimo, agregación máximo y centroide por integración trapezoidal. */
internal fun inferMamdani(configuration: MamdaniConfiguration, inputs: Map<String, Double>): FuzzyInference {
    if (configuration.inputs.keys.any { inputs[it]?.isFinite() != true })
        return FuzzyInference(emptyMap(), emptyList(), null, "Entrada ausente o no finita.")
    val memberships = configuration.inputs.mapValues { (input, terms) ->
        terms.mapValues { (_, shape) -> shape.membership(checkNotNull(inputs[input])) }
    }
    val activations = configuration.rules.map { rule ->
        RuleActivation(rule.id, rule.label, rule.conditions.minOf { memberships.getValue(it.input).getValue(it.term) }, rule.outputTerm)
    }
    var area = 0.0
    var moment = 0.0
    for (index in 0 until configuration.samplingPoints) {
        val x = configuration.outputMinimum + (configuration.outputMaximum-configuration.outputMinimum) *
            index / (configuration.samplingPoints-1)
        val membership = activations.maxOfOrNull { min(it.strength, configuration.outputs.getValue(it.outputTerm).membership(x)) } ?: 0.0
        val weight = if (index == 0 || index == configuration.samplingPoints-1) .5 else 1.0
        area += membership * weight
        moment += x * membership * weight
    }
    val centroid = if (area > 0.0) (moment/area).takeIf { it.isFinite() } else null
    return FuzzyInference(memberships, activations, centroid,
        if (centroid == null) "Ninguna regla produjo un área de salida válida." else null)
}
