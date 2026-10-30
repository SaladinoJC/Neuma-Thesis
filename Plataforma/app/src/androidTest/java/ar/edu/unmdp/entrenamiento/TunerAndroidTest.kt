package ar.edu.unmdp.entrenamiento

import android.Manifest
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File
import androidx.compose.ui.semantics.SemanticsProperties
import kotlinx.coroutines.*
import ar.edu.unmdp.entrenamiento.voice.*

class TunerAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private fun grant() {
        InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("pm grant ar.edu.unmdp.entrenamiento ${Manifest.permission.RECORD_AUDIO}").close()
    }
    @Test fun microfonoEntregaMuestrasYSePuedeAbrirDeNuevoTrasLiberarlo() = runBlocking {
        grant()
        repeat(2) {
            val observed = CompletableDeferred<Int>()
            val job = launch(Dispatchers.Default) {
                AndroidVoiceCapture().capture(YinConfiguration()) { _, _, rate -> observed.complete(rate) }
            }
            try { assertEquals(44100, withTimeout(10_000) { observed.await() }) }
            finally { job.cancelAndJoin() }
        }
    }
    @Test fun capturaRealPausaRecreacionYCierreNoEscribenHistorialDeVoz() {
        grant()
        compose.awaitGardenMenu()
        val voiceFile = File(compose.activity.filesDir, "voice-results.json")
        val before = if (voiceFile.exists()) voiceFile.readBytes() else null
        compose.openMenuDestination("game-tuner")
        compose.waitUntil(10_000) {
            compose.onAllNodesWithTag("tuner-start").fetchSemanticsNodes().any { !it.config.contains(SemanticsProperties.Disabled) }
        }
        compose.onNodeWithTag("tuner-configuration-open").performScrollTo().performClick()
        compose.onNodeWithTag("tuner-tuning-standard").performScrollTo().performClick()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithTag("tuner-start").fetchSemanticsNodes().any { !it.config.contains(SemanticsProperties.Disabled) }
        }
        compose.onNodeWithTag("tuner-configuration-open").performScrollTo().performClick()
        compose.onNodeWithText("Pro ±2 cents").performScrollTo().performClick()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithTag("tuner-start").fetchSemanticsNodes().any { !it.config.contains(SemanticsProperties.Disabled) }
        }
        compose.onNodeWithText("6 · Mi2").performScrollTo().performClick()
        compose.onNodeWithText("Objetivo: Mi2").assertExists()
        compose.onNodeWithTag("tuner-start").performScrollTo().performClick()
        compose.onNodeWithTag("tuner-status").assertTextEquals("Micrófono activo")
        compose.mainClock.advanceTimeBy(1500)
        compose.onNodeWithTag("tuner-evaluation").assertTextEquals("Esperando señal válida")
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("tuner-status").performScrollTo().assertTextEquals("Micrófono detenido")
        compose.onNodeWithTag("tuner-start").performScrollTo().performClick()
        compose.activityRule.scenario.moveToState(androidx.lifecycle.Lifecycle.State.CREATED)
        compose.activityRule.scenario.moveToState(androidx.lifecycle.Lifecycle.State.RESUMED)
        compose.onNodeWithTag("tuner-status").performScrollTo().assertTextEquals("Micrófono detenido")
        compose.onNodeWithTag("tuner-start").performScrollTo().performClick()
        compose.onNodeWithText("Volver al menú").performScrollTo().performClick()
        compose.openMenuDestination("game-tuner")
        compose.onNodeWithTag("tuner-status").performScrollTo().assertTextEquals("Micrófono detenido")
        if (before == null) assertFalse(voiceFile.exists()) else assertArrayEquals(before, voiceFile.readBytes())
    }
}
