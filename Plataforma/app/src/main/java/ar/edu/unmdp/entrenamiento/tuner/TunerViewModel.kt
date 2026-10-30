package ar.edu.unmdp.entrenamiento.tuner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unmdp.entrenamiento.voice.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class TunerUiState(val tuning: TunerState = TunerState(), val error: String? = null,
    val configuration: TunerConfigurationState = TunerConfigurationState(TunerConfiguration()), val configurationBusy: Boolean = false,
    val guided: GuidedTunerState = GuidedTunerState(), val referenceBusy: Boolean = false,
    val ear: EarTunerState = EarTunerState(), val history: TunerHistoryState = TunerHistoryState(),
    val historyBusy: Boolean = false, val historyOpen: Boolean = false)

class TunerViewModel(private val recorder: VoiceCapture = AndroidVoiceCapture(),
    configurationStore: TunerConfigurationStore? = null,
    private val referencePlayer: TunerReferencePlayer? = null,
    private val resultsStore: TunerResults = MemoryTunerResults(),
    private val clock: () -> Long = { System.nanoTime() / 1_000_000 }) : ViewModel() {
    private val session = TunerSession()
    private var guided = GuidedTunerSession()
    private var ear = EarTunerSession()
    private var history = TunerHistorySession(resultsStore)
    private val configuration = TunerConfigurationSession(configurationStore ?: MemoryTunerConfiguration(),
        initialConfiguration = if (configurationStore == null) TunerConfiguration() else null)
    private val mutableUi = MutableStateFlow(TunerUiState(configuration = configuration.state))
    val ui = mutableUi.asStateFlow()
    private var capture: Job? = null
    private var configurationJob: Job? = null
    private var referenceJob: Job? = null
    private var historyJob: Job? = null
    private var captureGeneration = 0L
    private fun guidedMode() = configuration.state.configuration?.mode == TunerMode.GUIDED
    private fun earMode() = configuration.state.configuration?.mode == TunerMode.TRAINING
    private fun measurement() = when {
        earMode() -> ear.state.measurement
        guidedMode() && guided.state.active -> guided.state.measurement
        else -> session.state
    }
    private fun publish(error: String? = ui.value.error) {
        val sealed = history.finish(guided.state, ear.state)
        mutableUi.value = ui.value.copy(tuning = measurement(), guided = guided.state, ear = ear.state, error = error, configuration = configuration.state, history = history.state)
        if (sealed) retryHistory()
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
    fun reload() = configure { configuration.load(); history.load() }
    fun resetDisplay() { guided = GuidedTunerSession(); ear = EarTunerSession(); history = TunerHistorySession(resultsStore); mutableUi.value = ui.value.copy(historyOpen = false); reload() }
    suspend fun awaitConfigurationIdle() { referenceJob?.cancelAndJoin(); capture?.cancelAndJoin(); configurationJob?.join(); historyJob?.join() }
    fun pendingForExport() = history.state.pending.toList()
    fun filterHistory(filter: TunerHistoryFilter) { history.filter(filter); publish() }
    fun openHistory() { stop(); history.filter(TunerHistoryFilter(mode = TunerMode.TRAINING)); mutableUi.value = ui.value.copy(historyOpen = true); publish(); historyOperation(loadOnly = true) }
    fun closeHistory() { mutableUi.value = ui.value.copy(historyOpen = false) }
    fun retryHistory() = historyOperation()
    private fun historyOperation(loadOnly: Boolean = false) {
        val previous = historyJob
        mutableUi.value = ui.value.copy(historyBusy = true)
        val operation = viewModelScope.launch(start = CoroutineStart.LAZY) {
            previous?.join()
            if (loadOnly) history.load() else history.savePending()
            publish()
            if (historyJob === coroutineContext[Job]) mutableUi.value = ui.value.copy(historyBusy = false)
        }
        historyJob = operation; operation.start()
    }
    fun createGuitar(name: String) = configure { configuration.createGuitar(name) }
    fun selectGuitar(id: String) = configure { configuration.selectGuitar(id) }
    fun selectTuning(id: String) = configure { configuration.selectTuning(id) }
    fun saveCustom(name: String, notes: List<String>, id: String? = null) = configure { configuration.saveCustomNames(name, notes, id) }
    fun permissionDenied() { publish("Micrófono no autorizado. Podés habilitarlo en Ajustes de Android.") }
    fun criterion(value: TunerCriterion) = configure { configuration.preferences(value, session.state.tracking) }
    fun tracking(value: TunerTracking) = configure { configuration.preferences(session.state.criterion, value) }
    fun mode(value: TunerMode) = configure { configuration.guidedPreferences(value, configuration.state.configuration!!.finalReview) }
    fun finalReview(value: Boolean) = configure { configuration.guidedPreferences(configuration.state.configuration!!.mode, value) }
    fun earClassification(value: Boolean) = configure { configuration.earClassification(value) }
    fun saveHistory(value: Boolean) { if (!guided.state.active && !ear.state.active) configure { configuration.saveHistory(value) } }
    fun classify(value: EarDirection) { if (!ui.value.referenceBusy) { ear.classify(value, clock()); publish() } }
    fun checkEar() {
        if (!earMode() || ui.value.referenceBusy || !ear.state.measurement.running || ear.state.phase != EarPhase.HIDDEN) return
        ear.check()
        if (ear.state.phase == EarPhase.CHECKING) {
            val generation = ++captureGeneration
            val previous = capture
            previous?.cancel()
            ear.suspendMeasurement()
            viewModelScope.launch {
                previous?.join()
                if (generation == captureGeneration && ear.state.active) { ear.resumeMeasurement(); launchCapture(); publish() }
            }
        }
        publish()
    }
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
    fun leavePending() { if (!ui.value.referenceBusy) { if (earMode()) ear.leavePending() else guided.leavePending(); publish() } }
    fun fixString(number: Int?) {
        if (ui.value.configurationBusy || ui.value.referenceBusy || configuration.state.configuration == null || guidedMode() || earMode()) return
        if (session.state.tracking == TunerTracking.CHROMATIC) {
            configure(after = { if (session.state.tracking == TunerTracking.STRINGS) session.fixString(number) }) {
                configuration.preferences(session.state.criterion, TunerTracking.STRINGS)
            }
        } else { session.fixString(number); publish() }
    }
    fun start() {
        if (capture?.isCompleted == false || ui.value.configurationBusy || ui.value.referenceBusy || configuration.state.configuration == null) return
        if (earMode()) {
            val setup = requireNotNull(configuration.state.configuration)
            ear.begin(setup.snapshot(), setup.finalReview, setup.earClassification)
        } else if (guidedMode()) {
            val setup = requireNotNull(configuration.state.configuration)
            guided.begin(setup.snapshot(), setup.finalReview)
        } else session.start()
        history.begin(requireNotNull(configuration.state.configuration))
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
                                if ((guidedMode() && !guided.state.active) || (earMode() && !ear.state.active)) capture?.cancel()
                            }
                        }
                    }
                    // A recorder that returns must not leave the watchdog running.
                    currentCoroutineContext().cancelChildren()
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { if (generation == captureGeneration) publish("No se pudo capturar: ${error.message}") }
            finally { if (generation == captureGeneration) { session.stop(); guided.interrupt(); ear.interrupt(); publish() } }
        }
    }
    private fun measure(estimate: PitchEstimate, timeMillis: Long) {
        if (earMode()) ear.measure(estimate, timeMillis) else if (guidedMode()) guided.measure(estimate, timeMillis) else session.measure(estimate, timeMillis)
    }
    fun playReference() {
        if (ui.value.referenceBusy || ui.value.configurationBusy) return
        val setup = configuration.state.configuration ?: return
        val target = measurement().targetMidi ?: run { publish("Fijá una cuerda para escuchar su referencia."); return }
        if (target !in tunerTargetRange) { publish("Referencia fuera de Do2–Mi4. No se cambió la octava."); return }
        val player = referencePlayer ?: run { publish("Salida de referencia no disponible."); return }
        history.referenceRequested(setup.referenceTimbre, target)
        val resume = measurement().running
        ++captureGeneration
        session.stop(); guided.suspendMeasurement(); ear.suspendMeasurement()
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
                    if (earMode()) ear.resumeMeasurement() else if (guidedMode()) guided.resumeMeasurement() else session.start()
                    launchCapture()
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { guided.interrupt(); ear.interrupt(); publish("No se pudo reproducir la referencia: ${error.message}") }
            finally { mutableUi.value = ui.value.copy(referenceBusy = false); publish() }
        }
    }
    fun stop() { ++captureGeneration; referenceJob?.cancel(); session.stop(); guided.interrupt(); ear.interrupt(); publish(); capture?.cancel() }
    override fun onCleared() { stop(); configurationJob?.cancel(); super.onCleared() }
}
