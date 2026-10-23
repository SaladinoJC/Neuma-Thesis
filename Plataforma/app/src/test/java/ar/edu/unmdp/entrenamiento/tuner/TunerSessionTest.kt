package ar.edu.unmdp.entrenamiento.tuner

import ar.edu.unmdp.entrenamiento.voice.PitchEstimate
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.pow

class TunerSessionTest {
    private fun reading(cents: Double) = PitchEstimate(110.0 * 2.0.pow(cents / 1200.0))
    @Test fun `margenes inclusivos no acumulan silencio pausas cambios ni huecos`() {
        for (criterion in TunerCriterion.entries) {
            for (sign in listOf(-1, 1)) {
                val session = TunerSession()
                session.criterion(criterion); session.start(); session.fixString(5)
                val edge = reading(sign * criterion.margin)
                for (t in 0L..1000L step 100) session.measure(edge, t)
                assertTrue(session.state.inTune)
                session.measure(reading(sign * (criterion.margin + .01)), 1100)
                assertFalse(session.state.inTune)
                session.measure(edge, 1200)
                session.measure(PitchEstimate(), 1300)
                for (t in 1400L..2300L step 100) session.measure(edge, t)
                assertFalse(session.state.inTune)
                session.measure(edge, 2400); assertTrue(session.state.inTune)
                session.measure(edge, 2800); assertFalse(session.state.inTune)
                session.criterion(criterion); assertFalse(session.state.inTune)
                session.stop(); session.measure(edge, 4000)
                assertNull(session.state.frequency)
                session.start(); session.measure(edge, 5000)
                assertFalse(session.state.inTune)
            }
        }
    }
    @Test fun `automatico no inventa cuerda y cromatico sigue notas ajenas a guitarra`() {
        val session = TunerSession(); session.start()
        session.measure(PitchEstimate(440.0), 0)
        assertNull(session.state.stringNumber); assertNull(session.state.targetMidi)
        assertEquals(69, session.state.measuredMidi)
        session.tracking(TunerTracking.CHROMATIC)
        for (t in 0L..1000L step 100) session.measure(PitchEstimate(440.0), t)
        assertEquals(69, session.state.targetMidi); assertTrue(session.state.inTune)
        session.fixString(5); session.measure(PitchEstimate(440.0), 1100)
        assertEquals(45, session.state.targetMidi)
        assertEquals(2400.0, requireNotNull(session.state.cents), .00001)
        session.fixString(null); session.measure(PitchEstimate(110.0), 1200)
        assertEquals(5, session.state.stringNumber)
        session.measure(PitchEstimate(Double.NaN), 1300)
        assertNull(session.state.measuredMidi); assertFalse(session.state.inTune)
    }
    @Test fun `PCM grave armonico ruido y octava conserva alturas reales`() {
        val yin = ar.edu.unmdp.entrenamiento.voice.YinEstimator()
        for ((hz, midi) in listOf(82.406889 to 40, 110.0 to 45, 146.832384 to 50,
            195.997718 to 55, 246.941651 to 59, 329.627557 to 64, 164.813778 to 52)) {
            val pcm = FloatArray(4096) {
                val phase = 2 * kotlin.math.PI * hz * it / 44100
                (.3 * kotlin.math.sin(phase) + .1 * kotlin.math.sin(2 * phase)).toFloat()
            }
            val estimate = yin.estimate(pcm)
            assertEquals(midi, estimate.midi)
            val cents = requireNotNull(estimate.cents(midi))
            println("TUNER synthetic Hz=$hz MIDI=$midi errorCents=$cents")
            assertTrue(kotlin.math.abs(cents) < 1.0)
        }
        assertFalse(yin.estimate(FloatArray(4096)).valid)
        val random = kotlin.random.Random(93)
        assertFalse(yin.estimate(FloatArray(4096) { random.nextFloat() - .5f }).valid)
    }
    @Test fun `sexta cuerda requiere un segundo continuo y conserva octava al fijarla`() {
        val session = TunerSession()
        session.start()
        for (time in 0L..1000L step 100) session.measure(PitchEstimate(82.406889), time)
        assertEquals(6, session.state.stringNumber)
        assertEquals(40, session.state.measuredMidi)
        assertTrue(session.state.inTune)
        session.fixString(6)
        session.measure(PitchEstimate(164.813778), 1100)
        assertEquals(40, session.state.targetMidi)
        assertEquals(52, session.state.measuredMidi)
        assertEquals(1200.0, requireNotNull(session.state.cents), .01)
        assertFalse(session.state.inTune)
    }
}
