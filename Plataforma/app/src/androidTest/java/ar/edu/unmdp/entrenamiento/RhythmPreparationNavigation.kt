package ar.edu.unmdp.entrenamiento

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule

/** Los recorridos antiguos siguen accediendo a las mismas opciones desde la preparación plegable. */
internal fun ComposeTestRule.openRhythmPreparationIfCollapsed() {
    if (onAllNodesWithText("Preparación y calibración").fetchSemanticsNodes().isNotEmpty())
        onNodeWithTag("rhythm-settings").performScrollTo().performClick()
}
