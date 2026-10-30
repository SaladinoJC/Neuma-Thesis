package ar.edu.unmdp.entrenamiento.rhythm

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RhythmHistoryViewModelTest {
    @Test fun reintentoFallidoNoOcultaErrorNiPierdePendienteExportable() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            var failing = true
            val saved = MemoryRhythmResults()
            val store = object : RhythmResults {
                override suspend fun all() = saved.all()
                override suspend fun save(record: RhythmRecord) { if (failing) error("Disco no disponible"); saved.save(record) }
            }
            val audio = object : RhythmAudio {
                override suspend fun start(bpm: Int, metronome: Boolean) = RhythmAudioStart(0, "controlled", 0)
                override fun tap() = Unit
                override fun stop() = Unit
            }
            val model = RhythmViewModel(audio, { 0 }, { 0 }, dispatcher, resultsStore = store)
            model.select(RhythmCategory.QUARTER); model.generate(12); runCurrent()
            model.play(); runCurrent(); model.background(); runCurrent(); model.awaitIdle()
            val id = model.pendingForExport().single().id
            model.retryHistory(); runCurrent(); model.awaitIdle()
            assertNotNull(model.ui.value.history.error)
            assertEquals(id, model.pendingForExport().single().id)
            failing = false
            model.retryHistory(); runCurrent(); model.awaitIdle()
            assertNull(model.ui.value.history.error)
            assertTrue(model.pendingForExport().isEmpty())
            assertEquals(id, saved.all().single().id)
        } finally { Dispatchers.resetMain() }
    }

    @Test fun finalizacionTapTardioReabrirYRepeticionConservanUnaPartidaRevisadaYUnNuevoIntento() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val saved = MemoryRhythmResults()
            var starts = 0
            val audio = object : RhythmAudio {
                override suspend fun start(bpm: Int, metronome: Boolean): RhythmAudioStart {
                    starts++; return RhythmAudioStart(testScheduler.currentTime * 1_000_000L, "controlled", 0)
                }
                override fun tap() = Unit
                override fun stop() = Unit
            }
            fun model() = RhythmViewModel(audio, { testScheduler.currentTime * 1_000_000L }, { testScheduler.currentTime }, dispatcher,
                resultsStore = saved, wallClock = { 1000 + testScheduler.currentTime })
            val model = model()
            model.select(RhythmCategory.QUARTER); model.generate(42); runCurrent()
            model.play(); runCurrent(); advanceTimeBy(20_100); runCurrent(); model.awaitIdle()
            val initial = saved.all().single()
            assertTrue(initial.result.completed)
            model.tapAtUptimeMillis(4000); runCurrent(); model.awaitIdle()
            val repaired = saved.all().single()
            assertEquals(initial.id, repaired.id)
            assertTrue(repaired.revision > initial.revision)
            assertEquals(TapKind.ON_TIME, repaired.result.attacks.first().kind)
            val reopened = model(); runCurrent()
            assertEquals(listOf(repaired), reopened.ui.value.history.results)
            assertEquals(1, starts)
            reopened.repeatRecord(repaired.id)
            assertEquals(initial.result.score, reopened.ui.value.session.score)
            assertTrue(reopened.ui.value.session.repeated)
            assertEquals(1, starts)
            reopened.play(); runCurrent(); reopened.background(); runCurrent(); reopened.awaitIdle()
            val repeat = saved.all().single { it.id != repaired.id }
            assertEquals(repaired.id, repeat.repeatedFromId)
            assertEquals(repaired.fingerprint, repeat.fingerprint)
            assertTrue(repeat.result.repeated)
            assertFalse(repeat.result.completed)
            reopened.background(); reopened.background(); runCurrent(); reopened.awaitIdle()
            assertEquals(2, saved.all().size)
            model.background()
        } finally { Dispatchers.resetMain() }
    }

    @Test fun segundoPlanoDuranteConteoGuardaInterrumpidaYPreparacionSinPlayNoCreaHistorial() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val saved = MemoryRhythmResults()
            val audio = object : RhythmAudio {
                override suspend fun start(bpm: Int, metronome: Boolean) = RhythmAudioStart(0, "controlled", 0)
                override fun tap() = Unit
                override fun stop() = Unit
            }
            val model = RhythmViewModel(audio, { testScheduler.currentTime * 1_000_000L }, { testScheduler.currentTime }, dispatcher, resultsStore = saved)
            model.select(RhythmCategory.TIED_QUARTERS); model.generate(12); runCurrent()
            model.background(); runCurrent(); model.awaitIdle(); assertTrue(saved.all().isEmpty())
            model.play(); runCurrent(); model.background(); runCurrent(); model.awaitIdle()
            val record = saved.all().single()
            assertFalse(record.result.completed)
            assertTrue(record.result.attacks.all { it.kind == null })
            assertEquals(0, record.result.omissions)
            model.resetDisplay(); assertTrue(model.ui.value.history.results.isEmpty())
        } finally { Dispatchers.resetMain() }
    }
}
