package ar.edu.unmdp.entrenamiento.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun PitchLabApp(ui: PitchLabUi, model: PitchLabViewModel, onHome: () -> Unit) {
    val resolver = LocalContext.current.applicationContext.contentResolver
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/markdown")) { uri ->
        model.export(if (uri == null) null else ({ resolver.openOutputStream(uri, "wt") }))
    }
    LaunchedEffect(ui.markdownReady) { if (ui.markdownReady) { model.pickerLaunched(); picker.launch("laboratorio-yin-${System.currentTimeMillis()}.md") } }
    BackHandler { if (!ui.exporting) { model.cancel(); onHome() } }
    MaterialTheme(colorScheme = darkColorScheme(primary = Pink, background = Night, surface = Panel)) {
        Scaffold { padding -> Column(Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TextButton(onClick = { model.cancel(); onHome() }, enabled = !ui.exporting) { Text("Volver a datos") }
            Text("Laboratorio YIN", style = MaterialTheme.typography.headlineMedium)
            Text("SEÑALES SINTÉTICAS · Sin micrófono ni audio guardado. Caracterización técnica; no voz de personas.")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = model::start, enabled = !ui.running && !ui.exporting, modifier = Modifier.testTag("lab-run")) { Text("Ejecutar 83 casos") }
                OutlinedButton(onClick = model::cancel, enabled = ui.running) { Text("Cancelar") }
            }
            if (ui.running) {
                LinearProgressIndicator(progress = { ui.completed.toFloat()/ui.total }, modifier = Modifier.fillMaxWidth())
                Text("${ui.completed}/${ui.total} casos")
            }
            ui.message?.let { Text(it, modifier = Modifier.testTag("lab-message")) }
            ui.report?.let { report ->
                Text("Último informe: ${report.cases.size} casos · ${report.cases.sumOf { it.windows.size }} ventanas", modifier = Modifier.testTag("lab-summary"))
                Text(report.environment)
                Text("Obligatorios: ${report.cases.count { it.requiredPassed == true }} cumplen · ${report.cases.count { it.requiredPassed == false }} fallan")
                OutlinedButton(onClick = model::prepareMarkdown, enabled = !ui.running && !ui.exporting) { Text("Exportar informe Markdown") }
                LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(report.cases, key = { it.fixture.id }) { result ->
                        Surface(color = Panel, modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(result.fixture.id)
                                Text("${result.windows.count { it.valid }}/${result.windows.size} válidas · Error máximo ${result.windows.mapNotNull { it.absoluteCents }.maxOrNull() ?: "sin referencia válida"} cents")
                                Text("${result.windows.count { it.octaveError }} errores de octava · ${result.requiredPassed?.let { if (it) "Cumple criterio" else "FALLA criterio" } ?: "Exploratorio"}")
                            }
                        }
                    }
                }
            }
        } }
    }
}
