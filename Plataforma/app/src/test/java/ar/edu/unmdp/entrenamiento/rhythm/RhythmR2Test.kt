package ar.edu.unmdp.entrenamiento.rhythm

import org.junit.Assert.*
import org.junit.Test

class RhythmR2Test {
    @Test fun `corpus mixto reproduce semilla sin cruzar barras ni inventar categorias objetivo`() {
        val selections = listOf(setOf(RhythmCategory.HALF, RhythmCategory.QUARTER),
            setOf(RhythmCategory.REST_EIGHTH, RhythmCategory.EIGHTH_REST),
            setOf(RhythmCategory.DOTTED_QUARTER_EIGHTH, RhythmCategory.QUARTER_REST))
        for (meter in 2..4) for (selection in selections) for (seed in 0L until 20L) {
            val session = RhythmSession(); session.generate(selection, seed, meter)
            val score = requireNotNull(session.state.score) { "$meter $selection $seed: ${session.state.error}" }
            assertTrue(score.bars.all { it.duration == Beat(meter) })
            assertEquals(selection, score.selected.toSet())
            for (category in selection) assertTrue(score.occurrences.count { it.target && it.category == category } >= 2)
            assertTrue(score.occurrences.filterNot { it.target }.all { it.category in setOf(RhythmCategory.QUARTER, RhythmCategory.EIGHTHS) })
            val again = RhythmSession(); again.generate(selection, seed, meter)
            assertEquals(score, again.state.score)
            val encoded = kotlinx.serialization.json.Json.encodeToString(RhythmScore.serializer(), score)
            assertEquals(score, kotlinx.serialization.json.Json.decodeFromString(RhythmScore.serializer(), encoded))
        }
    }

    @Test fun `cancelacion de generacion no se informa como seleccion imposible`() {
        val session = RhythmSession()
        try {
            session.generate(setOf(RhythmCategory.HALF), 1L, 3, checkCancelled = { throw kotlinx.coroutines.CancellationException("cancelled") })
            fail("Cancellation must propagate")
        } catch (_: kotlinx.coroutines.CancellationException) {
            assertNull(session.state.score)
            assertNull(session.state.error)
        }
    }

    @Test fun `cada categoria cabe con duracion exacta cuota y conteo segun metrica`() {
        for (meter in 2..4) for (category in RhythmCategory.entries) {
            val session = RhythmSession()
            session.generate(setOf(category), 12L, meter)
            if (category == RhythmCategory.WHOLE && meter != 4) {
                assertNull(session.state.score)
                assertNotNull(session.state.error)
                continue
            }
            val score = requireNotNull(session.state.score) { "$meter/4 $category: ${session.state.error}" }
            assertEquals(meter, score.beatsPerBar)
            assertTrue(score.bars.all { it.duration == Beat(meter) })
            val target = score.occurrences.filter { it.target }
            assertTrue(target.size >= 2)
            val duration = target.sumOf { it.notes.sumOf { note -> note.duration.asDouble() } }
            assertTrue("$meter/4 $category quota=$duration", duration in meter * 2.0..meter * 3.0)
            session.play(0)
            assertEquals(meter * 1_000_000_000L, session.executionStartNanos)
        }
    }

    @Test fun `seleccion imposible no quita categorias ni confunde presupuesto con imposibilidad`() {
        val session = RhythmSession()
        session.generate(RhythmCategory.entries.toSet(), 1L, 4)
        assertNull(session.state.score)
        assertTrue(requireNotNull(session.state.error).contains("Reducí"))
        session.generate(setOf(RhythmCategory.HALF), 2L, 4, maxCandidates = 0)
        assertTrue(requireNotNull(session.state.error).contains("búsqueda"))
    }

    @Test fun `silencio de compas dura la metrica sin crear ataques y extra ensucia un solo silencio`() {
        for (meter in 2..4) {
            val session = RhythmSession()
            session.generate(setOf(RhythmCategory.BAR_REST), 33L, meter)
            val score = requireNotNull(session.state.score)
            val rest = score.occurrences.first { it.target }
            assertEquals(listOf(RhythmNote(Beat(meter), rest = true)), rest.notes)
            session.play(0)
            assertTrue(session.state.attacks.none { it.occurrenceIndex == rest.index })
            val restTime = Beat(rest.bar * meter).nanos(60)
            val start = requireNotNull(session.executionStartNanos)
            session.tap(start + restTime + 400_000_000)
            session.advance(start + Beat(meter * 4).nanos(60))
            val result = requireNotNull(session.state.result)
            assertEquals(1, result.silences.count { it.extraCount == 1 })
            assertEquals(1, result.extras)
            assertTrue(result.silences.any { it.respected == true })
        }
    }

    @Test fun `ataque vecino dentro de ventana no penaliza silencio por segunda vez`() {
        val session = RhythmSession()
        session.generate(setOf(RhythmCategory.REST_EIGHTH), 31L, 4)
        val score = requireNotNull(session.state.score)
        val cell = score.occurrences.first { it.target }
        session.play(0)
        val start = requireNotNull(session.executionStartNanos)
        val cellStart = (Beat(cell.bar * 4) + cell.beat).nanos(60)
        session.tap(start + cellStart + 450_000_000)
        session.advance(start + 16_000_000_000)
        val result = requireNotNull(session.state.result)
        assertEquals(0, result.extras)
        assertTrue(result.silences.filter { it.occurrenceIndex == cell.index }.all { it.respected == true })
    }
}
