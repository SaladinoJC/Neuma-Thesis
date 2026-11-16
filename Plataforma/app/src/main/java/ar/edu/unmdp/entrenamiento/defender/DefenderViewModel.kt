package ar.edu.unmdp.entrenamiento.defender

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

data class DefenderUiState(val game: DefenderState = DefenderState(), val letterNames: Boolean = false,
    val noteSound: Boolean = true, val effectsSound: Boolean = true, val error: String? = null,
    val stage: DefenderStage = DefenderStage.N1, val accidentals: Boolean = false, val maxEnemies: Int = 4,
    val progression: Boolean = false, val travelMillis: Long = 10_000, val spawnIntervalMillis: Long? = null,
    val history: DefenderHistoryState = DefenderHistoryState(), val historyBusy: Boolean = false,
    val historyOpen: Boolean = false, val detailId: String? = null, val currentRecordId: String? = null,
    val inspectorOpen: Boolean = false, val simulation: DefenderSimulation? = null,
    val simulationBusy: Boolean = false, val simulationError: String? = null,
    val variant: String = "notes", val chordQuality: DefenderChordQuality = DefenderChordQuality.MAJOR,
    val chordInversion: Int = 0, val chordClefs: DefenderChordClefs = DefenderChordClefs.TREBLE) {
    val progress: DefenderProgress get() = history.progressFor(variant)
    val chordTarget: DefenderChordTarget get() = DefenderChordTarget(chordQuality, chordInversion, chordClefs)
    fun canChoose(target: DefenderChordTarget) = !progression || target in progress.unlockedTargets
    fun targetFor(quality: DefenderChordQuality = chordQuality, clefs: DefenderChordClefs = chordClefs): DefenderChordTarget {
        val candidate = DefenderChordTarget(quality, minOf(chordInversion, quality.intervals.lastIndex), clefs)
        return if (canChoose(candidate)) candidate else candidate.copy(inversion = 0)
    }
}

class DefenderViewModel(private val audio: DefenderAudio = AndroidDefenderAudio(),
    private val clock: () -> Long = { System.nanoTime() / 1_000_000 },
    resultsStore: DefenderResults = MemoryDefenderResults(), wallClock: () -> Long = System::currentTimeMillis,
    private val simulationDispatcher: CoroutineDispatcher = Dispatchers.Default) : ViewModel() {
    constructor(audio: DefenderAudio, clock: () -> Long) : this(audio, clock, MemoryDefenderResults())
    private val session = DefenderSession(clock)
    private val history = DefenderHistorySession(resultsStore, wallClock)
    private val mutableUi = MutableStateFlow(DefenderUiState())
    val ui = mutableUi.asStateFlow()
    private var ticker: Job? = null
    private var historyJob: Job? = null
    private var historyOperations = 0
    private var simulationJob: Job? = null
    private var finalized = false
    private val playing = mutableListOf<Job>()
    private val soundedResolutions = mutableSetOf<Int>()
    private val soundedMisses = mutableSetOf<DefenderEvent>()

    init { historyAction { history.load() } }
    private fun historyAction(action: suspend () -> Unit) {
        val previous = historyJob; historyOperations++
        mutableUi.value = ui.value.copy(historyBusy = true)
        historyJob = viewModelScope.launch {
            try { previous?.join(); action() }
            finally { historyOperations--; mutableUi.value = ui.value.copy(history = history.state, historyBusy = historyOperations > 0) }
        }
    }

    fun letterNames(value: Boolean) { if (ui.value.game.phase == DefenderPhase.SETUP) mutableUi.value = ui.value.copy(letterNames = value) }
    fun noteSound(value: Boolean) { if (ui.value.game.phase == DefenderPhase.SETUP) mutableUi.value = ui.value.copy(noteSound = value) }
    fun effectsSound(value: Boolean) { if (ui.value.game.phase == DefenderPhase.SETUP) mutableUi.value = ui.value.copy(effectsSound = value) }
    fun stage(value: DefenderStage) {
        if (ui.value.variant != "notes" || ui.value.game.phase != DefenderPhase.SETUP || (ui.value.progression && value !in ui.value.progress.unlocked)) return
        mutableUi.value = ui.value.copy(stage = value)
    }
    fun progression(value: Boolean) {
        if (ui.value.game.phase != DefenderPhase.SETUP) return
        val stage = if (value && ui.value.variant == "notes" && ui.value.stage !in ui.value.progress.unlocked) DefenderStage.N1 else ui.value.stage
        val target = if (value && ui.value.variant == "chords" && ui.value.chordTarget !in ui.value.progress.unlockedTargets)
            DefenderChordTarget(DefenderChordQuality.MAJOR) else ui.value.chordTarget
        mutableUi.value = ui.value.copy(progression = value, stage = stage, chordQuality = target.quality,
            chordInversion = target.inversion, chordClefs = target.clefs, error = null)
    }
    fun variant(value: String) {
        if (ui.value.game.phase != DefenderPhase.SETUP || value !in setOf("notes", "chords")) return
        if (value != ui.value.variant) {
            simulationJob?.cancel()
            mutableUi.value = ui.value.copy(variant = value, error = null, simulation = null, simulationBusy = false, simulationError = null)
        }
        progression(ui.value.progression)
    }
    fun chordQuality(value: DefenderChordQuality) {
        chordTarget(ui.value.targetFor(quality = value))
    }
    fun chordClefs(value: DefenderChordClefs) { chordTarget(ui.value.targetFor(clefs = value)) }
    fun chordInversion(value: Int) {
        if (value !in ui.value.chordQuality.intervals.indices) return
        chordTarget(ui.value.chordTarget.copy(inversion = value))
    }
    fun chordTarget(value: DefenderChordTarget) {
        if (ui.value.game.phase != DefenderPhase.SETUP || ui.value.variant != "chords" || !ui.value.canChoose(value)) return
        mutableUi.value = ui.value.copy(chordQuality = value.quality, chordInversion = value.inversion, chordClefs = value.clefs)
    }
    fun travel(value: Long) {
        if (ui.value.game.phase == DefenderPhase.SETUP && !ui.value.progression && value in 3000..14_000 && value % 1000 == 0L)
            mutableUi.value = ui.value.copy(travelMillis = value)
    }
    fun spawnInterval(value: Long?) {
        if (ui.value.game.phase == DefenderPhase.SETUP && !ui.value.progression &&
            (value == null || (value in 1000..6000 && value % 500 == 0L)))
            mutableUi.value = ui.value.copy(spawnIntervalMillis = value)
    }
    fun accidentals(value: Boolean) { if (ui.value.game.phase == DefenderPhase.SETUP) mutableUi.value = ui.value.copy(accidentals = value) }
    fun maxEnemies(value: Int) { if (value in 1..4 && ui.value.game.phase == DefenderPhase.SETUP) mutableUi.value = ui.value.copy(maxEnemies = value) }
    fun accidental(value: DefenderAccidental) { session.accidental(value); publish() }
    fun start(seed: Int = Random.nextInt()) {
        if (ui.value.game.phase != DefenderPhase.SETUP) return
        if (ui.value.progression && (ui.value.historyBusy || ui.value.history.error != null || ui.value.history.pending.isNotEmpty())) {
            mutableUi.value = ui.value.copy(error = "Guardá o recuperá el historial antes de iniciar progresión."); return
        }
        if (ui.value.progression && (if (ui.value.variant == "chords") ui.value.chordTarget !in ui.value.progress.unlockedTargets else ui.value.stage !in ui.value.progress.unlocked)) {
            mutableUi.value = ui.value.copy(error = "Elegí una etapa desbloqueada."); return
        }
        cancelAudio(); ticker?.cancel(); soundedResolutions.clear(); soundedMisses.clear()
        val options = ui.value
        finalized = false
        session.start(seed, DefenderConfiguration(noteSound = options.noteSound, effectsSound = options.effectsSound, letterNames = options.letterNames,
            stage = if (options.variant == "chords") DefenderStage.N1 else options.stage, accidentals = options.accidentals, maxEnemies = options.maxEnemies,
            travelMillis = if (options.progression) 10_000 else options.travelMillis, variant = options.variant,
            chordQuality = options.chordQuality, chordInversion = options.chordInversion, chordClefs = options.chordClefs,
            spawnIntervalMillis = if (options.progression) null else options.spawnIntervalMillis),
            if (options.progression) "progression" else "practice")
        history.begin()
        mutableUi.value = options.copy(game = session.state, error = null)
        publish(); launchTicker()
    }
    fun select(name: DefenderName) { session.select(name); publish() }
    fun quality(value: DefenderChordQuality) { session.quality(value); publish() }
    fun fire() { session.fire(); publish() }
    fun firePointer(eventUptimeMillis: Long, dispatchUptimeMillis: Long, capturedSelection: DefenderName,
        capturedAccidental: DefenderAccidental = DefenderAccidental.NATURAL, capturedQuality: DefenderChordQuality? = null) {
        session.fireAt(clock() + eventUptimeMillis - dispatchUptimeMillis, capturedSelection, capturedAccidental, capturedQuality)
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
        session.interrupt(); session.sealInput(); finalized = true
        ticker?.cancel(); cancelAudio(); publish(allowAudio = false)
        // Keep the interruption available to callers; re-entry offers preparation explicitly.
    }
    fun prepare() {
        home()
        session.prepare()
        mutableUi.value = ui.value.copy(game = session.state, error = null)
    }
    fun openHistory() {
        if (session.state.phase in setOf(DefenderPhase.RUNNING, DefenderPhase.PAUSED)) return
        sealFinished()
        mutableUi.value = ui.value.copy(historyOpen = true, detailId = null)
        historyAction { history.load() }
    }
    fun closeHistory() { mutableUi.value = ui.value.copy(historyOpen = false, detailId = null) }
    fun detail(id: String?) { mutableUi.value = ui.value.copy(detailId = id?.takeIf { history.find(it) != null }) }
    fun filterHistory(filter: DefenderHistoryFilter) { history.filter(filter); publish(allowAudio = false) }
    fun retryHistory() { historyAction { history.savePending(); if (history.state.pending.isEmpty()) history.load() } }
    fun openInspector() {
        if (session.state.phase == DefenderPhase.RUNNING) pause()
        sealFinished()
        mutableUi.value = ui.value.copy(inspectorOpen = true)
    }
    fun closeInspector() { mutableUi.value = ui.value.copy(inspectorOpen = false) }
    private fun sealFinished() {
        if (session.state.phase == DefenderPhase.FINISHED) {
            session.sealInput(); finalized = true; publish(allowAudio = false)
        }
    }
    fun simulate(scenario: DefenderScenario, variant: String = ui.value.variant) {
        if (session.state.phase == DefenderPhase.RUNNING || ui.value.simulationBusy) return
        if (variant !in setOf("notes", "chords")) return
        mutableUi.value = ui.value.copy(simulationBusy = true, simulationError = null)
        simulationJob = viewModelScope.launch {
            try {
                val report = withContext(simulationDispatcher) { simulateDefender(scenario, variant) }
                mutableUi.value = ui.value.copy(simulation = report)
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { mutableUi.value = ui.value.copy(simulationError = "No se pudo simular: ${error.message}") }
            finally { mutableUi.value = ui.value.copy(simulationBusy = false) }
        }
    }
    fun pendingForExport() = history.state.pending.toList()
    suspend fun awaitIdle() { historyJob?.join() }
    suspend fun stopAndJoin() { home(); ticker?.join(); historyJob?.join() }
    fun resetDisplay() {
        ticker?.cancel(); simulationJob?.cancel(); cancelAudio(); history.clear(); session.prepare()
        mutableUi.value = DefenderUiState()
    }
    private fun launchTicker() {
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (session.state.phase == DefenderPhase.RUNNING) { tick(); delay(33) }
        }
    }
    private fun publish(allowAudio: Boolean = true) {
        val game = session.state
        val captured = history.capture(game, finalized = finalized || game.phase == DefenderPhase.INTERRUPTED)
        mutableUi.value = ui.value.copy(game = game, history = history.state,
            currentRecordId = history.currentId, historyBusy = historyOperations > 0)
        if (captured) historyAction { history.savePending() }
        val audible = allowAudio && game.phase in listOf(DefenderPhase.RUNNING, DefenderPhase.FINISHED)
        // Source-time correction can replace an arrival with a hit. An enemy sounds at most once.
        game.events.forEach { event ->
            val fresh = when (event.kind) {
                "SHOT_HIT", "ARRIVAL" -> soundedResolutions.add(requireNotNull(event.enemy).id)
                "SHOT_MISS" -> soundedMisses.add(event)
                else -> false
            }
            if (!fresh || !audible || game.activeMillis - event.atMillis !in 0..250) return@forEach
            when (event.kind) {
                "SHOT_HIT" -> {
                    if (ui.value.noteSound) play {
                        val enemy = requireNotNull(event.enemy)
                        enemy.chord?.let { audio.chord(it.notes.map { note -> note.midi }) } ?: audio.note(enemy.note.midi)
                    }
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
