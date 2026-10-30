package ar.edu.unmdp.entrenamiento.rhythm

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RhythmEngravingTest {
    @Test fun everyCatalogCellPreservesRationalOnsetsAndAttackOwnership() {
        val fixtures = mutableListOf<RhythmNotationScore>()
        for (category in RhythmCategory.entries) for (meter in 2..4) {
            if (category == RhythmCategory.WHOLE && meter != 4) continue
            val session = RhythmSession().apply { generate(setOf(category), 12L, meter); play(0) }
            val state = session.state
            val score = requireNotNull(state.score)
            val engraved = score.toNotation()
            fixtures += engraved
            val notes = engraved.bars.flatMap { it.notes }
            assertEquals(score.occurrences.sumOf { it.notes.size }, notes.size)
            assertEquals(state.attacks.size, notes.mapNotNull { it.attackIndex }.distinct().size)
            for (note in notes.filter { !it.rest }) {
                val attack = state.attacks.single { it.index == note.attackIndex }
                assertEquals(attack.occurrenceIndex, note.occurrenceIndex)
                // A tied continuation shares the first head's verdict, without another attack.
                if (notes.none { it.tie && it.attackIndex == note.attackIndex && it.beat < note.beat })
                    assertEquals(attack.offsetNanos.toDouble(), note.beat * Beat(1).nanos(score.bpm), 1.1)
            }
            assertTrue(notes.filter { it.rest }.all { it.attackIndex == null })
            assertEquals((4 * meter).toDouble(), notes.last().endBeat, 0.00001)
        }
        // The browser verification consumes the actual native projection, not a second catalogue.
        val output = File("build/verification/rhythm-engraving/catalog.json")
        requireNotNull(output.parentFile).mkdirs(); output.writeText(Json.encodeToString(fixtures))
    }

    @Test fun tiedHeadsShareLateCorrectedVerdictAndRepeatClearsIt() {
        val session = RhythmSession().apply { generate(setOf(RhythmCategory.TIED_QUARTERS), 12L); play(0) }
        val score = session.state.score!!
        val notes = score.toNotation().bars.flatMap { it.notes }
        val first = notes.first { it.tie }
        val continuation = notes.first { it.id != first.id && it.attackIndex == first.attackIndex }
        val attack = session.state.attacks.single { it.index == first.attackIndex }
        session.advance(4_000_000_000L + attack.offsetNanos + attack.wideNanos + 1)
        assertEquals(TapKind.OMITTED, session.state.attacks[attack.index].kind)
        session.tap(4_000_000_000L + attack.offsetNanos)
        assertEquals(TapKind.ON_TIME, session.state.attacks[first.attackIndex!!].kind)
        assertEquals(TapKind.ON_TIME, session.state.attacks[continuation.attackIndex!!].kind)
        session.interrupt(); session.repeat()
        assertEquals(score.toNotation(), session.state.score!!.toNotation())
        assertTrue(session.state.attacks.isEmpty())
    }
}
