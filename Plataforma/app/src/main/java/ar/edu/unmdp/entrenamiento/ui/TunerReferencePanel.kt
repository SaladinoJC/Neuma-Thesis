package ar.edu.unmdp.entrenamiento.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import ar.edu.unmdp.entrenamiento.tuner.*

/** La referencia pertenece a la cuerda objetivo; mismo vocabulario y controles que el afinador. */
@Composable
fun TunerReferencePanel(ui: TunerUiState, model: TunerViewModel) {
    val configuration = ui.configuration.configuration ?: return
    Text("Referencia sonora", style = MaterialTheme.typography.titleMedium)
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        ReferenceTimbre.entries.forEach { timbre ->
            FilterChip(selected = configuration.referenceTimbre == timbre,
                enabled = !ui.configurationBusy && !ui.referenceBusy,
                onClick = { model.referenceTimbre(timbre) }, label = { Text(timbre.label) },
                modifier = Modifier.testTag("tuner-timbre-${timbre.name}"))
        }
    }
    val target = ui.tuning.targetMidi
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = model::playReference,
            enabled = !ui.referenceBusy && !ui.configurationBusy && target in tunerTargetRange,
            modifier = Modifier.testTag("tuner-reference")) {
            Text(if (ui.referenceBusy) "Reproduciendo · esperá" else target?.let { "Escuchar ${noteName(it)}" } ?: "Elegí una cuerda")
        }
        if (ui.referenceBusy) TextButton(onClick = model::stop, modifier = Modifier.testTag("tuner-reference-cancel")) { Text("Cancelar") }
    }
    if (ui.referenceBusy) Text("Micrófono en pausa durante la referencia", modifier = Modifier.testTag("tuner-reference-status"))
    else if (target == null) Text("Fijá una cuerda o iniciá Guiado para elegir el objetivo.", color = Muted)
}
