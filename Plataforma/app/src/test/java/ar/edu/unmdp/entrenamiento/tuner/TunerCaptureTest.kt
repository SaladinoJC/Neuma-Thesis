package ar.edu.unmdp.entrenamiento.tuner

import ar.edu.unmdp.entrenamiento.voice.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TunerCaptureTest {
    @Test fun `captura detenida libera recursos y no vuelve sola ni conserva lectura vieja`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var opened = 0; var released = 0
            val recorder = object : VoiceCapture {
                override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                    opened++
                    try {
                        repeat(11) { onMeasurement(PitchEstimate(110.0), 0, 44100); delay(100) }
                        awaitCancellation()
                    } finally { released++ }
                }
            }
            val model = TunerViewModel(recorder) { testScheduler.currentTime }
            model.start(); runCurrent(); advanceTimeBy(1050); runCurrent()
            assertTrue(model.ui.value.tuning.inTune)
            advanceTimeBy(450); runCurrent()
            assertFalse(model.ui.value.tuning.inTune)
            assertNull(model.ui.value.tuning.frequency)
            model.stop(); runCurrent()
            assertEquals(1, released); assertFalse(model.ui.value.tuning.running)
            advanceTimeBy(1000); assertEquals(1, opened)
            model.start(); runCurrent(); assertEquals(2, opened)
            assertFalse(model.ui.value.tuning.inTune)
            model.stop(); runCurrent(); assertEquals(2, released)
        } finally { Dispatchers.resetMain() }
    }
}
