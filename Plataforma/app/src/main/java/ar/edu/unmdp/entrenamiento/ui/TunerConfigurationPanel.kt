package ar.edu.unmdp.entrenamiento.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import ar.edu.unmdp.entrenamiento.tuner.*

@Composable
fun TunerConfigurationPanel(ui: TunerUiState, model: TunerViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var guitarName by remember { mutableStateOf("") }
    var editing by remember { mutableStateOf(false) }
    var editingId by remember { mutableStateOf<String?>(null) }
    var tuningName by remember { mutableStateOf("") }
    var savingTuning by remember { mutableStateOf(false) }
    val notes = remember { mutableStateListOf("Mi2", "La2", "Re3", "Sol3", "Si3", "Mi4") }
    LaunchedEffect(ui.configurationBusy, ui.configuration) {
        if (savingTuning && !ui.configurationBusy) {
            if (ui.configuration.error == null) editing = false
            savingTuning = false
        }
    }
    OutlinedButton(onClick = { expanded = !expanded }, modifier = Modifier.testTag("tuner-configuration-open")) {
        Text(if (expanded) "Cerrar configuración" else "Guitarras y afinaciones")
    }
    val configuration = ui.configuration.configuration
    ui.configuration.error?.let {
        Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("tuner-configuration-error"))
        TextButton(onClick = model::reload, enabled = !ui.configurationBusy) { Text("Reintentar configuración") }
    }
    if (ui.configurationBusy) Text("Guardando o leyendo configuración…")
    if (!expanded || configuration == null) return
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Guitarras", style = MaterialTheme.typography.titleMedium)
        configuration.guitars.forEach { guitar ->
            FilterChip(selected = guitar.id == configuration.currentGuitarId,
                enabled = !ui.configurationBusy, onClick = { model.selectGuitar(guitar.id) }, label = { Text(guitar.name) },
                modifier = Modifier.testTag("tuner-guitar-${guitar.id}"))
        }
        OutlinedTextField(value = guitarName, onValueChange = { guitarName = it }, label = { Text("Nombre de guitarra") },
            singleLine = true, modifier = Modifier.fillMaxWidth().testTag("tuner-guitar-name"))
        Button(onClick = { model.createGuitar(guitarName) }, enabled = !ui.configurationBusy,
            modifier = Modifier.testTag("tuner-guitar-create")) { Text("Crear guitarra") }
        Text("Afinación · sexta → primera", style = MaterialTheme.typography.titleMedium)
        configuration.tunings.forEach { tuning ->
            FilterChip(selected = tuning.id == configuration.currentGuitar.tuningId, enabled = !ui.configurationBusy,
                onClick = { model.selectTuning(tuning.id) }, label = { Text(tuning.name) }, modifier = Modifier.testTag("tuner-tuning-${tuning.id}"))
        }
        OutlinedButton(onClick = {
            editing = true; editingId = null; tuningName = ""
            notes.clear(); notes.addAll(configuration.currentTuning.notesSixToFirst.map(::noteName))
        }, enabled = !ui.configurationBusy, modifier = Modifier.testTag("tuner-custom-new")) { Text("Nueva afinación personalizada") }
        if (configuration.currentTuning.id in configuration.customTunings.map { it.id }) {
            OutlinedButton(onClick = {
                editing = true; editingId = configuration.currentTuning.id; tuningName = configuration.currentTuning.name
                notes.clear(); notes.addAll(configuration.currentTuning.notesSixToFirst.map(::noteName))
            }, enabled = !ui.configurationBusy, modifier = Modifier.testTag("tuner-custom-edit")) { Text("Editar afinación seleccionada") }
        }
        if (editing) {
            Text(if (editingId == null) "Nueva afinación" else "Editar afinación · se guardará una nueva revisión")
            OutlinedTextField(value = tuningName, onValueChange = { tuningName = it }, label = { Text("Nombre de afinación") },
                singleLine = true, modifier = Modifier.fillMaxWidth().testTag("tuner-custom-name"))
            notes.indices.forEach { index ->
                OutlinedTextField(value = notes[index], onValueChange = { notes[index] = it }, singleLine = true,
                    label = { Text("Cuerda ${6 - index} · nota y octava") },
                    modifier = Modifier.fillMaxWidth().testTag("tuner-custom-note-${6 - index}"))
            }
            Text("Seis alturas entre Do2 y Mi4. Ejemplos: Mi2, F#3, Sib3. Se respeta el orden de las cuerdas.", color = Muted)
            Button(onClick = { savingTuning = true; model.saveCustom(tuningName, notes.toList(), editingId) }, enabled = !ui.configurationBusy,
                modifier = Modifier.testTag("tuner-custom-save")) { Text("Guardar afinación") }
            ui.configuration.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("tuner-editor-error"))
            }
            TextButton(onClick = { editing = false }, enabled = !ui.configurationBusy) { Text("Cerrar editor") }
        }
        Text("Cambiar configuración detiene el micrófono. No se guardan resultados de práctica.", color = Muted)
    }
}
