package ar.edu.unmdp.entrenamiento.rhythm

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RhythmCalibrationViewModelTest {
    @Test fun `calibracion explicita no puntua y se conserva en la siguiente partida e historial`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var model: RhythmViewModel? = null
        try {
            var origin = 0L
            var playing = false
            var echoes = 0
            val audio = object : RhythmAudio {
                override suspend fun start(bpm: Int, metronome: Boolean): RhythmAudioStart {
                    playing = true
                    origin = currentTime * 1_000_000L + 750_000_000L
                    return RhythmAudioStart(origin, "controlled-lead", 0)
                }
                override fun tap() { echoes++ }
                override fun stop() { playing = false }
            }
            val game = RhythmViewModel(audio, { currentTime * 1_000_000L }, { currentTime }, dispatcher)
            model = game
            game.openCalibration(); game.startCalibration(); runCurrent()
            assertTrue(game.ui.value.calibrationState.active)
            for (pulse in 4..15) {
                val at = origin / 1_000_000 + pulse * 750L + 100
                advanceTimeBy(at - currentTime); runCurrent()
                game.tapAtUptimeMillis(at)
            }
            advanceUntilIdle()
            assertFalse(playing)
            assertEquals(100_000_000L, game.ui.value.calibrationState.candidate?.offsetNanos)
            assertNull(game.ui.value.calibration)
            assertTrue(game.ui.value.history.results.isEmpty())
            assertEquals(0, echoes)
            game.applyCalibration()
            assertFalse(game.ui.value.calibrationOpen)
            game.select(RhythmCategory.SIXTEENTHS); game.practiceTempo(120); game.generate(42); runCurrent()
            game.play(); runCurrent()
            val attacks = game.ui.value.session.attacks.toList()
            for (attack in attacks) {
                val at = (origin + 2_000_000_000L + attack.offsetNanos + 100_000_000L) / 1_000_000
                advanceTimeBy(at - currentTime); runCurrent()
                game.tapAtUptimeMillis(at)
            }
            advanceUntilIdle()
            val result = requireNotNull(game.ui.value.session.result)
            assertTrue(result.attacks.all { it.kind == TapKind.ON_TIME })
            assertEquals(100_000_000L, result.calibration?.offsetNanos)
            assertEquals(result, game.ui.value.history.results.single().result)
            game.clearCalibration()
            assertNull(game.ui.value.calibration)
            assertNotNull(game.ui.value.history.results.single().result.calibration)
        } finally {
            model?.background(); advanceUntilIdle(); Dispatchers.resetMain()
        }
    }

    @Test fun `cancelar calibracion libera audio y no modifica partitura ni historial`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var playing = false
        val audio = object : RhythmAudio {
            override suspend fun start(bpm: Int, metronome: Boolean): RhythmAudioStart {
                playing = true; return RhythmAudioStart(currentTime * 1_000_000L, "controlled", 0)
            }
            override fun tap() = Unit
            override fun stop() { playing = false }
        }
        val model = RhythmViewModel(audio, { currentTime * 1_000_000L }, { currentTime }, dispatcher)
        try {
            model.select(RhythmCategory.QUARTER); model.generate(7); runCurrent()
            val score = model.ui.value.session.score
            model.openCalibration(); model.startCalibration(); runCurrent()
            assertTrue(playing)
            model.closeCalibration(); advanceUntilIdle()
            assertFalse(playing)
            assertFalse(model.ui.value.calibrationState.active)
            assertFalse(model.ui.value.calibrationOpen)
            assertNull(model.ui.value.calibration)
            assertEquals(score, model.ui.value.session.score)
            assertTrue(model.ui.value.history.results.isEmpty())
        } finally { model.background(); advanceUntilIdle(); Dispatchers.resetMain() }
    }
}
