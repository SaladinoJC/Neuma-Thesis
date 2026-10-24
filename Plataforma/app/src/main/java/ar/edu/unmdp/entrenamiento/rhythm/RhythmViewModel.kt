package ar.edu.unmdp.entrenamiento.rhythm

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class RhythmUiState(val session: RhythmState = RhythmState(), val selected: Set<RhythmCategory> = emptySet(),
    val metronome: Boolean = true, val tapSound: Boolean = true, val busy: Boolean = false, val error: String? = null,
    val beatsPerBar: Int = 4)

class RhythmViewModel(private val audio: RhythmAudio = AndroidRhythmAudio(),
    private val clock: () -> Long = System::nanoTime,
    private val uptimeMillis: () -> Long = SystemClock::uptimeMillis,
    private val generationDispatcher: CoroutineDispatcher = Dispatchers.Default) : ViewModel() {
    private val session = RhythmSession()
    private val mutableUi = MutableStateFlow(RhythmUiState())
    val ui = mutableUi.asStateFlow()
    private var job: Job? = null
    private var inputOffset = 0L
    private fun publish() { mutableUi.value = ui.value.copy(session = session.state) }
    fun select(category: RhythmCategory) {
        if (session.active || ui.value.busy || job?.isCompleted == false) return
        val selected = ui.value.selected
        mutableUi.value = ui.value.copy(selected = if (category in selected) selected - category else selected + category, error = null)
        session.setup(); publish()
    }
    fun metronome(value: Boolean) { if (!session.active && !ui.value.busy) mutableUi.value = ui.value.copy(metronome = value) }
    fun tapSound(value: Boolean) { if (!session.active && !ui.value.busy) mutableUi.value = ui.value.copy(tapSound = value) }
    fun meter(beats: Int) {
        if (beats !in 2..4 || session.active || ui.value.busy || job?.isCompleted == false) return
        mutableUi.value = ui.value.copy(beatsPerBar = beats, error = null)
        session.setup(); publish()
    }
    fun generate(seed: Long = clock()) {
        if (session.active || ui.value.busy || job?.isCompleted == false) return
        val selected = ui.value.selected.toSet()
        val meter = ui.value.beatsPerBar
        if (selected.isEmpty()) { mutableUi.value = ui.value.copy(error = "Elegí al menos una categoría."); return }
        mutableUi.value = ui.value.copy(busy = true, error = null)
        job = viewModelScope.launch {
            try {
                val generated = withContext(generationDispatcher) {
                    val context = currentCoroutineContext()
                    RhythmSession().apply { generate(selected, seed, meter, checkCancelled = { context.ensureActive() }) }.state
                }
                ensureActive()
                if (generated.score != null) session.preview(generated.score)
                else { session.setup(); mutableUi.value = ui.value.copy(error = generated.error) }
                publish()
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (failure: Exception) { mutableUi.value = ui.value.copy(error = "No se pudo generar: ${failure.message}") }
            finally { mutableUi.value = ui.value.copy(busy = false) }
        }
    }
    fun play() {
        if (ui.value.busy || job?.isCompleted == false || session.state.phase != RhythmPhase.PREVIEW) return
        val options = ui.value
        mutableUi.value = ui.value.copy(busy = true, error = null)
        job = viewModelScope.launch {
            try {
                val score = requireNotNull(session.state.score)
                val start = audio.start(score.bpm, options.metronome, score.beatsPerBar)
                ensureActive()
                inputOffset = clock() - uptimeMillis() * 1_000_000L
                session.play(start.originNanos, options.metronome, options.tapSound, start.source,
                    start.uncertaintyNanos, inputOffset)
                mutableUi.value = ui.value.copy(busy = false)
                while (isActive && session.active) {
                    // The visual refresh only observes an absolute monotonic timeline.
                    session.advance(clock()); publish()
                    if (session.active) delay(16)
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (failure: Exception) {
                session.interrupt()
                mutableUi.value = ui.value.copy(error = "No se pudo reproducir: ${failure.message}")
            } finally {
                audio.stop(); mutableUi.value = ui.value.copy(busy = false); publish()
            }
        }
    }
    fun tapAtUptimeMillis(eventTime: Long) = tap(eventTime * 1_000_000L + inputOffset, eventTime)
    fun tapNow() = tap(clock(), null)
    private fun tap(timestamp: Long, rawUptime: Long?) {
        val active = session.active
        val before = session.state.taps.size
        session.tap(timestamp, rawUptime)
        if (active && session.state.taps.size > before && ui.value.tapSound) {
            try { audio.tap() }
            catch (failure: Exception) { background(); mutableUi.value = ui.value.copy(error = "Falló el sonido del tap: ${failure.message}") }
        }
        publish()
    }
    fun repeat() { if (!ui.value.busy) { session.repeat(); publish() } }
    fun background() {
        job?.cancel(); audio.stop(); session.interrupt()
        mutableUi.value = ui.value.copy(busy = false); publish()
    }
    fun home() = background()
    override fun onCleared() { background(); audio.close(); super.onCleared() }
}
