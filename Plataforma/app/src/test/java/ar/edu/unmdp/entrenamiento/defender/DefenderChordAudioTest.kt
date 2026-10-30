package ar.edu.unmdp.entrenamiento.defender

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.*

@OptIn(ExperimentalCoroutinesApi::class)
class DefenderChordAudioTest {
    @Test fun pcmContieneLasTresFrecuenciasEnLaOctavaPedidaSinClipping() {
        fun magnitude(pcm: ShortArray, midi: Int): Double {
            val frequency = 440 * 2.0.pow((midi - 69) / 12.0)
            var a = 0.0; var b = 0.0
            for (i in 4410 until 22050) {
                val phase = 2 * PI * frequency * i / 44100
                a += pcm[i] * cos(phase); b += pcm[i] * sin(phase)
            }
            return 2 * hypot(a, b) / (22050 - 4410)
        }
        for (quality in DefenderChordQuality.entries) {
            val chord = defenderChordPool(quality).first { it.root.name == DefenderName.DO && it.root.accidental == DefenderAccidental.NATURAL }
            val pcm = defenderChordTone(chord.notes.map { it.midi })
            assertEquals(26460, pcm.size)
            assertEquals(0, pcm.first().toInt()); assertEquals(0, pcm.last().toInt())
            assertTrue(pcm.all { abs(it.toInt()) < 32767 })
            chord.notes.forEach { assertTrue("Falta la voz MIDI ${it.midi}", magnitude(pcm, it.midi) > 1500) }
            assertTrue(magnitude(pcm, chord.root.midi) > 10 * magnitude(pcm, chord.root.midi - 12))
        }
    }

    @Test fun audioSoloAlAcertarEntregaLasTresAlturasYNoLaRaizSola() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val chords = mutableListOf<List<Int>>(); val notes = mutableListOf<Int>()
            val audio = object : DefenderAudio {
                override suspend fun note(midi: Int): Int { notes += midi; return 0 }
                override suspend fun chord(midis: List<Int>): Int { chords += midis; return 0 }
                override suspend fun effect(effect: DefenderEffect) = 0
            }
            val model = DefenderViewModel(audio, clock = { testScheduler.currentTime })
            model.variant("chords"); model.chordQuality(DefenderChordQuality.DIMINISHED); model.start(12); runCurrent()
            val expected = model.ui.value.game.enemy!!.chord!!
            assertTrue(chords.isEmpty()); assertTrue(notes.isEmpty())
            model.select(expected.root.name); model.accidental(expected.root.accidental)
            model.quality(DefenderChordQuality.MAJOR); model.fire(); runCurrent(); assertTrue(chords.isEmpty())
            advanceTimeBy(1); model.quality(expected.quality); model.fire(); runCurrent()
            assertEquals(listOf(expected.notes.map { it.midi }), chords)
            assertTrue(notes.isEmpty())
            model.home(); runCurrent()
        } finally { Dispatchers.resetMain() }
    }
}
