package ar.edu.unmdp.entrenamiento.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unmdp.entrenamiento.BuildConfig
import ar.edu.unmdp.entrenamiento.storage.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.OutputStream

data class DemonstrationUi(val busy: Boolean = false, val pickerReady: Boolean = false,
    val message: String? = null, val simulation: ProgressSimulation? = null)
class DemonstrationViewModel(private val data: Demonstration, private val drain: suspend () -> Unit,
    private val refresh: suspend () -> Unit,
    private val helpSimulation: () -> ar.edu.unmdp.entrenamiento.instruments.InstrumentResult? = { null }) : ViewModel() {
    private val mutableUi = MutableStateFlow(DemonstrationUi())
    val ui = mutableUi.asStateFlow()
    private var snapshot: String? = null
    private fun act(action: suspend () -> Unit) {
        if (ui.value.busy) return
        mutableUi.value = ui.value.copy(busy = true, message = null)
        viewModelScope.launch {
            try { action() }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { mutableUi.value = ui.value.copy(message = "No se pudo completar: ${error.message}") }
            finally { if (!ui.value.pickerReady) mutableUi.value = ui.value.copy(busy = false) }
        }
    }
    fun simulate(recovery: Boolean) = act {
        val simulation = withContext(Dispatchers.Default) { simulateProgress(recovery) }
        mutableUi.value = ui.value.copy(simulation = simulation)
    }
    fun prepareExport() = act {
        drain()
        snapshot = data.export(BuildConfig.VERSION_NAME, ui.value.simulation, helpSimulation())
        mutableUi.value = ui.value.copy(pickerReady = true)
    }
    fun pickerLaunched() { mutableUi.value = ui.value.copy(pickerReady = false) }
    fun export(open: (() -> OutputStream?)?) {
        val content = snapshot
        snapshot = null
        if (open == null || content == null) {
            mutableUi.value = ui.value.copy(busy = false, message = "Exportación cancelada.")
            return
        }
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { checkNotNull(open()) { "No se pudo abrir el destino." }.bufferedWriter(Charsets.UTF_8).use { it.write(content) } }
                mutableUi.value = ui.value.copy(message = "Exportación guardada.")
            } catch (error: Exception) { mutableUi.value = ui.value.copy(message = "No se pudo exportar: ${error.message}") }
            finally { mutableUi.value = ui.value.copy(busy = false) }
        }
    }
    fun reset() = act {
        withContext(NonCancellable) {
            drain()
            data.reset()
            refresh()
            mutableUi.value = ui.value.copy(simulation = null, message = "Datos reiniciados. Ambos juegos comienzan en el primer nivel.")
        }
    }
}
