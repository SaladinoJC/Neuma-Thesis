package ar.edu.unmdp.entrenamiento.voice

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import ar.edu.unmdp.entrenamiento.storage.Demonstration
import java.io.File
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.math.pow

class VocalProgressionTest {
    @get:Rule val temporary = TemporaryFolder()

    @Test fun `whole synthetic route uses public sessions and masters all signed intervals`() = runTest {
        val report = simulateVocalProgress(VocalDdaScenario.ROUTE)
        assertEquals(250, report.records.size)
        assertEquals(25, report.progress.unlocked.size)
        assertTrue(vocalCurriculum.flatten().all { report.progress[it].mastered })
        assertTrue(report.records.all { it.dda?.eligible == true && it.result.interruption == null })
        File("build/verification/vocal-v4/synthetic-route.json").apply {
            parentFile.mkdirs(); writeText(Json.encodeToString(VocalSimulationReport.serializer(), report))
        }
    }

    @Test fun `poor octave interrupted and manual scenarios never grant mastery`() = runTest {
        for (scenario in listOf(VocalDdaScenario.LOW, VocalDdaScenario.OCTAVE, VocalDdaScenario.INTERRUPTED, VocalDdaScenario.MANUAL)) {
            val report = simulateVocalProgress(scenario)
            assertFalse(report.progress[0].mastered)
            assertEquals(0, report.progress[0].highSeries)
            assertEquals(setOf(0, 12, -12), report.progress.unlocked)
            if (scenario == VocalDdaScenario.MANUAL) {
                assertEquals(50, report.progress[0].toleranceCents)
                assertTrue(report.records.all { it.dda == null && it.origin == "manual" })
            }
            if (scenario == VocalDdaScenario.LOW || scenario == VocalDdaScenario.OCTAVE) assertEquals(80, report.progress[0].toleranceCents)
            if (scenario == VocalDdaScenario.OCTAVE) assertEquals(1200.0, report.records.first().result.errorCents!!, .001)
        }
    }

    @Test fun `curriculum needs both octave directions and acquired access survives a poor series`() = runTest {
        val history = VocalIntervalHistorySession(MemoryVocalIntervalResults())
        for (key in listOf(0, 12)) for (series in 0..1) for (i in 1..5) addRound(history, key, "$key-$series", i)
        assertFalse(7 in history.state.progress.unlocked)
        for (series in 0..1) for (i in 1..5) addRound(history, -12, "down-$series", i)
        assertEquals(setOf(0, 12, -12, 7, -7, 5, -5), history.state.progress.unlocked)
        for (i in 1..5) addRound(history, 0, "poor", i, error = 100)
        assertTrue(history.state.progress[0].mastered)
        assertEquals(2, history.state.progress[0].masteryEvidence.size)
        assertEquals(0, history.state.progress[0].highSeries)
        assertTrue(7 in history.state.progress.unlocked)
    }

    @Test fun `locked stale or repeated series ordinals do not change recommendations`() = runTest {
        val history = VocalIntervalHistorySession(MemoryVocalIntervalResults())
        addRound(history, 7, "locked", 1)
        assertEquals(VocalDdaReason.LOCKED, history.state.records.last().dda!!.reason)
        addRound(history, 0, "ordered", 1)
        assertEquals(45, history.state.progress[0].toleranceCents)
        addRound(history, 0, "ordered", 1)
        assertEquals(VocalDdaReason.INVALID_SERIES, history.state.records.last().dda!!.reason)
        addRound(history, 0, "stale", 1, tolerance = 50)
        assertEquals(VocalDdaReason.STALE, history.state.records.last().dda!!.reason)
        assertEquals(45, history.state.progress[0].toleranceCents)
    }

    @Test fun `incomplete series cannot bridge two high series into a mastery hit`() = runTest {
        val history = VocalIntervalHistorySession(MemoryVocalIntervalResults())
        for (i in 1..5) addRound(history, 0, "complete", i)
        addRound(history, 0, "abandoned", 1)
        assertEquals(1, history.state.progress[0].highSeries)
        addRound(history, 0, "fresh", 1)
        assertTrue(history.state.records.last().dda!!.abandonedPreviousSeries)
        assertEquals(0, history.state.progress[0].highSeries)
        for (i in 2..5) addRound(history, 0, "fresh", i)
        assertEquals(1, history.state.progress[0].highSeries)
        assertFalse(history.state.progress[0].mastered)
    }

    @Test fun `interruption and shorter holding protocol never supply unlock evidence`() = runTest {
        val history = VocalIntervalHistorySession(MemoryVocalIntervalResults())
        addRound(history, 0, "interrupt", 1, interrupted = true)
        assertEquals(VocalDdaReason.INTERRUPTED, history.state.records.last().dda!!.reason)
        addRound(history, 0, "short", 1, hold = 100)
        assertEquals(VocalDdaReason.INSUFFICIENT, history.state.records.last().dda!!.reason)
        assertEquals(50, history.state.progress[0].toleranceCents)
    }

    @Test fun `regularity affects inference even when the stable note is within tolerance`() = runTest {
        val history = VocalIntervalHistorySession(MemoryVocalIntervalResults())
        addRound(history, 0, "variable", 1, alternating = true)
        val record = history.state.records.single()
        assertEquals(true, record.result.inTune)
        assertTrue(record.dda!!.deviationCents!! > 15)
        assertTrue(record.dda!!.inference!!.centroid!! < .7)
        assertNotEquals("alto", record.dda!!.band)
    }

    @Test fun `retry after an uncertain commit confirms the original decision only once`() = runTest {
        val file = File(temporary.root, "vocal-interval-results.json")
        val disk = JsonVocalIntervalResults(file)
        var fail = true
        val store = object : VocalIntervalResults {
            override suspend fun all() = disk.all()
            override suspend fun save(record: VocalIntervalRecord) { disk.save(record); if (fail) error("confirmación incierta") }
        }
        val history = VocalIntervalHistorySession(store)
        addRound(history, 0, "uncertain", 1)
        assertEquals(50, history.state.progress[0].toleranceCents)
        assertEquals(1, history.state.pending.size)
        fail = false; history.savePending(); history.savePending()
        val reopened = JsonVocalIntervalResults(file).all()
        assertEquals(1, reopened.size)
        assertEquals(1, reopened.single().dda!!.sequence)
        assertEquals(45, history.state.progress[0].toleranceCents)
        assertTrue(history.state.pending.isEmpty())
    }

    @Test fun `decisions snapshots and progression survive JSON reopen export duplicate and reset`() = runTest {
        val file = File(temporary.root, "vocal-interval-results.json")
        val store = JsonVocalIntervalResults(file)
        val history = VocalIntervalHistorySession(store)
        for (series in 0..1) for (i in 1..5) addRound(history, 0, "json-$series", i)
        val saved = store.all()
        store.save(saved.last())
        val reopened = VocalIntervalHistorySession(JsonVocalIntervalResults(file))
        reopened.load()
        assertEquals(10, reopened.state.records.size)
        assertTrue(reopened.state.progress[0].mastered)
        assertEquals(saved.last().dda, reopened.state.records.last().dda)
        val export = Json.parseToJsonElement(Demonstration(temporary.root).export("test")).jsonObject
        val state = export.getValue("vocalIntervalProgress").jsonObject.getValue("intervals").jsonObject.getValue("0").jsonObject
        assertTrue(state.getValue("mastered").jsonPrimitive.boolean)
        Demonstration(temporary.root).reset(); reopened.load()
        assertEquals(VocalProgress(), reopened.state.progress)
    }

    private suspend fun addRound(history: VocalIntervalHistorySession, key: Int, id: String, ordinal: Int,
        error: Int = 0, tolerance: Int = history.state.progress[key].toleranceCents,
        interrupted: Boolean = false, hold: Long = 500, alternating: Boolean = false) {
        history.begin(57, key, 1, "Synthetic" to "JVM", "test", origin = "dda", series = VocalSeriesStep(id, ordinal))
        val round = VocalIntervalSession(key, VocalIntervalConfiguration(toleranceCents = tolerance.toDouble(), holdMillis = hold))
        round.beginAnchor()
        for (t in 0L..hold step 50) round.accept(t, PitchEstimate(220.0))
        if (interrupted) round.interrupt()
        else {
            round.beginTarget()
            for (t in (hold + 100)..(hold * 2 + 100) step 50) {
                val cents = key * 100 + error + if (alternating) if ((t / 50) % 2 == 0L) 16 else -16 else 0
                round.accept(t, PitchEstimate(220 * 2.0.pow(cents / 1200.0)))
            }
        }
        history.finish(requireNotNull(round.state.result), 44100, null)
        history.savePending()
    }

    @Test fun `two complete high series master their own interval and preserve inspected decisions`() = runTest {
        val store = MemoryVocalIntervalResults()
        val history = VocalIntervalHistorySession(store)
        repeat(2) { series ->
            repeat(5) { index ->
                val tolerance = history.state.progress[0].toleranceCents
                history.begin(57, 0, 1, "Synthetic" to "JVM", "test", origin = "dda",
                    series = VocalSeriesStep("series-$series", index + 1))
                val round = VocalIntervalSession(0, VocalIntervalConfiguration(toleranceCents = tolerance.toDouble()))
                round.beginAnchor()
                for (t in 0L..500L step 50) round.accept(t, PitchEstimate(220.0))
                round.beginTarget()
                for (t in 600L..1100L step 50) round.accept(t, PitchEstimate(220.0))
                history.finish(round.state.result!!, 44100, null)
                history.savePending()
            }
        }
        val state = history.state.progress[0]
        assertTrue(state.mastered)
        assertEquals(30, state.toleranceCents)
        assertFalse(history.state.progress[12].mastered)
        assertEquals(setOf(0, 12, -12), history.state.progress.unlocked)
        val decision = history.state.records.last().dda!!
        assertTrue(decision.seriesComplete)
        assertEquals(2, decision.after[0].highSeries)
        assertTrue(decision.inference!!.centroid!! > .7)
        assertEquals(5, decision.inference!!.rules.size)
        assertEquals(10, store.all().size)
    }
}
