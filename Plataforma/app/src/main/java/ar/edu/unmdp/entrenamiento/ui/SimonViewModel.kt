package ar.edu.unmdp.entrenamiento.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unmdp.entrenamiento.audio.NotePlayer
import ar.edu.unmdp.entrenamiento.simon.Phase
import ar.edu.unmdp.entrenamiento.simon.Scale
import ar.edu.unmdp.entrenamiento.simon.SimonResult
import ar.edu.unmdp.entrenamiento.simon.SimonSession
import ar.edu.unmdp.entrenamiento.simon.SimonState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

enum class AppScreen { MENU, SIMON_SETUP, SIMON_GAME }

data class SimonUiState(
    val game: SimonState = SimonState(),
    val selectedScale: Scale = Scale.PENTATONIC,
    val screen: AppScreen = AppScreen.MENU,
    val highlighted: Int? = null,
    val busy: Boolean = false,
    val preparing: Boolean = false,
    val history: List<SimonResult> = emptyList(),
    val error: String? = null,
) {
    val inGame: Boolean get() = screen == AppScreen.SIMON_GAME
}

class SimonViewModel(private val session: SimonSession, private val player: NotePlayer) : ViewModel() {
    private val mutableUi = MutableStateFlow(SimonUiState())
    val ui = mutableUi.asStateFlow()
    private val actions = Mutex()
    private var playback: Job? = null
    private var feedbackHighlight: Job? = null
    private var foreground = true
    private var returningHome = false

    init {
        viewModelScope.launch {
            try { refreshHistory() } catch (error: Exception) { report(error) }
        }
    }

    fun selectScale(scale: Scale) {
        if (!ui.value.inGame) mutableUi.value = ui.value.copy(selectedScale = scale)
    }

    fun openSimon() {
        if (!ui.value.inGame && !ui.value.busy && !returningHome) {
            mutableUi.value = ui.value.copy(screen = AppScreen.SIMON_SETUP, error = null)
        }
    }

    fun start() {
        if (ui.value.busy || returningHome) return
        cancelAudio()
        mutableUi.value = ui.value.copy(busy = true, preparing = true, error = null)
        viewModelScope.launch {
            actions.withLock {
                try {
                    session.start(ui.value.selectedScale, tonicMidi = Random.nextInt(60, 72))
                    mutableUi.value = ui.value.copy(game = session.state, screen = AppScreen.SIMON_GAME)
                    if (foreground && !returningHome) playSequence(session.state)
                    else interruptLocked()
                } catch (error: Exception) { report(error) }
            }
        }
    }

    fun answer(button: Int) {
        val before = ui.value
        if (before.busy || returningHome || !foreground || before.game.phase != Phase.RESPONSE) return
        viewModelScope.launch {
            actions.withLock {
                if (returningHome || !foreground || session.state.phase != Phase.RESPONSE) return@withLock
                try {
                    // Solo el guardado/fin de ronda bloquea acciones; el sonido nunca bloquea una respuesta.
                    mutableUi.value = ui.value.copy(busy = true, error = null)
                    session.answer(button)
                    mutableUi.value = ui.value.copy(game = session.state,
                        busy = session.state.phase == Phase.PLAYBACK)
                    if (session.state.phase == Phase.FINISHED) refreshHistory()
                    if (foreground && !returningHome) {
                        player.feedback(session.state.midiNotes[button])
                        flashFeedback(button)
                        if (session.state.phase == Phase.PLAYBACK) playSequence(session.state, afterResponse = true)
                    } else interruptLocked()
                } catch (error: Exception) { report(error) }
            }
        }
    }

    fun home() {
        if (returningHome) return
        returningHome = true
        cancelAudio()
        viewModelScope.launch {
            actions.withLock {
                try {
                    cancelAudio()
                    interruptLocked()
                    mutableUi.value = ui.value.copy(screen = AppScreen.MENU, busy = false, highlighted = null)
                } catch (error: Exception) { report(error) }
                finally { returningHome = false }
            }
        }
    }

    fun background() {
        foreground = false
        cancelAudio()
        viewModelScope.launch {
            actions.withLock {
                try { interruptLocked() } catch (error: Exception) { report(error) }
            }
        }
    }

    fun resume() { foreground = true }

    private fun flashFeedback(button: Int) {
        feedbackHighlight?.cancel()
        mutableUi.value = ui.value.copy(highlighted = button)
        feedbackHighlight = viewModelScope.launch {
            delay(100)
            mutableUi.value = ui.value.copy(highlighted = null)
        }
    }

    private fun cancelAudio() {
        playback?.cancel()
        feedbackHighlight?.cancel()
        player.stopFeedback()
    }

    override fun onCleared() {
        cancelAudio()
        player.close()
        super.onCleared()
    }

    private fun playSequence(snapshot: SimonState, afterResponse: Boolean = false) {
        playback?.cancel()
        playback = viewModelScope.launch {
            try {
                player.prepare(snapshot.midiNotes)
                mutableUi.value = ui.value.copy(preparing = false)
                if (afterResponse) delay(600)
                if (snapshot.phase == Phase.PLAYBACK) {
                    delay(250)
                    player.stopFeedback()
                    for (button in snapshot.sequence) {
                        mutableUi.value = ui.value.copy(highlighted = button)
                        player.play(snapshot.midiNotes[button])
                        mutableUi.value = ui.value.copy(highlighted = null)
                        delay(180)
                    }
                    actions.withLock {
                        session.playbackFinished()
                        mutableUi.value = ui.value.copy(game = session.state, busy = false)
                    }
                } else {
                    mutableUi.value = ui.value.copy(busy = false, highlighted = null)
                }
            } catch (timedOut: TimeoutCancellationException) {
                // Un límite interno de audio vencido es un fallo; salir/cancelar la pantalla sigue siendo cancelación.
                currentCoroutineContext().ensureActive()
                playbackFailed(IllegalStateException("El audio tardó demasiado en prepararse o reproducirse.", timedOut))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                playbackFailed(error)
            }
        }
    }

    private suspend fun playbackFailed(error: Exception) {
        actions.withLock {
            try { interruptLocked() } catch (_: Exception) { /* Conserva el error principal. */ }
            report(error)
        }
    }

    private suspend fun interruptLocked() {
        session.interrupt()
        mutableUi.value = ui.value.copy(game = session.state, busy = false, preparing = false, highlighted = null)
        refreshHistory()
    }

    private suspend fun refreshHistory() {
        mutableUi.value = ui.value.copy(history = session.history().sortedByDescending { it.finishedAtMillis })
    }

    private fun report(error: Exception) {
        if (error is CancellationException) throw error
        mutableUi.value = ui.value.copy(busy = false, preparing = false, highlighted = null,
            error = "No se pudo completar la acción: ${error.message ?: "error inesperado"}")
    }
}
