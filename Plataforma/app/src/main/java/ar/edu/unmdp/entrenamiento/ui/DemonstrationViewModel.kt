package ar.edu.unmdp.entrenamiento.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unmdp.entrenamiento.BuildConfig
import ar.edu.unmdp.entrenamiento.storage.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.OutputStream

data class DemonstrationUi(val busy: Boolean = false, val pickerReady: Boolean = false,
    val message: String? = null, val simulation: ProgressSimulation? = null, val benchmark: DdaBenchReport? = null,
    val noticeId: Long = 0, val noticePending: Boolean = false, val awaitingDestination: Boolean = false,
    val recoveryReady: Boolean = false)
class DemonstrationViewModel(private val data: Demonstration, private val drain: suspend () -> Unit,
    private val refresh: suspend () -> Unit,
    private val helpSimulation: () -> ar.edu.unmdp.entrenamiento.instruments.InstrumentResult? = { null },
    private val pendingVoice: () -> ar.edu.unmdp.entrenamiento.voice.VoiceResult? = { null },
    private val drainForReset: suspend () -> Unit = drain,
    private val pendingTuner: () -> List<ar.edu.unmdp.entrenamiento.tuner.TunerResult> = { emptyList() },
    private val pendingRhythm: () -> List<ar.edu.unmdp.entrenamiento.rhythm.RhythmRecord> = { emptyList() },
    private val pendingDefender: () -> List<ar.edu.unmdp.entrenamiento.defender.DefenderRecord> = { emptyList() }) : ViewModel() {
    private val mutableUi = MutableStateFlow(DemonstrationUi())
    val ui = mutableUi.asStateFlow()
    private var snapshot: String? = null
    private var writing = false
    private var recoveryPending = false
    private fun notify(message: String) {
        mutableUi.value = ui.value.copy(message = message, noticeId = ui.value.noticeId+1, noticePending = true)
    }
    fun consumeNotice(id: Long) {
        if (ui.value.noticeId == id) mutableUi.value = ui.value.copy(noticePending = false)
    }
    private fun act(action: suspend () -> Unit) {
        if (ui.value.busy) return
        mutableUi.value = ui.value.copy(busy = true)
        viewModelScope.launch {
            try { action() }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { notify("No se pudo completar: ${error.message}") }
            finally { if (!ui.value.awaitingDestination && !writing) mutableUi.value = ui.value.copy(busy = false) }
        }
    }
    fun simulate(recovery: Boolean) = simulateScenario(if (recovery) ProgressScenario.RECOVERY else ProgressScenario.TWO_HIGH)
    fun simulateScenario(scenario: ProgressScenario) = act {
        val simulation = withContext(Dispatchers.Default) { simulateProgress(scenario) }
        mutableUi.value = ui.value.copy(simulation = simulation)
    }
    fun runDdaBank() = act {
        val benchmark = withContext(Dispatchers.Default) { runDdaBench() }
        mutableUi.value = ui.value.copy(benchmark = benchmark)
        notify("Banco DDA completado: ${benchmark.checks.count { !it.passed }} comprobaciones fallidas.")
    }
    fun prepareExport() = act {
        drain()
        snapshot = data.export(BuildConfig.VERSION_NAME, ui.value.simulation, helpSimulation(), ui.value.benchmark, pendingVoice(), pendingTuner(), pendingRhythm(), pendingDefender())
        mutableUi.value = ui.value.copy(pickerReady = true, awaitingDestination = true)
    }
    fun pickerLaunched() { mutableUi.value = ui.value.copy(pickerReady = false) }
    fun prepareRecovery() = act {
        recoveryPending = true
        mutableUi.value = ui.value.copy(recoveryReady = true, awaitingDestination = true)
    }
    fun recoveryLaunched() { mutableUi.value = ui.value.copy(recoveryReady = false) }
    fun exportRecovery(open: (() -> OutputStream?)?) {
        if (writing) return
        val ready = recoveryPending
        recoveryPending = false
        mutableUi.value = ui.value.copy(recoveryReady = false, awaitingDestination = false)
        if (open == null || !ready) {
            mutableUi.value = ui.value.copy(busy = false)
            notify(if (open == null) "Recuperación cancelada." else "La recuperación se interrumpió. Iniciá otra exportación.")
            return
        }
        writing = true
        viewModelScope.launch {
            try {
                val pending = pendingVoice()
                val tuner = pendingTuner()
                val rhythm = pendingRhythm()
                val defender = pendingDefender()
                withContext(Dispatchers.IO) { data.writeRecovery(checkNotNull(open()) { "No se pudo abrir el destino." }, pending, tuner, rhythm, defender) }
                notify("Originales guardados para recuperación.")
            } catch (error: Exception) { notify("No se pudo exportar: ${error.message}") }
            finally { writing = false; mutableUi.value = ui.value.copy(busy = false) }
        }
    }
    fun export(open: (() -> OutputStream?)?) {
        if (writing) return
        val content = snapshot
        snapshot = null
        mutableUi.value = ui.value.copy(pickerReady = false, awaitingDestination = false)
        if (open == null || content == null) {
            mutableUi.value = ui.value.copy(busy = false)
            notify(if (open == null) "Exportación cancelada." else "La exportación anterior se interrumpió. Iniciá otra exportación.")
            return
        }
        writing = true
        mutableUi.value = ui.value.copy(busy = true)
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { checkNotNull(open()) { "No se pudo abrir el destino." }.bufferedWriter(Charsets.UTF_8).use { it.write(content) } }
                notify("Exportación guardada.")
            } catch (error: Exception) { notify("No se pudo exportar: ${error.message}") }
            finally { writing = false; mutableUi.value = ui.value.copy(busy = false) }
        }
    }
    fun reset() = act {
        withContext(NonCancellable) {
            drainForReset()
            data.reset()
            refresh()
            mutableUi.value = ui.value.copy(simulation = null, benchmark = null)
            notify("Datos reiniciados. Ambos juegos comienzan en el primer nivel.")
        }
    }
}
