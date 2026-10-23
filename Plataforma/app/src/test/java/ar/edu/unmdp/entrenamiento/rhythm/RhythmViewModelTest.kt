package ar.edu.unmdp.entrenamiento.rhythm

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RhythmViewModelTest {
    @Test fun `generar es silencioso y timestamp tactil tardio repara omision sin reiniciar audio`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            var starts = 0; var tapSounds = 0; var active = false
            val audio = object : RhythmAudio {
                override suspend fun start(bpm: Int, metronome: Boolean): RhythmAudioStart {
                    starts++; active = true; return RhythmAudioStart(0, "controlled", 0)
                }
                override fun tap() { tapSounds++ }
                override fun stop() { active = false }
            }
            val model = RhythmViewModel(audio, { testScheduler.currentTime * 1_000_000 }, { testScheduler.currentTime }, dispatcher)
            model.select(RhythmCategory.QUARTER); model.generate(42); runCurrent()
            assertEquals(RhythmPhase.PREVIEW, model.ui.value.session.phase)
            assertEquals(0, starts)
            model.play(); runCurrent(); assertEquals(1, starts)
            advanceTimeBy(20_100); runCurrent()
            assertTrue(requireNotNull(model.ui.value.session.result).completed)
            assertFalse(active)
            model.tapAtUptimeMillis(4_000)
            assertEquals(TapKind.ON_TIME, requireNotNull(model.ui.value.session.result).attacks.first().kind)
            assertEquals(0, tapSounds)
            model.background(); runCurrent()
        } finally { Dispatchers.resetMain() }
    }
}
