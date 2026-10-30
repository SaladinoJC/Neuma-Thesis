package ar.edu.unmdp.entrenamiento.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import ar.edu.unmdp.entrenamiento.voice.*
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun VocalIntervalSaveNotice(ui: VocalIntervalUiState, model: VocalIntervalViewModel) {
    if (ui.historyBusy) Text("Actualizando historial…", modifier = Modifier.testTag("vocal-storage-busy"))
    val pending = ui.history.pending.any { it.id == ui.currentRecordId }
    val saved = ui.history.records.any { it.id == ui.currentRecordId }
    if (ui.round?.result != null) Text(when {
        pending -> "Guardado pendiente"
        saved -> "Ronda guardada"
        else -> "Preparando guardado"
    }, modifier = Modifier.testTag("vocal-save-state").semantics { liveRegion = LiveRegionMode.Polite })
    if (ui.history.pending.isNotEmpty()) Text(
        "Hay una ronda pendiente en memoria. Reintentá el guardado o exportala desde Datos y demostración antes de cerrar la app. No se iniciará otra ronda hasta recuperarla.",
        color = GardenTokens.ErrorText, modifier = Modifier.testTag("vocal-save-pending"))
    ui.history.error?.let { Text(it, color = GardenTokens.ErrorText,
        modifier = Modifier.testTag("vocal-storage-error").semantics { liveRegion = LiveRegionMode.Polite }) }
    if (ui.history.error != null || ui.history.pending.isNotEmpty()) OutlinedButton(onClick = model::retryHistory,
        enabled = !ui.historyBusy && !ui.working,
        modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-retry-save")) { Text("Reintentar historial") }
}

@Composable
internal fun VocalIntervalHistoryApp(ui: VocalIntervalUiState, model: VocalIntervalViewModel, onData: () -> Unit) {
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    var shown by rememberSaveable { mutableIntStateOf(20) }
    val entries = ui.history.entries
    val selected = entries.firstOrNull { it.id == selectedId }
    val back = { if (selectedId != null) selectedId = null else model.closeHistory() }
    BackHandler(onBack = back)
    TunerGardenTheme {
        Scaffold(containerColor = GardenTokens.Background, contentWindowInsets = WindowInsets.safeDrawing) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(GardenTokens.SpaceBase),
                verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceBase)) {
                TextButton(onClick = back, modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-history-back")) {
                    Text(if (selected == null) "Volver a la práctica" else "Volver al historial")
                }
                Text(if (selected == null) "Historial vocal" else "Detalle de ronda", style = MaterialTheme.typography.headlineMedium)
                VocalIntervalSaveNotice(ui, model)
                if (selected == null) {
                    if (entries.isEmpty() && !ui.historyBusy) Text(if (ui.history.error == null) "Todavía no hay rondas guardadas."
                        else "El historial no está disponible. Los originales se conservan.", modifier = Modifier.testTag("vocal-history-empty"))
                    entries.take(shown).forEach { record ->
                        OutlinedButton(onClick = { selectedId = record.id },
                            modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-record-${record.id}")) {
                            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceXs)) {
                                Text(vocalIntervalLabel(record.result.targetSemitones))
                                Text(vocalScaffoldingLabel(record.scaffoldingLevel))
                                Text(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(record.startedAtMillis)))
                                Text(if (ui.history.pending.any { it.id == record.id }) "Guardado pendiente"
                                    else if (record.result.interruption == null) "Ronda completa" else "Ronda interrumpida")
                                record.result.errorCents?.let { Text(String.format(Locale.ROOT, "Desviación: %+.1f cents", it)) }
                            }
                        }
                    }
                    if (entries.size > shown) OutlinedButton(onClick = { shown += 20 },
                        modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-history-more")) { Text("Mostrar otras 20 rondas") }
                } else VocalIntervalRecordDetail(selected)
                TextButton(onClick = onData, enabled = !ui.historyBusy,
                    modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-history-data")) { Text("Datos y demostración") }
            }
        }
    }
}

@Composable
private fun VocalIntervalRecordDetail(record: VocalIntervalRecord) {
    var measurementsShown by remember(record.id) { mutableIntStateOf(20) }
    var measurementsOpen by remember(record.id) { mutableStateOf(false) }
    val result = record.result
    Text(vocalIntervalLabel(result.targetSemitones), style = MaterialTheme.typography.titleMedium)
    Text(vocalScaffoldingLabel(record.scaffoldingLevel) + if (record.origin == "dda") " · Progresión" else " · Práctica manual")
    record.series?.let { Text("Serie ${it.id} · Ronda ${it.ordinal} de 5") }
    Text(if (record.scaffoldingLevel == 3) "Sin referencia sonora · Nota inicial elegida al cantar"
        else if (record.scaffoldingLevel == 2) "Ancla escuchada: ${noteName(requireNotNull(record.referenceAnchorMidi))} · ${record.referenceListens} escucha(s)"
        else "Referencia: ${noteName(requireNotNull(record.referenceAnchorMidi))} → ${noteName(requireNotNull(record.referenceTargetMidi))} · ${record.referenceListens} escucha(s)")
    if (result.interruption == null) {
        Text(if (result.inTune == true) "Dentro de la tolerancia experimental" else "Fuera de la tolerancia experimental")
        Text(String.format(Locale.ROOT, "Intervalo cantado: %.1f cents · desviación: %+.1f cents", result.intervalCents, result.errorCents),
            modifier = Modifier.testTag("vocal-history-result"))
    } else Text("Ronda interrumpida · Sin evaluación musical final")
    result.anchorHz?.let { Text(String.format(Locale.ROOT, if (result.anchorSource == VocalAnchorSource.GIVEN) "Ancla reproducida: %.1f Hz" else "Ancla cantada: %.1f Hz", it)) }
    result.targetHz?.let { Text(String.format(Locale.ROOT, "Segunda nota: %.1f Hz", it)) }
    Text("${result.measurements.size} mediciones derivadas · Sin grabación de audio")
    record.captureError?.let { Text(it, color = GardenTokens.ErrorText) }
    TextButton(onClick = { measurementsOpen = !measurementsOpen },
        modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-history-measurements")) {
        Text(if (measurementsOpen) "Ocultar mediciones" else "Ver mediciones y condiciones")
    }
    if (measurementsOpen) {
        VocalDecisionDetails(record)
        Text("${record.device} · Android ${record.androidVersion} · app ${record.appVersion}")
        Text("${record.analysisConfiguration.version} · captura: ${record.actualSampleRate?.let { "$it Hz" } ?: "sin tasa confirmada"}")
        Text("Sostenido: ${result.configuration.holdMillis} ms · tolerancia: ±${result.configuration.toleranceCents} cents · configuración ${record.configurationVersion}")
        Text("Tiempos desde el inicio de ronda; procesamiento por ventana, no latencia acústica.")
        result.measurements.take(measurementsShown).forEach { sample ->
            val frequency = sample.frequency?.let { String.format(Locale.ROOT, "%.1f Hz", it) } ?: "Sin tono válido"
            val error = sample.errorCents?.let { String.format(Locale.ROOT, "%+.1f cents", it) } ?: "Sin desviación objetivo"
            Text("${sample.elapsedMillis} ms · ${if (sample.phase == VocalIntervalPhase.ANCHOR) "Ancla" else "Segunda nota"} · $frequency · $error · ${sample.processingMicros?.let { "$it µs" } ?: "Sin tiempo de procesamiento"}")
        }
        if (result.measurements.size > measurementsShown) OutlinedButton(onClick = { measurementsShown += 20 },
            modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum)) { Text("Mostrar otras 20 mediciones") }
    }
}
