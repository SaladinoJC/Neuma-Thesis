package ar.edu.unmdp.entrenamiento.voice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import android.os.Build
import ar.edu.unmdp.entrenamiento.BuildConfig
import java.util.UUID

data class VocalSeriesState(val id: String, val semitones: Int, val ordinal: Int = 1, val ended: Boolean = false,
    val scaffoldingLevel: Int = 1)

data class VocalIntervalUiState(
    val semitones: Int = 7,
    val anchorMidi: Int = 57,
    val heardReference: Boolean = false,
    val referencePlaying: Boolean = false,
    val microphoneActive: Boolean = false,
    val transitioning: Boolean = false,
    val round: VocalIntervalState? = null,
    val frequencyHz: Double? = null,
    val actualSampleRate: Int? = null,
    val remainingSeconds: Int = 60,
    val error: String? = null,
    val permissionDenied: Boolean = false,
    val toleranceCents: Double = 50.0,
    val history: VocalIntervalHistoryState = VocalIntervalHistoryState(),
    val historyOpen: Boolean = false,
    val historyBusy: Boolean = false,
    val currentRecordId: String? = null,
    val progression: Boolean = false, val series: VocalSeriesState? = null,
    val progressOpen: Boolean = false, val simulation: VocalSimulationReport? = null,
    val simulationBusy: Boolean = false,
    val scaffoldingLevel: Int = 1,
) {
    val working: Boolean get() = referencePlaying || microphoneActive || transitioning
    val canConfigure: Boolean get() = !working && !historyBusy && history.pending.isEmpty() && round == null && !historyOpen && !progressOpen
}

/** All actions and session mutations run on Main. IO callbacks cross that boundary explicitly. */
class VocalIntervalViewModel(
    private val recorder: VoiceCapture = AndroidVoiceCapture(),
    private val reference: ReferencePlayer = AndroidReferencePlayer(),
    private val monotonicMillis: () -> Long = { System.nanoTime() / 1_000_000 },
    private val configuration: VocalIntervalConfiguration = VocalIntervalConfiguration(),
    resultsStore: VocalIntervalResults = MemoryVocalIntervalResults(),
    wallClock: () -> Long = System::currentTimeMillis,
    private val deviceInfo: () -> Pair<String, String> = { "${Build.MANUFACTURER} ${Build.MODEL}" to "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})" },
    private val automaticInitially: Boolean = false,
) : ViewModel() {
    private val mutableUi = MutableStateFlow(VocalIntervalUiState(toleranceCents = configuration.toleranceCents,
        progression = automaticInitially, semitones = if (automaticInitially) 0 else 7))
    val ui = mutableUi.asStateFlow()
    private var session: VocalIntervalSession? = null
    private var operation: Job? = null
    private var timer: Job? = null
    private var generation = 0L
    private var startedAt = 0L
    private val history = VocalIntervalHistorySession(resultsStore, wallClock)
    private var historyJob: Job? = null
    private var historyOperations = 0
    private var referenceListens = 0
    private var simulationJob: Job? = null

    fun selectInterval(semitones: Int) {
        if (ui.value.canConfigure && ui.value.series == null && semitones in -12..12 &&
            (!ui.value.progression || semitones in history.state.progress.unlockedFor(ui.value.scaffoldingLevel))) {
            referenceListens = 0
            mutableUi.value = ui.value.copy(semitones = semitones, heardReference = false, error = null)
        }
    }

    fun selectAnchor(midi: Int) {
        if (ui.value.canConfigure && ui.value.series == null && ui.value.scaffoldingLevel != 3 && midi in listOf(48, 57, 60, 69)) {
            referenceListens = 0
            mutableUi.value = ui.value.copy(anchorMidi = midi, heardReference = false, error = null)
        }
    }

    fun selectScaffolding(level: Int) {
        if (!ui.value.canConfigure || ui.value.series != null || level !in 1..3) return
        referenceListens = 0
        mutableUi.value = ui.value.copy(scaffoldingLevel = level, heardReference = false, error = null,
            toleranceCents = configuration.toleranceCents)
        alignSelection()
        historyAction { history.load(); alignSelection() }
    }

    private fun alignSelection() {
        val available = history.state.progress.unlockedFor(ui.value.scaffoldingLevel)
        if (ui.value.progression && ui.value.semitones !in available)
            mutableUi.value = ui.value.copy(semitones = vocalCurriculum.flatten().firstOrNull { it in available } ?: 0)
    }

    fun permissionDenied() {
        mutableUi.value = ui.value.copy(error = "Micrófono no autorizado. Habilitalo para cantar.", permissionDenied = true)
    }

    fun playReference() {
        if (!ui.value.canConfigure || ui.value.scaffoldingLevel == 3) return
        if (ui.value.progression && (ui.value.series == null || ui.value.series?.ended == true)) return
        val anchor = ui.value.anchorMidi
        val target = anchor + ui.value.semitones
        val level = ui.value.scaffoldingLevel
        mutableUi.value = ui.value.copy(referencePlaying = true, heardReference = false, error = null)
        replace { ticket ->
            try {
                check(reference.play(anchor) > 0) { "La primera nota no comenzó a sonar." }
                if (level == 1) {
                    delay(150)
                    check(reference.play(target) > 0) { "La segunda nota no comenzó a sonar." }
                }
                if (ticket == generation) {
                    referenceListens++
                    mutableUi.value = ui.value.copy(heardReference = true)
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) {
                if (ticket == generation) mutableUi.value = ui.value.copy(error = "No se pudo reproducir: ${error.message}")
            } finally {
                if (ticket == generation) mutableUi.value = ui.value.copy(referencePlaying = false, transitioning = false)
            }
        }
    }

    fun startAnchor() {
        if (ui.value.scaffoldingLevel != 2) startCaptureRound()
    }

    fun startGivenTarget() {
        if (ui.value.scaffoldingLevel == 2) startCaptureRound()
    }

    private fun startCaptureRound() {
        if (!ui.value.canConfigure || ui.value.scaffoldingLevel != 3 && !ui.value.heardReference) return
        val series = ui.value.series
        if (ui.value.progression && (series == null || series.ended)) return
        val effective = if (ui.value.progression) configuration.copy(toleranceCents = ui.value.toleranceCents) else configuration
        val id = history.begin(if (ui.value.scaffoldingLevel == 3) null else ui.value.anchorMidi,
            ui.value.semitones, referenceListens, deviceInfo(), BuildConfig.VERSION_NAME,
            if (ui.value.progression) "dda" else "manual", series?.let { VocalSeriesStep(it.id, it.ordinal) }, ui.value.scaffoldingLevel)
        val round = VocalIntervalSession(ui.value.semitones, effective,
            givenAnchorHz = if (ui.value.scaffoldingLevel == 2) referenceFrequency(ui.value.anchorMidi) else null)
        session = round
        startedAt = monotonicMillis()
        if (ui.value.scaffoldingLevel == 2) round.beginTarget() else round.beginAnchor()
        mutableUi.value = ui.value.copy(round = round.state, error = null, permissionDenied = false, actualSampleRate = null,
            currentRecordId = id,
            remainingSeconds = (configuration.timeoutMillis / 1000).toInt())
        timer = viewModelScope.launch {
            while (isActive && round.state.result == null) {
                delay(50)
                // A watchdog must not consume timestamps belonging to incoming audio frames.
                val elapsed = monotonicMillis() - startedAt
                if (elapsed >= configuration.timeoutMillis) { stop("timeout"); break }
                mutableUi.value = ui.value.copy(remainingSeconds = ((configuration.timeoutMillis - elapsed + 999) / 1000).toInt())
            }
        }
        capture(round)
    }

    fun startTarget() {
        val round = session ?: return
        if (ui.value.working || round.state.phase != VocalIntervalPhase.READY_TARGET) return
        round.beginTarget()
        mutableUi.value = ui.value.copy(round = round.state, frequencyHz = null)
        capture(round)
    }

    private class StageCompleted : CancellationException()

    private fun capture(round: VocalIntervalSession) {
        val phase = round.state.phase
        mutableUi.value = ui.value.copy(transitioning = true, error = null)
        replace { ticket ->
            mutableUi.value = ui.value.copy(transitioning = false, microphoneActive = true)
            try {
                recorder.capture(YinConfiguration()) { estimate, processingMicros, sampleRate ->
                    withContext(Dispatchers.Main.immediate) {
                        ensureActive()
                        if (ticket != generation || session !== round) throw CancellationException()
                        check(sampleRate == 44100) { "Frecuencia de captura inesperada: $sampleRate Hz." }
                        val accepted = round.accept(monotonicMillis() - startedAt, estimate, processingMicros)
                        if (accepted) mutableUi.value = ui.value.copy(round = round.state, actualSampleRate = sampleRate,
                            frequencyHz = estimate.frequency?.takeIf { it.isFinite() && it in configuration.minHz..configuration.maxHz })
                        if (round.state.phase != phase) throw StageCompleted()
                    }
                }
                if (ticket == generation && round.state.result == null) {
                    round.interrupt("capture-ended")
                    mutableUi.value = ui.value.copy(error = "El micrófono se detuvo antes de completar la nota.")
                }
            } catch (_: StageCompleted) {
                // Unwind capture's finally and release AudioRecord before exposing the next action.
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) {
                if (ticket == generation) {
                    round.interrupt("capture-error")
                    mutableUi.value = ui.value.copy(error = "No se pudo capturar: ${error.message}",
                        permissionDenied = error is SecurityException)
                }
            } finally {
                if (ticket == generation) {
                    mutableUi.value = ui.value.copy(round = round.state, microphoneActive = false,
                        transitioning = false, frequencyHz = null)
                    if (round.state.result != null) { timer?.cancel(); persistTerminal() }
                }
            }
        }
    }

    /** Invalidate callbacks immediately, then await release before any subsequent operation. */
    private fun replace(action: suspend (Long) -> Unit) {
        val previous = operation
        val ticket = ++generation
        previous?.cancel()
        operation = viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
            // Keep the release chain intact even if this waiting operation is itself cancelled.
            withContext(NonCancellable) { previous?.join() }
            ensureActive()
            if (ticket == generation) action(ticket)
        }
    }

    fun stop(reason: String = "user") {
        timer?.cancel()
        session?.interrupt(reason)
        mutableUi.value = ui.value.copy(round = session?.state, heardReference = false,
            microphoneActive = false, referencePlaying = false, frequencyHz = null, transitioning = true,
            series = ui.value.series?.copy(ended = true))
        replace { mutableUi.value = ui.value.copy(transitioning = false); persistTerminal() }
    }

    fun newRound() {
        if (ui.value.working || ui.value.historyBusy || history.state.pending.isNotEmpty() ||
            session != null && session?.state?.result == null || ui.value.series?.ended == false && session?.state?.result == null) return
        timer?.cancel()
        session = null
        referenceListens = 0
        val previous = ui.value.series
        val next = previous?.takeIf { !it.ended && it.ordinal < 5 }?.copy(ordinal = previous.ordinal + 1)
        mutableUi.value = VocalIntervalUiState(semitones = ui.value.semitones, anchorMidi = ui.value.anchorMidi,
            toleranceCents = if (next != null) history.state.progress.forLevel(next.semitones, next.scaffoldingLevel).toleranceCents.toDouble() else configuration.toleranceCents,
            history = history.state, progression = ui.value.progression, series = next, scaffoldingLevel = ui.value.scaffoldingLevel)
    }

    private fun persistTerminal() {
        val result = session?.state?.result ?: return
        if (history.finish(result, ui.value.actualSampleRate, ui.value.error)) historyAction { history.savePending() }
    }

    private fun historyAction(action: suspend () -> Unit) {
        val previous = historyJob
        historyOperations++
        publishHistory()
        historyJob = viewModelScope.launch {
            try { previous?.join(); action() }
            finally { historyOperations--; publishHistory() }
        }
    }

    private fun publishHistory() {
        val saved = history.state.records.firstOrNull { it.id == ui.value.currentRecordId }
        val series = ui.value.series?.let { current ->
            if (saved?.series?.id == current.id && (saved.dda?.eligible != true || saved.series.ordinal == 5)) current.copy(ended = true) else current
        }
        mutableUi.value = ui.value.copy(history = history.state, historyBusy = historyOperations > 0, series = series)
    }

    fun progression(automatic: Boolean) {
        if (!ui.value.canConfigure || ui.value.series != null) return
        referenceListens = 0
        mutableUi.value = ui.value.copy(progression = automatic, heardReference = false, error = null,
            semitones = if (automatic && ui.value.semitones !in history.state.progress.unlockedFor(ui.value.scaffoldingLevel)) 0 else ui.value.semitones)
        if (automatic) historyAction { history.load(); alignSelection() }
    }

    fun beginSeries() {
        if (!ui.value.canConfigure || !ui.value.progression || ui.value.series != null) return
        historyAction {
            history.load()
            if (history.state.error != null) return@historyAction
            val key = ui.value.semitones
            if (key !in history.state.progress.unlockedFor(ui.value.scaffoldingLevel)) {
                mutableUi.value = ui.value.copy(error = "Ese intervalo todavía está bloqueado.")
                return@historyAction
            }
            mutableUi.value = ui.value.copy(series = VocalSeriesState(UUID.randomUUID().toString(), key, scaffoldingLevel = ui.value.scaffoldingLevel),
                heardReference = false, toleranceCents = history.state.progress.forLevel(key, ui.value.scaffoldingLevel).toleranceCents.toDouble())
        }
    }

    fun openProgress() {
        if (ui.value.working || session != null && session?.state?.result == null) return
        mutableUi.value = ui.value.copy(progressOpen = true)
        historyAction { history.load() }
    }
    fun closeProgress() { mutableUi.value = ui.value.copy(progressOpen = false) }
    fun simulate(scenario: VocalDdaScenario, scaffoldingLevel: Int = ui.value.scaffoldingLevel) {
        if (ui.value.working || ui.value.simulationBusy || scaffoldingLevel !in 1..3) return
        mutableUi.value = ui.value.copy(simulationBusy = true)
        simulationJob = viewModelScope.launch {
            try {
                val report = withContext(Dispatchers.Default) { simulateVocalProgress(scenario, scaffoldingLevel) }
                mutableUi.value = ui.value.copy(simulation = report)
            } finally { mutableUi.value = ui.value.copy(simulationBusy = false) }
        }
    }
    fun pendingForExport(): List<VocalIntervalRecord> = history.state.pending.toList()
    suspend fun awaitStorage() { historyJob?.join() }
    fun retryHistory() { if (!ui.value.historyBusy) historyAction { history.savePending() } }
    fun openHistory() {
        if (ui.value.working || session != null && session?.state?.result == null) return
        mutableUi.value = ui.value.copy(historyOpen = true)
        historyAction { history.load() }
    }
    fun closeHistory() { mutableUi.value = ui.value.copy(historyOpen = false) }
    fun resetDisplay() {
        check(!ui.value.working && !ui.value.historyBusy)
        simulationJob?.cancel()
        session = null; referenceListens = 0; history.clear()
        mutableUi.value = VocalIntervalUiState(toleranceCents = configuration.toleranceCents,
            progression = automaticInitially, semitones = if (automaticInitially) 0 else 7)
    }
    suspend fun stopAndJoin() { stop(); operation?.join(); awaitStorage() }
    fun background() { if (ui.value.working || session?.state?.result == null && session != null || ui.value.series?.ended == false) stop("background") }
    override fun onCleared() { timer?.cancel(); operation?.cancel(); simulationJob?.cancel(); reference.close(); super.onCleared() }
}
