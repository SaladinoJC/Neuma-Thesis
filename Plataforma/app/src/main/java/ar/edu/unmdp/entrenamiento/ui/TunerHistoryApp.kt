package ar.edu.unmdp.entrenamiento.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import ar.edu.unmdp.entrenamiento.tuner.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Comparación deliberadamente textual: una misma guitarra, afinación/revisión, criterio y etapa. */
@Composable
fun TunerHistoryApp(ui: TunerUiState, model: TunerViewModel) {
    val state = ui.history
    var instrument by rememberSaveable { mutableStateOf(false) }
    var detailId by rememberSaveable { mutableStateOf<String?>(null) }
    var limit by rememberSaveable { mutableIntStateOf(20) }
    val detail = state.results.firstOrNull { it.id == detailId }
    val filter = state.filter
    fun change(value: TunerHistoryFilter) { limit = 20; model.filterHistory(value) }
    val back = { if (detailId != null) detailId = null else model.closeHistory() }
    TunerGardenTheme {
        BackHandler(onBack = back)
        Scaffold(containerColor = GardenTokens.Background) { padding ->
            LazyColumn(Modifier.fillMaxSize().padding(padding).testTag("tuner-history-screen"),
                contentPadding = PaddingValues(GardenTokens.SpaceLg), verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceMd)) {
                item {
                    TextButton(onClick = back, modifier = Modifier.testTag(if (detail != null) "tuner-history-back" else "tuner-history-close")) {
                        Text(if (detail != null) "Volver al historial" else "Volver al afinador")
                    }
                    Text(if (detail != null) "Detalle del recorrido" else "Historial del afinador", style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.semantics { heading() })
                }
                if (detail != null) {
                    item {
                        TunerResultHeading(detail)
                        Text("Inicio: ${date(detail.startedAtMillis)} · fin: ${date(detail.finishedAtMillis)}")
                        Text("Referencia elegida al iniciar: ${detail.referenceTimbre.label}")
                        Text(if (detail.requestedReferences.isEmpty()) "Referencias solicitadas: ninguna."
                            else "Referencias solicitadas: ${detail.requestedReferences.joinToString(" · ") { "${it.timbre.label} ${noteName(it.targetMidi)}" }}")
                        Text("Revisión final: ${if (detail.finalReview) "activada" else "apagada"} · clasificación: ${if (detail.classificationEnabled) "activada" else "apagada"}")
                        Text("Objetivos sexta → primera: ${detail.setup.tuning.notesSixToFirst.joinToString(" · ", transform = ::noteName)}")
                    }
                    if (detail.observations.isEmpty()) item { Text("Sin mediciones selladas antes de la interrupción.") }
                    TunerStage.entries.forEach { stage ->
                        val observations = detail.observations.filter { it.stage == stage }
                        if (observations.isNotEmpty()) {
                            item { HorizontalDivider(color = GardenTokens.Border); Text(stage.label, style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { heading() }) }
                            items(observations) { Observation(it) }
                        }
                    }
                    item { Text("El ajuste guiado y las correcciones se miden después de ajustar. La revisión no suma intentos de oído.", color = GardenTokens.TextSecondary) }
                } else {
                    item {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
                            FilterChip(!instrument, { instrument = false; change(TunerHistoryFilter(mode = TunerMode.TRAINING)) }, label = { Text("Oído") }, modifier = Modifier.testTag("tuner-history-ear"))
                            FilterChip(instrument, {
                                instrument = true
                                val first = state.results.lastOrNull()
                                change(TunerHistoryFilter(guitarId = first?.setup?.guitar?.id, tuningKey = first?.setup?.tuning?.historyKey,
                                    criterion = first?.setup?.criterion, stage = TunerStage.FINAL_REVIEW))
                            }, label = { Text("Instrumento") }, modifier = Modifier.testTag("tuner-history-instrument"))
                        }
                        if (ui.historyBusy) Text("Cargando seguimiento…")
                        ui.history.error?.let { Text(it, color = GardenTokens.ErrorText) }
                        if (state.pending.isNotEmpty()) Text("${state.pending.size} pendientes de guardar; todavía no se incluyen en los cálculos.", color = GardenTokens.ErrorText)
                        if (state.error != null || state.pending.isNotEmpty()) OutlinedButton(onClick = model::retryHistory,
                            enabled = !ui.historyBusy, modifier = Modifier.testTag("tuner-history-retry")) { Text("Reintentar seguimiento") }
                        if (instrument) Text("Compará la misma guitarra, afinación, criterio y etapa. Las diferencias no identifican una causa mecánica.")
                        else Text("Los aciertos cuentan solo intentos válidos iniciales. Las correcciones y la revisión final se muestran aparte.")
                        if (!instrument) ProgressText(state.earProgress, "tuner-history-progress")
                    }
                    item {
                        HistoryChoice("Guitarra", filter.guitarId, state.results.map { it.setup.guitar }.distinctBy { it.id }.map { it.id to it.name }, !instrument,
                            "tuner-history-guitar") { change(filter.copy(guitarId = it)) }
                    }
                    item {
                        HistoryChoice("Afinación", filter.tuningKey, state.results.map { it.setup.tuning }.distinctBy { it.historyKey }.map {
                            it.historyKey to "${it.name} · rev. ${it.revision} · ${it.notesSixToFirst.joinToString("/", transform = ::noteName)}"
                        }, !instrument, "tuner-history-tuning") { change(filter.copy(tuningKey = it)) }
                    }
                    item {
                        HistoryChoice("Criterio", filter.criterion?.name, TunerCriterion.entries.map { it.name to "${it.name} ±${it.margin.toInt()} cents" }, !instrument,
                            "tuner-history-criterion") { change(filter.copy(criterion = it?.let(TunerCriterion::valueOf))) }
                    }
                    item {
                        HistoryChoice("Cuerda", filter.stringNumber?.toString(), (6 downTo 1).map { it.toString() to "Cuerda $it" }, true,
                            "tuner-history-string") { change(filter.copy(stringNumber = it?.toInt())) }
                    }
                    if (instrument) item { HistoryChoice("Etapa", filter.stage?.name, TunerStage.entries.map { it.name to it.label }, false,
                            "tuner-history-stage") { change(filter.copy(stage = it?.let(TunerStage::valueOf))) }
                    }
                    if (!instrument) item {
                        (6 downTo 1).forEach { number ->
                            val progress = state.copy(filter = filter.copy(stringNumber = number)).earProgress
                            if ((filter.stringNumber == null || filter.stringNumber == number) && progress.validAttempts > 0)
                                Text("Cuerda $number · ${progress.correctAttempts}/${progress.validAttempts} aciertos · desvío inicial absoluto medio ${number(progress.meanInitialAbsoluteCents)} cents")
                        }
                    }
                    val comparable = !instrument || (filter.guitarId != null && filter.tuningKey != null && filter.criterion != null && filter.stage != null)
                    val entries = if (comparable) state.entries else emptyList()
                    item {
                        Text("Recorridos · ${entries.size}", style = MaterialTheme.typography.titleMedium)
                        if (!comparable) Text("Elegí guitarra, afinación y criterio para comparar etapas equivalentes.")
                        else if (entries.isEmpty()) Text("Sin datos para estos filtros.", modifier = Modifier.testTag("tuner-history-empty"))
                    }
                    items(entries.take(limit), key = { it.result.id }) { entry ->
                        Column(verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
                            HorizontalDivider(color = GardenTokens.Border)
                            TunerResultHeading(entry.result)
                            if (instrument) entry.observations.forEach { Observation(it) }
                            else ProgressText(entry.earProgress)
                            TextButton(onClick = { detailId = entry.result.id }, modifier = Modifier.testTag("tuner-history-detail")) { Text("Ver detalle") }
                        }
                    }
                    if (entries.size > limit) item { TextButton(onClick = { limit += 20 }) { Text("Mostrar 20 más") } }
                    item { Text("Exportar o recuperar originales: menú → Más → Datos de demostración. No se guardan grabaciones.", color = GardenTokens.TextSecondary) }
                }
            }
        }
    }
}

@Composable
private fun TunerResultHeading(result: TunerResult) {
    Text("${date(result.startedAtMillis)} · ${result.mode.label} · ${if (result.completed) "completo" else "interrumpido"}")
    Text("${result.setup.guitar.name} · ${result.setup.tuning.name} (rev. ${result.setup.tuning.revision}) · ${result.setup.criterion.name}")
}

@Composable
private fun ProgressText(progress: EarProgress, tag: String = "") {
    Text(if (progress.validAttempts == 0) "Sin intentos válidos de oído."
        else "${progress.correctAttempts}/${progress.validAttempts} aciertos (${number(100.0 * progress.correctAttempts / progress.validAttempts)} %) · desvío inicial absoluto medio ${number(progress.meanInitialAbsoluteCents)} cents",
        modifier = Modifier.testTag(tag))
    Text(if (progress.classified == 0) "Clasificación: sin datos."
        else "Clasificación: ${progress.correctClassifications}/${progress.classified} correctas.")
}

@Composable
private fun Observation(observation: TunerObservation) {
    Text("Cuerda ${observation.number} · ${noteName(observation.targetMidi)} · ${observation.cents?.let { String.format(Locale.ROOT, "%+.1f cents", it) } ?: "sin lectura válida"} · ${if (observation.passed) "aprobada" else "pendiente / error"}")
    observation.measuredMidi?.let { Text("Nota medida: ${noteName(it)}") }
    observation.classification?.let { Text("Clasificación ${if (it.correct) "correcta" else "incorrecta"}: ${it.answer.label}; medido ${it.actual.label} (${number(it.cents)} cents).") }
}

@Composable
internal fun HistoryChoice(label: String, selected: String?, options: List<Pair<String, String>>, allowAll: Boolean, tag: String, change: (String?) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { open = true }, shape = RoundedCornerShape(GardenTokens.RadiusLarge), modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum).testTag(tag)) {
            Text("$label: ${options.firstOrNull { it.first == selected }?.second ?: if (allowAll) "Sin filtro" else "Elegir"}")
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            if (allowAll) DropdownMenuItem(text = { Text("Sin filtro") }, onClick = { open = false; change(null) })
            options.forEach { (value, text) -> DropdownMenuItem(text = { Text(text) }, onClick = { open = false; change(value) }) }
        }
    }
}

private fun number(value: Double?) = value?.let { String.format(Locale.ROOT, "%.1f", it) } ?: "sin datos"
private fun date(millis: Long) = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault()).format(Date(millis))
