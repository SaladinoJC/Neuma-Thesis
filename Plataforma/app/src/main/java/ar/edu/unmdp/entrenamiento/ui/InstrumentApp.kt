package ar.edu.unmdp.entrenamiento.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.unmdp.entrenamiento.instruments.*

private val InstrumentBlue = Color(0xFF86B9FF)
private val CorrectGreen = Color(0xFF7CDBB0)

@Composable
fun InstrumentApp(ui: InstrumentUiState, onLevel: (InstrumentLevel) -> Unit, onStart: () -> Unit,
    onListen: () -> Unit, onAnswer: (Instrument) -> Unit, onNext: () -> Unit, onHome: () -> Unit) {
    MaterialTheme(colorScheme = darkColorScheme(primary = InstrumentBlue, secondary = Lavender,
        background = Night, surface = Panel, onSurface = SoftWhite, onBackground = SoftWhite, onPrimary = Night)) {
        BackHandler(onBack = onHome)
        Scaffold(containerColor = Night, contentWindowInsets = WindowInsets.safeDrawing) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)
                .background(Brush.verticalGradient(listOf(Night, Color(0xFF101E30))))) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onHome, modifier = Modifier.width(44.dp)
                        .semantics { contentDescription = "Volver al menú" }, contentPadding = PaddingValues(0.dp)) {
                        Text("‹", fontSize = 32.sp, color = SoftWhite)
                    }
                    Text("¿Qué instrumento es?", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                }
                if (ui.setup) InstrumentSetup(ui, onLevel, onStart)
                else if (ui.game.phase in listOf(InstrumentPhase.FINISHED, InstrumentPhase.INTERRUPTED))
                    InstrumentSummary(ui, onStart, onHome)
                else InstrumentQuestion(ui, onListen, onAnswer, onNext)
                ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp)) }
            }
        }
    }
}

@Composable
private fun ColumnScope.InstrumentSetup(ui: InstrumentUiState, onLevel: (InstrumentLevel) -> Unit, onStart: () -> Unit) {
    Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
            Surface(shape = CircleShape, color = InstrumentBlue.copy(alpha = .12f), modifier = Modifier.size(90.dp)) {
                Box(contentAlignment = Alignment.Center) { Text("♫", color = InstrumentBlue, fontSize = 44.sp) }
            }
        }
        Text("Escuchá. Reconocé. Descubrí.", fontSize = 27.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold)
        Text("Escuchá un sonido y elegí cuál de los cuatro instrumentos lo produjo. Podés escucharlo hasta tres veces antes de responder.",
            color = Muted, lineHeight = 22.sp)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("10 preguntas", color = InstrumentBlue)
            Text("Sin límite de tiempo", color = Muted)
        }
        Text("Elegí el nivel", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 6.dp))
        InstrumentLevel.entries.forEach { level ->
            val selected = ui.selectedLevel == level
            Surface(onClick = { onLevel(level) }, enabled = !ui.busy,
                modifier = Modifier.fillMaxWidth().testTag("level-${level.name}"),
                shape = RoundedCornerShape(16.dp), color = if (selected) InstrumentBlue.copy(alpha = .12f) else Panel,
                border = BorderStroke(1.dp, if (selected) InstrumentBlue else Muted.copy(alpha = .25f))) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("${level.label} · ${level.description}", fontWeight = FontWeight.Bold)
                    Text(level.instruments.joinToString(" · ") { it.label }, fontSize = 12.sp, color = Muted)
                }
            }
        }
        Button(onClick = onStart, enabled = !ui.busy, shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().height(54.dp)) { Text("Jugar instrumentos", fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun ColumnScope.InstrumentQuestion(ui: InstrumentUiState, onListen: () -> Unit,
    onAnswer: (Instrument) -> Unit, onNext: () -> Unit) {
    val state = ui.game
    val feedback = state.phase == InstrumentPhase.FEEDBACK
    Column(Modifier.weight(1f).padding(bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Pregunta ${state.round} de 10", fontWeight = FontWeight.Bold)
            Text(state.level.label, color = InstrumentBlue)
        }
        LinearProgressIndicator(progress = { state.answers.size / 10f }, modifier = Modifier.fillMaxWidth(),
            color = InstrumentBlue, trackColor = Panel)
        Column(Modifier.weight(1f).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Surface(color = InstrumentBlue.copy(alpha = .1f), shape = CircleShape, modifier = Modifier.size(92.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(if (ui.busy) "♫" else "?", color = InstrumentBlue, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text(if (ui.busy) "Escuchá el sonido…" else if (feedback) "Compará tu respuesta" else "¿Cuál escuchaste?",
                fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 18.dp), textAlign = TextAlign.Center)
        }
        OutlinedButton(onClick = onListen,
            enabled = !ui.busy && !feedback && state.listensRemaining > 0,
            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("instrument-listen"), shape = RoundedCornerShape(14.dp)) {
            Text(if (state.listensUsed == 0) "Escuchar el sonido" else "Volver a escuchar")
        }
        Text("${state.listensRemaining} ${if (state.listensRemaining == 1) "escucha restante" else "escuchas restantes"}",
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Muted, fontSize = 12.sp)
        state.options.chunked(2).forEachIndexed { rowIndex, options ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                options.forEachIndexed { columnIndex, instrument ->
                    val answer = state.answers.lastOrNull().takeIf { feedback }
                    val accent = when {
                        answer?.target == instrument -> CorrectGreen
                        answer?.selected == instrument -> Pink
                        else -> InstrumentBlue
                    }
                    Surface(onClick = { onAnswer(instrument) },
                        enabled = !ui.busy && state.phase == InstrumentPhase.RESPONSE,
                        modifier = Modifier.weight(1f).height(76.dp).testTag("instrument-option-${rowIndex * 2 + columnIndex}"),
                        color = if (feedback) accent.copy(alpha = .12f) else Panel,
                        shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, accent.copy(alpha = .5f))) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(instrument.label, fontWeight = FontWeight.SemiBold, color = SoftWhite, fontSize = 17.sp)
                        }
                    }
                }
            }
        }
        if (feedback) {
            val answer = state.answers.last()
            Text(if (answer.correct) "¡Correcto! Era ${answer.target.label}." else "Era ${answer.target.label}. Elegiste ${answer.selected.label}.",
                color = if (answer.correct) CorrectGreen else Pink, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Button(onClick = onNext, enabled = !ui.busy, modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp)) { Text("Siguiente pregunta") }
        } else {
            Text("Tomate el tiempo que necesites.", color = Muted, fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ColumnScope.InstrumentSummary(ui: InstrumentUiState, onStart: () -> Unit, onHome: () -> Unit) {
    val state = ui.game
    Column(Modifier.weight(1f).padding(vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(if (state.phase == InstrumentPhase.FINISHED) "Partida terminada" else "Partida interrumpida",
                fontSize = 27.sp, fontWeight = FontWeight.Bold)
            Text("${state.correctCount} / ${state.answers.size}", color = InstrumentBlue, fontSize = 54.sp, fontWeight = FontWeight.Bold)
            Text("${state.answers.size} preguntas respondidas", color = Muted)
            Text(state.level.label, color = InstrumentBlue)
            state.answers.forEach { answer ->
                Surface(color = Panel, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("${answer.round}. ${answer.target.label} · ${if (answer.correct) "Correcto" else "Incorrecto"}",
                            color = if (answer.correct) CorrectGreen else Pink, fontWeight = FontWeight.SemiBold)
                        Text("Tu respuesta: ${answer.selected.label} · ${answer.listensUsed} ${if (answer.listensUsed == 1) "escucha" else "escuchas"}",
                            color = Muted, fontSize = 12.sp)
                    }
                }
            }
        }
        Button(onClick = onStart, enabled = !ui.busy, modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp)) { Text("Otra partida") }
        TextButton(onClick = onHome, modifier = Modifier.fillMaxWidth()) { Text("Volver al menú") }
    }
}
