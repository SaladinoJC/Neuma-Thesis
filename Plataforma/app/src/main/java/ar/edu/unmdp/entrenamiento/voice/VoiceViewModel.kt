package ar.edu.unmdp.entrenamiento.voice

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.UUID

data class VoiceUiState(val running: Boolean = false, val targetMidi: Int = 60,
    val latest: VoiceMeasurement? = null, val error: String? = null, val saved: Int = 0)
class VoiceViewModel(private val results: JsonVoiceResults) : ViewModel() {
    private val mutableUi = MutableStateFlow(VoiceUiState())
    val ui = mutableUi.asStateFlow()
    private var capture: Job? = null
    fun target(midi: Int) { if (!ui.value.running) mutableUi.value = ui.value.copy(targetMidi = midi, latest = null) }
    fun permissionDenied() { mutableUi.value = ui.value.copy(error = "Micrófono no autorizado. Podés habilitarlo en Ajustes de Android.") }
    fun start() {
        if (capture?.isActive == true || ui.value.running) return
        val target = ui.value.targetMidi
        mutableUi.value = ui.value.copy(running = true, latest = null, error = null)
        capture = viewModelScope.launch {
            val measurements = mutableListOf<VoiceMeasurement>()
            val started = System.currentTimeMillis()
            val monotonic = System.nanoTime()
            var last = -200L
            var sampleRate = 0
            var failure: String? = null
            val configuration = YinConfiguration()
            try {
                withTimeoutOrNull(120_000) {
                    AndroidVoiceCapture().capture(configuration) { estimate, micros, rate ->
                        val elapsed = (System.nanoTime()-monotonic)/1_000_000
                        sampleRate = rate
                        if (elapsed-last >= 200) {
                            last = elapsed
                            val measurement = VoiceMeasurement(elapsed, target, estimate, micros, estimate.cents(target))
                            measurements.add(measurement)
                            withContext(Dispatchers.Main.immediate) { mutableUi.value = ui.value.copy(latest = measurement) }
                        }
                    }
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { failure = error.message ?: "Error de captura" }
            finally {
                withContext(NonCancellable) {
                    try {
                        results.save(VoiceResult(UUID.randomUUID().toString(), started, System.currentTimeMillis(),
                            "${Build.MANUFACTURER} ${Build.MODEL}", "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                            configuration, sampleRate, measurements.toList(), error = failure))
                        mutableUi.value = ui.value.copy(saved = ui.value.saved+1)
                    } catch (error: Exception) { failure = "No se pudo guardar: ${error.message}" }
                    mutableUi.value = ui.value.copy(running = false, latest = null, error = failure)
                }
            }
        }
    }
    fun stop() { capture?.cancel() }
    suspend fun stopAndJoin() { capture?.cancelAndJoin() }
    fun resetDisplay() { mutableUi.value = VoiceUiState() }
}
