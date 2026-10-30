package ar.edu.unmdp.entrenamiento.defender

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefenderD5ViewModelTest {
    @Test fun practicaEnFaConCuatroVocesRespondeRaizYReabreConfiguracionExacta() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val sounds = mutableListOf<List<Int>>()
            val audio = object : DefenderAudio {
                override suspend fun note(midi: Int): Int = error("No sustituir acorde por raíz")
                override suspend fun chord(midis: List<Int>): Int { sounds += midis; return 0 }
                override suspend fun effect(effect: DefenderEffect) = 0
            }
            var now = 0L; val store = MemoryDefenderResults()
            val model = DefenderViewModel(audio, clock = { now }, resultsStore = store)
            runCurrent(); model.variant("chords"); model.chordQuality(DefenderChordQuality.HALF_DIMINISHED7)
            model.chordInversion(3); model.chordClefs(DefenderChordClefs.BASS)
            model.chordQuality(DefenderChordQuality.MAJOR); assertEquals(2, model.ui.value.chordInversion)
            model.chordQuality(DefenderChordQuality.HALF_DIMINISHED7); model.chordInversion(3)
            model.start(71); runCurrent()
            val expected = model.ui.value.game.enemy!!.chord!!
            now++; model.select(expected.notes.first().name); model.accidental(expected.notes.first().accidental)
            model.quality(expected.quality); model.fire(); runCurrent()
            assertEquals(0, model.ui.value.game.points); assertTrue(sounds.isEmpty())
            now++; model.select(expected.root.name); model.accidental(expected.root.accidental); model.fire(); runCurrent()
            assertEquals(listOf(expected.notes.map { it.midi }), sounds)
            assertEquals(4, sounds.single().size)
            model.pause(); now += 5000; model.resume(); assertEquals(100, model.ui.value.game.points)
            model.home(); runCurrent()
            val reopened = DefenderViewModel(audio, resultsStore = store); runCurrent()
            val record = reopened.ui.value.history.results.single()
            assertEquals(DefenderChordTarget(DefenderChordQuality.HALF_DIMINISHED7, 3, DefenderChordClefs.BASS), record.game.configuration.chordTarget)
            assertEquals(DefenderPhase.INTERRUPTED, record.game.phase)
            assertEquals(setOf(DefenderChordTarget(DefenderChordQuality.MAJOR)), reopened.ui.value.history.progressFor("chords").unlockedTargets)
            reopened.home(); runCurrent()
        } finally { Dispatchers.resetMain() }
    }

    @Test fun controlesRespetanHitoDeOchoFundamentalesYFundamentalDeLaClave() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val audio = object : DefenderAudio {
                override suspend fun note(midi: Int) = 0
                override suspend fun chord(midis: List<Int>) = 0
                override suspend fun effect(effect: DefenderEffect) = 0
            }
            val store = MemoryDefenderResults()
            val model = DefenderViewModel(audio, resultsStore = store); runCurrent()
            model.variant("chords"); model.progression(true); model.chordInversion(1); model.chordClefs(DefenderChordClefs.BASS)
            assertEquals(DefenderChordTarget(DefenderChordQuality.MAJOR), model.ui.value.chordTarget)
            for (q in DefenderChordQuality.entries) repeat(2) {
                store.commit(DefenderRecord("$q-$it", 1, 2, DefenderHarness(variant = "chords", quality = q).finish()))
            }
            model.openHistory(); runCurrent(); model.closeHistory()
            model.chordInversion(1); assertEquals(1, model.ui.value.chordInversion)
            model.chordClefs(DefenderChordClefs.BASS); assertEquals(0, model.ui.value.chordInversion)
            assertEquals(DefenderChordClefs.BASS, model.ui.value.chordClefs)
            model.chordInversion(1); assertEquals(0, model.ui.value.chordInversion)
            model.chordClefs(DefenderChordClefs.MIXED); assertEquals(DefenderChordClefs.BASS, model.ui.value.chordClefs)
            model.progression(false); model.chordClefs(DefenderChordClefs.MIXED); model.chordInversion(2)
            assertEquals(DefenderChordClefs.MIXED, model.ui.value.chordClefs)
            model.progression(true); assertEquals(DefenderChordTarget(DefenderChordQuality.MAJOR), model.ui.value.chordTarget)
            model.home(); runCurrent()
        } finally { Dispatchers.resetMain() }
    }
}
