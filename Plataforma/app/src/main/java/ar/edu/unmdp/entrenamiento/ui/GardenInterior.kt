package ar.edu.unmdp.entrenamiento.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.unit.dp
import ar.edu.unmdp.entrenamiento.progress.Progress

/** Papel compartido con áreas seguras; también admite la superficie lisa de una variante. */
@Composable
internal fun GardenPageScaffold(background: Color = GardenTokens.Background, decorated: Boolean = true,
    content: @Composable (PaddingValues) -> Unit) {
    Scaffold(containerColor = background, contentWindowInsets = WindowInsets.safeDrawing) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (decorated) GardenPageDecoration(Modifier.matchParentSize())
            content(PaddingValues(0.dp))
        }
    }
}

/** Papel y encuadernación; la decoración no contiene información musical. */
@Composable
internal fun GardenPageDecoration(modifier: Modifier = Modifier) {
    Canvas(modifier.clearAndSetSemantics {}) {
        drawRect(GardenTokens.Background)
        drawLine(GardenTokens.Border.copy(alpha = .20f), Offset(8.dp.toPx(), 0f),
            Offset(8.dp.toPx(), size.height), 1.dp.toPx())
        drawLine(GardenTokens.Surface, Offset(12.dp.toPx(), 0f),
            Offset(12.dp.toPx(), size.height), 2.dp.toPx())
    }
}

@Composable
internal fun GardenPaperPanel(modifier: Modifier = Modifier, color: Color = GardenTokens.Surface,
    content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier.fillMaxWidth(), color = color,
        shape = RoundedCornerShape(GardenTokens.RadiusLarge)) {
        Column(Modifier.padding(GardenTokens.SpaceBase),
            verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm), content = content)
    }
}

@Composable
internal fun GardenProgressSummary(progress: Progress, levels: List<String>) {
    GardenPaperPanel {
        Text("Tu recorrido", style = MaterialTheme.typography.titleMedium)
        Text("Desbloqueado: ${levels[progress.level]}")
        Text(if (progress.level == levels.lastIndex) "Llegaste al último nivel de esta actividad."
            else "Próximo: ${levels[progress.level + 1]} · ${if (progress.streak == 1) "Falta 1 partida" else "Faltan 2 partidas"} de dominio alto.",
            color = GardenTokens.TextSecondary)
        if (progress.level < levels.lastIndex) LinearProgressIndicator(
            progress = { progress.streak / 2f }, modifier = Modifier.fillMaxWidth(),
            color = GardenTokens.Primary, trackColor = GardenTokens.SurfaceSelected)
    }
}

@Composable
internal fun GardenLevelChoice(label: String, description: String, selected: Boolean,
    enabled: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    GardenChoiceFocus(Modifier.fillMaxWidth()) { interaction ->
        Surface(onClick = onClick, enabled = enabled, interactionSource = interaction,
            modifier = modifier.fillMaxWidth().semantics { this.selected = selected },
            shape = RoundedCornerShape(GardenTokens.RadiusLarge),
            color = if (!enabled) GardenTokens.DisabledSurface else if (selected) GardenTokens.SurfaceSelected else GardenTokens.Surface,
            border = BorderStroke(if (selected) 2.dp else 1.dp, GardenTokens.Border)) {
            Row(Modifier.padding(GardenTokens.SpaceBase).heightIn(min = GardenTokens.TouchMinimum),
                horizontalArrangement = Arrangement.spacedBy(GardenTokens.SpaceSm), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(GardenTokens.SpaceXs)) {
                    Text(label, style = MaterialTheme.typography.titleMedium,
                        color = if (enabled) GardenTokens.Text else GardenTokens.DisabledText)
                    Text(description, color = if (enabled) GardenTokens.TextSecondary else GardenTokens.DisabledText)
                }
                // Reservar siempre la misma caja evita saltos de geometría al seleccionar.
                Canvas(Modifier.size(GardenTokens.SpaceLg).clearAndSetSemantics {}) {
                    if (selected) {
                        val ink = if (enabled) GardenTokens.Primary else GardenTokens.DisabledText
                        drawLine(ink, Offset(size.width * .15f, size.height * .50f),
                            Offset(size.width * .40f, size.height * .75f), 2.dp.toPx(), StrokeCap.Round)
                        drawLine(ink, Offset(size.width * .40f, size.height * .75f),
                            Offset(size.width * .85f, size.height * .25f), 2.dp.toPx(), StrokeCap.Round)
                    }
                }
            }
        }
    }
}

@Composable
internal fun GardenChoiceFocus(modifier: Modifier = Modifier, content: @Composable (MutableInteractionSource) -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    Box(modifier.then(if (focused) Modifier.border(2.dp, GardenTokens.Focus,
        RoundedCornerShape(GardenTokens.RadiusLarge)) else Modifier).padding(GardenTokens.SpaceXs)) {
        content(interaction)
    }
}

/** Escenario vectorial propio, idéntico para todos los timbres. */
@Composable
internal fun GardenGreenhouse(modifier: Modifier = Modifier) {
    Canvas(modifier.clearAndSetSemantics {}) {
        val w = size.width
        val h = size.height
        val roof = Path().apply {
            moveTo(w * .10f, h * .92f)
            lineTo(w * .10f, h * .35f)
            cubicTo(w * .10f, -h * .06f, w * .90f, -h * .06f, w * .90f, h * .35f)
            lineTo(w * .90f, h * .92f)
            close()
        }
        drawPath(roof, GardenTokens.Audio.copy(alpha = .13f))
        drawPath(roof, GardenTokens.Moss, style = Stroke(2.dp.toPx()))
        for (x in listOf(.30f, .50f, .70f)) drawLine(GardenTokens.Moss.copy(alpha = .50f),
            Offset(w * x, h * .15f), Offset(w * x, h * .91f), 1.dp.toPx())
        for (y in listOf(.36f, .68f)) drawLine(GardenTokens.Moss.copy(alpha = .50f),
            Offset(w * .10f, h * y), Offset(w * .90f, h * y), 1.dp.toPx())
        drawLine(GardenTokens.Success, Offset(w * .06f, h * .94f),
            Offset(w * .94f, h * .94f), 5.dp.toPx(), StrokeCap.Round)
        greenhousePlant(w * .13f, h * .89f, h * .64f, false)
        greenhousePlant(w * .87f, h * .89f, h * .51f, true)
        // Roseta botánica, nunca una nota musical ni una pista del timbre.
        drawCircle(GardenTokens.Surface, h * .24f, Offset(w * .50f, h * .52f))
        drawCircle(GardenTokens.Success, h * .21f, Offset(w * .50f, h * .52f), style = Stroke(1.dp.toPx()))
        for (side in listOf(-1f, 1f)) {
            val leaf = Path().apply {
                moveTo(w * .50f, h * .65f)
                cubicTo(w * .50f + side * h * .26f, h * .56f,
                    w * .50f + side * h * .12f, h * .35f, w * .50f, h * .40f)
                cubicTo(w * .50f + side * h * .02f, h * .49f,
                    w * .50f + side * h * .06f, h * .57f, w * .50f, h * .65f)
            }
            drawPath(leaf, if (side < 0) GardenTokens.Sage else GardenTokens.Primary)
        }
    }
}

private fun DrawScope.greenhousePlant(x: Float, base: Float, height: Float, mirrored: Boolean) {
    val direction = if (mirrored) -1f else 1f
    drawLine(GardenTokens.Primary, Offset(x, base), Offset(x + direction * height * .10f, base - height),
        2.dp.toPx(), StrokeCap.Round)
    for (index in 0..3) {
        val y = base - height * (.20f + index * .19f)
        val side = if (index % 2 == 0) direction else -direction
        val leaf = Path().apply {
            moveTo(x, y)
            cubicTo(x + side * height * .40f, y - height * .02f,
                x + side * height * .32f, y - height * .33f, x, y)
        }
        drawPath(leaf, if (index % 2 == 0) GardenTokens.Sage else GardenTokens.Moss)
    }
    drawRoundRect(GardenTokens.Warning, Offset(x - height * .16f, base - height * .04f),
        Size(height * .32f, height * .24f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()))
}
