package ar.edu.unmdp.entrenamiento.tuner

import ar.edu.unmdp.entrenamiento.voice.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TunerCaptureTest {
    @Test fun `cola sintetica decreciente de cuerdas agudas mantiene evidencia suficiente`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            for (hz in listOf(246.941651, 329.627557)) {
                val recorder = object : VoiceCapture {
                    override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                        val estimator = YinEstimator(configuration)
                        for (at in 0L..700L step 50) {
                            val amplitude = .025 * kotlin.math.exp(-at / 400.0)
                            val pcm = FloatArray(configuration.windowSize) { frame ->
                                (amplitude * kotlin.math.sin(2 * kotlin.math.PI * hz * frame / configuration.sampleRate)).toFloat()
                            }
                            onMeasurement(estimator.estimate(pcm), 0, configuration.sampleRate)
                            delay(50)
                        }
                        awaitCancellation()
                    }
                }
                val model = TunerViewModel(recorder) { currentTime }
                try {
                    model.start(); runCurrent(); advanceTimeBy(450); runCurrent()
                    assertTrue("La cola periódica de $hz Hz no debe cortarse antes de confirmar", model.ui.value.tuning.inTune)
                } finally { model.stop(); runCurrent() }
            }
        } finally { Dispatchers.resetMain() }
    }
    @Test fun `pulsaciones breves de Si y Mi agudas se confirman antes de decaer sin detener microfono`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            for (hz in listOf(246.941651, 329.627557)) {
                val recorder = object : VoiceCapture {
                    override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                        repeat(6) { onMeasurement(PitchEstimate(hz), 0, configuration.sampleRate); delay(100) }
                        onMeasurement(PitchEstimate(reason = "Silencio o nivel insuficiente"), 0, configuration.sampleRate)
                        awaitCancellation()
                    }
                }
                val model = TunerViewModel(recorder) { currentTime }
                try {
                    model.start(); runCurrent(); advanceTimeBy(500); runCurrent()
                    assertTrue("Debe confirmar antes de que decaiga la cuerda", model.ui.value.tuning.inTune)
                    advanceTimeBy(300); runCurrent()
                    assertTrue(model.ui.value.tuning.running)
                    assertFalse(model.ui.value.tuning.inTune)
                    assertNotNull(model.ui.value.tuning.lastConfirmation)
                } finally { model.stop(); runCurrent() }
            }
        } finally { Dispatchers.resetMain() }
    }
    @Test fun `cambiar afinacion drena captura y releer preferencias nunca reabre microfono`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var opened = 0; var released = 0
            val recorder = object : VoiceCapture {
                override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                    opened++
                    try {
                        repeat(11) { onMeasurement(PitchEstimate(82.406889), 0, 44100); delay(100) }
                        awaitCancellation()
                    } finally { released++ }
                }
            }
            val store = MemoryTunerConfiguration()
            val model = TunerViewModel(recorder, store) { testScheduler.currentTime }
            model.awaitConfigurationIdle()
            model.start(); runCurrent(); advanceTimeBy(1050); runCurrent()
            assertTrue(model.ui.value.tuning.inTune)
            model.selectTuning("drop-d")
            assertFalse(model.ui.value.tuning.running)
            model.awaitConfigurationIdle()
            assertEquals(1, released)
            assertFalse(model.ui.value.tuning.inTune)
            assertNull(model.ui.value.tuning.frequency)
            model.fixString(6)
            assertEquals(38, model.ui.value.tuning.targetMidi)
            model.criterion(TunerCriterion.PRO)
            model.tracking(TunerTracking.CHROMATIC)
            model.awaitConfigurationIdle()
            assertEquals(TunerCriterion.PRO, store.load().criterion)
            assertEquals(TunerTracking.CHROMATIC, store.load().tracking)
            model.reload(); model.awaitConfigurationIdle()
            advanceTimeBy(1000); runCurrent()
            assertEquals(1, opened)
            assertFalse(model.ui.value.tuning.running)
            assertFalse(model.ui.value.configurationBusy)
            assertEquals("drop-d", model.ui.value.configuration.configuration!!.currentTuning.id)
        } finally { Dispatchers.resetMain() }
    }

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
