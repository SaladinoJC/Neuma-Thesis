package ar.edu.unmdp.entrenamiento.defender

import org.junit.Assert.*
import org.junit.Test

class DefenderChordsTest {
    @Test fun catalogoConservaTercerasQuintasGrafiasYLimiteDeDosLineasAdicionales() {
        for (quality in DefenderChordQuality.entries.take(4)) {
            val pool = defenderChordPool(quality)
            assertTrue(pool.isNotEmpty())
            for (chord in pool) {
                assertEquals(3, chord.notes.size)
                assertEquals(chord.root, chord.notes.first())
                assertEquals(quality.intervals, chord.notes.map { it.midi - chord.root.midi })
                assertEquals(listOf(0, 2, 4), chord.notes.map { it.octave * 7 + it.name.ordinal - (chord.root.octave * 7 + chord.root.name.ordinal) })
                assertTrue(chord.notes.all { it.clef == DefenderClef.TREBLE && it.staffStep in -4..12 })
            }
        }
        assertFalse(defenderChordPool(DefenderChordQuality.AUGMENTED).any { it.root.name == DefenderName.SOL && it.root.accidental == DefenderAccidental.SHARP })
        val cs = defenderChordPool(DefenderChordQuality.MAJOR).first { it.root.name == DefenderName.DO && it.root.accidental == DefenderAccidental.SHARP }
        val db = defenderChordPool(DefenderChordQuality.MAJOR).first { it.root.name == DefenderName.RE && it.root.accidental == DefenderAccidental.FLAT && it.root.midi == cs.root.midi }
        assertEquals(cs.notes.map { it.midi }, db.notes.map { it.midi })
        assertNotEquals(cs.answer, db.answer)
        val output = java.io.File("build/verification/defender-d4/catalog.json")
        output.parentFile!!.mkdirs()
        output.writeText(kotlinx.serialization.json.Json.encodeToString(kotlinx.serialization.builtins.ListSerializer(DefenderChord.serializer()),
            DefenderChordQuality.entries.take(4).flatMap { defenderChordPool(it) }))
    }

    @Test fun exigeRespuestaCompletaYEscritaConservaErrorYLimpiaTrasAcierto() {
        var now = 0L
        val session = DefenderSession { now }
        session.start(19, DefenderConfiguration(variant = "chords", chordQuality = DefenderChordQuality.MINOR))
        val chord = session.state.enemy!!.chord!!
        session.select(chord.root.name); session.accidental(chord.root.accidental)
        session.fire()
        assertEquals(0, session.state.events.count { it.kind.startsWith("SHOT") })
        session.quality(DefenderChordQuality.MAJOR); now++; session.fire()
        assertEquals(0, session.state.points)
        assertEquals(chord.root.name, session.state.selected)
        assertEquals(DefenderChordQuality.MAJOR, session.state.selectedQuality)
        assertEquals(3, session.state.lives)
        session.quality(chord.quality); now++; session.fire()
        assertEquals(100, session.state.points)
        assertNull(session.state.selected)
        assertNull(session.state.selectedQuality)
        assertEquals(chord, session.state.results.single().enemy.chord)
    }

    @Test fun vivosNoRepitenRespuestaAunqueCambieRegistroYElCupoEsAcotado() {
        for (quality in DefenderChordQuality.entries) {
            var now = 0L
            val session = DefenderSession { now }
            session.start(44, DefenderConfiguration(variant = "chords", chordQuality = quality, maxEnemies = 4))
            repeat(120) {
                now += 100; session.tick()
                val answers = session.state.enemies.map { it.chord!!.answer }
                assertEquals(answers.distinct().size, answers.size)
                assertTrue(session.state.enemies.size <= 4)
            }
        }
    }

    @Test fun raizEnarmonicaNoSustituyeLaGrafiaMostrada() {
        var now = 0L
        val session = DefenderSession { now }
        val seed = (0..500).first {
            session.start(it, DefenderConfiguration(variant = "chords"))
            session.state.enemy!!.note.let { root -> root.name == DefenderName.DO && root.accidental == DefenderAccidental.SHARP }
        }
        session.start(seed, DefenderConfiguration(variant = "chords"))
        session.select(DefenderName.RE); session.accidental(DefenderAccidental.FLAT); session.quality(DefenderChordQuality.MAJOR)
        now++; session.fire(); assertEquals(0, session.state.points)
        assertEquals(DefenderName.RE, session.state.selected)
        session.select(DefenderName.DO); session.accidental(DefenderAccidental.SHARP); now++; session.fire()
        assertEquals(100, session.state.points)
        assertEquals(1, session.state.failedShots)
    }

    @Test fun entregaTardiaUsaCalidadCapturadaConTimestampOriginal() {
        var now = 0L
        val session = DefenderSession { now }
        session.start(19, DefenderConfiguration(variant = "chords"))
        val enemy = session.state.enemy!!
        session.select(enemy.note.name); session.accidental(enemy.note.accidental); session.quality(DefenderChordQuality.MINOR)
        now = enemy.deadlineMillis + 1; session.tick()
        session.fireAt(enemy.deadlineMillis - 1, enemy.note.name, enemy.note.accidental, enemy.chord!!.quality)
        assertEquals(100, session.state.points)
        assertEquals(3, session.state.lives)
        assertEquals(DefenderChordQuality.MAJOR, session.state.events.single { it.kind == "SHOT_HIT" }.quality)
    }
}
