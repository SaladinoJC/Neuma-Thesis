package ar.edu.unmdp.entrenamiento

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import ar.edu.unmdp.entrenamiento.rhythm.*
import ar.edu.unmdp.entrenamiento.ui.RhythmApp
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Preparado para Android; compilarlo no acredita haberlo ejecutado. */
class RhythmGardenAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun preparacionPlegableYAuxiliaresConservanPartituraConTextoGrande() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        var starts = 0
        val audio = object : RhythmAudio {
            override suspend fun start(bpm: Int, metronome: Boolean): RhythmAudioStart {
                starts++
                return RhythmAudioStart(0, "garden-ui-controlled", 0)
            }
            override fun tap() = Unit
            override fun stop() = Unit
        }
        lateinit var model: RhythmViewModel
        instrumentation.runOnMainSync { model = RhythmViewModel(audio, { 0L }, { 0L }, resultsStore = MemoryRhythmResults()) }
        compose.setContent {
            val ui by model.ui.collectAsState()
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) {
                Box(Modifier.width(320.dp).fillMaxHeight()) { RhythmApp(ui, model) { model.home() } }
            }
        }
        try {
            compose.waitUntil(10_000) { !model.ui.value.historyBusy }
            compose.onNodeWithTag("rhythm-generate").performScrollTo().performClick()
            compose.waitUntil(10_000) { model.ui.value.session.phase == RhythmPhase.PREVIEW }
            compose.onNodeWithTag("rhythm-mode-practice").assertDoesNotExist()
            val score = model.ui.value.session.score
            compose.openRhythmPreparationIfCollapsed()
            compose.onNodeWithTag("rhythm-calibration-open").performScrollTo().performClick()
            compose.onNodeWithTag("rhythm-calibration-status").performScrollTo().assertExists()
            compose.onNodeWithTag("rhythm-calibration-close").performClick()
            compose.onNodeWithTag("rhythm-open-history").performScrollTo().performClick()
            compose.onNodeWithTag("rhythm-history-screen").assertExists()
            compose.onNodeWithTag("rhythm-history-back").performScrollTo().performClick()
            compose.onNodeWithTag("rhythm-progress-open").performScrollTo().performClick()
            compose.onNodeWithTag("rhythm-progress-screen").assertExists()
            compose.onNodeWithTag("rhythm-progress-back").performScrollTo().performClick()
            compose.onNodeWithTag("rhythm-settings").performScrollTo().assertExists()
            assertEquals(score, model.ui.value.session.score)
            assertEquals(0, starts)
        } finally { instrumentation.runOnMainSync { model.background() } }
    }
}
