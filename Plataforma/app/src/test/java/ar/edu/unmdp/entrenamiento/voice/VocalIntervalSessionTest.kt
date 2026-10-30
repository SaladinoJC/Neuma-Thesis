package ar.edu.unmdp.entrenamiento.voice

import org.junit.Assert.*
import org.junit.Test
import kotlin.math.PI
import kotlin.math.sin

class VocalIntervalSessionTest {
    @Test fun `imitation evaluates a fifth from the sung anchor regardless of register`() {
        val session = VocalIntervalSession(targetSemitones = 7)
        session.beginAnchor()
        for (t in 0L..500L step 50) session.accept(t, PitchEstimate(220.0))
        assertEquals(VocalIntervalPhase.READY_TARGET, session.state.phase)
        session.beginTarget()
        for (t in 600L..1100L step 50) session.accept(t, PitchEstimate(329.6275569128699))
        val result = requireNotNull(session.state.result)
        assertEquals(VocalIntervalPhase.COMPLETED, session.state.phase)
        assertEquals(700.0, result.intervalCents!!, 0.001)
        assertEquals(0.0, result.errorCents!!, 0.001)
        assertTrue(result.inTune == true)
        assertEquals(22, result.measurements.size)
    }

    @Test fun `a stable wrong octave remains an error rather than being folded`() {
        val result = completed(7, 220.0, 659.2551138257398)
        assertEquals(1200.0, result.errorCents!!, 0.001)
        assertEquals(false, result.inTune)
    }

    @Test fun `descending octave and explicitly separated unison are playable`() {
        val descending = completed(-12, 440.0, 220.0)
        assertEquals(-1200.0, descending.intervalCents!!, 0.001)
        assertEquals(true, descending.inTune)
        assertEquals(true, completed(0, 330.0, 330.0).inTune)
    }

    @Test fun `transposing both sung notes does not change the interval`() {
        for ((anchor, target) in listOf(110.0 to 164.81377845643496, 440.0 to 659.2551138257398)) {
            assertEquals(700.0, completed(7, anchor, target).intervalCents!!, 0.001)
        }
    }

    @Test fun `silence invalid frequencies and long gaps cannot bridge a hold`() {
        for (bad in listOf(null, Double.NaN, Double.POSITIVE_INFINITY, 0.0, -220.0, 2000.0)) {
            val session = VocalIntervalSession(7)
            session.beginAnchor()
            for (t in 0L..250L step 50) session.accept(t, PitchEstimate(220.0))
            session.accept(300, PitchEstimate(bad))
            for (t in 350L..800L step 50) session.accept(t, PitchEstimate(220.0))
            assertEquals(VocalIntervalPhase.ANCHOR, session.state.phase)
            session.accept(850, PitchEstimate(220.0))
            assertEquals(VocalIntervalPhase.READY_TARGET, session.state.phase)
        }
        val session = VocalIntervalSession(7)
        session.beginAnchor()
        for (t in 0L..250L step 50) session.accept(t, PitchEstimate(220.0))
        for (t in 500L..950L step 50) session.accept(t, PitchEstimate(220.0))
        assertEquals(VocalIntervalPhase.ANCHOR, session.state.phase)
        session.accept(1000, PitchEstimate(220.0))
        assertEquals(VocalIntervalPhase.READY_TARGET, session.state.phase)
    }

    @Test fun `a drifting attack must leave the full stable window before acquisition`() {
        val session = VocalIntervalSession(0)
        session.beginAnchor()
        session.accept(0, PitchEstimate(220.0))
        session.accept(50, PitchEstimate(233.08188075904496))
        for (t in 100L..550L step 50) session.accept(t, PitchEstimate(220.0))
        assertEquals(VocalIntervalPhase.ANCHOR, session.state.phase)
        session.accept(600, PitchEstimate(220.0))
        assertEquals(VocalIntervalPhase.READY_TARGET, session.state.phase)
    }

    @Test fun `timeout during silence or between notes never invents a musical outcome`() {
        for (acquireAnchor in listOf(false, true)) {
            val session = VocalIntervalSession(7)
            session.beginAnchor()
            if (acquireAnchor) for (t in 0L..500L step 50) session.accept(t, PitchEstimate(220.0))
            session.tick(60_000)
            val result = requireNotNull(session.state.result)
            assertEquals(VocalIntervalPhase.INTERRUPTED, session.state.phase)
            assertEquals("timeout", result.interruption)
            assertNull(result.errorCents)
            assertNull(result.inTune)
        }
    }

    @Test fun `stale callbacks wrong phases and post closure input are ignored`() {
        val session = VocalIntervalSession(7)
        assertFalse(session.accept(0, PitchEstimate(220.0)))
        session.beginTarget()
        assertEquals(VocalIntervalPhase.READY_ANCHOR, session.state.phase)
        session.beginAnchor()
        assertFalse(session.accept(-1, PitchEstimate(220.0)))
        assertTrue(session.accept(100, PitchEstimate(220.0)))
        assertFalse(session.accept(100, PitchEstimate(880.0)))
        assertFalse(session.accept(50, PitchEstimate(880.0)))
        session.interrupt("background")
        val result = session.state.result
        assertEquals(1, result!!.measurements.size)
        assertEquals("background", result.interruption)
        session.beginAnchor()
        session.beginTarget()
        session.tick(60_000)
        session.interrupt("replacement")
        assertFalse(session.accept(200, PitchEstimate(220.0)))
        assertSame(result, session.state.result)
    }

    @Test fun `bounded telemetry interrupts without granting a completed round`() {
        val session = VocalIntervalSession(7, VocalIntervalConfiguration(maximumMeasurements = 2))
        session.beginAnchor()
        for (t in 0L..100L step 50) session.accept(t, PitchEstimate(220.0))
        val result = requireNotNull(session.state.result)
        assertEquals("measurement-limit", result.interruption)
        assertEquals(2, result.measurements.size)
        assertNull(result.inTune)
    }

    @Test fun `synthetic PCM traverses the real YIN and public round to a fourth`() {
        val yin = YinEstimator()
        val session = VocalIntervalSession(5)
        fun feed(start: Long, hz: Double) {
            for (t in start..start + 500 step 50) {
                val pcm = FloatArray(4096) { (.3 * sin(2 * PI * hz * (it + t * 44.1) / 44100)).toFloat() }
                session.accept(t, yin.estimate(pcm))
            }
        }
        session.beginAnchor()
        feed(0, 220.0)
        assertEquals(VocalIntervalPhase.READY_TARGET, session.state.phase)
        session.beginTarget()
        feed(600, 293.6647679174076)
        val result = requireNotNull(session.state.result)
        assertEquals(500.0, result.intervalCents!!, 10.0)
        assertEquals(true, result.inTune)
        println("Synthetic PCM only: anchorHz=${result.anchorHz}, targetHz=${result.targetHz}, intervalCents=${result.intervalCents}, errorCents=${result.errorCents}")
        assertEquals(22, result.measurements.size)
    }

    private fun completed(semitones: Int, anchor: Double, target: Double): VocalIntervalResult {
        val session = VocalIntervalSession(semitones)
        session.beginAnchor()
        for (t in 0L..500L step 50) session.accept(t, PitchEstimate(anchor))
        session.beginTarget()
        for (t in 600L..1100L step 50) session.accept(t, PitchEstimate(target))
        val result = requireNotNull(session.state.result)
        assertEquals(VocalIntervalPhase.COMPLETED, session.state.phase)
        assertFalse(session.accept(1200, PitchEstimate(target)))
        assertSame(result, session.state.result)
        return result
    }
}
