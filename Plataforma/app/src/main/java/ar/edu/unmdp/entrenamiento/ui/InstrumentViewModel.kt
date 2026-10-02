package ar.edu.unmdp.entrenamiento.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unmdp.entrenamiento.instruments.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
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
    val setup: Boolean = true,
    val selectedLevel: InstrumentLevel = InstrumentLevel.A,
    val game: InstrumentState = InstrumentState(),
    val busy: Boolean = false,
    val history: List<InstrumentResult> = emptyList(),
    val error: String? = null,
)

class InstrumentViewModel(private val session: InstrumentSession, private val player: InstrumentPlayer) : ViewModel() {
    private val mutableUi = MutableStateFlow(InstrumentUiState())
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
        if (ui.value.setup && !ui.value.busy) mutableUi.value = ui.value.copy(selectedLevel = level)
    }

    fun start() = act {
        session.start(ui.value.selectedLevel)
        mutableUi.value = ui.value.copy(setup = false, game = session.state)
        playQuestion()
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
                    mutableUi.value = ui.value.copy(open = false, setup = true)
                } catch (error: Exception) { report(error) }
                finally { returningHome = false }
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

    fun resume() { foreground = true }

    private suspend fun interrupt() {
        session.interrupt()
        mutableUi.value = ui.value.copy(game = session.state, busy = false)
        refreshHistory()
    }

    private suspend fun refreshHistory() {
        mutableUi.value = ui.value.copy(history = session.history().sortedByDescending { it.finishedAtMillis })
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
