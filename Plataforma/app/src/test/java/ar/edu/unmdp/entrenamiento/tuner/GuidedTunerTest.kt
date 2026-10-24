package ar.edu.unmdp.entrenamiento.tuner

import ar.edu.unmdp.entrenamiento.voice.PitchEstimate
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.pow

class GuidedTunerTest {
    private fun pitch(midi: Int, cents: Double = 0.0) = PitchEstimate(440 * 2.0.pow((midi - 69) / 12.0 + cents / 1200))
    private fun finishString(session: GuidedTunerSession, time: Long): Long {
        val midi = requireNotNull(session.state.measurement.targetMidi)
        repeat(11) { session.measure(pitch(midi), time + it * 100) }
        return time + 1100
    }
    @Test fun `recorrido Drop D respeta orden y no declara comprobado sin revision`() {
        val session = GuidedTunerSession()
        val config = TunerConfiguration(guitars = listOf(GuitarProfile("default-guitar", "Guitarra", "drop-d")))
        session.begin(config.snapshot(), false)
        var time = 0L
        for (number in 6 downTo 1) {
            assertEquals(number, session.state.measurement.fixedString)
            time = finishString(session, time)
        }
        assertEquals(GuidedPhase.COMPLETE, session.state.phase)
        assertFalse(session.state.checked)
        assertEquals(6, session.state.firstPass.size)
        assertEquals(38, session.state.firstPass.first().midi)
        session.measure(pitch(64), time)
        assertEquals(6, session.state.firstPass.size)
    }
    @Test fun `invalidas desviacion y huecos reinician permanencia y advertencia no pliega octavas`() {
        val session = GuidedTunerSession(); session.begin(TunerConfiguration().snapshot(), true)
        repeat(6) { session.measure(pitch(40), it * 100L) }
        session.measure(PitchEstimate(), 600)
        repeat(6) { session.measure(pitch(40), 700 + it * 100L) }
        assertTrue(session.state.firstPass.isEmpty())
        session.measure(pitch(40, 12.0), 1300)
        session.measure(pitch(52), 1400); session.measure(pitch(52), 1600); session.measure(pitch(52), 1800)
        assertTrue(session.state.farWarning)
        assertEquals(40, session.state.measurement.targetMidi)
        assertTrue(session.state.firstPass.isEmpty())
        session.measure(pitch(40, 90.0), 1900)
        assertFalse(session.state.farWarning)
        session.measure(pitch(40), 2000); session.measure(pitch(40), 3000)
        assertTrue(session.state.firstPass.isEmpty())
        repeat(10) { session.measure(pitch(40), 3100 + it * 100L) }
        assertEquals(1, session.state.firstPass.size)
        assertNull(session.state.measurement.frequency)
        assertEquals(5, session.state.measurement.fixedString)
    }
    @Test fun `repaso pendiente se corrige sin sobrescribir primera pasada`() {
        val session = GuidedTunerSession(); session.begin(TunerConfiguration().snapshot(), true)
        var time = 0L
        repeat(6) { time = finishString(session, time) }
        assertEquals(GuidedPhase.REVIEW, session.state.phase)
        session.measure(pitch(40, -20.0), time)
        session.leavePending()
        repeat(5) { time = finishString(session, time + 100) }
        assertEquals(GuidedPhase.CORRECTION, session.state.phase)
        assertFalse(session.state.checked)
        assertEquals(setOf(6), session.state.pending)
        assertFalse(session.state.review.first().passed)
        assertEquals(-20.0, session.state.review.first().cents!!, 0.001)
        finishString(session, time + 100)
        assertTrue(session.state.checked)
        assertEquals(GuidedPhase.COMPLETE, session.state.phase)
        assertEquals(6, session.state.firstPass.size)
        assertEquals(6, session.state.review.size)
        assertEquals(1, session.state.corrections.size)
        assertFalse(session.state.review.first().passed)
    }
    @Test fun `interrupcion conserva resumen y descarta lecturas posteriores y evidencia heredada`() {
        val session = GuidedTunerSession(); session.begin(TunerConfiguration().snapshot(), true)
        val time = finishString(session, 0)
        session.interrupt(); session.interrupt()
        repeat(20) { session.measure(pitch(45), time + it * 100) }
        assertEquals(GuidedPhase.INTERRUPTED, session.state.phase)
        assertEquals(1, session.state.firstPass.size)
        assertFalse(session.state.checked)
        session.begin(TunerConfiguration(criterion = TunerCriterion.PRO).snapshot(), true)
        repeat(11) { session.measure(pitch(40, 3.0), it * 100L) }
        assertTrue(session.state.firstPass.isEmpty())
        assertFalse(session.state.farWarning)
    }
}
