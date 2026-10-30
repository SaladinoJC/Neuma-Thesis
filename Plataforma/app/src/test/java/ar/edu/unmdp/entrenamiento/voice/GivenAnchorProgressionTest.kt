package ar.edu.unmdp.entrenamiento.voice

import ar.edu.unmdp.entrenamiento.storage.Demonstration
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*
import org.junit.*
import org.junit.Assert.*
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.math.pow

class GivenAnchorProgressionTest {
    @get:Rule val temporary = TemporaryFolder()

    @Test fun `imitation grants access but never transfers tolerance streak or mastery`() = runTest {
        val history = VocalIntervalHistorySession(MemoryVocalIntervalResults())
        add(history, 2, 0, "locked", 1)
        assertEquals(VocalDdaReason.LOCKED, history.state.records.last().dda!!.reason)
        masterImitation(history, 0)
        assertEquals(setOf(0), history.state.progress.unlockedFor(2))
        val imitation = history.state.progress[0]
        assertEquals(30, imitation.toleranceCents)
        assertEquals(VocalIntervalProgress(), history.state.progress.forLevel(0, 2))
        for (series in 0..1) for (i in 1..5) add(history, 2, 0, "given-$series", i)
        assertTrue(history.state.progress.forLevel(0, 2).mastered)
        assertEquals(imitation, history.state.progress[0])
        assertNotEquals(imitation.masteryEvidence, history.state.progress.forLevel(0, 2).masteryEvidence)
    }

    @Test fun `given access follows the matching signed imitation interval only`() = runTest {
        val history = VocalIntervalHistorySession(MemoryVocalIntervalResults())
        masterImitation(history, 12)
        assertEquals(setOf(12), history.state.progress.unlockedFor(2))
        add(history, 2, -12, "wrong-direction", 1)
        assertEquals(VocalDdaReason.LOCKED, history.state.records.last().dda!!.reason)
        add(history, 2, 12, "matching-direction", 1)
        assertTrue(history.state.records.last().dda!!.eligible)
        assertEquals(45, history.state.progress.forLevel(12, 2).toleranceCents)
        assertFalse(history.state.progress.forLevel(-12, 2).mastered)
    }

    @Test fun `series cannot mix a sung anchor and given anchor context`() = runTest {
        val history = VocalIntervalHistorySession(MemoryVocalIntervalResults())
        masterImitation(history, 0)
        add(history, 1, 0, "mixed", 1)
        add(history, 2, 0, "mixed", 2)
        assertEquals(VocalDdaReason.INVALID_SERIES, history.state.records.last().dda!!.reason)
        assertEquals(50, history.state.progress.forLevel(0, 2).toleranceCents)
    }

    @Test fun `wrong anchor provenance or reference cannot grant given progress`() = runTest {
        val history = VocalIntervalHistorySession(MemoryVocalIntervalResults())
        masterImitation(history, 0)
        for (sung in listOf(false, true)) {
            history.begin(if (sung) 57 else 60, 0, 1, "Synthetic" to "JVM", "test", "dda",
                VocalSeriesStep("provenance-$sung", 1), scaffoldingLevel = 2)
            val round = VocalIntervalSession(0, givenAnchorHz = if (sung) null else 220.0)
            if (sung) { round.beginAnchor(); for (t in 0L..500L step 50) round.accept(t, PitchEstimate(220.0)) }
            round.beginTarget()
            for (t in 600L..1100L step 50) round.accept(t, PitchEstimate(220.0))
            history.finish(round.state.result!!, 44100, null); history.savePending()
            assertEquals(VocalDdaReason.INSUFFICIENT, history.state.records.last().dda!!.reason)
        }
        assertFalse(history.state.progress.forLevel(0, 2).mastered)
    }

    @Test fun `given decisions survive uncertain commit reopen export and reset independently`() = runTest {
        val file = File(temporary.root, "vocal-interval-results.json")
        val disk = JsonVocalIntervalResults(file)
        val initial = VocalIntervalHistorySession(disk)
        masterImitation(initial, 0)
        var fail = true
        val uncertain = object : VocalIntervalResults {
            override suspend fun all() = disk.all()
            override suspend fun save(record: VocalIntervalRecord) { disk.save(record); if (fail) error("uncertain") }
        }
        val history = VocalIntervalHistorySession(uncertain)
        history.load()
        add(history, 2, 0, "uncertain-given", 1)
        assertEquals(50, history.state.progress.forLevel(0, 2).toleranceCents)
        assertEquals(1, history.state.pending.size)
        fail = false; history.savePending(); history.savePending()
        assertEquals(11, disk.all().size)
        val reopened = VocalIntervalHistorySession(JsonVocalIntervalResults(file))
        reopened.load()
        assertEquals(45, reopened.state.progress.forLevel(0, 2).toleranceCents)
        assertEquals(30, reopened.state.progress[0].toleranceCents)
        val export = Json.parseToJsonElement(Demonstration(temporary.root).export("test")).jsonObject
        assertTrue(export.getValue("vocalIntervalProgress").jsonObject.getValue("givenAnchorIntervals").jsonObject.containsKey("0"))
        val record = reopened.state.records.last()
        assertEquals(2, record.scaffoldingLevel)
        assertEquals(VocalAnchorSource.GIVEN, record.result.anchorSource)
        Demonstration(temporary.root).reset(); reopened.load()
        assertEquals(VocalProgress(), reopened.state.progress)
    }

    @Test fun `V4 snapshots load with given access but no inherited given performance`() = runTest {
        val original = VocalIntervalHistorySession(MemoryVocalIntervalResults())
        masterImitation(original, 0)
        val json = Json { encodeDefaults = true }
        fun legacy(element: JsonElement): JsonElement = when (element) {
            is JsonObject -> JsonObject(element.filterKeys { it !in setOf("anchorSource", "givenAnchorIntervals", "givenAnchorUnlocked") }.mapValues { legacy(it.value) })
            is JsonArray -> JsonArray(element.map { legacy(it) })
            else -> element
        }
        val records = legacy(json.encodeToJsonElement(ListSerializer(VocalIntervalRecord.serializer()), original.state.records))
        val file = File(temporary.root, "vocal-interval-results.json")
        file.writeText(buildJsonObject { put("schemaVersion", 1); put("results", records) }.toString())
        val reopened = VocalIntervalHistorySession(JsonVocalIntervalResults(file))
        reopened.load()
        assertNull(reopened.state.error)
        assertTrue(reopened.state.records.all { it.result.anchorSource == VocalAnchorSource.SUNG })
        assertEquals(setOf(0), reopened.state.progress.unlockedFor(2))
        assertTrue(reopened.state.progress.givenAnchorIntervals.isEmpty())
        add(reopened, 2, 0, "new-given", 1)
        assertEquals(45, reopened.state.progress.forLevel(0, 2).toleranceCents)
    }

    @Test fun `full given route uses five hundred isolated rounds for both scaffolding levels`() = runTest {
        val report = simulateVocalProgress(VocalDdaScenario.ROUTE, 2)
        assertEquals(500, report.records.size)
        assertEquals(250, report.records.count { it.scaffoldingLevel == 2 })
        assertEquals(25, report.progress.unlockedFor(2).size)
        assertTrue(vocalCurriculum.flatten().all { report.progress.forLevel(it, 2).mastered })
        assertTrue(report.records.filter { it.scaffoldingLevel == 2 }.all { it.result.anchorSource == VocalAnchorSource.GIVEN && it.result.measurements.size == 11 })
        File("build/verification/vocal-v5/given-route.json").apply {
            parentFile?.mkdirs(); writeText(Json { encodeDefaults = true }.encodeToString(VocalSimulationReport.serializer(), report))
        }
    }

    @Test fun `manual poor octave and interruption scenarios grant no given mastery`() = runTest {
        for (scenario in listOf(VocalDdaScenario.MANUAL, VocalDdaScenario.LOW, VocalDdaScenario.OCTAVE, VocalDdaScenario.INTERRUPTED)) {
            val report = simulateVocalProgress(scenario, 2)
            assertFalse(report.progress.forLevel(0, 2).mastered)
            if (scenario == VocalDdaScenario.MANUAL) assertTrue(report.progress.unlockedFor(2).isEmpty())
            else assertTrue(report.progress[0].mastered)
            if (scenario == VocalDdaScenario.OCTAVE)
                assertEquals(1200.0, report.records.first { it.scaffoldingLevel == 2 }.result.errorCents!!, .001)
        }
    }

    private suspend fun masterImitation(history: VocalIntervalHistorySession, key: Int) {
        for (series in 0..1) for (i in 1..5) add(history, 1, key, "imitation-$key-$series", i)
    }

    private suspend fun add(history: VocalIntervalHistorySession, level: Int, key: Int, id: String, ordinal: Int) {
        history.begin(57, key, 1, "Synthetic" to "JVM", "test", "dda", VocalSeriesStep(id, ordinal), level)
        val tolerance = history.state.progress.forLevel(key, level).toleranceCents.toDouble()
        val round = VocalIntervalSession(key, VocalIntervalConfiguration(toleranceCents = tolerance), givenAnchorHz = if (level == 2) 220.0 else null)
        if (level == 1) { round.beginAnchor(); for (t in 0L..500L step 50) round.accept(t, PitchEstimate(220.0)) }
        round.beginTarget()
        for (t in 600L..1100L step 50) round.accept(t, PitchEstimate(220 * 2.0.pow(key / 12.0)))
        history.finish(round.state.result!!, 44100, null); history.savePending()
    }
}
