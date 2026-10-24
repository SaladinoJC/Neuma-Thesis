package ar.edu.unmdp.entrenamiento.defender

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

data class DefenderUiState(val game: DefenderState = DefenderState(), val letterNames: Boolean = false,
    val noteSound: Boolean = true, val effectsSound: Boolean = true, val error: String? = null)

class DefenderViewModel(private val audio: DefenderAudio = AndroidDefenderAudio(),
    private val clock: () -> Long = { System.nanoTime() / 1_000_000 }) : ViewModel() {
    private val session = DefenderSession(clock)
    private val mutableUi = MutableStateFlow(DefenderUiState())
    val ui = mutableUi.asStateFlow()
    private var ticker: Job? = null
    private val playing = mutableListOf<Job>()
    private val soundedResolutions = mutableSetOf<Int>()
    private val soundedMisses = mutableSetOf<Pair<Long, DefenderName?>>()

    fun letterNames(value: Boolean) { if (ui.value.game.phase == DefenderPhase.SETUP) mutableUi.value = ui.value.copy(letterNames = value) }
    fun noteSound(value: Boolean) { if (ui.value.game.phase == DefenderPhase.SETUP) mutableUi.value = ui.value.copy(noteSound = value) }
    fun effectsSound(value: Boolean) { if (ui.value.game.phase == DefenderPhase.SETUP) mutableUi.value = ui.value.copy(effectsSound = value) }
    fun start(seed: Int = Random.nextInt()) {
        cancelAudio(); ticker?.cancel(); soundedResolutions.clear(); soundedMisses.clear()
        val options = ui.value
        session.start(seed, DefenderConfiguration(noteSound = options.noteSound, effectsSound = options.effectsSound, letterNames = options.letterNames))
        mutableUi.value = options.copy(game = session.state, error = null)
        publish(); launchTicker()
    }
    fun select(name: DefenderName) { session.select(name); publish() }
    fun fire() {
        android.util.Log.i("[DEBUG-d1]", "semantic fire before=${session.state}")
        session.fire(); publish()
        android.util.Log.i("[DEBUG-d1]", "semantic fire after=${session.state}")
    }
    fun firePointer(eventUptimeMillis: Long, dispatchUptimeMillis: Long, capturedSelection: DefenderName) {
        val probeNow = clock()
        android.util.Log.i("[DEBUG-d1]", "pointer event=$eventUptimeMillis dispatch=$dispatchUptimeMillis clock=$probeNow source=${probeNow + eventUptimeMillis - dispatchUptimeMillis} captured=$capturedSelection before=${session.state}")
        session.fireAt(probeNow + eventUptimeMillis - dispatchUptimeMillis, capturedSelection)
        android.util.Log.i("[DEBUG-d1]", "pointer after=${session.state}")
        publish()
        if (session.state.phase == DefenderPhase.RUNNING && ticker?.isActive != true) launchTicker()
    }
    fun tick() { session.tick(); publish() }
    fun pause() {
        session.pause(); ticker?.cancel(); cancelAudio(); publish(allowAudio = false)
    }
    fun background() { pause() }
    fun resume() {
        if (session.state.phase != DefenderPhase.PAUSED) return
        session.resume(); publish(); launchTicker()
    }
    fun home() {
        session.interrupt(); ticker?.cancel(); cancelAudio(); publish(allowAudio = false)
        // Keep the interruption available to callers; re-entry offers preparation explicitly.
    }
    fun prepare() {
        home()
        session.prepare()
        mutableUi.value = ui.value.copy(game = session.state, error = null)
    }
    private fun launchTicker() {
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (session.state.phase == DefenderPhase.RUNNING) { tick(); delay(33) }
        }
    }
    private fun publish(allowAudio: Boolean = true) {
        val game = session.state
        mutableUi.value = ui.value.copy(game = game)
        val audible = allowAudio && game.phase in listOf(DefenderPhase.RUNNING, DefenderPhase.FINISHED)
        // Source-time correction can replace an arrival with a hit. An enemy sounds at most once.
        game.events.forEach { event ->
            val fresh = when (event.kind) {
                "SHOT_HIT", "ARRIVAL" -> soundedResolutions.add(requireNotNull(event.enemy).id)
                "SHOT_MISS" -> soundedMisses.add(event.atMillis to event.selection)
                else -> false
            }
            if (!fresh || !audible || game.activeMillis - event.atMillis !in 0..250) return@forEach
            when (event.kind) {
                "SHOT_HIT" -> {
                    if (ui.value.noteSound) play { audio.note(requireNotNull(event.enemy).note.midi) }
                    if (ui.value.effectsSound) play { audio.effect(DefenderEffect.HIT) }
                }
                "SHOT_MISS" -> if (ui.value.effectsSound) play { audio.effect(DefenderEffect.MISS) }
                "ARRIVAL" -> if (ui.value.effectsSound) play { audio.effect(DefenderEffect.ARRIVAL) }
            }
        }
    }
    private fun play(action: suspend () -> Int) {
        playing.removeAll { it.isCompleted }
        playing += viewModelScope.launch {
            try { action() }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { mutableUi.value = ui.value.copy(error = "No se pudo reproducir: ${error.message}") }
        }
    }
    private fun cancelAudio() { playing.forEach { it.cancel() }; playing.clear() }
    override fun onCleared() { ticker?.cancel(); cancelAudio(); super.onCleared() }
}
