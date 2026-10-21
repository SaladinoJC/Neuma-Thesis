package ar.edu.unmdp.entrenamiento.ui

import androidx.compose.runtime.Composable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import ar.edu.unmdp.entrenamiento.voice.VoiceMeasurement

@Composable
internal fun VoicePlot(recent: List<VoiceMeasurement>, cents: Double?, running: Boolean) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("−100 cents", color = Muted); Text("Objetivo", color = Pink); Text("+100 cents", color = Muted)
    }
    Canvas(Modifier.fillMaxWidth().height(30.dp).testTag("voice-meter").semantics {
        contentDescription = cents?.let { "Desviación $it cents respecto del objetivo" } ?: "Sin estimación actual"
    }) {
        drawLine(Muted, Offset(0f, size.height/2), Offset(size.width, size.height/2), 2f)
        drawLine(Pink, Offset(size.width/2, 0f), Offset(size.width/2, size.height), 2f)
        cents?.takeIf { it.isFinite() }?.let {
            drawCircle(Lavender, 7.dp.toPx(), Offset(((it.coerceIn(-100.0, 100.0)+100)/200*size.width).toFloat().coerceIn(7.dp.toPx(), size.width-7.dp.toPx()), size.height/2))
        }
    }
    if (cents != null && kotlin.math.abs(cents) > 100) Text("Fuera de la escala gráfica ±100 cents; el número muestra la desviación completa.")
    Canvas(Modifier.fillMaxWidth().height(110.dp).testTag("voice-plot").semantics { contentDescription = "Evolución de cents; los huecos representan ausencia de tono" }) {
        drawRect(Panel)
        drawLine(Muted.copy(alpha = .5f), Offset(0f, size.height/2), Offset(size.width, size.height/2), 1f)
        val end = recent.lastOrNull()?.elapsedMillis ?: 0L
        val start = (end-10_000).coerceAtLeast(0)
        fun point(m: VoiceMeasurement): Offset? {
            val value = m.cents?.takeIf { m.valid && it.isFinite() } ?: return null
            return Offset(((m.elapsedMillis-start)/10_000f*size.width).coerceIn(0f, size.width),
                ((100-value.coerceIn(-100.0, 100.0))/200*size.height).toFloat())
        }
        recent.zipWithNext().forEach { (a, b) ->
            val first = point(a); val second = point(b)
            if (first != null && second != null && b.elapsedMillis-a.elapsedMillis <= 450)
                drawLine(Lavender, first, second, 2.dp.toPx())
        }
        if (running) recent.lastOrNull()?.let(::point)?.let { drawCircle(Pink, 4.dp.toPx(), it) }
    }
    Text(if (running) "Últimos diez segundos · Sin suavizado" else "Última captura · Los huecos indican ausencia de tono", color = Muted)
}
