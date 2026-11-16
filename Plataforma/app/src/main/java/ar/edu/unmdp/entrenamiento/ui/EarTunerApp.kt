package ar.edu.unmdp.entrenamiento.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import ar.edu.unmdp.entrenamiento.tuner.*
import java.util.Locale

/** Una columna de papel: el objetivo y la acción de comprobar preceden al resumen.
 * Los estados ocultos no componen ningún medidor ni texto con mediciones.
 */
@Composable
fun EarTunerApp(ui: TunerUiState, model: TunerViewModel, onHome: () -> Unit) {
    val context = LocalContext.current
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (!it) model.permissionDenied()
    }
    val ear = ui.ear
    val config = ui.configuration.configuration ?: return
    val busy = ui.configurationBusy || ui.referenceBusy
    val scroll = rememberScrollState()
    LaunchedEffect(ear.phase) {
        if (!ear.active && ear.results.isNotEmpty()) scroll.scrollTo(0)
    }
    TunerGardenTheme {
        BackHandler(onBack = onHome)
        Scaffold(containerColor = GardenTokens.Background) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).verticalScroll(scroll)
                .padding(GardenTokens.SpaceLg).testTag("ear-screen"), verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceMd)) {
                TextButton(onClick = onHome) { Text("Volver al menú") }
                Text("Afinar de oído", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading() })
                Text("${config.currentGuitar.name} · ${config.currentTuning.name}")
                if (!ear.active && ear.results.isNotEmpty()) {
                    Text("Resumen de oído", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.testTag("ear-summary"))
                    Text("${ear.setup?.guitar?.name} · ${ear.setup?.tuning?.name} · ${ear.setup?.criterion?.name}")
                    val errors = ear.results.count { !it.initialPassed }
                    Text("${ear.results.size} intentos válidos · $errors ${if (errors == 1) "error inicial" else "errores iniciales"}")
                    ear.results.forEach { result ->
                        HorizontalDivider(color = GardenTokens.Border)
                        Text("Cuerda ${result.number} · ${noteName(result.targetMidi)}")
                        Text("${if (result.initialPassed) "Acierto inicial" else "Error inicial"} · ${noteName(result.measuredMidi)} · ${deviation(result.initialCents)}")
                        Text(if (result.corrected) "Corrección completada" else "Corrección pendiente")
                        result.classification?.let { Text("Clasificación ${if (it.correct) "correcta" else "incorrecta"}: ${it.answer.label} / ${it.actual.label}") }
                    }
                    if (ear.review.review.isNotEmpty()) {
                        Text("Revisión final · no suma intentos de oído")
                        ear.review.review.forEach { result -> Text("Cuerda ${result.number}: ${if (result.passed) "aprobada" else if (result.number in ear.review.pending) "pendiente" else "corregida"}") }
                    }
                }
                if (!ear.active) {
                    TunerHistoryAccess(ui, model)
                    TunerModeSelector(ui.copy(configurationBusy = busy), model)
                    TunerConfigurationPanel(ui.copy(configurationBusy = busy), model)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
                        TunerCriterion.entries.forEach { criterion ->
                            FilterChip(config.criterion == criterion, { model.criterion(criterion) }, enabled = !busy,
                                label = { Text("${criterion.name.lowercase().replaceFirstChar { it.uppercase() }} ±${criterion.margin.toInt()} cents") })
                        }
                    }
                    TunerOption("Revisión final", config.finalReview, !busy, "ear-final-review", model::finalReview)
                    TunerOption("Clasificar antes de ajustar", config.earClassification, !busy, "ear-classification", model::earClassification)
                    Text("Escuchá la referencia, tocá la cuerda y ajustá sin mirar una medición. Comprobá cuando creas que está afinada.")
                }
                Text(when (ear.phase) {
                    EarPhase.READY -> "Preparación · sexta a primera"
                    EarPhase.HIDDEN -> "Ajuste de oído · cuerda ${ear.measurement.fixedString}"
                    EarPhase.CHECKING -> "Comprobando · tocá nuevamente"
                    EarPhase.CORRECTING -> "Corrección con ayuda · cuerda ${ear.measurement.fixedString}"
                    EarPhase.REVIEW -> "Revisión final · cuerda ${ear.measurement.fixedString}"
                    EarPhase.COMPLETE -> if (ear.finalReview) "Conjunto comprobado" else "Entrenamiento completo · sin revisión final"
                    EarPhase.INTERRUPTED -> "Entrenamiento interrumpido"
                }, style = MaterialTheme.typography.titleMedium, modifier = Modifier.testTag("ear-phase"))
                if (ear.active) {
                    Text("Objetivo: ${ear.measurement.targetMidi?.let(::noteName) ?: "Sin objetivo"}")
                    if (ear.farWarning) Text("Muy lejos, revisar cuerda correcta", color = GardenTokens.ErrorText, modifier = Modifier.testTag("ear-warning"))
                    if (ear.hidden) {
                        Text("Medición oculta durante el ajuste", modifier = Modifier.testTag("ear-hidden"))
                        if (ear.classificationEnabled && !ear.classificationSubmitted) {
                            Text("Antes de ajustar, ¿cómo suena respecto de la referencia?")
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
                                EarDirection.entries.forEach { answer ->
                                    OutlinedButton(onClick = { model.classify(answer) }, enabled = !busy,
                                        modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("ear-classify-${answer.name}")) { Text(answer.label) }
                                }
                            }
                        }
                        Button(onClick = model::checkEar, enabled = ear.phase == EarPhase.HIDDEN && !busy && ear.measurement.running,
                            shape = RoundedCornerShape(GardenTokens.RadiusLarge),
                            modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum).testTag("ear-check")) { Text("Comprobar") }
                    } else {
                        if (ear.phase == EarPhase.CORRECTING) ear.results.lastOrNull()?.let { result ->
                            Text("Intento inicial: ${if (result.initialPassed) "acierto" else "error"} · ${noteName(result.measuredMidi)} · ${deviation(result.initialCents)}",
                                modifier = Modifier.testTag("ear-initial-result"))
                            result.classification?.let { Text("Clasificación: ${if (it.correct) "correcta" else "incorrecta"} · respondiste ${it.answer.label}, era ${it.actual.label}") }
                        }
                        val measurement = ear.measurement
                        Text(measurement.measuredMidi?.let(::noteName) ?: "Tocá nuevamente", modifier = Modifier.testTag("ear-live-note"))
                        Text(measurement.cents?.let(::deviation) ?: "Esperando señal válida", modifier = Modifier.testTag("ear-live-cents"))
                        Text("Dejá sonar la cuerda ${measurement.stableRequiredMillis} ms dentro de ±${measurement.criterion.margin.toInt()} cents para continuar.")
                        if (ear.phase == EarPhase.REVIEW && ear.review.phase == GuidedPhase.REVIEW) {
                            OutlinedButton(onClick = model::leavePending, enabled = !busy, modifier = Modifier.testTag("ear-pending")) { Text("Dejar pendiente y continuar") }
                        }
                    }
                    TunerReferencePanel(ui, model)
                }
                ear.message?.let { Text(it, modifier = Modifier.testTag("ear-message")) }
                Text(if (ear.measurement.running) "Micrófono activo" else "Micrófono detenido", modifier = Modifier.testTag("ear-microphone"))
                if (!ear.active) Button(onClick = {
                    if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) model.start()
                    else permission.launch(Manifest.permission.RECORD_AUDIO)
                }, enabled = !busy, shape = RoundedCornerShape(GardenTokens.RadiusLarge), modifier = Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum).testTag("ear-start")) { Text("Iniciar entrenamiento") }
                if (ear.active || ui.referenceBusy) OutlinedButton(onClick = model::stop, modifier = Modifier.testTag("ear-stop")) { Text("Detener entrenamiento") }
                ui.error?.let { Text(it, color = GardenTokens.ErrorText) }
                if (!ear.active) Text("El historial separa intentos, correcciones y revisión final. El resumen actual también está disponible con seguimiento apagado.", color = GardenTokens.TextSecondary)
                Text("Normal/Pro y los tiempos de estabilidad son criterios experimentales.", color = GardenTokens.TextSecondary)

            }
        }
    }
}

private fun deviation(cents: Double) = String.format(Locale.ROOT, "%+.1f cents", cents)
