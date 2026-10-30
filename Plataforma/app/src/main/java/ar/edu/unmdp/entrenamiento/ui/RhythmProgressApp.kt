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
import ar.edu.unmdp.entrenamiento.rhythm.*
import java.util.Locale

@Composable
internal fun RhythmModeControls(ui: RhythmUiState, model: RhythmViewModel) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
        FilterChip(selected = !ui.progression, onClick = { model.progression(false) }, enabled = !ui.busy,
            modifier = Modifier.testTag("rhythm-mode-practice").heightIn(min = GardenTokens.TouchMinimum), label = { Text("Práctica") })
        FilterChip(selected = ui.progression, onClick = { model.progression(true) }, enabled = !ui.busy,
            modifier = Modifier.testTag("rhythm-mode-progression").heightIn(min = GardenTokens.TouchMinimum), label = { Text("Progresión") })
    }
    if (ui.progression) {
        Text("Tempo de la categoría más lenta. Dos ejercicios nuevos altos permiten avanzar.", color = GardenTokens.TextSecondary)
    } else {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
            TextButton(onClick = { model.practiceTempo(ui.practiceBpm - 5) }, enabled = !ui.busy && ui.practiceBpm > 60,
                modifier = Modifier.testTag("rhythm-tempo-down").heightIn(min = GardenTokens.TouchMinimum)) { Text("−5 BPM") }
            Text("${ui.practiceBpm} BPM", modifier = Modifier.padding(vertical = GardenTokens.SpaceMd))
            TextButton(onClick = { model.practiceTempo(ui.practiceBpm + 5) }, enabled = !ui.busy && ui.practiceBpm < 120,
                modifier = Modifier.testTag("rhythm-tempo-up").heightIn(min = GardenTokens.TouchMinimum)) { Text("+5 BPM") }
        }
        Text("Todas las células disponibles para practicar. No cambia tu progreso.", color = GardenTokens.TextSecondary)
    }
    OutlinedButton(onClick = model::openProgress, enabled = !ui.busy,
        modifier = Modifier.testTag("rhythm-progress-open").heightIn(min = GardenTokens.TouchMinimum)) { Text("Progreso y DDA") }
}

@Composable
internal fun RhythmProgressApp(ui: RhythmUiState, model: RhythmViewModel) {
    var categoryName by rememberSaveable { mutableStateOf(RhythmCategory.QUARTER.name) }
    var simulated by rememberSaveable { mutableStateOf(false) }
    var scenarioName by rememberSaveable { mutableStateOf(RhythmDdaScenario.SUCCESS.name) }
    var step by rememberSaveable(ui.simulation) { mutableIntStateOf(0) }
    val category = RhythmCategory.valueOf(categoryName)
    val progress = ui.history.progress
    val state = progress[category]
    val latest = ui.history.results.filter { category in it.result.score.selected }
        .maxWithOrNull(compareBy<RhythmRecord> { it.startedAtMillis }.thenBy { it.dda?.sequence ?: 0 })
    TunerGardenTheme {
        BackHandler(onBack = model::closeProgress)
        Scaffold(containerColor = GardenTokens.Background, contentWindowInsets = WindowInsets.safeDrawing) { padding ->
            LazyColumn(Modifier.fillMaxSize().padding(padding).testTag("rhythm-progress-screen"),
                contentPadding = PaddingValues(GardenTokens.SpaceBase), verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceMd)) {
                item {
                    TextButton(onClick = model::closeProgress, modifier = Modifier.testTag("rhythm-progress-back")) { Text("Volver al ejercicio") }
                    Text("Progreso de ritmo", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.semantics { heading() })
                    Text("${progress.unlocked.size}/${RhythmCategory.entries.size} células desbloqueadas · valores experimentales", color = GardenTokens.TextSecondary)
                    RhythmSaveStatus(ui, model, showCurrentStatus = false)
                }
                item {
                    HistoryChoice("Célula", categoryName, RhythmCategory.entries.sortedBy { it.code }.map { it.name to "${it.code} · ${it.label}" },
                        false, "rhythm-progress-category") { categoryName = requireNotNull(it) }
                    Text("${category.code} · ${if (category in progress.unlocked) "Desbloqueada" else "Bloqueada"}", color = GardenTokens.Primary)
                    Text("${state.bpm} BPM recomendados · ${state.highStreak}/2 resultados altos consecutivos", modifier = Modifier.testTag("rhythm-progress-value"))
                    Text(if (state.mastered) "Hito de dominio alto registrado. El acceso adquirido se conserva."
                        else "Todavía no hay un hito de dominio alto registrado.")
                    val required = rhythmPrerequisites[category].orEmpty()
                    Text(if (required.isEmpty()) "Disponible desde el inicio." else "Requiere dominio alto en: " + required.joinToString { "${it.code} ${it.label}" })
                    if (state.masteryEvidence.isNotEmpty()) Text("Ejercicios del hito: ${state.masteryEvidence.joinToString()}", style = MaterialTheme.typography.bodySmall)
                }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
                        FilterChip(selected = !simulated, onClick = { simulated = false }, label = { Text("Decisión real") },
                            modifier = Modifier.testTag("rhythm-dda-real").heightIn(min = GardenTokens.TouchMinimum))
                        FilterChip(selected = simulated, onClick = { simulated = true }, label = { Text("Simulación") },
                            modifier = Modifier.testTag("rhythm-dda-simulated").heightIn(min = GardenTokens.TouchMinimum))
                    }
                }
                if (!simulated) item {
                    if (latest == null) Text("Todavía no hay resultados de esta célula. Elegila en un ejercicio nuevo para empezar.")
                    else RhythmRecordDecisions(latest, progress, category)
                } else {
                    item {
                        Text("Datos sintéticos · no se guardan en tu perfil", color = GardenTokens.TextSecondary)
                        HistoryChoice("Escenario", scenarioName, RhythmDdaScenario.entries.map { it.name to it.label },
                            false, "rhythm-dda-scenario") { scenarioName = requireNotNull(it) }
                        OutlinedButton(onClick = { model.simulate(RhythmDdaScenario.valueOf(scenarioName)) }, enabled = !ui.simulationBusy,
                            modifier = Modifier.testTag("rhythm-dda-run").heightIn(min = GardenTokens.TouchMinimum)) { Text("Ejecutar simulación") }
                        if (ui.simulationBusy) Text("Calculando simulación…")
                        ui.simulationError?.let { Text(it, color = GardenTokens.ErrorText) }
                    }
                    ui.simulation?.let { report ->
                        item {
                            Text("Simulación: ${report.scenario}", style = MaterialTheme.typography.titleMedium)
                            report.notes.forEach { Text(it) }
                            Text("${report.records.size} ejercicios simulados · ${report.progress.unlocked.size} células desbloqueadas en este escenario")
                            HistoryChoice("Ejercicio simulado", step.toString(), report.records.indices.map { it.toString() to "Ejercicio ${it + 1}" },
                                false, "rhythm-dda-step") { step = requireNotNull(it).toInt() }
                        }
                        report.records.getOrNull(step)?.let { record -> item { RhythmRecordDecisions(record, report.progress) } }
                    }
                }
            }
        }
    }
}

@Composable
internal fun RhythmRecordDecisions(record: RhythmRecord, progress: RhythmProgress, category: RhythmCategory? = null) {
    Text("Ejercicio ${record.id} · revisión ${record.revision}", style = MaterialTheme.typography.bodySmall)
    Text("${if (record.result.origin == "progression") "Progresión" else "Práctica"} · ${record.result.score.bpm} BPM · ${if (record.result.completed) "Completo" else "Interrumpido"}")
    record.dda?.let { commit ->
        Text("Motor ${commit.configuration.version} · ${commit.configuration.samplingPoints} puntos · centroide · AND/implicación mínimo · agregación máximo", style = MaterialTheme.typography.bodySmall)
        if (commit.evaluatedRevision != record.revision)
            Text("Evaluación musical revisada. La progresión se conserva en la revisión ${commit.evaluatedRevision}, usada por ejercicios posteriores.", color = GardenTokens.TextSecondary)
        if (commit.newlyUnlocked.isNotEmpty()) Text("Desbloqueadas: ${commit.newlyUnlocked.joinToString { it.code }}", color = GardenTokens.Primary)
        if (commit.decisions.any { it.after != commit.after[it.category] })
            Text("Se conserva el tempo y los desbloqueos adquiridos antes de la revisión tardía.", color = GardenTokens.TextSecondary)
    }
    rhythmDiagnostics(record, progress).filter { category == null || it.category == category }.forEach { decision ->
        Column(Modifier.fillMaxWidth().padding(vertical = GardenTokens.SpaceSm), verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceXs)) {
            Text("${decision.category.code} · ${decision.category.label}", style = MaterialTheme.typography.titleMedium)
            Text(decision.reason.label, modifier = Modifier.testTag("rhythm-dda-reason"))
            Text("${decision.occurrences} apariciones objetivo · ${decision.successes}/${decision.units} unidades exitosas · ${decision.extras} extras atribuibles")
            Text("Precisión ${ddaNumber(decision.precision)} · control ${ddaNumber(decision.control)}")
            Text("Dominio ${decision.mastery ?: "sin evidencia suficiente"} · centroide ${ddaNumber(decision.inference?.centroid)}")
            Text("Racha ${decision.before.highStreak} → ${decision.after.highStreak} · tempo ${decision.before.bpm} → ${decision.after.bpm} BPM")
            Text("Propuesta +${decision.proposedDelta} BPM · acción +${decision.appliedDelta} BPM")
            decision.inference?.let { inference ->
                FuzzyRuleDetails(inference)
            }
        }
    }
}

private fun ddaNumber(value: Double?): String = value?.let { String.format(Locale.getDefault(), "%.3f", it) } ?: "—"
