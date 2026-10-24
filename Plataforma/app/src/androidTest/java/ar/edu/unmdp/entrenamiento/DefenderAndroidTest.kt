package ar.edu.unmdp.entrenamiento

import android.graphics.Bitmap
import android.view.View
import androidx.compose.ui.platform.LocalView
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.test.platform.app.InstrumentationRegistry
import ar.edu.unmdp.entrenamiento.defender.*
import ar.edu.unmdp.entrenamiento.ui.DefenderApp
import ar.edu.unmdp.entrenamiento.ui.NoteStaff
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File

class DefenderAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun disparoPorPulsacionAciertaUnaVezYLimpiaSeleccion() {
        var now = 0L
        val model = DefenderViewModel(clock = { now })
        lateinit var hostView: View
        compose.setContent {
            hostView = LocalView.current
            val ui by model.ui.collectAsStateWithLifecycle()
            DefenderApp(ui, model) { model.home() }
        }
        compose.onNodeWithTag("defender-note-sound").performScrollTo().performClick()
        compose.onNodeWithTag("defender-effects-sound").performScrollTo().performClick()
        compose.onNodeWithTag("defender-start").performScrollTo().performClick()
        val target = requireNotNull(model.ui.value.game.enemy).note.name
        compose.onNodeWithTag("defender-name-${target.name}").performClick()
        // Leave source-time margin for the real down-to-dispatch delay in firePointer.
        compose.runOnIdle { now = 1000L }
        tapWithDeviceUptime(hostView, compose.onNodeWithTag("defender-fire").fetchSemanticsNode().boundsInRoot.center)
        compose.onNodeWithTag("defender-score").assertTextEquals("100 pts")
        compose.onNodeWithTag("defender-fire").assertIsNotEnabled()
        compose.onNodeWithTag("defender-target").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(1, model.ui.value.game.results.size)
            assertEquals(0, model.ui.value.game.failedShots)
            assertNull(model.ui.value.game.selected)
            model.home()
        }
    }
    @Test fun partidaVerticalCompletaPausaOcultaContenidoYRevisaTresLlegadas() {
        var now = 0L
        val model = DefenderViewModel(clock = { now })
        compose.setContent {
            val ui by model.ui.collectAsStateWithLifecycle()
            DefenderApp(ui, model) { model.home() }
        }
        compose.onNodeWithTag("defender-start").performScrollTo().performClick()
        compose.onNodeWithTag("defender-target").assertIsDisplayed()
        screenshot("partida")
        compose.onNodeWithTag("defender-name-DO").performClick()
        compose.onNodeWithTag("defender-pause").performClick()
        compose.onNodeWithTag("defender-paused").assertIsDisplayed()
        compose.onNodeWithTag("defender-target").assertDoesNotExist()
        compose.onNodeWithTag("defender-fire").assertDoesNotExist()
        compose.runOnIdle { now = 50_000 }
        compose.onNodeWithTag("defender-resume").performClick()
        compose.onNodeWithTag("defender-lives").assertTextEquals("♥ 3")
        compose.runOnIdle { now = 60_000; model.tick() }
        compose.onNodeWithTag("defender-lives").assertTextEquals("♥ 2")
        compose.onNodeWithTag("defender-solution").assertIsDisplayed()
        screenshot("solucion")
        compose.runOnIdle { now = 84_000; model.tick() }
        compose.onNodeWithTag("defender-summary").assertTextEquals("Base alcanzada")
        compose.onNodeWithText("3 llegadas · 0 disparos fallidos").assertExists()
        screenshot("resumen")
        compose.runOnIdle { model.home() }
    }

    @Test fun extremosDeSolTienenClaveVectorialYAudioEnOctavaReal() = runBlocking {
        compose.setContent {
            Column {
                NoteStaff(DefenderNote(DefenderName.MI, 4), Modifier.width(200.dp).height(130.dp).testTag("staff-mi4"))
                NoteStaff(DefenderNote(DefenderName.FA, 5), Modifier.width(200.dp).height(130.dp).testTag("staff-fa5"))
            }
        }
        compose.onNodeWithTag("staff-mi4").assertContentDescriptionEquals("Pentagrama en clave de sol")
        compose.onNodeWithTag("staff-fa5").assertIsDisplayed()
        screenshot("extremos-sol")
        val audio = AndroidDefenderAudio()
        assertTrue(audio.note(64) >= 26460)
        assertTrue(audio.note(77) >= 26460)
        for (effect in DefenderEffect.entries) assertTrue(audio.effect(effect) >= 4410)
    }

    private fun screenshot(name: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val directory = File(context.getExternalFilesDir(null), "defender-d1").apply { mkdirs() }
        File(directory, "$name.png").outputStream().use {
            compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}
