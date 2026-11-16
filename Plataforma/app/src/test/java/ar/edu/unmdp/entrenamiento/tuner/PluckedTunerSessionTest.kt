package ar.edu.unmdp.entrenamiento.tuner

import ar.edu.unmdp.entrenamiento.voice.*
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.pow

class PluckedTunerSessionTest {
    private fun pitch(midi: Int, cents: Double = 0.0) = PitchEstimate(440.0 * 2.0.pow((midi - 69 + cents / 100) / 12))

    @Test fun `oido conserva error inicial y exige señal nueva para una correccion`() {
        val session = EarTunerSession(TunerPluckedPolicy.stableMillis)
        session.begin(TunerConfiguration(mode = TunerMode.TRAINING).snapshot(), false, false)
        session.check()
        for (time in 0L..400L step 100) session.measure(pitch(40, 30.0), time)
        assertEquals(EarPhase.CORRECTING, session.state.phase)
        assertFalse(session.state.results.single().initialPassed)
        session.measure(PitchEstimate(), 500)
        assertFalse(session.state.results.single().corrected)
        for (time in 600L..1000L step 100) session.measure(pitch(40), time)
        assertEquals(EarPhase.HIDDEN, session.state.phase)
        assertEquals(5, session.state.measurement.fixedString)
        assertTrue(session.state.results.single().corrected)
        assertEquals(30.0, session.state.results.single().initialCents, .001)
        assertNull(session.state.measurement.lastConfirmation)
        assertNull(session.state.measurement.frequency)
    }

    @Test fun `guiado completa las seis cuerdas incluidas Si y Mi con pulsaciones de 400 ms`() {
        val session = GuidedTunerSession(TunerPluckedPolicy.stableMillis)
        session.begin(TunerConfiguration().snapshot(), false)
        var time = 0L
        for (midi in listOf(40, 45, 50, 55, 59, 64)) {
            for (offset in 0L..400L step 100) session.measure(pitch(midi), time + offset)
            session.measure(PitchEstimate(), time + 450)
            time += 600
        }
        assertEquals(GuidedPhase.COMPLETE, session.state.phase)
        assertEquals(6, session.state.firstPass.size)
        assertTrue(session.state.firstPass.all { it.passed })
    }

    @Test fun `oido avanza si la nueva comprobacion ya confirma afinacion sin exigir otra pulsacion`() {
        val session = EarTunerSession(TunerPluckedPolicy.stableMillis)
        session.begin(TunerConfiguration(mode = TunerMode.TRAINING).snapshot(), false, false)
        var time = 0L
        for (midi in listOf(40, 45, 50, 55, 59, 64)) {
            session.check()
            for (offset in 0L..400L step 100) session.measure(pitch(midi), time + offset)
            session.measure(PitchEstimate(), time + 450)
            time += 600
        }
        assertEquals(EarPhase.COMPLETE, session.state.phase)
        assertEquals(6, session.state.results.size)
        assertTrue(session.state.results.all { it.initialPassed && !it.corrected })
    }

    @Test fun `silencio huecos y desvio reinician estabilidad sin aprobar una lectura anterior`() {
        for (criterion in TunerCriterion.entries) {
            val session = TunerSession(TunerPluckedPolicy.stableMillis)
            session.criterion(criterion); session.fixString(2); session.start()
            for (time in 0L..300L step 100) session.measure(pitch(59), time)
            assertFalse(session.state.inTune)
            session.measure(PitchEstimate(), 350)
            for (time in 400L..700L step 100) session.measure(pitch(59), time)
            assertFalse(session.state.inTune)
            session.measure(pitch(59), 800)
            assertTrue(session.state.inTune)
            session.measure(PitchEstimate(), 900)
            assertFalse(session.state.inTune)
            assertNotNull(session.state.lastConfirmation)
            session.measure(pitch(59, criterion.margin + .1), 1000)
            assertFalse(session.state.inTune)
            assertNull(session.state.lastConfirmation)
            session.measure(pitch(59), 1200)
            session.measure(pitch(59), 1500)
            assertEquals(0L, session.state.stableMillis)
            session.stop()
            assertNull(session.state.lastConfirmation)
        }
        val estimator = YinEstimator(TunerPluckedPolicy.analysis)
        assertFalse(estimator.estimate(FloatArray(4096)).valid)
        val random = kotlin.random.Random(42)
        assertFalse(estimator.estimate(FloatArray(4096) { (random.nextFloat() - .5f) * .02f }).valid)
    }
}
