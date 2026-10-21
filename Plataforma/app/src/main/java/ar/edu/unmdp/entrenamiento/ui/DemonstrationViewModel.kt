package ar.edu.unmdp.entrenamiento.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unmdp.entrenamiento.BuildConfig
import ar.edu.unmdp.entrenamiento.storage.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.OutputStream

data class DemonstrationUi(val busy: Boolean = false, val pickerReady: Boolean = false,
    val message: String? = null, val simulation: ProgressSimulation? = null,
    val noticeId: Long = 0, val noticePending: Boolean = false, val awaitingDestination: Boolean = false)
class DemonstrationViewModel(private val data: Demonstration, private val drain: suspend () -> Unit,
    private val refresh: suspend () -> Unit,
    private val helpSimulation: () -> ar.edu.unmdp.entrenamiento.instruments.InstrumentResult? = { null }) : ViewModel() {
    private val mutableUi = MutableStateFlow(DemonstrationUi())
    val ui = mutableUi.asStateFlow()
    private var snapshot: String? = null
    private var writing = false
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
    fun simulate(recovery: Boolean) = act {
        val simulation = withContext(Dispatchers.Default) { simulateProgress(recovery) }
        mutableUi.value = ui.value.copy(simulation = simulation)
    }
    fun prepareExport() = act {
        drain()
        snapshot = data.export(BuildConfig.VERSION_NAME, ui.value.simulation, helpSimulation())
        mutableUi.value = ui.value.copy(pickerReady = true, awaitingDestination = true)
    }
    fun pickerLaunched() { mutableUi.value = ui.value.copy(pickerReady = false) }
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
            drain()
            data.reset()
            refresh()
            mutableUi.value = ui.value.copy(simulation = null)
            notify("Datos reiniciados. Ambos juegos comienzan en el primer nivel.")
        }
    }
}
