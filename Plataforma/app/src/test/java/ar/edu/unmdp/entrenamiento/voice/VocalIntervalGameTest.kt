package ar.edu.unmdp.entrenamiento.voice

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class VocalIntervalGameTest {
    @Test fun `rapid repeated cancellation cannot bypass an unfinished audio release`() = gameTest {
        var playing = false
        val player = object : ReferencePlayer {
            override suspend fun play(midi: Int): Int {
                playing = true
                try { awaitCancellation() }
                finally { withContext(NonCancellable) { delay(1000); playing = false } }
            }
        }
        val model = VocalIntervalViewModel(oscillator(), player, { testScheduler.currentTime })
        model.playReference(); runCurrent()
        model.stop(); runCurrent()
        model.stop(); model.stop(); runCurrent()
        assertTrue(playing)
        assertTrue(model.ui.value.working)
        advanceTimeBy(1100); runCurrent()
        assertFalse(playing)
        assertFalse(model.ui.value.working)
        model.stopAndJoin()
    }

    @Test fun `cancelled reference releases audio before another action and never enables singing`() = gameTest {
        var playing = false
        var calls = 0
        val player = object : ReferencePlayer {
            override suspend fun play(midi: Int): Int {
                calls++; playing = true
                try { awaitCancellation() }
                finally { withContext(NonCancellable) { delay(100); playing = false } }
            }
        }
        val model = VocalIntervalViewModel(oscillator(), player, { testScheduler.currentTime })
        model.playReference(); runCurrent()
        assertTrue(playing)
        model.stop(); runCurrent()
        model.playReference(); model.startAnchor(); runCurrent()
        assertTrue(model.ui.value.working)
        advanceTimeBy(150); runCurrent()
        assertFalse(playing)
        assertFalse(model.ui.value.heardReference)
        assertNull(model.ui.value.round)
        assertEquals(1, calls)
        model.stopAndJoin()
    }

    @Test fun `callback delivered during cancelled capture cleanup cannot change the interrupted result`() = gameTest {
        val capture = object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                onMeasurement(PitchEstimate(220.0), 10, 44100)
                try { awaitCancellation() }
                finally { withContext(NonCancellable) { onMeasurement(PitchEstimate(880.0), 10, 44100) } }
            }
        }
        val model = VocalIntervalViewModel(capture, immediatePlayer(), { testScheduler.currentTime })
        model.playReference(); advanceTimeBy(200); runCurrent()
        model.startAnchor(); runCurrent()
        model.background(); runCurrent()
        val result = requireNotNull(model.ui.value.round?.result)
        assertEquals("background", result.interruption)
        assertEquals(1, result.measurements.size)
        assertNull(result.anchorHz)
        assertFalse(model.ui.value.working)
        model.stopAndJoin()
    }

    @Test fun `timeout while waiting for second note closes without inventing a musical score`() = gameTest {
        val model = VocalIntervalViewModel(oscillator(), immediatePlayer(), { testScheduler.currentTime })
        model.playReference(); advanceTimeBy(200); runCurrent()
        model.startAnchor(); advanceTimeBy(600); runCurrent()
        assertEquals(VocalIntervalPhase.READY_TARGET, model.ui.value.round?.phase)
        advanceTimeBy(60_000); runCurrent()
        val result = requireNotNull(model.ui.value.round?.result)
        assertEquals("timeout", result.interruption)
        assertNull(result.inTune)
        assertFalse(model.ui.value.working)
        model.stopAndJoin()
    }

    @Test fun `capture permission failure is visible and a new round requires a fresh reference`() = gameTest {
        val capture = object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                throw SecurityException("permiso revocado")
            }
        }
        val model = VocalIntervalViewModel(capture, immediatePlayer(), { testScheduler.currentTime })
        model.playReference(); advanceTimeBy(200); runCurrent()
        model.startAnchor(); runCurrent()
        assertEquals("capture-error", model.ui.value.round?.result?.interruption)
        assertTrue(model.ui.value.error!!.contains("permiso revocado"))
        model.newRound()
        assertTrue(model.ui.value.canConfigure)
        assertFalse(model.ui.value.heardReference)
        model.startAnchor(); runCurrent()
        assertNull(model.ui.value.round)
        model.stopAndJoin()
    }

    @Test fun `unexpected capture end is not mistaken for a sustained note`() = gameTest {
        val capture = object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                onMeasurement(PitchEstimate(220.0), 10, 44100)
            }
        }
        val model = VocalIntervalViewModel(capture, immediatePlayer(), { testScheduler.currentTime })
        model.playReference(); advanceTimeBy(200); runCurrent()
        model.startAnchor(); runCurrent()
        assertEquals("capture-ended", model.ui.value.round?.result?.interruption)
        assertNull(model.ui.value.round?.result?.inTune)
        assertNotNull(model.ui.value.error)
        model.stopAndJoin()
    }

    @Test fun `failed second reference note leaves microphone unavailable until a complete replay`() = gameTest {
        var notes = 0
        val player = object : ReferencePlayer {
            override suspend fun play(midi: Int): Int {
                if (++notes == 2) error("audio no disponible")
                return 26460
            }
        }
        val model = VocalIntervalViewModel(oscillator(), player, { testScheduler.currentTime })
        model.playReference(); advanceTimeBy(200); runCurrent()
        assertFalse(model.ui.value.heardReference)
        assertNotNull(model.ui.value.error)
        model.startAnchor(); runCurrent()
        assertNull(model.ui.value.round)
        model.playReference(); advanceTimeBy(200); runCurrent()
        assertTrue(model.ui.value.heardReference)
        model.stopAndJoin()
    }

    @Test fun `descending reference matches selection and a changed register invalidates the previous listening`() = gameTest {
        val notes = mutableListOf<Int>()
        val player = object : ReferencePlayer { override suspend fun play(midi: Int): Int { notes += midi; return 26460 } }
        val model = VocalIntervalViewModel(oscillator(), player, { testScheduler.currentTime })
        model.selectInterval(-12); model.selectAnchor(69)
        model.playReference(); advanceTimeBy(200); runCurrent()
        assertEquals(listOf(69, 57), notes)
        assertTrue(model.ui.value.heardReference)
        model.selectAnchor(48)
        assertFalse(model.ui.value.heardReference)
        model.startAnchor(); runCurrent()
        assertNull(model.ui.value.round)
        model.permissionDenied()
        assertTrue(model.ui.value.permissionDenied)
        assertFalse(model.ui.value.microphoneActive)
        model.stopAndJoin()
    }

    @Test fun `target capture waits for first recorder release and wrong stable target produces feedback`() = gameTest {
        var captures = 0
        var active = false
        val capture = object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                assertFalse(active)
                active = true; captures++
                try { while (true) { onMeasurement(PitchEstimate(220.0), 10, 44100); delay(50) } }
                finally { withContext(NonCancellable) { delay(200); active = false } }
            }
        }
        val model = VocalIntervalViewModel(capture, immediatePlayer(), { testScheduler.currentTime })
        model.playReference(); advanceTimeBy(200); runCurrent()
        model.startAnchor(); advanceTimeBy(550); runCurrent()
        assertEquals(VocalIntervalPhase.READY_TARGET, model.ui.value.round?.phase)
        model.startTarget(); runCurrent()
        assertEquals(1, captures)
        assertTrue(active)
        advanceTimeBy(200); runCurrent()
        assertFalse(active)
        model.selectInterval(0)
        assertEquals(7, model.ui.value.semitones)
        model.startTarget(); advanceTimeBy(800); runCurrent()
        val result = requireNotNull(model.ui.value.round?.result)
        assertEquals(false, result.inTune)
        assertEquals(-700.0, result.errorCents!!, 0.001)
        assertEquals(2, captures)
        assertFalse(active)
        model.stopAndJoin()
    }

    private fun gameTest(body: suspend TestScope.() -> Unit) = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try { body() } finally { Dispatchers.resetMain() }
    }

    private fun immediatePlayer() = object : ReferencePlayer { override suspend fun play(midi: Int) = 26460 }

    private fun oscillator() = object : VoiceCapture {
        override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
            while (true) { onMeasurement(PitchEstimate(220.0), 10, 44100); delay(50) }
        }
    }

    @Test fun `new round cannot silently discard an acquired anchor while waiting for the second note`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val capture = object : VoiceCapture {
                override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                    while (true) { onMeasurement(PitchEstimate(220.0), 10, 44100); delay(50) }
                }
            }
            val player = object : ReferencePlayer { override suspend fun play(midi: Int) = 26460 }
            val model = VocalIntervalViewModel(capture, player, monotonicMillis = { testScheduler.currentTime })
            model.playReference(); advanceTimeBy(200); runCurrent()
            model.startAnchor(); advanceTimeBy(600); runCurrent()
            val acquired = requireNotNull(model.ui.value.round)
            model.newRound()
            assertEquals(VocalIntervalPhase.READY_TARGET, model.ui.value.round?.phase)
            assertSame(acquired, model.ui.value.round)
            model.stopAndJoin()
        } finally { Dispatchers.resetMain() }
    }

    @Test fun `player listens then sings two separately captured notes and sees the relative result`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var activeCapture = false
            var captures = 0
            val played = mutableListOf<Int>()
            val capture = object : VoiceCapture {
                override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                    assertFalse(activeCapture)
                    activeCapture = true
                    val hz = if (++captures == 1) 220.0 else 329.6275569128699
                    try {
                        while (true) { onMeasurement(PitchEstimate(hz), 10, 44100); delay(50) }
                    } finally { activeCapture = false }
                }
            }
            val player = object : ReferencePlayer {
                override suspend fun play(midi: Int): Int { assertFalse(activeCapture); played += midi; return 26460 }
            }
            val model = VocalIntervalViewModel(capture, player, monotonicMillis = { testScheduler.currentTime })
            model.startAnchor(); runCurrent()
            assertEquals(0, captures)
            model.playReference(); advanceTimeBy(200); runCurrent()
            assertEquals(listOf(57, 64), played)
            assertTrue(model.ui.value.heardReference)
            model.startAnchor(); advanceTimeBy(600); runCurrent()
            assertEquals(VocalIntervalPhase.READY_TARGET, model.ui.value.round!!.phase)
            assertFalse(activeCapture)
            model.startTarget(); advanceTimeBy(600); runCurrent()
            val result = requireNotNull(model.ui.value.round!!.result)
            assertEquals(700.0, result.intervalCents!!, 0.001)
            assertEquals(true, result.inTune)
            assertFalse(activeCapture)
            assertFalse(model.ui.value.working)
            model.stopAndJoin()
        } finally { Dispatchers.resetMain() }
    }
}
