package ar.edu.unmdp.entrenamiento.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ar.edu.unmdp.entrenamiento.R
import ar.edu.unmdp.entrenamiento.instruments.*

@Composable
fun InstrumentApp(ui: InstrumentUiState, onLevel: (InstrumentLevel) -> Unit, onStart: () -> Unit,
    onListen: () -> Unit, onAnswer: (Instrument) -> Unit, onNext: () -> Unit, onHome: () -> Unit,
    onMode: (InstrumentMode) -> Unit, onInspect: () -> Unit, onCloseInspector: () -> Unit,
    onScenario: (InstrumentDdaScenario, InstrumentLevel) -> Unit, onRetryStorage: () -> Unit = {},
    onHistory: () -> Unit = {}) {
    TunerGardenTheme {
        BackHandler(onBack = onHome)
        Scaffold(containerColor = GardenTokens.Background, contentWindowInsets = WindowInsets.safeDrawing) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                GardenPageDecoration(Modifier.matchParentSize())
                Column(Modifier.fillMaxSize().padding(horizontal = GardenTokens.SpaceLg)) {
                    TextButton(onClick = onHome, modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum)
                        .semantics { contentDescription = "Volver al menú" }) { Text("Volver al libro") }
                    if (ui.setup) InstrumentSetup(ui, onLevel, onMode, onStart, onHistory, onInspect, onRetryStorage)
                    else if (ui.game.phase in listOf(InstrumentPhase.FINISHED, InstrumentPhase.INTERRUPTED))
                        InstrumentSummary(ui, onStart, onHome, onHistory, onInspect, onRetryStorage)
                    else InstrumentQuestion(ui, onListen, onAnswer, onNext, onRetryStorage, onInspect)
                }
            }
        }
        if (ui.inspectorOpen) InstrumentDdaInspector(ui, onCloseInspector, onScenario)
    }
}

@Composable
private fun ColumnScope.InstrumentSetup(ui: InstrumentUiState, onLevel: (InstrumentLevel) -> Unit,
    onMode: (InstrumentMode) -> Unit, onStart: () -> Unit, onHistory: () -> Unit,
    onInspect: () -> Unit, onRetryStorage: () -> Unit) {
    Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(bottom = GardenTokens.SpaceLg),
        verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceBase)) {
        Image(painterResource(R.drawable.garden_instruments), null,
            Modifier.fillMaxWidth().height(120.dp), contentScale = ContentScale.Fit)
        Text("¿Qué instrumento es?", style = MaterialTheme.typography.headlineMedium)
        Text("Escuchá una melodía y descubrí su timbre.", color = GardenTokens.TextSecondary)
        GardenPaperPanel {
            Text("10 preguntas · Sin límite de tiempo", style = MaterialTheme.typography.titleMedium)
            Text("Cada pregunta trae una frase nueva. Al volver a escuchar, la melodía se repite igual.")
            Text("Podés responder mientras suena.", color = GardenTokens.TextSecondary)
        }
        Text("Elegí cómo practicar", style = MaterialTheme.typography.titleMedium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
            FilterChip(selected = ui.selectedMode == InstrumentMode.AUTOMATIC,
                enabled = !ui.busy, onClick = { onMode(InstrumentMode.AUTOMATIC) },
                label = { Text("Automático") }, modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("mode-automatic"))
            FilterChip(selected = ui.selectedMode == InstrumentMode.MANUAL,
                enabled = !ui.busy, onClick = { onMode(InstrumentMode.MANUAL) },
                label = { Text("Práctica manual") }, modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("mode-manual"))
        }
        Text(if (ui.selectedMode == InstrumentMode.AUTOMATIC)
            "Empezás en ${InstrumentLevel.entries[ui.progress.level].label}. La ayuda se adapta entre preguntas, con una a tres escuchas."
            else "Elegí un nivel para practicar con tres escuchas por pregunta. Esta modalidad no desbloquea niveles.",
            color = GardenTokens.TextSecondary)
        GardenProgressSummary(ui.progress, InstrumentLevel.entries.map { it.label })
        Text("Niveles · Tocá uno para practicarlo", style = MaterialTheme.typography.titleMedium)
        InstrumentLevel.entries.forEach { level ->
            val selected = if (ui.selectedMode == InstrumentMode.AUTOMATIC) ui.progress.level == level.ordinal else ui.selectedLevel == level
            GardenLevelChoice("${level.label} · ${level.description}", level.instruments.joinToString(" · ") { it.label },
                selected, !ui.busy, Modifier.testTag("level-${level.name}"), onClick = { onLevel(level) })
        }
        InstrumentErrors(ui, onRetryStorage)
        Button(onClick = onStart, enabled = !ui.busy && ui.storageError == null,
            shape = RoundedCornerShape(GardenTokens.RadiusLarge),
            modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum).testTag("instrument-start"),
            contentPadding = PaddingValues(GardenTokens.SpaceBase)) { Text("Jugar instrumentos") }
        InstrumentSecondaryActions(ui, onHistory, onInspect)
    }
}

@Composable
private fun ColumnScope.InstrumentQuestion(ui: InstrumentUiState, onListen: () -> Unit,
    onAnswer: (Instrument) -> Unit, onNext: () -> Unit, onRetryStorage: () -> Unit, onInspect: () -> Unit) {
    val state = ui.game
    val feedback = state.phase == InstrumentPhase.FEEDBACK
    // Reiniciar scroll al presentar otra pregunta, no durante reproducción o respuesta.
    val scroll = key(state.round) { rememberScrollState() }
    Column(Modifier.weight(1f).verticalScroll(scroll).padding(bottom = GardenTokens.SpaceLg),
        verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceMd)) {
        FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceBase)) {
            Text("Pregunta ${state.round} de 10", style = MaterialTheme.typography.titleMedium)
            Text(state.level.label, color = GardenTokens.TextSecondary)
        }
        LinearProgressIndicator(progress = { state.answers.size / 10f }, modifier = Modifier.fillMaxWidth(),
            color = GardenTokens.Primary, trackColor = GardenTokens.SurfaceSelected)
        GardenGreenhouse(Modifier.fillMaxWidth().height(120.dp).testTag("instrument-greenhouse"))
        Text(if (state.phase == InstrumentPhase.PLAYBACK) "Escuchá la melodía…"
            else if (feedback) "Compará tu respuesta" else "¿Cuál escuchaste?", style = MaterialTheme.typography.headlineSmall)
        OutlinedButton(onClick = onListen,
            enabled = !ui.busy && state.phase in listOf(InstrumentPhase.READY, InstrumentPhase.RESPONSE) && state.listensRemaining > 0,
            modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum).testTag("instrument-listen"),
            shape = RoundedCornerShape(GardenTokens.RadiusLarge), contentPadding = PaddingValues(GardenTokens.SpaceMd)) {
            Text(if (state.listensUsed == 0) "Escuchar la melodía" else "Volver a escuchar")
        }
        Text("${state.listensRemaining} ${if (state.listensRemaining == 1) "escucha restante" else "escuchas restantes"}",
            color = GardenTokens.TextSecondary)
        val largeText = LocalDensity.current.fontScale > 1.3f
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val columns = if (maxWidth < 300.dp || largeText) 1 else 2
            Column(verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
                state.options.chunked(columns).forEachIndexed { rowIndex, options ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
                        options.forEachIndexed { columnIndex, instrument ->
                            val answer = state.answers.lastOrNull().takeIf { feedback }
                            val correct = answer?.target == instrument
                            val incorrect = answer?.selected == instrument && !correct
                            val ink = if (incorrect) GardenTokens.ErrorText else GardenTokens.Text
                            GardenChoiceFocus(Modifier.weight(1f)) { interaction ->
                                Surface(onClick = { onAnswer(instrument) }, enabled = !ui.busy && state.canAnswer, interactionSource = interaction,
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp).testTag("instrument-option-${rowIndex * columns + columnIndex}"),
                                    color = when { correct -> GardenTokens.SurfaceSelected; incorrect -> GardenTokens.ErrorSurface;
                                        !state.canAnswer || ui.busy -> GardenTokens.DisabledSurface; else -> GardenTokens.Surface },
                                    shape = RoundedCornerShape(GardenTokens.RadiusLarge),
                                    border = BorderStroke(if (correct || incorrect) 2.dp else 1.dp,
                                        if (incorrect) GardenTokens.ErrorText else GardenTokens.Border)) {
                                    Column(Modifier.padding(GardenTokens.SpaceMd), verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceXs)) {
                                        Text(instrument.label, fontWeight = FontWeight.SemiBold,
                                            color = if (!feedback && (!state.canAnswer || ui.busy)) GardenTokens.DisabledText else ink)
                                        if (correct) Text("Respuesta correcta", style = MaterialTheme.typography.bodySmall, color = GardenTokens.Primary)
                                        if (incorrect) Text("Tu respuesta", style = MaterialTheme.typography.bodySmall, color = GardenTokens.ErrorText)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (feedback) {
            val answer = state.answers.last()
            GardenPaperPanel(Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
                Text(if (answer.correct) "¡Correcto! Era ${answer.target.label}." else "Era ${answer.target.label}. Elegiste ${answer.selected.label}.",
                    color = if (answer.correct) GardenTokens.Primary else GardenTokens.ErrorText)
                if (state.mode == InstrumentMode.AUTOMATIC) Text("Próxima pregunta: ${answer.ddaDecision?.listensAfter ?: 3} escuchas", color = GardenTokens.TextSecondary)
            }
            Button(onClick = onNext, enabled = !ui.busy, modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum),
                contentPadding = PaddingValues(GardenTokens.SpaceBase)) { Text("Siguiente pregunta") }
        } else Text("Podés responder mientras suena.", color = GardenTokens.TextSecondary)
        InstrumentErrors(ui, onRetryStorage)
        TextButton(onClick = onInspect, modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("dda-open")) { Text("Inspección DDA") }
    }
}

@Composable
private fun ColumnScope.InstrumentSummary(ui: InstrumentUiState, onStart: () -> Unit, onHome: () -> Unit,
    onHistory: () -> Unit, onInspect: () -> Unit, onRetryStorage: () -> Unit) {
    val state = ui.game
    Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(bottom = GardenTokens.SpaceLg),
        verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceBase)) {
        Text(if (state.phase == InstrumentPhase.FINISHED) "Partida terminada" else "Partida interrumpida", style = MaterialTheme.typography.headlineMedium)
        GardenPaperPanel {
            Text("${state.correctCount} / ${state.answers.size}", style = MaterialTheme.typography.headlineLarge, color = GardenTokens.Primary)
            Text("${state.answers.size} preguntas respondidas")
            Text(state.level.label, color = GardenTokens.TextSecondary)
            Text(if (state.mode == InstrumentMode.AUTOMATIC) "Modalidad automática" else "Práctica manual", color = GardenTokens.TextSecondary)
        }
        GardenProgressSummary(ui.progress, InstrumentLevel.entries.map { it.label })
        Text("Repasá lo que escuchaste", style = MaterialTheme.typography.titleMedium)
        state.answers.forEach { answer ->
            GardenPaperPanel {
                Text("${answer.round}. ${answer.target.label} · ${if (answer.correct) "Correcto" else "Incorrecto"}",
                    color = if (answer.correct) GardenTokens.Primary else GardenTokens.ErrorText, fontWeight = FontWeight.SemiBold)
                Text("Tu respuesta: ${answer.selected.label} · ${answer.listensUsed} ${if (answer.listensUsed == 1) "escucha" else "escuchas"}", color = GardenTokens.TextSecondary)
            }
        }
        InstrumentErrors(ui, onRetryStorage)
        Button(onClick = onStart, enabled = !ui.busy && ui.storageError == null,
            modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum),
            contentPadding = PaddingValues(GardenTokens.SpaceBase)) { Text("Otra partida") }
        OutlinedButton(onClick = onHome, modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum)) { Text("Volver al menú") }
        InstrumentSecondaryActions(ui, onHistory, onInspect)
        state.result?.mastery?.let { MasteryButton(it) }
    }
}

@Composable
private fun InstrumentSecondaryActions(ui: InstrumentUiState, onHistory: () -> Unit, onInspect: () -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
        TextButton(onClick = onHistory, enabled = !ui.busy && ui.storageError == null,
            modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("instrument-history")) { Text("Ver historial") }
        TextButton(onClick = onInspect, modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("dda-open")) { Text("Inspección DDA") }
    }
}

@Composable
private fun InstrumentErrors(ui: InstrumentUiState, onRetryStorage: () -> Unit) {
    if (ui.busy) LinearProgressIndicator(Modifier.fillMaxWidth().testTag("instrument-busy"))
    if (ui.storageError != null || ui.error != null) GardenPaperPanel(Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
        ui.storageError?.let {
            Text(it, color = GardenTokens.ErrorText)
            TextButton(onClick = onRetryStorage, enabled = !ui.busy) { Text("Reintentar lectura") }
        }
        ui.error?.let { Text(it, color = GardenTokens.ErrorText) }
    }
}
