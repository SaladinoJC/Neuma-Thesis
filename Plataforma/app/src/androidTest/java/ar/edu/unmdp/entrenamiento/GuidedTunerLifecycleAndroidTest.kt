package ar.edu.unmdp.entrenamiento

import android.Manifest
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test

/** Captura real en emulador: verifica ciclo de vida, no precisión acústica. */
class GuidedTunerLifecycleAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private fun ready() {
        compose.waitUntil(10_000) {
            compose.onAllNodesWithTag("tuner-start").fetchSemanticsNodes()
                .any { !it.config.contains(SemanticsProperties.Disabled) }
        }
    }

    private fun interrupted() {
        compose.onNodeWithTag("tuner-status").performScrollTo().assertTextEquals("Micrófono detenido")
        compose.onNodeWithTag("tuner-guided-phase").performScrollTo()
            .assertTextEquals("Recorrido interrumpido · iniciar comienza uno nuevo")
        compose.onNodeWithTag("tuner-guided-summary").assertDoesNotExist()
    }

    @Test fun recrearPausarYSalirInterrumpenGuiadoSinCompletarNiReiniciar() {
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
            "pm grant ar.edu.unmdp.entrenamiento ${Manifest.permission.RECORD_AUDIO}").close()
        compose.onNodeWithTag("game-tuner").performScrollTo().performClick()
        ready()
        compose.onNodeWithTag("tuner-mode-GUIDED").performScrollTo().performClick()
        ready()
        try {
            compose.onNodeWithTag("tuner-start").performScrollTo().performClick()
            compose.onNodeWithTag("tuner-status").assertTextEquals("Micrófono activo")
            compose.activityRule.scenario.recreate()
            interrupted()
            compose.onNodeWithTag("tuner-start").performScrollTo().performClick()
            compose.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
            compose.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
            interrupted()
            compose.onNodeWithTag("tuner-start").performScrollTo().performClick()
            compose.onNodeWithText("Volver al menú").performScrollTo().performClick()
            compose.onNodeWithTag("game-tuner").performScrollTo().performClick()
            interrupted()
        } finally {
            compose.onNodeWithTag("tuner-mode-FREE").performScrollTo().performClick()
            ready()
        }
    }
}
