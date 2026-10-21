package ar.edu.unmdp.entrenamiento.storage

import ar.edu.unmdp.entrenamiento.simon.*
import ar.edu.unmdp.entrenamiento.instruments.*
import ar.edu.unmdp.entrenamiento.voice.*
import ar.edu.unmdp.entrenamiento.progress.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class DemonstrationExport(val schemaVersion: Int = 1, val appVersion: String,
    val exportedAtMillis: Long, val simon: List<SimonResult>, val instruments: List<InstrumentResult>,
    val voice: List<VoiceResult>, val simonProgress: Progress, val instrumentProgress: Progress,
    val simulations: ProgressSimulation? = null, val helpSimulation: InstrumentResult? = null, val syntheticPitchTests: List<PitchLabReport> = emptyList(), val ddaScenarios: DdaBenchReport? = null)

/** Accessed after sessions have drained their pending actions on the menu. */
class Demonstration(private val directory: File) {
    private val mutex = Mutex()
    private val json = Json { encodeDefaults = true; prettyPrint = true }
    private val names = listOf("simon-results.json", "instrument-results.json", "voice-results.json", "pitch-lab-results.json")
    private val marker = File(directory, "reset-pending")
    init { if (marker.exists()) finishReset() }
    private fun finishReset() {
        names.forEach { name -> val file = File(directory, name); check(!file.exists() || file.delete()) { "No se pudo borrar $name" } }
        check(!marker.exists() || marker.delete()) { "No se pudo completar el reinicio." }
    }
    suspend fun export(version: String, simulation: ProgressSimulation? = null, helpSimulation: InstrumentResult? = null, ddaScenarios: DdaBenchReport? = null): String = withContext(Dispatchers.IO) {
        mutex.withLock {
            val simon = JsonSimonResults(File(directory, names[0])).all()
            val instruments = JsonInstrumentResults(File(directory, names[1])).all()
            val voice = JsonVoiceResults(File(directory, names[2])).all()
            val pitchTests = JsonPitchLabResults(File(directory, names[3])).all()
            val data = DemonstrationExport(appVersion = version, exportedAtMillis = System.currentTimeMillis(),
                simon = simon, instruments = instruments, voice = voice,
                simonProgress = progression(simon.map { ProgressEntry(it.id, it.scale.ordinal, it.origin, it.completed, it.mastery) }),
                instrumentProgress = progression(instruments.map { ProgressEntry(it.id, it.level.ordinal, it.origin, it.completed, it.mastery) }),
                simulations = simulation, helpSimulation = helpSimulation, syntheticPitchTests = pitchTests, ddaScenarios = ddaScenarios)
            json.encodeToString(DemonstrationExport.serializer(), data)
        }
    }
    suspend fun reset() = withContext(Dispatchers.IO) { mutex.withLock {
        check(directory.isDirectory || directory.mkdirs())
        marker.writeText("Reset solicitado por el usuario", Charsets.UTF_8)
        finishReset()
    } }
}

@Serializable
data class ProgressSimulation(val origin: String = "simulated", val scenario: String,
    val simon: List<SimonResult>, val instruments: List<InstrumentResult>,
    val simonProgress: Progress, val instrumentProgress: Progress,
    val scenarioId: String = "", val steps: List<ProgressStep> = emptyList())

suspend fun simulateProgress(recovery: Boolean = false): ProgressSimulation =
    simulateProgress(if (recovery) ProgressScenario.RECOVERY else ProgressScenario.TWO_HIGH)
