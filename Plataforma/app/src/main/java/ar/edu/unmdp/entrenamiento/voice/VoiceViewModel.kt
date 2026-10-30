package ar.edu.unmdp.entrenamiento.voice

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.UUID

data class VoiceUiState(val running: Boolean = false, val targetMidi: Int = 60,
    val latest: VoiceMeasurement? = null, val error: String? = null, val saved: Int = 0,
    val referencePlaying: Boolean = false, val recent: List<VoiceMeasurement> = emptyList(), val lastCapture: VoiceResult? = null,
    val pendingSave: Boolean = false, val savingPending: Boolean = false)
class VoiceViewModel(private val results: VoiceResults, private val recorder: VoiceCapture = AndroidVoiceCapture(),
    private val reference: ReferencePlayer = AndroidReferencePlayer(),
    private val monotonicNanos: () -> Long = System::nanoTime,
    private val wallClock: () -> Long = System::currentTimeMillis,
    private val deviceInfo: () -> Pair<String, String> = { "${Build.MANUFACTURER} ${Build.MODEL}" to "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})" }) : ViewModel() {
    private val mutableUi = MutableStateFlow(VoiceUiState())
    val ui = mutableUi.asStateFlow()
    private var capture: Job? = null
    private var referenceJob: Job? = null
    private var retryJob: Job? = null
    private var pending: VoiceResult? = null
    fun pendingForExport(): VoiceResult? = pending
    fun retrySave() {
        val result = pending ?: return
        if (ui.value.running || ui.value.savingPending) return
        mutableUi.value = ui.value.copy(savingPending = true)
        retryJob = viewModelScope.launch {
            try {
                results.save(result)
                pending = null
                mutableUi.value = ui.value.copy(pendingSave = false, saved = ui.value.saved+1, error = null)
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { mutableUi.value = ui.value.copy(error = "No se pudo guardar: ${error.message}") }
            finally { mutableUi.value = ui.value.copy(savingPending = false) }
        }
    }
    fun target(midi: Int) { if (!ui.value.running && !ui.value.referencePlaying) mutableUi.value = ui.value.copy(targetMidi = midi, latest = null) }
    fun permissionDenied() { mutableUi.value = ui.value.copy(error = "Micrófono no autorizado. Podés habilitarlo en Ajustes de Android.") }
    fun start() {
        if (capture?.isActive == true || ui.value.running || ui.value.referencePlaying || pending != null) return
        val target = ui.value.targetMidi
        mutableUi.value = ui.value.copy(running = true, latest = null, recent = emptyList(), error = null)
        capture = viewModelScope.launch {
            val measurements = mutableListOf<VoiceMeasurement>()
            val started = wallClock()
            val monotonic = monotonicNanos()
            var last = -200L
            var sampleRate = 0
            var failure: String? = null
            val configuration = YinConfiguration()
            try {
                withTimeoutOrNull(120_000) {
                    recorder.capture(configuration) { estimate, micros, rate ->
                        val elapsed = (monotonicNanos()-monotonic)/1_000_000
                        sampleRate = rate
                        if (elapsed-last >= 200) {
                            last = elapsed
                            val measurement = VoiceMeasurement(elapsed, target, estimate, micros, estimate.cents(target))
                            measurements.add(measurement)
                            withContext(Dispatchers.Main.immediate) {
                                mutableUi.value = ui.value.copy(latest = measurement,
                                    recent = (ui.value.recent + measurement).filter { it.elapsedMillis >= elapsed-10_000 })
                            }
                        }
                    }
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { failure = error.message ?: "Error de captura" }
            finally {
                withContext(NonCancellable) {
                    try {
                        val device = deviceInfo()
                        val result = VoiceResult(UUID.randomUUID().toString(), started, wallClock(),
                            device.first, device.second, configuration, sampleRate, measurements.toList(), error = failure)
                        pending = result
                        mutableUi.value = ui.value.copy(lastCapture = result, pendingSave = true)
                        results.save(result)
                        pending = null
                        mutableUi.value = ui.value.copy(saved = ui.value.saved+1, pendingSave = false)
                    } catch (error: Exception) { failure = "No se pudo guardar: ${error.message}" }
                    mutableUi.value = ui.value.copy(running = false, latest = null, error = failure)
                }
            }
        }
    }
    fun playReference() {
        if (ui.value.referencePlaying) return
        val target = ui.value.targetMidi
        mutableUi.value = ui.value.copy(referencePlaying = true, error = null)
        referenceJob = viewModelScope.launch {
            try {
                capture?.cancelAndJoin()
                reference.play(target)
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { mutableUi.value = ui.value.copy(error = "No se pudo reproducir el objetivo: ${error.message}") }
            finally { mutableUi.value = ui.value.copy(referencePlaying = false) }
        }
    }
    fun stop() { referenceJob?.cancel(); capture?.cancel() }
    suspend fun stopAndJoin() {
        referenceJob?.cancel(); capture?.cancel()
        referenceJob?.join(); capture?.join(); retryJob?.join()
    }
    override fun onCleared() { stop(); reference.close(); super.onCleared() }
    fun resetDisplay() { pending = null; mutableUi.value = VoiceUiState() }
}
