package ar.edu.unmdp.entrenamiento.ui

import android.Manifest
import android.content.pm.PackageManager
import android.content.Intent
import androidx.core.net.toUri
import android.provider.Settings
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
import ar.edu.unmdp.entrenamiento.voice.*
import java.util.Locale

@Composable
fun VoiceApp(ui: VoiceUiState, model: VoiceViewModel, onHome: () -> Unit) {
    val context = LocalContext.current
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) model.start() else model.permissionDenied()
    }
    MaterialTheme(colorScheme = darkColorScheme(primary = Pink, background = Night, surface = Panel)) {
        BackHandler(onBack = onHome)
        Scaffold { padding ->
            Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                TextButton(onClick = onHome) { Text("Volver al menú") }
                Text("Afiná tu voz", style = MaterialTheme.typography.headlineMedium)
                Text("Prueba técnica · YIN · Sostené una nota en un lugar tranquilo. No se guarda audio.")
                Text("Objetivo: ${noteName(ui.targetMidi)}")
                listOf(listOf(48, 55, 57, 60), listOf(62, 64, 67, 69)).forEach { notes ->
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        notes.forEach { midi -> FilterChip(selected = ui.targetMidi == midi, onClick = { model.target(midi) },
                            enabled = !ui.running, label = { Text(noteName(midi)) }) }
                    }
                }
                val measurement = ui.latest
                Text(measurement?.estimate?.frequency?.let { String.format(Locale.ROOT, "%.1f Hz", it) } ?: "Sin tono válido",
                    style = MaterialTheme.typography.headlineLarge, modifier = Modifier.testTag("voice-frequency"))
                Text(measurement?.estimate?.midi?.let(::noteName) ?: "Sin nota")
                Text(measurement?.cents?.let { String.format(Locale.ROOT, "%+.1f cents respecto del objetivo", it) } ?: "Esperando una nota sostenida")
                measurement?.let {
                    Text(it.estimate.reason ?: "Periodicidad: ${String.format(Locale.ROOT, "%.3f", it.estimate.periodicity)}")
                    Text("Procesamiento: ${it.processingMicros} µs · ${it.elapsedMillis} ms desde inicio")
                }
                Text(if (ui.running) "Micrófono activo" else "Micrófono detenido", modifier = Modifier.testTag("voice-status"))
                Button(onClick = {
                    if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) model.start()
                    else permission.launch(Manifest.permission.RECORD_AUDIO)
                }, enabled = !ui.running, modifier = Modifier.testTag("voice-start")) { Text("Iniciar micrófono") }
                OutlinedButton(onClick = model::stop, enabled = ui.running, modifier = Modifier.testTag("voice-stop")) { Text("Detener y guardar mediciones") }
                ui.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = { context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, "package:${context.packageName}".toUri())) }) {
                        Text("Abrir ajustes de permisos")
                    }
                }
                Text("Captura mono 44.100 Hz · ventana 4.096 · salto 1.024 · rango 55–1.000 Hz · máximo 2 minutos por medición.")
                Text("Se conserva una medición derivada cada 200 ms. El tiempo de procesamiento no es latencia acústica.")
                Text("Sesiones guardadas en esta apertura: ${ui.saved}. Exportalas desde Datos y demostración.")
            }
        }
    }
}
