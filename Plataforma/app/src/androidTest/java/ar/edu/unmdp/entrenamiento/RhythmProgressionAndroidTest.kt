package ar.edu.unmdp.entrenamiento

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import ar.edu.unmdp.entrenamiento.rhythm.*
import ar.edu.unmdp.entrenamiento.ui.RhythmApp
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicLong

/** Compiled on the host; execution must wait for an authorized test emulator. */
class RhythmProgressionAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun progresionDesbloqueaYConservaPartituraAlRepetirComoPractica() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val clock = AtomicLong()
        val store = MemoryRhythmResults()
        val audio = object : RhythmAudio {
            override suspend fun start(bpm: Int, metronome: Boolean) = RhythmAudioStart(clock.get(), "controlled-r6", 0)
            override fun tap() = Unit
            override fun stop() = Unit
        }
        lateinit var model: RhythmViewModel
        instrumentation.runOnMainSync { model = RhythmViewModel(audio, clock::get, { clock.get() / 1_000_000L }, resultsStore = store) }
        compose.setContent { val ui by model.ui.collectAsState(); RhythmApp(ui, model) { model.home() } }
        try {
            compose.waitUntil(10_000) { !model.ui.value.historyBusy }
            compose.onNodeWithTag("rhythm-mode-progression").performScrollTo().performClick()
            compose.onNodeWithTag("rhythm-category-R01").performScrollTo().assertIsNotEnabled()
            compose.onNodeWithTag("rhythm-category-R20").performScrollTo().performClick()
            for (seed in listOf(1L, 2L)) {
                compose.runOnIdle { clock.set(seed) }
                compose.openRhythmPreparationIfCollapsed()
                compose.onNodeWithTag("rhythm-generate").performScrollTo().performClick()
                compose.waitUntil(10_000) { model.ui.value.session.phase == RhythmPhase.PREVIEW }
                compose.awaitRhythmScore()
                compose.onNodeWithTag("rhythm-play").performClick()
                compose.waitUntil(10_000) { model.ui.value.session.phase == RhythmPhase.COUNT_IN }
                val attacks = model.ui.value.session.attacks.toList()
                for (attack in attacks) {
                    compose.runOnIdle { clock.set(seed + Beat(4).nanos(60) + attack.offsetNanos) }
                    compose.onNodeWithTag("rhythm-tap").performSemanticsAction(SemanticsActions.OnClick) { it() }
                }
                compose.runOnIdle { clock.set(seed + Beat(20).nanos(60) + 1) }
                compose.waitUntil(10_000) { model.ui.value.session.phase == RhythmPhase.RESULTS && !model.ui.value.historyBusy }
            }
            assertEquals(65, model.ui.value.history.progress[RhythmCategory.QUARTER].bpm)
            assertTrue(RhythmCategory.HALF in model.ui.value.history.progress.unlocked)
            captureRhythmEvidence(compose, "r6", "resultado-progresion")
            compose.onNodeWithTag("rhythm-progress-open").performScrollTo().performClick()
            compose.onNodeWithTag("rhythm-progress-value").assertTextEquals("65 BPM recomendados · 0/2 resultados altos consecutivos")
            compose.onNodeWithTag("rhythm-progress-back").performScrollTo().performClick()
            val score = model.ui.value.session.score
            compose.onNodeWithTag("rhythm-repeat").performScrollTo().performClick()
            assertEquals(score, model.ui.value.session.score)
            assertFalse(model.ui.value.progression)
            assertEquals("practice", model.ui.value.session.origin)
        } finally { instrumentation.runOnMainSync { model.background() } }
    }

    @Test fun panelVacioYSimulacionNoModificanPerfilNiInicianAudio() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        var starts = 0
        val audio = object : RhythmAudio {
            override suspend fun start(bpm: Int, metronome: Boolean): RhythmAudioStart { starts++; return RhythmAudioStart(0, "controlled", 0) }
            override fun tap() = Unit
            override fun stop() = Unit
        }
        lateinit var model: RhythmViewModel
        instrumentation.runOnMainSync { model = RhythmViewModel(audio) }
        compose.setContent { val ui by model.ui.collectAsState(); RhythmApp(ui, model) { model.home() } }
        try {
            compose.onNodeWithTag("rhythm-progress-open").performScrollTo().performClick()
            compose.onNodeWithTag("rhythm-progress-screen").performScrollToNode(hasTestTag("rhythm-dda-simulated"))
            compose.onNodeWithTag("rhythm-dda-simulated").performClick()
            compose.onNodeWithTag("rhythm-progress-screen").performScrollToNode(hasTestTag("rhythm-dda-run"))
            compose.onNodeWithTag("rhythm-dda-run").performClick()
            compose.waitUntil(10_000) { model.ui.value.simulation != null && !model.ui.value.simulationBusy }
            assertEquals(65, model.ui.value.simulation!!.progress[RhythmCategory.QUARTER].bpm)
            assertEquals(60, model.ui.value.history.progress[RhythmCategory.QUARTER].bpm)
            assertTrue(model.ui.value.history.results.isEmpty())
            assertEquals(0, starts)
            captureRhythmEvidence(compose, "r6", "simulacion-aislada")
        } finally { instrumentation.runOnMainSync { model.background() } }
    }
}
