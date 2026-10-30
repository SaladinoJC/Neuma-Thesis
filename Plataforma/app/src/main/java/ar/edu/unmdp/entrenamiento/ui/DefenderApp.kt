package ar.edu.unmdp.entrenamiento.ui

import androidx.activity.compose.BackHandler
import android.os.SystemClock
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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

@Composable
fun DefenderApp(ui: DefenderUiState, model: DefenderViewModel, onData: (() -> Unit)? = null,
    reduceMotion: Boolean = false, onHome: () -> Unit) {
    if (ui.inspectorOpen) { DefenderInspectorApp(ui, model); return }
    if (ui.historyOpen) { DefenderHistoryApp(ui, model, onData); return }
    val reduced = reduceMotion || systemReducesMotion()
    TunerGardenTheme {
        BackHandler(onBack = onHome)
        Scaffold(containerColor = GardenTokens.Background, contentWindowInsets = WindowInsets.safeDrawing) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = GardenTokens.SpaceMd)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onHome) { Text("Menú") }
                    Text("Defender", modifier = Modifier.weight(1f), style = MaterialTheme.typography.headlineSmall)
                    if (ui.game.phase == DefenderPhase.RUNNING)
                        TextButton(onClick = model::pause, modifier = Modifier.testTag("defender-pause")) { Text("Pausa") }
                }
                when (ui.game.phase) {
                    DefenderPhase.SETUP -> DefenderSetup(ui, model, onData)
                    DefenderPhase.FINISHED, DefenderPhase.INTERRUPTED -> DefenderSummary(ui, model, onData)
                    else -> DefenderGame(ui, model, reduced)
                }
            }
        }
    }
}

@Composable
private fun DefenderSetup(ui: DefenderUiState, model: DefenderViewModel, onData: (() -> Unit)?) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(vertical = GardenTokens.SpaceBase),
        verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceBase)) {
        Text("Defendé el jardín", style = MaterialTheme.typography.headlineSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
            FilterChip(ui.variant == "notes", { model.variant("notes") }, label = { Text("Notas") },
                modifier = Modifier.testTag("defender-variant-notes").heightIn(min = GardenTokens.TouchMinimum))
            FilterChip(ui.variant == "chords", { model.variant("chords") }, label = { Text("Acordes por nombre") },
                modifier = Modifier.testTag("defender-variant-chords").heightIn(min = GardenTokens.TouchMinimum))
        }
        Text(if (ui.variant == "chords") "Leé el acorde y elegí raíz, alteración y calidad antes de tocar."
            else "Leé la nota, elegí su nombre y tocá antes de que llegue al árbol.")
        DefenderStorageNotice(ui, model, onData)
        DefenderModeControls(ui, model)
        val example = if (ui.variant == "chords") defenderChordPool(ui.chordQuality, ui.chordInversion, ui.chordClefs)
            .let { pool -> pool.firstOrNull { it.root.name == DefenderName.DO && it.root.accidental == DefenderAccidental.NATURAL } ?: pool.firstOrNull() } else null
        NoteStaff(example?.root ?: DefenderNote(DefenderName.MI, 4), Modifier.fillMaxWidth().height(120.dp), example?.notes)
        Text(if (ui.progression) "Progresión · elegí una etapa desbloqueada" else "Práctica · elegí qué leer", color = GardenTokens.Primary)
        if (ui.variant == "chords") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
                DefenderChordClefs.entries.forEach { clefs ->
                    FilterChip(ui.chordClefs == clefs, { model.chordClefs(clefs) }, enabled = ui.canChoose(ui.targetFor(clefs = clefs)),
                        label = { Text(clefs.label) }, modifier = Modifier.testTag("defender-chord-clefs-${clefs.name}").heightIn(min = GardenTokens.TouchMinimum))
                }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
                ui.chordQuality.intervals.indices.forEach { inversion ->
                    FilterChip(ui.chordInversion == inversion, { model.chordInversion(inversion) },
                        enabled = ui.canChoose(ui.chordTarget.copy(inversion = inversion)), label = { Text(defenderInversionLabel(inversion)) },
                        modifier = Modifier.testTag("defender-chord-inversion-$inversion").heightIn(min = GardenTokens.TouchMinimum))
                }
            }
            Text("Se responde raíz y calidad; el bajo puede ser otra nota. Deslizá las calidades durante la partida.", color = GardenTokens.TextSecondary)
            if (ui.progression) Text("Fa y primeras inversiones requieren dominar las ocho fundamentales en sol. Mezcla requiere las ocho fundamentales en fa. Las inversiones avanzan por calidad y clave.", color = GardenTokens.TextSecondary)
            DefenderChordQuality.entries.forEach { quality ->
                val locked = !ui.canChoose(ui.targetFor(quality = quality))
                FilterChip(ui.chordQuality == quality, { model.chordQuality(quality) }, enabled = !locked,
                    colors = FilterChipDefaults.filterChipColors(disabledLabelColor = GardenTokens.DisabledText, disabledContainerColor = GardenTokens.DisabledSurface),
                    label = { Text(quality.label + if (locked) " · Bloqueada" else "") },
                    modifier = Modifier.testTag("defender-quality-${quality.name}").heightIn(min = GardenTokens.TouchMinimum))
            }
            val missing = ui.chordTarget.prerequisites.filter { !ui.progress[it].mastered }
            if (ui.progression && missing.isNotEmpty()) Text("Falta dominar: ${missing.joinToString { it.label }}", color = GardenTokens.TextSecondary)
        } else DefenderStage.entries.forEach { stage ->
            val locked = ui.progression && stage !in ui.progress.unlocked
            FilterChip(selected = ui.stage == stage, onClick = { model.stage(stage) }, enabled = !locked,
                colors = FilterChipDefaults.filterChipColors(disabledLabelColor = GardenTokens.DisabledText,
                    disabledContainerColor = GardenTokens.DisabledSurface),
                label = { Text(stage.label + if (locked) " · Bloqueada" else "") },
                modifier = Modifier.testTag("defender-stage-${stage.name}").heightIn(min = GardenTokens.TouchMinimum))
        }
        if (ui.variant == "notes") Row(verticalAlignment = Alignment.CenterVertically) {
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
        Text("Viaje inicial ${if (ui.progression) 10 else ui.travelMillis / 1000} s · tres vidas\nSolo las llegadas quitan vida. Cada acierto suma 100 puntos.")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(selected = !ui.letterNames, onClick = { model.letterNames(false) }, label = { Text("Do–Si") })
            FilterChip(selected = ui.letterNames, onClick = { model.letterNames(true) }, label = { Text("C–B") })
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(if (ui.variant == "chords") "Sonido del acorde al acertar" else "Sonido de nota al acertar", modifier = Modifier.weight(1f))
            Switch(checked = ui.noteSound, onCheckedChange = model::noteSound, modifier = Modifier.testTag("defender-note-sound"))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Efectos de juego", modifier = Modifier.weight(1f))
            Switch(checked = ui.effectsSound, onCheckedChange = model::effectsSound, modifier = Modifier.testTag("defender-effects-sound"))
        }
        Button(onClick = { model.start() }, modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum).testTag("defender-start")) { Text("Jugar Defender") }
        Text(if (ui.progression) "Dos partidas altas completas de la misma etapa habilitan la siguiente. Valores experimentales."
            else "Esta práctica no modifica tu progreso. Podés revisar el resumen al terminar.", color = GardenTokens.TextSecondary)
        ui.error?.let { Text(it, color = GardenTokens.ErrorText) }
    }
}

@Composable
private fun ColumnScope.DefenderGame(ui: DefenderUiState, model: DefenderViewModel, reduced: Boolean) {
    val game = ui.game
    FlowRow(Modifier.fillMaxWidth().padding(vertical = GardenTokens.SpaceSm), horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceMd)) {
        Text(game.configuration.contentLabel, color = GardenTokens.Primary, fontWeight = FontWeight.Bold)
        Text("Vidas: ${game.lives}", color = GardenTokens.Primary, modifier = Modifier.testTag("defender-lives"))
        Text("${game.points} pts", fontWeight = FontWeight.Bold, modifier = Modifier.testTag("defender-score"))
        if (reduced) game.enemies.minOfOrNull { it.deadlineMillis - game.activeMillis }?.let {
            Text("Próxima llegada: ${(it.coerceAtLeast(0) + 999) / 1000} s", style = MaterialTheme.typography.bodySmall)
        }
    }
    if (game.phase == DefenderPhase.PAUSED) {
        Box(Modifier.weight(1f).fillMaxWidth().testTag("defender-paused"), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Text("En pausa", style = MaterialTheme.typography.headlineLarge)
                Text("Tiempo y audio detenidos", color = GardenTokens.TextSecondary)
                OutlinedButton(onClick = model::openInspector, modifier = Modifier.testTag("defender-inspector-open")) { Text("Progreso y DDA") }
                Button(onClick = model::resume, modifier = Modifier.testTag("defender-resume")) { Text("Reanudar") }
            }
        }
        return
    }
    DefenderArena(game, Modifier.weight(1f).fillMaxWidth(), reduced)
    // Fixed bottom controls remain reachable on a narrow portrait display.
    Surface(color = GardenTokens.Surface, shape = RoundedCornerShape(GardenTokens.RadiusLarge)) {
        Column(Modifier.padding(GardenTokens.SpaceSm), verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceXs)) {
            Text(game.feedback ?: if (game.configuration.variant == "chords") "Elegí raíz, alteración y calidad" else "Seleccioná la nota", color = if (game.feedback == null) GardenTokens.TextSecondary else GardenTokens.ErrorText,
                style = MaterialTheme.typography.bodySmall, modifier = Modifier.testTag("defender-feedback"))
            DefenderName.entries.toList().chunked(4).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    row.forEach { name ->
                        val selected = game.selected == name
                        OutlinedButton(onClick = { model.select(name) },
                            modifier = Modifier.weight(1f).heightIn(min = GardenTokens.TouchMinimum).testTag("defender-name-${name.name}"),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) GardenTokens.SurfaceSelected else Color.Transparent),
                            border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) GardenTokens.Primary else GardenTokens.Border)) {
                            Text(if (ui.letterNames) name.letter else name.solfege, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (row.size < 4) Spacer(Modifier.weight(1f))
                }
            }
            if (game.configuration.accidentals || game.configuration.variant == "chords") {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DefenderAccidental.entries.forEach { accidental ->
                        FilterChip(selected = game.selectedAccidental == accidental,
                            onClick = { model.accidental(accidental) }, label = { Text(accidental.symbol) },
                            modifier = Modifier.weight(1f).heightIn(min = GardenTokens.TouchMinimum).testTag("defender-accidental-${accidental.name}"))
                    }
                }
            }
            if (game.configuration.variant == "chords") Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
                DefenderChordQuality.entries.forEach { quality ->
                    FilterChip(game.selectedQuality == quality, { model.quality(quality) }, label = { Text(quality.label) },
                        modifier = Modifier.testTag("defender-answer-quality-${quality.name}").heightIn(min = GardenTokens.TouchMinimum))
                }
            }
        }
    }
    Button(onClick = model::fire, enabled = game.selected != null && (game.configuration.variant != "chords" || game.selectedQuality != null),
        modifier = Modifier.fillMaxWidth().padding(vertical = GardenTokens.SpaceSm).heightIn(min = GardenTokens.TouchMinimum).testTag("defender-fire")
            .pointerInput(game.selected, game.selectedAccidental, game.selectedQuality) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                    game.selected?.takeIf { game.configuration.variant != "chords" || game.selectedQuality != null }?.let { selected ->
                        model.firePointer(down.uptimeMillis, SystemClock.uptimeMillis(), selected, game.selectedAccidental, game.selectedQuality)
                        down.consume()
                    }
                    waitForUpOrCancellation(pass = PointerEventPass.Initial)?.consume()
                }
            }) {
        Text("Tocar", fontWeight = FontWeight.Bold)
    }
    ui.error?.let { Text(it, color = GardenTokens.ErrorText) }
}

@Composable
private fun DefenderArena(game: DefenderState, modifier: Modifier, reduced: Boolean) {
    BoxWithConstraints(modifier.testTag("defender-arena")) {
        Canvas(Modifier.fillMaxSize()) {
            val base = Offset(size.width * .5f, size.height * .94f)
            drawLine(GardenTokens.Moss, Offset(size.width * .25f, size.height * .84f),
                Offset(size.width * .75f, size.height * .84f), 2.dp.toPx())
            val trunk = Path().apply {
                moveTo(base.x - 10.dp.toPx(), base.y)
                quadraticTo(base.x + 6.dp.toPx(), base.y - 19.dp.toPx(), base.x - 3.dp.toPx(), base.y - 44.dp.toPx())
                lineTo(base.x + 6.dp.toPx(), base.y - 44.dp.toPx())
                quadraticTo(base.x + 14.dp.toPx(), base.y - 15.dp.toPx(), base.x + 10.dp.toPx(), base.y); close()
            }
            drawPath(trunk, GardenTokens.Primary)
            for ((index, x) in listOf(-24f, -8f, 12f).withIndex()) drawOval(
                if (index == 1) GardenTokens.Moss else GardenTokens.Sage,
                Offset(base.x + x.dp.toPx(), base.y - (64 + index % 2 * 9).dp.toPx()), Size(35.dp.toPx(), 40.dp.toPx()))
            game.results.lastOrNull()?.takeIf { !reduced && it.resolution == DefenderResolution.ELIMINATED && game.activeMillis - it.atMillis in 0..220 }?.let {
                val p = it.position
                val point = Offset(p.x * size.width, p.y * size.height)
                drawCircle(GardenTokens.Success, 9.dp.toPx(), point, style = Stroke(2.dp.toPx()))
            }
        }
        game.enemies.forEach { enemy ->
            val point = if (reduced) enemy.entry else enemy.position(game.activeMillis)
            val chord = enemy.chord != null
            val cardWidth = if (game.configuration.maxEnemies == 1) minOf(if (chord) 190.dp else 136.dp, maxWidth * if (chord) .7f else .45f) else maxWidth * if (chord) .34f else .235f
            val cardHeight = if (game.configuration.maxEnemies == 1) minOf(116.dp, maxHeight * .35f) else maxHeight * if (chord) .20f else .17f
            Column(Modifier.offset(x = maxWidth * point.x - cardWidth / 2, y = maxHeight * point.y - cardHeight / 2)
                .width(cardWidth).testTag("defender-enemy"), horizontalAlignment = Alignment.CenterHorizontally) {
                DefenderEnemyStaff(enemy, Modifier.fillMaxWidth().height(cardHeight).testTag("defender-target"))
                Canvas(Modifier.width(24.dp).height(12.dp)) {
                    val wing = Path().apply {
                        moveTo(size.width / 2, size.height)
                        quadraticTo(-size.width / 4, -size.height / 2, size.width / 2, size.height / 2)
                        quadraticTo(size.width * 1.25f, -size.height / 2, size.width / 2, size.height); close()
                    }
                    drawPath(wing, GardenTokens.Moss)
                    drawLine(GardenTokens.Primary, Offset(size.width / 2, size.height / 4), Offset(size.width / 2, size.height), 1.dp.toPx())
                }
            }
        }
        game.results.lastOrNull()?.takeIf { it.resolution == DefenderResolution.ARRIVED && game.activeMillis - it.atMillis in 0..1200 }?.let { result ->
            Text(result.enemy.solution(game.configuration.letterNames), color = GardenTokens.ErrorText, fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(x = maxWidth * result.position.x - 28.dp, y = maxHeight * result.position.y - 22.dp)
                    .background(GardenTokens.Background).padding(GardenTokens.SpaceXs).testTag("defender-solution"))
        }
    }
}

/** Own vector treble clef: spiral sits on the G4 line (second from the bottom). */
@Composable
fun NoteStaff(note: DefenderNote, modifier: Modifier = Modifier, chordNotes: List<DefenderNote>? = null) {
    val voices = chordNotes ?: listOf(note)
    Canvas(modifier.background(GardenTokens.Surface, RoundedCornerShape(GardenTokens.RadiusLarge)).semantics {
        contentDescription = "Pentagrama en clave de ${note.clef.label.lowercase()}"
    }) {
        val sx = size.width / if (chordNotes == null) 100f else 144f; val sy = size.height / 120f
        val ink = GardenTokens.Text
        translate(top = 8 * sy) {
        for (line in 0..4) drawLine(ink, Offset(4 * sx, (28 + line * 12) * sy),
            Offset((if (chordNotes == null) 96 else 140) * sx, (28 + line * 12) * sy), .9f * sy)
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
        val stemHeadX = if (chordNotes == null) 66f else 110f
        val offsets = defenderChordHeadOffsets(voices)
        voices.forEachIndexed { index, voice ->
        val headX = stemHeadX + if (chordNotes == null) 0 else offsets[index]
        val ledgerSteps = if (voice.staffStep < 0) (-2 downTo voice.staffStep step 2).toList()
            else if (voice.staffStep > 8) (10..voice.staffStep step 2).toList() else emptyList()
        ledgerSteps.forEach { step ->
            val ledgerY = (76 - step * 6) * sy
            drawLine(ink, Offset((headX-5)*sx, ledgerY), Offset((headX+19)*sx, ledgerY), .9f*sy)
        }
        if (voice.accidental != DefenderAccidental.NATURAL) {
            val ay = (76 - voice.staffStep * 6) * sy
            val ax = (if (chordNotes == null) 52f else 87f - (index % 3)*15f)*sx
            if (voice.accidental == DefenderAccidental.SHARP) {
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
        val y = (76 - voice.staffStep * 6) * sy
        drawOval(ink, Offset(headX * sx, y - 4.4f * sy), Size(12 * sx, 8.8f * sy))
        }
        val low = (76 - voices.minOf { it.staffStep } * 6)*sy
        val high = (76 - voices.maxOf { it.staffStep } * 6)*sy
        if (voices.map { it.staffStep }.average() >= 4) drawLine(ink, Offset(stemHeadX*sx, high), Offset(stemHeadX*sx, low + 29*sy), 1.4f*sx)
        else drawLine(ink, Offset((stemHeadX+12)*sx, low), Offset((stemHeadX+12)*sx, high - 29*sy), 1.4f*sx)
        }
    }
}

@Composable
internal fun DefenderEnemyStaff(enemy: DefenderEnemy, modifier: Modifier = Modifier) {
    NoteStaff(enemy.note, modifier, enemy.chord?.notes)
}

@Composable
private fun DefenderSummary(ui: DefenderUiState, model: DefenderViewModel, onData: (() -> Unit)?) {
    val game = ui.game
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(vertical = GardenTokens.SpaceBase),
        verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceMd)) {
        Text(if (game.phase == DefenderPhase.FINISHED) "Base alcanzada" else "Partida interrumpida",
            style = MaterialTheme.typography.headlineLarge, modifier = Modifier.testTag("defender-summary"))
        Text("${game.points} puntos · ${game.results.count { it.resolution == DefenderResolution.ELIMINATED }} eliminados")
        Text("${game.results.count { it.resolution == DefenderResolution.ARRIVED }} llegadas · ${game.failedShots} disparos fallidos")
        DefenderStorageNotice(ui, model, onData)
        Text(game.configuration.contentLabel, color = GardenTokens.Primary, fontWeight = FontWeight.Bold)
        if (game.configuration.variant == "chords") Text("${defenderInversionLabel(game.configuration.chordInversion)} · ${game.configuration.chordClefs.label} · grafía exacta de raíz")
        else Text(when (game.configuration.stage) {
            DefenderStage.N1 -> "Clave de sol · rango Mi4–Fa5"
            DefenderStage.N2 -> "Clave de sol · rango La3–Do6"
            DefenderStage.N3 -> "Clave de fa · rango Do2–Mi4"
            DefenderStage.N4 -> "Claves de sol y fa · rangos La3–Do6 / Do2–Mi4"
        })
        Text(if (game.configuration.variant == "chords") "Alteraciones explícitas en las voces del acorde"
            else if (game.configuration.accidentals) "Alteraciones: naturales, bemoles y sostenidos"
            else "Alteraciones: solo naturales")
        Text(if (game.origin == "progression") "Progresión · avance al cerrar el resumen" else "Práctica · no modifica tu progreso", color = GardenTokens.TextSecondary)
        Text("Semilla ${game.seed} · ${game.configuration.version}", color = GardenTokens.TextSecondary, style = MaterialTheme.typography.bodySmall)
        OutlinedButton(onClick = model::openInspector, modifier = Modifier.testTag("defender-inspector-open")) { Text("Progreso y DDA") }
        OutlinedButton(onClick = model::openHistory, modifier = Modifier.testTag("defender-history-open")) { Text("Historial de Defender") }
        Button(onClick = model::prepare, modifier = Modifier.fillMaxWidth().testTag("defender-new")) { Text("Preparar otra partida") }
        Text("Revisión", style = MaterialTheme.typography.titleLarge)
        game.results.forEach { result ->
            val note = result.enemy.note
            Surface(color = GardenTokens.Surface, shape = RoundedCornerShape(GardenTokens.RadiusLarge)) {
                Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DefenderEnemyStaff(result.enemy, Modifier.width(110.dp).height(84.dp))
                    Column {
                        Text("${result.enemy.solution()} · ${result.enemy.solution(true)}", fontWeight = FontWeight.Bold)
                        result.enemy.chord?.let { Text(it.notes.joinToString(" · ") { voice -> voice.writtenName() }) }
                        Text("Clave de ${note.clef.label.lowercase()} · ${if (result.enemy.chord != null) defenderInversionLabel(result.enemy.chord.inversion) else when (note.accidental) {
                            DefenderAccidental.NATURAL -> "natural"
                            DefenderAccidental.FLAT -> "bemol"
                            DefenderAccidental.SHARP -> "sostenido"
                        }}", color = GardenTokens.TextSecondary, style = MaterialTheme.typography.bodySmall)
                        Text(if (result.resolution == DefenderResolution.ELIMINATED) "Eliminado" else "Llegó al árbol", color = GardenTokens.TextSecondary)
                    }
                }
            }
        }
    }
}
