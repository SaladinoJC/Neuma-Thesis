package ar.edu.unmdp.entrenamiento

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class DdaBenchAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @Test fun bancoPresentaNueveEscenariosYNoventaYTresFronteras() {
        compose.awaitGardenMenu()
        compose.openMenuDestination("data-open")
        compose.onNodeWithTag("dda-bench-run").performScrollTo().performClick()
        compose.waitUntil(30_000) { compose.onAllNodesWithTag("dda-bench-summary").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("dda-bench-summary").performScrollTo().assertTextContains("9 escenarios · 93 fronteras · 21/21", substring = true)
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("dda-bench-summary").performScrollTo().assertTextContains("21/21", substring = true)
    }
}
