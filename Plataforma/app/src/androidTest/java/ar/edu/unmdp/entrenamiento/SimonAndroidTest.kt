package ar.edu.unmdp.entrenamiento

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
    fun iniciarAbandonarYReabrirConservaElResultadoInterrumpido() {
        compose.onNodeWithText("Jugar Simón").performClick()
        compose.onNodeWithText("Volver al menú").performClick()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Interrumpida").fetchSemanticsNodes().isNotEmpty()
        }
        compose.activityRule.scenario.recreate()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Interrumpida").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("Jugar Simón").assertIsDisplayed()
    }

    @Test
    fun responderConErrorMuestraResumenYConservaPartidaCompletaAlReabrir() {
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
    fun elDispositivoReproduceLosFramesDeUnaNotaReal() = runBlocking {
        val rendered = AndroidNotePlayer().play(60)
        assertTrue("AudioTrack debe avanzar 14400 frames para una nota de 300 ms a 48 kHz", rendered >= 14_400)
    }
}
