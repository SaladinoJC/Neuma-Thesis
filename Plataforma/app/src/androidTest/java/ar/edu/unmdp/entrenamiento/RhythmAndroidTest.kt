package ar.edu.unmdp.entrenamiento

import android.os.SystemClock
import android.view.MotionEvent
import android.view.Window
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import ar.edu.unmdp.entrenamiento.rhythm.AndroidRhythmAudio
import ar.edu.unmdp.entrenamiento.rhythm.RhythmViewModel
import ar.edu.unmdp.entrenamiento.rhythm.TapKind
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicLong

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
        lateinit var originalCallback: Window.Callback
        val observedDown = AtomicLong(-1L)
        compose.runOnUiThread {
            model = ViewModelProvider(compose.activity)[RhythmViewModel::class.java]
            originalCallback = compose.activity.window.callback
            compose.activity.window.callback = object : Window.Callback by originalCallback {
                override fun dispatchTouchEvent(event: MotionEvent): Boolean {
                    if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                        observedDown.set(event.eventTime)
                        println("RHYTHM probe ACTION_DOWN eventUptime=${event.eventTime} nowUptime=${SystemClock.uptimeMillis()} phase=${model.ui.value.session.phase} elapsed=${model.ui.value.session.elapsedNanos}")
                    }
                    return originalCallback.dispatchTouchEvent(event)
                }
            }
        }
        try {
        compose.onNodeWithTag("rhythm-play").performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithTag("rhythm-tap").fetchSemanticsNodes().isNotEmpty() }
        compose.runOnUiThread {
            println("RHYTHM probe beforeGesture nowUptime=${SystemClock.uptimeMillis()} phase=${model.ui.value.session.phase} elapsed=${model.ui.value.session.elapsedNanos}")
        }
        compose.onNodeWithTag("rhythm-tap").performTouchInput {
            down(center); advanceEventTime(100); up()
        }
        compose.runOnUiThread {
            println("RHYTHM probe afterGesture nowUptime=${SystemClock.uptimeMillis()} phase=${model.ui.value.session.phase} taps=${model.ui.value.session.taps}")
        }
        compose.waitUntil(30_000) { compose.onAllNodesWithTag("rhythm-summary").fetchSemanticsNodes().isNotEmpty() }
        val result = requireNotNull(model.ui.value.session.result)
        val projectedDown = observedDown.get().takeIf { it >= 0 }?.let { it * 1_000_000L + result.inputClockOffsetNanos }
        assertTrue("Expected one real count-in tap. rawDownUptime=${observedDown.get()} projectedDownNanos=$projectedDown countInStart=${result.countInStartNanos} inputOffset=${result.inputClockOffsetNanos} taps=${result.taps}",
            result.taps.any { it.kind == TapKind.PREPARATION })
        compose.onNodeWithTag("rhythm-status").performScrollTo().assertTextEquals("Ejercicio completado")
        compose.onNodeWithText("Tap durante el conteo", substring = true).performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("rhythm-repeat").performScrollTo().performClick()
        compose.onNodeWithTag("rhythm-status").performScrollTo().assertTextEquals("Vista previa · No suena el patrón objetivo")
        compose.onNodeWithTag("rhythm-play").performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithTag("rhythm-tap").fetchSemanticsNodes().isNotEmpty() }
        compose.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        compose.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        compose.onNodeWithTag("rhythm-status").performScrollTo().assertTextEquals("Ejercicio interrumpido")
        } finally {
            compose.runOnUiThread { compose.activity.window.callback = originalCallback }
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
