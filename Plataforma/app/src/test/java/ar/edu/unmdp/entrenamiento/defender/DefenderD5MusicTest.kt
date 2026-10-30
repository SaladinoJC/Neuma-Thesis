package ar.edu.unmdp.entrenamiento.defender

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.*

class DefenderD5MusicTest {
    @Test fun catalogoCompletoMantieneGrafiaInversionCerradaYLimitesEnAmbasClaves() {
        assertEquals(84, defenderChordTargets().size)
        val all = mutableSetOf<DefenderChord>()
        for (target in defenderChordTargets()) {
            val pool = defenderChordPool(target.quality, target.inversion, target.clefs)
            assertTrue(target.label, pool.isNotEmpty())
            assertEquals(target.clefs.values.toSet(), pool.map { it.root.clef }.toSet())
            for (chord in pool) {
                assertEquals(target.quality.intervals.size, chord.notes.size)
                assertEquals(target.inversion, chord.inversion)
                assertTrue(chord.notes.all { it.staffStep in -4..12 && it.midi in 24..96 })
                assertEquals(chord.notes.map { it.midi }.sorted(), chord.notes.map { it.midi })
                assertTrue(chord.notes.last().midi - chord.notes.first().midi < 12)
                val offsets = defenderChordHeadOffsets(chord.notes)
                chord.notes.zipWithNext().forEachIndexed { i, pair ->
                    if (pair.second.staffStep - pair.first.staffStep == 1) assertEquals(12, abs(offsets[i + 1] - offsets[i]))
                }
                val expected = chord.quality.intervals.drop(target.inversion) + chord.quality.intervals.take(target.inversion).map { it + 12 }
                assertEquals(expected, chord.notes.map { it.midi - chord.root.midi })
                val rootStep = chord.root.octave * 7 + chord.root.name.ordinal
                val letters = chord.quality.intervals.indices.map { 2 * it }
                assertEquals(letters.drop(target.inversion) + letters.take(target.inversion).map { it + 7 },
                    chord.notes.map { it.octave * 7 + it.name.ordinal - rootStep })
                all += chord
            }
        }
        val out = java.io.File("build/verification/defender-d5/catalog.json")
        out.parentFile!!.mkdirs()
        out.writeText(Json.encodeToString(ListSerializer(DefenderChord.serializer()), all.toList()))
    }

    @Test fun bajoInvertidoNoCambiaRespuestaYVivosSonUnicosEnMezcla() {
        for (quality in DefenderChordQuality.entries) for (inversion in quality.intervals.indices) {
            var now = 0L; val session = DefenderSession { now }
            session.start(87, DefenderConfiguration(variant = "chords", chordQuality = quality,
                chordInversion = inversion, chordClefs = DefenderChordClefs.MIXED, maxEnemies = 4))
            val chord = session.state.enemy!!.chord!!
            if (inversion > 0) {
                assertNotEquals(chord.root.name, chord.notes.first().name)
                session.select(chord.notes.first().name); session.accidental(chord.notes.first().accidental)
                session.quality(quality); now++; session.fire()
                assertEquals(0, session.state.points); assertEquals(3, session.state.lives)
            }
            session.select(chord.root.name); session.accidental(chord.root.accidental); session.quality(quality)
            now++; session.fire(); assertEquals(100, session.state.points)
            repeat(180) {
                now += 100; session.tick()
                val answers = session.state.enemies.map { it.chord!!.answer }
                assertEquals(answers.size, answers.distinct().size)
            }
        }
    }

    @Test fun audioDeTresYCuatroVocesCubreExtremosDeCadaInversionYClave() {
        for (target in defenderChordTargets().filter { it.clefs != DefenderChordClefs.MIXED }) {
            val pool = defenderChordPool(target.quality, target.inversion, target.clefs)
            for (chord in listOf(pool.minBy { it.notes.first().midi }, pool.maxBy { it.notes.last().midi })) {
                val pcm = defenderChordTone(chord.notes.map { it.midi }, sampleRate = 8000)
                assertEquals(4800, pcm.size); assertEquals(0, pcm.first().toInt()); assertEquals(0, pcm.last().toInt())
                assertTrue(pcm.all { abs(it.toInt()) <= 6554 })
                for (voice in chord.notes) {
                    val frequency = 440 * 2.0.pow((voice.midi - 69) / 12.0)
                    var a = 0.0; var b = 0.0
                    for (i in 800 until 4000) {
                        val angle = 2 * PI * frequency * i / 8000
                        a += pcm[i] * cos(angle); b += pcm[i] * sin(angle)
                    }
                    assertTrue("${target.label} MIDI ${voice.midi}", 2 * hypot(a, b) / 3200 > 1000)
                }
            }
        }
    }
}
