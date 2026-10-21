package ar.edu.unmdp.entrenamiento.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import ar.edu.unmdp.entrenamiento.history.*
import ar.edu.unmdp.entrenamiento.simon.Scale
import ar.edu.unmdp.entrenamiento.instruments.InstrumentLevel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun date(millis: Long) = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.forLanguageTag("es-AR")).format(Date(millis))
private fun n(value: Double?) = value?.let { String.format(Locale.ROOT, "%.2f", it) } ?: "Sin datos"
private fun title(item: HistoryItem) = item.simon?.let { "${it.scale.label} · ${it.completedLength} notas" }
    ?: item.instrument?.let { "${it.level.label} · ${it.answers.count { a -> a.correct }}/${it.answers.size}" }
    ?: "Captura de voz"

@Composable
fun HistoryApp(ui: HistoryUiState, model: HistoryViewModel, onHome: () -> Unit) {
    val state = ui.history
    val back = { if (state.selected != null) model.back() else onHome() }
    BackHandler(onBack = back)
    MaterialTheme(colorScheme = darkColorScheme(primary = Pink, background = Night, surface = Panel)) {
        Scaffold { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp)) {
                TextButton(onClick = back) { Text(if (state.selected != null) "Volver al historial" else "Volver al menú") }
                Text("Resultados", style = MaterialTheme.typography.headlineMedium)
                if (ui.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
                state.selected?.let { HistoryDetail(it) } ?: run {
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        HistoryGame.entries.forEach { game -> FilterChip(selected = state.game == game,
                            onClick = { model.filter(game) }, label = { Text(game.label) }, modifier = Modifier.testTag("history-game-${game.name}")) }
                    }
                    if (state.game != HistoryGame.VOICE) {
                        val levels = if (state.game == HistoryGame.SIMON) Scale.entries.map { it.label } else InstrumentLevel.entries.map { it.label }
                        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(selected = state.level == null, onClick = { model.filter(state.game, origin = state.origin) }, label = { Text("Todos los niveles") })
                            levels.forEachIndexed { index, label -> FilterChip(selected = state.level == index,
                                onClick = { model.filter(state.game, index, state.origin) }, label = { Text(label) }, modifier = Modifier.testTag("history-level-$index")) }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(null to "Todos", "automatic" to "Automático", "manual" to "Manual").forEach { (origin, label) ->
                                FilterChip(selected = state.origin == origin, onClick = { model.filter(state.game, state.level, origin) }, label = { Text(label) })
                            }
                        }
                    }
                    state.errors[state.game]?.let {
                        Text("No se pudo leer este historial: $it", color = MaterialTheme.colorScheme.error)
                        TextButton(onClick = model::load) { Text("Reintentar") }
                    }
                    Text("${state.items.size} de ${state.total} resultados", modifier = Modifier.testTag("history-count"))
                    if (state.total == 0 && !ui.loading && state.errors[state.game] == null) Text("Todavía no hay resultados para este filtro.")
                    val scroll = rememberLazyListState()
                    LaunchedEffect(state.game, state.level, state.origin) { scroll.scrollToItem(0) }
                    LazyColumn(Modifier.fillMaxWidth().weight(1f).testTag("history-list"), state = scroll,
                        verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(vertical = 12.dp)) {
                        items(state.items, key = { it.key }) { result ->
                            Surface(onClick = { model.select(result.key) }, color = Panel, modifier = Modifier.fillMaxWidth().testTag("history-row-${result.key}")) {
                                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                    Text(title(result), style = MaterialTheme.typography.titleMedium)
                                    Text("${date(result.atMillis)} · ${result.origin}")
                                    Text(if (result.simon?.completed == false || result.instrument?.completed == false) "Interrumpida" else "Ver detalle")
                                }
                            }
                        }
                        if (state.items.size < state.total) item {
                            Button(onClick = model::more, modifier = Modifier.fillMaxWidth().testTag("history-more")) { Text("Cargar 20 más") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryDetail(item: HistoryItem) {
    var expandedRound by rememberSaveable(item.key) { mutableIntStateOf(-1) }
    var voiceRows by rememberSaveable(item.key) { mutableIntStateOf(20) }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).testTag("history-detail"), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title(item), style = MaterialTheme.typography.titleLarge)
        Text("${date(item.atMillis)} · Origen: ${item.origin}")
        item.simon?.let { r ->
            Text("${if (r.completed) "Completada" else "Interrumpida"} · Tónica ${noteName(r.tonicMidi)}")
            Text("Longitud máxima completada: ${r.completedLength}")
            Text("Secuencia intentada: " + r.attemptedSequence.joinToString(" · ") { index ->
                r.scale.intervals.getOrNull(index)?.let { noteName(r.tonicMidi+it) } ?: "Índice $index" })
            Text(r.errorPosition?.let { "Error en posición ${it+1}" } ?: "Sin posición de error registrada")
            r.mastery?.let { MasteryButton(it) } ?: Text("Registro anterior sin calificación de dominio.")
            Text("Configuración: ${r.configurationVersion}")
        }
        item.instrument?.let { r ->
            Text(if (r.completed) "Completada" else "Interrumpida")
            r.mastery?.let { MasteryButton(it) } ?: Text("Registro anterior sin calificación de dominio.")
            Text("Banco: ${r.bankVersion} · ${r.configurationVersion}")
            r.answers.forEach { a ->
                HorizontalDivider()
                Text("${a.round}. ${a.target.label} · ${if (a.correct) "Correcto" else "Incorrecto"}")
                Text("Elegiste ${a.selected.label} · ${a.listensUsed}/${a.listensAllowed} escuchas")
                Text("Opciones: ${a.options.joinToString { it.label }}")
                Text(a.melody?.let { "Melodía: ${it.midiNotes.joinToString { midi -> noteName(midi) }} · ${it.durationsMillis.joinToString()} ms" }
                    ?: "Registro anterior: nota aislada MIDI ${r.midi ?: "no registrado"}, sin melodía.")
                if (a.ddaDecision != null) {
                    TextButton(onClick = { expandedRound = if (expandedRound == a.round) -1 else a.round }) { Text("Decisión DDA de la pregunta ${a.round}") }
                    if (expandedRound == a.round) DecisionDetails(a.ddaDecision)
                } else Text("Sin traza DDA registrada.")
            }
        }
        item.voice?.let { r ->
            val summary = voiceSummary(r)
            Text("${r.device} · Android ${r.androidVersion}")
            Text("Duración: ${(r.finishedAtMillis-r.startedAtMillis).coerceAtLeast(0)} ms · ${r.actualSampleRate} Hz · ${r.audioSource}")
            Text("${summary.valid}/${summary.total} estimaciones válidas" + if (summary.total > 0) " (${n(100.0*summary.valid/summary.total)} %)" else "")
            Text("Desviación respecto del objetivo: media ${n(summary.meanCents)} cents · mediana absoluta ${n(summary.medianAbsoluteCents)} cents")
            Text("Procesamiento p50/p95: ${summary.processingP50Micros ?: "—"}/${summary.processingP95Micros ?: "—"} µs")
            Text("Estos valores no miden exactitud del detector sobre voz desconocida ni latencia acústica.")
            Text("Configuración: ${r.configuration}")
            r.error?.let { Text("Error registrado: $it") }
            r.measurements.take(voiceRows).forEach { m -> Text("${m.elapsedMillis} ms · objetivo ${noteName(m.targetMidi)} · " +
                (m.estimate.frequency?.let { "${n(it)} Hz · ${n(m.cents)} cents" } ?: "Sin tono válido") + " · ${m.processingMicros} µs") }
            if (voiceRows < r.measurements.size) TextButton(onClick = { voiceRows += 20 }) { Text("Ver 20 mediciones más") }
        }
        Spacer(Modifier.height(24.dp))
    }
}
