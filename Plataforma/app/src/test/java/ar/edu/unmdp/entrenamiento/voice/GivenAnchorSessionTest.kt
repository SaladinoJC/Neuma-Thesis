package ar.edu.unmdp.entrenamiento.voice

import org.junit.Assert.*
import org.junit.Test
import kotlin.math.PI
import kotlin.math.sin

class GivenAnchorSessionTest {
    @Test fun `given reference keeps descending unison and octave errors without folding`() {
        for ((key, hz, expected) in listOf(Triple(-12, 110.0, 0.0), Triple(0, 220.0, 0.0), Triple(7, 659.2551138257398, 1200.0))) {
            val session = VocalIntervalSession(key, givenAnchorHz = 220.0)
            session.beginTarget()
            for (t in 0L..500L step 50) session.accept(t, PitchEstimate(hz))
            val result = session.state.result!!
            assertEquals(expected, result.errorCents!!, .001)
            assertEquals(expected == 0.0, result.inTune)
        }
    }

    @Test fun `invalid signal and capture gaps cannot finish a given anchor response`() {
        for (bad in listOf(null, Double.NaN, Double.POSITIVE_INFINITY)) {
            val session = VocalIntervalSession(0, givenAnchorHz = 220.0)
            session.beginTarget()
            for (t in 0L..250L step 50) session.accept(t, PitchEstimate(220.0))
            session.accept(300, PitchEstimate(bad))
            for (t in 350L..800L step 50) session.accept(t, PitchEstimate(220.0))
            assertNull(session.state.result)
            session.tick(60_000)
            assertEquals("timeout", session.state.result!!.interruption)
            assertEquals(VocalAnchorSource.GIVEN, session.state.result!!.anchorSource)
            assertNull(session.state.result!!.inTune)
        }
    }

    @Test fun `invalid external anchors are rejected before a round starts`() {
        for (hz in listOf(Double.NaN, Double.POSITIVE_INFINITY, 0.0, 20.0, 2000.0)) {
            assertTrue(runCatching { VocalIntervalSession(0, givenAnchorHz = hz) }.exceptionOrNull() is IllegalArgumentException)
        }
    }

    @Test fun `synthetic PCM traverses YIN into a single given anchor target`() {
        for ((key, frequency) in listOf(-12 to 110.0, 0 to 220.0, 7 to 329.6275569128699, 12 to 440.0)) {
            val session = VocalIntervalSession(key, givenAnchorHz = 220.0)
            val yin = YinEstimator()
            session.beginTarget()
            for (t in 0L..500L step 50) {
                val pcm = FloatArray(4096) { (.3 * sin(2 * PI * frequency * (it + t * 44.1) / 44100)).toFloat() }
                session.accept(t, yin.estimate(pcm))
            }
            val result = session.state.result!!
            assertEquals(key * 100.0, result.intervalCents!!, 10.0)
            assertEquals(true, result.inTune)
            println("Synthetic PCM given anchor=220Hz, interval=$key, measured=${result.intervalCents}, error=${result.errorCents}")
        }
    }

    @Test fun `given anchor evaluates one sung target and keeps the anchor provenance`() {
        val session = VocalIntervalSession(7, givenAnchorHz = 220.0)
        assertEquals(VocalIntervalPhase.READY_TARGET, session.state.phase)
        session.beginTarget()
        for (t in 0L..500L step 50) session.accept(t, PitchEstimate(329.6275569128699))
        val result = requireNotNull(session.state.result)
        assertEquals(VocalAnchorSource.GIVEN, result.anchorSource)
        assertEquals(220.0, result.anchorHz!!, .001)
        assertEquals(700.0, result.intervalCents!!, .001)
        assertEquals(true, result.inTune)
        assertEquals(11, result.measurements.size)
        assertTrue(result.measurements.all { it.phase == VocalIntervalPhase.TARGET })
    }
}
