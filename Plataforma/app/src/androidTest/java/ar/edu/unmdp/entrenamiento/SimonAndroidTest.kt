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
import org.junit.Rule
import org.junit.Test

class SimonAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun abreElMenuYPermitePrepararSimonSinIniciarPartida() {
        compose.onNodeWithText("Entrená tu oído").assertIsDisplayed()
        compose.onNodeWithTag("game-simon").performClick()
        compose.onNodeWithText("¿Cómo se juega?").assertIsDisplayed()
        compose.onNodeWithText("Jugar Simón").assertIsDisplayed()
        compose.onNodeWithText("Volver al menú").performClick()
        compose.onNodeWithText("Entrená tu oído").assertIsDisplayed()
    }

    @Test
    fun elPianoCromaticoRespondeAlPresionarLaTeclaSinEsperarASoltarla() {
        compose.onNodeWithTag("game-simon").performClick()
        compose.onNodeWithTag("scale-CHROMATIC").performClick()
        compose.onNodeWithText("Jugar Simón").performClick()
        compose.waitUntil(10_000) {
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
        for (midi in 60..82) {
            val rendered = player.play(midi)
            assertTrue("El piano OGG MIDI $midi debe reproducir al menos 26460 frames (600 ms a 44,1 kHz)", rendered >= 26_460)
        }
    }
}
