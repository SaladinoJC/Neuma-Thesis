package ar.edu.unmdp.entrenamiento.voice

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GivenAnchorGameTest {
    @Test fun `failed given save keeps the same target only record and blocks new practice`() = gameTest {
        val memory = MemoryVocalIntervalResults()
        var fail = true
        val store = object : VocalIntervalResults {
            override suspend fun all() = memory.all()
            override suspend fun save(record: VocalIntervalRecord) { if (fail) error("disk unavailable"); memory.save(record) }
        }
        val model = model(store)
        model.selectScaffolding(2); model.awaitStorage()
        complete(model)
        val pending = model.pendingForExport().single()
        model.newRound(); model.selectScaffolding(1)
        assertEquals(2, model.ui.value.scaffoldingLevel)
        assertEquals(pending.id, model.ui.value.currentRecordId)
        fail = false; model.retryHistory(); model.awaitStorage()
        assertEquals(pending.id, memory.all().single().id)
        assertTrue(memory.all().single().result.measurements.all { it.phase == VocalIntervalPhase.TARGET })
        model.newRound()
        assertEquals(2, model.ui.value.scaffoldingLevel)
        assertFalse(model.ui.value.heardReference)
        model.stopAndJoin()
    }

    @Test fun `background and late cleanup callback never turn a given interruption into a completed response`() = gameTest {
        val capture = object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                onMeasurement(PitchEstimate(329.6275569128699), 10, 44100)
                try { awaitCancellation() }
                finally { withContext(NonCancellable) { onMeasurement(PitchEstimate(880.0), 10, 44100) } }
            }
        }
        val model = VocalIntervalViewModel(capture, object : ReferencePlayer { override suspend fun play(midi: Int) = 26460 }, { testScheduler.currentTime })
        model.selectScaffolding(2); model.awaitStorage()
        model.playReference(); advanceTimeBy(200); runCurrent()
        model.startGivenTarget(); runCurrent()
        model.background(); model.stopAndJoin()
        val record = model.ui.value.history.records.single()
        assertEquals("background", record.result.interruption)
        assertEquals(VocalAnchorSource.GIVEN, record.result.anchorSource)
        assertEquals(1, record.result.measurements.size)
        assertNull(record.result.inTune)
    }

    @Test fun `given manual success does not bypass its imitation prerequisite`() = gameTest {
        val model = model(MemoryVocalIntervalResults(), 220.0, automatic = true)
        model.selectScaffolding(2); model.awaitStorage()
        model.beginSeries(); model.awaitStorage()
        assertNull(model.ui.value.series)
        assertNotNull(model.ui.value.error)
        model.progression(false)
        complete(model)
        assertEquals("manual", model.ui.value.history.records.single().origin)
        model.newRound(); model.progression(true); model.awaitStorage()
        assertTrue(model.ui.value.history.progress.unlockedFor(2).isEmpty())
        model.beginSeries(); model.awaitStorage()
        assertNull(model.ui.value.series)
        model.stopAndJoin()
    }

    @Test fun `given automatic series uses five captures and starts with its own tolerance`() = gameTest {
        val memory = MemoryVocalIntervalResults()
        simulateVocalProgress(VocalDdaScenario.GOOD).records.forEach { memory.save(it) }
        var captures = 0
        val played = mutableListOf<Int>()
        val capture = object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                captures++
                while (true) { onMeasurement(PitchEstimate(220.0), 10, 44100); delay(50) }
            }
        }
        val model = VocalIntervalViewModel(capture, object : ReferencePlayer {
            override suspend fun play(midi: Int): Int { played += midi; return 26460 }
        }, { testScheduler.currentTime }, resultsStore = memory, automaticInitially = true)
        model.selectScaffolding(2); model.awaitStorage(); model.beginSeries(); model.awaitStorage()
        assertEquals(50.0, model.ui.value.toleranceCents, .0)
        repeat(5) { index -> complete(model); if (index < 4) model.newRound() }
        assertEquals(5, captures)
        assertEquals(List(5) { 57 }, played)
        assertTrue(model.ui.value.series!!.ended)
        assertEquals(2, model.ui.value.history.progress[0].highSeries)
        assertEquals(1, model.ui.value.history.progress.forLevel(0, 2).highSeries)
        assertTrue(model.ui.value.history.records.takeLast(5).all { it.scaffoldingLevel == 2 && it.result.measurements.size == 11 })
        model.stopAndJoin()
    }

    private fun gameTest(body: suspend TestScope.() -> Unit) = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try { body() } finally { Dispatchers.resetMain() }
    }

    private fun TestScope.model(store: VocalIntervalResults, hz: Double = 329.6275569128699, automatic: Boolean = false) =
        VocalIntervalViewModel(object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                while (true) { onMeasurement(PitchEstimate(hz), 10, 44100); delay(50) }
            }
        }, object : ReferencePlayer { override suspend fun play(midi: Int) = 26460 },
            { testScheduler.currentTime }, resultsStore = store, automaticInitially = automatic)

    private suspend fun TestScope.complete(model: VocalIntervalViewModel) {
        model.playReference(); advanceTimeBy(200); runCurrent()
        model.startGivenTarget(); advanceTimeBy(600); runCurrent(); model.awaitStorage()
    }

    @Test fun `given anchor plays no target and captures only one sung note`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val played = mutableListOf<Int>()
            var active = false
            var captures = 0
            val capture = object : VoiceCapture {
                override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                    active = true; captures++
                    try { while (true) { onMeasurement(PitchEstimate(329.6275569128699), 10, 44100); delay(50) } }
                    finally { active = false }
                }
            }
            val player = object : ReferencePlayer {
                override suspend fun play(midi: Int): Int { assertFalse(active); played += midi; return 26460 }
            }
            val model = VocalIntervalViewModel(capture, player, { testScheduler.currentTime })
            model.selectScaffolding(2); model.awaitStorage()
            model.startGivenTarget(); runCurrent()
            assertEquals(0, captures)
            model.playReference(); advanceTimeBy(200); runCurrent()
            assertEquals(listOf(57), played)
            model.startGivenTarget(); advanceTimeBy(600); runCurrent(); model.awaitStorage()
            val record = model.ui.value.history.records.single()
            assertEquals(2, record.scaffoldingLevel)
            assertEquals(VocalAnchorSource.GIVEN, record.result.anchorSource)
            assertEquals(11, record.result.measurements.size)
            assertEquals(1, captures)
            assertEquals(700.0, record.result.intervalCents!!, .001)
            assertFalse(active)
            model.stopAndJoin()
        } finally { Dispatchers.resetMain() }
    }
}
