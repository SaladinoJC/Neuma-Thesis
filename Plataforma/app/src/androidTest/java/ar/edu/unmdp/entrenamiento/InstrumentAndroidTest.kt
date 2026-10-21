package ar.edu.unmdp.entrenamiento

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.graphics.asAndroidBitmap
import ar.edu.unmdp.entrenamiento.audio.AndroidInstrumentPlayer
import ar.edu.unmdp.entrenamiento.instruments.Instrument
import ar.edu.unmdp.entrenamiento.instruments.InstrumentMelody
import android.content.res.Configuration
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File
import android.graphics.Bitmap

class InstrumentAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @org.junit.Before
    fun awaitAppWindow() {
        compose.waitUntil(15_000) { compose.activity.hasWindowFocus() }
        compose.waitForIdle()
    }


    @Test
    fun instrumentosPermiteJugarDiezPreguntasYConservaElHistorialAlReabrir() {
        screenshot("menu")
        compose.onNodeWithTag("game-instruments").performClick()
        compose.onNodeWithTag("level-C").performScrollTo().performClick()
        screenshot("preparacion")
        compose.onNodeWithText("Jugar instrumentos").performClick()
        for (round in 1..10) {
            compose.waitUntil(30_000) {
                compose.onAllNodesWithTag("instrument-option-0").fetchSemanticsNodes().any {
                    !it.config.contains(SemanticsProperties.Disabled)
                }
            }
            compose.onNodeWithText("Pregunta $round de 10").assertIsDisplayed()
            compose.onNodeWithText("2 escuchas restantes").assertIsDisplayed()
            if (round == 1) {
                screenshot("pregunta")
                compose.activityRule.scenario.recreate()
                repeat(2) {
                    compose.onNodeWithTag("instrument-listen").performClick()
                    compose.waitUntil(10_000) {
                        compose.onAllNodesWithTag("instrument-option-0").fetchSemanticsNodes().any { node ->
                            !node.config.contains(SemanticsProperties.Disabled)
                        }
                    }
                }
                compose.onNodeWithText("0 escuchas restantes").assertIsDisplayed()
                compose.onNodeWithTag("instrument-listen").assertIsNotEnabled()
            }
            repeat(4) { compose.onNodeWithTag("instrument-option-$it").assertIsDisplayed() }
            compose.onNodeWithTag("instrument-option-0").performClick()
            if (round == 1) screenshot("respuesta")
            if (round < 10) compose.onNodeWithText("Siguiente pregunta").performClick()
        }
        compose.onNodeWithText("Partida terminada").assertIsDisplayed()
        compose.onNodeWithText("10 preguntas respondidas").assertIsDisplayed()
        screenshot("resumen")
        compose.onNodeWithText("Volver al menú").performClick()
        compose.activityRule.scenario.recreate()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Instrumentos · Nivel C · Completada", substring = true).fetchSemanticsNodes().isNotEmpty()
        }
        assertEquals(Configuration.ORIENTATION_PORTRAIT, compose.activity.resources.configuration.orientation)
    }

    @Test
    fun nivelesManualesYSalidaDurantePresentacionConservanUnaInterrupcion() {
        compose.onNodeWithTag("game-instruments").performClick()
        for (level in listOf("A", "B", "C")) compose.onNodeWithTag("level-$level").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("level-A").performScrollTo().performClick()
        compose.onNodeWithText("Jugar instrumentos").performClick()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithContentDescription("Volver al menú").performClick()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Instrumentos · Nivel A · Interrumpida").fetchSemanticsNodes().isNotEmpty()
        }
        compose.activityRule.scenario.recreate()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Instrumentos · Nivel A · Interrumpida").fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun screenshot(name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        val directory = File(compose.activity.getExternalFilesDir(null), "verification-instruments").apply { mkdirs() }
        File(directory, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    @Test
    fun elBancoReproduceNueveInstrumentosYConfirmaCadaEscuchaUnaSolaVez() = runBlocking {
        val player = AndroidInstrumentPlayer(compose.activity.applicationContext)
        val melody = InstrumentMelody(listOf(60, 64, 69, 67, 62, 60), listOf(400, 400, 400, 400, 400, 800))
        for (instrument in Instrument.entries) {
            var starts = 0
            val frames = player.play(instrument, melody) { starts++ }
            assertTrue("$instrument: una frase completa de 3,05 s de audio real", frames >= 134_505)
            assertEquals(1, starts)
        }
    }
}
