package ar.edu.unmdp.entrenamiento

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.semantics.SemanticsProperties
import ar.edu.unmdp.entrenamiento.audio.AndroidNotePlayer
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import android.content.res.Configuration
import org.junit.Assert.assertEquals
import android.content.pm.ActivityInfo
import android.util.Log
import kotlin.system.measureTimeMillis
import androidx.compose.ui.semantics.SemanticsActions
import org.junit.Rule
import org.junit.Test

class SimonAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @org.junit.Before
    fun awaitAppWindow() {
        compose.waitUntil(15_000) { compose.activity.hasWindowFocus() }
        compose.waitForIdle()
    }


    @Test
    fun recrearLaActividadNoInterrumpeLaPartidaYSimonEntraHorizontal() {
        compose.onNodeWithTag("game-simon").performClick()
        compose.waitUntil(30_000) {
            compose.activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        }
        for (scale in listOf("PENTATONIC", "DIATONIC", "CHROMATIC")) {
            compose.onNodeWithTag("scale-$scale").assertIsDisplayed()
        }
        compose.onNodeWithText("Jugar Simón").assertIsDisplayed()
        assertTrue(!compose.onNodeWithTag("simon-viewport").fetchSemanticsNode().config.contains(SemanticsActions.ScrollBy))
        compose.onNodeWithText("Jugar Simón").performClick()
        compose.activityRule.scenario.recreate() // Durante la reproducción inicial.
        compose.waitUntil(30_000) {
            compose.onAllNodesWithTag("note-0").fetchSemanticsNodes().any {
                !it.config.contains(SemanticsProperties.Disabled)
            }
        }
        compose.activityRule.scenario.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        }
        compose.waitForIdle()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("note-0").assertIsDisplayed()
        assertTrue("Recrear por configuración conserva el turno de respuesta",
            !compose.onNodeWithTag("note-0").fetchSemanticsNode().config.contains(SemanticsProperties.Disabled))
        assertEquals(Configuration.ORIENTATION_LANDSCAPE, compose.activity.resources.configuration.orientation)
        val viewport = compose.onNodeWithTag("simon-viewport").fetchSemanticsNode().boundsInRoot
        val keyboard = compose.onNodeWithTag("piano-keyboard").fetchSemanticsNode().boundsInRoot
        assertTrue("El teclado entero entra en la pantalla", viewport.contains(keyboard.topLeft) &&
            keyboard.right <= viewport.right && keyboard.bottom <= viewport.bottom)
        compose.onNodeWithText("Volver al menú").assertIsDisplayed().performClick()
        compose.waitUntil(30_000) {
            compose.activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        }
    }

    @Test
    fun abreElMenuYPermitePrepararSimonSinIniciarPartida() {
        compose.onNodeWithText("Entrená tu oído").assertIsDisplayed()
        compose.onNodeWithTag("game-simon").performClick()
        compose.onNodeWithText("¿Cómo se juega?").assertIsDisplayed()
        compose.onNodeWithText("Jugar Simón").assertIsDisplayed()
        compose.onNodeWithText("Volver al menú").performClick()
        compose.waitUntil(10_000) { compose.activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT }
        compose.onNodeWithText("Entrená tu oído").assertIsDisplayed()
    }

    @Test
    fun elPianoCromaticoRespondeAlPresionarLaTeclaSinEsperarASoltarla() {
        compose.onNodeWithTag("game-simon").performClick()
        compose.onNodeWithTag("scale-CHROMATIC").performClick()
        compose.onNodeWithText("Jugar Simón").performClick()
        compose.waitUntil(30_000) {
            compose.onAllNodesWithTag("note-0").fetchSemanticsNodes().any {
                !it.config.contains(SemanticsProperties.Disabled)
            }
        }
        compose.onNodeWithTag("piano-keyboard").assertIsDisplayed()
        for (index in 0..11) compose.onNodeWithTag("note-$index").assertIsDisplayed()
        val blackKey = (0..11).first { index ->
            compose.onNodeWithTag("note-$index").fetchSemanticsNode()
                .config[SemanticsProperties.ContentDescription].any { it.contains("♯") }
        }
        compose.onNodeWithTag("note-$blackKey").performTouchInput { down(center) }
        try {
            compose.waitUntil(3000) {
                compose.onAllNodesWithText("Partida terminada").fetchSemanticsNodes().isNotEmpty() ||
                    compose.onAllNodesWithText("Ronda 2").fetchSemanticsNodes().isNotEmpty()
            }
        } finally {
            compose.onRoot().performTouchInput { up() }
        }
    }

    @Test
    fun iniciarAbandonarYReabrirConservaElResultadoInterrumpido() {
        compose.onNodeWithTag("game-simon").performClick()
        compose.onNodeWithText("Jugar Simón").performClick()
        compose.onNodeWithText("Volver al menú").performClick()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Interrumpida").fetchSemanticsNodes().isNotEmpty()
        }
        compose.activityRule.scenario.recreate()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Interrumpida").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("Entrená tu oído").assertIsDisplayed()
    }

    @Test
    fun responderConErrorMuestraResumenYConservaPartidaCompletaAlReabrir() {
        compose.onNodeWithTag("game-simon").performClick()
        compose.onNodeWithText("Jugar Simón").performClick()
        // La primera nota de la secuencia no cambia. Si 0 acierta, 1 falla
        // en el inicio de la siguiente ronda: no dependemos del azar interno.
        for (index in 0..1) {
            compose.waitUntil(10_000) {
                compose.onAllNodesWithText("Partida terminada").fetchSemanticsNodes().isNotEmpty() ||
                    compose.onAllNodesWithTag("note-$index").fetchSemanticsNodes().any {
                        !it.config.contains(SemanticsProperties.Disabled)
                    }
            }
            if (compose.onAllNodesWithText("Partida terminada").fetchSemanticsNodes().isNotEmpty()) break
            compose.onNodeWithTag("note-$index").performClick()
        }
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Partida terminada").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("Longitud máxima completada").assertIsDisplayed()
        compose.onNodeWithText("Volver al menú").performClick()
        compose.activityRule.scenario.recreate()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Completada").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun elDispositivoReproduceLosSamplesDePianoCompletos() = runBlocking {
        val player = AndroidNotePlayer(compose.activity.applicationContext)
        try {
            for (midi in 60..82) {
                val rendered = player.play(midi)
                assertTrue("El piano OGG MIDI $midi debe reproducir al menos 26460 frames (600 ms a 44,1 kHz)", rendered >= 26_460)
            }
        } finally { player.close() }
    }

    @Test
    fun elPianoPrecargadoIniciaRafagasSinEsperarCadaSample() = runBlocking {
        val player = AndroidNotePlayer(compose.activity.applicationContext)
        try {
            val prepareElapsed = measureTimeMillis { player.prepare((60..82).toList()) }
            val streams = mutableListOf<Int>()
            val elapsed = measureTimeMillis {
                repeat(3) { for (midi in 60..82) streams.add(player.feedback(midi)) }
            }
            assertEquals(69, streams.distinct().size)
            assertTrue(streams.all { it > 0 })
            Log.i("SimonVerification", "Precarga 23 notas: ${prepareElapsed}ms; 69 ataques: ${elapsed}ms; no mide latencia acústica")
            player.stopFeedback()
            assertTrue(player.feedback(60) > 0)
        } finally { player.close() }
    }
}
