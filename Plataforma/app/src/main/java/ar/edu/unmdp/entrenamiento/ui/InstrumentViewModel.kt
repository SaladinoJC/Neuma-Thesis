package ar.edu.unmdp.entrenamiento.ui

import ar.edu.unmdp.entrenamiento.progress.Progress
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unmdp.entrenamiento.instruments.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class InstrumentUiState(
    val open: Boolean = false,
    val progress: Progress = Progress(),
    val setup: Boolean = true,
    val selectedLevel: InstrumentLevel = InstrumentLevel.A,
    val game: InstrumentState = InstrumentState(),
    val busy: Boolean = false,
    val history: List<InstrumentResult> = emptyList(),
    val error: String? = null,
    val storageError: String? = null,
    val selectedMode: InstrumentMode = InstrumentMode.AUTOMATIC,
    val inspectorOpen: Boolean = false,
    val simulation: InstrumentResult? = null,
    val simulationBusy: Boolean = false,
    val simulationScenario: InstrumentDdaScenario? = null,
    val ddaConfiguration: InstrumentDdaConfiguration = InstrumentDdaConfiguration(),
)

class InstrumentViewModel(private val session: InstrumentSession, private val player: InstrumentPlayer,
    private val simulationDispatcher: CoroutineDispatcher = Dispatchers.Default) : ViewModel() {
    private val mutableUi = MutableStateFlow(InstrumentUiState(ddaConfiguration = session.ddaConfiguration))
    val ui = mutableUi.asStateFlow()
    private val actions = Mutex()
    private var playback: Job? = null
    private var foreground = true
    private var returningHome = false

    init { viewModelScope.launch { try { refreshHistory() } catch (error: Exception) { report(error) } } }

    fun open() {
        if (!ui.value.busy && !returningHome) mutableUi.value = ui.value.copy(open = true, setup = true, error = null)
    }

    fun selectLevel(level: InstrumentLevel) {
        if (ui.value.setup && !ui.value.busy) mutableUi.value = ui.value.copy(selectedLevel = level, selectedMode = InstrumentMode.MANUAL)
    }

    fun selectMode(mode: InstrumentMode) {
        if (mode != InstrumentMode.SIMULATED && ui.value.setup && !ui.value.busy)
            mutableUi.value = ui.value.copy(selectedMode = mode,
                selectedLevel = if (mode == InstrumentMode.AUTOMATIC) InstrumentLevel.entries[ui.value.progress.level] else ui.value.selectedLevel)
    }

    fun openInspector() { mutableUi.value = ui.value.copy(inspectorOpen = true) }
    fun closeInspector() { mutableUi.value = ui.value.copy(inspectorOpen = false) }
    fun simulateScenario(scenario: InstrumentDdaScenario, level: InstrumentLevel) {
        if (ui.value.simulationBusy) return
        mutableUi.value = ui.value.copy(simulationBusy = true, simulation = null)
        viewModelScope.launch {
            try {
                val simulation = withContext(simulationDispatcher) { simulateInstrumentDda(scenario, level, session.ddaConfiguration) }
                mutableUi.value = ui.value.copy(simulation = simulation, simulationScenario = scenario)
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { report(error) }
            finally { mutableUi.value = ui.value.copy(simulationBusy = false) }
        }
    }

    fun start() {
        if (ui.value.storageError != null) return
        act {
            refreshHistory()
            if (ui.value.selectedMode == InstrumentMode.AUTOMATIC) session.startAutomatic()
            else session.start(ui.value.selectedLevel, ui.value.selectedMode)
            mutableUi.value = ui.value.copy(setup = false, game = session.state)
            playQuestion()
        }
    }

    fun answer(instrument: Instrument) = act {
        session.answer(instrument)
        mutableUi.value = ui.value.copy(game = session.state, busy = false)
        if (session.state.phase == InstrumentPhase.FINISHED) refreshHistory()
    }

    fun next() = act {
        session.nextQuestion()
        mutableUi.value = ui.value.copy(game = session.state)
        if (session.state.phase == InstrumentPhase.READY) playQuestion()
        else mutableUi.value = ui.value.copy(busy = false)
    }

    fun listen() {
        if (ui.value.busy || !foreground || returningHome || session.state.listensRemaining == 0 ||
            session.state.phase !in listOf(InstrumentPhase.READY, InstrumentPhase.RESPONSE)) return
        mutableUi.value = ui.value.copy(busy = true, error = null)
        playQuestion()
    }

    private fun act(action: suspend () -> Unit) {
        if (ui.value.busy || !foreground || returningHome) return
        mutableUi.value = ui.value.copy(busy = true, error = null)
        viewModelScope.launch {
            actions.withLock {
                try {
                    if (foreground && !returningHome) action()
                    else mutableUi.value = ui.value.copy(busy = false)
                } catch (error: Exception) { report(error) }
            }
        }
    }

    private fun playQuestion() {
        playback = viewModelScope.launch {
            actions.withLock {
                try {
                    if (!foreground || returningHome) return@withLock
                    session.listen { mutableUi.value = ui.value.copy(game = session.state) }
                    mutableUi.value = ui.value.copy(game = session.state, busy = false)
                } catch (timedOut: TimeoutCancellationException) {
                    currentCoroutineContext().ensureActive()
                    report(IllegalStateException("El audio tardó demasiado en reproducirse.", timedOut))
                } catch (cancelled: CancellationException) { throw cancelled }
                catch (error: Exception) { report(error) }
            }
        }
    }

    fun home() {
        if (returningHome) return
        returningHome = true
        playback?.cancel()
        viewModelScope.launch {
            actions.withLock {
                try {
                    interrupt()
                    mutableUi.value = ui.value.copy(open = false, setup = true, inspectorOpen = false)
                } catch (error: Exception) { report(error) }
                finally {
                    mutableUi.value = ui.value.copy(open = false, setup = true, busy = false, inspectorOpen = false)
                    returningHome = false
                }
            }
        }
    }

    fun background() {
        foreground = false
        playback?.cancel()
        viewModelScope.launch {
            actions.withLock {
                try { interrupt() } catch (error: Exception) { report(error) }
            }
        }
    }

    suspend fun awaitIdle() = actions.withLock {
        session.interrupt()
    }
    suspend fun discardForReset() {
        playback?.cancel()
        actions.withLock { session.discardForReset() }
    }

    suspend fun reload() = actions.withLock {
        session.interrupt()
        mutableUi.value = InstrumentUiState(ddaConfiguration = session.ddaConfiguration)
        refreshHistory()
    }

    fun resume() { foreground = true }

    private suspend fun interrupt() {
        session.interrupt()
        mutableUi.value = ui.value.copy(game = session.state, busy = false)
        refreshHistory()
    }

    fun retryStorage() {
        viewModelScope.launch { actions.withLock {
            try { refreshHistory(); mutableUi.value = ui.value.copy(error = null) }
            catch (error: Exception) { report(error) }
        } }
    }

    private suspend fun refreshHistory() {
        try {
            val history = session.history().sortedByDescending { it.finishedAtMillis }
            val progress = session.progress()
            mutableUi.value = ui.value.copy(history = history, progress = progress, storageError = null)
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (error: Exception) {
            mutableUi.value = ui.value.copy(storageError = "No se puede leer el historial. Exportá los originales desde Datos y demostración antes de reiniciar.")
            throw error
        }
    }

    private fun report(error: Exception) {
        if (error is CancellationException) throw error
        mutableUi.value = ui.value.copy(game = session.state, busy = false,
            error = "No se pudo completar la acción: ${error.message ?: "error inesperado"}")
    }

    override fun onCleared() {
        playback?.cancel()
        player.close()
        super.onCleared()
    }
}
