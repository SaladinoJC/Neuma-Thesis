package ar.edu.unmdp.entrenamiento.tuner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unmdp.entrenamiento.voice.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class TunerUiState(val tuning: TunerState = TunerState(), val error: String? = null)

class TunerViewModel(private val recorder: VoiceCapture = AndroidVoiceCapture(),
    private val clock: () -> Long = { System.nanoTime() / 1_000_000 }) : ViewModel() {
    private val session = TunerSession()
    private val mutableUi = MutableStateFlow(TunerUiState())
    val ui = mutableUi.asStateFlow()
    private var capture: Job? = null
    private fun publish(error: String? = ui.value.error) { mutableUi.value = TunerUiState(session.state, error) }
    fun permissionDenied() { publish("Micrófono no autorizado. Podés habilitarlo en Ajustes de Android.") }
    fun criterion(value: TunerCriterion) { session.criterion(value); publish() }
    fun tracking(value: TunerTracking) { session.tracking(value); publish() }
    fun fixString(number: Int?) { session.fixString(number); publish() }
    fun start() {
        if (capture?.isCompleted == false) return
        session.start(); publish(null)
        capture = viewModelScope.launch {
            try {
                coroutineScope {
                    var latest = clock()
                    launch {
                        while (isActive) {
                            delay(100)
                            if (clock() - latest > 250) { session.measure(PitchEstimate(), clock()); publish() }
                        }
                    }
                    recorder.capture(YinConfiguration()) { estimate, _, _ ->
                        withContext(Dispatchers.Main.immediate) {
                            ensureActive()
                            if (session.state.running) {
                                latest = clock(); session.measure(estimate, latest); publish()
                            }
                        }
                    }
                    // A recorder that returns must not leave the watchdog running.
                    currentCoroutineContext().cancelChildren()
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { publish("No se pudo capturar: ${error.message}") }
            finally { session.stop(); publish() }
        }
    }
    fun stop() { session.stop(); publish(); capture?.cancel() }
    override fun onCleared() { stop(); super.onCleared() }
}
