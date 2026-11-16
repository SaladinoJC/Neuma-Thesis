package ar.edu.unmdp.entrenamiento.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import ar.edu.unmdp.entrenamiento.rhythm.*

@Composable
internal fun RhythmCalibrationApp(ui: RhythmUiState, model: RhythmViewModel) {
    val state = ui.calibrationState
    RhythmTheme {
        BackHandler(onBack = model::closeCalibration)
        RhythmPageScaffold { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(RhythmTokens.SpaceBase),
                verticalArrangement = Arrangement.spacedBy(RhythmTokens.SpaceSm)) {
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(RhythmTokens.SpaceSm)) {
                    Text("Calibrar Rhythm Tap", style = MaterialTheme.typography.headlineSmall)
                    GardenPaperPanel(color = RhythmTokens.Surface) {
                    Text("Conservá la misma salida de audio y volumen que usarás al jugar. Escuchá cuatro pulsos; después tocá una vez por pulso durante doce pulsos, siguiendo el sonido.")
                    Text("El ajuste combina tu sincronización y la respuesta del equipo. No mide por separado la latencia física. El sonido de tus taps permanece apagado aquí.", color = RhythmTokens.TextSecondary)
                    }
                    Text(when {
                        ui.busy -> "Preparando audio…"
                        state.active && state.pulse == null -> "Prepará el conteo…"
                        state.active && requireNotNull(state.pulse) <= 4 -> "Escuchá · ${state.pulse} de 4"
                        state.active -> "Tocá · ${requireNotNull(state.pulse) - 4} de 12"
                        state.candidate != null -> "Ajuste propuesto: ${state.candidate.offsetNanos / 1_000_000} ms"
                        else -> "Calibración a 80 BPM"
                    }, style = MaterialTheme.typography.titleLarge, modifier = Modifier.testTag("rhythm-calibration-status"))
                    Text("Golpes registrados: ${state.samples} / 12")
                    state.error?.let { Text(it, color = RhythmTokens.ErrorText) }
                    ui.error?.let { Text(it, color = RhythmTokens.ErrorText) }
                    state.candidate?.let {
                        Text("Dispersión: ${it.spreadNanos / 1_000_000} ms · ${it.sampleCount} pulsos. Repetí si no seguiste el sonido con comodidad.")
                        Button(onClick = model::applyCalibration, enabled = !state.active && !ui.busy,
                            modifier = Modifier.heightIn(min = RhythmTokens.TouchMinimum).testTag("rhythm-calibration-apply")) { Text("Usar ajuste y volver") }
                    }
                    if (!state.active) OutlinedButton(onClick = model::startCalibration, enabled = !ui.busy,
                        modifier = Modifier.heightIn(min = RhythmTokens.TouchMinimum).testTag("rhythm-calibration-start")) {
                        Text(if (state.finished || state.error != null) "Repetir calibración" else "Iniciar calibración")
                    }
                    Text("Se conserva mientras la app siga abierta. Recalibrá al cambiar de salida de audio. No modifica partidas anteriores ni desbloquea niveles.", color = RhythmTokens.TextSecondary)
                }
                if (state.active) RhythmTapPad("TAP", true, model::tapAtUptimeMillis, model::tapNow, "rhythm-calibration-tap")
                TextButton(onClick = model::closeCalibration,
                    modifier = Modifier.heightIn(min = RhythmTokens.TouchMinimum).testTag("rhythm-calibration-close")) {
                    Text(if (state.active) "Cancelar y volver" else "Volver sin cambiar el ajuste")
                }
            }
        }
    }
}
