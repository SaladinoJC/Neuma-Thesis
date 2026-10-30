package ar.edu.unmdp.entrenamiento

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import ar.edu.unmdp.entrenamiento.simon.*
import ar.edu.unmdp.entrenamiento.storage.Demonstration
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import java.io.File

class HistoryAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @Test fun paginaYDetalleSeConservanAlRecrearSinBorrarResultados() {
        compose.awaitGardenMenu()
        runBlocking {
            Demonstration(compose.activity.filesDir).reset()
            val results = JsonSimonResults(File(compose.activity.filesDir, "simon-results.json"))
            repeat(26) { results.save(SimonResult(it, listOf(0), 0, id = "history-$it", finishedAtMillis = it.toLong())) }
        }
        compose.openMenuDestination("history-open")
        compose.waitUntil(10_000) { compose.onAllNodesWithText("20 de 26 resultados").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("history-list").performScrollToNode(hasTestTag("history-more"))
        compose.onNodeWithTag("history-more").performClick()
        compose.onNodeWithTag("history-count").assertTextEquals("26 de 26 resultados")
        compose.onNodeWithTag("history-list").performScrollToNode(hasTestTag("history-row-SIMON:history-25"))
        compose.onNodeWithTag("history-row-SIMON:history-25").performClick()
        compose.onNodeWithText("Longitud máxima completada: 25").assertIsDisplayed()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Longitud máxima completada: 25").assertIsDisplayed()
        compose.onNodeWithText("Volver al historial").performClick()
        compose.onNodeWithTag("history-game-VOICE").performClick()
        compose.onNodeWithTag("history-count").assertTextEquals("0 de 0 resultados")
    }
}
