package ar.edu.unmdp.entrenamiento.ui

import android.Manifest
import android.content.pm.PackageManager
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import ar.edu.unmdp.entrenamiento.tuner.*
import java.util.Locale

@Composable
fun TunerApp(ui: TunerUiState, model: TunerViewModel, onHome: () -> Unit) {
    if (ui.historyOpen) { TunerHistoryApp(ui, model); return }
    if (ui.configuration.configuration?.mode == TunerMode.TRAINING) {
        EarTunerApp(ui, model, onHome)
        return
    }
    val context = LocalContext.current
    // Permission callbacks may arrive after navigation or backgrounding. Starting remains explicit.
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (!it) model.permissionDenied()
    }
    val state = ui.tuning
    val configuration = ui.configuration.configuration
    TunerGardenTheme {
        BackHandler(onBack = onHome)
        Scaffold(containerColor = GardenTokens.Background) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TextButton(onClick = onHome) { Text("Volver al menú") }
                Text("Afinador", style = MaterialTheme.typography.headlineLarge)
                TunerHistoryAccess(ui, model)
                Text(configuration?.let { "${if (it.mode == TunerMode.FREE) "Libre" else "Guiado"} · ${it.currentGuitar.name} · ${it.currentTuning.name}" } ?: "Configuración no disponible",
                    style = MaterialTheme.typography.titleMedium, modifier = Modifier.testTag("tuner-setup"))
                TunerConfigurationPanel(ui.copy(configurationBusy = ui.configurationBusy || ui.referenceBusy), model)
                GuidedTunerPanel(ui.copy(configurationBusy = ui.configurationBusy || ui.referenceBusy), model)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
                    TunerCriterion.entries.forEach { criterion ->
                        FilterChip(selected = configuration?.criterion == criterion, enabled = configuration != null && !ui.configurationBusy && !ui.referenceBusy, onClick = { model.criterion(criterion) },
                            label = { Text(if (criterion == TunerCriterion.NORMAL) "Normal ±5 cents" else "Pro ±2 cents") })
                    }
                }
                if (configuration?.mode != TunerMode.GUIDED) FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
                    FilterChip(selected = state.tracking == TunerTracking.CHROMATIC, enabled = configuration != null && !ui.configurationBusy && !ui.referenceBusy,
                        onClick = { model.tracking(TunerTracking.CHROMATIC) }, label = { Text("Cromático") })
                    FilterChip(selected = state.tracking == TunerTracking.STRINGS, enabled = configuration != null && !ui.configurationBusy && !ui.referenceBusy,
                        onClick = { model.tracking(TunerTracking.STRINGS) }, label = { Text("Cuerdas") })
                }
                Text(state.measuredMidi?.let(::noteName) ?: "Sin tono válido",
                    style = MaterialTheme.typography.displayLarge, modifier = Modifier.testTag("tuner-note"))
                Text(state.frequency?.let { String.format(Locale.ROOT, "%.2f Hz", it) } ?: "Tocá una sola cuerda y dejala sonar")
                Text(state.targetMidi?.let { "Objetivo: ${noteName(it)}" } ?: "Sin objetivo asignado")
                Text(state.cents?.let { String.format(Locale.ROOT, "%+.1f cents", it) } ?: "Sin desviación válida")
                Text(when {
                    state.inTune -> "Afinado · ${state.stableRequiredMillis} ms estables"
                    state.cents == null -> "Esperando señal válida"
                    kotlin.math.abs(state.cents) <= state.criterion.margin -> "Dentro del margen · dejá sonar la cuerda"
                    state.cents < 0 -> "Grave respecto del objetivo"
                    else -> "Agudo respecto del objetivo"
                }, modifier = Modifier.testTag("tuner-evaluation"))
                if (configuration?.mode == TunerMode.FREE && state.frequency == null) state.lastConfirmation?.let {
                    Text("Última confirmación: ${noteName(it.measuredMidi)} · ${String.format(Locale.ROOT, "%+.1f", it.cents)} cents. La señal decayó; volvé a pulsar para medir.",
                        color = GardenTokens.TextSecondary, modifier = Modifier.testTag("tuner-last-confirmation"))
                }
                Text(state.fixedString?.let { "Cuerda $it fijada" }
                    ?: state.stringNumber?.let { "Cuerda $it detectada" } ?: "Sin cuerda asignada")
                if (configuration?.mode != TunerMode.GUIDED) {
                    Text("Fijar cuerda · sexta → primera")
                    (configuration?.currentTuning?.notesSixToFirst ?: listOf(40, 45, 50, 55, 59, 64)).mapIndexed { index, midi -> (6 - index) to midi }.chunked(3).forEach { row ->
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
                            row.forEach { (number, midi) ->
                                FilterChip(selected = state.fixedString == number, enabled = configuration != null && !ui.configurationBusy && !ui.referenceBusy, onClick = { model.fixString(number) },
                                    label = { Text("$number · ${noteName(midi)}") })
                            }
                        }
                    }
                    if (state.fixedString != null) TextButton(onClick = { model.fixString(null) }, enabled = !ui.configurationBusy && !ui.referenceBusy) { Text("Liberar cuerda") }
                }
                TunerReferencePanel(ui, model)
                Text(if (state.running) "Micrófono activo" else "Micrófono detenido", modifier = Modifier.testTag("tuner-status"))
                Button(onClick = {
                    if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) model.start()
                    else permission.launch(Manifest.permission.RECORD_AUDIO)
                }, enabled = !state.running && configuration != null && !ui.configurationBusy && !ui.referenceBusy, modifier = Modifier.testTag("tuner-start")) { Text("Iniciar micrófono") }
                OutlinedButton(onClick = model::stop, enabled = state.running || ui.referenceBusy, modifier = Modifier.testTag("tuner-stop")) { Text("Detener audio y micrófono") }
                ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                TextButton(onClick = { context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, "package:${context.packageName}".toUri())) }) { Text("Permisos en Ajustes") }
                Text("Después de autorizar el permiso, pulsá Iniciar micrófono. No se guardan grabaciones; el modo Libre no guarda desempeño.", color = GardenTokens.TextSecondary)
                Text("Una cuerda fijada conserva su altura y octava. Los márgenes Normal/Pro son experimentales.", color = GardenTokens.TextSecondary)
            }
        }
    }
}
