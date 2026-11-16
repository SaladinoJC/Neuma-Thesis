package ar.edu.unmdp.entrenamiento.rhythm

import org.junit.Assert.*
import org.junit.Test

class RhythmCalibrationTest {
    @Test fun `faltantes golpes irregulares y cancelacion no producen un ajuste`() {
        val missing = RhythmCalibrationSession()
        missing.start(0)
        repeat(5) { missing.tap((it + 4) * 750_000_000L) }
        missing.advance(13_000_000_000L)
        assertNull(missing.state.candidate)
        assertNotNull(missing.state.error)
        val uneven = RhythmCalibrationSession()
        uneven.start(0)
        repeat(12) { uneven.tap((it + 4) * 750_000_000L + if (it % 2 == 0) 200_000_000L else -200_000_000L) }
        uneven.advance(13_000_000_000L)
        assertNull(uneven.state.candidate)
        assertNotNull(uneven.state.error)
        uneven.start(20_000_000_000L)
        uneven.interrupt()
        assertFalse(uneven.state.active)
        assertNull(uneven.state.candidate)
        assertNotNull(uneven.state.error)
    }
    @Test fun `calibracion estima desfase consistente sin contar preparacion ni duplicados`() {
        val session = RhythmCalibrationSession()
        session.start(1_000_000_000L)
        repeat(16) { pulse ->
            session.tap(1_000_000_000L + pulse * 750_000_000L + 100_000_000L)
        }
        session.tap(1_000_000_000L + 15 * 750_000_000L + 110_000_000L)
        session.advance(14_000_000_000L)
        assertTrue(session.state.finished)
        val candidate = requireNotNull(session.state.candidate)
        assertEquals(100_000_000L, candidate.offsetNanos)
        assertEquals(12, candidate.sampleCount)
        assertEquals(0L, candidate.spreadNanos)
    }
}
