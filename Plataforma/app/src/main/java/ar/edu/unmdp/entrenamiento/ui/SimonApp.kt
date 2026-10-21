package ar.edu.unmdp.entrenamiento.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import ar.edu.unmdp.entrenamiento.BuildConfig
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.unmdp.entrenamiento.simon.Phase
import ar.edu.unmdp.entrenamiento.simon.Scale
import ar.edu.unmdp.entrenamiento.simon.SimonResult
import ar.edu.unmdp.entrenamiento.instruments.InstrumentResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.sin

internal val Night = Color(0xFF0B121C)
internal val Panel = Color(0xFF1B2432)
internal val Pink = Color(0xFFF278AA)
internal val Lavender = Color(0xFFAE9CFF)
internal val SoftWhite = Color(0xFFF4F1FF)
internal val Muted = Color(0xFFA6B1C5)

fun noteName(midi: Int): String {
    val pitch = listOf("Do", "Do♯", "Re", "Re♯", "Mi", "Fa", "Fa♯", "Sol", "Sol♯", "La", "La♯", "Si")
    return "${pitch[midi % 12]}${midi / 12 - 1}"
}

@Composable
fun SimonApp(ui: SimonUiState, onSimon: () -> Unit, onScale: (Scale) -> Unit,
    onStart: () -> Unit, onAnswer: (Int) -> Unit, onHome: () -> Unit,
    onInstruments: () -> Unit = {}, instrumentHistory: List<InstrumentResult> = emptyList(),
    onAutomatic: () -> Unit = {}, onVoice: () -> Unit = {}, onData: () -> Unit = {}, onHistory: () -> Unit = {}, onRetryStorage: () -> Unit = {}) {
    MaterialTheme(colorScheme = darkColorScheme(primary = Pink, secondary = Lavender,
        background = Night, surface = Panel, onSurface = SoftWhite, onBackground = SoftWhite, onPrimary = Night)) {
        BackHandler(enabled = ui.screen != AppScreen.MENU, onBack = onHome)
        Scaffold(containerColor = Night, contentWindowInsets = WindowInsets.safeDrawing) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)
                .background(Brush.verticalGradient(listOf(Color(0xFF111828), Night, Color(0xFF0B1920))))) {
                if (ui.storageError != null && ui.screen != AppScreen.MENU) {
                    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
                        Text(ui.storageError)
                        TextButton(onClick = onRetryStorage) { Text("Reintentar lectura") }
                        TextButton(onClick = onHome) { Text("Volver al menú") }
                    }
                } else when (ui.screen) {
                    AppScreen.MENU -> MainMenu(ui, onSimon, onInstruments, instrumentHistory, onVoice, onData, onHistory)
                    AppScreen.SIMON_SETUP -> Setup(ui, onScale, onStart, onHome, onAutomatic)
                    AppScreen.SIMON_GAME -> Game(ui, onAnswer, onHome, onStart)
                }
                ui.error?.takeIf { ui.storageError == null }?.let {
                    Surface(Modifier.align(Alignment.BottomCenter).padding(16.dp),
                        color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(12.dp)) {
                        Text(it, Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }
        }
    }
}

@Composable
private fun MainMenu(ui: SimonUiState, onSimon: () -> Unit, onInstruments: () -> Unit, instrumentHistory: List<InstrumentResult>, onVoice: () -> Unit, onData: () -> Unit, onHistory: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text("♫", color = Lavender, fontSize = 32.sp)
        }
        Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
            SoundWaves(Modifier.fillMaxSize())
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Entrená tu oído", color = SoftWhite, fontSize = 36.sp, lineHeight = 40.sp,
                    textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold)
                Text("Versión ${BuildConfig.VERSION_NAME}", color = Color(0xFFBBDCF0), fontSize = 14.sp)
            }
        }
        Text("ELEGÍ TU PRÓXIMO DESAFÍO", color = Muted, fontSize = 10.sp, letterSpacing = 1.5.sp)
        MenuCard("Simón dice musical", "Recordá y repetí notas en el piano", Pink, "♫",
            "Disponible", Modifier.testTag("game-simon"), onClick = onSimon)
        MenuCard("¿Qué instrumento es?", "Descubrí el instrumento por su sonido",
            Color(0xFF71AAFF), "≋", "Disponible", Modifier.testTag("game-instruments"), onClick = onInstruments)
        MenuCard("Afiná tu voz", "Explorá las notas que cantás", Color(0xFF63D9B0), "♪", "Prueba técnica", Modifier.testTag("game-voice"), onClick = onVoice)
        OutlinedButton(onClick = onData, modifier = Modifier.fillMaxWidth().testTag("data-open")) { Text("Datos y demostración") }
        OutlinedButton(onClick = onHistory, modifier = Modifier.fillMaxWidth().testTag("history-open")) { Text("Ver todos los resultados") }
        if (ui.history.isNotEmpty()) {
            Text("Últimas partidas de Simón", fontWeight = FontWeight.SemiBold)
            ui.history.take(5).forEach { HistoryCard(it) }
        }
        if (instrumentHistory.isNotEmpty()) {
            Text("Últimas partidas de instrumentos", fontWeight = FontWeight.SemiBold)
            instrumentHistory.take(5).forEach { result ->
                Surface(color = Panel, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Instrumentos · ${result.level.label} · ${if (result.completed) "Completada" else "Interrumpida"}",
                            color = Color(0xFF86B9FF), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("${result.answers.count { it.correct }} / ${result.answers.size} aciertos · " +
                            SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(result.finishedAtMillis)), color = Muted, fontSize = 12.sp)
                        Text(if (result.origin == "automatic") "Automática · Ayuda adaptable" else "Práctica manual", color = Muted, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuCard(title: String, subtitle: String, accent: Color, symbol: String,
    badge: String, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    Surface(onClick = { onClick?.invoke() }, enabled = onClick != null,
        modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
        color = accent.copy(alpha = if (onClick != null) 0.16f else 0.08f),
        border = BorderStroke(if (onClick != null) 1.5.dp else 1.dp, accent.copy(alpha = 0.5f))) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(56.dp).clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(accent, accent.copy(alpha = 0.5f)))),
                contentAlignment = Alignment.Center) { Text(symbol, fontSize = 34.sp, color = SoftWhite) }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = SoftWhite, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(subtitle, color = Muted, fontSize = 12.sp, lineHeight = 16.sp)
                Text(badge, color = accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
            if (onClick != null) Text("›", color = accent, fontSize = 30.sp)
        }
    }
}

@Composable
private fun SoundWaves(modifier: Modifier) {
    Canvas(modifier) {
        for (line in 0..5) {
            val path = Path()
            for (step in 0..80) {
                val x = size.width * step / 80
                val y = size.height * 0.78f + sin(step * 0.085 + line * 0.18).toFloat() *
                    size.height * 0.12f + line * 5.dp.toPx()
                if (step == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, Brush.horizontalGradient(listOf(Lavender.copy(alpha = 0.08f),
                Color(0xFF38C5DC).copy(alpha = 0.25f))),
                style = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx()))
        }
        drawCircle(Lavender.copy(alpha = 0.07f), size.width * 0.22f, Offset(size.width * 0.07f, size.height * 0.25f))
    }
}

@Composable
private fun PageHeader(onHome: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = onHome, contentPadding = PaddingValues(0.dp),
            modifier = Modifier.width(48.dp).semantics { contentDescription = "Volver al menú" }) {
            Text("‹", color = SoftWhite, fontSize = 32.sp)
        }
        Text("♫", color = Pink, fontSize = 24.sp, modifier = Modifier.padding(end = 9.dp))
        Text("Simón dice musical", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
private fun Setup(ui: SimonUiState, onScale: (Scale) -> Unit, onStart: () -> Unit, onHome: () -> Unit, onAutomatic: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp).testTag("simon-viewport")) {
        PageHeader(onHome)
        Row(Modifier.weight(1f).padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
                Text("¿Cómo se juega?", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Escuchá la melodía y repetila en el piano. En cada ronda se agrega una nota. Al primer error, termina la partida.",
                    color = Muted, fontSize = 14.sp, lineHeight = 20.sp)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Instruction("♪", "Escuchá", Pink)
                    Instruction("▥", "Repetí", Lavender)
                    Instruction("☆", "Avanzá", Color(0xFFFFCD75))
                }
                ProgressSummary(ui.progress, Scale.entries.map { it.label })
                FilterChip(selected = ui.automatic, onClick = onAutomatic, label = { Text("Automático") }, modifier = Modifier.testTag("simon-automatic"))
                Text(if (ui.automatic) "Próxima: ${Scale.entries[ui.progress.level].label}" else "Práctica manual · No desbloquea", color = Muted, fontSize = 11.sp)
            }
            Column(Modifier.weight(1.15f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Seleccioná la escala", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Scale.entries.forEach { scale ->
                    val selected = !ui.automatic && ui.selectedScale == scale
                    Surface(onClick = { onScale(scale) }, enabled = !ui.busy,
                        modifier = Modifier.fillMaxWidth().weight(1f).testTag("scale-${scale.name}"),
                        shape = RoundedCornerShape(12.dp), color = if (selected) Pink.copy(alpha = 0.12f) else Panel,
                        border = BorderStroke(1.dp, if (selected) Pink else Color(0xFF303848))) {
                        Row(Modifier.padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.size(16.dp).clip(CircleShape)
                                .background(if (selected) Pink else Color(0xFF414B60)))
                            Text(scale.label, Modifier.weight(1f), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("${scale.intervals.size} notas", color = Muted, fontSize = 12.sp)
                        }
                    }
                }
                Button(onClick = onStart, enabled = !ui.busy && ui.storageError == null, modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp)) { Text("Jugar Simón", fontWeight = FontWeight.Bold) }
                TextButton(onClick = onHome, modifier = Modifier.fillMaxWidth().height(40.dp)) { Text("Volver al menú") }
            }
        }
    }
}

@Composable
private fun Instruction(symbol: String, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(symbol, color = color, fontSize = 26.sp)
        Text(label, fontSize = 12.sp)
    }
}

@Composable
private fun Game(ui: SimonUiState, onAnswer: (Int) -> Unit, onHome: () -> Unit, onStart: () -> Unit) {
    val state = ui.game
    val ended = state.phase == Phase.FINISHED || state.phase == Phase.INTERRUPTED
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 4.dp).testTag("simon-viewport"),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onHome, contentPadding = PaddingValues(0.dp),
                modifier = Modifier.width(48.dp).semantics { contentDescription = "Volver al menú" }) {
                Text("‹", color = SoftWhite, fontSize = 30.sp)
            }
            Column(Modifier.weight(1f)) {
                Text("Simón dice musical", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text("${state.scale.label} · Tónica ${noteName(state.tonicMidi)}", color = Muted, fontSize = 11.sp)
            }
            Text("Ronda ${state.sequence.size}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 16.dp))
            Text("${state.completedLength} completadas", color = Pink, fontSize = 12.sp)
            TextButton(onClick = onHome) { Text("Volver al menú", fontSize = 12.sp) }
        }
        if (ended) {
            Row(Modifier.weight(1f).fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1.5f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(if (state.phase == Phase.FINISHED) "Partida terminada" else "Partida interrumpida",
                        fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    state.result?.let { ResultCard(it) }
                    ProgressSummary(ui.progress, Scale.entries.map { it.label })
                    state.result?.mastery?.let { MasteryButton(it) }
                }
                Button(onClick = onStart, enabled = !ui.busy && ui.storageError == null, modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(16.dp)) { Text("Otra partida") }
            }
        } else {
            val listening = state.phase == Phase.PLAYBACK
            Surface(color = Panel, shape = RoundedCornerShape(12.dp)) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (ui.preparing) "Preparando la partida…" else if (listening) "Escuchá la secuencia…" else "Ahora repetí la secuencia",
                        fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.testTag("phase"))
                    Text(if (listening) "${state.sequence.size} ${if (state.sequence.size == 1) "nota" else "notas"}" else
                        "${state.responseIndex} / ${state.sequence.size}", color = Pink, fontSize = 14.sp,
                        modifier = Modifier.testTag("response-progress"))
                }
            }
            PianoKeyboard(state.midiNotes, ui.highlighted, state.phase == Phase.RESPONSE && !ui.busy,
                onAnswer, Modifier.fillMaxWidth().weight(1f))
            Text("Las teclas atenuadas no pertenecen a esta escala.", color = Muted, fontSize = 10.sp,
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ResultCard(result: SimonResult) {
    Surface(color = Panel, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Pink.copy(alpha = 0.3f))) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("${result.completedLength} ${if (result.completedLength == 1) "nota" else "notas"}",
                color = Pink, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Longitud máxima completada", fontSize = 13.sp)
            Text("Secuencia intentada (${result.attemptedSequence.size}): " +
                result.attemptedSequence.joinToString(" · ") { noteName(result.tonicMidi + result.scale.intervals[it]) },
                color = Muted, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            result.errorPosition?.let { Text("Error en la posición ${it + 1}.", color = Pink, fontSize = 12.sp) }
        }
    }
}

@Composable
private fun HistoryCard(result: SimonResult) {
    Surface(color = Panel.copy(alpha = 0.8f), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(result.scale.label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(if (result.completed) "Completada" else "Interrumpida", color = Pink, fontSize = 12.sp)
            }
            Text(if (result.origin == "automatic") "Automática" else "Práctica manual", color = Muted, fontSize = 12.sp)
            Text("${result.completedLength} notas completadas · ${noteName(result.tonicMidi)}", color = Muted, fontSize = 12.sp)
            Text(SimpleDateFormat("dd/MM HH:mm", Locale.forLanguageTag("es-AR")).format(Date(result.finishedAtMillis)), color = Muted, fontSize = 11.sp)
        }
    }
}
