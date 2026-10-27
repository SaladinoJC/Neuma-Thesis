package ar.edu.unmdp.entrenamiento.tuner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unmdp.entrenamiento.voice.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class TunerUiState(val tuning: TunerState = TunerState(), val error: String? = null,
    val configuration: TunerConfigurationState = TunerConfigurationState(TunerConfiguration()), val configurationBusy: Boolean = false,
    val guided: GuidedTunerState = GuidedTunerState(), val referenceBusy: Boolean = false)

class TunerViewModel(private val recorder: VoiceCapture = AndroidVoiceCapture(),
    configurationStore: TunerConfigurationStore? = null,
    private val referencePlayer: TunerReferencePlayer? = null,
    private val clock: () -> Long = { System.nanoTime() / 1_000_000 }) : ViewModel() {
    private val session = TunerSession()
    private var guided = GuidedTunerSession()
    private val configuration = TunerConfigurationSession(configurationStore ?: MemoryTunerConfiguration(),
        initialConfiguration = if (configurationStore == null) TunerConfiguration() else null)
    private val mutableUi = MutableStateFlow(TunerUiState(configuration = configuration.state))
    val ui = mutableUi.asStateFlow()
    private var capture: Job? = null
    private var configurationJob: Job? = null
    private var referenceJob: Job? = null
    private var captureGeneration = 0L
    private fun guidedMode() = configuration.state.configuration?.mode == TunerMode.GUIDED
    private fun measurement() = if (guidedMode() && guided.state.active) guided.state.measurement else session.state
    private fun publish(error: String? = ui.value.error) {
        mutableUi.value = ui.value.copy(tuning = measurement(), guided = guided.state, error = error, configuration = configuration.state)
    }
    init { if (configurationStore != null) reload() }
    private fun configure(after: () -> Unit = {}, action: suspend () -> Unit) {
        stop()
        val previous = configurationJob
        mutableUi.value = ui.value.copy(configurationBusy = true)
        val operation = viewModelScope.launch(start = CoroutineStart.LAZY) {
            previous?.join()
            referenceJob?.join()
            capture?.cancelAndJoin()
            action()
            configuration.state.configuration?.let { session.configure(it.snapshot()) }
            after()
            publish(null)
            if (configurationJob === coroutineContext[Job]) mutableUi.value = ui.value.copy(configurationBusy = false)
        }
        configurationJob = operation
        operation.start()
    }
    fun reload() = configure { configuration.load() }
    fun resetDisplay() { guided = GuidedTunerSession(); reload() }
    suspend fun awaitConfigurationIdle() { referenceJob?.cancelAndJoin(); capture?.cancelAndJoin(); configurationJob?.join() }
    fun createGuitar(name: String) = configure { configuration.createGuitar(name) }
    fun selectGuitar(id: String) = configure { configuration.selectGuitar(id) }
    fun selectTuning(id: String) = configure { configuration.selectTuning(id) }
    fun saveCustom(name: String, notes: List<String>, id: String? = null) = configure { configuration.saveCustomNames(name, notes, id) }
    fun permissionDenied() { publish("Micrófono no autorizado. Podés habilitarlo en Ajustes de Android.") }
    fun criterion(value: TunerCriterion) = configure { configuration.preferences(value, session.state.tracking) }
    fun tracking(value: TunerTracking) = configure { configuration.preferences(session.state.criterion, value) }
    fun mode(value: TunerMode) = configure { configuration.guidedPreferences(value, configuration.state.configuration!!.finalReview) }
    fun finalReview(value: Boolean) = configure { configuration.guidedPreferences(configuration.state.configuration!!.mode, value) }
    fun referenceTimbre(value: ReferenceTimbre) {
        if (ui.value.configurationBusy || ui.value.referenceBusy || configuration.state.configuration == null) return
        mutableUi.value = ui.value.copy(configurationBusy = true)
        val operation = viewModelScope.launch(start = CoroutineStart.LAZY) {
            try { configuration.referenceTimbre(value); publish() }
            finally { if (configurationJob === coroutineContext[Job]) mutableUi.value = ui.value.copy(configurationBusy = false) }
        }
        configurationJob = operation
        operation.start()
    }
    fun leavePending() { if (!ui.value.referenceBusy) { guided.leavePending(); publish() } }
    fun fixString(number: Int?) {
        if (ui.value.configurationBusy || ui.value.referenceBusy || configuration.state.configuration == null || guidedMode()) return
        if (session.state.tracking == TunerTracking.CHROMATIC) {
            configure(after = { if (session.state.tracking == TunerTracking.STRINGS) session.fixString(number) }) {
                configuration.preferences(session.state.criterion, TunerTracking.STRINGS)
            }
        } else { session.fixString(number); publish() }
    }
    fun start() {
        if (capture?.isCompleted == false || ui.value.configurationBusy || ui.value.referenceBusy || configuration.state.configuration == null) return
        if (guidedMode()) {
            val setup = requireNotNull(configuration.state.configuration)
            guided.begin(setup.snapshot(), setup.finalReview)
        } else session.start()
        publish(null)
        launchCapture()
    }
    private fun launchCapture() {
        val generation = ++captureGeneration
        capture = viewModelScope.launch {
            try {
                coroutineScope {
                    var latest = clock()
                    launch {
                        while (isActive) {
                            delay(100)
                            if (generation == captureGeneration && clock() - latest > 250) { measure(PitchEstimate(), clock()); publish() }
                        }
                    }
                    recorder.capture(YinConfiguration()) { estimate, _, _ ->
                        withContext(Dispatchers.Main.immediate) {
                            ensureActive()
                            if (generation == captureGeneration && measurement().running) {
                                latest = clock(); measure(estimate, latest); publish()
                                if (guidedMode() && !guided.state.active) capture?.cancel()
                            }
                        }
                    }
                    // A recorder that returns must not leave the watchdog running.
                    currentCoroutineContext().cancelChildren()
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { if (generation == captureGeneration) publish("No se pudo capturar: ${error.message}") }
            finally { if (generation == captureGeneration) { session.stop(); guided.interrupt(); publish() } }
        }
    }
    private fun measure(estimate: PitchEstimate, timeMillis: Long) {
        if (guidedMode()) guided.measure(estimate, timeMillis) else session.measure(estimate, timeMillis)
    }
    fun playReference() {
        if (ui.value.referenceBusy || ui.value.configurationBusy) return
        val setup = configuration.state.configuration ?: return
        val target = measurement().targetMidi ?: run { publish("Fijá una cuerda para escuchar su referencia."); return }
        if (target !in tunerTargetRange) { publish("Referencia fuera de Do2–Mi4. No se cambió la octava."); return }
        val player = referencePlayer ?: run { publish("Salida de referencia no disponible."); return }
        val resume = measurement().running
        ++captureGeneration
        session.stop(); guided.suspendMeasurement()
        capture?.cancel()
        mutableUi.value = ui.value.copy(referenceBusy = true)
        publish(null)
        referenceJob = viewModelScope.launch {
            try {
                capture?.cancelAndJoin()
                player.play(setup.referenceTimbre, target)
                delay(TUNER_REFERENCE_SETTLE_MS)
                ensureActive()
                if (resume) {
                    if (guidedMode()) guided.resumeMeasurement() else session.start()
                    launchCapture()
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { guided.interrupt(); publish("No se pudo reproducir la referencia: ${error.message}") }
            finally { mutableUi.value = ui.value.copy(referenceBusy = false); publish() }
        }
    }
    fun stop() { ++captureGeneration; referenceJob?.cancel(); session.stop(); guided.interrupt(); publish(); capture?.cancel() }
    override fun onCleared() { stop(); configurationJob?.cancel(); super.onCleared() }
}
