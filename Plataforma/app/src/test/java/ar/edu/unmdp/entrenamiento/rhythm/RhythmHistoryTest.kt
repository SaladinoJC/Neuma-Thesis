package ar.edu.unmdp.entrenamiento.rhythm

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

internal fun rhythmResult(category: RhythmCategory = RhythmCategory.EIGHTH_TRIPLET, meter: Int = 4,
    bpm: Int = 60, complete: Boolean = true, metronome: Boolean = false): RhythmResult {
    val session = RhythmSession().apply { generate(setOf(category), 12L, meter) }
    session.preview(session.state.score!!.copy(bpm = bpm)); session.play(0, metronome, tapSound = false)
    val attack = session.state.attacks.first()
    session.tap(session.executionStartNanos!! + attack.offsetNanos, 123)
    if (complete) session.advance(session.executionStartNanos!! + Beat(meter * 4).nanos(bpm) + 1)
    else session.interrupt()
    return session.state.result!!
}

class RhythmHistoryTest {
    @get:Rule val temporary = TemporaryFolder()

    @Test fun archivoReabiertoConservaPartituraEvaluacionHuellaYVersionesSinRegenerar() = runTest {
        val file = File(temporary.root, "rhythm-results.json")
        val history = RhythmHistorySession(JsonRhythmResults(file), now = { 700L }, newId = { "run-1" })
        val result = rhythmResult()
        history.begin(); history.capture(result); history.savePending()
        val reopened = RhythmHistorySession(JsonRhythmResults(file))
        reopened.load()
        val record = reopened.state.results.single()
        assertEquals("run-1", record.id)
        assertEquals(result, record.result)
        assertEquals(result.score.fingerprint, record.fingerprint)
        assertEquals("rhythm-simple-v4", record.result.score.version)
        assertEquals("rhythm-timing-v1", record.result.timingVersion)
        assertEquals(123L, record.result.taps.single().inputUptimeMillis)
        assertEquals(700L, record.startedAtMillis)
        assertEquals(700L, record.finishedAtMillis)
        assertTrue(reopened.state.pending.isEmpty())
    }

    @Test fun revisionesDelMismoIdReemplazanSinDuplicarNiRebajarVersion() = runTest {
        val file = File(temporary.root, "rhythm-results.json")
        val store = JsonRhythmResults(file)
        val history = RhythmHistorySession(store, newId = { "same-id" })
        val result = rhythmResult()
        history.begin(); history.capture(result); history.savePending()
        val first = store.all().single()
        val changed = result.copy(taps = result.taps + RhythmTap(999L, 1L, TapKind.EXTRA))
        history.capture(changed); history.savePending()
        val latest = store.all().single()
        assertEquals(first.revision + 1, latest.revision)
        assertEquals(changed, latest.result)
        store.save(first); store.save(latest)
        assertEquals(listOf(latest), store.all())
        assertFalse(history.capture(changed))
    }

    @Test fun falloDespuesDeEscribirConservaPendienteYReintentoEsIdempotente() = runTest {
        val disk = JsonRhythmResults(File(temporary.root, "rhythm-results.json"))
        var fail = true
        val store = object : RhythmResults {
            override suspend fun all() = disk.all()
            override suspend fun save(record: RhythmRecord) {
                disk.save(record)
                if (fail) { fail = false; error("Confirmación perdida") }
            }
        }
        val history = RhythmHistorySession(store)
        history.begin(); history.capture(rhythmResult(complete = false)); history.savePending()
        assertNotNull(history.state.error)
        val id = history.state.pending.single().id
        assertFalse(history.state.pending.single().result.completed)
        history.savePending(); history.savePending()
        assertNull(history.state.error)
        assertTrue(history.state.pending.isEmpty())
        assertEquals(id, disk.all().single().id)
    }

    @Test fun guardadoSuspendidoNoDescartaUnaRevisionNuevaDelMismoEjercicio() = runTest {
        val saved = MemoryRhythmResults()
        val entered = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        var first = true
        val store = object : RhythmResults {
            override suspend fun all() = saved.all()
            override suspend fun save(record: RhythmRecord) {
                if (first) { first = false; entered.complete(Unit); release.await() }
                saved.save(record)
            }
        }
        val history = RhythmHistorySession(store)
        val result = rhythmResult()
        history.begin(); history.capture(result)
        val writing = launch { history.savePending() }; entered.await()
        val changed = result.copy(taps = result.taps + RhythmTap(999L, 1L, TapKind.EXTRA))
        history.capture(changed)
        release.complete(Unit); writing.join()
        assertTrue(history.state.pending.isEmpty())
        assertEquals(changed, saved.all().single().result)
        assertEquals(2, saved.all().single().revision)
    }

    @Test fun lecturaSuspendidaConservaNuevosGuardadosPendientesYFiltroActual() = runTest {
        val saved = MemoryRhythmResults()
        val reading = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        val store = object : RhythmResults {
            override suspend fun all(): List<RhythmRecord> {
                val snapshot = saved.all(); reading.complete(Unit); release.await(); return snapshot
            }
            override suspend fun save(record: RhythmRecord) = saved.save(record)
        }
        val history = RhythmHistorySession(store)
        val load = launch { history.load() }; reading.await()
        history.begin(); history.capture(rhythmResult()); history.savePending()
        history.begin(); history.capture(rhythmResult(complete = false))
        val filter = RhythmHistoryFilter(category = RhythmCategory.EIGHTH_TRIPLET, metronome = false)
        history.filter(filter)
        release.complete(Unit); load.join()
        assertEquals(1, history.state.results.size)
        assertEquals(1, history.state.pending.size)
        assertEquals(filter, history.state.filter)
    }

    @Test fun filtrosDeCategoriaCompasTempoOrigenYMetronomoSonIndependientes() = runTest {
        val history = RhythmHistorySession(MemoryRhythmResults())
        for (result in listOf(rhythmResult(), rhythmResult(RhythmCategory.TIED_QUARTERS, 3, 120, metronome = true),
            rhythmResult(RhythmCategory.REST_EIGHTH, 2, 60, complete = false))) {
            history.begin(); history.capture(result); history.savePending()
        }
        for (filter in listOf(RhythmHistoryFilter(category = RhythmCategory.EIGHTH_TRIPLET),
            RhythmHistoryFilter(beatsPerBar = 3), RhythmHistoryFilter(bpm = 120),
            RhythmHistoryFilter(metronome = true))) {
            history.filter(filter); assertEquals(1, history.state.entries.size)
        }
        history.filter(RhythmHistoryFilter(origin = "practice")); assertEquals(3, history.state.entries.size)
        history.filter(RhythmHistoryFilter(origin = "progression")); assertTrue(history.state.entries.isEmpty())
        history.clear(); assertTrue(history.state.results.isEmpty()); assertTrue(history.state.pending.isEmpty())
    }
}
