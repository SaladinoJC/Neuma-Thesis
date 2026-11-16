package ar.edu.unmdp.entrenamiento.rhythm

import org.junit.Assert.*
import org.junit.Test

class RhythmSessionTest {
    @Test fun `cuatro semicorcheas a 120 BPM conservan timestamps y admiten ajuste positivo y negativo`() {
        for (offset in listOf(-100_000_000L, 100_000_000L)) {
            val session = RhythmSession()
            session.generate(setOf(RhythmCategory.SIXTEENTHS), 42L)
            session.preview(requireNotNull(session.state.score).copy(bpm = 120))
            session.play(0, calibration = RhythmCalibration(offset, 0, 12))
            val attacks = session.state.attacks.toList()
            for (attack in attacks) {
                val raw = 2_000_000_000L + attack.offsetNanos + offset
                session.advance(raw + 200_000_000L) // Entrega tardía de UI, se conserva el instante del contacto.
                session.tap(raw)
            }
            session.advance(20_000_000_000L)
            val result = requireNotNull(session.state.result)
            assertTrue(result.attacks.all { it.kind == TapKind.ON_TIME })
            assertEquals(attacks.size, result.taps.size)
            assertEquals(0, result.extras)
            assertEquals(0, result.omissions)
            assertEquals(offset, result.calibration?.offsetNanos)
            assertEquals(2_000_000_000L + attacks.first().offsetNanos + offset, result.taps.first().timestampNanos)
        }
    }
    @Test fun `conteo visual sigue cada pulso del tempo real a 60 90 y 120 BPM`() {
        for (bpm in listOf(60, 90, 120)) for (meter in 2..4) {
            val session = RhythmSession()
            session.generate(setOf(RhythmCategory.QUARTER), 42L, meter)
            session.preview(requireNotNull(session.state.score).copy(bpm = bpm))
            session.play(0)
            for (beat in 0 until meter) {
                session.advance(Beat(beat).nanos(bpm))
                assertEquals("$bpm BPM, $meter/4, pulso $beat", beat + 1, session.state.countInBeat)
            }
            session.advance(Beat(meter).nanos(bpm))
            assertNull(session.state.countInBeat)
            assertEquals(RhythmPhase.PLAYING, session.state.phase)
        }
    }
    @Test fun `fronteras inclusivas y un solo tap por ataque sin depender del metronomo`() {
        for ((ms, expected) in listOf(-150L to TapKind.EARLY, -60L to TapKind.ON_TIME,
            60L to TapKind.ON_TIME, 150L to TapKind.LATE, 151L to TapKind.EXTRA)) {
            val session = RhythmSession(); session.generate(setOf(RhythmCategory.QUARTER), 21L)
            session.play(0, metronome = false)
            val attack = session.state.attacks[2]
            session.tap(4_000_000_000L + attack.offsetNanos + ms * 1_000_000L)
            assertEquals("offset=$ms", expected, session.state.taps.last().kind)
        }
        val session = RhythmSession(); session.generate(setOf(RhythmCategory.EIGHTHS), 9L); session.play(0)
        val attacks = session.state.attacks.toList()
        session.advance(21_000_000_000L)
        // One late-delivered event repairs one omission, preserving its original delta.
        session.tap(4_000_000_000L + attacks[3].offsetNanos + 50_000_000L)
        assertEquals(1, requireNotNull(session.state.result).attacks.count { it.kind == TapKind.ON_TIME })
        session.tap(4_000_000_000L + attacks[3].offsetNanos + 50_000_001L)
        assertEquals(TapKind.EXTRA, requireNotNull(session.state.result).taps.last().kind)
        session.repeat(); session.play(30_000_000_000L); session.interrupt()
        assertFalse(requireNotNull(session.state.result).completed)
        session.tap(34_000_000_000L)
        assertTrue(session.state.taps.isEmpty())
    }
    @Test fun `corpus semillas y selecciones conservan duraciones apariciones y serializacion`() {
        val fingerprints = mutableSetOf<String>()
        val json = kotlinx.serialization.json.Json
        for (categories in listOf(setOf(RhythmCategory.QUARTER), setOf(RhythmCategory.EIGHTHS), setOf(RhythmCategory.QUARTER, RhythmCategory.EIGHTHS))) {
            for (seed in 0L until 30L) {
                val session = RhythmSession(); session.generate(categories, seed)
                val score = requireNotNull(session.state.score)
                assertEquals(4, score.bars.size)
                assertEquals(10, score.occurrences.count { it.target })
                assertTrue(score.bars.all { it.duration == Beat(4) })
                for (category in categories) assertTrue(score.occurrences.count { it.target && it.category == category } >= 2)
                val encoded = json.encodeToString(RhythmScore.serializer(), score)
                assertEquals(score, json.decodeFromString(RhythmScore.serializer(), encoded))
                fingerprints.add(score.fingerprint)
            }
        }
        assertTrue(fingerprints.size > 40)
        val empty = RhythmSession(); empty.generate(emptySet(), 1)
        assertEquals(RhythmPhase.SETUP, empty.state.phase); assertNotNull(empty.state.error)
    }
    @Test fun `timestamp del conteo entregado tarde no devuelve la ejecucion a conteo`() {
        val session = RhythmSession(); session.generate(setOf(RhythmCategory.QUARTER), 5L)
        session.play(0L); session.advance(5_000_000_000L)
        session.tap(3_000_000_000L)
        assertEquals(RhythmPhase.PLAYING, session.state.phase)
        assertEquals(1_000_000_000L, session.state.elapsedNanos)
        assertEquals(TapKind.PREPARATION, session.state.taps.single().kind)
        session.advance(21_000_000_000L)
        session.tap(3_100_000_000L)
        assertEquals(2, requireNotNull(session.state.result).taps.count { it.kind == TapKind.PREPARATION })
    }
    @Test fun `Play cuenta antes de ejecutar evalua un tap una sola vez y permite repetir como practica`() {
        val session = RhythmSession()
        session.generate(setOf(RhythmCategory.QUARTER), 42L)
        val score = session.state.score
        session.play(0L)
        assertEquals(RhythmPhase.COUNT_IN, session.state.phase)
        session.tap(3_000_000_000L)
        assertEquals(TapKind.PREPARATION, session.state.taps.single().kind)
        session.tap(4_000_000_000L)
        assertEquals(TapKind.ON_TIME, session.state.taps.last().kind)
        session.tap(4_000_000_001L)
        assertEquals(TapKind.EXTRA, session.state.taps.last().kind)
        session.advance(20_200_000_000L)
        assertEquals(RhythmPhase.RESULTS, session.state.phase)
        assertTrue(requireNotNull(session.state.result).completed)
        assertTrue(requireNotNull(session.state.result).omissions > 0)
        assertEquals(1, requireNotNull(session.state.result).attacks.count { it.kind == TapKind.ON_TIME })
        session.repeat()
        assertEquals(score, session.state.score)
        assertEquals(RhythmPhase.PREVIEW, session.state.phase)
        assertTrue(session.state.taps.isEmpty())
    }
    @Test fun `generar no inicia y produce cuatro compases exactos con procedencia y semilla`() {
        val session = RhythmSession()
        session.generate(setOf(RhythmCategory.QUARTER, RhythmCategory.EIGHTHS), 42L)
        assertEquals(RhythmPhase.PREVIEW, session.state.phase)
        val score = requireNotNull(session.state.score)
        assertEquals(4, score.bars.size)
        assertTrue(score.bars.all { it.duration == Beat(4) })
        assertEquals(10, score.occurrences.count { it.target })
        assertEquals(16, score.occurrences.size)
        for (category in score.selected) assertTrue(score.occurrences.count { it.target && it.category == category } >= 2)
        assertTrue(session.state.taps.isEmpty())
        val again = RhythmSession(); again.generate(score.selected.toSet(), 42L)
        assertEquals(score, again.state.score)
    }
}
