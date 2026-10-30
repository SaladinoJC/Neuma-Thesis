package ar.edu.unmdp.entrenamiento.rhythm

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class RhythmUiState(val session: RhythmState = RhythmState(), val selected: Set<RhythmCategory> = emptySet(),
    val metronome: Boolean = true, val tapSound: Boolean = true, val busy: Boolean = false, val error: String? = null,
    val beatsPerBar: Int = 4, val history: RhythmHistoryState = RhythmHistoryState(),
    val historyBusy: Boolean = false, val historyOpen: Boolean = false, val detailId: String? = null,
    val currentRecordId: String? = null, val progression: Boolean = false, val practiceBpm: Int = 60,
    val progressOpen: Boolean = false, val simulation: RhythmDdaSimulation? = null, val simulationBusy: Boolean = false,
    val simulationError: String? = null)

class RhythmViewModel(private val audio: RhythmAudio = AndroidRhythmAudio(),
    private val clock: () -> Long = System::nanoTime,
    private val uptimeMillis: () -> Long = SystemClock::uptimeMillis,
    private val generationDispatcher: CoroutineDispatcher = Dispatchers.Default,
    resultsStore: RhythmResults = MemoryRhythmResults(),
    wallClock: () -> Long = System::currentTimeMillis) : ViewModel() {
    private val session = RhythmSession()
    private val history = RhythmHistorySession(resultsStore, wallClock)
    private val mutableUi = MutableStateFlow(RhythmUiState())
    val ui = mutableUi.asStateFlow()
    private var job: Job? = null
    private var historyJob: Job? = null
    private var historyOperations = 0
    private var simulationJob: Job? = null
    private var repeatedFromId: String? = null
    private var inputOffset = 0L
    init { historyAction { history.load() } }
    private fun publish() {
        val changed = session.state.result?.let(history::capture) == true
        mutableUi.value = ui.value.copy(session = session.state, history = history.state,
            historyBusy = historyOperations > 0, currentRecordId = history.currentId)
        if (changed) historyAction { history.savePending() }
    }
    private fun historyAction(action: suspend () -> Unit) {
        val previous = historyJob
        historyOperations++
        mutableUi.value = ui.value.copy(historyBusy = true)
        historyJob = viewModelScope.launch {
            try { previous?.join(); action() }
            finally {
                historyOperations--
                mutableUi.value = ui.value.copy(history = history.state, historyBusy = historyOperations > 0)
            }
        }
    }
    fun select(category: RhythmCategory) {
        if (session.active || ui.value.busy || job?.isCompleted == false) return
        if (ui.value.progression && category !in ui.value.history.progress.unlocked) return
        val selected = ui.value.selected
        mutableUi.value = ui.value.copy(selected = if (category in selected) selected - category else selected + category, error = null)
        session.setup(); publish()
    }
    fun progression(value: Boolean) {
        if (session.active || ui.value.busy || job?.isActive == true) return
        if (value == ui.value.progression) return
        mutableUi.value = ui.value.copy(progression = value,
            selected = if (value) ui.value.selected.intersect(ui.value.history.progress.unlocked) else ui.value.selected,
            error = null)
        repeatedFromId = null; session.setup(); publish()
    }
    fun practiceTempo(bpm: Int) {
        if (ui.value.progression || session.active || ui.value.busy || job?.isActive == true || bpm !in 60..120 || bpm % 5 != 0) return
        mutableUi.value = ui.value.copy(practiceBpm = bpm, error = null)
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
        val progressing = ui.value.progression
        if (progressing && (ui.value.historyBusy || ui.value.history.error != null || ui.value.history.pending.isNotEmpty())) {
            mutableUi.value = ui.value.copy(error = "Guardá o recuperá el historial antes de generar progresión."); return
        }
        if (progressing && !ui.value.history.progress.unlocked.containsAll(selected)) {
            mutableUi.value = ui.value.copy(error = "La progresión solo admite categorías desbloqueadas."); return
        }
        val bpm = if (progressing) ui.value.history.progress.tempo(selected) else ui.value.practiceBpm
        if (selected.isEmpty()) { mutableUi.value = ui.value.copy(error = "Elegí al menos una categoría."); return }
        mutableUi.value = ui.value.copy(busy = true, error = null)
        job = viewModelScope.launch {
            try {
                val generated = withContext(generationDispatcher) {
                    val context = currentCoroutineContext()
                    RhythmSession().apply { generate(selected, seed, meter, checkCancelled = { context.ensureActive() }) }.state
                }
                ensureActive()
                if (generated.score != null) {
                    val score = generated.score.copy(bpm = bpm)
                    val seen = progressing && history.state.results.any { it.result.completed && it.fingerprint == score.fingerprint }
                    session.preview(score, repeated = seen, origin = if (progressing && !seen) "progression" else "practice")
                    if (seen) mutableUi.value = ui.value.copy(error = "Esta partitura ya fue evaluada. Se conserva como práctica; generá otra para progresar.")
                }
                else { session.setup(); mutableUi.value = ui.value.copy(error = generated.error) }
                repeatedFromId = null
                publish()
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (failure: Exception) { mutableUi.value = ui.value.copy(error = "No se pudo generar: ${failure.message}") }
            finally { mutableUi.value = ui.value.copy(busy = false) }
        }
    }
    fun play() {
        if (ui.value.busy || job?.isCompleted == false || session.state.phase != RhythmPhase.PREVIEW) return
        if (session.state.origin == "progression" && (ui.value.historyBusy || ui.value.history.error != null || ui.value.history.pending.isNotEmpty())) {
            mutableUi.value = ui.value.copy(error = "Guardá o recuperá el historial antes de iniciar progresión."); return
        }
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
                history.begin(repeatedFromId)
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
    fun repeat() {
        if (!ui.value.busy) {
            repeatedFromId = history.currentId; session.repeat()
            mutableUi.value = ui.value.copy(progression = false, practiceBpm = session.state.score?.bpm ?: ui.value.practiceBpm)
            publish()
        }
    }
    fun openHistory() {
        if (session.active || ui.value.busy) return
        mutableUi.value = ui.value.copy(historyOpen = true, detailId = null)
        historyAction { history.load() }
    }
    fun closeHistory() { mutableUi.value = ui.value.copy(historyOpen = false, detailId = null) }
    fun openProgress() { if (!session.active && !ui.value.busy) mutableUi.value = ui.value.copy(progressOpen = true) }
    fun closeProgress() { mutableUi.value = ui.value.copy(progressOpen = false) }
    fun simulate(scenario: RhythmDdaScenario) {
        if (session.active || ui.value.simulationBusy) return
        mutableUi.value = ui.value.copy(simulationBusy = true, simulationError = null)
        simulationJob = viewModelScope.launch {
            try {
                val report = withContext(generationDispatcher) { simulateRhythmDda(scenario) }
                mutableUi.value = ui.value.copy(simulation = report)
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { mutableUi.value = ui.value.copy(simulationError = "No se pudo simular: ${error.message}") }
            finally { mutableUi.value = ui.value.copy(simulationBusy = false) }
        }
    }
    fun detail(id: String?) { mutableUi.value = ui.value.copy(detailId = id?.takeIf { history.find(it) != null }) }
    fun filterHistory(filter: RhythmHistoryFilter) { history.filter(filter); publish() }
    fun retryHistory() { historyAction { history.savePending(); if (history.state.pending.isEmpty()) history.load() } }
    fun repeatRecord(id: String) {
        if (session.active || ui.value.busy || job?.isActive == true) return
        val record = history.find(id) ?: return
        session.preview(record.result.score, repeated = true)
        if (session.state.score == null) { publish(); return }
        repeatedFromId = record.id
        mutableUi.value = ui.value.copy(selected = record.result.score.selected.toSet(),
            beatsPerBar = record.result.score.beatsPerBar, metronome = record.result.metronome,
            tapSound = record.result.tapSound, historyOpen = false, detailId = null, error = null)
        mutableUi.value = ui.value.copy(progression = false, practiceBpm = record.result.score.bpm)
        publish()
    }
    fun pendingForExport(): List<RhythmRecord> = history.state.pending.toList()
    suspend fun awaitIdle() { job?.join(); historyJob?.join() }
    suspend fun stopAndJoin() { background(); awaitIdle() }
    fun resetDisplay() {
        simulationJob?.cancel()
        history.clear(); session.setup(); repeatedFromId = null
        mutableUi.value = RhythmUiState()
    }
    fun background() {
        job?.cancel(); audio.stop(); session.interrupt()
        mutableUi.value = ui.value.copy(busy = false); publish()
    }
    fun home() = background()
    override fun onCleared() { background(); audio.close(); super.onCleared() }
}
