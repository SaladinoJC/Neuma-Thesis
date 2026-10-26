package ar.edu.unmdp.entrenamiento.ui

import androidx.activity.compose.BackHandler
import android.os.SystemClock
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.unmdp.entrenamiento.defender.*

private val DefenderCyan = Color(0xFF55D9FF)
private val DefenderSpace = Color(0xFF070D25)

@Composable
fun DefenderApp(ui: DefenderUiState, model: DefenderViewModel, onHome: () -> Unit) {
    MaterialTheme(colorScheme = darkColorScheme(primary = DefenderCyan, secondary = Pink,
        background = DefenderSpace, surface = Panel, onSurface = SoftWhite, onBackground = SoftWhite)) {
        BackHandler(onBack = onHome)
        Scaffold(containerColor = DefenderSpace, contentWindowInsets = WindowInsets.safeDrawing) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 12.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onHome) { Text("‹ Menú") }
                    Text("DEFENDER", modifier = Modifier.weight(1f), fontWeight = FontWeight.Black,
                        color = DefenderCyan, letterSpacing = 2.sp)
                    if (ui.game.phase == DefenderPhase.RUNNING)
                        TextButton(onClick = model::pause, modifier = Modifier.testTag("defender-pause")) { Text("Pausa") }
                }
                when (ui.game.phase) {
                    DefenderPhase.SETUP -> DefenderSetup(ui, model)
                    DefenderPhase.FINISHED, DefenderPhase.INTERRUPTED -> DefenderSummary(ui, model)
                    else -> DefenderGame(ui, model)
                }
            }
        }
    }
}

@Composable
private fun DefenderSetup(ui: DefenderUiState, model: DefenderViewModel) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Defendé la base", style = MaterialTheme.typography.headlineLarge)
        Text("Leé la nota, elegí su nombre y dispará antes de que llegue.")
        NoteStaff(DefenderNote(DefenderName.MI, 4), Modifier.fillMaxWidth().height(120.dp))
        Text("Práctica · elegí qué leer", color = DefenderCyan)
        DefenderStage.entries.forEach { stage ->
            FilterChip(selected = ui.stage == stage, onClick = { model.stage(stage) },
                label = { Text(stage.label) }, modifier = Modifier.testTag("defender-stage-${stage.name}"))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Incluir ♭ y ♯", modifier = Modifier.weight(1f))
            Switch(checked = ui.accidentals, onCheckedChange = model::accidentals,
                modifier = Modifier.testTag("defender-accidentals"))
        }
        Text("Enemigos simultáneos (si hay espacio)")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..4).forEach { count ->
                FilterChip(selected = ui.maxEnemies == count, onClick = { model.maxEnemies(count) },
                    label = { Text("$count") }, modifier = Modifier.testTag("defender-count-$count"))
            }
        }
        Text("Viaje 10 s · tres vidas\nSolo las llegadas quitan vida. Cada acierto suma 100 puntos.")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(selected = !ui.letterNames, onClick = { model.letterNames(false) }, label = { Text("Do–Si") })
            FilterChip(selected = ui.letterNames, onClick = { model.letterNames(true) }, label = { Text("C–B") })
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Sonido de nota al acertar", modifier = Modifier.weight(1f))
            Switch(checked = ui.noteSound, onCheckedChange = model::noteSound, modifier = Modifier.testTag("defender-note-sound"))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Efectos de juego", modifier = Modifier.weight(1f))
            Switch(checked = ui.effectsSound, onCheckedChange = model::effectsSound, modifier = Modifier.testTag("defender-effects-sound"))
        }
        Button(onClick = { model.start() }, modifier = Modifier.fillMaxWidth().height(56.dp).testTag("defender-start")) { Text("Jugar Defender") }
        Text("Esta práctica no modifica tu progreso. Podés revisar el resumen al terminar.", color = Muted)
    }
}

@Composable
private fun ColumnScope.DefenderGame(ui: DefenderUiState, model: DefenderViewModel) {
    val game = ui.game
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(game.configuration.stage.label, color = DefenderCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text("♥ ${game.lives}", color = Pink, modifier = Modifier.testTag("defender-lives"))
        Text("${game.points} pts", fontWeight = FontWeight.Bold, modifier = Modifier.testTag("defender-score"))
    }
    if (game.phase == DefenderPhase.PAUSED) {
        Box(Modifier.weight(1f).fillMaxWidth().testTag("defender-paused"), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Text("En pausa", style = MaterialTheme.typography.headlineLarge)
                Text("Tiempo y audio detenidos", color = Muted)
                Button(onClick = model::resume, modifier = Modifier.testTag("defender-resume")) { Text("Reanudar") }
            }
        }
        return
    }
    DefenderArena(game, Modifier.weight(1f).fillMaxWidth())
    // Fixed bottom controls remain reachable on a narrow portrait display.
    Surface(color = Panel, border = BorderStroke(1.dp, DefenderCyan.copy(alpha = .4f)), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(game.feedback ?: "Seleccioná la nota", color = if (game.feedback == null) Muted else Pink,
                fontSize = 13.sp, modifier = Modifier.testTag("defender-feedback"))
            DefenderName.entries.toList().chunked(4).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    row.forEach { name ->
                        val selected = game.selected == name
                        OutlinedButton(onClick = { model.select(name) },
                            modifier = Modifier.weight(1f).height(48.dp).testTag("defender-name-${name.name}"),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) DefenderCyan.copy(alpha = .25f) else Color.Transparent),
                            border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) DefenderCyan else Muted)) {
                            Text(if (ui.letterNames) name.letter else name.solfege, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (row.size < 4) Spacer(Modifier.weight(1f))
                }
            }
            if (game.configuration.accidentals) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DefenderAccidental.entries.forEach { accidental ->
                        FilterChip(selected = game.selectedAccidental == accidental,
                            onClick = { model.accidental(accidental) }, label = { Text(accidental.symbol) },
                            modifier = Modifier.weight(1f).testTag("defender-accidental-${accidental.name}"))
                    }
                }
            }
        }
    }
    Button(onClick = model::fire, enabled = game.selected != null,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).height(52.dp).testTag("defender-fire")
            .pointerInput(game.selected, game.selectedAccidental) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                    game.selected?.let { selected ->
                        model.firePointer(down.uptimeMillis, SystemClock.uptimeMillis(), selected, game.selectedAccidental)
                        down.consume()
                    }
                    waitForUpOrCancellation(pass = PointerEventPass.Initial)?.consume()
                }
            }) {
        Text("DISPARAR", fontWeight = FontWeight.Black, letterSpacing = 2.sp)
    }
    ui.error?.let { Text(it, color = Pink, fontSize = 12.sp) }
}

@Composable
private fun DefenderArena(game: DefenderState, modifier: Modifier) {
    BoxWithConstraints(modifier.testTag("defender-arena")) {
        Canvas(Modifier.fillMaxSize()) {
            repeat(58) { index ->
                val x = ((index * 37 + 11) % 101) / 100f * size.width
                val y = ((index * 61 + 7) % 103) / 103f * size.height
                drawRect(if (index % 5 == 0) Pink.copy(alpha = .7f) else DefenderCyan.copy(alpha = .4f),
                    Offset(x, y), Size(if (index % 7 == 0) 4.dp.toPx() else 1.dp.toPx(), 2.dp.toPx()))
            }
            val base = Offset(size.width * .5f, size.height * .94f)
            if (game.configuration.maxEnemies > 1) drawLine(DefenderCyan.copy(alpha = .6f),
                Offset(size.width * .25f, size.height * .84f), Offset(size.width * .75f, size.height * .84f), 2.dp.toPx())
            drawArc(DefenderCyan.copy(alpha = .4f), 180f, 180f, false,
                Offset(size.width * .16f, size.height * .83f), Size(size.width * .68f, size.height * .34f), style = Stroke(2.dp.toPx()))
            val ship = Path().apply {
                moveTo(base.x, base.y - 28.dp.toPx()); lineTo(base.x + 23.dp.toPx(), base.y + 8.dp.toPx())
                lineTo(base.x + 7.dp.toPx(), base.y + 3.dp.toPx()); lineTo(base.x, base.y - 8.dp.toPx())
                lineTo(base.x - 7.dp.toPx(), base.y + 3.dp.toPx()); lineTo(base.x - 23.dp.toPx(), base.y + 8.dp.toPx()); close()
            }
            drawPath(ship, DefenderCyan)
            drawRect(Pink, Offset(base.x - 4.dp.toPx(), base.y + 10.dp.toPx()), Size(8.dp.toPx(), 12.dp.toPx()))
            game.results.lastOrNull()?.takeIf { it.resolution == DefenderResolution.ELIMINATED && game.activeMillis - it.atMillis in 0..220 }?.let {
                val p = it.position
                drawLine(DefenderCyan, base, Offset(p.x * size.width, p.y * size.height), 3.dp.toPx())
            }
        }
        game.enemies.forEach { enemy ->
            val point = enemy.position(game.activeMillis)
            val cardWidth = if (game.configuration.maxEnemies == 1) minOf(136.dp, maxWidth * .45f) else maxWidth * .235f
            val cardHeight = if (game.configuration.maxEnemies == 1) minOf(116.dp, maxHeight * .35f) else maxHeight * .17f
            Column(Modifier.offset(x = maxWidth * point.x - cardWidth / 2, y = maxHeight * point.y - cardHeight / 2)
                .width(cardWidth).testTag("defender-enemy"), horizontalAlignment = Alignment.CenterHorizontally) {
                NoteStaff(enemy.note, Modifier.fillMaxWidth().height(cardHeight).testTag("defender-target"))
                Text("▟▙", color = Pink, fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
        }
        game.results.lastOrNull()?.takeIf { it.resolution == DefenderResolution.ARRIVED && game.activeMillis - it.atMillis in 0..1200 }?.let { result ->
            Text("${result.enemy.note.name.solfege}${if (result.enemy.note.accidental == DefenderAccidental.NATURAL) "" else result.enemy.note.accidental.symbol}${result.enemy.note.octave}", color = Pink, fontWeight = FontWeight.Black,
                modifier = Modifier.offset(x = maxWidth * result.position.x - 28.dp, y = maxHeight * result.position.y - 22.dp)
                    .background(DefenderSpace).padding(4.dp).testTag("defender-solution"))
        }
    }
}

/** Own vector treble clef: spiral sits on the G4 line (second from the bottom). */
@Composable
fun NoteStaff(note: DefenderNote, modifier: Modifier = Modifier) {
    Canvas(modifier.background(Color(0xFFFFF1D9), RoundedCornerShape(5.dp)).semantics {
        contentDescription = "Pentagrama en clave de ${note.clef.label.lowercase()}"
    }) {
        val sx = size.width / 100f; val sy = size.height / 120f
        val ink = Color(0xFF13223D)
        translate(top = 8 * sy) {
        for (line in 0..4) drawLine(ink, Offset(4 * sx, (28 + line * 12) * sy),
            Offset(96 * sx, (28 + line * 12) * sy), .9f * sy)
        if (note.clef == DefenderClef.TREBLE) {
        val clef = Path().apply {
            moveTo(19*sx, 92*sy)
            cubicTo(32*sx, 94*sy, 28*sx, 79*sy, 25*sx, 69*sy)
            lineTo(16*sx, 24*sy)
            cubicTo(10*sx, -1*sy, 39*sx, 0*sy, 28*sx, 21*sy)
            cubicTo(18*sx, 39*sy, 1*sx, 40*sy, 5*sx, 62*sy)
            cubicTo(10*sx, 80*sy, 36*sx, 77*sy, 35*sx, 58*sy)
            cubicTo(33*sx, 44*sy, 12*sx, 44*sy, 11*sx, 60*sy)
            cubicTo(10*sx, 72*sy, 23*sx, 75*sy, 29*sx, 65*sy)
        }
        drawPath(clef, ink, style = Stroke(2.3f * sy))
        } else {
            val bass = Path().apply {
                moveTo(8*sx, 43*sy); cubicTo(5*sx, 20*sy, 36*sx, 16*sy, 31*sx, 40*sy)
                cubicTo(29*sx, 53*sy, 17*sx, 61*sy, 9*sx, 64*sy)
            }
            drawPath(bass, ink, style = Stroke(3f*sy))
            drawCircle(ink, 2.6f*sy, Offset(9*sx, 40*sy))
            drawCircle(ink, 1.8f*sy, Offset(39*sx, 34*sy))
            drawCircle(ink, 1.8f*sy, Offset(39*sx, 46*sy))
        }
        val ledgerSteps = if (note.staffStep < 0) (-2 downTo note.staffStep step 2).toList()
            else if (note.staffStep > 8) (10..note.staffStep step 2).toList() else emptyList()
        ledgerSteps.forEach { step ->
            val ledgerY = (76 - step * 6) * sy
            drawLine(ink, Offset(61*sx, ledgerY), Offset(85*sx, ledgerY), .9f*sy)
        }
        if (note.accidental != DefenderAccidental.NATURAL) {
            val ay = (76 - note.staffStep * 6) * sy
            val ax = 52*sx
            if (note.accidental == DefenderAccidental.SHARP) {
                for (dx in listOf(-2f, 3f)) drawLine(ink, Offset(ax+dx*sx, ay-10*sy), Offset(ax+dx*sx, ay+9*sy), 1.3f*sx)
                for (dy in listOf(-3f, 3f)) drawLine(ink, Offset(ax-6*sx, ay+dy*sy+2*sy), Offset(ax+7*sx, ay+dy*sy-2*sy), 2f*sy)
            } else {
                val flat = Path().apply {
                    moveTo(ax-3*sx, ay-14*sy); lineTo(ax-3*sx, ay+6*sy)
                    cubicTo(ax+12*sx, ay-4*sy, ax+5*sx, ay-10*sy, ax-3*sx, ay-2*sy)
                }
                drawPath(flat, ink, style = Stroke(1.6f*sx))
            }
        }
        val y = (76 - note.staffStep * 6) * sy
        drawOval(ink, Offset(66 * sx, y - 4.4f * sy), Size(12 * sx, 8.8f * sy))
        if (note.staffStep >= 4) drawLine(ink, Offset(66*sx, y), Offset(66*sx, y + 29*sy), 1.4f*sx)
        else drawLine(ink, Offset(78*sx, y), Offset(78*sx, y - 29*sy), 1.4f*sx)
        }
    }
}

@Composable
private fun DefenderSummary(ui: DefenderUiState, model: DefenderViewModel) {
    val game = ui.game
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(if (game.phase == DefenderPhase.FINISHED) "Base alcanzada" else "Práctica interrumpida",
            style = MaterialTheme.typography.headlineLarge, modifier = Modifier.testTag("defender-summary"))
        Text("${game.points} puntos · ${game.results.count { it.resolution == DefenderResolution.ELIMINATED }} eliminados")
        Text("${game.results.count { it.resolution == DefenderResolution.ARRIVED }} llegadas · ${game.failedShots} disparos fallidos")
        Text(game.configuration.stage.label, color = DefenderCyan, fontWeight = FontWeight.Bold)
        Text(when (game.configuration.stage) {
            DefenderStage.N1 -> "Clave de sol · rango Mi4–Fa5"
            DefenderStage.N2 -> "Clave de sol · rango La3–Do6"
            DefenderStage.N3 -> "Clave de fa · rango Do2–Mi4"
            DefenderStage.N4 -> "Claves de sol y fa · rangos La3–Do6 / Do2–Mi4"
        })
        Text(if (game.configuration.accidentals) "Alteraciones: naturales, bemoles y sostenidos"
            else "Alteraciones: solo naturales")
        Text("Práctica · no modifica tu progreso", color = Muted)
        Text("Semilla ${game.seed} · ${game.configuration.version}", color = Muted, fontSize = 12.sp)
        Button(onClick = { model.start() }, modifier = Modifier.fillMaxWidth()) { Text("Nueva práctica") }
        Text("Revisión", style = MaterialTheme.typography.titleLarge)
        game.results.forEach { result ->
            val note = result.enemy.note
            val symbol = if (note.accidental == DefenderAccidental.NATURAL) "" else note.accidental.symbol
            Surface(color = Panel, shape = RoundedCornerShape(8.dp)) {
                Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    NoteStaff(result.enemy.note, Modifier.width(110.dp).height(84.dp))
                    Column {
                        Text("${note.name.solfege}$symbol${note.octave} · ${note.name.letter}$symbol${note.octave}", fontWeight = FontWeight.Bold)
                        Text("Clave de ${note.clef.label.lowercase()} · ${when (note.accidental) {
                            DefenderAccidental.NATURAL -> "natural"
                            DefenderAccidental.FLAT -> "bemol"
                            DefenderAccidental.SHARP -> "sostenido"
                        }}", color = Muted, fontSize = 12.sp)
                        Text(if (result.resolution == DefenderResolution.ELIMINATED) "Eliminado" else "Llegó a la base", color = Muted)
                    }
                }
            }
        }
    }
}
