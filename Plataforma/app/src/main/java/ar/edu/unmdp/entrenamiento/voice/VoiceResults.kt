package ar.edu.unmdp.entrenamiento.voice

import kotlinx.serialization.Serializable
import ar.edu.unmdp.entrenamiento.storage.JsonResultsFile
import java.io.File

@Serializable
data class VoiceMeasurement(val elapsedMillis: Long, val targetMidi: Int, val estimate: PitchEstimate,
    val processingMicros: Long, val cents: Double?, val valid: Boolean = estimate.valid, val midi: Int? = estimate.midi)
@Serializable
data class VoiceResult(val id: String, val startedAtMillis: Long, val finishedAtMillis: Long,
    val device: String, val androidVersion: String, val configuration: YinConfiguration,
    val actualSampleRate: Int, val measurements: List<VoiceMeasurement>, val origin: String = "microphone",
    val audioSource: String = "VOICE_RECOGNITION", val error: String? = null)
class JsonVoiceResults(file: File) {
    private val store = JsonResultsFile(file, VoiceResult.serializer())
    suspend fun all(): List<VoiceResult> = store.all()
    suspend fun save(result: VoiceResult) = store.append(result) { it.id == result.id }
}
