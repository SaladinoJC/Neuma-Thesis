package ar.edu.unmdp.entrenamiento.ui

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

@Composable
fun DemonstrationApp(ui: DemonstrationUi, model: DemonstrationViewModel, onHome: () -> Unit) {
    var confirm by remember { mutableStateOf(false) }
    val resolver = LocalContext.current.applicationContext.contentResolver
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        model.export(if (uri == null) null else ({ resolver.openOutputStream(uri, "wt") }))
    }
    LaunchedEffect(ui.pickerReady) {
        if (ui.pickerReady) { model.pickerLaunched(); picker.launch("entrenamiento-auditivo-${System.currentTimeMillis()}.json") }
    }
    MaterialTheme(colorScheme = darkColorScheme(primary = Pink, background = Night, surface = Panel)) {
        BackHandler { if (!ui.busy) onHome() }
        Scaffold { padding ->
            Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)) {
                TextButton(onClick = onHome, enabled = !ui.busy) { Text("Volver al menú") }
                Text("Datos y demostración", style = MaterialTheme.typography.headlineMedium)
                Text("Recorrido: Simón · instrumentos automáticos · panel DDA · voz · exportación. El progreso se conserva al cerrar.")
                Text("Escenarios de desbloqueo", style = MaterialTheme.typography.titleLarge)
                Text("DATOS SIMULADOS · Se ejecutan en memoria, sin audio ni cambios en tu perfil.")
                OutlinedButton(onClick = { model.simulate(false) }, enabled = !ui.busy, modifier = Modifier.testTag("progress-two-high")) { Text("Dos partidas altas en ambos juegos") }
                OutlinedButton(onClick = { model.simulate(true) }, enabled = !ui.busy, modifier = Modifier.testTag("progress-recovery")) { Text("Alta, baja y recuperación de racha") }
                ui.simulation?.let { simulation ->
                    Text("SIMULADO · ${simulation.scenario}")
                    Text("Simón: nivel ${simulation.simonProgress.level+1}, racha ${simulation.simonProgress.streak}/2", modifier = Modifier.testTag("simulated-simon-progress"))
                    Text("Instrumentos: nivel ${simulation.instrumentProgress.level+1}, racha ${simulation.instrumentProgress.streak}/2")
                    simulation.simon.forEachIndexed { i, result ->
                        Text("Simón ${i+1}: ${result.completedLength} notas · ${result.origin}")
                        result.mastery?.let { MasteryButton(it) }
                    }
                    simulation.instruments.forEachIndexed { i, result ->
                        Text("Instrumentos ${i+1}: ${result.answers.count { it.correct }}/10 · ${result.origin}")
                        result.mastery?.let { MasteryButton(it) }
                    }
                }
                Text("Exportar resultados", style = MaterialTheme.typography.titleLarge)
                Text("Incluye partidas, progreso, decisiones, configuración y mediciones de voz; el escenario simulado visible va separado. Elegí dónde guardar el JSON.")
                Button(onClick = model::prepareExport, enabled = !ui.busy, modifier = Modifier.testTag("data-export")) { Text("Exportar JSON") }
                Text("Reiniciar perfil local", style = MaterialTheme.typography.titleLarge)
                Text("Borra historiales, mediciones, rachas y desbloqueos de esta app. Los archivos ya exportados se conservan.")
                OutlinedButton(onClick = { confirm = true }, enabled = !ui.busy, modifier = Modifier.testTag("data-reset")) { Text("Reiniciar datos") }
                if (ui.busy) CircularProgressIndicator()
                ui.message?.let { Text(it, modifier = Modifier.testTag("data-message")) }
            }
        }
        if (confirm) AlertDialog(onDismissRequest = { confirm = false }, title = { Text("¿Reiniciar todos los datos locales?") },
            text = { Text("Se borrarán partidas, mediciones y progreso. Esta acción no se puede deshacer.") },
            confirmButton = { TextButton(onClick = { confirm = false; model.reset() }, modifier = Modifier.testTag("data-reset-confirm")) { Text("Borrar y reiniciar") } },
            dismissButton = { TextButton(onClick = { confirm = false }) { Text("Cancelar") } })
    }
}
