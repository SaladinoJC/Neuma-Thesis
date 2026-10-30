package ar.edu.unmdp.entrenamiento.voice

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FreeProductionGameTest {
    @Test fun `manual free success grants no access and switching help requires a fresh reference`() = gameTest {
        val model = model(automatic = true)
        model.selectScaffolding(3); model.awaitStorage()
        model.beginSeries(); model.awaitStorage()
        assertNull(model.ui.value.series)
        assertNotNull(model.ui.value.error)
        model.progression(false)
        complete(model)
        assertEquals("manual", model.ui.value.history.records.single().origin)
        assertTrue(model.ui.value.history.progress.unlockedFor(3).isEmpty())
        model.newRound(); model.selectScaffolding(1); model.awaitStorage()
        model.startAnchor(); runCurrent()
        assertNull(model.ui.value.round)
        assertFalse(model.ui.value.heardReference)
        model.stopAndJoin()
    }

    @Test fun `free series uses two captures per round and only adapts its own future tolerance`() = gameTest {
        val memory = MemoryVocalIntervalResults()
        simulateVocalProgress(VocalDdaScenario.GOOD, 2).records.forEach { memory.save(it) }
        var captures = 0
        val model = model(memory, automatic = true, onCapture = { captures++ })
        model.selectScaffolding(3); model.awaitStorage(); model.beginSeries(); model.awaitStorage()
        assertEquals(50.0, model.ui.value.toleranceCents, .0)
        repeat(5) { index ->
            val tolerance = model.ui.value.toleranceCents
            complete(model)
            assertEquals(tolerance, model.ui.value.round!!.result!!.configuration.toleranceCents, .0)
            if (index < 4) model.newRound()
        }
        assertEquals(10, captures)
        assertTrue(model.ui.value.series!!.ended)
        assertEquals(1, model.ui.value.history.progress.forLevel(0, 3).highSeries)
        assertEquals(30, model.ui.value.history.progress.forLevel(0, 3).toleranceCents)
        assertEquals(2, model.ui.value.history.progress.forLevel(0, 2).highSeries)
        assertEquals(2, model.ui.value.history.progress[0].highSeries)
        assertTrue(model.ui.value.history.records.takeLast(5).all { it.referenceListens == 0 && it.result.measurements.size == 22 })
        model.stopAndJoin()
    }

    @Test fun `silence times out between freely sung notes without starting the second capture`() = gameTest {
        var captures = 0
        val model = model(onCapture = { captures++ }, configuration = VocalIntervalConfiguration(timeoutMillis = 1000))
        model.selectScaffolding(3); model.awaitStorage()
        model.startAnchor(); advanceTimeBy(600); runCurrent()
        assertEquals(VocalIntervalPhase.READY_TARGET, model.ui.value.round!!.phase)
        advanceTimeBy(500); runCurrent(); model.awaitStorage()
        val record = model.ui.value.history.records.single()
        assertEquals("timeout", record.result.interruption)
        assertNull(record.result.targetHz)
        assertEquals(1, captures)
        model.startTarget(); runCurrent()
        assertEquals(1, captures)
        assertEquals(0, record.referenceListens)
        model.stopAndJoin()
    }

    @Test fun `background rejects late free input and a failed save retains the same recoverable record`() = gameTest {
        val memory = MemoryVocalIntervalResults()
        var fail = true
        val store = object : VocalIntervalResults {
            override suspend fun all() = memory.all()
            override suspend fun save(record: VocalIntervalRecord) { if (fail) error("disk unavailable"); memory.save(record) }
        }
        val capture = object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                onMeasurement(PitchEstimate(196.0), 10, 44100)
                try { awaitCancellation() }
                finally { withContext(NonCancellable) { onMeasurement(PitchEstimate(392.0), 10, 44100) } }
            }
        }
        val model = VocalIntervalViewModel(capture, silentPlayer(), { testScheduler.currentTime }, resultsStore = store)
        model.selectScaffolding(3); model.awaitStorage(); model.startAnchor(); runCurrent()
        model.background(); model.stopAndJoin()
        val pending = model.pendingForExport().single()
        assertEquals("background", pending.result.interruption)
        assertEquals(1, pending.result.measurements.size)
        assertNull(pending.referenceAnchorMidi)
        model.newRound(); model.selectScaffolding(1)
        assertEquals(pending.id, model.ui.value.currentRecordId)
        assertEquals(3, model.ui.value.scaffoldingLevel)
        fail = false; model.retryHistory(); model.awaitStorage(); model.retryHistory(); model.awaitStorage()
        assertEquals(pending.id, memory.all().single().id)
        model.newRound()
        assertEquals(3, model.ui.value.scaffoldingLevel)
        assertNull(model.ui.value.round)
        model.stopAndJoin()
    }

    private fun gameTest(body: suspend TestScope.() -> Unit) = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try { body() } finally { Dispatchers.resetMain() }
    }
    private fun silentPlayer() = object : ReferencePlayer {
        override suspend fun play(midi: Int): Int = error("Free production must be silent")
    }
    private fun TestScope.model(store: VocalIntervalResults = MemoryVocalIntervalResults(), automatic: Boolean = false,
        onCapture: () -> Unit = {}, configuration: VocalIntervalConfiguration = VocalIntervalConfiguration()) =
        VocalIntervalViewModel(object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                onCapture()
                while (true) { onMeasurement(PitchEstimate(196.0), 10, 44100); delay(50) }
            }
        }, silentPlayer(), { testScheduler.currentTime }, configuration, resultsStore = store, automaticInitially = automatic)
    private suspend fun TestScope.complete(model: VocalIntervalViewModel) {
        model.startAnchor(); advanceTimeBy(600); runCurrent()
        model.startTarget(); advanceTimeBy(600); runCurrent(); model.awaitStorage()
    }

    @Test fun `free production sings a chosen anchor and target without any reference playback`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var captures = 0
            var active = false
            val capture = object : VoiceCapture {
                override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                    assertFalse(active)
                    active = true
                    val hz = if (++captures == 1) 196.0 else 392.0
                    try { while (true) { onMeasurement(PitchEstimate(hz), 10, 44100); delay(50) } }
                    finally { active = false }
                }
            }
            val model = VocalIntervalViewModel(capture, object : ReferencePlayer {
                override suspend fun play(midi: Int): Int = error("Free production must be silent")
            }, { testScheduler.currentTime })
            model.selectScaffolding(3); model.awaitStorage()
            model.selectInterval(12)
            assertEquals(3, model.ui.value.scaffoldingLevel)
            model.playReference(); runCurrent()
            assertFalse(model.ui.value.heardReference)
            model.startAnchor(); advanceTimeBy(600); runCurrent()
            assertEquals(VocalIntervalPhase.READY_TARGET, model.ui.value.round!!.phase)
            assertEquals(196.0, model.ui.value.round!!.anchorHz!!, .001)
            assertFalse(active)
            model.startTarget(); advanceTimeBy(600); runCurrent(); model.awaitStorage()
            val record = model.ui.value.history.records.single()
            assertEquals(3, record.scaffoldingLevel)
            assertEquals(0, record.referenceListens)
            assertNull(record.referenceAnchorMidi)
            assertNull(record.referenceTargetMidi)
            assertEquals(VocalAnchorSource.SUNG, record.result.anchorSource)
            assertEquals(1200.0, record.result.intervalCents!!, .001)
            assertEquals(22, record.result.measurements.size)
            assertEquals(2, captures)
            assertFalse(active)
            model.stopAndJoin()
        } finally { Dispatchers.resetMain() }
    }
}
