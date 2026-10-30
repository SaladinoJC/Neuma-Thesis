package ar.edu.unmdp.entrenamiento.rhythm

import org.junit.Assert.*
import org.junit.Test

class RhythmR3Test {
    private val cells = linkedMapOf(
        RhythmCategory.SIXTEENTHS to listOf(Beat(1, 4), Beat(1, 4), Beat(1, 4), Beat(1, 4)),
        RhythmCategory.EIGHTH_TWO_SIXTEENTHS to listOf(Beat(1, 2), Beat(1, 4), Beat(1, 4)),
        RhythmCategory.TWO_SIXTEENTHS_EIGHTH to listOf(Beat(1, 4), Beat(1, 4), Beat(1, 2)),
        RhythmCategory.SIXTEENTH_EIGHTH_SIXTEENTH to listOf(Beat(1, 4), Beat(1, 2), Beat(1, 4)),
        RhythmCategory.DOTTED_EIGHTH_SIXTEENTH to listOf(Beat(3, 4), Beat(1, 4)),
        RhythmCategory.SIXTEENTH_DOTTED_EIGHTH to listOf(Beat(1, 4), Beat(3, 4)),
        RhythmCategory.REST_SIXTEENTH to listOf(Beat(1, 4), Beat(1, 4), Beat(1, 4), Beat(1, 4)),
        RhythmCategory.SIXTEENTHS_REST to listOf(Beat(1, 4), Beat(1, 4), Beat(1, 4), Beat(1, 4)),
    )

    @Test fun cadaCelulaConservaOrdenDuracionSilenciosYCuotaEnLasTresMetricas() {
        for ((category, durations) in cells) for (meter in 2..4) {
            val session = RhythmSession(); session.generate(setOf(category), 12L, meter)
            val score = requireNotNull(session.state.score) { "$category $meter: ${session.state.error}" }
            assertEquals("rhythm-simple-v3", score.version)
            assertEquals(setOf(category), score.selected.toSet())
            assertTrue(score.bars.all { it.duration == Beat(meter) })
            val targets = score.occurrences.filter { it.target }
            assertTrue(targets.size >= 2)
            assertTrue(targets.size.toDouble() in meter * 2.0..meter * 3.0)
            for (target in targets) {
                assertEquals(durations, target.notes.map { it.duration })
                val rests = target.notes.mapIndexedNotNull { index, note -> index.takeIf { note.rest } }
                assertEquals(when (category) { RhythmCategory.REST_SIXTEENTH -> listOf(0); RhythmCategory.SIXTEENTHS_REST -> listOf(3); else -> emptyList<Int>() }, rests)
                assertTrue(target.notes.none { it.tieToNext || it.tuplet != null })
                assertEquals(1, target.beat.denominator)
            }
            assertTrue(score.occurrences.filterNot { it.target }.all { it.category in setOf(RhythmCategory.QUARTER, RhythmCategory.EIGHTHS) })
        }
    }
    @Test fun mezclaConCategoriasAnterioresEsReproducibleYSigueSiendoUnaSeleccionExplicita() {
        val selected = setOf(RhythmCategory.SIXTEENTHS, RhythmCategory.DOTTED_EIGHTH_SIXTEENTH,
            RhythmCategory.SIXTEENTHS_REST, RhythmCategory.DOTTED_QUARTER_EIGHTH)
        for (seed in 0L until 20) {
            val session = RhythmSession(); session.generate(selected, seed)
            val score = requireNotNull(session.state.score) { session.state.error ?: "Sin partitura" }
            assertEquals(4, score.bars.size)
            assertTrue(score.bars.all { it.duration == Beat(4) })
            selected.forEach { category -> assertTrue(score.occurrences.count { it.target && it.category == category } >= 2) }
            assertTrue(score.occurrences.all { it.category in selected || (!it.target && it.category in setOf(RhythmCategory.QUARTER, RhythmCategory.EIGHTHS)) })
            val again = RhythmSession(); again.generate(selected, seed)
            assertEquals(score, again.state.score)
            val json = kotlinx.serialization.json.Json.encodeToString(RhythmScore.serializer(), score)
            assertEquals(score, kotlinx.serialization.json.Json.decodeFromString(RhythmScore.serializer(), json))
        }
        val impossible = RhythmSession(); impossible.generate(cells.keys, 1L, 4)
        assertNull(impossible.state.score)
        assertTrue(impossible.state.error!!.contains("Reducí"))
    }
    @Test fun ventanasDensasRecalculadasA120BpmTienenFronterasInclusivasYUnTapPorAtaque() {
        for ((offset, expected) in listOf(25_000_000L to TapKind.ON_TIME, 25_000_001L to TapKind.LATE,
            56_250_000L to TapKind.LATE, 56_250_001L to TapKind.EXTRA, -56_250_000L to TapKind.EARLY)) {
            val session = RhythmSession(); session.generate(setOf(RhythmCategory.SIXTEENTHS), 12L)
            session.preview(session.state.score!!.copy(bpm = 120)); session.play(0)
            val target = session.state.score!!.occurrences.first { it.target }
            val attack = session.state.attacks.first { it.occurrenceIndex == target.index && it.offsetNanos > 0 }
            assertEquals(25_000_000L, attack.narrowNanos)
            assertEquals(56_250_000L, attack.wideNanos)
            val time = session.executionStartNanos!! + attack.offsetNanos + offset
            session.tap(time)
            assertEquals(expected, session.state.taps.single().kind)
            assertTrue(session.state.attacks.count { it.kind in setOf(TapKind.ON_TIME, TapKind.EARLY, TapKind.LATE) } <= 1)
            assertEquals(time, session.state.taps.single().timestampNanos)
        }
        val session = RhythmSession(); session.generate(setOf(RhythmCategory.SIXTEENTHS), 12L)
        session.preview(session.state.score!!.copy(bpm = 120)); session.play(0)
        val attack = session.state.attacks.first()
        val time = session.executionStartNanos!! + attack.offsetNanos
        session.tap(time); session.tap(time)
        assertEquals(listOf(TapKind.ON_TIME, TapKind.EXTRA), session.state.taps.map { it.kind })
    }
    @Test fun silencioCortoNoPideTapNiDuplicaUnaRespuestaAdelantadaValida() {
        val session = RhythmSession(); session.generate(setOf(RhythmCategory.REST_SIXTEENTH), 12L)
        session.preview(session.state.score!!.copy(bpm = 120)); session.play(0, metronome = false)
        val cell = session.state.score!!.occurrences.first { it.target }
        val at = session.executionStartNanos!! + (Beat(cell.bar * 4) + cell.beat).nanos(120)
        session.tap(at + 20_000_000L) // Extra dentro del silencio inicial de 125 ms.
        session.tap(at + 75_000_000L) // 50 ms antes del primer ataque: respuesta válida.
        session.advance(session.executionStartNanos!! + 8_000_000_000L)
        val result = session.state.result!!
        assertEquals(listOf(TapKind.EXTRA, TapKind.EARLY), result.taps.map { it.kind })
        assertEquals(1, result.silences.first { it.occurrenceIndex == cell.index }.extraCount)
        assertFalse(result.metronome)
        assertTrue(result.omissions > 0)
        assertEquals(1, result.extras)
    }
    @Test fun derivaSinMetronomoConservaGrillaYDesviosAcumulados() {
        val session = RhythmSession(); session.generate(setOf(RhythmCategory.SIXTEENTHS), 12L)
        session.play(0, metronome = false)
        val grid = session.state.attacks.map { it.offsetNanos }
        val target = session.state.score!!.occurrences.first { it.target }
        val attacks = session.state.attacks.filter { it.occurrenceIndex == target.index }
        for ((attack, delta) in attacks.zip(listOf(0L, 20_000_000L, 40_000_000L, 80_000_000L)))
            session.tap(session.executionStartNanos!! + attack.offsetNanos + delta)
        assertEquals(grid, session.state.attacks.map { it.offsetNanos })
        assertEquals(listOf(0L, 20_000_000L, 40_000_000L, 80_000_000L), session.state.taps.map { it.deltaNanos })
        assertEquals(listOf(TapKind.ON_TIME, TapKind.ON_TIME, TapKind.ON_TIME, TapKind.LATE), session.state.taps.map { it.kind })
    }
}
