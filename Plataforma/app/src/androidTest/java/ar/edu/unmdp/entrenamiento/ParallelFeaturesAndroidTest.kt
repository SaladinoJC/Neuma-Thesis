package ar.edu.unmdp.entrenamiento

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import ar.edu.unmdp.entrenamiento.simon.JsonSimonResults
import ar.edu.unmdp.entrenamiento.simon.SimonResult
import ar.edu.unmdp.entrenamiento.storage.Demonstration
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.File

/** Verifica la integración de los tres módulos sobre el perfil existente. */
class ParallelFeaturesAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun nuevosFrentesVuelvenAlMenuSinModificarHistorialesPrevios() {
        compose.waitUntil(15_000) { compose.activity.hasWindowFocus() }
        val data = Demonstration(compose.activity.filesDir)
        val before = runBlocking {
            data.reset()
            JsonSimonResults(File(compose.activity.filesDir, "simon-results.json"))
                .save(SimonResult(1, listOf(0), 0, id = "before-parallel-features"))
            Json.parseToJsonElement(data.export("integration-before")).jsonObject
        }
        listOf("game-tuner", "game-rhythm", "game-defender").forEach { tag ->
            compose.onNodeWithTag(tag).performScrollTo().performClick()
            compose.waitForIdle()
            assertEquals(android.content.res.Configuration.ORIENTATION_PORTRAIT,
                compose.activity.resources.configuration.orientation)
            compose.runOnUiThread { compose.activity.onBackPressedDispatcher.onBackPressed() }
            compose.onNodeWithTag(tag).performScrollTo().assertIsDisplayed()
        }
        val after = runBlocking { Json.parseToJsonElement(data.export("integration-after")).jsonObject }
        listOf("simon", "instruments", "voice", "syntheticPitchTests", "simonProgress", "instrumentProgress").forEach { key ->
            assertEquals("La navegación cambió $key", before[key], after[key])
        }
    }
}
