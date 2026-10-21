package ar.edu.unmdp.entrenamiento.voice

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class VoiceSessionTest {
    @Test fun `fallo de guardado retiene mediciones y reintenta el mismo identificador`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var attempts = 0
            val entries = mutableListOf<VoiceResult>()
            val results = object : VoiceResults {
                override suspend fun all() = entries.toList()
                override suspend fun save(result: VoiceResult) {
                    if (++attempts == 1) error("Disco no disponible")
                    entries.add(result)
                }
            }
            val capture = object : VoiceCapture {
                override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                    onMeasurement(PitchEstimate(220.0), 10, 44100)
                }
            }
            val player = object : ReferencePlayer { override suspend fun play(midi: Int) = 26460 }
            val model = VoiceViewModel(results, capture, player, deviceInfo = { "JVM" to "test" })
            model.start(); advanceUntilIdle()
            assertTrue(model.ui.value.pendingSave)
            val pending = requireNotNull(model.pendingForExport())
            assertEquals(1, pending.measurements.size)
            model.start(); advanceUntilIdle(); assertEquals(1, attempts)
            model.retrySave(); model.retrySave(); advanceUntilIdle()
            assertFalse(model.ui.value.pendingSave)
            assertEquals(listOf(pending.id), entries.map { it.id })
            assertEquals(1, model.ui.value.saved)
        } finally { Dispatchers.resetMain() }
    }
    @Test fun `escuchar objetivo cierra captura antes del tono e impide grabarlo`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var recording = false
            var playing = false
            val released = CompletableDeferred<Unit>()
            val capture = object : VoiceCapture {
                override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                    recording = true
                    try { awaitCancellation() } finally { recording = false }
                }
            }
            val player = object : ReferencePlayer {
                override suspend fun play(midi: Int): Int {
                    assertFalse(recording); playing = true
                    released.await(); playing = false; return 26460
                }
            }
            val results = object : VoiceResults {
                val entries = mutableListOf<VoiceResult>()
                override suspend fun all() = entries.toList()
                override suspend fun save(result: VoiceResult) { entries.add(result) }
            }
            val model = VoiceViewModel(results, capture, player, deviceInfo = { "JVM test" to "test" })
            model.start(); runCurrent(); assertTrue(recording)
            model.playReference(); runCurrent()
            assertFalse(recording); assertTrue(playing)
            assertTrue(model.ui.value.referencePlaying)
            model.start(); runCurrent(); assertFalse(recording)
            released.complete(Unit); advanceUntilIdle()
            assertFalse(model.ui.value.referencePlaying)
            assertNotNull(model.ui.value.lastCapture)
        } finally { Dispatchers.resetMain() }
    }
    @Test fun `grafica conserva diez segundos y el silencio borra la estimacion actual`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var now = 0L
            val capture = object : VoiceCapture {
                override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                    repeat(60) { i -> now = i*200_000_000L; onMeasurement(if (i == 59) PitchEstimate() else PitchEstimate(440.0), 10, 44100) }
                    awaitCancellation()
                }
            }
            val results = object : VoiceResults {
                override suspend fun all() = emptyList<VoiceResult>()
                override suspend fun save(result: VoiceResult) {}
            }
            val player = object : ReferencePlayer { override suspend fun play(midi: Int) = 26460 }
            val model = VoiceViewModel(results, capture, player, monotonicNanos = { now }, deviceInfo = { "JVM test" to "test" })
            model.start(); runCurrent()
            assertEquals(51, model.ui.value.recent.size)
            assertFalse(requireNotNull(model.ui.value.latest).valid)
            model.stop(); advanceUntilIdle()
            assertNull(model.ui.value.latest)
            assertEquals(60, requireNotNull(model.ui.value.lastCapture).measurements.size)
        } finally { Dispatchers.resetMain() }
    }
}
