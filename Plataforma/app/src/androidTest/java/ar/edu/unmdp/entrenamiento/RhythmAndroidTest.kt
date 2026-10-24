package ar.edu.unmdp.entrenamiento

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import ar.edu.unmdp.entrenamiento.rhythm.AndroidRhythmAudio
import ar.edu.unmdp.entrenamiento.rhythm.RhythmPhase
import ar.edu.unmdp.entrenamiento.rhythm.RhythmViewModel
import ar.edu.unmdp.entrenamiento.rhythm.TapKind
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
        lateinit var model: RhythmViewModel
        compose.runOnUiThread {
            model = ViewModelProvider(compose.activity)[RhythmViewModel::class.java]
        }
        try {
            // Automatic Compose idling used to wait out the continuously refreshed 20s
            // timeline before injecting the gesture. Render explicit frames while the
            // real audio/monotonic clock keeps running, and inspect the public state.
            compose.mainClock.autoAdvance = false
            compose.onNodeWithTag("rhythm-play").performClick()
            compose.waitUntil(10_000) { model.ui.value.session.phase == RhythmPhase.COUNT_IN }
            compose.mainClock.advanceTimeBy(64)
            val tapCenter = compose.onNodeWithTag("rhythm-tap").fetchSemanticsNode().boundsInWindow.center
            assertEquals(RhythmPhase.COUNT_IN, model.ui.value.session.phase)
            tapWithDeviceUptime(compose.activity.window.decorView, tapCenter)
            compose.waitUntil(2_000) { model.ui.value.session.taps.isNotEmpty() }
            val tap = model.ui.value.session.taps.single()
            assertEquals(TapKind.PREPARATION, tap.kind)
            assertNotNull(tap.inputUptimeMillis)
            assertTrue(tap.relativeNanos < 0)

            compose.waitUntil(30_000) { model.ui.value.session.phase == RhythmPhase.RESULTS }
            val result = requireNotNull(model.ui.value.session.result)
            assertTrue(result.completed)
            assertEquals(listOf(tap), result.taps)
            compose.mainClock.advanceTimeBy(64)
            compose.mainClock.autoAdvance = true
            compose.onNodeWithTag("rhythm-status").performScrollTo().assertTextEquals("Ejercicio completado")
            compose.onNodeWithText("Tap durante el conteo", substring = true).performScrollTo().assertIsDisplayed()
            compose.onNodeWithTag("rhythm-repeat").performScrollTo().performClick()
            compose.onNodeWithTag("rhythm-status").performScrollTo().assertTextEquals("Vista previa · No suena el patrón objetivo")
            assertEquals(result.score, model.ui.value.session.score)
            assertTrue(model.ui.value.session.repeated)

            compose.mainClock.autoAdvance = false
            compose.onNodeWithTag("rhythm-play").performClick()
            compose.waitUntil(10_000) { model.ui.value.session.phase == RhythmPhase.COUNT_IN }
            compose.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
            compose.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
            assertFalse(requireNotNull(model.ui.value.session.result).completed)
            compose.mainClock.advanceTimeBy(64)
            compose.mainClock.autoAdvance = true
            compose.onNodeWithTag("rhythm-status").performScrollTo().assertTextEquals("Ejercicio interrumpido")
        } finally {
            compose.runOnUiThread { model.background() }
            compose.mainClock.autoAdvance = true
        }
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
