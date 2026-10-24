package ar.edu.unmdp.entrenamiento.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import ar.edu.unmdp.entrenamiento.tuner.*
import java.util.Locale

@Composable
fun GuidedTunerPanel(ui: TunerUiState, model: TunerViewModel) {
    val configuration = ui.configuration.configuration ?: return
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TunerMode.entries.forEach { mode ->
            FilterChip(selected = configuration.mode == mode, enabled = !ui.configurationBusy,
                onClick = { model.mode(mode) }, label = { Text(if (mode == TunerMode.FREE) "Libre" else "Guiado") },
                modifier = Modifier.testTag("tuner-mode-${mode.name}"))
        }
    }
    if (configuration.mode != TunerMode.GUIDED) return
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Switch(checked = configuration.finalReview, onCheckedChange = model::finalReview,
            enabled = !ui.configurationBusy, modifier = Modifier.testTag("tuner-final-review"))
        Text("Revisión final · segunda pasada")
    }
    val guided = ui.guided
    Text(when (guided.phase) {
        GuidedPhase.READY -> "Prepará la guitarra y elegí afinación y precisión. Al iniciar, seguí sexta → primera."
        GuidedPhase.FIRST_PASS -> "Primera pasada · cuerda ${ui.tuning.fixedString}"
        GuidedPhase.REVIEW -> "Revisión final · cuerda ${ui.tuning.fixedString}"
        GuidedPhase.CORRECTION -> "Corregir pendientes · cuerda ${ui.tuning.fixedString}"
        GuidedPhase.COMPLETE -> if (guided.checked) "Conjunto comprobado" else "Primera pasada completa · sin revisión final"
        GuidedPhase.INTERRUPTED -> "Recorrido interrumpido · iniciar comienza uno nuevo"
    }, modifier = Modifier.testTag("tuner-guided-phase"))
    if (guided.farWarning) Text("Muy lejos, revisar cuerda correcta", color = MaterialTheme.colorScheme.error,
        modifier = Modifier.testTag("tuner-guided-warning"))
    if (guided.phase == GuidedPhase.REVIEW) {
        OutlinedButton(onClick = model::leavePending, modifier = Modifier.testTag("tuner-guided-pending")) {
            Text("Dejar pendiente y continuar")
        }
    }
    if (guided.firstPass.isNotEmpty() || guided.review.isNotEmpty()) {
        Text("Resumen del recorrido · ${guided.setup?.guitar?.name} · ${guided.setup?.tuning?.name}",
            style = MaterialTheme.typography.titleMedium, modifier = Modifier.testTag("tuner-guided-summary"))
        Text("Solo en memoria. Primera pasada y revisión se conservan separadas.", color = Muted)
        listOf("Primera pasada" to guided.firstPass, "Revisión" to guided.review, "Correcciones" to guided.corrections).forEach { (title, results) ->
            if (results.isNotEmpty()) {
                Text(title)
                results.forEach { result ->
                    val deviation = result.cents?.let { String.format(Locale.ROOT, "%+.1f cents", it) } ?: "sin lectura válida"
                    Text("Cuerda ${result.number} · ${noteName(result.midi)} · ${if (result.passed) "aprobada" else "pendiente"} · $deviation")
                }
            }
        }
    }
}
