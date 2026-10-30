package ar.edu.unmdp.entrenamiento.voice

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VocalAutomaticTest {
    @Test fun `pending automatic result blocks continuation until its single decision is confirmed`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val memory = MemoryVocalIntervalResults()
            var failing = true
            val store = object : VocalIntervalResults {
                override suspend fun all() = memory.all()
                override suspend fun save(record: VocalIntervalRecord) { if (failing) error("offline storage"); memory.save(record) }
            }
            val model = model(store)
            model.progression(true); model.awaitStorage(); model.selectInterval(0); model.beginSeries(); model.awaitStorage()
            complete(model)
            assertEquals(50, model.ui.value.history.progress[0].toleranceCents)
            model.newRound()
            assertEquals(1, model.ui.value.series!!.ordinal)
            assertEquals(1, model.pendingForExport().size)
            failing = false; model.retryHistory(); model.awaitStorage()
            assertEquals(45, model.ui.value.history.progress[0].toleranceCents)
            model.newRound()
            assertEquals(2, model.ui.value.series!!.ordinal)
            assertEquals(45.0, model.ui.value.toleranceCents, .0)
            assertEquals(1, memory.all().size)
            model.stopAndJoin()
        } finally { Dispatchers.resetMain() }
    }

    @Test fun `background between rounds closes a partial series and another starts at ordinal one`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val model = model(MemoryVocalIntervalResults())
            model.progression(true); model.awaitStorage(); model.selectInterval(0); model.beginSeries(); model.awaitStorage()
            model.newRound()
            assertEquals(1, model.ui.value.series!!.ordinal)
            complete(model); model.newRound()
            val old = model.ui.value.series!!.id
            model.background(); runCurrent()
            assertTrue(model.ui.value.series!!.ended)
            model.newRound(); model.beginSeries(); model.awaitStorage()
            assertNotEquals(old, model.ui.value.series!!.id)
            assertEquals(1, model.ui.value.series!!.ordinal)
            assertEquals(0, model.ui.value.history.progress[0].highSeries)
            assertEquals(1, model.ui.value.history.records.size)
            model.stopAndJoin()
        } finally { Dispatchers.resetMain() }
    }

    @Test fun `unreadable history prevents automatic series and simulation writes no player record`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val store = object : VocalIntervalResults {
                override suspend fun all(): List<VocalIntervalRecord> = error("perfil ilegible")
                override suspend fun save(record: VocalIntervalRecord) = error("must not save")
            }
            val model = model(store)
            model.progression(true); model.awaitStorage(); model.beginSeries(); model.awaitStorage()
            assertNull(model.ui.value.series)
            assertNotNull(model.ui.value.history.error)
            model.playReference(); runCurrent()
            assertFalse(model.ui.value.heardReference)
            model.simulate(VocalDdaScenario.GOOD)
            model.ui.first { !it.simulationBusy && it.simulation != null }
            assertEquals(10, model.ui.value.simulation!!.records.size)
            assertTrue(model.ui.value.history.records.isEmpty())
            assertTrue(model.pendingForExport().isEmpty())
            model.stopAndJoin()
        } finally { Dispatchers.resetMain() }
    }

    private fun TestScope.model(store: VocalIntervalResults) = VocalIntervalViewModel(
        object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                while (true) { onMeasurement(PitchEstimate(220.0), 10, 44100); delay(50) }
            }
        }, object : ReferencePlayer { override suspend fun play(midi: Int) = 26460 },
        { testScheduler.currentTime }, resultsStore = store)

    private suspend fun TestScope.complete(model: VocalIntervalViewModel) {
        model.playReference(); advanceTimeBy(200); runCurrent()
        model.startAnchor(); advanceTimeBy(600); runCurrent()
        model.startTarget(); advanceTimeBy(600); runCurrent(); model.awaitStorage()
    }

    @Test fun `automatic series changes only future tolerance and records five linked rounds`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val capture = object : VoiceCapture {
                override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                    while (true) { onMeasurement(PitchEstimate(220.0), 10, 44100); delay(50) }
                }
            }
            val player = object : ReferencePlayer { override suspend fun play(midi: Int) = 26460 }
            val model = VocalIntervalViewModel(capture, player, { testScheduler.currentTime })
            model.progression(true); model.awaitStorage()
            model.selectInterval(0); model.beginSeries(); model.awaitStorage()
            val id = model.ui.value.series!!.id
            repeat(5) { index ->
                val before = model.ui.value.toleranceCents
                model.playReference(); advanceTimeBy(200); runCurrent()
                model.startAnchor(); advanceTimeBy(600); runCurrent()
                model.startTarget(); advanceTimeBy(600); runCurrent(); model.awaitStorage()
                val result = model.ui.value.round!!.result!!
                assertEquals(before, result.configuration.toleranceCents, 0.0)
                assertEquals(index + 1, model.ui.value.series!!.ordinal)
                if (index < 4) model.newRound()
            }
            assertTrue(model.ui.value.series!!.ended)
            assertEquals(1, model.ui.value.history.progress[0].highSeries)
            assertEquals(listOf(1, 2, 3, 4, 5), model.ui.value.history.records.map { it.series!!.ordinal })
            assertTrue(model.ui.value.history.records.all { it.origin == "dda" && it.series!!.id == id })
            assertEquals(30, model.ui.value.history.progress[0].toleranceCents)
            model.stopAndJoin()
        } finally { Dispatchers.resetMain() }
    }
}
