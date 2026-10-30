package ar.edu.unmdp.entrenamiento.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import ar.edu.unmdp.entrenamiento.voice.*
import java.util.Locale
import kotlin.math.abs

private val intervalNames = listOf("Unísono", "Segunda menor", "Segunda mayor", "Tercera menor",
    "Tercera mayor", "Cuarta justa", "Tritono", "Quinta justa", "Sexta menor", "Sexta mayor",
    "Séptima menor", "Séptima mayor", "Octava")

internal fun vocalIntervalLabel(semitones: Int) = intervalNames[abs(semitones)] +
    if (semitones == 0) "" else if (semitones > 0) " ascendente" else " descendente"

internal fun vocalScaffoldingLabel(level: Int) = when (level) { 3 -> "Producción libre"; 2 -> "Ancla dada"; else -> "Imitación" }

internal val vocalScaffoldingChoices = (1..3).map { it to vocalScaffoldingLabel(it) }

@Composable
fun VocalIntervalApp(ui: VocalIntervalUiState, model: VocalIntervalViewModel, onData: () -> Unit = {}, onHome: () -> Unit) {
    if (ui.progressOpen) { VocalProgressApp(ui, model); return }
    if (ui.historyOpen) { VocalIntervalHistoryApp(ui, model, onData); return }
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    fun permitted() = context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    var microphoneGranted by remember { mutableStateOf(permitted()) }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        microphoneGranted = granted
        if (!granted) model.permissionDenied()
        // Permission alone never starts audio, including a result delivered after leaving the screen.
    }
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_RESUME) microphoneGranted = permitted() }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    BackHandler(onBack = onHome)
    TunerGardenTheme {
        Scaffold(containerColor = GardenTokens.Background, contentWindowInsets = WindowInsets.safeDrawing) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(GardenTokens.SpaceBase), verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceBase)) {
                TextButton(onClick = onHome, modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-home")) { Text("Volver al menú") }
                Text("Intervalos vocales", style = MaterialTheme.typography.headlineMedium)
                Text(if (ui.scaffoldingLevel == 3) "Elegí tu nota inicial y cantá ambas notas del intervalo, sin referencia sonora."
                    else if (ui.scaffoldingLevel == 2) "Escuchá el ancla y cantá solo la nota del intervalo indicado."
                    else "Escuchá dos notas y cantá la relación entre ellas en un registro cómodo.")
                ui.series?.let { Text("${vocalScaffoldingLabel(it.scaffoldingLevel)} · Ronda ${it.ordinal} de 5" + if (it.ended) " · Cerrada" else "",
                    modifier = Modifier.testTag("vocal-series-status")) }
                if (ui.round == null) {
                    if (ui.series == null) {
                    VocalChoice("Ayuda auditiva", ui.scaffoldingLevel, vocalScaffoldingChoices,
                        ui.canConfigure, "vocal-scaffolding", model::selectScaffolding)
                    if (!microphoneGranted) TextButton(onClick = { permission.launch(Manifest.permission.RECORD_AUDIO) },
                        enabled = ui.canConfigure, modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-permission")) { Text("Habilitar micrófono") }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
                        FilterChip(!ui.progression, { model.progression(false) }, enabled = ui.canConfigure,
                            modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-mode-manual"), label = { Text("Práctica") })
                        FilterChip(ui.progression, { model.progression(true) }, enabled = ui.canConfigure,
                            modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-mode-auto"), label = { Text("Progresión") })
                    }
                    if (ui.progression) {
                        Text("Cinco rondas por serie. Dos series altas permiten avanzar en el orden de intervalos.")
                        val available = ui.history.progress.unlockedFor(ui.scaffoldingLevel)
                        if (available.isEmpty()) Text(if (ui.scaffoldingLevel == 3)
                            "Producción libre requiere un hito de ancla dada para cada intervalo y dirección. Podés probarla en Práctica."
                            else "Ancla dada requiere un hito de imitación para cada intervalo y dirección. Podés probarla en Práctica.",
                            modifier = Modifier.testTag(if (ui.scaffoldingLevel == 3) "vocal-free-locked" else "vocal-given-locked"))
                        else VocalChoice("Intervalo desbloqueado", ui.semitones,
                            vocalCurriculum.flatten().filter { it in available }.map { it to vocalIntervalLabel(it) },
                            ui.canConfigure, "vocal-auto-interval", model::selectInterval)
                    } else {
                    VocalChoice("Intervalo", abs(ui.semitones), intervalNames.mapIndexed { i, label -> i to label },
                        ui.canConfigure, "vocal-interval") { model.selectInterval(if (ui.semitones < 0) -it else it) }
                    if (ui.semitones != 0) Row(Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
                        FilterChip(ui.semitones > 0, { model.selectInterval(abs(ui.semitones)) }, enabled = ui.canConfigure,
                            label = { Text("Ascendente") }, modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-up"))
                        FilterChip(ui.semitones < 0, { model.selectInterval(-abs(ui.semitones)) }, enabled = ui.canConfigure,
                            label = { Text("Descendente") }, modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-down"))
                    }
                    }
                    if (ui.scaffoldingLevel != 3) VocalChoice("Primera nota de la referencia", ui.anchorMidi,
                        listOf(48, 57, 60, 69).map { it to noteName(it) }, ui.canConfigure, "vocal-register", model::selectAnchor)
                    if (ui.progression) Button(onClick = model::beginSeries,
                        enabled = ui.canConfigure && ui.semitones in ui.history.progress.unlockedFor(ui.scaffoldingLevel),
                        modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-series-start")) { Text("Iniciar serie") }
                    } else Text(vocalIntervalLabel(ui.semitones))
                    if (ui.series?.ended == true) Button(onClick = model::newRound, enabled = !ui.working && !ui.historyBusy && ui.history.pending.isEmpty(),
                        modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-series-new")) { Text("Preparar otra serie") }
                    if (ui.scaffoldingLevel != 3) {
                    Button(onClick = model::playReference, enabled = ui.canConfigure && (!ui.progression || ui.series?.ended == false),
                        modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-listen")) {
                        Text(if (ui.referencePlaying) "Escuchando referencia…" else if (ui.heardReference) "Volver a escuchar"
                            else if (ui.scaffoldingLevel == 2) "Escuchar ancla" else "Escuchar intervalo")
                    }
                    Text("El micrófono permanece apagado mientras escuchás.", color = GardenTokens.TextSecondary)
                    }
                    if (ui.heardReference || ui.scaffoldingLevel == 3 && (!ui.progression || ui.series?.ended == false)) {
                        Text(if (ui.scaffoldingLevel == 2) "Cantá ${vocalIntervalLabel(ui.semitones)} desde el ancla escuchada. Sostené solo la nota objetivo, sin vibrato."
                            else if (ui.scaffoldingLevel == 3) "Cantá una nota cómoda y sostenida. Será tu ancla; después cantarás ${vocalIntervalLabel(ui.semitones)} desde ella."
                            else "Cantá una nota sostenida, sin vibrato, hasta que se confirme. Después cantarás la segunda.")
                        Button(onClick = {
                            if (permitted()) { if (ui.scaffoldingLevel == 2) model.startGivenTarget() else model.startAnchor() }
                            else permission.launch(Manifest.permission.RECORD_AUDIO)
                        }, enabled = !ui.working, modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum)
                            .testTag(if (ui.scaffoldingLevel == 2) "vocal-given-start" else "vocal-start")) {
                            Text(if (!microphoneGranted) "Habilitar micrófono" else if (ui.scaffoldingLevel == 2) "Cantar nota objetivo" else "Cantar primera nota")
                        }
                    }
                } else {
                    val phase = ui.round.phase
                    val status = when (phase) {
                        VocalIntervalPhase.ANCHOR -> "1 de 2 · Sostené la primera nota"
                        VocalIntervalPhase.READY_TARGET -> "Primera nota confirmada"
                        VocalIntervalPhase.TARGET -> if (ui.scaffoldingLevel == 2) "Sostené la nota objetivo" else "2 de 2 · Sostené la segunda nota"
                        VocalIntervalPhase.COMPLETED -> "Ronda completa"
                        VocalIntervalPhase.INTERRUPTED -> "Ronda interrumpida"
                        else -> "Prepará tu voz"
                    }
                    Text(status, style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.testTag("vocal-stage").semantics { liveRegion = LiveRegionMode.Polite })
                    Text(intervalNames[abs(ui.semitones)] + if (ui.semitones == 0) "" else if (ui.semitones > 0) " ascendente" else " descendente")
                    if (ui.microphoneActive) {
                        Text("Cantá sin vibrato hasta que se confirme el sostenido.")
                        Text(ui.frequencyHz?.let { String.format(Locale.ROOT, "%.1f Hz", it) } ?: "Esperando señal válida",
                            modifier = Modifier.testTag("vocal-frequency"))
                        if (phase == VocalIntervalPhase.TARGET) {
                            val error = ui.round.latestErrorCents
                            Text(error?.let { String.format(Locale.ROOT, "%+.1f cents respecto del intervalo", it) } ?: "Buscando la nota objetivo",
                                style = MaterialTheme.typography.headlineSmall, modifier = Modifier.testTag("vocal-feedback"))
                            Text(when { error == null -> "Sostené una sola nota."
                                abs(error) <= ui.toleranceCents -> "Cerca del objetivo; mantené la nota."
                                error < 0 -> "Probá un poco más agudo."
                                else -> "Probá un poco más grave." })
                        }
                    }
                    if (phase == VocalIntervalPhase.READY_TARGET) {
                        Text(if (ui.scaffoldingLevel == 3) "Desde tu primera nota, cantá ${vocalIntervalLabel(ui.semitones)}. El micrófono se abrirá al tocar el botón."
                            else "Ahora cantá la segunda nota del intervalo que escuchaste. El micrófono se abrirá al tocar el botón.")
                        Button(onClick = model::startTarget, enabled = !ui.working,
                            modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-target")) { Text("Cantar segunda nota") }
                    }
                    ui.round.result?.let { result ->
                        if (result.interruption == null) {
                            Text(if (result.inTune == true) "Dentro de la tolerancia de práctica" else "Fuera de la tolerancia de práctica",
                                style = MaterialTheme.typography.headlineSmall, modifier = Modifier.testTag("vocal-outcome"))
                            Text(String.format(Locale.ROOT, "Intervalo cantado: %.1f cents · objetivo: %d cents", result.intervalCents, ui.semitones * 100))
                            Text(String.format(Locale.ROOT, "Desviación: %+.1f cents", result.errorCents), modifier = Modifier.testTag("vocal-result-error"))
                            Text("Criterio experimental, pendiente de probar con voces reales.", color = GardenTokens.TextSecondary)
                        } else Text(when (result.interruption) {
                            "timeout" -> "Se terminó el tiempo. Podés iniciar otra ronda."
                            "background" -> "La app pasó a segundo plano. La ronda no se reanuda automáticamente."
                            "capture-error", "capture-ended" -> "La captura no pudo completar la ronda."
                            else -> "La ronda se canceló antes de terminar."
                        })
                        Button(onClick = model::newRound, enabled = !ui.working && !ui.historyBusy && ui.history.pending.isEmpty(),
                            modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-new")) {
                            Text(if (!ui.progression) "Nueva ronda" else if (ui.series?.ended == false) "Siguiente ronda" else "Preparar otra serie") }
                    }
                    if (ui.round.result == null) Text("Tiempo disponible: ${ui.remainingSeconds} s", color = GardenTokens.TextSecondary)
                }
                Text(if (ui.microphoneActive) "Micrófono activo" else "Micrófono detenido", modifier = Modifier.testTag("vocal-microphone"))
                if (ui.working || ui.round?.result == null && ui.round != null || ui.series?.ended == false) OutlinedButton(onClick = { model.stop() },
                    modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-cancel")) { Text("Cancelar") }
                ui.error?.let { Text(it, color = GardenTokens.ErrorText, modifier = Modifier.testTag("vocal-error").semantics { liveRegion = LiveRegionMode.Polite }) }
                if (ui.permissionDenied) TextButton(onClick = {
                    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, "package:${context.packageName}".toUri()))
                }, modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum)) { Text("Abrir ajustes de permisos") }
                VocalIntervalSaveNotice(ui, model)
                OutlinedButton(onClick = model::openProgress, enabled = !ui.working && !ui.historyBusy && (ui.round == null || ui.round.result != null),
                    modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-progress")) { Text("Progreso y DDA") }
                OutlinedButton(onClick = model::openHistory,
                    enabled = !ui.working && !ui.historyBusy && (ui.round == null || ui.round.result != null),
                    modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-history")) { Text("Historial vocal") }
                TextButton(onClick = onData, enabled = !ui.working && !ui.historyBusy && (ui.round == null || ui.round.result != null),
                    modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum).testTag("vocal-data")) { Text("Datos y demostración") }
                Text((if (ui.progression) "Progresión experimental" else "Práctica sin progreso") + " · Se guardan resultados y mediciones derivadas, sin audio.",
                    color = GardenTokens.TextSecondary)
            }
        }
    }
}

@Composable
internal fun VocalChoice(label: String, value: Int, choices: List<Pair<Int, String>>, enabled: Boolean,
    tag: String, select: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceXs)) {
        Text(label)
        Box {
            OutlinedButton(onClick = { expanded = true }, enabled = enabled,
                modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum).testTag(tag)) { Text(choices.first { it.first == value }.second) }
            DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
                choices.forEach { (key, name) -> DropdownMenuItem(text = { Text(name) },
                    modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("$tag-$key"),
                    onClick = { expanded = false; select(key) }) }
            }
        }
    }
}
