package ar.edu.unmdp.entrenamiento.rhythm

import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class RhythmR4Test {
    private fun category(code: String) = RhythmCategory.entries.single { it.code == code }

    @Test fun corpusDeTresCategoriasConservaPlantillasExactasContextoCuotaYSemilla() {
        for (code in listOf("R15", "R16", "R17")) for (meter in 2..4) for (seed in 0L until 20L) {
            val selected = category(code)
            val session = RhythmSession().apply { generate(setOf(selected), seed, meter) }
            val score = requireNotNull(session.state.score) { "$code $meter $seed: ${session.state.error}" }
            assertEquals("rhythm-simple-v4", score.version)
            assertEquals(setOf(selected), score.selected.toSet())
            assertEquals(4, score.bars.size)
            assertTrue(score.bars.all { it.duration == Beat(meter) })
            val targets = score.occurrences.filter { it.target }
            assertTrue(targets.size >= 2)
            val duration = targets.sumOf { it.notes.sumOf { note -> note.duration.asDouble() } }
            assertTrue(duration in meter * 2.0..meter * 3.0)
            for (cell in targets) {
                assertEquals(1, cell.beat.denominator)
                assertTrue(cell.notes.none { it.rest })
                when (code) {
                    "R15" -> assertEquals(List(3) { RhythmNote(Beat(1, 3), tuplet = 3) }, cell.notes)
                    "R16" -> assertEquals(listOf(RhythmNote(Beat(1, 2)), RhythmNote(Beat(1)), RhythmNote(Beat(1, 2))), cell.notes)
                    "R17" -> assertEquals(listOf(RhythmNote(Beat(1), tieToNext = true), RhythmNote(Beat(1))), cell.notes)
                }
            }
            assertTrue(score.occurrences.filterNot { it.target }.all { it.category.code in setOf("R20", "R21") })
            val again = RhythmSession().apply { generate(setOf(selected), seed, meter) }
            assertEquals(score, again.state.score)
            assertEquals(score, Json.decodeFromString(RhythmScore.serializer(), Json.encodeToString(RhythmScore.serializer(), score)))
        }
    }

    @Test fun tresillosUsanPosicionesAbsolutasSinAcumularRedondeoYSusVentanasSonLocales() {
        val session = RhythmSession().apply { generate(setOf(category("R15")), 12L) }
        val score = session.state.score!!.copy(bpm = 120)
        session.preview(score); session.play(0, metronome = false)
        for (cell in score.occurrences.filter { it.target }) {
            val start = Beat(cell.bar * 4) + cell.beat
            val attacks = session.state.attacks.filter { it.occurrenceIndex == cell.index }
            assertEquals((0..2).map { (start + Beat(it, 3)).nanos(120) }, attacks.map { it.offsetNanos })
            assertTrue(attacks.all { it.narrowNanos in 33_333_333L..33_333_334L })
            assertTrue(attacks.all { it.wideNanos in 74_999_999L..75_000_000L })
        }
        val attacks = session.state.attacks.toList()
        attacks.forEach { session.tap(session.executionStartNanos!! + it.offsetNanos) }
        session.advance(session.executionStartNanos!! + 8_000_000_001L)
        assertEquals(attacks.size, session.state.result!!.attacks.count { it.kind == TapKind.ON_TIME })
        assertEquals(0, session.state.result!!.extras)
        assertEquals(0, session.state.result!!.omissions)
    }

    @Test fun ligaduraConservaDosFigurasUnAtaqueYReataqueEsExtra() {
        val session = RhythmSession().apply { generate(setOf(category("R17")), 12L) }
        val score = session.state.score!!
        session.play(0)
        val cell = score.occurrences.first { it.target }
        val attack = session.state.attacks.single { it.occurrenceIndex == cell.index }
        val start = session.executionStartNanos!!
        session.tap(start + attack.offsetNanos)
        session.tap(start + attack.offsetNanos + 1_000_000_000L)
        session.advance(start + 16_000_000_001L)
        assertEquals(listOf(TapKind.ON_TIME, TapKind.EXTRA), session.state.taps.map { it.kind })
        assertEquals(1, session.state.result!!.extras)
        assertEquals(2, cell.notes.size)
        assertTrue(cell.notes.first().tieToNext)
        assertFalse(cell.notes.last().tieToNext)
        session.repeat()
        assertEquals(score, session.state.score)
        assertTrue(session.state.repeated)
        session.play(30_000_000_000L); session.interrupt()
        assertFalse(session.state.result!!.completed)
    }

    @Test fun sincopaCruzaElPulsoConAtaquesEnCeroMedioYUnoYMedio() {
        val session = RhythmSession().apply { generate(setOf(category("R16")), 17L) }
        val score = session.state.score!!
        session.play(0)
        for (cell in score.occurrences.filter { it.target }) {
            val start = Beat(cell.bar * 4) + cell.beat
            val offsets = session.state.attacks.filter { it.occurrenceIndex == cell.index }.map { it.offsetNanos }
            assertEquals(listOf(start, start + Beat(1, 2), start + Beat(3, 2)).map { it.nanos(60) }, offsets)
        }
    }

    @Test fun partituraInvalidaNoIniciaConSumaIncorrectaTernariaAlteradaOLigaduraIncompleta() {
        for (code in listOf("R15", "R16", "R17")) {
            val session = RhythmSession().apply { generate(setOf(category(code)), 12L) }
            val score = session.state.score!!
            val target = score.occurrences.first { it.target }
            val invalid = when (code) {
                "R15" -> target.notes.map { it.copy(duration = Beat(1, 2)) }
                "R16" -> target.notes.map { it.copy(duration = Beat(1, 3), tuplet = 3) }
                else -> target.notes.map { it.copy(tieToNext = true) }
            }
            val altered = score.copy(bars = score.bars.map { bar -> bar.copy(occurrences = bar.occurrences.map { if (it.index == target.index) it.copy(notes = invalid) else it }) })
            session.preview(altered); session.play(0)
            assertFalse("$code no debe ejecutarse", session.active)
            assertNotNull(session.state.error)
            assertNull(session.state.score)
            assertNull(session.state.result)
        }
    }

    @Test fun mezclaConSemicorcheasMantieneSeleccionYExplicaIncompatibilidadSinQuitarCategorias() {
        val selected = setOf(category("R15"), category("R16"), category("R17"), category("R03"))
        for (seed in 0L until 20L) {
            val session = RhythmSession().apply { generate(selected, seed) }
            val score = requireNotNull(session.state.score) { session.state.error ?: "Sin partitura" }
            assertEquals(selected, score.selected.toSet())
            selected.forEach { chosen -> assertTrue(score.occurrences.count { it.target && it.category == chosen } >= 2) }
            assertTrue(score.bars.all { it.duration == Beat(4) })
        }
        val impossible = RhythmSession().apply { generate(selected, 1L, 2) }
        assertNull(impossible.state.score)
        assertTrue(impossible.state.error!!.contains("Reducí"))
    }
}
