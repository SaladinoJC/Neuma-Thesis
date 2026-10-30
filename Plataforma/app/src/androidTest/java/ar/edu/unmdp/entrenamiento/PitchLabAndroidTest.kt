package ar.edu.unmdp.entrenamiento

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import ar.edu.unmdp.entrenamiento.storage.Demonstration
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class PitchLabAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @Test fun laboratorioEjecutaBancoSinMicrofonoYExportaOrigenSeparado() {
        compose.awaitGardenMenu()
        compose.openMenuDestination("data-open")
        compose.onNodeWithTag("pitch-lab-open").performScrollTo().performClick()
        compose.onNodeWithTag("lab-run").performClick()
        compose.waitUntil(60_000) { compose.onAllNodesWithTag("lab-summary").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("lab-summary").assertTextContains("83 casos", substring = true)
        val exported = runBlocking { Json.parseToJsonElement(Demonstration(compose.activity.filesDir).export("test")).jsonObject }
        val report = exported.getValue("syntheticPitchTests").jsonArray.last().jsonObject
        assertEquals("synthetic", report.getValue("origin").jsonPrimitive.content)
        assertEquals(83, report.getValue("cases").jsonArray.size)
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("lab-summary").assertTextContains("83 casos", substring = true)
    }
}
