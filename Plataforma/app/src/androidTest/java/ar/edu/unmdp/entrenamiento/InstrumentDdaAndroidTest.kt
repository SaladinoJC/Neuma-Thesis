package ar.edu.unmdp.entrenamiento

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.semantics.SemanticsProperties
import org.junit.Rule
import org.junit.Test

class InstrumentDdaAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @org.junit.Before
    fun awaitAppWindow() {
        compose.awaitGardenMenu()
        compose.waitForIdle()
    }


    @Test
    fun inspectorReproduceRecuperacionSinCambiarLaPartidaReal() {
        compose.openMenuDestination("game-instruments")
        compose.waitUntil(10_000) { compose.onAllNodesWithTag("dda-open").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("dda-open").performScrollTo().performClick()
        compose.onNodeWithTag("dda-simulated").performClick()
        compose.onNodeWithTag("dda-level-C").performClick()
        compose.onNodeWithTag("dda-scenario-RECOVERY").performScrollTo().performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithTag("dda-selected-round").fetchSemanticsNodes().isNotEmpty() }
        repeat(7) { compose.onNodeWithTag("dda-previous-decision").performScrollTo().performClick() }
        compose.onNodeWithTag("dda-applied").performScrollTo().assertTextContains("3 → 2", substring = true)
        repeat(3) { compose.onNodeWithTag("dda-next-decision").performScrollTo().performClick() }
        compose.onNodeWithTag("dda-applied").performScrollTo().assertTextContains("2 → 3", substring = true)
        compose.onNodeWithTag("dda-reason").assertTextContains("próxima pregunta", substring = true)
        compose.onNodeWithTag("dda-close").performClick()
        compose.onNodeWithText("Jugar instrumentos").performScrollTo().performClick()
        awaitAnswer()
        compose.onNodeWithText("2 escuchas restantes").assertIsDisplayed()
        compose.onNodeWithText("Nivel A").assertIsDisplayed()
        compose.onNodeWithTag("instrument-option-0").performScrollTo().performClick()
        compose.onNodeWithTag("dda-open").performScrollTo().performClick()
        compose.onNodeWithTag("dda-reason").performScrollTo().assertTextContains("tres respuestas", substring = true)
        compose.onNodeWithTag("dda-close").performClick()
        compose.onNodeWithContentDescription("Volver al menú").performClick()
    }

    @Test
    fun escenariosDeAciertosAyudadosYAlternanciaMantienenLaAyuda() {
        compose.openMenuDestination("game-instruments")
        compose.waitUntil(10_000) { compose.onAllNodesWithTag("dda-open").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("dda-open").performScrollTo().performClick()
        compose.onNodeWithTag("dda-simulated").performClick()
        for (scenario in listOf("HELPED", "ALTERNATING")) {
            compose.onNodeWithTag("dda-scenario-$scenario").performScrollTo().performClick()
            compose.waitUntil(10_000) { compose.onAllNodesWithTag("dda-selected-round").fetchSemanticsNodes().isNotEmpty() }
            repeat(7) { compose.onNodeWithTag("dda-previous-decision").performScrollTo().performClick() }
            compose.onNodeWithTag("dda-applied").performScrollTo().assertTextContains("3 → 3", substring = true)
        }
        compose.onNodeWithTag("dda-close").performClick()
        compose.onNodeWithContentDescription("Volver al menú").performClick()
    }

    private fun awaitAnswer() {
        compose.waitUntil(30_000) {
            compose.onAllNodesWithTag("instrument-option-0").fetchSemanticsNodes().any { !it.config.contains(SemanticsProperties.Disabled) }
        }
    }
}
