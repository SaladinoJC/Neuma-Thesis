package ar.edu.unmdp.entrenamiento.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import ar.edu.unmdp.entrenamiento.rhythm.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun RhythmSaveStatus(ui: RhythmUiState, model: RhythmViewModel, onData: (() -> Unit)? = null, showCurrentStatus: Boolean = true) {
    if (ui.historyBusy) Text("Actualizando historial…")
    ui.history.error?.let { Text(it, color = RhythmTokens.ErrorText, modifier = Modifier.testTag("rhythm-history-error")) }
    if (ui.history.pending.isNotEmpty()) Text("${ui.history.pending.size} ejercicio(s) pendientes en memoria. Reintentá o exportá una copia.",
        color = RhythmTokens.ErrorText, modifier = Modifier.testTag("rhythm-history-pending"))
    if (ui.history.error != null || ui.history.pending.isNotEmpty()) {
        OutlinedButton(onClick = model::retryHistory, enabled = !ui.historyBusy, modifier = Modifier.testTag("rhythm-history-retry")) { Text("Reintentar historial") }
        if (onData != null) TextButton(onClick = onData, modifier = Modifier.testTag("rhythm-data")) { Text("Datos y exportación") }
    }
    if (showCurrentStatus && ui.session.result != null && ui.currentRecordId != null) {
        val pending = ui.history.pending.any { it.id == ui.currentRecordId }
        val saved = ui.history.results.any { it.id == ui.currentRecordId }
        Text(if (pending) "Guardado pendiente" else if (saved) "Ejercicio guardado" else "Preparando guardado…",
            modifier = Modifier.testTag("rhythm-save-status"))
    }
}

@Composable
internal fun RhythmHistoryApp(ui: RhythmUiState, model: RhythmViewModel, onData: (() -> Unit)?) {
    var filtersOpen by rememberSaveable { mutableStateOf(false) }
    var technical by rememberSaveable(ui.detailId) { mutableStateOf(false) }
    var limit by rememberSaveable(ui.history.filter) { mutableIntStateOf(20) }
    val state = ui.history
    val detail = (state.results + state.pending).filter { it.id == ui.detailId }.maxByOrNull { it.revision }
    val back = { if (ui.detailId != null) model.detail(null) else model.closeHistory() }
    RhythmTheme {
        BackHandler(onBack = back)
        RhythmPageScaffold { padding ->
            LazyColumn(Modifier.fillMaxSize().padding(padding).testTag("rhythm-history-screen"),
                contentPadding = PaddingValues(RhythmTokens.SpaceBase), verticalArrangement = Arrangement.spacedBy(RhythmTokens.SpaceMd)) {
                item {
                    TextButton(onClick = back, modifier = Modifier.testTag("rhythm-history-back")) { Text(if (detail != null) "Volver al historial" else "Volver al ejercicio") }
                    Text(if (detail != null) "Detalle rítmico" else "Historial de ritmo", style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.semantics { heading() })
                    RhythmSaveStatus(ui, model, showCurrentStatus = false)
                }
                if (detail != null) {
                    item {
                        GardenPaperPanel(color = RhythmTokens.Surface) { RecordHeading(detail) }
                        Text("Fin: ${rhythmDate(detail.finishedAtMillis)}")
                        OutlinedButton(onClick = { model.repeatRecord(detail.id) }, enabled = !ui.busy,
                            modifier = Modifier.testTag("rhythm-history-repeat")) { Text("Repetir como práctica") }
                    }
                    item {
                        RhythmScoreView(detail.result.score, RhythmState(RhythmPhase.RESULTS, detail.result.score,
                            attacks = detail.result.attacks, result = detail.result), reduced = true, Modifier.fillMaxWidth())
                    }
                    item { RhythmResultSummary(detail.result, detailed = true) }
                    item {
                        TextButton(onClick = { technical = !technical }, modifier = Modifier.testTag("rhythm-history-technical")) { Text(if (technical) "Ocultar datos del ejercicio" else "Datos del ejercicio") }
                        if (technical) {
                            Text("Semilla: ${detail.result.score.seed} · generador: ${detail.result.score.version}")
                            Text("Huella: ${detail.fingerprint}", modifier = Modifier.testTag("rhythm-record-fingerprint"))
                            Text("Criterio temporal: ${detail.result.timingVersion} · revisión del resultado ${detail.revision}")
                            Text("Reloj de audio: ${detail.result.audioClockSource} · incertidumbre: ${detail.result.clockUncertaintyNanos?.let { rhythmMillis(it) + " ms" } ?: "sin medición"}")
                            Text("Offset táctil: ${detail.result.inputClockOffsetNanos} ns · origen de conteo: ${detail.result.countInStartNanos} ns")
                            detail.repeatedFromId?.let { Text("Repetición del intento: $it") }
                            Text("Los datos históricos se consultan sin recalcular su evaluación.")
                            RhythmRecordDecisions(detail, ui.history.progress)
                        }
                    }
                } else {
                    item {
                        Text("${state.entries.size} ejercicio(s)")
                        TextButton(onClick = { filtersOpen = !filtersOpen }, modifier = Modifier.testTag("rhythm-history-filters")) { Text(if (filtersOpen) "Cerrar filtros" else "Filtrar historial") }
                        if (filtersOpen) {
                            val filter = state.filter
                            Column(verticalArrangement = Arrangement.spacedBy(RhythmTokens.SpaceSm)) {
                                HistoryChoice("Categoría", filter.category?.name, RhythmCategory.entries.sortedBy { it.code }.map { it.name to "${it.code} · ${it.label}" }, true,
                                    "rhythm-history-category") { model.filterHistory(filter.copy(category = it?.let(RhythmCategory::valueOf))) }
                                HistoryChoice("Compás", filter.beatsPerBar?.toString(), (2..4).map { it.toString() to "$it/4" }, true,
                                    "rhythm-history-meter") { model.filterHistory(filter.copy(beatsPerBar = it?.toInt())) }
                                HistoryChoice("Tempo", filter.bpm?.toString(), state.results.map { it.result.score.bpm }.distinct().sorted().map { it.toString() to "$it BPM" }, true,
                                    "rhythm-history-tempo") { model.filterHistory(filter.copy(bpm = it?.toInt())) }
                                HistoryChoice("Origen", filter.origin, listOf("practice" to "Práctica", "progression" to "Progresión"), true,
                                    "rhythm-history-origin") { model.filterHistory(filter.copy(origin = it)) }
                                HistoryChoice("Metrónomo", filter.metronome?.toString(), listOf("true" to "Activado", "false" to "Apagado"), true,
                                    "rhythm-history-metronome") { model.filterHistory(filter.copy(metronome = it?.toBooleanStrict())) }
                                TextButton(onClick = { model.filterHistory(RhythmHistoryFilter()) }, modifier = Modifier.testTag("rhythm-history-clear-filters")) { Text("Quitar filtros") }
                            }
                        }
                    }
                    if (state.entries.isEmpty()) item {
                        Text(if (state.results.isEmpty()) "Todavía no hay ejercicios guardados." else "No hay ejercicios con estos filtros.", modifier = Modifier.testTag("rhythm-history-empty"))
                    }
                    items(state.entries.take(limit), key = { it.id }) { record ->
                        GardenPaperPanel(color = RhythmTokens.Surface) {
                            RecordHeading(record)
                            Text("${record.result.attacks.count { it.kind == TapKind.ON_TIME }}/${record.result.attacks.size} ataques a tiempo · ${record.result.extras} extras")
                            OutlinedButton(onClick = { model.detail(record.id) }, modifier = Modifier.testTag("rhythm-history-detail")) { Text("Ver partitura y resultado") }
                        }
                    }
                    if (state.entries.size > limit) item {
                        TextButton(onClick = { limit += 20 }, modifier = Modifier.testTag("rhythm-history-more")) { Text("Ver más (${minOf(limit, state.entries.size)}/${state.entries.size})") }
                    }
                }
                if (onData != null) item { OutlinedButton(onClick = onData, modifier = Modifier.testTag("rhythm-data")) { Text("Datos y exportación") } }
            }
        }
    }
}

@Composable
private fun RecordHeading(record: RhythmRecord) {
    val result = record.result
    Text("${rhythmDate(record.startedAtMillis)} · ${if (result.completed) "Completo" else "Interrumpido"}")
    Text("${result.score.beatsPerBar}/4 · ${result.score.bpm} BPM · ${if (result.origin == "progression") "Progresión" else "Práctica"}${if (result.repeated) " repetida" else ""}")
    Text(result.score.selected.joinToString(" · ") { "${it.code} ${it.label}" })
    Text("Metrónomo ${if (result.metronome) "activado" else "apagado"} · taps ${if (result.tapSound) "con sonido" else "sin sonido"}", style = MaterialTheme.typography.bodySmall)
}

private fun rhythmDate(millis: Long) = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault()).format(Date(millis))
