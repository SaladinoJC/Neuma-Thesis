package ar.edu.unmdp.entrenamiento

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

/** Recorrido nuevo: el historial vuelve a la preparación de origen, sin iniciar partida. */
class InstrumentGardenAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun historialDesdeInstrumentosConservaElOrigenYLaSeleccionManual() {
        compose.awaitGardenMenu()
        compose.openMenuDestination("game-instruments")
        compose.onNodeWithTag("level-C").performScrollTo().performClick()
        compose.onNodeWithTag("instrument-history").performScrollTo().performClick()
        compose.onNodeWithTag("history-game-INSTRUMENTS").assertIsSelected()
        compose.onNodeWithText("Volver a Instrumentos").performClick()
        compose.onNodeWithTag("mode-manual").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("level-C").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("instrument-start").performScrollTo().assertIsEnabled()
        compose.onNodeWithContentDescription("Volver al menú").performClick()
    }
}
