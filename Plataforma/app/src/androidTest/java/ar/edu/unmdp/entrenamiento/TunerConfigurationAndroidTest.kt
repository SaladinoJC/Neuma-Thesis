package ar.edu.unmdp.entrenamiento

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.semantics.SemanticsProperties
import org.junit.Rule
import org.junit.Test

class TunerConfigurationAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private fun awaitReady() {
        compose.waitUntil(15_000) {
            compose.onAllNodesWithTag("tuner-start").fetchSemanticsNodes().any { !it.config.contains(SemanticsProperties.Disabled) }
        }
    }
    @Test fun perfilDropDYPersonalizadaSeConservanSinIniciarMicrofono() {
        compose.awaitGardenMenu()
        compose.openMenuDestination("game-tuner")
        awaitReady()
        compose.onNodeWithTag("tuner-configuration-open").performScrollTo().performClick()
        val name = "Guitarra A2 ${System.nanoTime()}"
        compose.onNodeWithTag("tuner-guitar-name").performScrollTo().performTextReplacement(name)
        compose.onNodeWithTag("tuner-guitar-create").performScrollTo().performClick()
        awaitReady()
        compose.onNodeWithTag("tuner-tuning-drop-d").performScrollTo().performClick()
        awaitReady()
        compose.onNodeWithTag("tuner-setup").assertTextContains(name, substring = true)
        compose.onNodeWithTag("tuner-setup").assertTextContains("Drop D", substring = true)
        compose.onNodeWithTag("tuner-configuration-open").performScrollTo().performClick()
        compose.onNodeWithText("6 · Re2").performScrollTo().performClick()
        compose.onNodeWithText("Objetivo: Re2").assertExists()
        compose.onNodeWithText("Pro ±2 cents").performScrollTo().performClick()
        awaitReady()
        compose.onNodeWithTag("tuner-configuration-open").performScrollTo().performClick()
        compose.onNodeWithTag("tuner-custom-new").performScrollTo().performClick()
        compose.onNodeWithTag("tuner-custom-name").performScrollTo().performTextReplacement("Personal A2")
        compose.onNodeWithTag("tuner-custom-note-6").performScrollTo().performTextReplacement("Do2")
        compose.onNodeWithTag("tuner-custom-save").performScrollTo().performClick()
        awaitReady()
        compose.onNodeWithTag("tuner-setup").assertTextContains("Personal A2", substring = true)
        compose.activityRule.scenario.recreate()
        awaitReady()
        compose.onNodeWithTag("tuner-setup").assertTextContains(name, substring = true)
        compose.onNodeWithTag("tuner-setup").assertTextContains("Personal A2", substring = true)
        compose.onNodeWithText("Pro ±2 cents").assertIsSelected()
        compose.onNodeWithText("6 · Do2").performScrollTo().assertExists()
        compose.onNodeWithTag("tuner-status").performScrollTo().assertTextEquals("Micrófono detenido")
        compose.onNodeWithTag("tuner-configuration-open").performScrollTo().performClick()
        compose.onNodeWithTag("tuner-custom-edit").performScrollTo().performClick()
        compose.onNodeWithTag("tuner-custom-note-6").performScrollTo().performTextReplacement("Si1")
        compose.onNodeWithTag("tuner-custom-save").performScrollTo().performClick()
        awaitReady()
        compose.onNodeWithTag("tuner-editor-error").performScrollTo().assertTextContains("fuera del rango", substring = true)
        compose.onNodeWithTag("tuner-configuration-open").performScrollTo().performClick()
        compose.onNodeWithText("6 · Do2").performScrollTo().assertExists()
    }
}
