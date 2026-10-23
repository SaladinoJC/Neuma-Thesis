package ar.edu.unmdp.entrenamiento

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import ar.edu.unmdp.entrenamiento.rhythm.AndroidRhythmAudio
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class RhythmAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @Test fun partituraConteoTapsRepeticionYPausaEnVertical() {
        compose.waitUntil(15_000) { compose.activity.hasWindowFocus() }
        compose.onNodeWithTag("game-rhythm").performScrollTo().performClick()
        compose.onNodeWithTag("rhythm-category-R20").performScrollTo().performClick()
        compose.onNodeWithTag("rhythm-category-R21").performScrollTo().performClick()
        compose.onNodeWithTag("rhythm-generate").performScrollTo().performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithTag("rhythm-play").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("rhythm-bar-0").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("rhythm-bar-3").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("rhythm-metronome").performScrollTo().performClick()
        compose.onNodeWithTag("rhythm-tap-sound").performScrollTo().performClick()
        compose.onNodeWithTag("rhythm-play").performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithTag("rhythm-tap").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("rhythm-tap").performTouchInput {
            down(center); advanceEventTime(100); up()
        }
        compose.waitUntil(30_000) { compose.onAllNodesWithTag("rhythm-summary").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("rhythm-status").performScrollTo().assertTextEquals("Ejercicio completado")
        compose.onNodeWithText("Tap durante el conteo", substring = true).performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("rhythm-repeat").performScrollTo().performClick()
        compose.onNodeWithTag("rhythm-status").performScrollTo().assertTextEquals("Vista previa · No suena el patrón objetivo")
        compose.onNodeWithTag("rhythm-play").performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithTag("rhythm-tap").fetchSemanticsNodes().isNotEmpty() }
        compose.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        compose.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        compose.onNodeWithTag("rhythm-status").performScrollTo().assertTextEquals("Ejercicio interrumpido")
    }
    @Test fun audioRealAvanzaFramesYLiberaTimelineYTaps() = runBlocking {
        val audio = AndroidRhythmAudio()
        try {
            val start = audio.start(120, false)
            assertTrue(start.originNanos > 0)
            assertNull(start.uncertaintyNanos)
            assertEquals(2, audio.activeTracks())
            delay(180)
            val frames = audio.playbackFrames()
            assertTrue("AudioTrack frames=$frames", frames > 0)
            audio.tap()
            println("RHYTHM emulator playedFrames=$frames clock=${start.source}")
        } finally { audio.stop() }
        assertEquals(0, audio.activeTracks())
    }
}
