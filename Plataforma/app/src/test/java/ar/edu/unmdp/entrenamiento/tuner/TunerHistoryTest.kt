package ar.edu.unmdp.entrenamiento.tuner

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import ar.edu.unmdp.entrenamiento.voice.PitchEstimate
import kotlin.math.pow
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.ExperimentalCoroutinesApi

class TunerHistoryTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test fun consultarMientrasTerminaOtroRecorridoNoPierdeElPendienteNiElFiltroNuevo() = runTest {
        val ready = CompletableDeferred<Unit>()
        val disk = MemoryTunerResults()
        val history = TunerHistorySession(object : TunerResults {
            override suspend fun all(): List<TunerResult> { ready.await(); return disk.all() }
            override suspend fun save(result: TunerResult) = disk.save(result)
        })
        val loading = launch { history.load() }; runCurrent()
        val config = TunerConfiguration(mode = TunerMode.GUIDED)
        val guided = GuidedTunerSession()
        history.begin(config); guided.begin(config.snapshot(), true); guided.interrupt()
        history.finish(guided.state, EarTunerState())
        history.filter(TunerHistoryFilter(stringNumber = 6))
        ready.complete(Unit); loading.join()
        assertEquals(1, history.state.pending.size)
        assertEquals(6, history.state.filter.stringNumber)
        history.savePending()
        assertEquals(1, disk.all().size)
    }
    private fun feed(ear: EarTunerSession, midi: Int, cents: Double, start: Long, duration: Long) {
        for (t in start..start + duration step 100) ear.measure(PitchEstimate(440 * 2.0.pow((midi - 69 + cents / 100) / 12)), t)
    }
    @Test fun seguimientoApagadoNoGuardaYUnaInterrupcionConSeguimientoSeConservaUnaSolaVez() = runTest {
        val store = MemoryTunerResults()
        val history = TunerHistorySession(store, now = { 1000L }, newId = { "session-1" })
        val guided = GuidedTunerSession()
        val config = TunerConfiguration(mode = TunerMode.GUIDED)
        guided.begin(config.snapshot(), false)
        history.begin(config.copy(saveHistory = false))
        guided.interrupt()
        assertFalse(history.finish(guided.state, EarTunerState()))
        history.savePending()
        assertTrue(store.all().isEmpty())
        guided.begin(config.snapshot(), false)
        history.begin(config)
        guided.interrupt()
        assertTrue(history.finish(guided.state, EarTunerState()))
        assertFalse(history.finish(guided.state, EarTunerState()))
        history.savePending(); history.savePending(); history.load()
        val result = history.state.results.single()
        assertEquals("session-1", result.id)
        assertFalse(result.completed)
        assertEquals(TunerMode.GUIDED, result.mode)
        assertTrue(result.observations.isEmpty())
        assertTrue(history.state.pending.isEmpty())
    }
    @Test fun elErrorInicialNoSeMezclaConCorreccionYLaAfinacionGuardadaEsUnaCopia() = runTest {
        val history = TunerHistorySession(MemoryTunerResults())
        val notes = mutableListOf(40, 45, 50, 55, 59, 64)
        val config = TunerConfiguration(mode = TunerMode.TRAINING,
            guitars = listOf(GuitarProfile("one", "Criolla", "custom")), currentGuitarId = "one",
            customTunings = listOf(GuitarTuning("custom", "Ensayo", notes)))
        val ear = EarTunerSession()
        history.begin(config); ear.begin(config.snapshot(), false, true)
        feed(ear, 40, -30.0, 0, 400); ear.classify(EarDirection.LOW, 400)
        ear.check(); feed(ear, 40, 30.0, 500, 400)
        feed(ear, 40, 1.0, 1000, 1000)
        ear.interrupt(); history.finish(GuidedTunerState(), ear.state); history.savePending()
        notes[0] = 38
        val result = history.state.results.single()
        assertEquals(40, result.setup.tuning.notesSixToFirst.first())
        assertEquals("Criolla", result.setup.guitar.name)
        val attempt = result.observations.first { it.stage == TunerStage.EAR_ATTEMPT }
        val correction = result.observations.first { it.stage == TunerStage.EAR_CORRECTION }
        assertEquals(30.0, attempt.cents!!, 0.001)
        assertFalse(attempt.passed)
        assertTrue(attempt.classification!!.correct)
        assertEquals(1.0, correction.cents!!, 0.001)
        assertTrue(correction.passed)
        assertEquals(2, result.observations.size)
    }
    @Test fun unFalloDespuesDeEscribirPermiteReintentarSinDuplicarNiPerderLaSiguienteGuitarra() = runTest {
        val directory = kotlin.io.path.createTempDirectory("tuner-history").toFile()
        try {
            val file = java.io.File(directory, "tuner-results.json")
            val disk = JsonTunerResults(file)
            var fail = true
            val store = object : TunerResults {
                override suspend fun all() = disk.all()
                override suspend fun save(result: TunerResult) {
                    disk.save(result)
                    if (fail) { fail = false; error("Sin confirmación de escritura") }
                }
            }
            var next = 0
            val history = TunerHistorySession(store, newId = { "session-${++next}" })
            val guided = GuidedTunerSession()
            val config = TunerConfiguration(mode = TunerMode.GUIDED)
            history.begin(config); guided.begin(config.snapshot(), false); guided.interrupt()
            history.finish(guided.state, EarTunerState()); history.savePending()
            assertNotNull(history.state.error)
            assertEquals(1, history.state.pending.size)
            val other = config.copy(guitars = listOf(GuitarProfile("electric", "Eléctrica", "drop-d")), currentGuitarId = "electric")
            history.begin(other); guided.begin(other.snapshot(), false); guided.interrupt()
            history.finish(guided.state, EarTunerState()); history.savePending()
            assertNull(history.state.error)
            assertTrue(history.state.pending.isEmpty())
            val reopened = TunerHistorySession(JsonTunerResults(file)); reopened.load()
            assertEquals(listOf("Guitarra", "Eléctrica"), reopened.state.results.map { it.setup.guitar.name })
            assertEquals(38, reopened.state.results.last().setup.tuning.notesSixToFirst.first())
        } finally { directory.deleteRecursively() }
    }
    @Test fun progresoSoloCuentaIntentosValidosYFiltrosNoMezclanGuitarrasNiEtapas() = runTest {
        val history = TunerHistorySession(MemoryTunerResults())
        val config = TunerConfiguration(mode = TunerMode.TRAINING)
        val ear = EarTunerSession()
        history.begin(config); ear.begin(config.snapshot(), false, false)
        ear.check(); feed(ear, 40, 30.0, 0, 400); feed(ear, 40, 0.0, 500, 1000)
        ear.check(); feed(ear, 45, -1.0, 1600, 400); feed(ear, 45, 0.0, 2100, 1000)
        ear.check(); ear.measure(PitchEstimate(), 3200)
        ear.interrupt(); history.finish(GuidedTunerState(), ear.state); history.savePending()
        assertEquals(2, history.state.earProgress.validAttempts)
        assertEquals(1, history.state.earProgress.correctAttempts)
        assertEquals(15.5, history.state.earProgress.meanInitialAbsoluteCents!!, 0.001)
        assertEquals(0, history.state.earProgress.classified)
        history.filter(TunerHistoryFilter(stringNumber = 6))
        assertEquals(1, history.state.earProgress.validAttempts)
        assertEquals(30.0, history.state.earProgress.meanInitialAbsoluteCents!!, 0.001)
        history.filter(TunerHistoryFilter(guitarId = "another"))
        assertTrue(history.state.entries.isEmpty())
        assertNull(history.state.earProgress.meanInitialAbsoluteCents)
        history.filter(TunerHistoryFilter(stage = TunerStage.EAR_CORRECTION))
        assertEquals(0, history.state.earProgress.validAttempts)
        assertEquals(2, history.state.entries.single().observations.size)
    }
    @Test fun ajusteGuiadoRevisionSinLecturaYCorreccionFinalSonEtapasDistintas() = runTest {
        val history = TunerHistorySession(MemoryTunerResults())
        val config = TunerConfiguration(mode = TunerMode.GUIDED)
        val guided = GuidedTunerSession()
        history.begin(config); guided.begin(config.snapshot(), true)
        var time = 0L
        for (midi in config.currentTuning.notesSixToFirst) repeat(11) {
            guided.measure(PitchEstimate(440 * 2.0.pow((midi - 69) / 12.0)), time); time += 100
        }
        repeat(6) { guided.leavePending() }
        repeat(11) { guided.measure(PitchEstimate(82.406889), time); time += 100 }
        guided.interrupt(); history.finish(guided.state, EarTunerState()); history.savePending()
        val observations = history.state.results.single().observations
        assertEquals(6, observations.count { it.stage == TunerStage.GUIDED_ADJUSTMENT })
        assertEquals(6, observations.count { it.stage == TunerStage.FINAL_REVIEW && it.cents == null && !it.passed })
        assertEquals(1, observations.count { it.stage == TunerStage.FINAL_CORRECTION && it.passed })
        assertEquals(0, history.state.earProgress.validAttempts)
        history.filter(TunerHistoryFilter(tuningKey = config.currentTuning.copy(revision = 2).historyKey))
        assertTrue(history.state.entries.isEmpty())
    }
}
