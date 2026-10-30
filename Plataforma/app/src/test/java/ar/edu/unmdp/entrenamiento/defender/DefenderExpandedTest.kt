package ar.edu.unmdp.entrenamiento.defender

import org.junit.Assert.*
import org.junit.Test

class DefenderExpandedTest {
    @Test fun `corpus conserva rangos claves y hasta dos lineas adicionales`() {
        val expected = mapOf(DefenderStage.N1 to (64 to 77), DefenderStage.N2 to (57 to 84),
            DefenderStage.N3 to (36 to 64))
        expected.forEach { (stage, range) ->
            val pool = defenderNotePool(stage, false)
            assertEquals(range.first, pool.minOf { it.midi })
            assertEquals(range.second, pool.maxOf { it.midi })
            assertTrue(pool.all { it.staffStep in -4..12 && it.accidental == DefenderAccidental.NATURAL })
        }
        val mixed = defenderNotePool(DefenderStage.N4, true)
        assertEquals(102, mixed.size)
        assertEquals(DefenderClef.entries.toSet(), mixed.map { it.clef }.toSet())
        assertTrue(mixed.all { it.staffStep in -4..12 && it.midi in 35..85 })
    }

    @Test fun `matching exige grafia y elige distancia geometrica aunque deadline diga otra cosa`() {
        val cSharp = DefenderNote(DefenderName.DO, 4, DefenderAccidental.SHARP)
        val dFlat = DefenderNote(DefenderName.RE, 4, DefenderAccidental.FLAT)
        assertEquals(cSharp.midi, dFlat.midi)
        val farFast = DefenderEnemy(1, cSharp, 0, 1000, DefenderPoint(0f, 0f))
        val closeSlow = DefenderEnemy(2, cSharp, 0, 10000, DefenderPoint(.49f, .81f))
        val live = listOf(farFast, closeSlow)
        assertNull(defenderMatch(live, dFlat.name, dFlat.accidental, 0))
        assertEquals(2, defenderMatch(live, cSharp.name, cSharp.accidental, 0)?.id)
        val tied = closeSlow.copy(id = 3)
        assertEquals(2, defenderMatch(listOf(tied, closeSlow), cSharp.name, cSharp.accidental, 0)?.id)
    }

    @Test fun `trayectorias reservan espacio futuro y no solo instante de spawn`() {
        val note = DefenderNote(DefenderName.MI, 4)
        val a = DefenderEnemy(1, note, 0, 10000, DefenderPoint(0f, 0f), DefenderPoint(1f, 1f))
        val crossing = DefenderEnemy(2, note, 0, 10000, DefenderPoint(1f, 0f), DefenderPoint(0f, 1f))
        assertFalse(defenderHasRoom(crossing, listOf(a)))
        assertTrue(defenderHasRoom(crossing.copy(appearedAtMillis = 10000), listOf(a)))
    }

    @Test fun `sesion alterada conserva seleccion completa al fallar y limpia al acertar`() {
        var now = 0L
        val session = DefenderSession { now }
        session.start(14, DefenderConfiguration(stage = DefenderStage.N4, accidentals = true, maxEnemies = 4))
        val enemy = requireNotNull(session.state.enemy)
        val wrong = DefenderAccidental.entries.first { it != enemy.note.accidental }
        session.select(enemy.note.name); session.accidental(wrong); session.fire()
        assertEquals(0, session.state.points)
        assertEquals(wrong, session.state.selectedAccidental)
        assertEquals(3, session.state.lives)
        now = 1
        session.accidental(enemy.note.accidental); session.fire()
        assertEquals(100, session.state.points)
        assertNull(session.state.selected)
        assertEquals(DefenderAccidental.NATURAL, session.state.selectedAccidental)
        assertEquals(enemy.note.accidental, session.state.events.last { it.kind == "SHOT_HIT" }.accidental)
    }

    @Test fun `multiples enemigos resuelven llegadas cronologicas y cancelan restantes al terminar`() {
        var now = 0L
        val session = DefenderSession { now }
        session.start(27, DefenderConfiguration(maxEnemies = 4))
        now = 8000; session.tick()
        assertTrue(session.state.enemies.size in 2..4)
        now = 100000; session.tick()
        assertEquals(0, session.state.lives)
        assertEquals(3, session.state.results.size)
        assertTrue(session.state.enemies.isEmpty())
        assertEquals(session.state.results.map { it.atMillis }.sorted(), session.state.results.map { it.atMillis })
    }

    @Test fun `cupo cuatro es alcanzable sin reservas superpuestas ni rafagas`() {
        var reachedFour = false
        for (seed in 0..30) {
            var now = 0L
            val session = DefenderSession { now }
            session.start(seed, DefenderConfiguration(maxEnemies = 4))
            for (time in 0L..100000L step 1000) {
                now = time; session.tick()
                if (time < 90000) session.state.enemy?.let { session.select(it.note.name); session.fire() }
                val live = session.state.enemies
                assertTrue(live.size <= 4)
                for (i in live.indices) for (j in i + 1 until live.size) {
                    val a = live[i].position(time); val b = live[j].position(time)
                    assertTrue(kotlin.math.abs(a.x - b.x) >= .245f || kotlin.math.abs(a.y - b.y) >= .205f)
                }
                if (live.size == 4) { reachedFour = true; break }
            }
            if (reachedFour) break
        }
        assertTrue("El cupo de cuatro debe ser utilizable con reserva legible", reachedFour)
    }
}
