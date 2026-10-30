package ar.edu.unmdp.entrenamiento.tuner

import ar.edu.unmdp.entrenamiento.voice.PitchEstimate
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.pow

class EarTunerTest {
    private fun pitch(midi: Int, cents: Double = 0.0) = PitchEstimate(frequency = 440.0 * 2.0.pow((midi - 69 + cents / 100) / 12))
    private fun feed(s: EarTunerSession, midi: Int, cents: Double, start: Long, duration: Long) {
        for (t in start..start + duration step 100) s.measure(pitch(midi, cents), t)
    }
    @Test fun errorInicialSeRevelaSoloTrasComprobarYNoSeBorraAlCorregir() {
        val s = EarTunerSession()
        s.begin(TunerConfiguration().snapshot(), finalReview = false, classification = false)
        feed(s, 40, 30.0, 0, 1200)
        assertNull(s.state.measurement.cents)
        assertNull(s.state.measurement.frequency)
        assertNull(s.state.measurement.measuredMidi)
        assertFalse(s.state.measurement.inTune)
        s.check()
        assertTrue(s.state.results.isEmpty())
        feed(s, 40, 30.0, 1300, 400)
        assertEquals(EarPhase.CORRECTING, s.state.phase)
        assertFalse(s.state.results.single().initialPassed)
        s.check()
        feed(s, 40, 0.0, 1800, 1000)
        assertEquals(5, s.state.measurement.fixedString)
        assertEquals(1, s.state.results.size)
        assertFalse(s.state.results.single().initialPassed)
        assertTrue(s.state.results.single().corrected)
        assertEquals(30.0, s.state.results.single().initialCents, 0.001)
    }
    @Test fun clasificacionExigeLecturaPreviaRecienteYNoSobreviveOtroRecorrido() {
        val s = EarTunerSession()
        s.begin(TunerConfiguration().snapshot(), false, true)
        s.classify(EarDirection.LOW, 0)
        s.check()
        assertEquals(EarPhase.HIDDEN, s.state.phase)
        feed(s, 40, -30.0, 100, 400)
        s.classify(EarDirection.HIGH, 800) // Caducada.
        s.check()
        assertEquals(EarPhase.HIDDEN, s.state.phase)
        feed(s, 40, -30.0, 900, 400)
        s.classify(EarDirection.HIGH, 1300)
        assertNull(s.state.classification)
        s.check()
        feed(s, 40, 0.0, 1400, 400)
        assertFalse(s.state.results.single().classification!!.correct)
        assertTrue(s.state.results.single().initialPassed)
        s.interrupt()
        s.begin(TunerConfiguration().snapshot(), false, true)
        s.check()
        assertEquals(EarPhase.HIDDEN, s.state.phase)
    }
    @Test fun silencioHuecosYVariacionNoConsumenIntentosNiReutilizanDatosOcultos() {
        val s = EarTunerSession(); s.begin(TunerConfiguration().snapshot(), false, false)
        feed(s, 40, 0.0, 0, 1000)
        s.check()
        s.measure(PitchEstimate(), 1100)
        assertTrue(s.state.results.isEmpty())
        s.measure(pitch(40, 15.0), 1200)
        s.measure(pitch(40, 15.0), 1600) // El hueco rompe la evidencia.
        s.measure(pitch(40, 25.0), 1700) // La amplitud también.
        assertTrue(s.state.results.isEmpty())
        assertNull(s.state.measurement.cents)
        feed(s, 40, 25.0, 1800, 300)
        assertEquals(1, s.state.results.size)
    }
    @Test fun referenciaIntercaladaYDetencionInvalidanEstabilidadSinBorrarResultado() {
        val s = EarTunerSession(); s.begin(TunerConfiguration().snapshot(), false, false)
        s.check(); feed(s, 40, 0.0, 0, 300)
        s.suspendMeasurement()
        s.measure(pitch(40), 400)
        assertTrue(s.state.results.isEmpty())
        s.resumeMeasurement(); feed(s, 40, 0.0, 500, 400)
        assertTrue(s.state.results.single().initialPassed)
        feed(s, 40, 0.0, 1000, 600)
        s.suspendMeasurement(); s.resumeMeasurement()
        feed(s, 40, 0.0, 1700, 900)
        assertEquals(6, s.state.measurement.fixedString)
        s.measure(pitch(40), 2700)
        assertEquals(5, s.state.measurement.fixedString)
        s.interrupt(); s.measure(pitch(45), 5000)
        assertEquals(EarPhase.INTERRUPTED, s.state.phase)
        assertEquals(1, s.state.results.size)
        assertFalse(s.state.measurement.running)
    }
    @Test fun seisIntentosYRevisionConPendienteSonEtapasDistintas() {
        val s = EarTunerSession(); s.begin(TunerConfiguration().copy(criterion = TunerCriterion.PRO).snapshot(), true, false)
        var t = 0L
        for (midi in listOf(40, 45, 50, 55, 59, 64)) {
            s.check(); feed(s, midi, 0.0, t, 400); t += 500
            feed(s, midi, 0.0, t, 1000); t += 1100
        }
        assertEquals(EarPhase.REVIEW, s.state.phase)
        assertEquals(6, s.state.results.size)
        assertTrue(s.state.results.all { it.initialPassed && it.corrected })
        s.leavePending() // Sexta pendiente, sin lectura inventada.
        for (midi in listOf(45, 50, 55, 59, 64)) { feed(s, midi, 0.0, t, 1000); t += 1100 }
        assertEquals(setOf(6), s.state.review.pending)
        assertEquals(6, s.state.results.size)
        feed(s, 40, 0.0, t, 1000)
        assertEquals(EarPhase.COMPLETE, s.state.phase)
        assertTrue(s.state.review.checked)
        assertNull(s.state.review.review.first().cents)
        assertEquals(6, s.state.results.size)
    }
    @Test fun advertenciaExcepcionalNoPublicaDireccionNiAltura() {
        val s = EarTunerSession(); s.begin(TunerConfiguration().snapshot(), false, false)
        feed(s, 52, 0.0, 0, 400)
        assertTrue(s.state.farWarning)
        assertNull(s.state.measurement.measuredMidi)
        assertNull(s.state.measurement.cents)
        assertEquals(0L, s.state.measurement.stableMillis)
        s.measure(PitchEstimate(), 500)
        assertFalse(s.state.farWarning)
    }
}
