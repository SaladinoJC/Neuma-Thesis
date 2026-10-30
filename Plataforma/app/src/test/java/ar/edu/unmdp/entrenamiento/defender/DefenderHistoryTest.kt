package ar.edu.unmdp.entrenamiento.defender

import ar.edu.unmdp.entrenamiento.storage.Demonstration
import ar.edu.unmdp.entrenamiento.storage.DemonstrationExport
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test
import java.nio.file.Files

class DefenderHistoryTest {
    private fun record(id: String, stage: DefenderStage = DefenderStage.N1, hits: Int = 24, origin: String = "progression") =
        DefenderRecord(id, 1, 2, DefenderHarness(stage, origin).finish(hits))

    @Test fun dosAltasDelMismoObjetivoHabilitanSoloLaSiguienteEtapaYSeConservanTrasErrores() = runBlocking {
        val store = MemoryDefenderResults()
        val first = store.commit(record("a"))
        assertEquals(setOf(DefenderStage.N1), first.progress!!.after.unlocked)
        assertEquals(1, first.progress.after[DefenderStage.N1].highStreak)
        val second = store.commit(record("b"))
        assertEquals(setOf(DefenderStage.N1, DefenderStage.N2), second.progress!!.after.unlocked)
        assertEquals(listOf("a", "b"), second.progress.after[DefenderStage.N1].masteryEvidence)
        store.commit(record("easy", origin = "practice"))
        assertEquals(second.progress.after, defenderProgress(store.all()))
        val error = store.commit(record("error", hits = 7))
        assertEquals(DefenderProgressReason.NOT_HIGH, error.progress!!.reason)
        assertTrue(DefenderStage.N2 in error.progress.after.unlocked)
        val locked = store.commit(record("locked", stage = DefenderStage.N4))
        assertEquals(DefenderProgressReason.LOCKED, locked.progress!!.reason)
        assertFalse(locked.progress.after[DefenderStage.N4].mastered)
    }

    @Test fun mezclaNoAcreditaClaveAusenteYNuncaComparteDominioConOtraVariante() = runBlocking {
        val store = MemoryDefenderResults()
        for (stage in listOf(DefenderStage.N1, DefenderStage.N2, DefenderStage.N3)) {
            store.commit(record("${stage}a", stage)); store.commit(record("${stage}b", stage))
        }
        assertTrue(DefenderStage.N4 in defenderProgress(store.all()).unlocked)
        // Synthetic repository input: valid N4 treble content, without bass exposure.
        val trebleOnly = DefenderHarness().finish().let { it.copy(configuration = it.configuration.copy(stage = DefenderStage.N4)) }
        val absent = store.commit(DefenderRecord("absent", 1, 2, trebleOnly))
        assertEquals(DefenderProgressReason.INSUFFICIENT, absent.progress!!.reason)
        assertEquals(0, absent.progress.after[DefenderStage.N4].highStreak)
        val both = store.commit(record("mix", DefenderStage.N4, hits = 60))
        assertTrue(both.progress!!.evidence.drop(1).all { it.resolved >= 3 && it.mastery == "alto" })
        assertEquals(1, both.progress.after[DefenderStage.N4].highStreak)
        assertEquals(setOf(DefenderChordQuality.MAJOR), defenderProgress(store.all(), "chords").unlockedQualities)
    }

    @Test fun guardadoFallidoMantienePendienteYReintentoNoDuplicaRacha() = runBlocking {
        val saved = MemoryDefenderResults(); var fail = true
        val store = object : DefenderResults {
            override suspend fun all() = saved.all()
            override suspend fun commit(record: DefenderRecord): DefenderRecord {
                if (fail) error("Disco no disponible")
                return saved.commit(record)
            }
        }
        val history = DefenderHistorySession(store, newId = { "retry" })
        history.begin(); history.capture(record("unused").game); history.savePending()
        assertEquals(1, history.state.pending.size)
        assertEquals(0, history.state.progress[DefenderStage.N1].highStreak)
        fail = false; history.savePending(); history.savePending()
        assertEquals(1, saved.all().size)
        assertEquals(1, history.state.progress[DefenderStage.N1].highStreak)
    }

    @Test fun jsonIdempotenteReabreExportaRecuperaYReiniciaSinPerderRitmo() = runBlocking {
        val dir = Files.createTempDirectory("defender-d3").toFile()
        val store = JsonDefenderResults(dir.resolve("defender-results.json"))
        val a = record("a"); val b = record("b")
        store.commit(a); val second = store.commit(b)
        assertEquals(second, store.commit(b))
        assertEquals(2, store.all().size)
        val reopened = JsonDefenderResults(dir.resolve("defender-results.json"))
        assertTrue(DefenderStage.N2 in defenderProgress(reopened.all()).unlocked)
        val rhythm = ar.edu.unmdp.entrenamiento.rhythm.RhythmSession().apply {
            generate(setOf(ar.edu.unmdp.entrenamiento.rhythm.RhythmCategory.QUARTER), 1); play(0); interrupt()
        }
        val rhythmRecord = ar.edu.unmdp.entrenamiento.rhythm.RhythmRecord("rhythm", 1, 2, rhythm.state.result!!)
        val rhythmStore = ar.edu.unmdp.entrenamiento.rhythm.JsonRhythmResults(dir.resolve("rhythm-results.json"))
        rhythmStore.save(rhythmRecord)
        val data = Demonstration(dir)
        val export = Json.decodeFromString(DemonstrationExport.serializer(), data.export("test"))
        assertEquals(reopened.all(), export.defender)
        assertEquals(defenderProgress(reopened.all()), export.defenderProgress)
        assertEquals(listOf(rhythmRecord), export.rhythm)
        val output = java.io.ByteArrayOutputStream(); data.writeRecovery(output)
        java.util.zip.ZipInputStream(output.toByteArray().inputStream()).use {
            val entries = mutableMapOf<String, ByteArray>()
            var entry = it.nextEntry
            while (entry != null) { entries[entry.name] = it.readBytes(); entry = it.nextEntry }
            assertArrayEquals(dir.resolve("defender-results.json").readBytes(), entries["defender-results.json"])
            assertArrayEquals(dir.resolve("rhythm-results.json").readBytes(), entries["rhythm-results.json"])
        }
        data.reset(); assertTrue(reopened.all().isEmpty())
        assertTrue(rhythmStore.all().isEmpty())
    }

    @Test fun lecturaSuspendidaConservaGuardadosPendientesYFiltros() = runTest {
        val saved = MemoryDefenderResults(); val entered = CompletableDeferred<Unit>(); val release = CompletableDeferred<Unit>()
        val store = object : DefenderResults {
            override suspend fun all(): List<DefenderRecord> { val snapshot = saved.all(); entered.complete(Unit); release.await(); return snapshot }
            override suspend fun commit(record: DefenderRecord) = saved.commit(record)
        }
        val history = DefenderHistorySession(store)
        val load = launch { history.load() }; entered.await()
        history.begin(); history.capture(record("a").game); history.savePending()
        val filter = DefenderHistoryFilter(stage = DefenderStage.N1, origin = "progression", completed = true)
        history.filter(filter); release.complete(Unit); load.join()
        assertEquals(1, history.state.entries.size)
        assertEquals(filter, history.state.filter)
    }

    @Test fun commitsConcurrentesCoordinanRachaYNoDuplicanIdentidades() = runBlocking {
        val file = Files.createTempDirectory("defender-concurrent").resolve("defender-results.json").toFile()
        val a = record("a"); val b = record("b")
        val jobs = listOf(a, b, a).map { r -> async { JsonDefenderResults(file).commit(r) } }
        jobs.forEach { it.await() }
        val records = JsonDefenderResults(file).all()
        assertEquals(2, records.size)
        assertEquals(setOf(1L, 2L), records.map { it.progress!!.sequence }.toSet())
        assertTrue(DefenderStage.N2 in defenderProgress(records).unlocked)
    }

    @Test fun archivoIncompatibleNoSePisaYRecuperacionIncluyeOriginalYPendiente() = runBlocking {
        val dir = Files.createTempDirectory("defender-corrupt").toFile(); val file = dir.resolve("defender-results.json")
        val bytes = "{\"schemaVersion\":99,\"results\":[]}".toByteArray(); file.writeBytes(bytes)
        val history = DefenderHistorySession(JsonDefenderResults(file), newId = { "pending" })
        history.load(); assertNotNull(history.state.error)
        history.begin(); history.capture(record("a").game); history.savePending()
        assertEquals(1, history.state.pending.size)
        assertArrayEquals(bytes, file.readBytes())
        val out = java.io.ByteArrayOutputStream(); Demonstration(dir).writeRecovery(out, pendingDefenderSaves = history.state.pending)
        java.util.zip.ZipInputStream(out.toByteArray().inputStream()).use { zip ->
            val entries = mutableMapOf<String, ByteArray>(); var entry = zip.nextEntry
            while (entry != null) { entries[entry.name] = zip.readBytes(); entry = zip.nextEntry }
            assertArrayEquals(bytes, entries["defender-results.json"])
            assertTrue("pending-defender-saves.json" in entries)
            assertTrue("LEEME.txt" in entries)
        }
    }
}
