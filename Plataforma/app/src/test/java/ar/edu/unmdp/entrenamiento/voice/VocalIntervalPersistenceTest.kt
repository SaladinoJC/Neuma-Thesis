package ar.edu.unmdp.entrenamiento.voice

import java.io.File
import java.io.ByteArrayOutputStream
import java.util.zip.ZipInputStream
import ar.edu.unmdp.entrenamiento.storage.Demonstration
import kotlinx.serialization.json.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class VocalIntervalPersistenceTest {
    @get:Rule val temporary = TemporaryFolder()

    @Test fun `repeated interruption saves a single frozen round and leaves the technical lab untouched`() = persistenceTest {
        val file = File(temporary.root, "vocal-interval-results.json")
        val lab = File(temporary.root, "voice-results.json").apply { writeText("legacy lab bytes") }
        val model = practice(JsonVocalIntervalResults(file))
        start(model)
        model.stop(); model.background(); model.stopAndJoin()
        val saved = JsonVocalIntervalResults(file).all().single()
        assertEquals("user", saved.result.interruption)
        assertNull(saved.result.inTune)
        assertEquals(1, saved.result.measurements.size)
        assertEquals("legacy lab bytes", lab.readText())
    }

    @Test fun `uncertain commit retains a pending snapshot blocks next round and retries without duplication`() = persistenceTest {
        val file = File(temporary.root, "vocal-interval-results.json")
        val disk = JsonVocalIntervalResults(file)
        var writes = 0
        val uncertain = object : VocalIntervalResults {
            override suspend fun all() = disk.all()
            override suspend fun save(record: VocalIntervalRecord) {
                disk.save(record)
                if (++writes == 1) error("fallo tras confirmar en disco")
            }
        }
        val model = practice(uncertain)
        start(model); model.stopAndJoin()
        val pending = model.pendingForExport().single()
        assertNotNull(model.ui.value.history.error)
        model.newRound(); model.startAnchor()
        assertEquals(pending.id, model.ui.value.currentRecordId)
        assertEquals(VocalIntervalPhase.INTERRUPTED, model.ui.value.round?.phase)
        val exported = Json.parseToJsonElement(Demonstration(temporary.root).export("test",
            pendingVocalIntervalSaves = model.pendingForExport())).jsonObject
        assertEquals(pending.id, exported.getValue("vocalIntervals").jsonArray.single().jsonObject.getValue("id").jsonPrimitive.content)
        assertEquals(pending.id, exported.getValue("pendingVocalIntervalSaves").jsonArray.single().jsonObject.getValue("id").jsonPrimitive.content)
        model.retryHistory(); model.retryHistory(); model.awaitStorage()
        assertTrue(model.pendingForExport().isEmpty())
        assertEquals(listOf(pending.id), disk.all().map { it.id })
        assertEquals(2, writes)
        model.newRound()
        assertNull(model.ui.value.round)
        assertFalse(model.ui.value.heardReference)
        model.stopAndJoin()
    }

    @Test fun `corrupt or unsupported originals survive failed saving and recovery includes the pending round`() = persistenceTest {
        for (original in listOf("{broken", "{\"schemaVersion\":99,\"results\":[]}")) {
            val directory = temporary.newFolder()
            val file = File(directory, "vocal-interval-results.json").apply { writeText(original) }
            val model = practice(JsonVocalIntervalResults(file))
            start(model); model.stopAndJoin()
            assertEquals(original, file.readText())
            val pending = model.pendingForExport().single()
            model.openHistory(); model.awaitStorage()
            assertNotNull(model.ui.value.history.error)
            assertEquals(pending.id, model.pendingForExport().single().id)
            val data = Demonstration(directory)
            assertTrue(runCatching { data.export("test", pendingVocalIntervalSaves = model.pendingForExport()) }.isFailure)
            val output = ByteArrayOutputStream()
            data.writeRecovery(output, pendingVocalIntervalSaves = model.pendingForExport())
            val recovered = mutableMapOf<String, String>()
            ZipInputStream(output.toByteArray().inputStream()).use { zip ->
                while (true) { val entry = zip.nextEntry ?: break; recovered[entry.name] = zip.readBytes().toString(Charsets.UTF_8) }
            }
            assertEquals(original, recovered["vocal-interval-results.json"])
            val recoveredPending = Json.parseToJsonElement(recovered.getValue("pending-vocal-interval-saves.json")).jsonArray.single().jsonObject
            assertEquals(pending.id, recoveredPending.getValue("id").jsonPrimitive.content)
            assertEquals(1, recoveredPending.getValue("result").jsonObject.getValue("measurements").jsonArray.size)
            data.reset(); model.resetDisplay()
            assertTrue(model.pendingForExport().isEmpty())
            assertFalse(file.exists())
            model.stopAndJoin()
        }
    }

    @Test fun `permission denial and cancelled reference do not create phantom rounds`() = persistenceTest {
        val file = File(temporary.root, "vocal-interval-results.json")
        val player = object : ReferencePlayer { override suspend fun play(midi: Int): Int { awaitCancellation() } }
        val model = VocalIntervalViewModel(singleHeldNote(), player, { testScheduler.currentTime }, resultsStore = JsonVocalIntervalResults(file))
        model.permissionDenied(); model.playReference(); runCurrent(); model.stopAndJoin()
        model.openHistory(); model.awaitStorage()
        assertTrue(model.ui.value.history.entries.isEmpty())
        assertFalse(file.exists())
    }

    @Test fun `draining waits for storage before reset and stale results do not return afterwards`() = persistenceTest {
        val file = File(temporary.root, "vocal-interval-results.json")
        val disk = JsonVocalIntervalResults(file)
        val release = CompletableDeferred<Unit>()
        val store = object : VocalIntervalResults {
            override suspend fun all() = disk.all()
            override suspend fun save(record: VocalIntervalRecord) { release.await(); disk.save(record) }
        }
        val model = practice(store)
        start(model); model.stop(); runCurrent()
        assertTrue(model.ui.value.historyBusy)
        var drained = false
        val cleanup = launch { model.stopAndJoin(); drained = true }
        runCurrent()
        assertFalse(drained)
        release.complete(Unit); cleanup.join()
        assertTrue(drained)
        Demonstration(temporary.root).reset(); model.resetDisplay()
        model.stopAndJoin(); model.openHistory(); model.awaitStorage()
        assertTrue(model.ui.value.history.entries.isEmpty())
        assertFalse(file.exists())
    }

    @Test fun `a history read completing after a round ends cannot erase its pending result`() = persistenceTest {
        val release = CompletableDeferred<Unit>()
        val store = object : VocalIntervalResults {
            override suspend fun all(): List<VocalIntervalRecord> { release.await(); return emptyList() }
            override suspend fun save(record: VocalIntervalRecord) {}
        }
        val history = VocalIntervalHistorySession(store, newId = { "preserved" })
        history.begin(57, 7, 1, "JVM" to "test", "test")
        val read = launch { history.load() }; runCurrent()
        val round = VocalIntervalSession(7)
        round.beginAnchor(); round.interrupt()
        history.finish(round.state.result!!, null, null)
        release.complete(Unit); read.join()
        assertEquals("preserved", history.state.pending.single().id)
    }

    @Test fun `non finite diagnostic input can be saved as a rejected frame without invalid JSON`() = persistenceTest {
        val file = File(temporary.root, "vocal-interval-results.json")
        val capture = object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                onMeasurement(PitchEstimate(Double.NaN, Double.POSITIVE_INFINITY, Double.NaN), -1, 44100)
                awaitCancellation()
            }
        }
        val model = practice(JsonVocalIntervalResults(file), capture)
        start(model); model.stopAndJoin()
        val first = JsonVocalIntervalResults(file).all().single().result.measurements.single()
        assertNull(first.frequency); assertNull(first.periodicity); assertNull(first.rms); assertNull(first.processingMicros)
        assertNotNull(first.rejection)
    }

    private fun persistenceTest(body: suspend TestScope.() -> Unit) = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try { body() } finally { Dispatchers.resetMain() }
    }

    private fun TestScope.practice(store: VocalIntervalResults, capture: VoiceCapture = singleHeldNote()) =
        VocalIntervalViewModel(capture, object : ReferencePlayer { override suspend fun play(midi: Int) = 26460 },
            { testScheduler.currentTime }, resultsStore = store, deviceInfo = { "JVM" to "synthetic" })

    private suspend fun TestScope.start(model: VocalIntervalViewModel) {
        model.playReference(); advanceTimeBy(200); runCurrent()
        model.startAnchor(); runCurrent()
    }

    private fun singleHeldNote() = object : VoiceCapture {
        override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
            onMeasurement(PitchEstimate(220.0, .98, .2), 37, 44100)
            awaitCancellation()
        }
    }

    @Test fun `completed practice survives reopening with its reference and full derived measurements`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val file = File(temporary.root, "vocal-interval-results.json")
            var captures = 0
            val capture = object : VoiceCapture {
                override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                    val hz = if (++captures == 1) 220.0 else 329.6275569128699
                    while (true) { onMeasurement(PitchEstimate(hz, .98, .2), 37, 44100); delay(50) }
                }
            }
            val player = object : ReferencePlayer { override suspend fun play(midi: Int) = 26460 }
            val model = VocalIntervalViewModel(capture, player, { testScheduler.currentTime },
                resultsStore = JsonVocalIntervalResults(file), wallClock = { 1_000_000 + testScheduler.currentTime },
                deviceInfo = { "Synthetic JVM" to "test" })
            repeat(2) { model.playReference(); advanceTimeBy(200); runCurrent() }
            model.startAnchor(); advanceTimeBy(600); runCurrent()
            model.startTarget(); advanceTimeBy(600); runCurrent()
            model.stopAndJoin()
            val reopened = VocalIntervalViewModel(resultsStore = JsonVocalIntervalResults(file))
            reopened.openHistory(); reopened.awaitStorage()
            val record = reopened.ui.value.history.records.single()
            assertEquals("manual", record.origin)
            assertEquals(1, record.scaffoldingLevel)
            assertEquals(57, record.referenceAnchorMidi)
            assertEquals(64, record.referenceTargetMidi)
            assertEquals(2, record.referenceListens)
            assertEquals(44100, record.actualSampleRate)
            assertEquals("Synthetic JVM", record.device)
            assertEquals(22, record.result.measurements.size)
            assertTrue(record.result.measurements.all { it.processingMicros == 37L })
            assertEquals(700.0, record.result.intervalCents!!, .001)
            assertTrue(reopened.pendingForExport().isEmpty())
            reopened.stopAndJoin()
        } finally { Dispatchers.resetMain() }
    }
}
