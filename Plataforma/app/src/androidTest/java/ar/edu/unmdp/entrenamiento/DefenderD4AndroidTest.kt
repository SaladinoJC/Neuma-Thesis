package ar.edu.unmdp.entrenamiento

import androidx.compose.runtime.*
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import ar.edu.unmdp.entrenamiento.defender.*
import ar.edu.unmdp.entrenamiento.ui.DefenderApp
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.concurrent.atomic.AtomicLong

/** Se compila en host; ejecutar solo cuando el usuario autorice emulador o dispositivo. */
class DefenderD4AndroidTest {
    @get:Rule val compose = createComposeRule()
    private val audio = object : DefenderAudio {
        override suspend fun note(midi: Int) = error("Una tríada debe usar tres voces")
        override suspend fun chord(midis: List<Int>): Int { require(midis.size == 3); return 0 }
        override suspend fun effect(effect: DefenderEffect) = 0
    }

    @Test fun triadasDesbloqueanSuPropiaRutaYSeConservanTrasReabrir() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val file = File(instrumentation.targetContext.cacheDir, "defender-d4-${System.nanoTime()}/defender-results.json")
        val now = AtomicLong()
        fun create() = DefenderViewModel(audio, clock = now::get, resultsStore = JsonDefenderResults(file))
        lateinit var initial: DefenderViewModel
        instrumentation.runOnMainSync { initial = create() }
        var current by mutableStateOf(initial)
        compose.setContent { val ui by current.ui.collectAsState(); DefenderApp(ui, current) { current.home() } }
        try {
            compose.waitUntil(10_000) { !current.ui.value.historyBusy }
            compose.onNodeWithTag("defender-variant-chords").performScrollTo().performClick()
            compose.onNodeWithTag("defender-mode-progression").performScrollTo().performClick()
            compose.onNodeWithTag("defender-quality-MINOR").performScrollTo().assertIsNotEnabled()
            repeat(2) { index ->
                compose.onNodeWithTag("defender-start").performScrollTo().performClick()
                compose.waitUntil(10_000) { current.ui.value.game.phase == DefenderPhase.RUNNING }
                repeat(24) {
                    compose.runOnIdle {
                        while (current.ui.value.game.enemies.isEmpty()) { now.addAndGet(100); current.tick() }
                        now.incrementAndGet()
                    }
                    val chord = current.ui.value.game.enemies.minBy { it.appearedAtMillis }.chord!!
                    compose.onNodeWithTag("defender-name-${chord.root.name.name}").performClick()
                    compose.onNodeWithTag("defender-accidental-${chord.root.accidental.name}").performClick()
                    compose.onNodeWithTag("defender-answer-quality-${chord.quality.name}").performClick()
                    compose.onNodeWithTag("defender-fire").performSemanticsAction(SemanticsActions.OnClick) { it() }
                }
                compose.runOnIdle {
                    while (current.ui.value.game.phase == DefenderPhase.RUNNING) { now.addAndGet(100); current.tick() }
                }
                compose.waitUntil(10_000) { !current.ui.value.historyBusy }
                assertFalse(current.ui.value.history.results.last().sealed)
                compose.onNodeWithTag("defender-inspector-open").performScrollTo().performClick()
                compose.waitUntil(10_000) { !current.ui.value.historyBusy }
                assertEquals(if (index == 0) 1 else 0, current.ui.value.progress[DefenderChordQuality.MAJOR].highStreak)
                captureRhythmEvidence(compose, "defender-d4", "triadas-progreso-${index + 1}")
                compose.onNodeWithTag("defender-inspector-back").performScrollTo().performClick()
                compose.onNodeWithTag("defender-new").performScrollTo().performClick()
            }
            assertTrue(DefenderChordQuality.MINOR in current.ui.value.progress.unlockedQualities)
            assertEquals(setOf(DefenderStage.N1), current.ui.value.history.progress.unlocked)
            compose.runOnIdle { current.home(); current = create() }
            compose.waitUntil(10_000) { !current.ui.value.historyBusy }
            compose.onNodeWithTag("defender-variant-chords").performScrollTo().performClick()
            assertEquals(2, current.ui.value.history.results.size)
            assertTrue(DefenderChordQuality.MINOR in current.ui.value.progress.unlockedQualities)
            compose.onNodeWithTag("defender-history-open").performScrollTo().performClick()
            compose.waitUntil(10_000) { !current.ui.value.historyBusy }
            compose.onNodeWithTag("defender-history-screen").performScrollToNode(hasTestTag("defender-history-detail"))
            compose.onAllNodesWithTag("defender-history-detail").onFirst().performClick()
            compose.onNodeWithTag("defender-progress-reason").assertExists()
            captureRhythmEvidence(compose, "defender-d4", "historial-triada-reabierto")
        } finally { instrumentation.runOnMainSync { current.home() } }
    }

    @Test fun practicaIncluyeCalidadesBloqueadasYSimulacionNoOtorgaLogros() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        lateinit var model: DefenderViewModel
        instrumentation.runOnMainSync { model = DefenderViewModel(audio) }
        compose.setContent { val ui by model.ui.collectAsState(); DefenderApp(ui, model, reduceMotion = true) { model.home() } }
        try {
            compose.waitUntil(10_000) { !model.ui.value.historyBusy }
            compose.onNodeWithTag("defender-variant-chords").performScrollTo().performClick()
            compose.onNodeWithTag("defender-quality-AUGMENTED").performScrollTo().performClick()
            assertEquals(DefenderChordQuality.AUGMENTED, model.ui.value.chordQuality)
            assertFalse(model.ui.value.progression)
            compose.onNodeWithTag("defender-inspector-open").performScrollTo().performClick()
            compose.onNodeWithTag("defender-inspector-screen").performScrollToNode(hasTestTag("defender-dda-simulated"))
            compose.onNodeWithTag("defender-dda-simulated").performClick()
            compose.onNodeWithTag("defender-inspector-screen").performScrollToNode(hasTestTag("defender-dda-run"))
            compose.onNodeWithTag("defender-dda-run").performClick()
            compose.waitUntil(15_000) { model.ui.value.simulation != null && !model.ui.value.simulationBusy }
            assertEquals("chords", model.ui.value.simulation!!.variant)
            assertTrue(model.ui.value.history.results.isEmpty())
            assertEquals(setOf(DefenderChordQuality.MAJOR), model.ui.value.progress.unlockedQualities)
            assertEquals(setOf(DefenderStage.N1), model.ui.value.history.progress.unlocked)
            captureRhythmEvidence(compose, "defender-d4", "simulacion-triadas-aislada")
        } finally { instrumentation.runOnMainSync { model.home() } }
    }
}
