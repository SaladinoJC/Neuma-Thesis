package ar.edu.unmdp.entrenamiento.rhythm

import org.junit.Assert.*
import org.junit.Test

class RhythmNotationTest {
    @Test fun elTresilloConservaDuracionesYLigaduraComparteUnAtaque() {
        val score = RhythmScore(1, listOf(RhythmCategory.EIGHTH_TRIPLET, RhythmCategory.TIED_QUARTERS), listOf(
            RhythmBar(0, listOf(
                RhythmOccurrence(0, 0, Beat(0), RhythmCategory.EIGHTH_TRIPLET, true,
                    List(3) { RhythmNote(Beat(1, 3), tuplet = 3) }),
                RhythmOccurrence(1, 0, Beat(1), RhythmCategory.TIED_QUARTERS, true,
                    listOf(RhythmNote(Beat(1), tieToNext = true), RhythmNote(Beat(1)))),
                RhythmOccurrence(2, 0, Beat(3), RhythmCategory.QUARTER_REST, true,
                    listOf(RhythmNote(Beat(1), rest = true)))))))
        val notation = score.toNotation()
        assertEquals(listOf(0.0, 1.0 / 3, 2.0 / 3, 1.0, 2.0, 3.0), notation.notes.map { it.beat })
        assertEquals(listOf("8", "8", "8", "q", "q", "q"), notation.notes.map { it.duration })
        assertEquals(1_000_000_000L, notation.notes[3].attackNanos)
        assertEquals(notation.notes[3].attackNanos, notation.notes[4].attackNanos)
        assertNull(notation.notes.last().attackNanos)
    }

    @Test fun elResultadoPersistidoColoreaLaNotaExactaYRepetirLimpiaElFeedback() {
        val session = RhythmSession().apply { generate(setOf(RhythmCategory.SIXTEENTHS), 17, 4); play(0) }
        val score = session.state.score!!
        val attack = session.state.attacks[1]
        session.tap(4_000_000_000L + attack.offsetNanos)
        session.interrupt()
        val result = session.state.result!!
        val stored = RhythmState(RhythmPhase.RESULTS, score, result = result)
        val painted = score.toNotation().feedback(stored)
        assertEquals("ON_TIME", painted.single { it.id == score.toNotation().notes.single { it.attackNanos == attack.offsetNanos }.id }.kind)
        assertTrue(score.toNotation().feedback(RhythmState(RhythmPhase.PREVIEW, score)).all { it.kind == "PENDING" })
    }
}
