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

/** Host compilation only until the user authorizes starting the test emulator. */
class DefenderD3AndroidTest {
    @get:Rule val compose = createComposeRule()
    private val audio = object : DefenderAudio {
        override suspend fun note(midi: Int) = 0
        override suspend fun effect(effect: DefenderEffect) = 0
    }

    @Test fun dosCompletasDesbloqueanTrasCerrarResumenYReaperturaConservaHistorial() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val file = File(instrumentation.targetContext.cacheDir, "defender-d3-${System.nanoTime()}/defender-results.json")
        val now = AtomicLong()
        fun create() = DefenderViewModel(audio, clock = now::get, resultsStore = JsonDefenderResults(file))
        lateinit var initial: DefenderViewModel
        instrumentation.runOnMainSync { initial = create() }
        var current by mutableStateOf(initial)
        compose.setContent { val ui by current.ui.collectAsState(); DefenderApp(ui, current) { current.home() } }
        try {
            compose.waitUntil(10_000) { !current.ui.value.historyBusy }
            compose.onNodeWithTag("defender-mode-progression").performScrollTo().performClick()
            compose.onNodeWithTag("defender-stage-N2").performScrollTo().assertIsNotEnabled()
            repeat(2) { index ->
                compose.onNodeWithTag("defender-start").performScrollTo().performClick()
                compose.waitUntil(10_000) { current.ui.value.game.phase == DefenderPhase.RUNNING }
                assertEquals(10_000L, current.ui.value.game.currentTravelMillis)
                repeat(24) {
                    compose.runOnIdle {
                        while (current.ui.value.game.enemies.isEmpty()) { now.addAndGet(100); current.tick() }
                        now.incrementAndGet()
                    }
                    val name = current.ui.value.game.enemies.minBy { it.appearedAtMillis }.note.name
                    compose.onNodeWithTag("defender-name-${name.name}").performClick()
                    compose.onNodeWithTag("defender-fire").performSemanticsAction(SemanticsActions.OnClick) { it() }
                }
                compose.runOnIdle {
                    while (current.ui.value.game.phase == DefenderPhase.RUNNING) { now.addAndGet(100); current.tick() }
                }
                compose.waitUntil(10_000) { !current.ui.value.historyBusy }
                assertFalse(current.ui.value.history.results.last().sealed)
                compose.onNodeWithTag("defender-inspector-open").performScrollTo().performClick()
                compose.waitUntil(10_000) { !current.ui.value.historyBusy }
                assertEquals(if (index == 0) 1 else 0, current.ui.value.history.progress[DefenderStage.N1].highStreak)
                captureRhythmEvidence(compose, "defender-d3", "progreso-${index + 1}")
                compose.onNodeWithTag("defender-inspector-back").performScrollTo().performClick()
                compose.onNodeWithTag("defender-new").performScrollTo().performClick()
            }
            assertTrue(DefenderStage.N2 in current.ui.value.history.progress.unlocked)
            compose.runOnIdle { current.home(); current = create() }
            compose.waitUntil(10_000) { !current.ui.value.historyBusy }
            assertEquals(2, current.ui.value.history.results.size)
            assertTrue(DefenderStage.N2 in current.ui.value.history.progress.unlocked)
            compose.onNodeWithTag("defender-history-open").performScrollTo().performClick()
            compose.waitUntil(10_000) { !current.ui.value.historyBusy }
            compose.onNodeWithTag("defender-history-screen").performScrollToNode(hasTestTag("defender-history-detail"))
            compose.onAllNodesWithTag("defender-history-detail").onFirst().performClick()
            compose.onNodeWithTag("defender-progress-reason").assertExists()
            captureRhythmEvidence(compose, "defender-d3", "historial-reabierto")
        } finally { instrumentation.runOnMainSync { current.home() } }
    }

    @Test fun estadoVacioSimulacionYPracticaBloqueadaNoAlteranPerfil() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        lateinit var model: DefenderViewModel
        instrumentation.runOnMainSync { model = DefenderViewModel(audio) }
        compose.setContent { val ui by model.ui.collectAsState(); DefenderApp(ui, model, reduceMotion = true) { model.home() } }
        try {
            compose.waitUntil(10_000) { !model.ui.value.historyBusy }
            compose.onNodeWithTag("defender-inspector-open").performScrollTo().performClick()
            compose.onNodeWithTag("defender-inspector-screen").performScrollToNode(hasTestTag("defender-dda-simulated"))
            compose.onNodeWithTag("defender-dda-simulated").performClick()
            compose.onNodeWithTag("defender-inspector-screen").performScrollToNode(hasTestTag("defender-dda-run"))
            compose.onNodeWithTag("defender-dda-run").performClick()
            compose.waitUntil(15_000) { model.ui.value.simulation != null && !model.ui.value.simulationBusy }
            assertTrue(model.ui.value.history.results.isEmpty())
            assertEquals(setOf(DefenderStage.N1), model.ui.value.history.progress.unlocked)
            captureRhythmEvidence(compose, "defender-d3", "simulacion-aislada")
            compose.onNodeWithTag("defender-inspector-back").performScrollTo().performClick()
            compose.onNodeWithTag("defender-stage-N4").performScrollTo().performClick()
            assertEquals(DefenderStage.N4, model.ui.value.stage)
            assertFalse(model.ui.value.progression)
        } finally { instrumentation.runOnMainSync { model.home() } }
    }
}
