package ar.edu.unmdp.entrenamiento.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ar.edu.unmdp.entrenamiento.instruments.*
import java.util.Locale

@Composable
internal fun InstrumentDdaInspector(ui: InstrumentUiState, onClose: () -> Unit,
    onScenario: (InstrumentDdaScenario, InstrumentLevel) -> Unit) {
    var simulated by rememberSaveable { mutableStateOf(false) }
    var levelName by rememberSaveable { mutableStateOf("A") }
    var selectedRound by rememberSaveable { mutableIntStateOf(-1) }
    val historical = ui.history.firstOrNull()
    val answers = if (simulated) ui.simulation?.answers.orEmpty()
        else ui.game.answers.ifEmpty { historical?.answers.orEmpty() }
    val configuration = if (simulated) ui.simulation?.ddaConfiguration
        else if (ui.game.answers.isNotEmpty()) ui.game.result?.ddaConfiguration ?: ui.ddaConfiguration
        else historical?.ddaConfiguration
    val index = if (selectedRound < 0) answers.lastIndex else selectedRound.coerceAtMost(answers.lastIndex)
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.fillMaxSize().padding(16.dp).testTag("dda-inspector"),
            shape = RoundedCornerShape(18.dp), color = Night) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Inspección DDA", style = MaterialTheme.typography.titleLarge)
                    TextButton(onClick = onClose, modifier = Modifier.testTag("dda-close")) { Text("Cerrar") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(selected = !simulated, onClick = { simulated = false; selectedRound = -1 }, label = { Text("Partida real") })
                    FilterChip(selected = simulated, onClick = { simulated = true; selectedRound = -1 },
                        label = { Text("Simulación") }, modifier = Modifier.testTag("dda-simulated"))
                }
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Parámetros de prueba, pendientes de calibración.", color = Muted)
                    if (simulated) {
                        Text("DATOS SIMULADOS · No cambian tu partida ni tu historial.", color = Pink)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            InstrumentLevel.entries.forEach { level ->
                                FilterChip(selected = levelName == level.name, onClick = { levelName = level.name },
                                    enabled = !ui.simulationBusy, label = { Text(level.label) }, modifier = Modifier.testTag("dda-level-${level.name}"))
                            }
                        }
                        InstrumentDdaScenario.entries.forEach { scenario ->
                            OutlinedButton(onClick = { selectedRound = -1; onScenario(scenario, InstrumentLevel.valueOf(levelName)) },
                                enabled = !ui.simulationBusy, modifier = Modifier.fillMaxWidth().testTag("dda-scenario-${scenario.name}")) {
                                Text(scenario.label)
                            }
                        }
                        if (ui.simulationBusy) Text("Calculando escenario…")
                        ui.simulation?.let { Text("${ui.simulationScenario?.label} · ${it.level.label} · 10 preguntas", color = Lavender) }
                    } else {
                        val origin = if (ui.game.answers.isNotEmpty()) ui.game.mode.name.lowercase(Locale.ROOT) else historical?.origin
                        Text(if (origin == "automatic") "Partida automática · ayuda adaptable" else "Práctica manual · ayuda fija")
                    }
                    if (index >= 0) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            TextButton(onClick = { selectedRound = index-1 }, enabled = index > 0,
                                modifier = Modifier.testTag("dda-previous-decision")) { Text("Anterior") }
                            Text("Pregunta ${answers[index].round}", modifier = Modifier.testTag("dda-selected-round"))
                            TextButton(onClick = { selectedRound = index+1 }, enabled = index < answers.lastIndex,
                                modifier = Modifier.testTag("dda-next-decision")) { Text("Siguiente") }
                        }
                        val decision = answers[index].ddaDecision
                        if (decision == null) Text("Esta partida anterior no registra decisiones DDA.")
                        else DecisionDetails(decision)
                    } else Text(if (simulated) "Elegí un escenario para reproducir sus decisiones." else "Respondé una pregunta para inspeccionar su decisión.")
                    configuration?.let { ConfigurationDetails(it) }
                }
            }
        }
    }
}

private fun number(value: Double) = String.format(Locale.ROOT, "%.3f", value)

@Composable
private fun DecisionDetails(decision: InstrumentDdaDecision) {
    Text("Ventana: preguntas ${decision.windowRounds.joinToString()}")
    Text("Exactitud: ${number(decision.accuracy)} (${decision.correctCount}/${decision.windowRounds.size})")
    Text("Autonomía: ${number(decision.autonomy)} (${decision.firstListenCorrectCount}/${decision.correctCount} aciertos con primera escucha)")
    decision.inference?.let { inference ->
        Text("Pertenencias", style = MaterialTheme.typography.titleMedium)
        inference.memberships.forEach { (input, terms) -> Text("$input: " + terms.entries.joinToString { "${it.key}=${number(it.value)}" }) }
        Text("Reglas · fuerza de activación", style = MaterialTheme.typography.titleMedium)
        inference.rules.forEach { Text("${it.id}: ${it.label} · ${number(it.strength)}") }
        Text("Centroide: ${inference.centroid?.let(::number) ?: "sin salida"}")
        inference.diagnostic?.let { Text(it, color = Pink) }
    }
    Text("Propuesta: ${deltaLabel(decision.proposedDelta)}")
    Text("Aplicada: ${deltaLabel(decision.appliedDelta)} · ${decision.listensBefore} → ${decision.listensAfter} escuchas",
        modifier = Modifier.testTag("dda-applied"), color = Lavender)
    Text("Motivo: ${decision.reason.label}", modifier = Modifier.testTag("dda-reason"))
    Text("Configuración: ${decision.configurationVersion}", color = Muted)
}

private fun deltaLabel(delta: Int): String = when (delta) {
    -1 -> "quitar una escucha"
    1 -> "agregar una escucha"
    else -> "mantener"
}

@Composable
private fun ConfigurationDetails(configuration: InstrumentDdaConfiguration) {
    Text("Configuración de prueba · ${configuration.inference.version}", style = MaterialTheme.typography.titleMedium)
    Text("AND/implicación: mínimo · agregación: máximo · centroide: ${configuration.inference.samplingPoints} puntos")
    Text("Umbrales de acción: ±${configuration.actionThreshold}")
    configuration.inference.inputs.forEach { (name, terms) ->
        Text("$name: " + terms.entries.joinToString { (term, shape) -> "$term (${shape.a}, ${shape.b}, ${shape.c}, ${shape.d})" })
    }
    configuration.inference.outputs.forEach { (term, shape) -> Text("Salida $term: (${shape.a}, ${shape.b}, ${shape.c}, ${shape.d})") }
}
