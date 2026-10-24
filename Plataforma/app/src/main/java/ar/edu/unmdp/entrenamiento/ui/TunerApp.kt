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
    val context = LocalContext.current
    // Permission callbacks may arrive after navigation or backgrounding. Starting remains explicit.
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (!it) model.permissionDenied()
    }
    val state = ui.tuning
    val configuration = ui.configuration.configuration
    MaterialTheme(colorScheme = darkColorScheme(primary = Pink, secondary = Lavender, background = Night, surface = Panel)) {
        BackHandler(onBack = onHome)
        Scaffold(containerColor = Night) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TextButton(onClick = onHome) { Text("Volver al menú") }
                Text("Afinador", style = MaterialTheme.typography.headlineLarge)
                Text(configuration?.let { "${if (it.mode == TunerMode.FREE) "Libre" else "Guiado"} · ${it.currentGuitar.name} · ${it.currentTuning.name}" } ?: "Configuración no disponible",
                    style = MaterialTheme.typography.titleMedium, modifier = Modifier.testTag("tuner-setup"))
                TunerConfigurationPanel(ui, model)
                GuidedTunerPanel(ui, model)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TunerCriterion.entries.forEach { criterion ->
                        FilterChip(selected = configuration?.criterion == criterion, enabled = configuration != null && !ui.configurationBusy, onClick = { model.criterion(criterion) },
                            label = { Text(if (criterion == TunerCriterion.NORMAL) "Normal ±5 cents" else "Pro ±2 cents") })
                    }
                }
                if (configuration?.mode != TunerMode.GUIDED) Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = state.tracking == TunerTracking.CHROMATIC, enabled = configuration != null && !ui.configurationBusy,
                        onClick = { model.tracking(TunerTracking.CHROMATIC) }, label = { Text("Cromático") })
                    FilterChip(selected = state.tracking == TunerTracking.STRINGS, enabled = configuration != null && !ui.configurationBusy,
                        onClick = { model.tracking(TunerTracking.STRINGS) }, label = { Text("Cuerdas") })
                }
                Text(state.measuredMidi?.let(::noteName) ?: "Sin tono válido",
                    style = MaterialTheme.typography.displayLarge, modifier = Modifier.testTag("tuner-note"))
                Text(state.frequency?.let { String.format(Locale.ROOT, "%.2f Hz", it) } ?: "Tocá una sola cuerda y dejala sonar")
                Text(state.targetMidi?.let { "Objetivo: ${noteName(it)}" } ?: "Sin objetivo asignado")
                Text(state.cents?.let { String.format(Locale.ROOT, "%+.1f cents", it) } ?: "Sin desviación válida")
                Text(when {
                    state.inTune -> "Afinado · un segundo estable"
                    state.cents == null -> "Esperando señal válida"
                    kotlin.math.abs(state.cents) <= state.criterion.margin -> "Dentro del margen · sostené la nota"
                    state.cents < 0 -> "Grave respecto del objetivo"
                    else -> "Agudo respecto del objetivo"
                }, modifier = Modifier.testTag("tuner-evaluation"))
                Text(state.fixedString?.let { "Cuerda $it fijada" }
                    ?: state.stringNumber?.let { "Cuerda $it detectada" } ?: "Sin cuerda asignada")
                if (configuration?.mode != TunerMode.GUIDED) {
                Text("Fijar cuerda · sexta → primera")
                (configuration?.currentTuning?.notesSixToFirst ?: listOf(40, 45, 50, 55, 59, 64)).mapIndexed { index, midi -> (6 - index) to midi }.chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { (number, midi) ->
                            FilterChip(selected = state.fixedString == number, enabled = configuration != null && !ui.configurationBusy, onClick = { model.fixString(number) },
                                label = { Text("$number · ${noteName(midi)}") })
                        }
                    }
                }
                if (state.fixedString != null) TextButton(onClick = { model.fixString(null) }) { Text("Liberar cuerda") }
                }
                Text(if (state.running) "Micrófono activo" else "Micrófono detenido", modifier = Modifier.testTag("tuner-status"))
                Button(onClick = {
                    if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) model.start()
                    else permission.launch(Manifest.permission.RECORD_AUDIO)
                }, enabled = !state.running && configuration != null && !ui.configurationBusy, modifier = Modifier.testTag("tuner-start")) { Text("Iniciar micrófono") }
                OutlinedButton(onClick = model::stop, enabled = state.running, modifier = Modifier.testTag("tuner-stop")) { Text("Detener micrófono") }
                ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                TextButton(onClick = { context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, "package:${context.packageName}".toUri())) }) { Text("Permisos en Ajustes") }
                Text("Después de autorizar el permiso, pulsá Iniciar micrófono. No se guardan mediciones ni audio.", color = Muted)
                Text("Una cuerda fijada conserva su altura y octava. Los márgenes Normal/Pro son experimentales.", color = Muted)
            }
        }
    }
}
