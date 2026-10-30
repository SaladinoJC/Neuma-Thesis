package ar.edu.unmdp.entrenamiento.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unmdp.entrenamiento.voice.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.OutputStream

data class PitchLabUi(val running: Boolean = false, val completed: Int = 0, val total: Int = 83,
    val report: PitchLabReport? = null, val message: String? = null, val markdownReady: Boolean = false,
    val exporting: Boolean = false)
class PitchLabViewModel(private val session: PitchLabSession) : ViewModel() {
    private val mutableUi = MutableStateFlow(PitchLabUi())
    val ui = mutableUi.asStateFlow()
    private var job: Job? = null
    private var markdown: String? = null
    fun load() { viewModelScope.launch {
        try {
            val last = session.history().lastOrNull()
            if (!ui.value.running && ui.value.report == null) mutableUi.value = ui.value.copy(report = last)
        } catch (error: Exception) { mutableUi.value = ui.value.copy(message = "No se pudo leer el laboratorio: ${error.message}") }
    } }
    fun start() {
        if (ui.value.running || ui.value.exporting) return
        mutableUi.value = ui.value.copy(running = true, completed = 0, message = null)
        job = viewModelScope.launch {
            try {
                val report = withContext(Dispatchers.Default) { session.run(onProgress = { completed, total ->
                    withContext(Dispatchers.Main.immediate) { mutableUi.value = ui.value.copy(completed = completed, total = total) }
                }) }
                mutableUi.value = ui.value.copy(report = report, message = "Informe sintético guardado.")
            } catch (cancelled: CancellationException) {
                mutableUi.value = ui.value.copy(message = "Prueba cancelada; no se guarda un informe parcial.")
                throw cancelled
            } catch (error: Exception) { mutableUi.value = ui.value.copy(message = "No se pudo completar: ${error.message}") }
            finally { mutableUi.value = ui.value.copy(running = false) }
        }
    }
    fun cancel() { job?.cancel() }
    suspend fun cancelAndJoin() { job?.cancelAndJoin() }
    fun resetDisplay() { mutableUi.value = PitchLabUi() }
    fun prepareMarkdown() {
        if (ui.value.running || ui.value.exporting) return
        markdown = ui.value.report?.markdown() ?: return
        mutableUi.value = ui.value.copy(markdownReady = true, exporting = true)
    }
    fun pickerLaunched() { mutableUi.value = ui.value.copy(markdownReady = false) }
    fun export(open: (() -> OutputStream?)?) {
        val content = markdown; markdown = null
        if (content == null || open == null) {
            mutableUi.value = ui.value.copy(exporting = false, message = if (open == null) "Exportación cancelada." else "Informe interrumpido: iniciá otra exportación.")
            return
        }
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { checkNotNull(open()).bufferedWriter(Charsets.UTF_8).use { it.write(content) } }
                mutableUi.value = ui.value.copy(message = "Informe Markdown exportado.")
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { mutableUi.value = ui.value.copy(message = "No se pudo exportar: ${error.message}") }
            finally { mutableUi.value = ui.value.copy(exporting = false) }
        }
    }
}
