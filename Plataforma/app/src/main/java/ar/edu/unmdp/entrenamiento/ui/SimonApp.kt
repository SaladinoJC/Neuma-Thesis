package ar.edu.unmdp.entrenamiento.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ar.edu.unmdp.entrenamiento.simon.Phase
import ar.edu.unmdp.entrenamiento.simon.Scale
import ar.edu.unmdp.entrenamiento.simon.SimonResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Ink = Color(0xFF213B46)
private val Clay = Color(0xFFB65B35)
private val Cream = Color(0xFFF6F3EC)
private val NoteColors = listOf(0xFFEAD2BC, 0xFFF0DCB1, 0xFFD6E4CA, 0xFFBDDDD7,
    0xFFC4D8EC, 0xFFD9CBE7, 0xFFE8CAD7, 0xFFC8D6CF, 0xFFDADBB8, 0xFFD3CEE7, 0xFFE6C8BD, 0xFFBCD8DB)

fun noteName(midi: Int): String {
    val pitch = listOf("Do", "Do♯", "Re", "Re♯", "Mi", "Fa", "Fa♯", "Sol", "Sol♯", "La", "La♯", "Si")
    return "${pitch[midi % 12]}${midi / 12 - 1}"
}

@Composable
fun SimonApp(
    ui: SimonUiState,
    onScale: (Scale) -> Unit,
    onStart: () -> Unit,
    onAnswer: (Int) -> Unit,
    onHome: () -> Unit,
) {
    MaterialTheme(colorScheme = lightColorScheme(primary = Ink, secondary = Clay,
        background = Cream, surface = Color.White, onSurface = Ink, onBackground = Ink)) {
        BackHandler(enabled = ui.inGame, onBack = onHome)
        Scaffold(containerColor = Cream, contentWindowInsets = WindowInsets.safeDrawing) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Text("ENTRENAMIENTO AUDITIVO", style = MaterialTheme.typography.labelMedium,
                    color = Clay, fontWeight = FontWeight.Bold)
                if (!ui.inGame) Home(ui, onScale, onStart)
                else Game(ui, onAnswer, onHome, onStart)
                ui.error?.let {
                    Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(12.dp)) {
                        Text(it, Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }
        }
    }
}

@Composable
private fun Home(ui: SimonUiState, onScale: (Scale) -> Unit, onStart: () -> Unit) {
    Text("Escuchá. Recordá.\nRepetí.", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
    Text("Simón musical", style = MaterialTheme.typography.titleLarge, color = Clay)
    Surface(color = Ink, shape = RoundedCornerShape(22.dp)) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Una secuencia, una nota más", style = MaterialTheme.typography.titleMedium,
                color = Color.White, fontWeight = FontWeight.Bold)
            Text("Escuchá y mirá los botones. Cuando sea tu turno, repetí las notas en el mismo orden. " +
                "Cada acierto agrega una nota; el primer error termina la partida.", color = Color(0xFFDBE5E8))
        }
    }
    Text("Elegí tu escala", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Scale.entries.forEach { scale ->
        val selected = ui.selectedScale == scale
        OutlinedButton(onClick = { onScale(scale) }, enabled = !ui.busy,
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
            border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) Ink else Color(0xFFD5DCD8)),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) Color(0xFFE5EBE6) else Color.White)) {
            Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(scale.label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                Text("${scale.intervals.size} notas")
            }
        }
    }
    Button(onClick = onStart, enabled = !ui.busy, modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp)) { Text("Jugar Simón") }
    Text("Práctica libre · Una tónica nueva en cada partida", style = MaterialTheme.typography.bodySmall)
    if (ui.history.isNotEmpty()) {
        HorizontalDivider()
        Text("Tus últimas partidas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        ui.history.take(5).forEach { result -> HistoryCard(result) }
    }
}

@Composable
private fun Game(ui: SimonUiState, onAnswer: (Int) -> Unit, onHome: () -> Unit, onStart: () -> Unit) {
    val state = ui.game
    Text("Simón musical", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
    Text("${state.scale.label} · Tónica ${noteName(state.tonicMidi)}", color = Clay)
    val ended = state.phase == Phase.FINISHED || state.phase == Phase.INTERRUPTED
    if (ended) {
        Text(if (state.phase == Phase.FINISHED) "Partida terminada" else "Partida interrumpida",
            style = MaterialTheme.typography.titleLarge)
        state.result?.let { ResultCard(it) }
        Button(onClick = onStart, enabled = !ui.busy, modifier = Modifier.fillMaxWidth().height(54.dp)) {
            Text("Otra partida")
        }
    } else {
        Surface(color = Ink, shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(if (state.phase == Phase.PLAYBACK) "Escuchá la secuencia" else "Tu turno",
                    color = Color.White, style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.testTag("phase"))
                Text(if (state.phase == Phase.PLAYBACK) "Los botones se iluminan en orden." else
                    "Repetí ${state.sequence.size} ${if (state.sequence.size == 1) "nota" else "notas"}.",
                    color = Color(0xFFDBE5E8))
                Text("Longitud completada: ${state.completedLength}", color = Color.White)
            }
        }
        val columns = if (state.midiNotes.size > 7) 4 else 3
        state.midiNotes.indices.chunked(columns).forEach { indexes ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                indexes.forEach { index ->
                    val highlighted = ui.highlighted == index
                    Surface(onClick = { onAnswer(index) },
                        enabled = state.phase == Phase.RESPONSE && !ui.busy,
                        modifier = Modifier.weight(1f).heightIn(min = 76.dp).testTag("note-$index")
                            .semantics { stateDescription = if (highlighted) "Sonando" else "En espera" },
                        shape = RoundedCornerShape(16.dp),
                        color = if (highlighted) Ink else Color(NoteColors[index]),
                        contentColor = if (highlighted) Color.White else Ink,
                        border = BorderStroke(if (highlighted) 3.dp else 1.dp, if (highlighted) Clay else Color.Transparent)) {
                        Column(Modifier.padding(vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(noteName(state.midiNotes[index]), style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold)
                            Text("${index + 1}", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                repeat(columns - indexes.size) { Spacer(Modifier.weight(1f)) }
            }
        }
        Text("Respondé cuando aparezca «Tu turno».", style = MaterialTheme.typography.bodySmall)
    }
    TextButton(onClick = onHome, modifier = Modifier.fillMaxWidth()) { Text("Volver al menú") }
}

@Composable
private fun ResultCard(result: SimonResult) {
    Surface(color = Color.White, shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("${result.completedLength} ${if (result.completedLength == 1) "nota" else "notas"}",
                style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text("Longitud máxima completada")
            Text("Secuencia intentada (${result.attemptedSequence.size}): " +
                result.attemptedSequence.joinToString(" · ") { noteName(result.tonicMidi + result.scale.intervals[it]) })
            result.errorPosition?.let { Text("Error en la posición ${it + 1}.", color = Clay) }
        }
    }
}

@Composable
private fun HistoryCard(result: SimonResult) {
    Surface(color = Color.White, shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(result.scale.label, fontWeight = FontWeight.Bold)
                Text(if (result.completed) "Completada" else "Interrumpida", color = Clay)
            }
            Text("${result.completedLength} notas completadas · ${noteName(result.tonicMidi)}")
            Text(SimpleDateFormat("dd/MM HH:mm", Locale.forLanguageTag("es-AR")).format(Date(result.finishedAtMillis)),
                style = MaterialTheme.typography.bodySmall)
        }
    }
}
