package ar.edu.unmdp.entrenamiento

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import ar.edu.unmdp.entrenamiento.storage.Demonstration
import android.Manifest
import android.content.pm.PackageManager

class DemonstrationAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @org.junit.Before
    fun awaitAppWindow() {
        compose.waitUntil(15_000) { compose.activity.hasWindowFocus() }
        compose.waitForIdle()
    }


    @Test fun escenariosNoModificanPerfilYReinicioRequiereConfirmacion() {
        val data = Demonstration(compose.activity.filesDir)
        val before = runBlocking { Json.parseToJsonElement(data.export("test")).jsonObject }
        compose.onNodeWithTag("data-open").performScrollTo().performClick()
        compose.onNodeWithTag("progress-two-high").performScrollTo().performClick()
        compose.waitUntil(15_000) { compose.onAllNodesWithTag("simulated-simon-progress").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("simulated-simon-progress").performScrollTo().assertTextContains("nivel 2", substring = true)
        val after = runBlocking { Json.parseToJsonElement(data.export("test")).jsonObject }
        for (key in listOf("simon", "instruments", "voice", "simonProgress", "instrumentProgress")) assertEquals(before[key], after[key])
        compose.onNodeWithTag("data-reset").performScrollTo().performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.onNodeWithTag("data-reset").performScrollTo().performClick()
        compose.onNodeWithTag("data-reset-confirm").performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithTag("data-message").fetchSemanticsNodes().isNotEmpty() }
        val reset = runBlocking { Json.parseToJsonElement(data.export("test")).jsonObject }
        for (key in listOf("simon", "instruments", "voice")) assertTrue(reset.getValue(key).jsonArray.isEmpty())
        compose.onNodeWithText("Volver al menú").performScrollTo().performClick()
        compose.onNodeWithTag("game-simon").performScrollTo().performClick()
        compose.onNodeWithText("Próxima: Pentatónica").assertIsDisplayed()
    }

    @Test fun capturaRealDelEmuladorSeLiberaAlSalirYGuardaMedicionesSinAudio() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.uiAutomation.grantRuntimePermission(compose.activity.packageName, Manifest.permission.RECORD_AUDIO)
        compose.onNodeWithTag("game-voice").performScrollTo().performClick()
        compose.onNodeWithTag("voice-start").performScrollTo().performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Micrófono activo").fetchSemanticsNodes().isNotEmpty() }
        // Allow AudioRecord to supply actual PCM; silence is a legitimate emulator result.
        Thread.sleep(1500)
        compose.onNodeWithTag("voice-stop").performScrollTo().performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Micrófono detenido").fetchSemanticsNodes().isNotEmpty() }
        val export = runBlocking { Json.parseToJsonElement(Demonstration(compose.activity.filesDir).export("test")).jsonObject }
        val voice = export.getValue("voice").jsonArray.last().jsonObject
        assertTrue(voice.getValue("error") is JsonNull)
        assertEquals(44100, voice.getValue("actualSampleRate").jsonPrimitive.int)
        assertTrue(voice.getValue("measurements").jsonArray.isNotEmpty())
        assertFalse(voice.containsKey("samples"))
        compose.onNodeWithTag("voice-start").performScrollTo().performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Micrófono activo").fetchSemanticsNodes().isNotEmpty() }
        // A quick app switch can pause without reaching onStop.
        compose.activityRule.scenario.moveToState(androidx.lifecycle.Lifecycle.State.STARTED)
        compose.activityRule.scenario.moveToState(androidx.lifecycle.Lifecycle.State.RESUMED)
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Micrófono detenido").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("voice-start").performScrollTo().performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Micrófono activo").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Volver al menú").performScrollTo().performClick()
        compose.onNodeWithTag("game-voice").performScrollTo().performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Micrófono detenido").fetchSemanticsNodes().isNotEmpty() }
    }
    @Test fun cancelarSelectorDeExportacionConservaPerfil() {
        val data = Demonstration(compose.activity.filesDir)
        val before = runBlocking { Json.parseToJsonElement(data.export("test")).jsonObject }
        compose.onNodeWithTag("data-open").performScrollTo().performClick()
        compose.onNodeWithTag("data-export").performScrollTo().performClick()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        compose.waitUntil(15_000) {
            instrumentation.uiAutomation.rootInActiveWindow?.packageName?.toString()?.contains("documentsui") == true
        }
        // The first Back can hide the filename keyboard; then dismiss the picker.
        repeat(3) {
            if (instrumentation.uiAutomation.rootInActiveWindow?.packageName?.toString()?.contains("documentsui") == true) {
                instrumentation.sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK)
                Thread.sleep(500)
            }
        }
        compose.waitUntil(10_000) { compose.onAllNodesWithTag("data-message").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("data-message").performScrollTo().assertTextEquals("Exportación cancelada.")
        val after = runBlocking { Json.parseToJsonElement(data.export("test")).jsonObject }
        for (key in listOf("simon", "instruments", "voice", "simonProgress", "instrumentProgress")) assertEquals(before[key], after[key])
    }
}
