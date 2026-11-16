package ar.edu.unmdp.entrenamiento.defender

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefenderD3ViewModelTest {
    private val silent = object : DefenderAudio {
        override suspend fun note(midi: Int) = 0
        override suspend fun effect(effect: DefenderEffect) = 0
    }

    @Test fun pausaNoFinalizaReabrirNoIniciaAudioYModosNoMezclanEtapas() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler); Dispatchers.setMain(dispatcher)
        try {
            val store = MemoryDefenderResults()
            val model = DefenderViewModel(silent, clock = { testScheduler.currentTime }, resultsStore = store)
            runCurrent(); model.progression(true); model.stage(DefenderStage.N4)
            assertEquals(DefenderStage.N1, model.ui.value.stage)
            model.start(7); runCurrent(); model.background(); runCurrent()
            assertEquals(DefenderPhase.PAUSED, model.ui.value.game.phase)
            assertTrue(store.all().isEmpty())
            advanceTimeBy(50_000); runCurrent()
            assertEquals(0L, model.ui.value.game.activeMillis)
            model.openInspector(); model.closeInspector()
            assertEquals(DefenderPhase.PAUSED, model.ui.value.game.phase)
            model.home(); runCurrent(); model.awaitIdle()
            assertEquals(DefenderPhase.INTERRUPTED, store.all().single().game.phase)
            assertEquals(0, model.ui.value.history.progress[DefenderStage.N1].highStreak)
            val reopened = DefenderViewModel(silent, clock = { testScheduler.currentTime }, resultsStore = store)
            runCurrent(); assertEquals(1, reopened.ui.value.history.results.size)
            assertEquals(DefenderPhase.SETUP, reopened.ui.value.game.phase)
            reopened.stage(DefenderStage.N4); reopened.travel(14_000); reopened.start(9); runCurrent()
            assertEquals("practice", reopened.ui.value.game.origin)
            assertEquals(14_000L, reopened.ui.value.game.currentTravelMillis)
            model.home(); reopened.home(); runCurrent()
        } finally { Dispatchers.resetMain() }
    }

    @Test fun simulacionesCalculadasAlcanzanLimitesYNoTocanPerfil() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler); Dispatchers.setMain(dispatcher)
        try {
            val store = MemoryDefenderResults()
            val model = DefenderViewModel(silent, resultsStore = store, simulationDispatcher = dispatcher)
            runCurrent()
            model.simulate(DefenderScenario.UNLOCK); runCurrent()
            assertTrue(DefenderStage.N2 in model.ui.value.simulation!!.progress.unlocked)
            assertEquals(setOf(DefenderStage.N1), model.ui.value.history.progress.unlocked)
            assertTrue(store.all().isEmpty())
            val reports = DefenderScenario.entries.map { simulateDefender(it) }
            val fast = reports.first { it.scenario == DefenderScenario.FAST.label }.records.single().game
            val slow = reports.first { it.scenario == DefenderScenario.SLOW.label }.records.single().game
            assertEquals(3000L, fast.currentTravelMillis)
            assertEquals(14_000L, slow.currentTravelMillis)
            assertTrue(fast.ddaDecisions.any { it.reason == DefenderDdaReason.COOLDOWN })
            val steady = reports.first { it.scenario == DefenderScenario.STEADY.label }.records.single().game
            assertTrue(steady.ddaDecisions.filter { it.afterResolved <= 24 }.all { it.afterTravelMillis == 10_000L })
            val practice = reports.first { it.scenario == DefenderScenario.PRACTICE.label }
            assertEquals(setOf(DefenderStage.N1), practice.progress.unlocked)
            val dir = java.io.File("build/verification/defender-d3").apply { mkdirs() }
            java.io.File(dir, "scenarios.json").writeText(kotlinx.serialization.json.Json.encodeToString(
                kotlinx.serialization.builtins.ListSerializer(DefenderSimulation.serializer()), reports))
            model.home(); runCurrent()
        } finally { Dispatchers.resetMain() }
    }

    @Test fun terceraLlegadaCorregidaAntesDeCerrarNoRegalaPartidaCompletaNiDesbloqueos() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val store = MemoryDefenderResults(); var now = 0L
            val model = DefenderViewModel(silent, clock = { now }, resultsStore = store)
            runCurrent(); model.progression(true); model.start(42); runCurrent()
            repeat(24) {
                while (model.ui.value.game.enemies.isEmpty()) { now += 100; model.tick() }
                val enemy = model.ui.value.game.enemies.minBy { it.appearedAtMillis }
                now++; model.select(enemy.note.name); model.fire()
            }
            while (model.ui.value.game.phase == DefenderPhase.RUNNING) { now += 100; model.tick() }
            runCurrent(); model.awaitIdle()
            val provisional = store.all().single()
            assertFalse(provisional.sealed)
            assertEquals(0, model.ui.value.history.progress[DefenderStage.N1].highStreak)
            val last = model.ui.value.game.results.last()
            model.firePointer(last.enemy.deadlineMillis - 1, now, last.enemy.note.name)
            runCurrent()
            assertEquals(DefenderPhase.RUNNING, model.ui.value.game.phase)
            model.home(); runCurrent(); model.awaitIdle()
            val final = store.all().single()
            assertTrue(final.sealed)
            assertEquals(DefenderPhase.INTERRUPTED, final.game.phase)
            assertEquals(0, model.ui.value.history.progress[DefenderStage.N1].highStreak)
        } finally { Dispatchers.resetMain() }
    }

    @Test fun guardadoPendienteBloqueaProximaProgresionYReintentoConservaUnSoloIntento() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var failed = true; var now = 0L; val disk = MemoryDefenderResults()
            val store = object : DefenderResults {
                override suspend fun all() = disk.all()
                override suspend fun commit(record: DefenderRecord): DefenderRecord {
                    if (failed) error("Disco no disponible")
                    return disk.commit(record)
                }
            }
            val model = DefenderViewModel(silent, clock = { now }, resultsStore = store)
            runCurrent(); model.progression(true); model.start(7); runCurrent()
            now = 60_000; model.tick(); runCurrent(); model.prepare(); runCurrent()
            val pending = model.pendingForExport().single()
            assertTrue(pending.sealed)
            model.start(8); runCurrent()
            assertEquals(DefenderPhase.SETUP, model.ui.value.game.phase)
            assertNotNull(model.ui.value.error)
            failed = false; model.retryHistory(); runCurrent(); model.awaitIdle()
            assertEquals(pending.id, disk.all().single().id)
            assertTrue(model.pendingForExport().isEmpty())
            model.retryHistory(); runCurrent(); model.awaitIdle(); assertEquals(1, disk.all().size)
            model.home(); runCurrent()
        } finally { Dispatchers.resetMain() }
    }
}
