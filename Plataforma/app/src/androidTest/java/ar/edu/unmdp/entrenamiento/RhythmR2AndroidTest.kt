package ar.edu.unmdp.entrenamiento

import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ar.edu.unmdp.entrenamiento.rhythm.*
import ar.edu.unmdp.entrenamiento.ui.RhythmApp
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class RhythmR2AndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun compasesSimplesConSilenciosConteoYPracticaRepetible() {
        var now = 0L
        val audio = object : RhythmAudio {
            override suspend fun start(bpm: Int, metronome: Boolean) = RhythmAudioStart(now, "controlled", 0L)
            override fun tap() = Unit
            override fun stop() = Unit
        }
        val model = RhythmViewModel(audio, clock = { now })
        lateinit var host: View
        compose.setContent {
            host = LocalView.current
            val ui by model.ui.collectAsStateWithLifecycle()
            RhythmApp(ui, model) { model.home() }
        }
        try {
            compose.onNodeWithTag("rhythm-category-R19").performScrollTo().performClick()
            for (meter in 2..4) {
                compose.onNodeWithTag("rhythm-meter-$meter").performScrollTo().performClick()
                compose.onNodeWithTag("rhythm-generate").performScrollTo().performClick()
                compose.waitUntil(5_000) { model.ui.value.session.phase == RhythmPhase.PREVIEW }
                compose.onNodeWithTag("rhythm-score").performScrollTo().assertIsDisplayed()
                saveRhythmScreenshot("flow-$meter", compose.onRoot().captureToImage().asAndroidBitmap())
                val score = requireNotNull(model.ui.value.session.score)
                assertEquals(meter, score.beatsPerBar)
                val epoch = now
                compose.awaitRhythmScore()
                compose.onNodeWithTag("rhythm-play").performClick()
                compose.waitUntil(5_000) { model.ui.value.session.phase == RhythmPhase.COUNT_IN }
                tapWithDeviceUptime(host, compose.onNodeWithTag("rhythm-tap").fetchSemanticsNode().boundsInRoot.center)
                assertEquals(TapKind.PREPARATION, model.ui.value.session.taps.single().kind)
                compose.runOnIdle { now = epoch + meter * 5_000_000_000L + 1 }
                compose.waitUntil(5_000) { model.ui.value.session.phase == RhythmPhase.RESULTS }
                assertTrue(requireNotNull(model.ui.value.session.result).silences.all { it.respected == true })
                compose.onNodeWithTag("rhythm-repeat").performScrollTo().performClick()
                assertEquals(score, model.ui.value.session.score)
            }
        } finally { compose.runOnIdle { model.background() } }
    }

    @Test fun audioRealAceptaLasTresMetricasYLiberaRecursos() = runBlocking {
        val audio = AndroidRhythmAudio()
        for (meter in 2..4) {
            try {
                audio.start(120, false, meter)
                delay(180)
                assertTrue(audio.playbackFrames() > 0)
            } finally { audio.stop() }
            assertEquals(0, audio.activeTracks())
        }
    }
}
