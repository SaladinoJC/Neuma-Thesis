package ar.edu.unmdp.entrenamiento.rhythm

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RhythmProgressionViewModelTest {
    @Test fun modosBloqueosTempoFijoRepetirYReabrirPerfilSinAudioAutomatico() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val saved = MemoryRhythmResults()
            var starts = 0
            var origin = 0L
            val audio = object : RhythmAudio {
                override suspend fun start(bpm: Int, metronome: Boolean): RhythmAudioStart {
                    starts++; origin = testScheduler.currentTime * 1_000_000L
                    return RhythmAudioStart(origin, "controlled-r6", 0)
                }
                override fun tap() = Unit
                override fun stop() = Unit
            }
            fun create() = RhythmViewModel(audio, { testScheduler.currentTime * 1_000_000L },
                { testScheduler.currentTime }, dispatcher, saved)
            val model = create(); runCurrent()
            model.progression(true); model.select(RhythmCategory.HALF)
            assertTrue(model.ui.value.selected.isEmpty())
            model.select(RhythmCategory.QUARTER); model.metronome(false)
            for (seed in listOf(1L, 2L)) {
                model.generate(seed); runCurrent()
                assertEquals(60, model.ui.value.session.score!!.bpm)
                model.play(); runCurrent()
                val attacks = model.ui.value.session.attacks.toList()
                model.practiceTempo(120); model.progression(false)
                assertTrue(model.ui.value.progression)
                attacks.forEach { model.tapAtUptimeMillis((origin + 4_000_000_000L + it.offsetNanos) / 1_000_000L) }
                advanceTimeBy(20_100); runCurrent(); model.awaitIdle()
            }
            assertEquals(65, model.ui.value.history.progress[RhythmCategory.QUARTER].bpm)
            model.repeat()
            assertFalse(model.ui.value.progression)
            assertEquals("practice", model.ui.value.session.origin)
            model.progression(true); model.generate(3); runCurrent()
            assertEquals(65, model.ui.value.session.score!!.bpm)
            model.background(); runCurrent(); model.awaitIdle()
            val reopened = create(); runCurrent()
            assertEquals(65, reopened.ui.value.history.progress[RhythmCategory.QUARTER].bpm)
            assertEquals(2, starts)
            reopened.progression(false); reopened.practiceTempo(90)
            reopened.select(RhythmCategory.TIED_QUARTERS); reopened.generate(4); runCurrent()
            assertEquals(90, reopened.ui.value.session.score!!.bpm)
            assertEquals("practice", reopened.ui.value.session.origin)
            model.background(); reopened.background(); runCurrent()
        } finally { Dispatchers.resetMain() }
    }

    @Test fun falloDeGuardadoBloqueaNuevaProgresionHastaReintentarSinCambiarRacha() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            var fail = true
            val disk = MemoryRhythmResults()
            val store = object : RhythmResults {
                override suspend fun all() = disk.all()
                override suspend fun save(record: RhythmRecord) { commit(record) }
                override suspend fun commit(record: RhythmRecord): RhythmRecord {
                    if (fail) error("Fallo de disco")
                    return disk.commit(record)
                }
            }
            val audio = object : RhythmAudio {
                override suspend fun start(bpm: Int, metronome: Boolean) = RhythmAudioStart(0, "controlled", 0)
                override fun tap() = Unit
                override fun stop() = Unit
            }
            val model = RhythmViewModel(audio, { testScheduler.currentTime * 1_000_000L }, { testScheduler.currentTime }, dispatcher, store)
            runCurrent(); model.progression(true); model.select(RhythmCategory.QUARTER); model.generate(1); runCurrent()
            model.play(); runCurrent()
            model.ui.value.session.attacks.forEach { model.tapAtUptimeMillis(4000 + it.offsetNanos / 1_000_000L) }
            advanceTimeBy(20_100); runCurrent(); model.awaitIdle()
            assertEquals(0, model.ui.value.history.progress[RhythmCategory.QUARTER].highStreak)
            assertEquals(1, model.pendingForExport().size)
            model.generate(2); runCurrent()
            assertNotNull(model.ui.value.error)
            fail = false; model.retryHistory(); runCurrent(); model.awaitIdle()
            assertEquals(1, model.ui.value.history.progress[RhythmCategory.QUARTER].highStreak)
            model.retryHistory(); runCurrent(); model.awaitIdle()
            assertEquals(1, disk.all().size)
            model.background(); runCurrent()
        } finally { Dispatchers.resetMain() }
    }
}
