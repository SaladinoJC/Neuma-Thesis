package ar.edu.unmdp.entrenamiento.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unmdp.entrenamiento.history.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class HistoryUiState(val history: HistoryState = HistoryState(), val loading: Boolean = false)
class HistoryViewModel(private val session: HistorySession) : ViewModel() {
    private val mutableUi = MutableStateFlow(HistoryUiState())
    val ui = mutableUi.asStateFlow()
    fun load() {
        if (ui.value.loading) return
        mutableUi.value = ui.value.copy(loading = true)
        viewModelScope.launch {
            try { session.load() }
            finally { mutableUi.value = HistoryUiState(session.state) }
        }
    }
    fun filter(game: HistoryGame, level: Int? = null, origin: String? = null) {
        session.filter(game, level, origin); publish()
    }
    fun more() { session.more(); publish() }
    fun select(key: String) { session.select(key); publish() }
    fun back() { session.back(); publish() }
    private fun publish() { mutableUi.value = ui.value.copy(history = session.state) }
}
