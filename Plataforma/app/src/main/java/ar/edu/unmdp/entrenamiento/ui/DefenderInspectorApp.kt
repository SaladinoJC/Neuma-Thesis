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
import ar.edu.unmdp.entrenamiento.defender.*

@Composable
internal fun DefenderInspectorApp(ui: DefenderUiState, model: DefenderViewModel) {
    var simulated by rememberSaveable { mutableStateOf(false) }
    var scenario by rememberSaveable { mutableStateOf(DefenderScenario.FAST.name) }
    var stage by rememberSaveable { mutableStateOf(DefenderStage.N1.name) }
    var recordIndex by rememberSaveable(ui.simulation) { mutableIntStateOf(0) }
    var selectedDecision by rememberSaveable(simulated, ui.simulation, recordIndex, ui.detailId) { mutableIntStateOf(-1) }
    var masteryRules by rememberSaveable { mutableStateOf(false) }
    val current = (ui.history.results + ui.history.pending).filter {
        it.id == (ui.detailId ?: ui.currentRecordId) &&
            (ui.detailId != null || ui.game.phase != DefenderPhase.SETUP || it.variant == ui.variant)
    }.maxByOrNull { it.revision }
    val realRecord = current ?: if (ui.game.phase == DefenderPhase.SETUP && ui.detailId == null)
        ui.history.results.filter { it.variant == ui.variant }.maxByOrNull { it.startedAtMillis } else null
    val record = if (simulated) ui.simulation?.records?.getOrNull(recordIndex) else realRecord
    val game = if (!simulated && ui.detailId == null && ui.game.phase != DefenderPhase.SETUP) ui.game else record?.game
    val initialTarget = game?.configuration?.chordTarget ?: ui.chordTarget
    var qualityName by rememberSaveable(simulated, ui.detailId, recordIndex) { mutableStateOf(initialTarget.quality.name) }
    var clefsName by rememberSaveable(simulated, ui.detailId, recordIndex) { mutableStateOf(initialTarget.clefs.name) }
    var inversion by rememberSaveable(simulated, ui.detailId, recordIndex) { mutableIntStateOf(initialTarget.inversion) }
    val decisions = game?.ddaDecisions.orEmpty()
    val index = if (selectedDecision < 0) decisions.lastIndex else minOf(selectedDecision, decisions.lastIndex)
    val selectedStage = DefenderStage.valueOf(stage)
    val variant = if (simulated) ui.simulation?.variant ?: ui.variant else game?.configuration?.variant ?: ui.variant
    val chords = variant == "chords"
    val selectedQuality = DefenderChordQuality.valueOf(qualityName)
    val target = DefenderChordTarget(selectedQuality, minOf(inversion, selectedQuality.intervals.lastIndex), DefenderChordClefs.valueOf(clefsName))
    val progress = if (simulated) ui.simulation?.progress ?: DefenderProgress(variant) else ui.history.progressFor(variant)
    TunerGardenTheme {
        BackHandler(onBack = model::closeInspector)
        Scaffold(containerColor = GardenTokens.Background, contentWindowInsets = WindowInsets.safeDrawing) { padding ->
            LazyColumn(Modifier.fillMaxSize().padding(padding).testTag("defender-inspector-screen"),
                contentPadding = PaddingValues(GardenTokens.SpaceBase), verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceMd)) {
                item {
                    TextButton(onClick = model::closeInspector, modifier = Modifier.testTag("defender-inspector-back")) { Text("Volver a Defender") }
                    Text("Progreso y DDA", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.semantics { heading() })
                    Text("${if (chords) "Acordes" else "Notas"} · ${if (chords) progress.unlockedTargets.size else progress.unlocked.size}/${if (chords) defenderChordTargets().size else 4} contenidos disponibles · parámetros experimentales", color = GardenTokens.TextSecondary)
                    DefenderStorageNotice(ui, model)
                    if (chords) {
                        HistoryChoice("Calidad", qualityName, DefenderChordQuality.entries.map { it.name to it.label }, false,
                            "defender-progress-quality") { qualityName = requireNotNull(it) }
                        HistoryChoice("Clave", clefsName, DefenderChordClefs.entries.map { it.name to it.label }, false,
                            "defender-progress-clefs") { clefsName = requireNotNull(it) }
                        HistoryChoice("Inversión", target.inversion.toString(), selectedQuality.intervals.indices.map { it.toString() to defenderInversionLabel(it) }, false,
                            "defender-progress-inversion") { inversion = requireNotNull(it).toInt() }
                    }
                    else HistoryChoice("Etapa", stage, DefenderStage.entries.map { it.name to it.label }, false, "defender-progress-stage") { stage = requireNotNull(it) }
                    val entry = if (chords) progress[target] else progress[selectedStage]
                    val unlocked = if (chords) target in progress.unlockedTargets else selectedStage in progress.unlocked
                    Text("${if (chords) target.label else selectedStage.name} · ${if (unlocked) "Desbloqueada" else "Bloqueada"} · racha ${entry.highStreak}/2",
                        modifier = Modifier.testTag("defender-progress-value"))
                    if (chords) {
                        if (target.prerequisites.isEmpty()) Text("Disponible desde el inicio.")
                        else target.prerequisites.forEach { required ->
                            Text("${if (progress[required].mastered) "Cumplido" else "Falta dominar"}: ${required.label}")
                        }
                    } else Text(if (selectedStage.ordinal == 0) "Disponible desde el inicio."
                        else "Requiere dos partidas altas elegibles de ${DefenderStage.entries[selectedStage.ordinal - 1].name}.")
                    if (entry.masteryEvidence.isNotEmpty()) Text("Evidencia del hito: ${entry.masteryEvidence.joinToString()}")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
                        FilterChip(!simulated, { simulated = false }, label = { Text("Partida real") }, modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum))
                        FilterChip(simulated, { simulated = true }, label = { Text("Simulación") },
                            modifier = Modifier.testTag("defender-dda-simulated").heightIn(min = GardenTokens.TouchMinimum))
                    }
                }
                if (simulated) {
                    item {
                        Text("Datos sintéticos · no modifican el perfil ni reproducen audio", color = GardenTokens.TextSecondary)
                        HistoryChoice("Escenario", scenario, DefenderScenario.entries.filter { chords || it != DefenderScenario.CHORD_ROUTE }.map { it.name to if (chords && it == DefenderScenario.MIXED) "Ruta de cuatro tríadas" else it.label }, false,
                            "defender-dda-scenario") { scenario = requireNotNull(it) }
                        OutlinedButton(onClick = { model.simulate(DefenderScenario.valueOf(scenario), variant) }, enabled = !ui.simulationBusy,
                            modifier = Modifier.testTag("defender-dda-run")) { Text("Ejecutar simulación") }
                        if (ui.simulationBusy) Text("Calculando escenario…")
                        ui.simulationError?.let { Text(it, color = GardenTokens.ErrorText) }
                    }
                    ui.simulation?.let { report -> item {
                        Text(report.scenario, style = MaterialTheme.typography.titleMedium)
                        report.notes.forEach { Text(it) }
                        Text("${report.records.size} partidas simuladas · ${if (report.variant == "chords") report.progress.unlockedTargets.size else report.progress.unlocked.size}/${if (report.variant == "chords") defenderChordTargets().size else 4} contenidos en este escenario")
                        HistoryChoice("Partida simulada", recordIndex.toString(), report.records.indices.map { it.toString() to "Partida ${it + 1}" }, false,
                            "defender-dda-record") { recordIndex = requireNotNull(it).toInt() }
                    } }
                }
                item {
                    if (decisions.isEmpty()) Text("Todavía no hay enemigos resueltos para inspeccionar.")
                    else {
                        Text("Viaje de enemigos nuevos", style = MaterialTheme.typography.titleMedium)
                        HistoryChoice("Después de resolver", index.toString(), decisions.mapIndexed { i, d -> i.toString() to "${d.afterResolved} enemigos" }, false,
                            "defender-dda-decision") { selectedDecision = requireNotNull(it).toInt() }
                        val decision = decisions[index]
                        Text(decision.reason.label, modifier = Modifier.testTag("defender-dda-reason"))
                        Text("Enemigos de ventana: ${decision.enemyIds.joinToString()} · eventos [${decision.eventStartIndex}, ${decision.eventEndIndex})")
                        Text("${decision.correctShots} aciertos · ${decision.failedShots} intentos fallidos · ${decision.attempts} intentos")
                        Text("Supervivencia ${defenderNumber(decision.survival)} · precisión ${defenderNumber(decision.precision)}")
                        Text("Rapidez ${defenderNumber(decision.rapidity)} · solidez ${defenderNumber(decision.solidity)}")
                        Text("Centroide ${defenderNumber(decision.inference?.centroid)} · propuesta ${decision.proposedDeltaMillis / 1000} s · aplicada ${decision.appliedDeltaMillis / 1000} s")
                        Text("Viaje ${decision.beforeTravelMillis / 1000} → ${decision.afterTravelMillis / 1000} s. Los enemigos visibles conservan el suyo.")
                        Text("${decision.configurationVersion} · mínimo/máximo · centroide con ${game?.configuration?.dda?.inference?.samplingPoints} puntos", style = MaterialTheme.typography.bodySmall)
                        decision.inference?.let { FuzzyRuleDetails(it) }
                    }
                }
                record?.let { saved -> item {
                    Text("Desbloqueo al guardar", style = MaterialTheme.typography.titleMedium)
                    DefenderProgressEvidence(saved)
                    TextButton(onClick = { masteryRules = !masteryRules }) { Text(if (masteryRules) "Ocultar reglas del desbloqueo" else "Ver reglas del desbloqueo") }
                    if (masteryRules) saved.progress?.evidence?.forEach { evidence ->
                        Text(listOfNotNull(evidence.quality?.label, evidence.inversion?.let(::defenderInversionLabel), evidence.clef?.label)
                            .joinToString(" · ").ifEmpty { "Conjunto" }, style = MaterialTheme.typography.titleMedium)
                        evidence.inference?.let { FuzzyRuleDetails(it) }
                    }
                } }
                if (record == null && !simulated && ui.game.phase == DefenderPhase.PAUSED) item {
                    Text("La partida sigue en curso. El desbloqueo se evalúa al finalizar y cerrar el resumen.")
                }
            }
        }
    }
}
