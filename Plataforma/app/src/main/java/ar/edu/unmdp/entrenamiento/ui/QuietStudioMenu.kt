package ar.edu.unmdp.entrenamiento.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ar.edu.unmdp.entrenamiento.BuildConfig

@Composable
internal fun QuietStudioMenu(state: GardenMenuState, simon: SimonUiState,
    onSimon: () -> Unit, onInstruments: () -> Unit, onRhythm: () -> Unit,
    onDefender: () -> Unit, onVocal: () -> Unit, onTuner: () -> Unit,
    onGuided: () -> Unit, onTraining: () -> Unit, onHistory: () -> Unit,
    onVoice: () -> Unit, onData: () -> Unit, onRetry: () -> Unit) {
    var more by rememberSaveable { mutableStateOf(false) }
    QuietStudioTheme {
        Scaffold(containerColor = QuietTokens.Background, contentWindowInsets = WindowInsets.safeDrawing) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(GardenTokens.SpaceLg).testTag("quiet-menu"),
                verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceBase)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("neuma.", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.weight(1f))
                    Text("Quiet Studio", color = QuietTokens.TextSecondary, style = MaterialTheme.typography.bodySmall)
                }
                Text("Tu estudio musical", style = MaterialTheme.typography.headlineSmall)
                Text("Elegí qué querés practicar hoy.", color = QuietTokens.TextSecondary)
                Column(verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
                    QuietEntry("Simón", "Escuchá, recordá y repetí la secuencia.", 0, "game-simon", onSimon)
                    QuietEntry("Instrumentos", "Descubrí el timbre que está sonando.", 1, "game-instruments", onInstruments)
                    QuietEntry("Rhythm Tap", "Leé cuatro compases y encontrá el pulso.", 2, "game-rhythm", onRhythm)
                    QuietEntry("Defender", "Reconocé notas y acordes en el pentagrama.", 3, "game-defender", onDefender)
                    QuietEntry("Intervalos vocales", "Escuchá y cantá la distancia entre notas.", 4, "game-vocal-intervals", onVocal)
                }
                Text("Afinador", style = MaterialTheme.typography.titleMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm)) {
                    OutlinedButton(onClick = onTuner) { Text("Libre") }
                    OutlinedButton(onClick = onGuided) { Text("Guiado") }
                    OutlinedButton(onClick = onTraining) { Text("Oído") }
                }
                TextButton(onClick = { more = !more }, modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum)) {
                    Text(if (more) "Cerrar herramientas" else "Historial y herramientas")
                }
                if (more) {
                    TextButton(onClick = onHistory) { Text("Historial general") }
                    TextButton(onClick = onVoice) { Text("Prueba de voz") }
                    TextButton(onClick = onData) { Text("Datos de demostración") }
                    TunerOption("Reducir movimiento", state.reduceMotion, true, "quiet-reduce-motion") { state.reduceMotion = it }
                }
                simon.storageError?.let {
                    Text(it, color = QuietTokens.ErrorText)
                    OutlinedButton(onClick = onRetry) { Text("Reintentar lectura") }
                }
                Text("Neuma Quiet Studio · ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodySmall,
                    color = QuietTokens.TextSecondary)
            }
        }
    }
}

@Composable
private fun QuietEntry(title: String, description: String, motif: Int, tag: String, action: () -> Unit) {
    Surface(onClick = action, modifier = Modifier.fillMaxWidth().testTag(tag),
        color = QuietTokens.Surface, shape = RoundedCornerShape(GardenTokens.RadiusLarge)) {
        Row(Modifier.padding(GardenTokens.SpaceBase), horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceBase),
            verticalAlignment = Alignment.CenterVertically) {
            QuietMotif(motif, Modifier.size(42.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceXs)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(description, style = MaterialTheme.typography.bodyMedium, color = QuietTokens.TextSecondary)
            }
        }
    }
}

/** Abstract study objects, never substitutes for musical notation. Original scalable Canvas. */
@Composable
private fun QuietMotif(kind: Int, modifier: Modifier) {
    Canvas(modifier) {
        val ink = QuietTokens.Primary
        val stroke = 1.6.dp.toPx()
        when (kind) {
            0 -> repeat(4) { n ->
                val x = size.width * (.12f + n * .23f)
                drawLine(ink, Offset(x, size.height * .2f), Offset(x, size.height * .8f), stroke)
            }
            1 -> repeat(3) { n -> drawCircle(ink, size.width * (.16f + n * .13f), center, style = Stroke(stroke)) }
            2 -> {
                drawCircle(ink, size.width * .35f, center, style = Stroke(stroke))
                drawCircle(QuietTokens.Warning, size.width * .08f, center)
            }
            3 -> repeat(4) { n ->
                val y = size.height * (.2f + n * .2f)
                drawLine(ink, Offset(size.width * .12f, y), Offset(size.width * .88f, y), stroke)
            }
            else -> {
                drawCircle(ink, size.width * .12f, Offset(size.width * .2f, size.height * .7f), style = Stroke(stroke))
                drawCircle(ink, size.width * .12f, Offset(size.width * .8f, size.height * .3f), style = Stroke(stroke))
                drawLine(ink, Offset(size.width * .32f, size.height * .62f), Offset(size.width * .68f, size.height * .38f), stroke)
            }
        }
    }
}
