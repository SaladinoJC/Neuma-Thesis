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
    val simulations: ProgressSimulation? = null, val helpSimulation: InstrumentResult? = null, val syntheticPitchTests: List<PitchLabReport> = emptyList())

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
    suspend fun export(version: String, simulation: ProgressSimulation? = null, helpSimulation: InstrumentResult? = null): String = withContext(Dispatchers.IO) {
        mutex.withLock {
            val simon = JsonSimonResults(File(directory, names[0])).all()
            val instruments = JsonInstrumentResults(File(directory, names[1])).all()
            val voice = JsonVoiceResults(File(directory, names[2])).all()
            val pitchTests = JsonPitchLabResults(File(directory, names[3])).all()
            val data = DemonstrationExport(appVersion = version, exportedAtMillis = System.currentTimeMillis(),
                simon = simon, instruments = instruments, voice = voice,
                simonProgress = progression(simon.map { ProgressEntry(it.id, it.scale.ordinal, it.origin, it.completed, it.mastery) }),
                instrumentProgress = progression(instruments.map { ProgressEntry(it.id, it.level.ordinal, it.origin, it.completed, it.mastery) }),
                simulations = simulation, helpSimulation = helpSimulation, syntheticPitchTests = pitchTests)
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
    val simonProgress: Progress, val instrumentProgress: Progress)

/** Real session rules with deterministic audio/randomness and isolated memory stores. */
suspend fun simulateProgress(recovery: Boolean = false): ProgressSimulation {
    val simon = SimonSession({ 0 }, clock = { 0L })
    var target = Instrument.PIANO
    val player = object : InstrumentPlayer {
        override suspend fun play(instrument: Instrument, melody: InstrumentMelody, onStarted: () -> Unit): Int {
            target = instrument; onStarted(); return 0
        }
    }
    val instruments = InstrumentSession({ 0 }, player, clock = { 0L })
    val outcomes = if (recovery) listOf(true, false, true, true) else listOf(true, true)
    for (high in outcomes) {
        simon.startAutomatic(60)
        if (high) repeat(5) {
            simon.playbackFinished(); repeat(simon.state.sequence.size) { simon.answer(0) }
        }
        simon.playbackFinished(); simon.answer(1)
        instruments.startAutomatic()
        repeat(10) {
            instruments.listen()
            instruments.answer(if (high) target else instruments.state.options.first { it != target })
            instruments.nextQuestion()
        }
    }
    return ProgressSimulation(scenario = if (recovery) "Alta, baja, alta, alta" else "Dos altas consecutivas",
        simon = simon.history().map { it.copy(origin = "simulated") },
        instruments = instruments.history().map { it.copy(origin = "simulated") },
        simonProgress = simon.progress(), instrumentProgress = instruments.progress())
}
