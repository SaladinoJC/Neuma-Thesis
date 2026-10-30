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
import ar.edu.unmdp.entrenamiento.defender.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun DefenderStorageNotice(ui: DefenderUiState, model: DefenderViewModel, onData: (() -> Unit)? = null) {
    if (ui.historyBusy) Text("Actualizando historial…")
    ui.history.error?.let { Text(it, color = GardenTokens.ErrorText, modifier = Modifier.testTag("defender-storage-error")) }
    if (ui.history.pending.isNotEmpty()) Text("${ui.history.pending.size} partida(s) pendientes en memoria. Reintentá o exportá una copia.",
        color = GardenTokens.ErrorText, modifier = Modifier.testTag("defender-storage-pending"))
    if (ui.history.error != null || ui.history.pending.isNotEmpty()) {
        OutlinedButton(onClick = model::retryHistory, enabled = !ui.historyBusy, modifier = Modifier.testTag("defender-storage-retry")) { Text("Reintentar guardado") }
        if (onData != null) OutlinedButton(onClick = onData) { Text("Datos y exportación") }
    }
    if (ui.game.phase in setOf(DefenderPhase.FINISHED, DefenderPhase.INTERRUPTED)) {
        val saved = ui.history.results.any { it.id == ui.currentRecordId }
        val pending = ui.history.pending.any { it.id == ui.currentRecordId }
        val sealed = ui.history.results.firstOrNull { it.id == ui.currentRecordId }?.sealed == true
        Text(if (pending) "Guardado pendiente" else if (saved && !sealed) "Resultado guardado · avance al cerrar el resumen"
            else if (saved) "Partida guardada" else "Preparando guardado…", modifier = Modifier.testTag("defender-save-status"))
    }
}

@Composable
internal fun DefenderModeControls(ui: DefenderUiState, model: DefenderViewModel) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
        FilterChip(!ui.progression, { model.progression(false) }, label = { Text("Práctica") },
            modifier = Modifier.testTag("defender-mode-practice").heightIn(min = GardenTokens.TouchMinimum))
        FilterChip(ui.progression, { model.progression(true) }, label = { Text("Progresión") },
            modifier = Modifier.testTag("defender-mode-progression").heightIn(min = GardenTokens.TouchMinimum))
    }
    if (!ui.progression) FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
        TextButton(onClick = { model.travel(ui.travelMillis - 1000) }, enabled = ui.travelMillis > 6000,
            modifier = Modifier.testTag("defender-travel-down").heightIn(min = GardenTokens.TouchMinimum)) { Text("−1 s") }
        Text("Viaje ${ui.travelMillis / 1000} s", modifier = Modifier.padding(vertical = GardenTokens.SpaceMd))
        TextButton(onClick = { model.travel(ui.travelMillis + 1000) }, enabled = ui.travelMillis < 14_000,
            modifier = Modifier.testTag("defender-travel-up").heightIn(min = GardenTokens.TouchMinimum)) { Text("+1 s") }
    }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
        OutlinedButton(onClick = model::openHistory, modifier = Modifier.testTag("defender-history-open")) { Text("Historial") }
        OutlinedButton(onClick = model::openInspector, modifier = Modifier.testTag("defender-inspector-open")) { Text("Progreso y DDA") }
    }
}

@Composable
internal fun DefenderHistoryApp(ui: DefenderUiState, model: DefenderViewModel, onData: (() -> Unit)?) {
    var filters by rememberSaveable { mutableStateOf(false) }
    var limit by rememberSaveable(ui.history.filter) { mutableIntStateOf(20) }
    val state = ui.history
    val detail = (state.results + state.pending).filter { it.id == ui.detailId }.maxByOrNull { it.revision }
    val back = { if (ui.detailId != null) model.detail(null) else model.closeHistory() }
    TunerGardenTheme {
        BackHandler(onBack = back)
        Scaffold(containerColor = GardenTokens.Background, contentWindowInsets = WindowInsets.safeDrawing) { padding ->
            LazyColumn(Modifier.fillMaxSize().padding(padding).testTag("defender-history-screen"),
                contentPadding = PaddingValues(GardenTokens.SpaceBase), verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceMd)) {
                item {
                    TextButton(onClick = back, modifier = Modifier.testTag("defender-history-back")) { Text(if (detail == null) "Volver a Defender" else "Volver al historial") }
                    Text(if (detail == null) "Historial de Defender" else "Detalle de partida", style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.semantics { heading() })
                    Text("Notas y acordes · avances independientes", color = GardenTokens.TextSecondary)
                    DefenderStorageNotice(ui, model, onData)
                }
                if (detail == null) {
                    item {
                        Text("${state.entries.size} partida(s)")
                        TextButton(onClick = { filters = !filters }, modifier = Modifier.testTag("defender-history-filters")) { Text(if (filters) "Cerrar filtros" else "Filtrar historial") }
                        if (filters) {
                            val f = state.filter
                            HistoryChoice("Variante", f.variant, listOf("notes" to "Notas", "chords" to "Acordes por nombre"), true, "defender-history-variant") {
                                model.filterHistory(f.copy(variant = it, stage = null, quality = null, accidentals = null, inversion = null, clefs = null))
                            }
                            if (f.variant != "chords") HistoryChoice("Etapa de notas", f.stage?.name, DefenderStage.entries.map { it.name to it.label }, true, "defender-history-stage") {
                                model.filterHistory(f.copy(stage = it?.let(DefenderStage::valueOf), quality = null, inversion = null, clefs = null))
                            }
                            if (f.variant != "notes") HistoryChoice("Calidad", f.quality?.name, DefenderChordQuality.entries.map { it.name to it.label }, true, "defender-history-quality") {
                                model.filterHistory(f.copy(quality = it?.let(DefenderChordQuality::valueOf), stage = null, accidentals = null))
                            }
                            if (f.variant != "notes") {
                                HistoryChoice("Inversión", f.inversion?.toString(), (0..3).map { it.toString() to defenderInversionLabel(it) }, true,
                                    "defender-history-inversion") { model.filterHistory(f.copy(inversion = it?.toInt(), stage = null, accidentals = null)) }
                                HistoryChoice("Clave de acordes", f.clefs?.name, DefenderChordClefs.entries.map { it.name to it.label }, true,
                                    "defender-history-clefs") { model.filterHistory(f.copy(clefs = it?.let(DefenderChordClefs::valueOf), stage = null, accidentals = null)) }
                            }
                            HistoryChoice("Origen", f.origin, listOf("practice" to "Práctica", "progression" to "Progresión"), true, "defender-history-origin") { model.filterHistory(f.copy(origin = it)) }
                            if (f.variant != "chords") HistoryChoice("Alteraciones (notas)", f.accidentals?.toString(), listOf("true" to "Con alteraciones", "false" to "Solo naturales"), true, "defender-history-accidentals") { model.filterHistory(f.copy(accidentals = it?.toBooleanStrict(), quality = null, inversion = null, clefs = null)) }
                            HistoryChoice("Cierre", f.completed?.toString(), listOf("true" to "Game over completo", "false" to "Interrumpida"), true, "defender-history-completed") { model.filterHistory(f.copy(completed = it?.toBooleanStrict())) }
                            TextButton(onClick = { model.filterHistory(DefenderHistoryFilter()) }) { Text("Quitar filtros") }
                        }
                    }
                    if (state.entries.isEmpty()) item { Text(if (state.results.isEmpty()) "Todavía no hay partidas guardadas." else "No hay partidas con estos filtros.", modifier = Modifier.testTag("defender-history-empty")) }
                    items(state.entries.take(limit), key = { it.id }) { record ->
                        Column(verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceXs)) {
                            DefenderRecordHeading(record)
                            OutlinedButton(onClick = { model.detail(record.id) }, modifier = Modifier.testTag("defender-history-detail")) { Text("Ver resultado y pérdidas") }
                        }
                    }
                    if (state.entries.size > limit) item { TextButton(onClick = { limit += 20 }) { Text("Ver más (${minOf(limit, state.entries.size)}/${state.entries.size})") } }
                } else {
                    item {
                        DefenderRecordHeading(detail)
                        Text("Intento ${detail.id}", style = MaterialTheme.typography.bodySmall)
                        Text("Semilla ${detail.game.seed} · ${detail.game.configuration.version} · revisión ${detail.revision}")
                        Text("Tiempo activo: ${defenderNumber(detail.game.activeMillis / 1000.0)} s · ${detail.game.events.count { it.kind == "PAUSE" }} pausas")
                        Text("${detail.game.events.count { it.kind == "CANCEL" }} objetivos cancelados al cerrar; no cuentan como resueltos.")
                        DefenderProgressEvidence(detail)
                        Text("Pérdidas", style = MaterialTheme.typography.titleMedium)
                    }
                    val losses = detail.game.results.filter { it.resolution == DefenderResolution.ARRIVED }
                    if (losses.isEmpty()) item { Text("No hubo llegadas registradas.") }
                    items(losses, key = { it.enemy.id }) { result -> DefenderLoss(result) }
                    item {
                        Text("Eventos y decisiones intrapartida", style = MaterialTheme.typography.titleMedium)
                        Text("${detail.game.events.size} eventos · ${detail.game.ddaDecisions.size} decisiones. Las marcas y la configuración completas se conservan al exportar.")
                        OutlinedButton(onClick = model::openInspector, modifier = Modifier.testTag("defender-detail-inspect")) { Text("Inspeccionar DDA") }
                    }
                }
                if (onData != null) item { OutlinedButton(onClick = onData, modifier = Modifier.testTag("defender-data")) { Text("Datos y exportación") } }
            }
        }
    }
}

@Composable
internal fun DefenderLoss(result: DefenderResult) {
    val note = result.enemy.note
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceMd)) {
        DefenderEnemyStaff(result.enemy, Modifier.width(110.dp).height(92.dp))
        Column(Modifier.weight(1f)) {
            Text("${result.enemy.solution()} · clave de ${note.clef.label.lowercase()}")
            result.enemy.chord?.let { Text(it.notes.joinToString(" · ") { voice -> voice.writtenName() }) }
            result.enemy.chord?.let { Text("${defenderInversionLabel(it.inversion)} · bajo ${it.notes.first().writtenName()}") }
            Text("Llegó a ${defenderNumber(result.atMillis / 1000.0)} s · viaje ${defenderNumber(result.enemy.travelMillis / 1000.0)} s", color = GardenTokens.TextSecondary)
        }
    }
}

@Composable
internal fun DefenderProgressEvidence(record: DefenderRecord) {
    record.progress?.let { progress ->
        Text("${progress.configuration.version} · centroide con ${progress.configuration.samplingPoints} puntos", style = MaterialTheme.typography.bodySmall)
        Text(progress.reason.label, modifier = Modifier.testTag("defender-progress-reason"))
        val config = record.game.configuration
        val before = if (record.variant == "chords") progress.before[config.chordTarget] else progress.before[config.stage]
        val after = if (record.variant == "chords") progress.after[config.chordTarget] else progress.after[config.stage]
        Text("Racha ${before.highStreak} → ${after.highStreak}")
        if (progress.newlyUnlocked.isNotEmpty()) Text("Desbloqueadas: ${progress.newlyUnlocked.joinToString { it.name }}", color = GardenTokens.Primary)
        if (progress.newlyUnlockedTargets.isEmpty() && progress.newlyUnlockedQualities.isNotEmpty()) Text("Calidades desbloqueadas: ${progress.newlyUnlockedQualities.joinToString { it.label }}", color = GardenTokens.Primary)
        if (progress.newlyUnlockedTargets.isNotEmpty()) Text("Contenidos desbloqueados: ${progress.newlyUnlockedTargets.joinToString { it.label }}", color = GardenTokens.Primary)
        if (progress.evaluatedRevision != record.revision) Text("Evaluación musical revisada; la decisión usada por partidas posteriores corresponde a la revisión ${progress.evaluatedRevision}.")
        progress.evidence.forEach {
            val label = listOfNotNull(it.quality?.label, it.inversion?.let(::defenderInversionLabel), it.clef?.label).joinToString(" · ").ifEmpty { "Conjunto" }
            Text("$label: ${it.eliminated}/${it.resolved} eliminados · S ${defenderNumber(it.survival)} · P global ${defenderNumber(it.precision)}")
            Text("Dominio ${it.mastery ?: "sin salida"} · centroide ${defenderNumber(it.inference?.centroid)}")
        }
    }
}

@Composable
private fun DefenderRecordHeading(record: DefenderRecord) {
    Text(SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault()).format(Date(record.startedAtMillis)))
    Text("${record.game.configuration.contentLabel} · ${if (record.game.origin == "progression") "Progresión" else "Práctica"} · ${if (!record.sealed) "Sin cierre confirmado" else if (record.game.phase == DefenderPhase.FINISHED) "Completa" else "Interrumpida"}")
    Text("${record.game.points} puntos · ${record.game.results.size} resueltos · ${record.game.failedShots} intentos fallidos")
}

internal fun defenderNumber(value: Double?): String = value?.let { String.format(Locale.getDefault(), "%.3f", it) } ?: "—"
