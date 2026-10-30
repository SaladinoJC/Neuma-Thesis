package ar.edu.unmdp.entrenamiento

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.semantics.SemanticsProperties
import ar.edu.unmdp.entrenamiento.voice.AndroidReferencePlayer
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class VoiceReferenceAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @Test fun referenciaRealAvanzaFramesYLiberaLaSalida() = runBlocking {
        val player = AndroidReferencePlayer()
        assertTrue(player.play(48) >= 26460)
        assertTrue(player.play(69) >= 26460)
    }
    @Test fun objetivoDeshabilitaMicrofonoMientrasSuenaYGraficaNoInventaNota() {
        compose.awaitGardenMenu()
        compose.openMenuDestination("game-voice")
        compose.onNodeWithTag("voice-reference").performScrollTo().performClick()
        compose.onNodeWithTag("voice-start").assertIsNotEnabled()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithTag("voice-start").fetchSemanticsNodes().any { !it.config.contains(SemanticsProperties.Disabled) }
        }
        compose.onNodeWithTag("voice-plot").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("voice-meter").assertContentDescriptionEquals("Sin estimación actual")
    }
}
