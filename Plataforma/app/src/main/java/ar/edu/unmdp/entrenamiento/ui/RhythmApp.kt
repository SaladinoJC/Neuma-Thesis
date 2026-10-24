package ar.edu.unmdp.entrenamiento.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ar.edu.unmdp.entrenamiento.rhythm.*
import java.util.Locale

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun RhythmApp(ui: RhythmUiState, model: RhythmViewModel, onHome: () -> Unit) {
    val state = ui.session
    val active = state.phase == RhythmPhase.COUNT_IN || state.phase == RhythmPhase.PLAYING
    val score = state.score
    val barRequests = remember { List(4) { BringIntoViewRequester() } }
    val meter = score?.beatsPerBar ?: ui.beatsPerBar
    val bar = score?.let { (state.elapsedNanos.coerceAtLeast(0) / Beat(it.beatsPerBar).nanos(it.bpm)).toInt().coerceIn(0, 3) } ?: 0
    LaunchedEffect(bar, state.phase) {
        if (state.phase == RhythmPhase.PLAYING) barRequests[bar].bringIntoView()
    }
    MaterialTheme(colorScheme = darkColorScheme(primary = Lavender, secondary = Pink,
        background = Night, surface = Panel, onSurface = SoftWhite, onBackground = SoftWhite)) {
        BackHandler(onBack = onHome)
        Scaffold(containerColor = Night, contentWindowInsets = WindowInsets.safeDrawing) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onHome) { Text("Menú") }
                    Text("Rhythm Tap", style = MaterialTheme.typography.headlineSmall)
                }
                Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).testTag("rhythm-scroll"),
                    verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Práctica · 4 compases · $meter/4 · 60 BPM", color = Lavender)
                    if (!active) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            (2..4).forEach { beats ->
                                FilterChip(selected = ui.beatsPerBar == beats, enabled = !ui.busy,
                                    onClick = { model.meter(beats) }, modifier = Modifier.testTag("rhythm-meter-$beats"),
                                    label = { Text("$beats/4") })
                            }
                        }
                        Text("Elegí las células que querés practicar")
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            RhythmCategory.entries.forEach { category ->
                                FilterChip(selected = category in ui.selected, enabled = !ui.busy,
                                    onClick = { model.select(category) }, modifier = Modifier.testTag("rhythm-category-${category.code}"),
                                    label = { Text(category.label) })
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(ui.metronome, model::metronome, enabled = !ui.busy, modifier = Modifier.testTag("rhythm-metronome"))
                            Text("Metrónomo después del conteo")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(ui.tapSound, model::tapSound, enabled = !ui.busy, modifier = Modifier.testTag("rhythm-tap-sound"))
                            Text("Sonido de mis taps reales")
                        }
                        Button(onClick = { model.generate() }, enabled = !ui.busy && ui.selected.isNotEmpty(),
                            modifier = Modifier.testTag("rhythm-generate")) { Text("Generar ejercicio") }
                    }
                    ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    Text(when (state.phase) {
                        RhythmPhase.SETUP -> "Generá y revisá antes de tocar Play"
                        RhythmPhase.PREVIEW -> "Vista previa · No suena el patrón objetivo"
                        RhythmPhase.COUNT_IN -> "Conteo · ${((state.elapsedNanos + Beat(meter).nanos(60)) / Beat(1).nanos(60) + 1).coerceIn(1, meter.toLong())} de $meter"
                        RhythmPhase.PLAYING -> "Tocá el ritmo · Compás ${bar + 1} de 4"
                        RhythmPhase.RESULTS -> if (state.result?.completed == true) "Ejercicio completado" else "Ejercicio interrumpido"
                    }, modifier = Modifier.testTag("rhythm-status"), fontWeight = FontWeight.SemiBold)
                    if (ui.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
                    if (score != null) {
                        score.bars.forEach { measure ->
                            Column(Modifier.bringIntoViewRequester(barRequests[measure.index])) {
                                Text("Compás ${measure.index + 1}", style = MaterialTheme.typography.labelMedium)
                                RhythmBarDrawing(measure, score, state, Modifier.fillMaxWidth().height(100.dp))
                            }
                        }
                    }
                    state.result?.let { result ->
                        Text("Resumen en memoria · Práctica", fontWeight = FontWeight.Bold, modifier = Modifier.testTag("rhythm-summary"))
                        val onTime = result.attacks.count { it.kind == TapKind.ON_TIME }
                        Text("A tiempo: $onTime / ${result.attacks.size} · Omitidos: ${result.omissions} · Extras: ${result.extras}")
                        Text("Adelantados: ${result.attacks.count { it.kind == TapKind.EARLY }} · Atrasados: ${result.attacks.count { it.kind == TapKind.LATE }}")
                        score?.selected?.forEach { category ->
                            val owners = score.occurrences.filter { it.target && it.category == category }.map { it.index }.toSet()
                            val attacks = result.attacks.filter { it.occurrenceIndex in owners }
                            Text("${category.label}: ${attacks.count { it.kind == TapKind.ON_TIME }} / ${attacks.size} a tiempo (células objetivo)")
                            val extras = result.taps.count { tap ->
                                tap.kind == TapKind.EXTRA && score.occurrences.any { occurrence ->
                                    val start = (Beat(occurrence.bar * score.beatsPerBar) + occurrence.beat).nanos(score.bpm)
                                    val duration = occurrence.notes.fold(Beat(0)) { sum, note -> sum + note.duration }.nanos(score.bpm)
                                    occurrence.index in owners && tap.relativeNanos >= start && tap.relativeNanos < start + duration
                                }
                            }
                            Text("Adelantados ${attacks.count { it.kind == TapKind.EARLY }} · Atrasados ${attacks.count { it.kind == TapKind.LATE }} · Omitidos ${attacks.count { it.kind == TapKind.OMITTED }} · Extras $extras",
                                style = MaterialTheme.typography.bodySmall)
                            val silences = result.silences.filter { it.occurrenceIndex in owners }
                            if (silences.isNotEmpty()) Text("Silencios respetados ${silences.count { it.respected == true }} / ${silences.size} · Pendientes ${silences.count { it.respected == null }}",
                                style = MaterialTheme.typography.bodySmall)
                        }
                        Text("Cada golpe conserva su desvío con signo; los errores no detienen el ejercicio.", color = Muted)
                        result.attacks.forEach { attack ->
                            Text("Golpe ${attack.index + 1}: ${attack.kind?.let(::kindLabel) ?: "Sin evaluar (interrupción)"}" + (attack.deltaNanos?.let {
                                String.format(Locale.ROOT, " · %+.1f ms", it / 1_000_000.0)
                            } ?: ""), style = MaterialTheme.typography.bodySmall)
                        }
                        result.taps.filter { it.kind == TapKind.EXTRA || it.kind == TapKind.PREPARATION }.forEach { tap ->
                            Text("${kindLabel(tap.kind)} · ${tap.relativeNanos / 1_000_000} ms", style = MaterialTheme.typography.bodySmall)
                        }
                        Text("El resumen se pierde al cerrar el proceso. No hay DDA ni desbloqueos en esta entrega.", color = Muted)
                        OutlinedButton(onClick = model::repeat, modifier = Modifier.testTag("rhythm-repeat")) { Text("Repetir mismo patrón") }
                    }
                    Spacer(Modifier.height(12.dp))
                }
                if (state.phase == RhythmPhase.PREVIEW) {
                    Button(onClick = model::play, enabled = !ui.busy, modifier = Modifier.fillMaxWidth().testTag("rhythm-play")) { Text("Play · Conteo y ejecución") }
                }
                // Keep the original pointer listener alive through RESULTS for late delivery of
                // a touch that occurred before the deadline. New result-screen taps are rejected.
                if (active || state.phase == RhythmPhase.RESULTS) {
                    Text(if (active) kindLabel(state.feedback) else "Finalizado", color = Pink, modifier = Modifier.testTag("rhythm-feedback"))
                    Box(Modifier.fillMaxWidth().height(100.dp).background(Panel, RoundedCornerShape(16.dp))
                        .testTag("rhythm-tap").semantics {
                            contentDescription = "Registrar un tap rítmico"
                            if (active) onClick { model.tapNow(); true } else disabled()
                        }.pointerInput(model) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    event.changes.filter { it.pressed && !it.previousPressed }.forEach { down ->
                                        model.tapAtUptimeMillis(down.uptimeMillis)
                                        down.consume()
                                    }
                                }
                            }
                        }, contentAlignment = Alignment.Center) {
                        Text(when (state.phase) {
                            RhythmPhase.COUNT_IN -> "Escuchá el conteo"
                            RhythmPhase.RESULTS -> "Finalizado"
                            else -> "TAP"
                        }, style = MaterialTheme.typography.headlineMedium)
                    }
                    if (active) TextButton(onClick = model::background, modifier = Modifier.testTag("rhythm-interrupt")) { Text("Interrumpir ejercicio") }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

private fun kindLabel(kind: TapKind?): String = when (kind) {
    TapKind.ON_TIME -> "A tiempo"
    TapKind.EARLY -> "Adelantado"
    TapKind.LATE -> "Atrasado"
    TapKind.EXTRA -> "Golpe extra"
    TapKind.OMITTED -> "Omitido"
    TapKind.PREPARATION -> "Tap durante el conteo"
    null -> "Seguí el pulso"
}

@Composable
private fun RhythmBarDrawing(bar: RhythmBar, score: RhythmScore, state: RhythmState, modifier: Modifier) {
    val description = "Compás ${bar.index + 1}, ${score.beatsPerBar}/4: " + bar.occurrences.joinToString("; ") { it.category.label }
    Canvas(modifier.testTag("rhythm-bar-${bar.index}").semantics { contentDescription = description }) {
        drawRect(Panel)
        val left = 16.dp.toPx(); val right = size.width - 16.dp.toPx()
        val y = size.height * .68f
        drawLine(Muted, Offset(left, y), Offset(right, y), 1.dp.toPx())
        drawLine(SoftWhite, Offset(left, y - 28.dp.toPx()), Offset(left, y + 10.dp.toPx()), 2.dp.toPx())
        drawLine(SoftWhite, Offset(right, y - 28.dp.toPx()), Offset(right, y + 10.dp.toPx()), 2.dp.toPx())
        val beatWidth = (right - left - 22.dp.toPx()) / score.beatsPerBar
        bar.occurrences.forEach { occurrence ->
            val x = left + 14.dp.toPx() + occurrence.beat.asDouble().toFloat() * beatWidth
            val color = if (occurrence.target) SoftWhite else Muted
            val top = y - 32.dp.toPx()
            var noteBeat = 0f
            occurrence.notes.forEach { note ->
                val nx = if (occurrence.category == RhythmCategory.BAR_REST) (left + right) / 2 else x + noteBeat * beatWidth
                noteBeat += note.duration.asDouble().toFloat()
                if (note.rest) {
                    when {
                        occurrence.category == RhythmCategory.BAR_REST || note.duration.asDouble() >= 2 -> {
                            val line = y - 20.dp.toPx()
                            drawLine(color, Offset(nx - 10.dp.toPx(), line), Offset(nx + 10.dp.toPx(), line), 1.dp.toPx())
                            val hangs = occurrence.category == RhythmCategory.BAR_REST
                            drawRect(color, Offset(nx - 6.dp.toPx(), if (hangs) line else line - 5.dp.toPx()), Size(12.dp.toPx(), 5.dp.toPx()))
                        }
                        note.duration == Beat(1, 2) -> {
                            drawCircle(color, 3.dp.toPx(), Offset(nx - 2.dp.toPx(), y - 17.dp.toPx()))
                            drawLine(color, Offset(nx + 5.dp.toPx(), y - 22.dp.toPx()), Offset(nx - 3.dp.toPx(), y + 5.dp.toPx()), 2.dp.toPx())
                        }
                        else -> {
                            val rest = Path().apply {
                                moveTo(nx - 3.dp.toPx(), y - 30.dp.toPx())
                                lineTo(nx + 5.dp.toPx(), y - 21.dp.toPx())
                                lineTo(nx - 4.dp.toPx(), y - 13.dp.toPx())
                                lineTo(nx + 4.dp.toPx(), y - 5.dp.toPx())
                                cubicTo(nx - 7.dp.toPx(), y - 8.dp.toPx(), nx - 7.dp.toPx(), y + 2.dp.toPx(), nx, y + 5.dp.toPx())
                            }
                            drawPath(rest, color, style = Stroke(2.5.dp.toPx()))
                        }
                    }
                } else {
                    val hollow = note.duration.asDouble() >= 2
                    val head = Offset(nx - 5.dp.toPx(), y - 3.dp.toPx())
                    val headSize = Size(if (note.duration == Beat(4)) 13.dp.toPx() else 10.dp.toPx(), 7.dp.toPx())
                    if (hollow) drawOval(color, head, headSize, style = Stroke(1.8.dp.toPx()))
                    else drawOval(color, head, headSize)
                    if (note.duration != Beat(4)) drawLine(color, Offset(nx + 4.dp.toPx(), y), Offset(nx + 4.dp.toPx(), top), 1.6.dp.toPx())
                    if (note.duration == Beat(3, 2)) drawCircle(color, 1.8.dp.toPx(), Offset(nx + 12.dp.toPx(), y - 3.dp.toPx()))
                    if (note.duration == Beat(1, 2) && occurrence.category != RhythmCategory.EIGHTHS) {
                        val flag = Path().apply {
                            moveTo(nx + 4.dp.toPx(), top)
                            cubicTo(nx + 20.dp.toPx(), top + 9.dp.toPx(), nx + 13.dp.toPx(), top + 14.dp.toPx(), nx + 10.dp.toPx(), top + 19.dp.toPx())
                        }
                        drawPath(flag, color, style = Stroke(2.dp.toPx()))
                    }
                }
            }
            if (occurrence.category == RhythmCategory.EIGHTHS) {
                drawLine(color, Offset(x + 4.dp.toPx(), top), Offset(x + beatWidth / 2 + 4.dp.toPx(), top), 4.dp.toPx())
            }
        }
        if (state.phase == RhythmPhase.PLAYING) {
            val beat = state.elapsedNanos.toDouble() / Beat(1).nanos(score.bpm) - bar.index * score.beatsPerBar
            if (beat >= 0 && beat < score.beatsPerBar) {
                val x = left + 14.dp.toPx() + beat.toFloat() * beatWidth
                drawLine(Pink, Offset(x, 4.dp.toPx()), Offset(x, size.height - 4.dp.toPx()), 2.dp.toPx())
            }
        }
    }
}
