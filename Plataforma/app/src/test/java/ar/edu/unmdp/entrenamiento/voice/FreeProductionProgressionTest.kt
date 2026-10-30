package ar.edu.unmdp.entrenamiento.voice

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import kotlinx.serialization.json.*
import kotlinx.serialization.builtins.ListSerializer
import ar.edu.unmdp.entrenamiento.storage.Demonstration
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayOutputStream
import java.util.zip.ZipInputStream
import java.io.File
import kotlin.math.pow

class FreeProductionProgressionTest {
    @get:Rule val temporary = TemporaryFolder()

    @Test fun `V5 snapshots grant only free access then uncertain free save reopens and exports exactly once`() = runTest {
        val json = Json { encodeDefaults = true }
        val old = simulateVocalProgress(VocalDdaScenario.GOOD, 2)
        fun legacy(element: JsonElement): JsonElement = when (element) {
            is JsonObject -> JsonObject(element.filterKeys { it !in setOf("freeProductionIntervals", "freeProductionUnlocked") }.mapValues { legacy(it.value) })
            is JsonArray -> JsonArray(element.map { legacy(it) })
            else -> element
        }
        val records = legacy(json.encodeToJsonElement(ListSerializer(VocalIntervalRecord.serializer()), old.records))
        val file = File(temporary.root, "vocal-interval-results.json")
        file.writeText(buildJsonObject { put("schemaVersion", 1); put("results", records) }.toString())
        val disk = JsonVocalIntervalResults(file)
        var uncertain = true
        val history = VocalIntervalHistorySession(object : VocalIntervalResults {
            override suspend fun all() = disk.all()
            override suspend fun save(record: VocalIntervalRecord) { disk.save(record); if (uncertain) error("uncertain commit") }
        })
        history.load()
        assertNull(history.state.error)
        assertEquals(setOf(0), history.state.progress.unlockedFor(3))
        assertEquals(VocalIntervalProgress(), history.state.progress.forLevel(0, 3))
        add(history, 3, 0, "uncertain-free", 1)
        val id = history.state.pending.single().id
        assertEquals(50, history.state.progress.forLevel(0, 3).toleranceCents)
        uncertain = false; history.savePending(); history.savePending()
        val reopened = VocalIntervalHistorySession(JsonVocalIntervalResults(file)); reopened.load()
        assertEquals(1, reopened.state.records.count { it.id == id })
        assertEquals(45, reopened.state.progress.forLevel(0, 3).toleranceCents)
        assertEquals(30, reopened.state.progress.forLevel(0, 2).toleranceCents)
        assertNull(reopened.state.records.last().referenceAnchorMidi)
        assertEquals("vocal-free-production-dda-v1", reopened.state.records.last().configurationVersion)
        val data = Demonstration(temporary.root)
        val exported = Json.parseToJsonElement(data.export("test")).jsonObject
        assertTrue(exported.getValue("vocalIntervalProgress").jsonObject.getValue("freeProductionIntervals").jsonObject.containsKey("0"))
        val free = exported.getValue("vocalIntervals").jsonArray.last().jsonObject
        assertEquals(JsonNull, free["referenceAnchorMidi"])
        assertEquals(0, free.getValue("referenceListens").jsonPrimitive.int)
        val bytes = ByteArrayOutputStream(); data.writeRecovery(bytes)
        val entries = mutableMapOf<String, String>()
        ZipInputStream(bytes.toByteArray().inputStream()).use { zip ->
            while (true) { val entry = zip.nextEntry ?: break; entries[entry.name] = zip.readBytes().toString(Charsets.UTF_8) }
        }
        assertTrue(entries.getValue("vocal-interval-results.json").contains(id))
        assertFalse(entries.keys.any { "free-production" in it })
        data.reset(); reopened.load()
        assertEquals(VocalProgress(), reopened.state.progress)
        assertTrue(reopened.state.records.isEmpty())
    }

    @Test fun `free series rejects mixed help and an anchor that was provided instead of sung`() = runTest {
        val memory = MemoryVocalIntervalResults()
        simulateVocalProgress(VocalDdaScenario.GOOD, 2).records.forEach { memory.save(it) }
        val history = VocalIntervalHistorySession(memory); history.load()
        add(history, 2, 0, "mixed", 1)
        add(history, 3, 0, "mixed", 2)
        assertEquals(VocalDdaReason.INVALID_SERIES, history.state.records.last().dda!!.reason)
        history.begin(null, 0, 0, "Synthetic" to "JVM", "test", "dda", VocalSeriesStep("wrong-source", 1), 3)
        val given = VocalIntervalSession(0, givenAnchorHz = 220.0)
        given.beginTarget(); for (t in 0L..500L step 50) given.accept(t, PitchEstimate(220.0))
        history.finish(given.state.result!!, 44100, null); history.savePending()
        assertEquals(VocalDdaReason.INSUFFICIENT, history.state.records.last().dda!!.reason)
        assertEquals(VocalIntervalProgress(), history.state.progress.forLevel(0, 3))
    }

    @Test fun `free poor octave interrupted and manual signals grant no free mastery`() = runTest {
        for (scenario in listOf(VocalDdaScenario.MANUAL, VocalDdaScenario.LOW, VocalDdaScenario.OCTAVE, VocalDdaScenario.INTERRUPTED)) {
            val report = simulateVocalProgress(scenario, 3)
            assertFalse(report.progress.forLevel(0, 3).mastered)
            if (scenario == VocalDdaScenario.MANUAL) assertTrue(report.progress.unlockedFor(3).isEmpty())
            else assertTrue(report.progress.forLevel(0, 2).mastered)
            val free = report.records.first { it.scaffoldingLevel == 3 }
            assertEquals(0, free.referenceListens)
            if (scenario == VocalDdaScenario.OCTAVE) assertEquals(1200.0, free.result.errorCents!!, .001)
        }
    }

    @Test fun `abandoning a partial free series resets only its streak and preserves previous mastery`() = runTest {
        val memory = MemoryVocalIntervalResults()
        simulateVocalProgress(VocalDdaScenario.GOOD, 2).records.forEach { memory.save(it) }
        val history = VocalIntervalHistorySession(memory); history.load()
        for (i in 1..5) add(history, 3, 0, "free-first", i)
        assertEquals(1, history.state.progress.forLevel(0, 3).highSeries)
        add(history, 3, 0, "free-abandoned", 1)
        for (i in 1..5) add(history, 3, 0, "free-restarted", i)
        assertTrue(history.state.records.takeLast(5).first().dda!!.abandonedPreviousSeries)
        assertEquals(1, history.state.progress.forLevel(0, 3).highSeries)
        assertFalse(history.state.progress.forLevel(0, 3).mastered)
        assertTrue(history.state.progress.forLevel(0, 2).mastered)
    }

    @Test fun `full free route masters twenty five signed objectives separately in seven hundred fifty rounds`() = runTest {
        val report = simulateVocalProgress(VocalDdaScenario.ROUTE, 3)
        assertEquals(750, report.records.size)
        for (level in 1..3) {
            assertEquals(250, report.records.count { it.scaffoldingLevel == level })
            assertEquals(25, report.progress.unlockedFor(level).size)
            assertTrue(vocalCurriculum.flatten().all { report.progress.forLevel(it, level).mastered })
        }
        val free = report.records.filter { it.scaffoldingLevel == 3 }
        assertTrue(free.all { it.referenceListens == 0 && it.referenceAnchorMidi == null && it.referenceTargetMidi == null &&
            it.result.anchorSource == VocalAnchorSource.SUNG && it.result.measurements.size == 22 && it.dda!!.eligible })
        assertTrue(free.all { kotlin.math.abs(it.result.anchorHz!! - 196.0) < .001 })
        File("build/verification/vocal-v6/free-route.json").apply {
            parentFile?.mkdirs(); writeText(Json { encodeDefaults = true }.encodeToString(VocalSimulationReport.serializer(), report))
        }
    }

    @Test fun `free production requires matching given mastery and keeps all three performances independent`() = runTest {
        val history = VocalIntervalHistorySession(MemoryVocalIntervalResults())
        add(history, 3, 12, "locked", 1)
        assertEquals(VocalDdaReason.LOCKED, history.state.records.last().dda!!.reason)
        for (level in 1..2) {
            for (series in 0..1) for (i in 1..5) add(history, level, 12, "level-$level-$series", i)
            if (level == 1) assertTrue(history.state.progress.unlockedFor(3).isEmpty())
        }
        assertEquals(setOf(12), history.state.progress.unlockedFor(3))
        val imitation = history.state.progress.forLevel(12, 1)
        val given = history.state.progress.forLevel(12, 2)
        assertEquals(VocalIntervalProgress(), history.state.progress.forLevel(12, 3))
        add(history, 3, -12, "wrong-direction", 1)
        assertEquals(VocalDdaReason.LOCKED, history.state.records.last().dda!!.reason)
        for (series in 0..1) for (i in 1..5) add(history, 3, 12, "free-$series", i)
        assertTrue(history.state.progress.forLevel(12, 3).mastered)
        assertEquals(imitation, history.state.progress.forLevel(12, 1))
        assertEquals(given, history.state.progress.forLevel(12, 2))
        assertFalse(history.state.progress.forLevel(-12, 3).mastered)
    }

    private suspend fun add(history: VocalIntervalHistorySession, level: Int, key: Int, id: String, ordinal: Int) {
        history.begin(if (level == 3) null else 57, key, if (level == 3) 0 else 1,
            "Synthetic" to "JVM", "test", "dda", VocalSeriesStep(id, ordinal), level)
        val round = VocalIntervalSession(key,
            VocalIntervalConfiguration(toleranceCents = history.state.progress.forLevel(key, level).toleranceCents.toDouble()),
            givenAnchorHz = if (level == 2) 220.0 else null)
        if (level != 2) { round.beginAnchor(); for (t in 0L..500L step 50) round.accept(t, PitchEstimate(196.0)) }
        round.beginTarget()
        val anchor = if (level == 2) 220.0 else 196.0
        for (t in 600L..1100L step 50) round.accept(t, PitchEstimate(anchor * 2.0.pow(key / 12.0)))
        history.finish(round.state.result!!, 44100, null); history.savePending()
    }
}
