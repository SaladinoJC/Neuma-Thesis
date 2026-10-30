package ar.edu.unmdp.entrenamiento.voice

import kotlinx.serialization.Serializable
import kotlin.math.pow

@Serializable
enum class VocalDdaScenario(val label: String) {
    GOOD("Dos series precisas"), LOW("Desviación sostenida"), OCTAVE("Error de octava"),
    INTERRUPTED("Series interrumpidas"), MANUAL("Práctica sin progreso"), ROUTE("Ruta completa sintética")
}

@Serializable
data class VocalSimulationReport(val scenario: VocalDdaScenario, val records: List<VocalIntervalRecord>,
    val progress: VocalProgress, val origin: String = "simulated", val scaffoldingLevel: Int = 1)

/** Same public session and commit path, isolated from the player's store and microphone. */
suspend fun simulateVocalProgress(scenario: VocalDdaScenario, scaffoldingLevel: Int = 1): VocalSimulationReport {
    require(scaffoldingLevel in 1..3)
    var identity = 0
    val store = MemoryVocalIntervalResults()
    val history = VocalIntervalHistorySession(store, now = { identity * 10_000L }, newId = { "sim-${++identity}" })
    val targets = if (scenario == VocalDdaScenario.ROUTE) vocalCurriculum.flatten() else listOf(0)
    suspend fun produce(level: Int, signal: VocalDdaScenario) {
    for (key in targets) repeat(2) { series ->
        val rounds = if (signal == VocalDdaScenario.INTERRUPTED) 1 else 5
        repeat(rounds) { index ->
            val manual = signal == VocalDdaScenario.MANUAL
            val tolerance = history.state.progress.forLevel(key, level).toleranceCents.toDouble()
            history.begin(if (level == 3) null else 57, key, if (level == 3) 0 else 1,
                "Señal sintética" to "Simulación sin hardware", "simulation",
                if (manual) "manual" else "dda", if (manual) null else VocalSeriesStep("sim-$level-$key-$series", index + 1), level)
            val round = VocalIntervalSession(key, VocalIntervalConfiguration(toleranceCents = tolerance),
                givenAnchorHz = if (level == 2) 220.0 else null)
            val anchor = if (level == 3) 196.0 else 220.0
            if (level != 2) {
                round.beginAnchor()
                for (t in 0L..500L step 50) round.accept(t, PitchEstimate(anchor))
            }
            if (signal == VocalDdaScenario.INTERRUPTED) round.interrupt("simulated-interruption")
            else {
                round.beginTarget()
                val error = when (signal) { VocalDdaScenario.LOW -> 100; VocalDdaScenario.OCTAVE -> 1200; else -> 0 }
                val hz = anchor * 2.0.pow((key * 100 + error) / 1200.0)
                for (t in 600L..1100L step 50) round.accept(t, PitchEstimate(hz))
            }
            history.finish(requireNotNull(round.state.result), 44100, null)
            history.savePending()
        }
    }
    }
    if (scenario != VocalDdaScenario.MANUAL) for (level in 1 until scaffoldingLevel) produce(level, VocalDdaScenario.GOOD)
    produce(scaffoldingLevel, scenario)
    return VocalSimulationReport(scenario, store.all(), history.state.progress, scaffoldingLevel = scaffoldingLevel)
}
