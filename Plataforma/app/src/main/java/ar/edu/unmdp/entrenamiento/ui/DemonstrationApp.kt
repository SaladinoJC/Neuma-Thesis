package ar.edu.unmdp.entrenamiento.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import ar.edu.unmdp.entrenamiento.storage.ProgressScenario
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun DemonstrationApp(ui: DemonstrationUi, model: DemonstrationViewModel, onHome: () -> Unit, onPitchLab: () -> Unit = {}) {
    var boundaries by rememberSaveable { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(ui.noticeId) {
        if (ui.noticePending && ui.message != null) {
            model.consumeNotice(ui.noticeId)
            snackbar.showSnackbar(ui.message, withDismissAction = true)
        }
    }
    var confirm by remember { mutableStateOf(false) }
    val resolver = LocalContext.current.applicationContext.contentResolver
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        model.export(if (uri == null) null else ({ resolver.openOutputStream(uri, "wt") }))
    }
    val recovery = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri ->
        model.exportRecovery(if (uri == null) null else ({ resolver.openOutputStream(uri, "wt") }))
    }
    LaunchedEffect(ui.recoveryReady) {
        if (ui.recoveryReady) { model.recoveryLaunched(); recovery.launch("recuperacion-${System.currentTimeMillis()}.zip") }
    }
    LaunchedEffect(ui.pickerReady) {
        if (ui.pickerReady) { model.pickerLaunched(); picker.launch("entrenamiento-auditivo-${System.currentTimeMillis()}.json") }
    }
    TunerGardenTheme {
        BackHandler { if (!ui.busy) onHome() }
        Scaffold(snackbarHost = { SnackbarHost(snackbar, Modifier.testTag("operation-snackbar")) }) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)) {
                TextButton(onClick = onHome, enabled = !ui.busy) { Text("Volver al menú") }
                Text("Datos y demostración", style = MaterialTheme.typography.headlineMedium)
                Text("Se conservan partidas, progreso, mediciones del laboratorio de voz, configuraciones, recorridos del afinador con seguimiento activo, Rhythm Tap, Defender y rondas de intervalos vocales. Las mediciones derivadas no contienen grabaciones de audio.")
                OutlinedButton(onClick = onPitchLab, enabled = !ui.busy, modifier = Modifier.testTag("pitch-lab-open")) { Text("Pruebas sintéticas de tono") }
                Text("Escenarios de desbloqueo", style = MaterialTheme.typography.titleLarge)
                Text("DATOS SIMULADOS · Se ejecutan en memoria, sin audio ni cambios en tu perfil.")
                OutlinedButton(onClick = { model.simulate(false) }, enabled = !ui.busy, modifier = Modifier.testTag("progress-two-high")) { Text("Dos partidas altas en ambos juegos") }
                OutlinedButton(onClick = { model.simulate(true) }, enabled = !ui.busy, modifier = Modifier.testTag("progress-recovery")) { Text("Alta, baja y recuperación de racha") }
                ProgressScenario.entries.drop(2).forEach { scenario ->
                    OutlinedButton(onClick = { model.simulateScenario(scenario) }, enabled = !ui.busy,
                        modifier = Modifier.testTag("progress-scenario-${scenario.name}")) { Text(scenario.label) }
                }
                OutlinedButton(onClick = model::runDdaBank, enabled = !ui.busy, modifier = Modifier.testTag("dda-bench-run")) { Text("Ejecutar banco DDA completo") }
                ui.benchmark?.let { report ->
                    Text("Último banco: ${report.simulations.size} escenarios · ${report.boundaries.size} fronteras · ${report.checks.count { it.passed }}/${report.checks.size} comprobaciones", modifier = Modifier.testTag("dda-bench-summary"))
                    Text("DATOS SIMULADOS · Las fronteras caracterizan reglas sin calibración pedagógica.")
                    report.checks.forEach { Text("${if (it.passed) "Cumple" else "FALLA"}: ${it.label} · ${it.observed}") }
                    TextButton(onClick = { boundaries = !boundaries }) { Text(if (boundaries) "Ocultar fronteras" else "Inspeccionar fronteras de dominio") }
                    if (boundaries) report.boundaries.forEach { case ->
                        Text("${case.game} · Nivel ${case.level+1} · ${case.description}")
                        MasteryButton(case.mastery)
                    }
                }
                ui.simulation?.let { simulation ->
                    Text("SIMULADO · ${simulation.scenario}")
                    Text("Simón: nivel ${simulation.simonProgress.level+1}, racha ${simulation.simonProgress.streak}/2", modifier = Modifier.testTag("simulated-simon-progress"))
                    Text("Instrumentos: nivel ${simulation.instrumentProgress.level+1}, racha ${simulation.instrumentProgress.streak}/2")
                    simulation.steps.forEach { step ->
                        Text("${step.game} · Nivel ${step.level+1} · ${step.effectiveMode} · ${if (step.completed) "Completa" else "Interrumpida"}")
                        Text("Racha ${step.before.streak} → ${step.after.streak}; nivel ${step.before.level+1} → ${step.after.level+1}")
                        Text(step.reason + if (step.duplicateAttempted) " · Mismo identificador guardado dos veces" else "")
                    }
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
                Text("Incluye partidas, progreso, decisiones, configuración y mediciones de voz; el escenario visible y el banco DDA van separados. Elegí dónde guardar el JSON.")
                Button(onClick = model::prepareExport, enabled = !ui.busy, modifier = Modifier.testTag("data-export")) { Text("Exportar JSON") }
                Text("Si el JSON local no se puede leer, conservá una copia de los archivos originales antes de reiniciar. La copia ZIP no repara ni importa datos.")
                OutlinedButton(onClick = model::prepareRecovery, enabled = !ui.busy, modifier = Modifier.testTag("data-recovery")) { Text("Exportar originales para recuperación") }
                Text("Reiniciar perfil local", style = MaterialTheme.typography.titleLarge)
                Text("Borra historiales, mediciones, rachas, desbloqueos y configuraciones del afinador de esta app. Los archivos ya exportados se conservan.")
                OutlinedButton(onClick = { confirm = true }, enabled = !ui.busy, modifier = Modifier.testTag("data-reset")) { Text("Reiniciar datos") }
                if (ui.busy) CircularProgressIndicator()
                ui.message?.let { Text("Última operación", style = MaterialTheme.typography.labelLarge); Text(it, modifier = Modifier.testTag("data-message")) }
            }
        }
        if (confirm) AlertDialog(onDismissRequest = { confirm = false }, title = { Text("¿Reiniciar todos los datos locales?") },
            text = { Text("Se borrarán partidas, mediciones, progreso, historial del afinador, guardados pendientes, guitarras y afinaciones personalizadas. Esta acción no se puede deshacer.") },
            confirmButton = { TextButton(onClick = { confirm = false; model.reset() }, modifier = Modifier.testTag("data-reset-confirm")) { Text("Borrar y reiniciar") } },
            dismissButton = { TextButton(onClick = { confirm = false }) { Text("Cancelar") } })
    }
}
