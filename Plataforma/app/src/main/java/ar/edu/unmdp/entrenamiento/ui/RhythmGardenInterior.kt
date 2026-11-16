package ar.edu.unmdp.entrenamiento.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import ar.edu.unmdp.entrenamiento.BuildConfig

/** Misma área segura en ambos temas; Quiet Studio conserva su superficie sin ornamentación. */
@Composable
internal fun RhythmPageScaffold(content: @Composable (PaddingValues) -> Unit) {
    GardenPageScaffold(RhythmTokens.Background, decorated = !BuildConfig.QUIET_STUDIO, content = content)
}

/** Terraza estática: el único seguimiento temporal lo realiza la notación real. */
@Composable
internal fun RhythmGardenTerrace(modifier: Modifier = Modifier) {
    Canvas(modifier.clearAndSetSemantics {}) {
        val w = size.width
        val h = size.height
        val hills = Path().apply {
            moveTo(0f, h * .67f)
            cubicTo(w * .16f, h * .18f, w * .30f, h * .50f, w * .47f, h * .42f)
            cubicTo(w * .72f, h * .16f, w * .84f, h * .33f, w, h * .55f)
            lineTo(w, h); lineTo(0f, h); close()
        }
        drawPath(hills, GardenTokens.SurfaceSelected)
        for (x in listOf(.16f, .32f, .68f, .84f)) drawLine(GardenTokens.Sage,
            Offset(w * x, h * .58f), Offset(w * x, h * .91f), 3.dp.toPx(), StrokeCap.Round)
        drawLine(GardenTokens.Moss, Offset(w * .06f, h * .55f), Offset(w * .94f, h * .55f), 3.dp.toPx(), StrokeCap.Round)
        drawLine(GardenTokens.Success, Offset(w * .04f, h * .94f), Offset(w * .96f, h * .94f), 4.dp.toPx(), StrokeCap.Round)
        // Atril vacío: la partitura se graba por separado desde los datos del ejercicio.
        val desk = Path().apply {
            moveTo(w * .42f, h * .16f); lineTo(w * .58f, h * .16f)
            lineTo(w * .60f, h * .50f); lineTo(w * .40f, h * .50f); close()
        }
        drawPath(desk, GardenTokens.Surface)
        drawPath(desk, GardenTokens.Primary, style = Stroke(1.5.dp.toPx()))
        drawLine(GardenTokens.Primary, Offset(w * .50f, h * .52f), Offset(w * .50f, h * .87f), 2.dp.toPx())
        drawLine(GardenTokens.Primary, Offset(w * .44f, h * .91f), Offset(w * .50f, h * .79f), 2.dp.toPx())
        drawLine(GardenTokens.Primary, Offset(w * .56f, h * .91f), Offset(w * .50f, h * .79f), 2.dp.toPx())
        for (side in listOf(.10f, .90f)) {
            val x = w * side
            drawLine(GardenTokens.Primary, Offset(x, h * .92f), Offset(x, h * .25f), 1.5.dp.toPx())
            for (index in 0..2) {
                val y = h * (.32f + index * .16f)
                val direction = if (index % 2 == 0) 1f else -1f
                val leaf = Path().apply {
                    moveTo(x, y)
                    cubicTo(x + direction * h * .32f, y - h * .25f,
                        x + direction * h * .35f, y + h * .05f, x, y)
                }
                drawPath(leaf, if (index % 2 == 0) GardenTokens.Moss else GardenTokens.Sage)
            }
            drawRect(GardenTokens.Warning, Offset(x - h * .10f, h * .83f), Size(h * .20f, h * .14f))
        }
    }
}
