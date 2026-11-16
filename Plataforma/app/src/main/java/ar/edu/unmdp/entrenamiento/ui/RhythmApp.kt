package ar.edu.unmdp.entrenamiento.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import ar.edu.unmdp.entrenamiento.rhythm.*
import ar.edu.unmdp.entrenamiento.BuildConfig

@Composable
fun RhythmApp(ui: RhythmUiState, model: RhythmViewModel, reduceMotion: Boolean = false, onData: (() -> Unit)? = null, onHome: () -> Unit) {
    if (ui.calibrationOpen) { RhythmCalibrationApp(ui, model); return }
    if (ui.progressOpen) { RhythmProgressApp(ui, model); return }
    if (ui.historyOpen) { RhythmHistoryApp(ui, model, onData); return }
    val state = ui.session
    val active = state.phase == RhythmPhase.COUNT_IN || state.phase == RhythmPhase.PLAYING
    val score = state.score
    var editCategories by rememberSaveable { mutableStateOf(false) }
    var showSettings by rememberSaveable { mutableStateOf(true) }
    val quiet = BuildConfig.QUIET_STUDIO
    LaunchedEffect(state.phase) {
        if (state.phase == RhythmPhase.PREVIEW || state.phase == RhythmPhase.RESULTS) {
            editCategories = false
            showSettings = false
        }
    }
    val scroll = rememberScrollState()
    var scoreTop by remember { mutableIntStateOf(0) }
    var notationReady by remember { mutableStateOf(false) }
    val systemReduced = systemReducesMotion()
    val reduced = reduceMotion || systemReduced
    val meter = score?.beatsPerBar ?: ui.beatsPerBar
    val bar = score?.let { (state.elapsedNanos.coerceAtLeast(0) / Beat(it.beatsPerBar).nanos(it.bpm)).toInt().coerceIn(0, 3) } ?: 0
    LaunchedEffect(active, scoreTop) { if (active) scroll.scrollTo(scoreTop) }
    RhythmTheme {
        BackHandler(onBack = onHome)
        RhythmPageScaffold { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = RhythmTokens.SpaceBase),
                verticalArrangement = Arrangement.spacedBy(RhythmTokens.SpaceSm)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onHome, modifier = Modifier.heightIn(min = RhythmTokens.TouchMinimum)) { Text(if (quiet) "Menú" else "Libro") }
                    Text(if (quiet) "Encontrá tu pulso" else "Rhythm Tap", style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f))
                }
                Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(scroll).testTag("rhythm-scroll"),
                    verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val mode = if ((score != null && state.origin == "progression") || (score == null && ui.progression)) "Progresión" else "Práctica"
                    val tempo = score?.bpm ?: if (ui.progression) ui.history.progress.tempo(ui.selected) else ui.practiceBpm
                    Text("$mode · 4 compases · $meter/4 · $tempo BPM", color = RhythmTokens.TextSecondary)
                    if (!active) RhythmSaveStatus(ui, model, onData)
                    if (!active && state.phase != RhythmPhase.SETUP) TextButton(onClick = { showSettings = !showSettings },
                        modifier = Modifier.heightIn(min = RhythmTokens.TouchMinimum).testTag("rhythm-settings")) {
                        Text(if (showSettings) "Cerrar preparación" else "Preparación y calibración")
                    }
                    if (!active) {
                      if (state.phase == RhythmPhase.SETUP || showSettings) {
                        if (!quiet && state.phase == RhythmPhase.SETUP) {
                            Text("Tu práctica en la terraza", style = MaterialTheme.typography.headlineSmall)
                            Text("Elegí qué practicar, revisá la partitura y marcá el ritmo. El conteo te acompaña antes de empezar.", color = RhythmTokens.TextSecondary)
                        }
                        Text("Antes de tocar: calibración", style = MaterialTheme.typography.titleMedium)
                        Text(ui.calibration?.let { "Ajuste de toque: ${it.offsetNanos / 1_000_000} ms. Recalibrá si cambiás la salida de audio." }
                            ?: "Sin calibrar · ajuste de 0 ms. Calibrá si los golpes se registran adelantados o atrasados.", color = RhythmTokens.TextSecondary)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(RhythmTokens.SpaceSm)) {
                            OutlinedButton(onClick = model::openCalibration, enabled = !ui.busy,
                                modifier = Modifier.heightIn(min = RhythmTokens.TouchMinimum).testTag("rhythm-calibration-open")) { Text("Calibrar antes de jugar") }
                            if (ui.calibration != null) TextButton(onClick = model::clearCalibration, enabled = !ui.busy,
                                modifier = Modifier.heightIn(min = RhythmTokens.TouchMinimum)) { Text("Quitar ajuste") }
                        }
                        RhythmModeControls(ui, model)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(RhythmTokens.SpaceSm)) {
                            (2..4).forEach { beats ->
                                FilterChip(selected = ui.beatsPerBar == beats, enabled = !ui.busy,
                                    onClick = { model.meter(beats) }, modifier = Modifier.testTag("rhythm-meter-$beats"),
                                    label = { Text("$beats/4") })
                            }
                        }
                        if (state.phase != RhythmPhase.SETUP) TextButton(onClick = { editCategories = !editCategories }, modifier = Modifier.heightIn(min = RhythmTokens.TouchMinimum).testTag("rhythm-edit-categories")) {
                            Text(if (editCategories) "Cerrar selección" else "Cambiar células (${ui.selected.size})")
                        }
                        if (state.phase == RhythmPhase.SETUP || editCategories) Column(verticalArrangement = Arrangement.spacedBy(RhythmTokens.SpaceXs)) {
                            Text("Elegí las células que querés practicar")
                            RhythmCategory.entries.forEach { category ->
                                val locked = ui.progression && category !in ui.history.progress.unlocked
                                FilterChip(selected = category in ui.selected, enabled = !ui.busy && !locked,
                                    onClick = { model.select(category) },
                                    label = { Text(category.label + if (locked) " · Bloqueada" else "") },
                                    modifier = Modifier.testTag("rhythm-category-${category.code}").heightIn(min = RhythmTokens.TouchMinimum),
                                    shape = RoundedCornerShape(RhythmTokens.RadiusLarge))
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
                            modifier = Modifier.heightIn(min = RhythmTokens.TouchMinimum).testTag("rhythm-generate")) { Text("Generar ejercicio") }
                      }
                    }
                    ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    Text(when (state.phase) {
                        RhythmPhase.SETUP -> "Generá y revisá antes de tocar Play"
                        RhythmPhase.PREVIEW -> "Vista previa · No suena el patrón objetivo"
                        RhythmPhase.COUNT_IN -> state.countInBeat?.let { "Conteo · $it de $meter" } ?: "Prepará el conteo…"
                        RhythmPhase.PLAYING -> "Tocá el ritmo · Compás ${bar + 1} de 4"
                        RhythmPhase.RESULTS -> if (state.result?.completed == true) "Ejercicio completado" else "Ejercicio interrumpido"
                    }, modifier = Modifier.testTag("rhythm-status"), fontWeight = FontWeight.SemiBold)
                    if (ui.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
                    if (score != null) Surface(color = RhythmTokens.Surface,
                        shape = RoundedCornerShape(RhythmTokens.RadiusLarge), modifier = Modifier.fillMaxWidth().onGloballyPositioned {
                        val top = it.positionInParent().y.toInt()
                        if (scoreTop != top) scoreTop = top
                    }) {
                      Column {
                        if (!quiet && state.phase != RhythmPhase.RESULTS && (!active || LocalDensity.current.fontScale <= 1.3f))
                            RhythmGardenTerrace(Modifier.fillMaxWidth().height(80.dp).testTag("rhythm-terrace"))
                        RhythmScoreView(score, state, reduced, Modifier.fillMaxWidth().padding(RhythmTokens.SpaceSm)) { notationReady = it }
                      }
                    }
                    state.result?.let { result ->
                        GardenPaperPanel(color = RhythmTokens.Surface) { RhythmResultSummary(result) }
                        Text("Podés consultar este ejercicio y repetirlo desde el historial.", color = RhythmTokens.TextSecondary)
                        Button(onClick = { model.generate() }, enabled = !ui.busy && ui.selected.isNotEmpty(),
                            modifier = Modifier.heightIn(min = RhythmTokens.TouchMinimum).testTag("rhythm-new-exercise")) { Text("Nuevo ejercicio") }
                        OutlinedButton(onClick = model::repeat, modifier = Modifier.heightIn(min = RhythmTokens.TouchMinimum).testTag("rhythm-repeat")) { Text("Repetir mismo patrón") }
                    }
                    if (!active) FlowRow(horizontalArrangement = Arrangement.spacedBy(RhythmTokens.SpaceSm)) {
                        OutlinedButton(onClick = model::openHistory, enabled = !ui.busy,
                            modifier = Modifier.heightIn(min = RhythmTokens.TouchMinimum).testTag("rhythm-open-history")) { Text("Historial de ritmo") }
                        OutlinedButton(onClick = model::openProgress, enabled = !ui.busy,
                            modifier = Modifier.heightIn(min = RhythmTokens.TouchMinimum).testTag("rhythm-progress-open")) { Text("Progreso y DDA") }
                    }
                    Spacer(Modifier.height(12.dp))
                }
                if (state.phase == RhythmPhase.PREVIEW) {
                    Button(onClick = model::play, enabled = !ui.busy && notationReady, modifier = Modifier.fillMaxWidth().testTag("rhythm-play")) { Text("Play · Conteo y ejecución") }
                }
                // Keep the original pointer listener alive through RESULTS for late delivery of
                // a touch that occurred before the deadline. New result-screen taps are rejected.
                if (active || state.phase == RhythmPhase.RESULTS) {
                    Text(if (active) rhythmKindLabel(state.feedback) else "Finalizado", color = RhythmTokens.Primary, modifier = Modifier.testTag("rhythm-feedback"))
                    RhythmTapPad(when (state.phase) {
                            RhythmPhase.COUNT_IN -> "Escuchá el conteo"
                            RhythmPhase.RESULTS -> "Finalizado"
                            else -> "TAP"
                        }, active, model::tapAtUptimeMillis, model::tapNow)
                    if (active) TextButton(onClick = model::background, modifier = Modifier.heightIn(min = RhythmTokens.TouchMinimum).testTag("rhythm-interrupt")) { Text("Interrumpir ejercicio") }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

internal fun rhythmKindLabel(kind: TapKind?): String = when (kind) {
    TapKind.ON_TIME -> "A tiempo"
    TapKind.EARLY -> "Adelantado"
    TapKind.LATE -> "Atrasado"
    TapKind.EXTRA -> "Golpe extra"
    TapKind.OMITTED -> "Omitido"
    TapKind.PREPARATION -> "Tap durante el conteo"
    null -> "Seguí el pulso"
}

@Composable
internal fun RhythmBarDrawing(bar: RhythmBar, score: RhythmScore, state: RhythmState, modifier: Modifier) {
    val description = "Compás ${bar.index + 1}, ${score.beatsPerBar}/4: " + bar.occurrences.joinToString("; ") { it.category.label }
    val textMeasurer = rememberTextMeasurer()
    val feedback = remember(score, state.attacks, state.result) {
        score.toNotation().feedback(state).associate { it.id to it.kind }
    }
    // El número es parte de la notación y mantiene la proporción con las plicas en dp.
    val tripletLabel = textMeasurer.measure("3", TextStyle(fontFamily = gardenBody,
        fontWeight = FontWeight.SemiBold, fontSize = with(LocalDensity.current) { 12.dp.toSp() }))
    Canvas(modifier.testTag("rhythm-bar-${bar.index}").semantics { contentDescription = description }) {
        val left = 16.dp.toPx(); val right = size.width - 16.dp.toPx()
        val y = size.height * .68f
        drawLine(RhythmTokens.Border, Offset(left, y), Offset(right, y), 1.dp.toPx())
        drawLine(RhythmTokens.Text, Offset(left, y - 28.dp.toPx()), Offset(left, y + 10.dp.toPx()), 2.dp.toPx())
        drawLine(RhythmTokens.Text, Offset(right, y - 28.dp.toPx()), Offset(right, y + 10.dp.toPx()), 2.dp.toPx())
        val beatWidth = (right - left - 22.dp.toPx()) / score.beatsPerBar
        bar.occurrences.forEach { occurrence ->
            val x = left + 14.dp.toPx() + occurrence.beat.asDouble().toFloat() * beatWidth
            val color = if (occurrence.target) RhythmTokens.Text else RhythmTokens.TextSecondary
            val top = y - 32.dp.toPx()
            var noteBeat = 0f
            val positions = occurrence.notes.map { note ->
                val nx = if (occurrence.category == RhythmCategory.BAR_REST) (left + right) / 2 else x + noteBeat * beatWidth
                noteBeat += note.duration.asDouble().toFloat()
                nx
            }
            fun beams(note: RhythmNote) = when {
                note.rest -> 0
                note.tuplet == 3 && note.duration == Beat(1, 3) -> 1
                note.duration == Beat(1, 4) -> 2
                note.duration == Beat(1, 2) || note.duration == Beat(3, 4) -> 1
                else -> 0
            }
            val groups = mutableListOf<List<Int>>()
            var group = mutableListOf<Int>()
            for (index in occurrence.notes.indices) {
                if (beams(occurrence.notes[index]) == 0) {
                    if (group.isNotEmpty()) groups += group.toList()
                    group = mutableListOf()
                } else group += index
            }
            if (group.isNotEmpty()) groups += group.toList()
            val connected = groups.filter { it.size >= 2 }.flatten().toSet()
            occurrence.notes.forEachIndexed { index, note ->
                val nx = positions[index]
                if (note.rest) {
                    when {
                        occurrence.category == RhythmCategory.BAR_REST || note.duration.asDouble() >= 2 -> {
                            val line = y - 20.dp.toPx()
                            drawLine(color, Offset(nx - 10.dp.toPx(), line), Offset(nx + 10.dp.toPx(), line), 1.dp.toPx())
                            val hangs = occurrence.category == RhythmCategory.BAR_REST
                            drawRect(color, Offset(nx - 6.dp.toPx(), if (hangs) line else line - 5.dp.toPx()), Size(12.dp.toPx(), 5.dp.toPx()))
                        }
                        note.duration == Beat(1, 2) || note.duration == Beat(1, 4) -> {
                            val flags = if (note.duration == Beat(1, 4)) 2 else 1
                            val stemTop = if (flags == 2) 29f else 22f
                            drawLine(color, Offset(nx + 6.dp.toPx(), y - stemTop.dp.toPx()), Offset(nx - 2.dp.toPx(), y + 4.dp.toPx()), 1.8.dp.toPx())
                            repeat(flags) { flag ->
                                val cy = y - (if (flags == 2) 22f - flag * 10 else 15f).dp.toPx()
                                val tipY = cy - 4.dp.toPx()
                                val tipX = nx + (6 - 8 * (stemTop - (y - tipY) / density) / (stemTop + 4)).dp.toPx()
                                val cx = tipX - 8.dp.toPx()
                                drawCircle(color, 2.6.dp.toPx(), Offset(cx, cy))
                                val hook = Path().apply {
                                    moveTo(cx, cy)
                                    cubicTo(cx + 3.dp.toPx(), cy + 5.dp.toPx(), tipX - 2.dp.toPx(), tipY + 7.dp.toPx(), tipX, tipY)
                                }
                                drawPath(hook, color, style = Stroke(1.6.dp.toPx()))
                            }
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
                    val headColor = when (feedback["${occurrence.index}:$index"]) {
                        "ON_TIME" -> RhythmTokens.Primary
                        "EARLY", "LATE", "OMITTED" -> RhythmTokens.ErrorText
                        else -> color
                    }
                    val hollow = note.duration.asDouble() >= 2
                    val head = Offset(nx - 5.dp.toPx(), y - 3.dp.toPx())
                    val headSize = Size(if (note.duration == Beat(4)) 13.dp.toPx() else 10.dp.toPx(), 7.dp.toPx())
                    if (hollow) drawOval(headColor, head, headSize, style = Stroke(1.8.dp.toPx()))
                    else drawOval(headColor, head, headSize)
                    if (note.duration != Beat(4)) drawLine(color, Offset(nx + 4.dp.toPx(), y), Offset(nx + 4.dp.toPx(), top), 1.6.dp.toPx())
                    if (note.duration == Beat(3, 2) || note.duration == Beat(3, 4)) drawCircle(color, 1.8.dp.toPx(), Offset(nx + 12.dp.toPx(), y - 3.dp.toPx()))
                    if (beams(note) > 0 && index !in connected) repeat(beams(note)) { level ->
                        val flagTop = top + level * 7.dp.toPx()
                        val flag = Path().apply {
                            moveTo(nx + 4.dp.toPx(), flagTop)
                            cubicTo(nx + 20.dp.toPx(), flagTop + 9.dp.toPx(), nx + 13.dp.toPx(), flagTop + 14.dp.toPx(), nx + 10.dp.toPx(), flagTop + 19.dp.toPx())
                        }
                        drawPath(flag, color, style = Stroke(2.dp.toPx()))
                    }
                }
            }
            groups.filter { it.size >= 2 }.forEach { notes ->
                fun stem(index: Int) = positions[index] + 4.dp.toPx()
                drawLine(color, Offset(stem(notes.first()), top), Offset(stem(notes.last()), top), 4.dp.toPx())
                notes.zipWithNext().forEach { (first, second) ->
                    if (beams(occurrence.notes[first]) == 2 && beams(occurrence.notes[second]) == 2)
                        drawLine(color, Offset(stem(first), top + 7.dp.toPx()), Offset(stem(second), top + 7.dp.toPx()), 3.dp.toPx())
                }
                notes.forEachIndexed { position, index ->
                    if (beams(occurrence.notes[index]) == 2 &&
                        notes.getOrNull(position - 1)?.let { beams(occurrence.notes[it]) == 2 } != true &&
                        notes.getOrNull(position + 1)?.let { beams(occurrence.notes[it]) == 2 } != true) {
                        val next = notes.getOrNull(position + 1)
                        val neighbor = next ?: notes[position - 1]
                        val length = minOf(7.dp.toPx(), kotlin.math.abs(stem(neighbor) - stem(index)) * .45f)
                        drawLine(color, Offset(stem(index), top + 7.dp.toPx()), Offset(stem(index) + if (next != null) length else -length, top + 7.dp.toPx()), 3.dp.toPx())
                    }
                }
            }
            if (occurrence.notes.size == 3 && occurrence.notes.all { it.tuplet == 3 && it.duration == Beat(1, 3) }) {
                val first = positions.first() - 2.dp.toPx()
                val last = positions.last() + 8.dp.toPx()
                val center = (first + last) / 2
                val bracketY = top - 11.dp.toPx()
                val gap = tripletLabel.size.width / 2f + 3.dp.toPx()
                drawLine(color, Offset(first, bracketY + 4.dp.toPx()), Offset(first, bracketY), 1.dp.toPx())
                drawLine(color, Offset(first, bracketY), Offset(center - gap, bracketY), 1.dp.toPx())
                drawLine(color, Offset(center + gap, bracketY), Offset(last, bracketY), 1.dp.toPx())
                drawLine(color, Offset(last, bracketY), Offset(last, bracketY + 4.dp.toPx()), 1.dp.toPx())
                drawText(tripletLabel, color = color,
                    topLeft = Offset(center - tripletLabel.size.width / 2f, bracketY - tripletLabel.size.height / 2f))
            }
            occurrence.notes.forEachIndexed { index, note ->
                if (note.tieToNext && index + 1 < positions.size) {
                    val start = positions[index] + 3.dp.toPx()
                    val end = positions[index + 1] - 3.dp.toPx()
                    val tie = Path().apply {
                        moveTo(start, y + 8.dp.toPx())
                        cubicTo(start + (end - start) / 3, y + 22.dp.toPx(),
                            end - (end - start) / 3, y + 22.dp.toPx(), end, y + 8.dp.toPx())
                    }
                    drawPath(tie, color, style = Stroke(1.6.dp.toPx()))
                }
            }
        }
        if (state.phase == RhythmPhase.PLAYING) {
            val beat = state.elapsedNanos.toDouble() / Beat(1).nanos(score.bpm) - bar.index * score.beatsPerBar
            if (beat >= 0 && beat < score.beatsPerBar) {
                val x = left + 14.dp.toPx() + beat.toFloat() * beatWidth
                drawLine(RhythmTokens.Focus, Offset(x, 4.dp.toPx()), Offset(x, size.height - 4.dp.toPx()), 2.dp.toPx())
            }
        }
    }
}
