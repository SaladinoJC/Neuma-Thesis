package ar.edu.unmdp.entrenamiento.defender

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefenderChordViewModelTest {
    private val silent = object : DefenderAudio {
        override suspend fun note(midi: Int) = 0
        override suspend fun chord(midis: List<Int>) = 0
        override suspend fun effect(effect: DefenderEffect) = 0
    }

    @Test fun calidadBloqueadaCierreDeResumenYReaperturaConservanAvancesSeparados() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val store = MemoryDefenderResults(); var now = 0L
            for (id in listOf("n1", "n2")) store.commit(DefenderRecord(id, 1, 2, DefenderHarness().finish()))
            val noteProgress = defenderProgress(store.all())
            val model = DefenderViewModel(silent, clock = { now }, resultsStore = store)
            runCurrent(); model.variant("chords"); model.progression(true); model.chordQuality(DefenderChordQuality.MINOR)
            assertEquals(DefenderChordQuality.MAJOR, model.ui.value.chordQuality)
            repeat(2) { index ->
                model.start(index); runCurrent()
                repeat(24) {
                    while (model.ui.value.game.enemies.isEmpty()) { now += 100; model.tick() }
                    val chord = model.ui.value.game.enemy!!.chord!!
                    now++; model.select(chord.root.name); model.accidental(chord.root.accidental); model.quality(chord.quality); model.fire()
                }
                while (model.ui.value.game.phase == DefenderPhase.RUNNING) { now += 100; model.tick() }
                runCurrent(); model.awaitIdle()
                assertEquals(if (index == 0) 0 else 1, model.ui.value.progress[DefenderChordQuality.MAJOR].highStreak)
                model.openInspector(); runCurrent(); model.awaitIdle(); model.closeInspector(); model.prepare(); runCurrent()
            }
            assertTrue(DefenderChordQuality.MINOR in model.ui.value.progress.unlockedQualities)
            assertEquals(noteProgress, defenderProgress(store.all()))
            val reopened = DefenderViewModel(silent, clock = { now }, resultsStore = store)
            runCurrent(); reopened.variant("chords"); reopened.progression(true); reopened.chordQuality(DefenderChordQuality.MINOR)
            assertEquals(DefenderChordQuality.MINOR, reopened.ui.value.chordQuality)
            reopened.start(3); runCurrent(); assertEquals(10_000L, reopened.ui.value.game.currentTravelMillis)
            model.home(); reopened.home(); runCurrent()
        } finally { Dispatchers.resetMain() }
    }

    @Test fun simulacionDeTriadasNoModificaNotasNiPerfilReal() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler); Dispatchers.setMain(dispatcher)
        try {
            val store = MemoryDefenderResults()
            val model = DefenderViewModel(silent, resultsStore = store, simulationDispatcher = dispatcher)
            runCurrent(); model.variant("chords"); model.simulate(DefenderScenario.UNLOCK); runCurrent()
            assertEquals("chords", model.ui.value.simulation!!.variant)
            assertTrue(DefenderChordQuality.MINOR in model.ui.value.simulation!!.progress.unlockedQualities)
            assertEquals(setOf(DefenderChordQuality.MAJOR), model.ui.value.progress.unlockedQualities)
            assertTrue(store.all().isEmpty())
            model.variant("notes"); runCurrent()
            assertNull(model.ui.value.simulation)
            assertEquals(setOf(DefenderStage.N1), model.ui.value.progress.unlocked)
            model.home(); runCurrent()
        } finally { Dispatchers.resetMain() }
    }
}
