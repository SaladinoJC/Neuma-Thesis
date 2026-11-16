package ar.edu.unmdp.entrenamiento.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import ar.edu.unmdp.entrenamiento.voice.*
import java.util.Locale

@Composable
internal fun VocalProgressApp(ui: VocalIntervalUiState, model: VocalIntervalViewModel) {
    var key by rememberSaveable { mutableIntStateOf(0) }
    var level by rememberSaveable { mutableIntStateOf(ui.scaffoldingLevel) }
    var simulated by rememberSaveable { mutableStateOf(false) }
    var scenario by remember { mutableStateOf(VocalDdaScenario.GOOD) }
    var selected by remember(key, level, simulated, ui.simulation) { mutableIntStateOf(0) }
    val progress = if (simulated) ui.simulation?.progress ?: VocalProgress() else ui.history.progress
    val records = (if (simulated) ui.simulation?.records.orEmpty() else ui.history.records)
        .filter { it.result.targetSemitones == key && it.scaffoldingLevel == level && it.dda != null }.takeLast(20).asReversed()
    BackHandler(onBack = model::closeProgress)
    TunerGardenTheme {
        GardenPageScaffold { padding ->
            LazyColumn(Modifier.fillMaxSize().padding(padding).testTag("vocal-progress-screen"),
                contentPadding = PaddingValues(GardenTokens.SpaceBase), verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceBase)) {
                item {
                    TextButton(onClick = model::closeProgress, modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-progress-back")) { Text("Volver a la práctica") }
                    Text("Progreso vocal", style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.semantics { heading() })
                    Text("Parámetros experimentales, sin calibración pedagógica")
                    VocalChoice("Ayuda auditiva", level, vocalScaffoldingChoices, true,
                        "vocal-progress-scaffolding") { level = it }
                    VocalIntervalSaveNotice(ui, model)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
                        FilterChip(!simulated, { simulated = false }, label = { Text("Perfil") }, modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum))
                        FilterChip(simulated, { simulated = true }, label = { Text("Simulación") }, modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-simulation-tab"))
                    }
                    if (simulated) {
                        Text("DATOS SINTÉTICOS · Sin micrófono ni escrituras en tu perfil.")
                        VocalChoice("Escenario", scenario.ordinal, VocalDdaScenario.entries.map { it.ordinal to it.label },
                            !ui.simulationBusy, "vocal-simulation-choice") { scenario = VocalDdaScenario.entries[it] }
                        OutlinedButton(onClick = { model.simulate(scenario, level) }, enabled = !ui.simulationBusy,
                            modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-simulation-run")) { Text("Ejecutar simulación") }
                        if (ui.simulationBusy) Text("Calculando simulación…")
                        ui.simulation?.let {
                            val hitCount = vocalCurriculum.flatten().count { key -> it.progress.forLevel(key, level).mastered }
                            Text("${it.records.size} rondas sintéticas · $hitCount hitos")
                            Text("Simulación de ${vocalScaffoldingLabel(it.scaffoldingLevel)}; incluye hitos sintéticos de las ayudas anteriores cuando corresponde.")
                        }
                    }
                    Text("${progress.unlockedFor(level).size}/25 intervalos disponibles", modifier = Modifier.testTag("vocal-unlocked-count"))
                    VocalChoice("Intervalo", key, vocalCurriculum.flatten().map { it to vocalIntervalLabel(it) }, true,
                        "vocal-progress-interval") { key = it }
                    val state = progress.forLevel(key, level)
                    Text(if (key in progress.unlockedFor(level)) "Desbloqueado" else "Bloqueado")
                    GardenPaperPanel {
                    Text("${state.highSeries}/2 series altas · tolerancia recomendada ${state.toleranceCents} cents")
                    Text(if (state.mastered) "Hito registrado. El acceso adquirido se conserva." else "Todavía no hay hito para este intervalo y dirección.")
                    val group = vocalCurriculum.indexOfFirst { key in it }
                    Text(if (level == 3) "Requiere el hito de ancla dada de este intervalo y dirección. Su desempeño se registra por separado."
                        else if (level == 2) "Requiere el hito de imitación de este intervalo y dirección. Su desempeño se registra por separado."
                        else if (group == 0) "Disponible desde el inicio." else "Requiere los hitos de todos los intervalos de la etapa anterior.")
                    if (level == 1 && group > 0) Text(vocalCurriculum[group - 1].joinToString { vocalIntervalLabel(it) })
                    if (state.masteryEvidence.isNotEmpty()) Text("Series del hito: ${state.masteryEvidence.joinToString()}", style = MaterialTheme.typography.bodySmall)
                    Text("Una serie alta exige cinco rondas elegibles altas. Entrar en tolerancia por sí solo no acredita esa categoría.")
                    }
                }
                item {
                    if (records.isEmpty()) Text("No hay decisiones de progresión para este intervalo.")
                    else {
                        VocalChoice("Decisión", selected.coerceAtMost(records.lastIndex), records.indices.map { i ->
                            i to "Ronda ${records[i].series?.ordinal ?: 0} · ${records[i].dda!!.reason.label}" }, true, "vocal-dda-record") { selected = it }
                        VocalDecisionDetails(records[selected.coerceAtMost(records.lastIndex)])
                    }
                }
            }
        }
    }
}

@Composable
internal fun VocalDecisionDetails(record: VocalIntervalRecord) {
    val decision = record.dda
    if (decision == null) { Text("Práctica manual: no modifica progreso."); return }
    val key = record.result.targetSemitones
    val level = record.scaffoldingLevel
    var functions by remember(record.id) { mutableStateOf(false) }
    Text(decision.reason.label, style = MaterialTheme.typography.titleMedium)
    Text("${record.series?.ordinal}/5 · secuencia ${decision.sequence} · ${if (decision.eligible) "Elegible" else "Sin progreso"}")
    Text(vocalScaffoldingLabel(level))
    Text("Tolerancia ${decision.before.forLevel(key, level).toleranceCents} → ${decision.after.forLevel(key, level).toleranceCents} cents; cambio aplicado ${decision.appliedDelta}")
    Text("Racha de series ${decision.before.forLevel(key, level).highSeries} → ${decision.after.forLevel(key, level).highSeries}")
    if (decision.abandonedPreviousSeries) Text("La serie anterior quedó incompleta: se reinició su racha.")
    if (decision.unlocked.isNotEmpty()) Text("Nuevos accesos: ${decision.unlocked.joinToString { vocalIntervalLabel(it) }}")
    if (decision.givenAnchorUnlocked.isNotEmpty()) Text("Ancla dada habilitada: ${decision.givenAnchorUnlocked.joinToString { vocalIntervalLabel(it) }}")
    if (decision.freeProductionUnlocked.isNotEmpty()) Text("Producción libre habilitada: ${decision.freeProductionUnlocked.joinToString { vocalIntervalLabel(it) }}")
    decision.inference?.let { inference ->
        Text(String.format(Locale.ROOT, "Error absoluto medio %.2f cents · dispersión %.2f cents · %d muestras", decision.meanAbsoluteCents, decision.deviationCents, decision.samples))
        Text(String.format(Locale.ROOT, "Precisión %.3f · regularidad %.3f · centroide %.3f · %s", decision.precision, decision.regularity, inference.centroid, decision.band))
        FuzzyRuleDetails(inference)
    }
    TextButton(onClick = { functions = !functions }, modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum)) { Text(if (functions) "Ocultar funciones" else "Ver funciones y configuración") }
    if (functions) {
        Text("${decision.configuration.version} · mínimo/máximo/centroide · ${decision.configuration.samplingPoints} puntos")
        decision.configuration.inputs.forEach { (input, terms) -> terms.forEach { (term, shape) -> Text("$input/$term: $shape") } }
        decision.configuration.outputs.forEach { (term, shape) -> Text("Salida $term: $shape") }
        Text("Alto ≥0,70; medio ≥0,40. Paso máximo 5 cents entre 30 y 80, solo para futuras rondas.")
    }
}
