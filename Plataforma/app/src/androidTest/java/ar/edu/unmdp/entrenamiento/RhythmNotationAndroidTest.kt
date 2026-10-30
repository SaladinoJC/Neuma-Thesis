package ar.edu.unmdp.entrenamiento

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import ar.edu.unmdp.entrenamiento.rhythm.*
import ar.edu.unmdp.entrenamiento.ui.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File

/** Exporta ejemplos reales del generador para inspección visual, sin comparar píxeles. */
class RhythmNotationAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun ejemplosCanonicosDeCadaFiguraEnLasMetricasCompatibles() {
        val categories = RhythmCategory.entries.filter { it !in setOf(RhythmCategory.QUARTER, RhythmCategory.EIGHTHS) }
        val fixtures = categories.associateWith { category ->
            (2..4).filter { category != RhythmCategory.WHOLE || it == 4 }.map { meter ->
                RhythmSession().apply { generate(setOf(category), 12L, meter) }.state.also {
                    assertNotNull("$category $meter/4: ${it.error}", it.score)
                }
            }
        }
        var selected by mutableStateOf(categories.first())
        compose.setContent {
            MaterialTheme(colorScheme = darkColorScheme(onSurface = SoftWhite)) {
                Column(Modifier.fillMaxSize().background(Night).testTag("notation").padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("${selected.code} · ${selected.label}", style = MaterialTheme.typography.titleLarge, color = SoftWhite)
                    Text("Blanco: célula elegida. Gris: conexión.", color = SoftWhite)
                    fixtures.getValue(selected).forEach { state ->
                        val score = requireNotNull(state.score)
                        val bar = score.bars.first { it.occurrences.any { cell -> cell.target } }
                        Text("${score.beatsPerBar}/4 · compás ${bar.index + 1}", color = SoftWhite)
                        RhythmBarDrawing(bar, score, state, Modifier.fillMaxWidth().height(100.dp))
                    }
                }
            }
        }
        for (category in categories) {
            compose.runOnIdle { selected = category }
            saveRhythmScreenshot(category.code, compose.onNodeWithTag("notation").captureToImage().asAndroidBitmap())
        }
    }
}

internal fun saveRhythmScreenshot(name: String, bitmap: Bitmap) {
    val directory = File(InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null), "r2-notation")
    assertTrue(directory.isDirectory || directory.mkdirs())
    File(directory, "$name.png").outputStream().use { output ->
        assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output))
    }
}
