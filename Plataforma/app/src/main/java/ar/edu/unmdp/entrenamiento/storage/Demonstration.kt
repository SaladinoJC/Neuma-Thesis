package ar.edu.unmdp.entrenamiento.storage

import ar.edu.unmdp.entrenamiento.simon.*
import ar.edu.unmdp.entrenamiento.instruments.*
import ar.edu.unmdp.entrenamiento.voice.*
import ar.edu.unmdp.entrenamiento.progress.*
import ar.edu.unmdp.entrenamiento.tuner.TunerConfiguration
import ar.edu.unmdp.entrenamiento.tuner.JsonTunerConfiguration
import ar.edu.unmdp.entrenamiento.tuner.TunerResult
import ar.edu.unmdp.entrenamiento.tuner.JsonTunerResults
import ar.edu.unmdp.entrenamiento.rhythm.RhythmRecord
import ar.edu.unmdp.entrenamiento.rhythm.JsonRhythmResults
import ar.edu.unmdp.entrenamiento.rhythm.RhythmProgress
import ar.edu.unmdp.entrenamiento.rhythm.rhythmProgress
import ar.edu.unmdp.entrenamiento.defender.DefenderRecord
import ar.edu.unmdp.entrenamiento.defender.DefenderProgress
import ar.edu.unmdp.entrenamiento.defender.JsonDefenderResults
import ar.edu.unmdp.entrenamiento.defender.defenderProgress
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Serializable
data class DemonstrationExport(val schemaVersion: Int = 1, val appVersion: String,
    val exportedAtMillis: Long, val simon: List<SimonResult>, val instruments: List<InstrumentResult>,
    val voice: List<VoiceResult>, val simonProgress: Progress, val instrumentProgress: Progress,
    val simulations: ProgressSimulation? = null, val helpSimulation: InstrumentResult? = null, val syntheticPitchTests: List<PitchLabReport> = emptyList(), val ddaScenarios: DdaBenchReport? = null,
    val pendingVoiceSave: VoiceResult? = null, val tunerConfiguration: TunerConfiguration? = null,
    val tuner: List<TunerResult> = emptyList(), val pendingTunerSaves: List<TunerResult> = emptyList(),
    val rhythm: List<RhythmRecord> = emptyList(), val pendingRhythmSaves: List<RhythmRecord> = emptyList(),
    val rhythmProgress: RhythmProgress? = null, val defender: List<DefenderRecord> = emptyList(),
    val defenderProgress: DefenderProgress? = null, val pendingDefenderSaves: List<DefenderRecord> = emptyList(),
    val defenderProgressByVariant: Map<String, DefenderProgress> = emptyMap())

/** Accessed after sessions have drained their pending actions on the menu. */
class Demonstration(private val directory: File) {
    private val profile = ProfileFiles.inDirectory(directory)
    private val json = Json { encodeDefaults = true; prettyPrint = true }
    private val names = listOf("simon-results.json", "instrument-results.json", "voice-results.json", "pitch-lab-results.json", "tuner-configuration.json", "tuner-results.json", "rhythm-results.json", "defender-results.json")
    private val marker = File(directory, "reset-pending")
    init {
        if (marker.exists()) runBlocking(Dispatchers.IO) { profile.transaction {
            if (marker.exists()) { profile.generation.incrementAndGet(); finishReset() }
        } }
    }
    private fun finishReset() {
        names.forEach { name -> val file = File(directory, name); check(!file.exists() || file.delete()) { "No se pudo borrar $name" } }
        check(!marker.exists() || marker.delete()) { "No se pudo completar el reinicio." }
    }
    suspend fun export(version: String, simulation: ProgressSimulation? = null, helpSimulation: InstrumentResult? = null, ddaScenarios: DdaBenchReport? = null, pendingVoiceSave: VoiceResult? = null, pendingTunerSaves: List<TunerResult> = emptyList(), pendingRhythmSaves: List<RhythmRecord> = emptyList(), pendingDefenderSaves: List<DefenderRecord> = emptyList()): String = withContext(Dispatchers.IO) {
        profile.transaction {
            val simon = JsonSimonResults(File(directory, names[0])).all()
            val instruments = JsonInstrumentResults(File(directory, names[1])).all()
            val voice = JsonVoiceResults(File(directory, names[2])).all()
            val pitchTests = JsonPitchLabResults(File(directory, names[3])).all()
            val rhythm = JsonRhythmResults(File(directory, names[6])).all()
            val defender = JsonDefenderResults(File(directory, names[7])).all()
            val data = DemonstrationExport(appVersion = version, exportedAtMillis = System.currentTimeMillis(),
                simon = simon, instruments = instruments, voice = voice,
                simonProgress = progression(simon.map { ProgressEntry(it.id, it.scale.ordinal, it.origin, it.completed, it.mastery) }),
                instrumentProgress = progression(instruments.map { ProgressEntry(it.id, it.level.ordinal, it.origin, it.completed, it.mastery) }),
                simulations = simulation, helpSimulation = helpSimulation, syntheticPitchTests = pitchTests, ddaScenarios = ddaScenarios, pendingVoiceSave = pendingVoiceSave,
                tunerConfiguration = File(directory, names[4]).takeIf { it.exists() }?.let { JsonTunerConfiguration(it).load() },
                tuner = JsonTunerResults(File(directory, names[5])).all(), pendingTunerSaves = pendingTunerSaves,
                rhythm = rhythm, pendingRhythmSaves = pendingRhythmSaves, rhythmProgress = rhythmProgress(rhythm),
                defender = defender, defenderProgress = defenderProgress(defender), pendingDefenderSaves = pendingDefenderSaves,
                defenderProgressByVariant = listOf("notes", "chords").associateWith { defenderProgress(defender, it) })
            json.encodeToString(DemonstrationExport.serializer(), data)
        }
    }
    /** Copia los archivos conocidos sin interpretarlos ni reemplazar los originales dañados. */
    suspend fun writeRecovery(output: OutputStream, pendingVoiceSave: VoiceResult? = null, pendingTunerSaves: List<TunerResult> = emptyList(), pendingRhythmSaves: List<RhythmRecord> = emptyList(), pendingDefenderSaves: List<DefenderRecord> = emptyList()) = withContext(Dispatchers.IO) { profile.transaction {
        ZipOutputStream(output).use { zip ->
            names.forEach { name ->
                val file = File(directory, name)
                if (file.isFile) {
                    zip.putNextEntry(ZipEntry(name))
                    file.inputStream().use { it.copyTo(zip) }
                    zip.closeEntry()
                }
            }
            if (pendingVoiceSave != null) {
                zip.putNextEntry(ZipEntry("pending-voice-save.json"))
                zip.write(json.encodeToString(VoiceResult.serializer(), pendingVoiceSave).toByteArray(Charsets.UTF_8))
                zip.closeEntry()
            }
            if (pendingTunerSaves.isNotEmpty()) {
                zip.putNextEntry(ZipEntry("pending-tuner-saves.json"))
                zip.write(json.encodeToString(ListSerializer(TunerResult.serializer()), pendingTunerSaves).toByteArray(Charsets.UTF_8))
                zip.closeEntry()
            }
            if (pendingRhythmSaves.isNotEmpty()) {
                zip.putNextEntry(ZipEntry("pending-rhythm-saves.json"))
                zip.write(json.encodeToString(ListSerializer(RhythmRecord.serializer()), pendingRhythmSaves).toByteArray(Charsets.UTF_8))
                zip.closeEntry()
            }
            if (pendingDefenderSaves.isNotEmpty()) {
                zip.putNextEntry(ZipEntry("pending-defender-saves.json"))
                zip.write(json.encodeToString(ListSerializer(DefenderRecord.serializer()), pendingDefenderSaves).toByteArray(Charsets.UTF_8))
                zip.closeEntry()
            }
            zip.putNextEntry(ZipEntry("LEEME.txt"))
            zip.write("Copia de recuperación de archivos locales. Puede contener JSON dañado o de una versión incompatible. Configuración y resultados derivados, sin grabaciones. No es una importación ni una reparación automática.".toByteArray(Charsets.UTF_8))
            zip.closeEntry()
        }
    } }
    suspend fun reset() = withContext(Dispatchers.IO) { profile.transaction {
        profile.generation.incrementAndGet()
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
